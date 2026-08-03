package com.bank.customer_service.service;

import com.bank.customer_service.dto.CustomerRequest;
import com.bank.customer_service.dto.CustomerResponse;

import java.util.List;

public interface CustomerService {
    List<CustomerResponse> getAllCustomers();
    CustomerResponse getCustomerById(Long customerId);
    CustomerResponse createCustomer(CustomerRequest customerRequest);
    CustomerResponse updateCustomer(Long customerId,CustomerRequest customerRequest);
    void deleteCustomer(Long customerId);

}
