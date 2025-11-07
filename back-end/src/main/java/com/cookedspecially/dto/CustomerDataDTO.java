package com.cookedspecially.dto;

import java.util.List;
import com.cookedspecially.domain.Customer;

public class CustomerDataDTO {
	
	public Customer customer;
	public List<CustomerAddressDTO> addressByRestaurant;
	public String status;
	public String message; 
	  

}