package com.cookedspecially.domain;

import java.util.List;

public interface ICouponRule {
	
	List<ICouponRule> getRuleList();	
	boolean IsValid();
	double CalculateDiscount();

}


