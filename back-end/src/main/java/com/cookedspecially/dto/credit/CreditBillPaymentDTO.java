package com.cookedspecially.dto.credit;

import com.google.gson.Gson;

public class CreditBillPaymentDTO {

	public String creditBillId;
	public String paymentType;
	public String remark;
	public float billAmount;

    public String to_String() {
        Gson gson = new Gson();
		return gson.toJson(this);
	}
}
