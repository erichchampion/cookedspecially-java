package com.cookedspecially.dto.zomato.common;

import java.util.List;

import com.cookedspecially.dto.zomato.menu.TierValues;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author Rahul
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ItemCharges {

	public Integer charge_id;
	public String charge_name;
	public String charge_type;
	public Float charge_value;
	public Integer charge_is_active;
	public String applicable_on;
	public Integer charge_always_applicable;
	public Float charge_applicable_below_order_amount;
	public Integer has_tier_wise_values;
	public List<TierValues> tier_wise_values;
 }
