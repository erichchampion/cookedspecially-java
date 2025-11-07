package com.cookedspecially.enums;

public enum CouponState {
   //Valid Coupon (enabled/disabled/Date Expired/Visible)
	Enabled, 
	Disabled,
	
	//Hidden; mostly hidden and will be only used for reporting already applied
	//generate when edit/delete any coupon already applied to any check;
	//Which cannot be edit; cannot be applied; used only for reporting
   NonActive,  
   
   //placeholder
   None
}



