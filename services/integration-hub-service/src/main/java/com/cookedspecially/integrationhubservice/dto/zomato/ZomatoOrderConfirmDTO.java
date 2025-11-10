package com.cookedspecially.integrationhubservice.dto.zomato;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZomatoOrderConfirmDTO {

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("estimated_prep_time")
    private Integer estimatedPrepTime; // in minutes

    @JsonProperty("message")
    private String message;
}
