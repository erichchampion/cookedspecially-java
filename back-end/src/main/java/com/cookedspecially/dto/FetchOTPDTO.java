package com.cookedspecially.dto;

import javax.validation.constraints.Min;

import com.cookedspecially.validator.Phone;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true) 
public class FetchOTPDTO {
	
    @Phone(message="Invalid Mobile No")
	public String phoneNumber;
    
    public String device="";
    
    public String appId="";
    
    @Min(1)
    public int orgId;
}
