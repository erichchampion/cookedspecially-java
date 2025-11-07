package com.cookedspecially;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class OTPAuthenticationProvider implements AuthenticationProvider {
	final static Logger logger = Logger.getLogger(OTPAuthenticationProvider.class);
	final String ROLE = "ROLE_CLIENT";
	final List<SimpleGrantedAuthority> CLIENT_AUTHORITIES = Arrays.asList(new SimpleGrantedAuthority(ROLE));

	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
	    	String userName = authentication.getName().trim();
	        String password = authentication.getCredentials().toString().trim();
	        Authentication auth = null;
	        logger.info(userName);
	        if (userName.equals("Abhi")){
	        	User user= new User(userName,password, true, true, true, true, CLIENT_AUTHORITIES);
	        	auth = new UsernamePasswordAuthenticationToken(user, password, CLIENT_AUTHORITIES);
	        	return auth;
	        }
	        else 
	        	return null;
	}
	
	@Override
	public boolean supports(Class<? extends Object> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}
}
