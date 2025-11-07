package com.cookedspecially.dto.zomato.order;

import java.util.List;

import com.cookedspecially.dto.zomato.common.CustomerDetails;
import com.cookedspecially.dto.zomato.common.ItemCharges;
import com.cookedspecially.dto.zomato.common.ItemDiscount;
import com.cookedspecially.dto.zomato.common.Items;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Rahul.
 */
@JsonIgnoreProperties(ignoreUnknown = true) 
public class ZomatoOrderBody {

	public Integer order_id;
	public Integer restaurant_id;
	public String restaurant_name;
	public String outlet_id;
	public String order_date_time;
	public Integer enable_delivery;
	public Float net_amount;
	public Float gross_amount;
	public String payment_mode;
	public String payment_status;
	public Float amount_paid;
	public Float amount_balance;
	public String order_type;
	public List<ItemCharges> order_additional_charges;
	public List<ItemDiscount> order_discounts;
	public CustomerDetails customer_details;
	public List<Items> order_items;
	public String order_instructions;
	
	
}
