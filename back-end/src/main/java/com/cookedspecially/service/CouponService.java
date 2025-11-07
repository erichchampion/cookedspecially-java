/**
 * 
 */
package com.cookedspecially.service;

import java.util.List;

import com.cookedspecially.domain.Coupon;
import com.cookedspecially.domain.CouponResponse;
import com.cookedspecially.domain.JsonCouponInfo;
import com.cookedspecially.domain.Restaurant;
import com.cookedspecially.enums.CouponState;



public interface CouponService {


	public void addCoupon(Coupon coupon);
	public void updateCoupon(Coupon coupon);
	public void removeCoupon(Integer id);
	
	public List<Coupon> listCoupon();
	public List<Coupon> listCouponByResturantId(Integer restaurantId);
	
	public Coupon getCouponById(Integer CouponId);
	//multiple coupon allowed with same code
	public List<Coupon> getAllCouponsByCode(String CouponCode, Integer restaurantId);
	//only one enable coupon possible with a rest
	public Coupon getEnabledCouponByCode(String couponCode, Integer restaurantId);
	
	public List<Coupon> getCouponListByCouponState(CouponState state, Integer restaurantId);
	
			
	public CouponResponse getCouponDef(JsonCouponInfo coupInfo);
	public boolean IsCustomerAssociate(Integer couponID, Integer custID);
	
	public Restaurant getRestaurant(Coupon cpn);
	
	
}