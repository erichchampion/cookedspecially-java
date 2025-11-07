package com.cookedspecially.dto;

import java.util.ArrayList;

import com.cookedspecially.domain.CustomerAddress;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
 * created by Rahul
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerAddressDTO {

	public String restaurantName;
	public Integer restaurantId;
	public String state;
	public String city;
	public ArrayList<CustomerAddress> customerAddress;
}
