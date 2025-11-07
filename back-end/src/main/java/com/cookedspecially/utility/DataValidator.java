package com.cookedspecially.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;

import com.cookedspecially.controller.CustomerController;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

/**
 * @author Abhishek 
 *
 */
public class DataValidator {
	    final static Logger logger = Logger.getLogger(CustomerController.class);
		private static PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance(); 
	
	public static boolean isValidMobileNo(String mobileNo){
		try {
			PhoneNumber numberProto = phoneUtil.parse(mobileNo, "");
			    return phoneUtil.isValidNumber(numberProto);
		} catch (Exception e) {
			logger.warn("Could not parse mobile no "+mobileNo);
			return false;
		}
	}
	public static String validateAndFormateMobileNo(String mobileNo) throws Exception{
		try {
			PhoneNumber	numberProto = phoneUtil.parse(mobileNo, ""); 
            if(phoneUtil.isValidNumber(numberProto)){
            	mobileNo=phoneUtil.format(numberProto, PhoneNumberFormat.E164);
            }
			} catch (Exception e) {
				throw new Exception(e.getMessage());
			}
		return mobileNo;
	}
	public static String formateMobileNo(String mobileNo) throws Exception{
		PhoneNumber numberProto =null;
		if(mobileNo.startsWith("91") && mobileNo.length()==12)
			mobileNo="+"+mobileNo;
		for(String cc: getCountryCode()){
        	try {
        		numberProto = phoneUtil.parse(mobileNo, cc); 
                if(phoneUtil.isValidNumber(numberProto)){
                 	return phoneUtil.format(numberProto, PhoneNumberFormat.E164);
                }
				} catch (Exception e) {
				}
        	
			}
        throw new Exception("Invalid Mobile No");
	}
	public static boolean isValidEmail(String email){	
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
			return true;
		} catch (AddressException e) {
			logger.warn("Invalid Email address "+email);
			return false;		}
	  }
	
	private static List<String> getCountryCode(){
		List<String> countryCodeList=new ArrayList<>();
		countryCodeList.add(0, "");
		countryCodeList.add(1, "IN");
		String[] locales = Locale.getISOCountries();
		for (String countryCode : locales) {
			Locale country = new Locale("", countryCode); 
			countryCodeList.add(country.getCountry());
		}
		return countryCodeList;
	}
}
