package com.bank.transaction_service.mapper;

import com.bank.transaction_service.dto.DepositRequest;
import com.bank.transaction_service.dto.TransactionResponse;
import com.bank.transaction_service.dto.WithdrawRequest;
import com.bank.transaction_service.entity.Transaction;

public class TransactionMapper {
    public static Transaction toEntity(DepositRequest depositRequest){
        return Transaction.builder()
                .accountId(depositRequest.getAccountId())
                .amount(depositRequest.getAmount())
                .description(depositRequest.getDescription())
                .build();
    }
    public static Transaction toEntity(WithdrawRequest withdrawRequest){
        return Transaction.builder()
                .accountId(withdrawRequest.getAccountId())
                .amount(withdrawRequest.getAmount())
                .description(withdrawRequest.getDescription())
                .build();
    }
    public static TransactionResponse toResponse(Transaction transaction){
        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .accountId(transaction.getAccountId())
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .transactionStatus(transaction.getTransactionStatus())
                .transactionDate(transaction.getTransactionDate())
                .build();
    }
}
