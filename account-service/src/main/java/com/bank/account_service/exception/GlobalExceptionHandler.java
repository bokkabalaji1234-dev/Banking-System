package com.bank.account_service.exception;

import com.bank.account_service.constants.ErrorConstants;
import com.bank.account_service.dto.ErrorResponse;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFoundException(
            AccountNotFoundException ex, HttpServletRequest request
            ){
        String msg=ex.getMessage();
        ErrorResponse response=ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(ErrorConstants.ACCOUNT_NOT_FOUND)
                .message(msg)
                .path(request.getRequestURI()).build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(response);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String >> handleMethodAgumentNotValidException(MethodArgumentNotValidException ex){
        Map<String,String> result= new HashMap<>();

        List<FieldError> errors=ex.getBindingResult()
                .getFieldErrors();
        for (FieldError error:errors){
            result.put(error.getField(),error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(result);
    }
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(
            FeignException ex,
            HttpServletRequest request
    ){
        String error;
        String message;

        if (ex.status() == 404) {
            error = ErrorConstants.CUSTOMER_NOT_FOUND;
            message = "Customer not found.";
        } else if (ex.status() == 503) {
            error = ErrorConstants.CUSTOMER_SERVICE_UNAVAILABLE;
            message = "Customer Service is currently unavailable.";
        } else {
            error = ErrorConstants.CUSTOMER_SERVICE_ERROR;
            message = "Error while communicating with Customer Service.";
        }
        ErrorResponse errorResponse=ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(ex.status())
                .error(error)
                .message(message)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.valueOf(ex.status()))
                .body(errorResponse);
    }

    @ExceptionHandler(CustomerServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleCustomerServiceUnavailable(
            CustomerServiceUnavailableException ex,
            HttpServletRequest request) {
        ErrorResponse errorResponse=ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .error(ErrorConstants.CUSTOMER_SERVICE_UNAVAILABLE)
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(errorResponse);
    }
}
