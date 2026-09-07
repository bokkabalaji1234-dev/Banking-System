package com.bank.transaction_service.service.impl;

import com.bank.transaction_service.client.AccountClient;
import com.bank.transaction_service.dto.*;
import com.bank.transaction_service.entity.Transaction;
import com.bank.transaction_service.entity.TransferIdempotencyRecord;
import com.bank.transaction_service.enums.TransactionStatus;
import com.bank.transaction_service.enums.TransactionType;
import com.bank.transaction_service.exception.InvalidTransferException;
import com.bank.transaction_service.exception.TransactionNotFoundException;
import com.bank.transaction_service.mapper.TransactionMapper;
import com.bank.transaction_service.repository.TransactionRepository;
import com.bank.transaction_service.repository.TransferIdempotencyRecordRepository;
import com.bank.transaction_service.service.TransactionService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    private final static Logger log= LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;
    private final TransferIdempotencyRecordRepository transferIdempotencyRecordRepository;
    private final AccountClient accountClient;


    @Transactional
    @Retry(name = "accountService")
    @CircuitBreaker(
            name = "accountService",
            fallbackMethod = "depositFallback")
    @Override
    public TransactionResponse deposit(DepositRequest depositRequest,String idempotencyKey) {
        log.info("Deposit transaction started for accountId: {}",
                depositRequest.getAccountId());
        DepositBalanceRequest request=DepositBalanceRequest.builder()
                .amount(depositRequest.getAmount())
                .build();
        accountClient.deposit(depositRequest.getAccountId(),idempotencyKey,request);
        Transaction transaction=TransactionMapper.toEntity(depositRequest);
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setTransactionStatus(TransactionStatus.SUCCESS);
        transaction.setTransactionDate(LocalDateTime.now());
        Transaction savedTransaction=transactionRepository.save(transaction);
        log.info(
                "Deposit completed successfully. TransactionId: {}, AccountId: {}, Amount: {}",
                savedTransaction.getTransactionId(),
                savedTransaction.getAccountId(),
                savedTransaction.getAmount()
        );
        return TransactionMapper.toResponse(savedTransaction);
    }
//FallBack method for deposit
    private TransactionResponse depositFallback(
            DepositRequest depositRequest,
            Throwable throwable){
        log.info("Account Service unavailable during deposit. AccountId: {}",
                depositRequest.getAccountId(),
                throwable);
        throw new RuntimeException("Account Service is currently unavailable. Please try again later.");
    }

    @Transactional
    @Retry(name = "accountService")
    @CircuitBreaker(
            name = "accountService",
            fallbackMethod = "withdrawFallback")
    @Override
    public TransactionResponse withdraw(WithdrawRequest withdrawRequest,String idempotencyKey) {
        log.info("Withdraw transaction started for accountId: {}",
                withdrawRequest.getAccountId());
        WithdrawBalanceRequest request=WithdrawBalanceRequest.builder()
                .amount(withdrawRequest.getAmount())
                .build();
        accountClient.withdraw(withdrawRequest.getAccountId(),idempotencyKey,request);
        Transaction transaction= TransactionMapper.toEntity(withdrawRequest);
        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setTransactionStatus(TransactionStatus.SUCCESS);
        transaction.setTransactionDate(LocalDateTime.now());
        Transaction savedTransaction=transactionRepository.save(transaction);
        log.info("Transaction saved successfully. TransactionId: {}",
                savedTransaction.getTransactionId());
        return TransactionMapper.toResponse(savedTransaction);
    }

    @Override
    public TransactionResponse transfer(TransferRequest transferRequest, String idempotencyKey) {
        log.info(
                "Transfer started. From Account: {}, To Account: {}, Amount: {}, Key: {}",
                transferRequest.getFromAccountId(),
                transferRequest.getToAccountId(),
                transferRequest.getAmount(),
                idempotencyKey
        );
        // 1. Check duplicate transfer
        Optional<TransferIdempotencyRecord> existingRecord =
                transferIdempotencyRecordRepository
                        .findByIdempotencyKey(idempotencyKey);
        if (existingRecord.isPresent()) {
            log.warn(
                    "DUPLICATE TRANSFER DETECTED. Idempotency-Key: {}",
                    idempotencyKey
            );
            Long transactionId = existingRecord.get().getTransactionId();
            return getTransactionById(transactionId);
        }
        // 2. Validate source and destination
        if (transferRequest.getFromAccountId()
                .equals(transferRequest.getToAccountId())) {
            throw new InvalidTransferException(
                    "Source and destination accounts cannot be the same."
            );
        }
        // 3. Withdraw from source account
        WithdrawRequest withdrawRequest =
                WithdrawRequest.builder()
                        .accountId(transferRequest.getFromAccountId())
                        .amount(transferRequest.getAmount())
                        .description(
                                "Transfer to Account: "
                                        + transferRequest.getToAccountId()
                        )
                        .build();
        TransactionResponse response = withdraw(withdrawRequest,idempotencyKey+"-WITHDRAW");
        TransferIdempotencyRecord record =
                TransferIdempotencyRecord.builder()
                        .idempotencyKey(idempotencyKey)
                        .fromAccountId(transferRequest.getFromAccountId())
                        .toAccountId(transferRequest.getToAccountId())
                        .amount(transferRequest.getAmount())
                        .status(TransactionStatus.PENDING.name())
                        .transactionId(response.getTransactionId())
                        .createdAt(LocalDateTime.now())
                        .build();
        record = transferIdempotencyRecordRepository.save(record);
        try {
            // 4. Deposit into destination account
            DepositRequest depositRequest =
                    DepositRequest.builder()
                            .accountId(transferRequest.getToAccountId())
                            .amount(transferRequest.getAmount())
                            .description(
                                    "Transfer from Account: "
                                            + transferRequest.getFromAccountId()
                            )
                            .build();
            deposit(depositRequest,idempotencyKey+"-DEPOSIT");
            record.setStatus(TransactionStatus.SUCCESS.name());
            record.setTransactionId(response.getTransactionId());

            transferIdempotencyRecordRepository.save(record);
        } catch (Exception ex) {
            log.error(
                    "Deposit failed during transfer. Starting compensation. " +
                            "From Account: {}, To Account: {}, Amount: {}",
                    transferRequest.getFromAccountId(),
                    transferRequest.getToAccountId(),
                    transferRequest.getAmount(),
                    ex
            );
           //5.Compensation
            compensateWithdrawal(
                    transferRequest.getFromAccountId(),
                    transferRequest.getAmount(),
                    idempotencyKey + "-COMPENSATION"
            );
            record.setStatus(TransactionStatus.COMPENSATED.name());

            transferIdempotencyRecordRepository.save(record);
            throw new RuntimeException(
                    "Transfer failed. Amount has been compensated to source account."
            );
        }
        // 6. Save successful transfer
        log.info(
                "Transfer completed successfully. From Account: {}, To Account: {}, Amount: {}",
                transferRequest.getFromAccountId(),
                transferRequest.getToAccountId(),
                transferRequest.getAmount()
        );
        return response;
    }

    @Override
    public TransactionResponse getTransactionById(Long transactionId) {
        log.info("Fetching transaction with ID: {}", transactionId);
        Transaction transaction=transactionRepository.findById(transactionId)
                .orElseThrow(() ->
                        new TransactionNotFoundException(
                                "Transaction not found with ID: " + transactionId
                        )
                );
        log.info("Transaction fetched successfully. TransactionId: {}", transactionId);
        return TransactionMapper.toResponse(transaction);
    }

    @Override
    public List<TransactionResponse> getTransactionsByAccountId(Long accountId) {
        log.info("Fetching transactions for accountId: {}", accountId);
        List<TransactionResponse> responses=transactionRepository.findByAccountId(accountId)
                .stream()
                .map(TransactionMapper::toResponse)
                .toList();
        log.info("Fetched {} transactions for accountId: {}",
                responses.size(),
                accountId);
        return responses;
    }
    private void compensateWithdrawal(Long accountId, BigDecimal amount, String idempotencyKey) {
        log.warn("Starting compensation. AccountId: {}, Amount: {}",
                accountId, amount);
        DepositBalanceRequest request = DepositBalanceRequest.builder()
                .amount(amount)
                .build();

        accountClient.deposit(accountId, idempotencyKey, request);
        log.info(
                "Compensation completed successfully. AccountId: {}, Amount: {}",
                accountId,
                amount
        );
    }
    private TransactionResponse depositFallback(
            DepositRequest depositRequest,
            String idempotencyKey,
            Throwable throwable) {

        log.error(
                "Account Service unavailable during deposit. AccountId: {}",
                depositRequest.getAccountId(),
                throwable
        );

        throw new RuntimeException(
                "Account Service is currently unavailable. Please try again later."
        );
    }
}
