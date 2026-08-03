package com.bank.customer_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerRequest {
    @NotBlank(message = "customer name is required")
    @Size(min=3)
     private String customerName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "email is required")
     private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$",message = "Phone number must contains exactly 10 digits")
     private String phoneNumber;

     private String address;
}
