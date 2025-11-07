package com.cookedspecially.dto.zomato.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author Rahul
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ItemDiscount {
	
	public String discount_name;
	public String discount_type;
	public Float discount_value;
	public String discount_code;
	public boolean discount_is_taxed;
}
