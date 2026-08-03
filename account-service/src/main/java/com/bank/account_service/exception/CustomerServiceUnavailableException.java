package com.bank.account_service.exception;

public class CustomerServiceUnavailableException extends RuntimeException{
    public CustomerServiceUnavailableException(String msg){
        super(msg);
    }
}
