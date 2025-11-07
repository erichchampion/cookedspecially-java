package com.cookedspecially.dto.zomato.menu;

import java.util.List;

import com.cookedspecially.dto.zomato.common.ItemCharges;
import com.cookedspecially.dto.zomato.common.OrderAdditionalCharges;
import com.cookedspecially.dto.zomato.common.Taxes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author Rahul
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Menu {

		public List<Taxes> taxes;
		public List<ItemCharges> charges;
		public List<Categories> categories;
		public List<OrderAdditionalCharges> order_additional_charges;
		
}
