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
public class ZomatoOrderRejectDTO {

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("reason_code")
    private String reasonCode;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("message")
    private String message;
}
