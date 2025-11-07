/**
 * 
 */
package com.cookedspecially.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.cookedspecially.enums.check.CheckType;
import com.cookedspecially.enums.check.PaymentMode;
import com.cookedspecially.enums.check.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRawValue;

/**
 * @author shashank
 *
 */
 
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="CHECKS")
public class Check implements Serializable {
    private static final long serialVersionUID = 1L;
	@Id
	@Column(name="ID")
	@GeneratedValue
	private Integer checkId;
	
	@Column(name="RESTAURANTID")
	private Integer restaurantId;
	
	@Column(name="USERID")
	private Integer userId;
	
	@Column(name="TABLEID")
	private Integer tableId;
	
	@Column(name="CUSTOMERID")
	private Integer customerId;
	
	@Column(name="GUESTS")
	private Integer guests;
	
	@Column(name="PAYMENT")
	private PaymentMode payment;
	
	@Column(name="STATUS")
	private Status status;
	
	@Column(name="CHECKTYPE")
	private CheckType checkType;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="OPENTIME")
	private Date openTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CLOSETIME")
	private Date closeTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="MODIFIEDTIME")
	private Date modifiedTime;
	
	@Column(name="BILL")
	private float bill;
	
	@Column(name="responseCode")
	private String responseCode;
	
	@Column(name="additionalChargesName1")
	private String additionalChargesName1;
	
	@Column(name="additionalChargesName2")
	private String additionalChargesName2;
	
	@Column(name="additionalChargesName3")
	private String additionalChargesName3;
	
	@Column(name="additionalChargesValue1")
	private float additionalChargesValue1 = 0.0f;
	
	@Column(name="additionalChargesValue2")
	private float additionalChargesValue2 = 0.0f;
	
	@Column(name="additionalChargesValue3")
	private float additionalChargesValue3 = 0.0f;
	
	@Column(name ="deliveryTime")
	private Date deliveryTime;
	
	@Column(name="deliveryArea")
	private String deliveryArea;
	
	@Column(name="outCircleDeliveryCharges")
	private float outCircleDeliveryCharges =0.0f;
	
	@Column(name="deliveryAddress")
	private String deliveryAddress;
	
	@Column(name="transactionId")
	private String transactionId;
	
	@Column(name="transactionStatus")
	private String transactionStatus;
	
	@Column(name="phone")
	private String  phone;
	
	@Column(name="invoiceId")
	private String invoiceId;
	
	@Column(name="orderId")
	private Integer orderId;
	
	@Column(name="name")
	private String name;
	
	@Column(name="discountPercent")
	private float discountPercent;
	
	@Column(name="discountAmount")
	private float discountAmount;
	
	@Column(name="roundOffTotal")
	private double roundOffTotal;
	
	@Column(name="taxJsonObject")
	private String taxJsonObject;
	
	@Column(name="paymentType")
	private String paymentType;
	
	@Column(name="paymentDetail")
	private String paymentDetail;
	
	@Column(name="kitchenScreenId")
	private int kitchenScreenId;

	@Column(name="orderSource")
	private String orderSource;
	
	@Column(name="deliveryInst")
	private String deliveryInst;
	
	@Column(name="rewards")
	private Double rewards;
	
	@Column(name="editOrderRemark")
	private String editOrderRemark;
	
	@Column(name="firstOrder")
	private boolean firstOrder;
	
	@Column(name="allowEdit")
	private boolean allowEdit;
	
	@Column(name="creditBalance")
	private float creditBalance;
	
	@Column(name="lastInvoiceAmount")
	private float lastInvoiceAmount;
	
	@Column(name="zomatoOrderId")
	private Integer zomatoOrderId;
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(orphanRemoval=true)
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.REMOVE})
	@JoinTable(name="CHECK_ORDER", 
	                joinColumns={@JoinColumn(name="CHECKID")},
	                inverseJoinColumns={@JoinColumn(name="ORDERID")})
	private List<Order> orders = new ArrayList<Order>();
	
	@OneToMany(fetch=FetchType.EAGER, orphanRemoval=true)
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.REMOVE})
	@JoinTable(name="CHECK_DCLIST", 
	                joinColumns={@JoinColumn(name="CHECKID")}, 
	                inverseJoinColumns={@JoinColumn(name="ID")})
	private List<Order_DCList> discount_Charge = new ArrayList<Order_DCList>();
	
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany()
	@Cascade({org.hibernate.annotations.CascadeType.REFRESH})	
	@JoinTable(name="CHECK_COUPON_LIST", 
	                joinColumns={@JoinColumn(name="CHECKID")}, 
	                inverseJoinColumns={@JoinColumn(name="COUPONID")})	
	@JsonIgnoreProperties("check_used")		
	private List<Coupon> coupon_Applied = new ArrayList<Coupon>();
	
	
	public List<Coupon> getCoupon_Applied() {
		return coupon_Applied;
	}

	public void setCoupon_Applied(List<Coupon> coupon_Applied) {
		this.coupon_Applied.clear();
		this.coupon_Applied.addAll(coupon_Applied);
		//this.coupon_Applied = coupon_Applied;
	}
	
	public boolean isFirstOrder() {
		return firstOrder;
	}

	public void setFirstOrder(boolean firstOrder) {
		this.firstOrder = firstOrder;
	}
	
	public String getEditOrderRemark() {
		return editOrderRemark;
	}

	public void setEditOrderRemark(String editOrderRemark) {
		this.editOrderRemark = editOrderRemark;
	}
	
	public Double getRewards() {
		return rewards;
	}

	public void setRewards(Double rewards) {
		this.rewards = rewards;
	}

	public String getDeliveryInst() {
		return deliveryInst;
	}

	public void setDeliveryInst(String deliveryInst) {
		this.deliveryInst = deliveryInst;
	}

	public String getOrderSource() {
		return orderSource;
	}

	public void setOrderSource(String thirdParty) {
		this.orderSource = thirdParty;
	}
	
	
	public List<Order_DCList> getDiscount_Charge() {
		return discount_Charge;
	}

	public void setDiscount_Charge(List<Order_DCList> discount_Charge) {
		this.discount_Charge = discount_Charge;
	}

	public int getKitchenScreenId() {
		return kitchenScreenId;
	}

	public void setKitchenScreenId(int kitchenScreenId) {
		this.kitchenScreenId = kitchenScreenId;
	}
	
	@JsonIgnore
	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	@JsonIgnore
	public String getPaymentDetail() {
		return paymentDetail;
	}

	public void setPaymentDetail(String paymentDetail) {
		this.paymentDetail = paymentDetail;
	}
	
	@JsonIgnore
	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	@JsonRawValue
	public String getTaxJsonObject() {
		return taxJsonObject;
	}

	public void setTaxJsonObject(String taxJsonObject) {
		this.taxJsonObject = taxJsonObject;
	}
	
	public double getRoundOffTotal() {
		return roundOffTotal;
	}

	public void setRoundOffTotal(double roundOffTotal) {
		this.roundOffTotal = roundOffTotal;
	}
	
	public Date getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(Date deliveryTime) {
		this.deliveryTime = deliveryTime;
	}
	
	public Integer getOrderId() {
		
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		
		this.orderId = orderId;
	}

	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Integer getCheckId() {
		return checkId;
	}

	public void setCheckId(Integer checkId) {
		this.checkId = checkId;
	}

	public Integer getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}

	@JsonIgnore
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@JsonIgnore
	public Integer getTableId() {
		return tableId == null ? -1 : tableId;
	}

	public void setTableId(Integer tableId) {
		this.tableId = tableId;
	}

	public Integer getCustomerId() {
		return customerId == null ? -1: customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	@JsonIgnore
	public PaymentMode getPayment() {
		return payment;
	}

	public void setPayment(PaymentMode payment) {
		this.payment = payment;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}
	@JsonIgnore
	public Integer getGuests() {
		return guests;
	}


	public void setGuests(Integer guests) {
		this.guests = guests;
	}

	public Date getOpenTime() {
		return openTime;
	}

	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
	}

	public Date getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(Date closeTime) {
		this.closeTime = closeTime;
	}

	public Date getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(Date modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public float getBill() {
		return bill;
	}

	public void setBill(float bill) {
		this.bill = bill;
	}

	@JsonIgnore
	public String getAdditionalChargesName1() {
		return additionalChargesName1;
	}

	public void setAdditionalChargesName1(String additionalChargesName1) {
		this.additionalChargesName1 = additionalChargesName1;
	}

	@JsonIgnore
	public String getAdditionalChargesName2() {
		return additionalChargesName2;
	}

	public void setAdditionalChargesName2(String additionalChargesName2) {
		this.additionalChargesName2 = additionalChargesName2;
	}
	@JsonIgnore
	public String getAdditionalChargesName3() {
		return additionalChargesName3;
	}

	public void setAdditionalChargesName3(String additionalChargesName3) {
		this.additionalChargesName3 = additionalChargesName3;
	}
	@JsonIgnore
	public float getAdditionalChargesValue1() {
		return additionalChargesValue1;
	}

	public void setAdditionalChargesValue1(float additionalChargesValue1) {
		this.additionalChargesValue1 = additionalChargesValue1;
	}
	
	@JsonIgnore
	public float getAdditionalChargesValue2() {
		return additionalChargesValue2;
	}

	public void setAdditionalChargesValue2(float additionalChargesValue2) {
		this.additionalChargesValue2 = additionalChargesValue2;
	}

	@JsonIgnore
	public float getAdditionalChargesValue3() {
		return additionalChargesValue3;
	}

	public void setAdditionalChargesValue3(float additionalChargesValue3) {
		this.additionalChargesValue3 = additionalChargesValue3;
	}

	public CheckType getCheckType() {
		return checkType;
	}

	public void setCheckType(CheckType checkType) {
		this.checkType = checkType;
	}

	public String getDeliveryArea() {
		// Erich: A hack, since many reports have been hardcoded to only work with a non-null value
		return deliveryArea != null ? deliveryArea : "N/A";
	}

	public void setDeliveryArea(String deliveryArea){
		this.deliveryArea = deliveryArea;
	}
	
	public float getOutCircleDeliveryCharges() {
		return outCircleDeliveryCharges;
	}

	public void setOutCircleDeliveryCharges(float outCircleDeliveryCharges) {
		this.outCircleDeliveryCharges = outCircleDeliveryCharges;
	}
		
	public String getDeliveryAddress() {
		// Erich: A hack, since many reports have been hardcoded to only work with a non-null value
		return deliveryAddress != null ? deliveryAddress : "N/A";
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public float getDiscountPercent() {
		return discountPercent;
	}

	public void setDiscountPercent(float discountPercent) {
		this.discountPercent = discountPercent;
	}

	public float getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(float discountAmount) {
		this.discountAmount = discountAmount;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getTransactionStatus() {
		return transactionStatus;
	}
	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}
	
	public boolean isAllowEdit() {
		return allowEdit;
	}

	public void setAllowEdit(boolean allowEdit) {
		this.allowEdit = allowEdit;
	}
	
	public float getCreditBalance() {
		return creditBalance;
	}

	public void setCreditBalance(float creditBalance) {
		this.creditBalance = creditBalance;
	}
	
	public float getLastInvoiceAmount() {
		return lastInvoiceAmount;
	}

	public void setLastInvoiceAmount(float lastInvoiceAmount) {
		this.lastInvoiceAmount = lastInvoiceAmount;
	}
	
	public Integer getZomatoOrderId() {
		return zomatoOrderId;
	}

	public void setZomatoOrderId(Integer zomatoOrderId) {
		this.zomatoOrderId = zomatoOrderId;
	}

}
