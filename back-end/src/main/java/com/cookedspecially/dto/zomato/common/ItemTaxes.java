package com.cookedspecially.dto.zomato.common;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author Rahul
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ItemTaxes {

	public String order_type;
	public List<Integer> taxes;
}
