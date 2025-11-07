package com.cookedspecially.dto.zomato.menu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author Rahul
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class AddUpdateMenu {
	
	public Integer outlet_id;
	public Menu menu;
	
}
