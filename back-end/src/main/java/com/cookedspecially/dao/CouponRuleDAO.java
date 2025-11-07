/**
 * 
 */
package com.cookedspecially.dao;

import com.cookedspecially.domain.CouponFlatRules;

public interface CouponRuleDAO {
	
	public void addRule(CouponFlatRules rule);	
	public void updateRule(CouponFlatRules rule);
	public CouponFlatRules getRuleById(Integer ruleId);
	public CouponFlatRules getRuleByCouponId(Integer couponId);
	public void removeRule(Integer ruleId);
	public void removeRuleByCouponID(Integer couponId);
	

}
