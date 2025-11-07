package com.cookedspecially.domain;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.cookedspecially.controller.CouponController;
import com.cookedspecially.enums.CouponRepeatRule;



public class CouponLimitRule implements ICouponRule {
	
	
	final static Logger logger = Logger.getLogger(CouponController.class);	
	private boolean isUsedOncePerCustomer;  //or multipletimes could be
	private Coupon coupon;
	
	private boolean isNoLimitCount;  //or multipletimes could be
	private int maxLimit;
	
	
	public CouponLimitRule(Coupon pcoupon, boolean isUsedOnce, int nMaxLimit, boolean bIsNoLimitCount) {
		coupon = pcoupon;
		isUsedOncePerCustomer = isUsedOnce;
		
		isNoLimitCount = bIsNoLimitCount;
		maxLimit =nMaxLimit;
		
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<ICouponRule> getRuleList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean IsValid() {
		
		CouponResponse coupResp = this.coupon.getCouponResponse();
		
		if(!this.isNoLimitCount && (this.coupon.getCheck_used().size() >= this.maxLimit))
		{
			logger.info("CouponLimitRule:IsValid- Coupon is already redeemed as per its count.");
			coupResp.setIsValid(false);
			coupResp.setError("Coupon is already redeemed as per its redemption limit and not valid now.");			
			return false;
		}
		
		if(this.isUsedOncePerCustomer)
		{
			int custID = this.coupon.jsonCoupInfo.getCustomerId();
			if(custID > 0)
			{
				if(coupon.getCouponService().IsCustomerAssociate(coupon.getCoupanId(), custID))
				{
					logger.info("CouponLimitRule:IsValid-Coupon already redeemed by customer");
					coupResp.setIsValid(true);
					coupResp.setIsCouponApplicable(false);
					coupResp.setError("Coupon is already redeemed by this customer");
					return false;
				}
			}
		}
		
		
		
		return true;
	}

	@Override
	public double CalculateDiscount() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	
	

}
