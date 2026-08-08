package com.bank.transaction_service.service.impl;

import com.bank.transaction_service.client.AccountClient;
import com.bank.transaction_service.dto.*;
import com.bank.transaction_service.entity.Transaction;
import com.bank.transaction_service.enums.TransactionStatus;
import com.bank.transaction_service.enums.TransactionType;
import com.bank.transaction_service.exception.InvalidTransferException;
import com.bank.transaction_service.exception.TransactionNotFoundException;
import com.bank.transaction_service.mapper.TransactionMapper;
import com.bank.transaction_service.repository.TransactionRepository;
import com.bank.transaction_service.service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    private final static Logger log= LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;
    private final AccountClient accountClient;

    @Transactional
    @Override
    public TransactionResponse deposit(DepositRequest depositRequest) {
        log.info("Deposit transaction started for accountId: {}",
                depositRequest.getAccountId());
        DepositBalanceRequest request=DepositBalanceRequest.builder()
                .amount(depositRequest.getAmount())
                .build();
        accountClient.deposit(depositRequest.getAccountId(),request);
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

    @Transactional
    @Override
    public TransactionResponse withdraw(WithdrawRequest withdrawRequest) {
        log.info("Withdraw transaction started for accountId: {}",
                withdrawRequest.getAccountId());
        WithdrawBalanceRequest request=WithdrawBalanceRequest.builder()
                .amount(withdrawRequest.getAmount())
                .build();
        accountClient.withdraw(withdrawRequest.getAccountId(),request);
        Transaction transaction= TransactionMapper.toEntity(withdrawRequest);
        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setTransactionStatus(TransactionStatus.SUCCESS);
        transaction.setTransactionDate(LocalDateTime.now());
        Transaction savedTransaction=transactionRepository.save(transaction);
        log.info("Transaction saved successfully. TransactionId: {}",
                savedTransaction.getTransactionId());
        return TransactionMapper.toResponse(savedTransaction);
    }

    @Transactional
    @Override
    public TransactionResponse transfer(TransferRequest transferRequest) {
        log.info(
                "Transfer started. From Account: {}, To Account: {}, Amount: {}",
                transferRequest.getFromAccountId(),
                transferRequest.getToAccountId(),
                transferRequest.getAmount()
        );
        if (transferRequest.getFromAccountId()
                .equals(transferRequest.getToAccountId())) {
            throw new InvalidTransferException(
                    "Source and destination accounts cannot be the same."
            );
        }
        WithdrawRequest withdrawRequest=WithdrawRequest.builder()
                .accountId(transferRequest.getFromAccountId())
                .amount(transferRequest.getAmount())
                .description(
                        "Transfer to Account: " + transferRequest.getToAccountId()
                )
                .build();
        TransactionResponse response=withdraw(withdrawRequest);
        DepositRequest depositRequest=DepositRequest.builder()
                .accountId(transferRequest.getToAccountId())
                .amount(transferRequest.getAmount())
                .description(
                        "Transfer from Account: " + transferRequest.getFromAccountId()
                )
                .build();
        deposit(depositRequest);
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
}
