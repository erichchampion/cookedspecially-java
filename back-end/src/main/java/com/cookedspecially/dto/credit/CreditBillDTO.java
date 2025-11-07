package com.cookedspecially.dto.credit;

import java.sql.Timestamp;

public class CreditBillDTO {

	public String billId;
	public float currentBillAmount;
	public Timestamp generationTime;
	public Timestamp lastPaymentDate;
	public Timestamp updatedOn;
	public float paymentMade;
	public String status;
	public float billAmount;
	public int customerId;
	public String mobileNo;
	public String name;
	public String email;
	public String creditType;
	public float maxLimit;
	public String billingAddress;
	public String banner;


	public void setBillAmount(Object billAmount) {
		try {
			this.billAmount = Float.parseFloat("" + billAmount);
		} catch (Exception e) {
			this.billAmount = 0;
		}
	}

	public void setCurrentBillAmount(Object currentBillAmount) {
		try{
		this.currentBillAmount = Float.parseFloat(""+currentBillAmount);
	 } catch (Exception e) {
   	  this.paymentMade=0;
       }
	}

	public void setPaymentMade(Object paymentMade) {
      try {
	   this.paymentMade = Float.parseFloat(""+paymentMade);
          } catch (Exception e) {
        	  this.paymentMade=0;
            }
	}

	public void setMaxLimit(float maxLimit) {
		try {
			this.maxLimit = Float.parseFloat("" + maxLimit);
		} catch (Exception e) {
			this.maxLimit = 0;
		}
	}

	public void setCustomerId(Object customerId) {
		this.customerId = Integer.parseInt(""+customerId);
	}
	
}
