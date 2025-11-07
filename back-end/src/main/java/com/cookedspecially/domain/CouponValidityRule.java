package com.cookedspecially.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.cookedspecially.controller.CouponController;
import com.cookedspecially.enums.CouponRepeatRule;



public class CouponValidityRule implements ICouponRule {
	
	private Date StartDate;
	private Date EndDate;
	private boolean isDurationRequired;
	private Coupon coupon;
	
	final static Logger logger = Logger.getLogger(CouponController.class);	
	
	private CouponRepeatRule repeatRule;
	
	public CouponValidityRule(Coupon coup, String start,String End,boolean isDurationRequired, CouponRepeatRule repeat) {
		// TODO Auto-generated constructor stub
		this.coupon = coup;
		this.isDurationRequired = isDurationRequired;
		
		Restaurant rest = this.coupon.getCouponService().getRestaurant(coup);
		
		
		SimpleDateFormat formatterD = new SimpleDateFormat("yyyy-MM-dd");		
		TimeZone tzD =  TimeZone.getTimeZone(rest.getTimeZone() );
		formatterD.setTimeZone(tzD);
		
		try {
			StartDate = formatterD.parse(start);
			EndDate = formatterD.parse(End);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		
		repeatRule = repeat;
	}

	@Override
	public List<ICouponRule> getRuleList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean IsValid() {
		
		CouponResponse coupResp = this.coupon.getCouponResponse();
		
		if(this.isDurationRequired)//if duration validation required;then check start and end date
		{
			//date of now
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
	
			//Here you say to java the initial timezone. This is the secret
			Restaurant rest = this.coupon.getCouponService().getRestaurant(this.coupon);
			TimeZone tzD =  TimeZone.getTimeZone(rest.getTimeZone() );
			sdf.setTimeZone(tzD);		
			
			//Will print in UTC
			System.out.println(sdf.format(calendar.getTime()));  
			Date newDate = calendar.getTime();
			
			
			if(newDate.compareTo(StartDate) < 0 || newDate.compareTo(EndDate) > 0)
			{
				logger.info("CouponValidityRule:IsValid-Coupon is not in date range");
				//coupon expired
				coupResp.setIsValid(false);
				coupResp.setError("Coupon has been expired");
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
