/**
 * 
 */
package com.cookedspecially.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.cookedspecially.enums.CouponRepeatRule;
import com.cookedspecially.enums.check.OrderSource;
import com.cookedspecially.enums.check.PaymentMode;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="couponflatrules")
public class CouponFlatRules {

	//unique id for internal
	@Id
	@Column(name="coupanRuleId")
	@GeneratedValue
	@JsonIgnore
	private Integer coupanRuleId;
	
	@Column(name="orderSource")
	private String orderSource = OrderSource.Any.toString();
	
	@Column(name="paymentMode")
	private String paymentMode = "Any";//PaymentMode.ANY.toString(); since we are adding Any not ANY in jsp combo box
	
	@Column(name="minOrderPayment")
	private double minOrderPayment = 0.0;
	
	@Column(name="isUsedOncePerCustomer")
	private boolean isUsedOncePerCustomer;
	
	@Column(name="isForSelectedCustomer")
	private boolean isForSelectedCustomer;	
	
	@Column(name="isDurationRequired")
	private boolean isDurationRequired = false;
	
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(orphanRemoval=false)
	@Cascade({org.hibernate.annotations.CascadeType.REFRESH})
	@JoinTable(name="COUPONRULES_DELIVERYAREAS", 
	                joinColumns={@JoinColumn(name="COUPONRULEID")}, 
	                inverseJoinColumns={@JoinColumn(name="DELIVERYAREAID")})
	private List<DeliveryArea> deliveryAreas = new ArrayList<DeliveryArea>();

	
public List<DeliveryArea> getDeliveryAreas() {
	return deliveryAreas;
}

public void setDeliveryAreas(List<DeliveryArea> deliveryAreas) {
	this.deliveryAreas = deliveryAreas;
}
	
	
public boolean getIsDurationRequired() {
	return isDurationRequired;
}
public void setIsDurationRequired(boolean isDurationRequired) {
	this.isDurationRequired = isDurationRequired;
}

	public boolean getIsForSelectedCustomer() {
		return isForSelectedCustomer;
	}
	public void setIsForSelectedCustomer(boolean isForSelectedCustomer) {
		this.isForSelectedCustomer = isForSelectedCustomer;
	}
	
	@Column(name="maxCount")
	private Integer maxCount = 1000;
	
	public Integer getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(Integer maxCount) {
		this.maxCount = maxCount;
	}
	
	
	@Column(name="isMaxCountNoLimit")
	private boolean isMaxCountNoLimit = true;
	
	public boolean getIsMaxCountNoLimit() {
		return isMaxCountNoLimit;
	}

	public void setIsMaxCountNoLimit(boolean isMaxCountNoLimit) {
		this.isMaxCountNoLimit = isMaxCountNoLimit;
	}
	
	
	@Column(name="startDate")
	private String startDate;
	
	@Column(name="endDate")
	private String endDate;
	
	@Column(name="repeatRule")
	private CouponRepeatRule repeatRule = CouponRepeatRule.None;
	
	@Column(name="isAbsoluteDiscount")	
	private boolean isAbsoluteDiscount = true; //or is percentage
	
	@Column(name="discountValue")	
	private double discountValue;
	
	@Column(name="dilveryDiscountValue")	
	private double dilveryDiscountValue;
	
	public Integer getCoupanRuleId() {
		return coupanRuleId;
	}
	public void setCoupanRuleId(Integer coupanRuleId) {
		this.coupanRuleId = coupanRuleId;
	}
	public String getStartDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String sDate=null;
		try {
			if(this.endDate!=null){
			java.util.Date date = formatter.parse(this.startDate);
			sDate=formatter.format(date);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return sDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String eDate=null;
		try {
			if(this.endDate!=null){
			java.util.Date date = formatter.parse(this.endDate);
			eDate=formatter.format(date);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return eDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	public CouponRepeatRule getRepeatRule() {
		return repeatRule;
	}
	public void setRepeatRule(CouponRepeatRule repeatRule) {
		this.repeatRule = repeatRule;
	}
	public void setOrderSource(String orderSource) {
		this.orderSource = orderSource;
	}
	

	
	public String getOrderSource() {
		return orderSource;
	}
	public void setState(String orderSource) {
		this.orderSource = orderSource;
	} 
	
	public String getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	} 
	
	public double getMinOrderPayment() {
		return minOrderPayment;
	}
	public void setMinOrderPayment(double minOrderPayment) {
		this.minOrderPayment = minOrderPayment;
	} 
	
	
	
	public boolean getIsUsedOncePerCustomer() {
		return isUsedOncePerCustomer;
	}
	public void setIsUsedOncePerCustomer(boolean isUsedOncePerCustomer) {
		this.isUsedOncePerCustomer = isUsedOncePerCustomer;
	}
	
	
	public boolean getIsAbsoluteDiscount() {
		return isAbsoluteDiscount;
	}
	public void setIsAbsoluteDiscount(boolean isAbsoluteDiscount) {
		this.isAbsoluteDiscount = isAbsoluteDiscount;
	} 
	
	public double getDiscountValue() {
		return discountValue;
	}
	public void setDiscountValue(double discountValue) {
		this.discountValue = discountValue;
	} 
	
	public double getDilveryDiscountValue() {
		return dilveryDiscountValue;
	}
	public void setDilveryDiscountValue(double dilveryDiscountValue) {
		this.dilveryDiscountValue = dilveryDiscountValue;
	} 
	
	
}
