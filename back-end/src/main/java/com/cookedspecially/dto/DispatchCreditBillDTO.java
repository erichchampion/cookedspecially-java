package com.cookedspecially.dto;

import com.google.gson.Gson;

public class DispatchCreditBillDTO {

	public int deliveryBoyId;
	public String creditBillId;
	public float transactionCash;
	public String paymentType;
	public String tillId;
	public int ffcId;
	public float billAmount;
	public String transactionCategory;
	public int customerId;
	
	public String toString(){
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
