package com.cookedspecially.dto;


import com.cookedspecially.enums.check.AdditionalCategories;
import com.cookedspecially.enums.restaurant.ChargesType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscountDTO {

	public long id;
	public long dcId;
	public String name;
	public AdditionalCategories category;
	public ChargesType type;
	public float value;
}
