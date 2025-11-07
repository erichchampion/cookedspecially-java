package com.cookedspecially.dto.credit;

import com.google.gson.Gson;

import java.util.Date;

public class CreditTransactionDTO {
	
public String invoiceId;
public String id;
public String type;
public Date date;
public int customerId;
public String status;
public Integer checkId;
public String remark;
public float amount;
public float runningBalance;


public void setRunningBalance(double runningBalance) {
	this.runningBalance = Float.parseFloat(""+runningBalance);
}


public String toString(){
	final Gson gson=new Gson();
	return gson.toJson(this);
}

}
