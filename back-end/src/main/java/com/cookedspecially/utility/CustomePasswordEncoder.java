package com.cookedspecially.utility;

import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

/**
 * @author Abhishek 
 *
 */
@SuppressWarnings("deprecation")
public class CustomePasswordEncoder extends org.springframework.security.authentication.encoding.ShaPasswordEncoder {
	
	private static String salt = "BITE MY SHINY METAL ASS!!";
    private static PasswordEncoder encoder=new ShaPasswordEncoder();
	
	public CustomePasswordEncoder() {
        super();
    }

    @Override
    public String encodePassword(String originalPassword, Object custome_salt) {  
        return encoder.encodePassword(originalPassword, salt);
    }

	public static String getSalt() {
		return salt;
	}
    
    
}
