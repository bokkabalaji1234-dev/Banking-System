package com.bank.account_service.client;

import com.bank.account_service.dto.CustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "CUSTOMER-SERVICE")
public interface CustomerClient {

    @GetMapping("/customers/{customerId}")
    CustomerResponse getCustomerById(@PathVariable Long customerId);
}
