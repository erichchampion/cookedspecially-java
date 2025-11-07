package com.cookedspecially.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import com.cookedspecially.validator.Email;
import com.cookedspecially.validator.Phone;
import com.google.gson.Gson;

public class WebCustomerRegisterDTO {
	
	 @Size(min=2)
	 public String userName;
	 
	 @Phone(message="Invalid Mobile No.")
	 public String mobileNo;
	 
	 @Min(1)
	 public int orgID;
	 
	 @Email
	 public String emailId;
	  
	 public String to_string(){
			final Gson gson=new Gson();
			return gson.toJson(this);
		}
}
