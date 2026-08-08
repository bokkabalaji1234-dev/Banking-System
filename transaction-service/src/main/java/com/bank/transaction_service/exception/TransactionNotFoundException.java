package com.bank.transaction_service.exception;

public class TransactionNotFoundException extends RuntimeException{
    public TransactionNotFoundException(String msg)
    {
        super(msg);
    }
}
