package com.cookedspecially.integrationhubservice.dto.zomato;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZomatoOrderDTO {

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("restaurant_id")
    private String restaurantId;

    @JsonProperty("order_status")
    private String orderStatus;

    @JsonProperty("order_type")
    private String orderType;

    @JsonProperty("order_date")
    private LocalDateTime orderDate;

    @JsonProperty("customer")
    private CustomerDetails customer;

    @JsonProperty("items")
    private List<ItemDetails> items;

    @JsonProperty("subtotal")
    private BigDecimal subtotal;

    @JsonProperty("taxes")
    private List<TaxDetails> taxes;

    @JsonProperty("delivery_charge")
    private BigDecimal deliveryCharge;

    @JsonProperty("total")
    private BigDecimal total;

    @JsonProperty("payment_mode")
    private String paymentMode;

    @JsonProperty("delivery_address")
    private DeliveryAddress deliveryAddress;

    @JsonProperty("instructions")
    private String instructions;
}
