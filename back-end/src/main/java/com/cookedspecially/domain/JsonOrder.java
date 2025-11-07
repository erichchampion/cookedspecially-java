/**
 * 
 */
package com.cookedspecially.domain;

import java.io.Serializable;
import java.util.List;

import com.cookedspecially.enums.check.CheckType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author shashank
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonOrder implements Serializable 
{
	private static final long serialVersionUID = 1L;
	
	public int checkId;
	public int number;
	public int tableId;
	public int custId;
	public float price;
	public CheckType checkType;
 // Added just for delivery orders. as there is only one order per check
	public String deliveryArea;
	public String deliveryAddress;
	public List<JsonDish> items;
	public List<String> couponCode;
	
	public List<String> getCouponCode() {
		return couponCode;
	}
	public void setCouponCode(List<String> couponCode) {
		this.couponCode = couponCode;
	}
	public CheckType getCheckType() {
		return checkType;
	}
	public void setCheckType(CheckType checkType) {
		this.checkType = checkType;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public List<JsonDish> getItems() {
		return items;
	}
	public void setItems(List<JsonDish> items) {
		this.items = items;
	}
	public int getTableId() {
		return tableId;
	}
	public void setTableId(int tableId) {
		this.tableId = tableId;
	}
	public int getCheckId() {
		return checkId;
	}
	public void setCheckId(int checkId) {
		this.checkId = checkId;
	}
	public int getCustId() {
		return custId;
	}
	public void setCustId(int custId) {
		this.custId = custId;
	}
	public String getDeliveryArea() {
		// Erich: A hack, since many reports have been hardcoded to only work with a non-null value
		return deliveryArea != null ? deliveryArea : "N/A";
	}
	public void setDeliveryArea(String deliveryArea) {
		this.deliveryArea = deliveryArea;
	}
	public String getDeliveryAddress() {
		// Erich: A hack, since many reports have been hardcoded to only work with a non-null value
		return deliveryAddress != null ? deliveryAddress : "N/A";
	}
	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}
}
