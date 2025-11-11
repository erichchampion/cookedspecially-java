package com.cookedspecially.integrationhubservice.dto.zomato;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxDetails {

    @JsonProperty("tax_name")
    private String taxName;

    @JsonProperty("tax_rate")
    private BigDecimal taxRate;

    @JsonProperty("tax_amount")
    private BigDecimal taxAmount;
}
