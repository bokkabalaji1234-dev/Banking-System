package com.bank.account_service.exception;

public class InvalidAmountException extends RuntimeException{
    public InvalidAmountException(String msg){
        super(msg);
    }
}
