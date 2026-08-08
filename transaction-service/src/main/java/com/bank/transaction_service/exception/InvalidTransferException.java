package com.bank.transaction_service.exception;

public class InvalidTransferException extends RuntimeException{
    public InvalidTransferException(String msg)
    {
        super(msg);
    }
}
