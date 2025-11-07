package com.cookedspecially.controller;

import io.swagger.annotations.Api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cookedspecially.dto.ResponseDTO;
import com.cookedspecially.dto.notification.ResultDTO;
import com.cookedspecially.service.UtilityService;

@Controller
@RequestMapping("/utility")
@Api(description="Utility REST API's")
public class UtilityController {

	final static Logger logger = Logger.getLogger(UtilityController.class);
	
	
	@Autowired
	private UtilityService utilityService;
	
	
	@RequestMapping(value = "/formateMobileNo/{batchSize}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO formateMobileNo(@PathVariable("batchSize") Integer batchSize,HttpServletRequest request, HttpServletResponse response) throws JSONException{
		if(batchSize==null || batchSize.intValue()==0)
			batchSize=300; 
		return utilityService.formateMobileNo(null, batchSize);
	}
	@RequestMapping(value = "/listInvalidMobileNoCustomer", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO listInvalidMobileNoCustomer(HttpServletRequest request) throws JSONException{
		//Integer.parseInt(request.getSession().getAttribute("organisationId").toString());
		 return utilityService.listInvalidMobileNoCustomer(null);
	}
	
	@RequestMapping(value = "/removeInvalidCustomer/{batchSize}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO removeInvalidCustomer(@PathVariable("batchSize") Integer batchSize) throws JSONException{
		if(batchSize==null || batchSize.intValue()==0)
			batchSize=300;
		 return utilityService.removeInvalidCustomerWithInvalidPhone(null,batchSize);
	}
	
	/*   UPDATE MOBILE NO IN CHECK TABLE */
	@RequestMapping(value = "/formateMobileNoOfChecks/{batchSize}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseDTO formateMobileNoOfChecks(@PathVariable("batchSize") Integer batchSize){
		if(batchSize==null || batchSize.intValue()==0)
			batchSize=300;
		return utilityService.formateMobileNoOfChecks(batchSize);
	}
	
	@RequestMapping(value = "/removeDuplicateCustomer", method = RequestMethod.GET)
	@ResponseBody
	public ResponseDTO removeDuplicateCustomer(){
		return utilityService.removeDuplicateCustomer(0);
	}
	
	/*   OTP through SMS Test API */
	@RequestMapping(value = "/sendTestOTP/{otpDetails}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseDTO sendTestOTP(@PathVariable String otpDetails){
		return utilityService.sendTestOTP(otpDetails);
	}
}
