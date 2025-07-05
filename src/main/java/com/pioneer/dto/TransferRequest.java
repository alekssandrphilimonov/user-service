package com.pioneer.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class TransferRequest {

    @NotNull(message = "Recipient user ID must not be null")
    private Long toUserId;

    @NotNull(message = "Transfer value must not be null")
    @DecimalMin(value = "0.01", message = "Transfer value must be greater than zero")
    private BigDecimal value;
}
