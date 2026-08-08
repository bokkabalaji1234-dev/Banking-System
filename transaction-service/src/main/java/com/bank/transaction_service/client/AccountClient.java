package com.bank.transaction_service.client;

import com.bank.transaction_service.dto.AccountResponse;
import com.bank.transaction_service.dto.DepositBalanceRequest;
import com.bank.transaction_service.dto.WithdrawBalanceRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="ACCOUNT-SERVICE")
public interface AccountClient {

    @GetMapping("/accounts/{accountId}")
    AccountResponse getAccountById(@PathVariable Long accountId);
    @PutMapping("/accounts/{accountId}/withdraw")
    AccountResponse withdraw(@PathVariable Long accountId,
                              @RequestBody WithdrawBalanceRequest withdrawBalanceRequest);

    @PutMapping("/accounts/{accountId}/deposit")
    public AccountResponse deposit(@PathVariable Long accountId,
                                                   @RequestBody DepositBalanceRequest depositBalanceRequest);

}
