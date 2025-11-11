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
public class ZomatoRestaurantStatusDTO {

    @JsonProperty("restaurant_id")
    private String restaurantId;

    @JsonProperty("open")
    private Boolean open;

    @JsonProperty("delivery_enabled")
    private Boolean deliveryEnabled;

    @JsonProperty("estimated_delivery_time")
    private Integer estimatedDeliveryTime; // in minutes

    @JsonProperty("message")
    private String message;
}
