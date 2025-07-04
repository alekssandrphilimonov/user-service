package com.pioneer.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserFilter {
    private String name;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
}
