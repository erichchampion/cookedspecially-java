package com.cookedspecially.dto.zomato.common;

import java.util.List;

import com.cookedspecially.dto.zomato.common.ItemTaxes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author Rahul
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Items {

	public Integer zomato_item_id;
	public Integer item_id;
	public String item_name;
	public Float item_unit_price;
	public Integer item_quantity;
	public Float item_final_price;
	public String item_short_description;
	public String item_long_description;
	public Integer item_is_active;
	public Integer item_is_default;
	public String item_image_url;
	public List<Integer> item_tags;
	public Groups groups;
	public List<ItemTaxes> item_taxes;
	public List<ItemCharges> item_charges;
	public List<ItemDiscount> item_discounts;
	public Integer item_is_recommended;
	public Object[] item_groups;
	public Integer item_order;
	
}
