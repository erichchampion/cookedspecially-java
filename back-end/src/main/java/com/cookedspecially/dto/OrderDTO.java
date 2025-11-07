package com.cookedspecially.dto;

import java.util.Date;
import java.util.List;

import com.cookedspecially.domain.Coupon;
import com.cookedspecially.enums.check.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Ankit on 8/27/2015 , rahul.
 */
@JsonIgnoreProperties(ignoreUnknown = true) 
public class OrderDTO {

    public String id;
    public String checkId;
    public String orderType;
    public Status paymentStatus;
    public Date deliveryTime;
    public Date orderTime;
    public String table;
    public String deliveryArea;
    public String customerName;
    public List<OrderDishDTO> items;
    public Integer fulfillmentCenterId ;
    public String customerMobNo;
    public Integer customerId;
    public String customerEmail;
    public String status;
    public String invoiceId;
    public String paymentMethod;
    public String orderSource;
    public Double orderAmount;
    public Long changeAmount;
    public Date lastModified;
    public String deliveryAddress;
    public String deliveryAgent;
    public Integer deliveryAgentId;
    public Float discountAmount;
    public Float discountPercentage;
    public String instructions;
    public List<DiscountDTO> discountList;
    public String deliveryDateTime;
    public Integer deliveryCharges;
    public Integer finalOrderAmount;
    public String paidStatus;
    public String taxJsonObj;
    public boolean isfirstOrder;
    public boolean allowEdit;
    public String remark;
    public boolean isEdited;
    public float creditBalance;
    public List<Coupon> couponApplied;
    public List<String> couponCode;
    public Float zomatoGrossAmount;
}
