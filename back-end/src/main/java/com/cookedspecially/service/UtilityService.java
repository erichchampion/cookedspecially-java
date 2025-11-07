package com.cookedspecially.service;

import com.cookedspecially.dto.ResponseDTO;
import com.cookedspecially.dto.notification.ResultDTO;

public interface UtilityService {
	//@PreAuthorize("hasAnyRole('restaurantManager','admin')")
	public ResultDTO formateMobileNo(Integer orgId, int batchSize);
	//@PreAuthorize("hasAnyRole('restaurantManager','admin')")
	public ResultDTO listInvalidMobileNoCustomer(Integer orgId);
	//@PreAuthorize("hasAnyRole('restaurantManager','admin')")
	public ResultDTO removeInvalidCustomerWithInvalidPhone(Integer orgId,int batchSize);
	//@PreAuthorize("hasAnyRole('restaurantManager','admin')")
	public ResponseDTO sendTestOTP(String otpDetails);
	//@PreAuthorize("hasAnyRole('restaurantManager','admin')")
	public ResponseDTO formateMobileNoOfChecks(int batchSize);
	public ResponseDTO removeDuplicateCustomer(Integer orgId);
}
