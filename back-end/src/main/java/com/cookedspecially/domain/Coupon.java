/**
 * 
 */
package com.cookedspecially.domain;

import java.util.ArrayList;
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

import com.cookedspecially.controller.CouponController;

import com.cookedspecially.enums.CouponState;

import com.cookedspecially.service.CouponService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;



@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="coupon")
public class Coupon {
	
	final static Logger logger = Logger.getLogger(CouponController.class);	

	//unique id for internal
	@Id
	@Column(name="coupanId")
	@GeneratedValue
	private Integer coupanId;
	
	//to maintain history while editing coupon which is already redeemed; 
	//it creates new coupon (child)
	@Column(name="parentCouponId")
	private Integer parentCouponId = -1;
	
	@Column(name="restaurantID")
	private Integer restaurantID;
	
	@Column(name="couponRuleID")
	private Integer couponRuleID;
	
	/*
	 * using now State
	@Column(name="isDisabled")
	private boolean isDisabled = true;
	
	public boolean getIsDisabled() {
		return isDisabled;
	}

	public void setIsDisabled(boolean isDisabled) {
		this.isDisabled = isDisabled;
	}
	
	*/

	@Column(name="redeemedCount")
	private Integer redeemedCount=0;

	

	public Integer getRedeemedCount() {
		return redeemedCount;
	}

	public void setRedeemedCount(Integer redeemedCount) {
		this.redeemedCount = redeemedCount;
	}

	//coupon name 
	@Column(name="couponName")
	private String couponName;
		
	
	//coupon code to apply
	@Column(name="couponCode")
	private String couponCode;
		
	//coupon marketing message
	@Column(name="description")
	private String description;
		
	//Active/Expired
	//not being used currently; let see if it is helpful in future
	@Column(name="state")
	private CouponState state = CouponState.Disabled;
	

	
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany()
	@Cascade({org.hibernate.annotations.CascadeType.REFRESH})	
	@JoinTable(name="CHECK_COUPON_LIST", 
	                joinColumns={@JoinColumn(name="COUPONID")}, 
	                inverseJoinColumns={@JoinColumn(name="CHECKID")})	
	@JsonIgnoreProperties("coupon_Applied")		
	private List<Check> check_used = new ArrayList<Check>();
	
	
	public List<Check> getCheck_used() {
		return this.check_used;
	}
	public void setCheck_used(List<Check> check_used) {
		this.check_used = check_used;
	}
	
	@Transient
	private List<ICouponRule> ruleList;	
	
	@Transient
	private CouponFlatRules flatRules ;//= new CouponFlatRules();	
	
	public List<ICouponRule> getRuleList(boolean bNeedpopulate)
	{
		if(ruleList == null || bNeedpopulate )
		{
		ruleList = new ArrayList<ICouponRule>();
		
		ICouponRule rule = new CouponConditionRule(this,flatRules.getOrderSource(), flatRules.getPaymentMode(), 
				flatRules.getMinOrderPayment(),flatRules.getDeliveryAreas());
		ruleList.add(rule);
		
		rule = new CouponLimitRule(this,flatRules.getIsUsedOncePerCustomer(),flatRules.getMaxCount(),flatRules.getIsMaxCountNoLimit());
		ruleList.add(rule);
			
		rule = new CouponValidityRule(this,flatRules.getStartDate(),flatRules.getEndDate(),flatRules.getIsDurationRequired(),
				flatRules.getRepeatRule());
		ruleList.add(rule);
		
		rule = new CouponDiscountRule(this,flatRules.getIsAbsoluteDiscount(),flatRules.getDiscountValue(),flatRules.getDilveryDiscountValue());
		ruleList.add(rule);
		}
		
		return ruleList;
		
	}
	
	public boolean IsValid(CouponService serV, JsonCouponInfo pcoupInfo)
	{
		this.coupResponse = new CouponResponse();
		
		this.couponService = serV;
		this.jsonCoupInfo = pcoupInfo;		
		
		logger.info("Coupon:IsValid- Validating.." + this.getCouponCode());
		if(this.state != CouponState.Enabled)
		{
			logger.info("Coupon:IsValid -Coupon is disabled");
			this.coupResponse.setIsValid(false);
			this.coupResponse.setError("Coupon is not valid");			
			return false;
		}
		
		List<ICouponRule> couponRuleList = getRuleList(true);
		for(ICouponRule rule : couponRuleList)
		{
			if(!rule.IsValid())
			{
				if(this.coupResponse.getIsValid())
				{
					this.coupResponse.setRules(this.flatRules);
				}
				//break
				//coupResponse already filled by rule
				return false;
			}
		}	
		
		this.coupResponse.setRules(this.flatRules);
		this.coupResponse.setIsValid(true);
		this.coupResponse.setIsCouponApplicable(true);
		this.coupResponse.setError("No Error");
		this.coupResponse.setCouponName(this.couponName);
		logger.info("Coupon:IsValid- Coupon is correct, No Error.");

		return true;
	}
	
	@Transient
	JsonCouponInfo jsonCoupInfo;
	@Transient
	CouponService couponService;
	@Transient
	CouponResponse coupResponse;
	
	public CouponResponse getCouponResponse()
	{
		return coupResponse;
	}
	public JsonCouponInfo getJsonCouponInfo()
	{
		return jsonCoupInfo;
	}
	public CouponService getCouponService()
	{
		return couponService;
	}
	
	
	public Integer getParentCouponId() {
		return parentCouponId;
	}


	public void setParentCouponId(Integer parentCouponId) {
		this.parentCouponId = parentCouponId;
	}
	
	public Integer getCoupanId() {
		return coupanId;
	}


	public void setCoupanId(Integer coupanId) {
		this.coupanId = coupanId;
	}


	

	public String getCouponName() {
		return couponName;
	}


	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}


	


	public List<ICouponRule> getRuleList() {
		return ruleList;
	}


	public void setRuleList(List<ICouponRule> ruleList) {
		this.ruleList = ruleList;
	}
	
	public CouponFlatRules getFlatRules() {
		return flatRules;
	}
	public void setFlatRules(CouponFlatRules flatRules) {
		this.flatRules = flatRules;
	}
	
	public CouponState getState() {
		return state;
	}
	public void setState(CouponState state) {
		
		//just for protection
		//allow state change only if existing state is not nonactive;
		//once coupon reach to NonActive, it cannot be enabled or disabled again by  mistake
		if(this.state != CouponState.NonActive)
		{
			this.state = state;
		}
	}
	
		
	public String getDescription() {
		return description;
	}
	public void setDescription(String desc) {
		this.description = desc;
	}
	
	public String getCouponCode() {
		return couponCode;
	}
	public void setCouponCode(String code) {
		this.couponCode = code;
	}
	
	
	
	public Integer getRestaurantID() {
		return restaurantID;
	}

	public void setRestaurantID(Integer restaurantID) {
		this.restaurantID = restaurantID;
	}

	public Integer getCouponRuleID() {
		return couponRuleID;
	}

	public void setCouponRuleID(Integer couponRuleID) {
		this.couponRuleID = couponRuleID;
	}
	
}
