package com.cookedspecially.dto.credit;

import java.sql.Timestamp;

public class CreditDispatchedBillDTO {
 public String status;
 public String deliveryBoy;
 public String deliveredBY;
 public String customerName;
 public String mobileNo;
 public String creditType;
 public String creditBillId;
 public String ffcName;
 public String tillName;
 public String restaurantName;
 public Timestamp updateOn;
 public float billAmount;
public void setBillAmount(Object billAmount) {
	this.billAmount = Float.parseFloat(""+billAmount);
}
 
 
 
}
