package com.cookedspecially.eXoTel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.codec.binary.Base64;


/**
 * @author Abhishek 
 *
 */
public class ExOTel {
	
   private String sid;
   private String token;
   private String authStr;
   private String url;
   private byte[] authEncBytes;
   private String authStringEnc;
   private Integer otpValidationBuffer;
   private Integer otpLength;
   
   
   
public ExOTel() {
	super();
	Properties prop = new Properties();
	String propFileName = "eXoTel.properties";
	InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
	if (inputStream != null) {
		try {
			prop.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	} 
   this.sid = prop.getProperty("sid");
   this.token = prop.getProperty("token");
   this.authStr=sid + ":" +token;
   this.url="https://"+ authStr + "@twilix.exotel.in/v1/Accounts/"+ sid + "/Sms/send";
   this.authEncBytes=Base64.encodeBase64(authStr.getBytes());
   this.authStringEnc=new String(authEncBytes);
   this.otpValidationBuffer=Integer.parseInt(prop.getProperty("otpValidationTime"));
   this.otpLength=Integer.parseInt(prop.getProperty("otpLength"));
}
public String getSid() {
	return sid;
}
public void setSid(String sid) {
	this.sid = sid;
}
public String getToken() {
	return token;
}
public void setToken(String token) {
	this.token = token;
}
public String getAuthStr() {
	return authStr;
}
public void setAuthStr(String authStr) {
	this.authStr = authStr;
}
public String getUrl() {
	return url;
}
public void setUrl(String url) {
	this.url = url;
}
public byte[] getAuthEncBytes() {
	return authEncBytes;
}
public String getAuthStringEnc() {
	return authStringEnc;
}
public Integer getOtpValidationBuffer() {
	return otpValidationBuffer;
}
public void setOtpValidationBuffer(Integer otpValidationBuffer) {
	this.otpValidationBuffer = otpValidationBuffer;
}
public Integer getOtpLength() {
	return otpLength;
}
public void setOtpLength(Integer otpLength) {
	this.otpLength = otpLength;
}
public void setAuthEncBytes(byte[] authEncBytes) {
	this.authEncBytes = authEncBytes;
}
public void setAuthStringEnc(String authStringEnc) {
	this.authStringEnc = authStringEnc;
}
   
}
