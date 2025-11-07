/**
 * 
 */
package com.cookedspecially.domain;

import java.io.Serializable;
import java.util.List;

import com.cookedspecially.enums.check.CheckType;
import com.cookedspecially.enums.check.OrderSource;
import com.cookedspecially.enums.check.PaymentMode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author shashank
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonCouponInfo implements Serializable 
{
	
	private static final long serialVersionUID = 1L;
	
	public int restaurantID;	
	public int checkId;	
	public int customerId;	
	public String couponCode;	
	
	// if required
	public String orderSource;
	public String paymentMode;
	public double OrderAmount;
	public int deliveryAreaId = -1;
	
	public int getDeliveryAreaId() {
		return deliveryAreaId;
	}
	public void setDeliveryAreaId(int deliveryAreaId) {
		this.deliveryAreaId = deliveryAreaId;
	}
	
	public int getCheckId() {
		return checkId;
	}
	public void setCheckId(int checkId) {
		this.checkId = checkId;
	}
	public int getRestaurantID() {
		return restaurantID;
	}
	public void setRestaurantID(int restaurantID) {
		this.restaurantID = restaurantID;
	}
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public String getCouponCode() {
		return couponCode;
	}
	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}
	
	public String getOrderSource() {
		return orderSource;
	}
	public void setOrderSource(String orderSource) {
		System.out.println(orderSource);
		this.orderSource = orderSource;
	}
	public double getOrderAmount() {
		return OrderAmount;
	}
	public void setOrderAmount(double orderAmount) {
		OrderAmount = orderAmount;
	}
	
	public String getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
}
