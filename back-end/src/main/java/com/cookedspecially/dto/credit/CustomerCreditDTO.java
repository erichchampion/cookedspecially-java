package com.cookedspecially.dto.credit;

import com.cookedspecially.enums.credit.CustomerCreditAccountStatus;
import com.google.gson.Gson;

public class CustomerCreditDTO {
public int customerId;
public int creditTypeId;
public float maxLimit;
public float creditBalance;
public int ffcId;
public String billingAddress;
public CustomerCreditAccountStatus status=CustomerCreditAccountStatus.ACTIVE;

public String to_string(){
	Gson gson = new Gson();
	return gson.toJson(this);
}
}
