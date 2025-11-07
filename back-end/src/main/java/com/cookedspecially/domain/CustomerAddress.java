package com.cookedspecially.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cookedspecially.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;



@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
@Entity 
@Table(name="CUSTOMERADDRESS")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerAddress implements Serializable {
    private static final long serialVersionUID = 1L;

	@Id
	@Column(name="id")
	private int id;
	
	@Column(name="customerId")
	private int customerId;
	
	@Column(name="address")
	private String customerAddress;
	
	@Column(name="deliveryArea")
	private String deliveryArea;
	
	@Column(name="city")
	private String city;
	
	@Column(name="state")
	private String state;
	
	@Column(name="status")
	private String status = Status.ACTIVE.toString();
	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getDeliveryArea() {
		// Erich: A hack, since many reports have been hardcoded to only work with a non-null value
		return deliveryArea != null ? deliveryArea : "N/A";
	}
	public void setDeliveryArea(String deliveryArea) {
		this.deliveryArea = deliveryArea;
	}
	public String getCustomerAddress() {
		return customerAddress;
	}
	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	
}
