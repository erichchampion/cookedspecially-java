package com.cookedspecially.kitchenservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for initiating a till handover
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TillHandoverRequest {

    @NotNull(message = "From user ID is required")
    private String fromUserId;

    @NotNull(message = "From user name is required")
    private String fromUserName;

    @NotNull(message = "To user ID is required")
    private String toUserId;

    @NotNull(message = "To user name is required")
    private String toUserName;

    @NotNull(message = "Actual balance is required")
    @PositiveOrZero(message = "Actual balance must be zero or positive")
    private BigDecimal actualBalance;

    private String notes;
}
