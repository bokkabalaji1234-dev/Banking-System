package com.bank.customer_service.controller;

import com.bank.customer_service.dto.CustomerRequest;
import com.bank.customer_service.dto.CustomerResponse;
import com.bank.customer_service.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse>  createCustomer(@Valid @RequestBody CustomerRequest customerRequest){
       CustomerResponse customerResponse= customerService.createCustomer(customerRequest);
       return ResponseEntity.status(HttpStatus.CREATED)
               .body(customerResponse);
    }
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long customerId){
        CustomerResponse response=customerService.getCustomerById(customerId);
        return ResponseEntity.ok(response);
    }
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers(){
        return ResponseEntity.ok(customerService.getAllCustomers());
    }
    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable Long customerId,
                                                           @Valid @RequestBody CustomerRequest customerRequest){
        return ResponseEntity.ok(customerService.updateCustomer(customerId,customerRequest));

    }
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long customerId){
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

}
