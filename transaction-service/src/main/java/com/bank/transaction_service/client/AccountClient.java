package com.bank.transaction_service.client;

import com.bank.transaction_service.dto.AccountResponse;
import com.bank.transaction_service.dto.DepositBalanceRequest;
import com.bank.transaction_service.dto.WithdrawBalanceRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="ACCOUNT-SERVICE")
public interface AccountClient {

    @GetMapping("/accounts/{accountId}")
    AccountResponse getAccountById(@PathVariable Long accountId);

    @PutMapping("/accounts/{accountId}/withdraw")
    AccountResponse withdraw(
            @PathVariable Long accountId,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody WithdrawBalanceRequest withdrawBalanceRequest);

    @PutMapping("/accounts/{accountId}/deposit")
    AccountResponse deposit(
            @PathVariable Long accountId,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody DepositBalanceRequest depositBalanceRequest);
}
