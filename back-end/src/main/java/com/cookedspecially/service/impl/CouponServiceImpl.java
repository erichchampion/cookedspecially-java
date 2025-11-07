/**
 * 
 */
package com.cookedspecially.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cookedspecially.controller.CouponController;
import com.cookedspecially.dao.CouponDAO;
import com.cookedspecially.dao.CouponRuleDAO;

import com.cookedspecially.domain.Coupon;
import com.cookedspecially.domain.CouponFlatRules;
import com.cookedspecially.domain.CouponResponse;
import com.cookedspecially.domain.JsonCouponInfo;
import com.cookedspecially.domain.Restaurant;
import com.cookedspecially.enums.CouponState;
import com.cookedspecially.service.CouponService;
import com.cookedspecially.service.RestaurantService;


@Service
public class CouponServiceImpl implements CouponService {
	
	final static Logger logger = Logger.getLogger(CouponService.class);
	
	@Autowired
	private RestaurantService restService;

	@Autowired
	private CouponDAO couponDAO;
	
	@Autowired
	private CouponRuleDAO couponRuleDAO;
	
	
	@Override
	@Transactional
	public void addCoupon(Coupon coupon)
	{
		CouponFlatRules cfr =  coupon.getFlatRules();		
		couponRuleDAO.addRule(cfr);		
		coupon.setCouponRuleID(cfr.getCoupanRuleId());		
		couponDAO.addCoupon(coupon);		
	}
	@Override
	@Transactional
	public void updateCoupon(Coupon coupon)
	{
		CouponFlatRules cfr =  coupon.getFlatRules();		
		couponRuleDAO.updateRule(cfr);		
		coupon.setCouponRuleID(cfr.getCoupanRuleId());	
		couponDAO.updateCoupon(coupon);	
	}
	
	@Override
	@Transactional
	public List<Coupon> listCoupon()
	{
		return couponDAO.listCoupon();
	}
	
	@Override
	@Transactional
	public Coupon getCouponById(Integer CouponId)
	{
		Coupon c = couponDAO.getCouponById(CouponId);
		if(c != null && c.getCouponRuleID() != null)
		{
			CouponFlatRules r = couponRuleDAO.getRuleById(c.getCouponRuleID());		
			c.setFlatRules(r);			
			
		}
		
		return c;
		
		
	}
	
	//multiple coupon allowed with same code
	@Override
	@Transactional
	public List<Coupon> getAllCouponsByCode(String CouponCode, Integer restaurantId)
	{
		List<Coupon> coupList = couponDAO.getAllCouponsByCode(CouponCode, restaurantId);
		for(Coupon c:coupList)
		{
			if(c != null && c.getCouponRuleID() != null)
			{
			CouponFlatRules r = couponRuleDAO.getRuleById(c.getCouponRuleID());		
			c.setFlatRules(r);	
			
			}
		}
		return coupList;
		
	}
	
	//only one enable coupon possible with a rest	
	@Override
	@Transactional
	public Coupon getEnabledCouponByCode(String couponCode, Integer restaurantId) {
		// TODO Auto-generated method stub
		Coupon c = couponDAO.getEnabledCouponByCode(couponCode, restaurantId);
		if(c != null && c.getCouponRuleID() != null)
		{
		CouponFlatRules r = couponRuleDAO.getRuleById(c.getCouponRuleID());		
		c.setFlatRules(r);	
		
		}
		
		return c;
	}
	
	
	
	@Override
	@Transactional
	public Restaurant getRestaurant(Coupon cpn)
	{
		return restService.getRestaurant(cpn.getRestaurantID());
	}
	
	
	//currently these not containing CouponRule and checks
	@Override
	@Transactional	
	public List<Coupon> listCouponByResturantId(Integer restaurantId)
	{
		return couponDAO.listCouponByResturantId(restaurantId);
	}
	
	@Override
	@Transactional
	public List<Coupon> getCouponListByCouponState(CouponState state, Integer restaurantId){
		return couponDAO.getCouponListByCouponState(state,restaurantId);
		
	}
	
	@Override
	@Transactional
	public void removeCoupon(Integer id)
	{
		Coupon c = couponDAO.getCouponById(id);
		if(c != null)
		{
			if(c.getCouponRuleID() != null)
				couponRuleDAO.removeRule(c.getCouponRuleID());
			
		couponDAO.removeCoupon(id);
		}
	}
	
	@Override
	@Transactional
	public CouponResponse getCouponDef(JsonCouponInfo coupInfo)
	{
		CouponResponse coupResp;  
		Coupon c = couponDAO.getEnabledCouponByCode(coupInfo.getCouponCode(),coupInfo.getRestaurantID());
		if(c == null)
		{
			logger.info("getCouponDef- " + coupInfo.getCouponCode() + "-Coupon does not exist");
			coupResp = new CouponResponse();
			coupResp.setIsValid(false);
			coupResp.setError("Coupon does not exist");
			return coupResp;
		}
		
		CouponFlatRules r = couponRuleDAO.getRuleById(c.getCouponRuleID());		
		c.setFlatRules(r);
		
		c.IsValid(this, coupInfo);
		coupResp = c.getCouponResponse();
		coupResp.setCouponName(c.getCouponName());
		coupResp.setCouponCode(c.getCouponCode());
		
//		if(c.IsValid(this, coupInfo))
//		{
//			
//		}
//		else
//		{
//			coupResp.setIsValid(false);
//			coupResp.setError("Coupon is not valid");
//		}
		
		return coupResp;
	
	}

	@Override
	@Transactional
	public boolean IsCustomerAssociate(Integer couponID, Integer custID)
	{
		//see using table check_coupon, if is there any customer exist
		//return false;	
		return couponDAO.isCustomerAssociate(couponID, custID);
				
	}
	
	

	
}
