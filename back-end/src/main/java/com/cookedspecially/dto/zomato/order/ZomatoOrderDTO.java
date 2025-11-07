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
public class ZomatoOrderDTO {

	public ZomatoOrderBody order;
	
	
}
