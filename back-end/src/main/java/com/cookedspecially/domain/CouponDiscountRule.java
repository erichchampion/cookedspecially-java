package com.cookedspecially.domain;


import java.util.List;





public class CouponDiscountRule implements ICouponRule {
	
	private boolean isAbsoluteDiscount; //or is percentage
	private double discountValue;
	private double dilveryDiscountValue;
	private Coupon coupon;
	
	
	public CouponDiscountRule(Coupon coup, boolean isAbDiscuont,double disVal, double divDisVal) {
		// TODO Auto-generated constructor stub
		this.coupon = coup;
		isAbsoluteDiscount = isAbDiscuont;
		discountValue = disVal;
		dilveryDiscountValue = divDisVal;
	}

	@Override
	public List<ICouponRule> getRuleList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean IsValid() {
		// TODO Auto-generated method stub
		
		
		return true;
	}

	@Override
	public double CalculateDiscount() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	

}
