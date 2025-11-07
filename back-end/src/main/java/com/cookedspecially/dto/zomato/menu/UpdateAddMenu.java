package com.cookedspecially.dto.zomato.menu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author Rahul
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class UpdateAddMenu {
	
	public Menu menu;
}
