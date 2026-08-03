package com.bank.customer_service.mapper;

import com.bank.customer_service.dto.CustomerRequest;
import com.bank.customer_service.dto.CustomerResponse;
import com.bank.customer_service.entity.Customer;

public class CustomerMapper {
    public static Customer toEntity(CustomerRequest request){
        return Customer.builder()
                .customerName(request.getCustomerName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .build();
//        Customer customer=new Customer();
//        customer.setCustomerName(request.getCustomerName());
//        customer.setEmail(request.getEmail());
//        customer.setPhoneNumber(request.getPhoneNumber());
//        customer.setAddress(request.getAddress());
//        return customer;
    }
    public static CustomerResponse toResponse(Customer customer){

//        CustomerResponse response=new CustomerResponse();
//        response.setCustomerId(customer.getCustomerId());
//        response.setCustomerName(customer.getCustomerName());
//        response.setEmail(customer.getEmail());
//        response.setPhoneNumber(customer.getPhoneNumber());
//        response.setAddress(customer.getAddress());
//        response.setCreatedDate(customer.getCreatedDate());
//        return response;
        return CustomerResponse.builder()
                .customerId(customer.getCustomerId())
                .customerName(customer.getCustomerName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .address(customer.getAddress())
                .createdDate(customer.getCreatedDate())
                .build();
    }
}
