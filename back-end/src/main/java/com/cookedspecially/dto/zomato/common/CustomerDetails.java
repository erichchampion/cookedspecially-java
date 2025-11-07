package com.cookedspecially.dto.zomato.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author Rahul
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class CustomerDetails {
	
	public String name;
	public String phone_number;
	public String email;
	public String address;
	public String delivery_area;
	public String City;
	public String State;
	public String country;
	public Integer pincode;
	public String order_instructions;
	public String address_instructions;
}
