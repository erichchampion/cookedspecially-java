package com.cookedspecially.dto.credit;

import com.cookedspecially.enums.credit.BilligCycle;
import com.google.gson.Gson;

import javax.validation.constraints.Size;

public class CreditTypeDTO {
	
	@Size(min=3)
	public String name;

	@Size(min = 3)
	public String banner;


	public Integer orgId;
	
	public Float maxLimit;

	public BilligCycle billingCycle;
	
	public String to_string(){
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
