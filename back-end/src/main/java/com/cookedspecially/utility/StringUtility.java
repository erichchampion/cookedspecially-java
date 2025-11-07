/**
 * 
 */
package com.cookedspecially.utility;

import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

/**
 * @author sagarwal
 *
 */
public class StringUtility {

	public static boolean isNullOrEmpty(String str) {
		if (str == null || str.length() == 0) {
			return true;
		}
		return false;
	}
	
	public static String getErrorString(BindingResult bindingResult){
		StringBuilder errorString=new StringBuilder();
		
		 List<FieldError> errors = bindingResult.getFieldErrors();
		    
		for (Object object : bindingResult.getAllErrors()) {
		    if(object instanceof FieldError) {
		        FieldError fieldError = (FieldError) object;
		    }
		    if(object instanceof ObjectError) {
		        ObjectError objectError = (ObjectError) object;
		    }
		}
		return errorString.toString();
	}
	
}

