package com.cookedspecially.dto.zomato.menu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class TierValues {
	
	public Integer tier_id;
	public Float charge_value;
	public Integer charge_applicable_below_order_amount;
	public Integer charge_always_applicable;
}
