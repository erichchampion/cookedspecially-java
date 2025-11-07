package com.cookedspecially.validator.Impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


import com.cookedspecially.validator.Email;


public class EmailValidator implements ConstraintValidator<Email, String> {
	
	private Pattern pattern;
	private Matcher matcher;

	private static final String EMAIL_PATTERN ="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	public EmailValidator() {
		pattern = Pattern.compile(EMAIL_PATTERN);
	}
	
	
    @Override
    public void initialize(Email email) { }
 
    @Override
    public boolean isValid(String email, ConstraintValidatorContext cxt) {
        if(email == null) {
            return false;
        }
        matcher = pattern.matcher(email);
		return matcher.matches();
    }
 
}