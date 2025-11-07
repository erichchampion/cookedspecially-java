package com.cookedspecially.domain;

import java.io.Serializable;
import java.util.Date;

public class CheckSummery implements Serializable {
    private static final long serialVersionUID = 1L;
	
	private Date openTime;
	
	private String invoiceId;

	private String deliveryAddress;
	
	private double invoiceAmount;
	
	private Double rewardPoints;

	public Date getOpenTime() {
		return openTime;
	}

	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getDeliveryAddress() {
		// Erich: A hack, since many reports have been hardcoded to only work with a non-null value
		return deliveryAddress != null ? deliveryAddress : "N/A";
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public double getInvoiceAmount() {
		return invoiceAmount;
	}

	public void setInvoiceAmount(double invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	public Double getRewardPoints() {
		return rewardPoints;
	}

	public void setRewardPoints(Double rewardPoints) {
		this.rewardPoints = rewardPoints;
	}
}
