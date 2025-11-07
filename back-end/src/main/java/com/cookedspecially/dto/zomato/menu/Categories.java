package com.cookedspecially.dto.zomato.menu;

import java.util.List;

import com.cookedspecially.dto.zomato.common.ItemCharges;
import com.cookedspecially.dto.zomato.common.ItemDiscount;
import com.cookedspecially.dto.zomato.common.ItemTaxes;
import com.cookedspecially.dto.zomato.common.Items;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author Rahul
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Categories {

	public Integer category_id;
	public String category_name;
	public String category_description;
	public Integer category_is_active;
	public String category_image_url;
	public String[] category_tags;
	public List<CategorySchedules> category_schedules;
	public Integer has_subcategory;
	public List<SubCategories> subcategories;
	public List<Items> items;
	public List<ItemTaxes> item_taxes;
	public List<ItemCharges> item_charges;
	public List<ItemDiscount> item_discount;
	public Integer category_order;
	
	
	
}
