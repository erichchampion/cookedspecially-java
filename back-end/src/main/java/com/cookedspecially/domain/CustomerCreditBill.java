package com.cookedspecially.domain;

import java.sql.Timestamp;
import java.util.Date;

public class CustomerCreditBill {

	private String billId;
	private float amount;
	private Date date;
	private String name;
	private Customer customer;
	private String description;
	
	public CustomerCreditBill(Customer customer, CreditBill creditBill){
		this.customer=customer;
		this.name=customer.getFirstName() +" "+customer.getLastName();
		this.billId=creditBill.getBillId();
		this.amount =creditBill.getAmount();
		this.date=creditBill.getDate();
	}
	public CustomerCreditBill(Customer customer,Check check,String description){
		this.customer=customer;
		this.name=customer.getFirstName() +" "+customer.getLastName();
		if(customer.getCredit()!=null){
			this.amount =customer.getCredit().getCreditBalance();
		}else{
			this.amount=0;
		}
		this.date= check.getOpenTime();
		this.description=description;
	}
	public String getBillId() {
		return billId;
	}
	public void setBillId(String billId) {
		this.billId = billId;
	}
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
