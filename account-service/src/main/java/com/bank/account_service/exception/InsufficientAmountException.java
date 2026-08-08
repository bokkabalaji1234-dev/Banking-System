package com.bank.account_service.exception;

public class InsufficientAmountException extends RuntimeException{
    public InsufficientAmountException(String msg){
        super(msg);
    }
}
