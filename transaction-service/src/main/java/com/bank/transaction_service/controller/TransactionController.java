package com.bank.transaction_service.controller;

import com.bank.transaction_service.dto.DepositRequest;
import com.bank.transaction_service.dto.TransactionResponse;
import com.bank.transaction_service.dto.TransferRequest;
import com.bank.transaction_service.dto.WithdrawRequest;
import com.bank.transaction_service.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@Valid @RequestBody DepositRequest depositRequest,String idempotencyKey){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.deposit(depositRequest,idempotencyKey));
    }
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@Valid @RequestBody WithdrawRequest withdrawRequest,String idempotencyKey){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.withdraw(withdrawRequest,idempotencyKey));
    }
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody TransferRequest transferRequest){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.transfer(transferRequest,idempotencyKey));
    }
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long transactionId){
        return ResponseEntity.ok(transactionService.getTransactionById(transactionId));
    }
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionByAccountId(@PathVariable Long accountId){
        return ResponseEntity.ok(transactionService.getTransactionsByAccountId(accountId));
    }

}
