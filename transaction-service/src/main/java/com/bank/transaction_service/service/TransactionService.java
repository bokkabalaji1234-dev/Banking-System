package com.bank.transaction_service.service;

import com.bank.transaction_service.dto.DepositRequest;
import com.bank.transaction_service.dto.TransactionResponse;
import com.bank.transaction_service.dto.TransferRequest;
import com.bank.transaction_service.dto.WithdrawRequest;

import java.util.List;

public interface TransactionService  {
    TransactionResponse deposit(DepositRequest depositRequest,String idempotencyKey);
    TransactionResponse withdraw(WithdrawRequest withdrawRequest,String idempotencyKey);
    TransactionResponse transfer(TransferRequest transferRequest, String idempotencyKey);
    TransactionResponse getTransactionById(Long transactionId);
    List<TransactionResponse> getTransactionsByAccountId(Long accountId);
}
