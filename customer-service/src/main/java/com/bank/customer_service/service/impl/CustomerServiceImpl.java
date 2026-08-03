package com.bank.customer_service.service.impl;

import com.bank.customer_service.dto.CustomerRequest;
import com.bank.customer_service.dto.CustomerResponse;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.exception.CustomerNotFoundException;
import com.bank.customer_service.mapper.CustomerMapper;
import com.bank.customer_service.repository.CustomerRepository;
import com.bank.customer_service.service.CustomerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class CustomerServiceImpl implements CustomerService {
    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        log.info("Fetching All customers");
        return customerRepository.findAll()
                .stream()
                .map(CustomerMapper::toResponse)
                .toList();
    }

    @Override
    public CustomerResponse getCustomerById(Long customerId) {
        log.info("Fetching customer with ID: {}", customerId);
        Customer customer=customerRepository.findById(customerId)
                .orElseThrow(()->new CustomerNotFoundException("Customer not found with id :"+customerId));
        log.info("Customer fetched successfully with ID: {}", customerId);
        return CustomerMapper.toResponse(customer);
    }

    @Override
    public CustomerResponse createCustomer(CustomerRequest customerRequest) {
        log.info("Request received for creating customer with email: {}",customerRequest.getEmail());
        Customer customer=CustomerMapper.toEntity(customerRequest);
        customer.setCreatedDate(LocalDateTime.now());
        Customer savedCustomer=customerRepository.save(customer);
        log.info("Created Date after save: {}", savedCustomer.getCreatedDate());
        log.info("Customer created successfully with ID: {}" ,savedCustomer.getCustomerId());
        return CustomerMapper.toResponse(savedCustomer);
    }

    @Override
    public CustomerResponse updateCustomer(Long customerId, CustomerRequest customerRequest) {
        log.info("Updating customer with ID: {}",customerId);
        Customer customer= customerRepository.findById(customerId)
                .orElseThrow(()->new CustomerNotFoundException("Customer not found with id "+ customerId));
        customer.setCustomerName(customerRequest.getCustomerName());
        customer.setEmail(customerRequest.getEmail());
        customer.setPhoneNumber(customerRequest.getPhoneNumber());
        customer.setAddress(customerRequest.getAddress());
        Customer updatedCustomer=customerRepository.save(customer);
        log.info("Customer updated successfully with ID: {}",updatedCustomer.getCustomerId());
        return CustomerMapper.toResponse(updatedCustomer);
    }

    @Override
    public void deleteCustomer(Long customerId) {
        log.info("Deleting customer with ID: {}",customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer Not Found with Id : " + customerId));
        customerRepository.delete(customer);
        log.info("Customer deleted successfully with ID: {}",customerId);
    }
}
