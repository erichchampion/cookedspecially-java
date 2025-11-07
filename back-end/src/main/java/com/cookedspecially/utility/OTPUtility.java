package com.cookedspecially.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cookedspecially.dao.NotificationDAO;
import com.cookedspecially.domain.Notifier;

import com.cookedspecially.domain.OTP;
import com.cookedspecially.dto.notification.ResultDTO;
import com.cookedspecially.eXoTel.ExOTel;
import com.cookedspecially.enums.notification.Device;
import com.cookedspecially.service.impl.CashRegisterServiceImpl;

/**
 * @author Abhishek 
 *
 */
public class OTPUtility {
	
	private static final Random generator = new Random();
	private static final ExOTel eXoTel=new ExOTel();
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	final static Logger logger = Logger.getLogger(OTPUtility.class);
	
	 @Autowired
	 private NotificationDAO notificationDAO;

	   
	    
	public OTPUtility() {}

	public Integer generateOTP(){
		return generateRandumNumber();
	}

	public Boolean isValidOTP(OTP otp, int responseOTP){
		boolean flag=false;
		  if(responseOTP==otp.getOtp())
		  {
			  Date currentDate = new Date();
			  long timeDifference=0;
			try {
				Date otpGeneratedDate = dateFormatter.parse(otp.getGeneratedOn().toString());
				timeDifference=((currentDate.getTime()-otpGeneratedDate.getTime())/1000);
				if(timeDifference>=0 && timeDifference<=(eXoTel.getOtpValidationBuffer()*60)){
					  flag=true;  
				  }
			} catch (ParseException e) {
				e.printStackTrace();
			}
			    
		  }
		return flag;
	}
	private Integer generateRandumNumber(){
		long nextLong = Math.abs(generator.nextLong());
		return Integer.parseInt(String.valueOf(nextLong).substring(0, eXoTel.getOtpLength()));
	}
	
}
