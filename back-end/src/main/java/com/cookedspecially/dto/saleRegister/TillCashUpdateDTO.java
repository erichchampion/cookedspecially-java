package com.cookedspecially.dto.saleRegister;

import com.google.gson.Gson;

public class TillCashUpdateDTO {
public String tillId;
public float amount;
public String paymentType;
public Integer checkId;
public String remarks;
public String tillTransactionType;
public Integer ffcId;
public String updatedToPaymentType;
	public boolean isCredit;

public String toString(){
	Gson gson = new Gson();
	return gson.toJson(this);
}
}
