package com.cookedspecially.dto;

import javax.validation.constraints.Min;

import com.cookedspecially.validator.Phone;

public class CustomerLoginDTO {

	@Phone
	public String mobileNo;
	
	@Min(1)
	public int orgId;
	
	public String device="";
	
	public String appId="";

}
