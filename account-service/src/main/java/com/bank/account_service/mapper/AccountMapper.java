package com.bank.account_service.mapper;

import com.bank.account_service.dto.AccountRequest;
import com.bank.account_service.dto.AccountResponse;
import com.bank.account_service.entity.Account;

public class AccountMapper {
    public static Account toEntity(AccountRequest request){
        return Account.builder()
                .accountType(request.getAccountType())
                .balance(request.getBalance())
                .branchName(request.getBranchName())
                .customerId(request.getCustomerId()).build();
    }
    public static AccountResponse toResponse(Account account){
        return AccountResponse.builder()
                .accountId(account.getAccountId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .branchName(account.getBranchName())
                .status(account.getStatus())
                .customerId(account.getCustomerId())
                .createdDate(account.getCreatedDate())
                .build();
    }
}
