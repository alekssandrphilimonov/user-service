package com.pioneer.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
public class UserFilter {

    private String name;
    private LocalDate dateOfBirth;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "\\d{10,15}", message = "Invalid phone number")
    private String phone;
}
