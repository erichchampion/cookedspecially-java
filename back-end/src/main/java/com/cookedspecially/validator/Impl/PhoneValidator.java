package com.cookedspecially.validator.Impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.cookedspecially.utility.DataValidator;
import com.cookedspecially.validator.Phone;

public class PhoneValidator implements ConstraintValidator<Phone, String> {
	 
    @Override
    public void initialize(Phone phone) { }
 
    @Override
    public boolean isValid(String phoneNo, ConstraintValidatorContext cxt) {
    	if(phoneNo == null) {
            return false;
        }
        return DataValidator.isValidMobileNo(phoneNo);
    }
 
}
