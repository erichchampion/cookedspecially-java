/**
 * 
 */
package com.cookedspecially.domain;

import java.io.Serializable;

/**
 * Return as rest when client call for coupon definetion
 *
 */
public class CouponResponse implements Serializable {
    private static final long serialVersionUID = 1L;
	
    //Is coupon is valid at all
    // if valid, give me Coupon def
    // otherwise don't.
    boolean IsValid;    
    
    String couponName;
	String couponCode;

	// If coupon valid, is it still applicable? if not give me error and still
    // can send couponDef to client if they want it further processing on client side.
    //>>Coupon exist/Valid but not applicable for this order/check
    boolean IsCouponApplicable;
    
    //Error string
	String error;
	
	CouponFlatRules rules;
		
	public CouponFlatRules getRules() {
		return rules;
	}

	public void setRules(CouponFlatRules rules) {
		this.rules = rules;
	}

	public boolean isIsCouponApplicable() {
		return IsCouponApplicable;
	}

	public void setIsCouponApplicable(boolean isCouponApplicable) {
		IsCouponApplicable = isCouponApplicable;
	}
	public boolean getIsValid() {
		return IsValid;
	}

	public void setIsValid(boolean isValid) {
		IsValid = isValid;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getCouponName() {
		return couponName;
	}

	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}
	
	
}
