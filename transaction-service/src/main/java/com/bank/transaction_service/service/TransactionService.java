package com.bank.transaction_service.service;

import com.bank.transaction_service.dto.DepositRequest;
import com.bank.transaction_service.dto.TransactionResponse;
import com.bank.transaction_service.dto.TransferRequest;
import com.bank.transaction_service.dto.WithdrawRequest;

import java.util.List;

public interface TransactionService  {
    TransactionResponse deposit(DepositRequest depositRequest);
    TransactionResponse withdraw(WithdrawRequest withdrawRequest);
    TransactionResponse transfer(TransferRequest transferRequest);
    TransactionResponse getTransactionById(Long transactionId);
    List<TransactionResponse> getTransactionsByAccountId(Long accountId);
}
