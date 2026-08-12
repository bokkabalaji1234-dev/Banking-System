package com.bank.account_service.controller;

import com.bank.account_service.client.CustomerClient;
import com.bank.account_service.dto.AccountRequest;
import com.bank.account_service.dto.AccountResponse;
import com.bank.account_service.dto.DepositBalanceRequest;
import com.bank.account_service.dto.WithdrawBalanceRequest;
import com.bank.account_service.service.AccountService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts(){
        return ResponseEntity.ok(accountService.getAllAccounts());
    }
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable Long accountId){
        return ResponseEntity.ok(accountService.getAccountById(accountId));
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest accountRequest){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.createAccount(accountRequest));
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<AccountResponse> updateAccount(@PathVariable Long accountId,
                                                         @Valid @RequestBody AccountRequest request){
        return ResponseEntity.ok(accountService.updateAccount(accountId,request));
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long accountId){
        accountService.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountResponse>> getAllAccountsByCustomerId(@PathVariable
                                                                            Long customerId){
        return ResponseEntity.ok(accountService.getAccountsByCustomerId(customerId));
    }
    @PutMapping("/{accountId}/withdraw")
    public ResponseEntity<AccountResponse> withdraw(@PathVariable Long accountId,
                                                    @RequestHeader ("Idempotency-Key") String idempotencyKey,
                                                    @Valid @RequestBody WithdrawBalanceRequest withdrawBalanceRequest){
        return ResponseEntity.ok(accountService.withdraw(accountId,withdrawBalanceRequest,idempotencyKey));
    }
    @PutMapping("/{accountId}/deposit")
    public ResponseEntity<AccountResponse> deposit(@PathVariable Long accountId,
                                                   @RequestHeader("Idempotency-Key") String idempotencyKey,
                                                   @Valid @RequestBody DepositBalanceRequest depositBalanceRequest){
        return ResponseEntity.ok(accountService.deposit(accountId,depositBalanceRequest,idempotencyKey));
    }
}
