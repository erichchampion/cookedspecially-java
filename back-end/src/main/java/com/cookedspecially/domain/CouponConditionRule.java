package com.cookedspecially.domain;


import java.util.List;

import org.apache.log4j.Logger;

import com.cookedspecially.controller.CouponController;
import com.cookedspecially.enums.check.OrderSource;
import com.cookedspecially.enums.check.PaymentMode;
import com.cookedspecially.domain.DeliveryArea;


public class CouponConditionRule implements ICouponRule {
	
	private String orderSource;
	private String paymentMode;
	private double minOrderPayment;
	private Coupon couponOwner;
	
	private List<DeliveryArea> deliveryAreas;
	
	final static Logger logger = Logger.getLogger(CouponController.class);	
	
	
	public CouponConditionRule(Coupon coupon, String OSource,String pMode, double minPayment,List<DeliveryArea> delAreas) {
		
		
		couponOwner = coupon;
		orderSource = OSource;
		paymentMode = pMode;
		minOrderPayment = minPayment;
		deliveryAreas = delAreas;
	}

	@Override
	public List<ICouponRule> getRuleList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean IsValid() {
		
		JsonCouponInfo coupInfo = couponOwner.getJsonCouponInfo();
		CouponResponse coupResp = couponOwner.getCouponResponse();	

		
		if(!this.orderSource.equalsIgnoreCase(OrderSource.Any.toString()))
		{
			System.out.println(coupInfo.getOrderSource() +"-- ");	
			if(!coupInfo.getOrderSource().equalsIgnoreCase(this.orderSource))
			{
				logger.info("CouponConditionRule:IsValid-OrderSource is not valid");
				coupResp.setIsValid(true);
				coupResp.setIsCouponApplicable(false);
				coupResp.setError("Coupon is only applicable when order from " + this.orderSource);
				return false;
			}		
		}
		if(!this.paymentMode.equalsIgnoreCase(PaymentMode.ANY.toString()))
		{
			if(coupInfo.getPaymentMode() != this.paymentMode)
			{
				logger.info("CouponConditionRule:IsValid-PaymentMode is not valid");
				coupResp.setIsValid(true);
				coupResp.setIsCouponApplicable(false);
				coupResp.setError("Coupon is only applicable when payment mode is " + this.paymentMode);
				return false;
			}		
		}				
				
		if(coupInfo.getOrderAmount() < this.minOrderPayment )
		{
			logger.info("CouponConditionRule:IsValid-Total amount is less than expected");
			coupResp.setIsValid(true);
			coupResp.setIsCouponApplicable(false);
			coupResp.setError("Total amount should exceed " + this.minOrderPayment);
			return false;
		}	
		if(deliveryAreas.size() > 0) //implies coupon is delivery area specific
		{
			final int delAreaId = coupInfo.getDeliveryAreaId();
			if(delAreaId <= 0)//must provide delivery area before applying the coupon;otherwise error
			{
				//error
				logger.info("CouponConditionRule:IsValid-DeliveryArea is not valid");
				coupResp.setIsValid(true);
				coupResp.setIsCouponApplicable(false);
				coupResp.setError("Coupon is delivery area specific; Please provide delivery Area");
				return false;
			}
			
			boolean bIsPresent = false;
			for(DeliveryArea delArea : this.deliveryAreas){
				if(delArea.getId() == delAreaId)
				{	
					bIsPresent = true;
					break;					
				}
			}
			if(!bIsPresent){
				logger.info("CouponConditionRule:IsValid-DeliveryArea is not valid");
				coupResp.setIsValid(true);
				coupResp.setIsCouponApplicable(false);
				coupResp.setError("Coupon is only applicable to specific delivery areas");
				return false;				
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

