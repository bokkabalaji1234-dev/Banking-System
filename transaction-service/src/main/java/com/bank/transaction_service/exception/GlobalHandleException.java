package com.bank.transaction_service.exception;

import com.bank.transaction_service.dto.ErrorResponse;
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
public class GlobalHandleException {

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTransactionNotFoundException(TransactionNotFoundException ex,
                                                                            HttpServletRequest request){
        ErrorResponse errorResponse=ErrorResponse.builder()
                .localDateTime(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("TRANSACTION_NOT_FOUND")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }
    @ExceptionHandler(InvalidTransferException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTransferException(InvalidTransferException ex,
                                                                        HttpServletRequest request){
        ErrorResponse errorResponse=ErrorResponse.builder()
                .localDateTime(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("INVALID_TRANSFER")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        Map<String,String> result=new HashMap<>();
        List<FieldError> errorList=ex.getBindingResult().getFieldErrors();
        for (FieldError error:errorList){
            result.put(error.getField(),error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(result);

    }
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(
            FeignException ex,
            HttpServletRequest request) {

        HttpStatus status;

        if (ex.status() > 0) {
            status = HttpStatus.valueOf(ex.status());
        } else {
            status = HttpStatus.BAD_GATEWAY;
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .localDateTime(LocalDateTime.now())
                .status(status.value())
                .error("ACCOUNT_SERVICE_ERROR")
                .message("Error occurred while communicating with Account Service")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status)
                .body(errorResponse);
    }
}
