package com.cookedspecially.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import com.cookedspecially.validator.Phone;

public class VerifyWebCustomer {

	 @Phone(message="Invalid Mobile No.")
	 public String mobileNo;
	 
	 @Min(1)
	 public int orgID;
	 
	 @Min(100000)
	 public int otp;
	 
}
