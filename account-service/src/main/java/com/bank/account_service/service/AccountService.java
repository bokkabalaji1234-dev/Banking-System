package com.bank.account_service.service;

import com.bank.account_service.dto.AccountRequest;
import com.bank.account_service.dto.AccountResponse;
import com.bank.account_service.dto.DepositBalanceRequest;
import com.bank.account_service.dto.WithdrawBalanceRequest;

import java.util.List;

public interface AccountService {
    List<AccountResponse> getAllAccounts();
    AccountResponse getAccountById(Long accountId);
    AccountResponse createAccount(AccountRequest request);
    AccountResponse updateAccount(Long accountId,AccountRequest request);
    void deleteAccount(Long accountId);
    List<AccountResponse> getAccountsByCustomerId(Long customerId);
    AccountResponse withdraw(Long accountId, WithdrawBalanceRequest withdrawBalanceRequest);
    AccountResponse deposit(Long accountId, DepositBalanceRequest depositBalanceRequest);
}
