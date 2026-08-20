package com.bank.account_service.service.impl;

import com.bank.account_service.client.CustomerClient;
import com.bank.account_service.dto.*;
import com.bank.account_service.entity.Account;
import com.bank.account_service.entity.IdempotencyRecord;
import com.bank.account_service.enums.AccountStatus;
import com.bank.account_service.exception.AccountNotFoundException;
import com.bank.account_service.exception.CustomerServiceUnavailableException;
import com.bank.account_service.exception.InsufficientAmountException;
import com.bank.account_service.exception.InvalidAmountException;
import com.bank.account_service.mapper.AccountMapper;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.repository.IdempotencyRecordRepository;
import com.bank.account_service.service.AccountService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {

    private final CustomerClient customerClient;
    private  final AccountRepository accountRepository;
    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private static final Logger log= LoggerFactory.getLogger(AccountServiceImpl.class);

    private String generateAccountNumber(){
        Random random=new Random();
        int number=100000+random.nextInt(900000);
        return "ACC"+number;
    }
    private Account findAccountById(Long accountId){
        Account account=accountRepository.findById(accountId)
                .orElseThrow(() -> {
            log.warn("Account not found with ID: {}", accountId);
            return new AccountNotFoundException(
                    "Account not found with ID: " + accountId);
        });
        return account;
    }
    private AccountResponse createAccountFallback(AccountRequest request,
                                                  Throwable ex){
        log.error(
                "Fallback executed while creating account for customerId: {}. Reason: {}",
                request.getCustomerId(),
                ex.getMessage()
        );
        throw new CustomerServiceUnavailableException(
                "Customer Service is temporarily unavailable. Please try again later."
        );
    }
    @Override
    public List<AccountResponse> getAllAccounts() {
        log.info("Fetching all accounts");
        List<AccountResponse> responses=accountRepository.findAll()
                .stream()
                .map(AccountMapper::toResponse)
                .toList();
        log.info("Fetched {} accounts successfully", responses.size());
        return responses;
    }

    @Override
    public AccountResponse getAccountById(Long accountId) {
        log.info("Fetching account with ID: {}", accountId);
//        Account account=accountRepository.findById(accountId)
//                .orElse(null);
//        if(account==null){
//            log.warn("Account not found with ID: {}", accountId);
//            throw new AccountNotFoundException("Account not found with ID: " + accountId);
//        }
        Account account=findAccountById(accountId);
        log.info("Account fetched successfully with ID: {}", accountId);
        return AccountMapper.toResponse(account);
    }

    @Override
    @CircuitBreaker(name = "customerService",fallbackMethod = "createAccountFallback")
    public AccountResponse createAccount(AccountRequest request) {

        log.info("Creating account for customerId: {}", request.getCustomerId());
        CustomerResponse customerResponse=customerClient.getCustomerById(request.getCustomerId());
        log.info("Customer verified successfully with ID: {}",
                customerResponse.getCustomerId());
        Account account = AccountMapper.toEntity(request);
        account.setAccountNumber(generateAccountNumber());
        account.setStatus(AccountStatus.ACTIVE);
        account.setCreatedDate(LocalDateTime.now());
        Account savedAccount = accountRepository.save(account);
        log.info("Account Created Successfully with account number : {}", savedAccount.getAccountNumber());
        return AccountMapper.toResponse(savedAccount);

    }

    @Override
    public AccountResponse updateAccount(Long accountId, AccountRequest request) {
        log.info("Updating account with ID: {}", accountId);
//        Account account = accountRepository.findById(accountId)
//                .orElse(null);
//        if(account==null){
//            log.warn("Account not found with ID: {}", accountId);
//            throw new AccountNotFoundException("Account not found with ID: " + accountId);
//        }
        Account account=findAccountById(accountId);
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getBalance());
        account.setBranchName(request.getBranchName());
        Account savedAccount= accountRepository.save(account);
        log.info("Account updated Successfully with account number : {}",savedAccount.getAccountId());

        return AccountMapper.toResponse(savedAccount);
    }

    @Override
    public void deleteAccount(Long accountId) {
        log.info("Deleting account with ID: {}", accountId);
//        Account account=accountRepository.findById(accountId)
//                .orElse(null);
//        if(account==null){
//            log.warn("Account not found with ID: {}", accountId);
//            throw new AccountNotFoundException("Account not found with ID: " + accountId);
//        }
        Account account=findAccountById(accountId);
        accountRepository.delete(account);
        log.info("Account deleted with account ID: {}", account.getAccountId());
    }

    @Override
    public List<AccountResponse> getAccountsByCustomerId(Long customerId) {
        log.info("Fetching accounts for customer ID: {}", customerId);
        List<AccountResponse> responses= accountRepository.findByCustomerId(customerId)
                .stream()
                .map(AccountMapper::toResponse)
                .toList();

        log.info("Fetched {} accounts for customer ID: {}", responses.size(), customerId);
        return responses;
    }

    @Transactional
    @Override
    public AccountResponse withdraw(Long accountId, WithdrawBalanceRequest withdrawBalanceRequest,String idempotencyKey) {
        log.info("Withdraw request received for accountId: {}", accountId);
        //1.check whether existing request was already processed
        var existingRecord=idempotencyRecordRepository.findByIdempotencyKey(idempotencyKey);
        if(existingRecord.isPresent()){
            log.warn(
                    "DUPLICATE REQUEST DETECTED! Idempotency-Key: {}. " +
                            "WITHDRAW will NOT be processed again.",
                    idempotencyKey
            );
            IdempotencyRecord record=existingRecord.get();
            //for now return current account status
            AccountResponse response=AccountMapper.toResponse(findAccountById(accountId));
            response.setBalance(record.getBalanceAfterOperation());
            return response;
        }
        //2.Find the Account
        Account account=findAccountById(accountId);
        //3.Valid the Amount
        BigDecimal amount=withdrawBalanceRequest.getAmount();
        if(amount.compareTo(BigDecimal.ZERO)<=0){
            throw new InvalidAmountException("Amount should be greater than zero");
        }
        if(account.getBalance().compareTo(amount)<0){
            throw new InsufficientAmountException(
                    String.format("Insufficient balance for accountId: %d", accountId));
        }
        //4.update the amount
        account.setBalance(account.getBalance().subtract(amount));
        //5.save the account
        Account savedAccount=accountRepository.save(account);
        //6.save idempotency record
        IdempotencyRecord record=IdempotencyRecord.builder()
                .idempotencyKey(idempotencyKey)
                .accountId(accountId)
                .operation("WITHDRAW")
                .status("SUCCESS")
                .balanceAfterOperation(savedAccount.getBalance())
                .createdAt(LocalDateTime.now())
                .build();
        idempotencyRecordRepository.save(record);
        log.info(
                "Withdraw successful. AccountId: {}, Remaining Balance: {}",
                accountId,
                amount,
                savedAccount.getBalance()
        );
        return AccountMapper.toResponse(savedAccount);
    }

    @Transactional
    @Override
    public AccountResponse deposit(Long accountId, DepositBalanceRequest depositBalanceRequest,String idempotencyKey) {
        log.info("Deposit request received for accountId: {}", accountId);
        // 1. Check whether this request was already processed
        var existingRecord= idempotencyRecordRepository.findByIdempotencyKey(idempotencyKey);
        if(existingRecord.isPresent()){
            log.warn(
                    "DUPLICATE REQUEST DETECTED! Idempotency-Key: {}. " +
                            "Deposit will NOT be processed again.",
                    idempotencyKey
            );
            IdempotencyRecord record=existingRecord.get();
            AccountResponse response=AccountMapper.toResponse(
                    findAccountById(accountId));
            //For now, return the current account state
            response.setBalance(record.getBalanceAfterOperation());
            return response;
        }
        //2.Find the Account
        Account account = findAccountById(accountId);
        //3.Valid the amount
        BigDecimal depositAmount= depositBalanceRequest.getAmount();
        if(depositAmount.compareTo(BigDecimal.ZERO)<=0){
            throw  new InvalidAmountException("Amount should be greater than zero");
        }
        //4.Update the Balance
        account.setBalance(account.getBalance().add(depositAmount));
        //5.save account
        Account savedAccount= accountRepository.save(account);
       //  6.save idempotencyRecord
        IdempotencyRecord record=IdempotencyRecord.builder()
                .idempotencyKey(idempotencyKey)
                .accountId(accountId)
                .operation("DEPOSIT")
                .status("SUCCESS")
                .balanceAfterOperation(savedAccount.getBalance())
                .createdAt(LocalDateTime.now())
                .build();
        idempotencyRecordRepository.save(record);
        log.info(
                "Deposit successful. AccountId: {}, Amount: {}, Balance: {}",
                accountId,
                depositAmount,
                savedAccount.getBalance()
        );
        return AccountMapper.toResponse(savedAccount);
    }
}
