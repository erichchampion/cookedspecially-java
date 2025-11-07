package com.cookedspecially.utility;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.cookedspecially.controller.CustomerController;
import com.cookedspecially.eXoTel.ExOTel;

/**
 * @author Abhishek 
 *
 */
@SuppressWarnings("deprecation")
public class MessageSender {
	final static Logger logger = Logger.getLogger(MessageSender.class);

	public static Boolean sendMessage(String mobileNumber, String content, String priority){
		Integer response=sendRequest(createPostRequest(getPostParameters(mobileNumber, content, priority)));
		logger.info("Response for SMS sent to "+mobileNumber+" is "+response);
		if(response>=200 & response < 300)
		   return true;	
        return false;
	}
	
	public static String sendTestMessage(String mobileNumber, String content, String priority){
		String responseString=null; 
		try {
			    @SuppressWarnings("resource")
				HttpClient client = new DefaultHttpClient();
	            HttpResponse response = client.execute(createPostRequest(getPostParameters(mobileNumber, content, priority)));
	            HttpEntity entity = (HttpEntity) response.getEntity();
	            if (entity != null) {
	            	responseString=EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
	             }
	            responseString=responseString+"\n\n Status on SMS sent and Response code="+response.getStatusLine().getStatusCode();
	            logger.info("Status on SMS sent to using exotel is "+entity+" and Response code="+response.getStatusLine().getStatusCode());
	        } catch (Exception e) {
	            logger.info("Could not send OTP. Exception="+e);
	            responseString="Could not send OTP. Exception="+e;
		 }
		return responseString;
	}

	private static Integer sendRequest(HttpPost post) {
		int httpStatusCode=400;
		 try {
			    HttpClient client = new DefaultHttpClient();
	            HttpResponse response = client.execute(post);
	            HttpEntity entity = (HttpEntity) response.getEntity();
//	            if (entity != null) {
//	                 String retSrc = EntityUtils.toString(entity); 
//	                 JSONObject result = new JSONObject(retSrc);
//	                 JSONArray tokenList = result.getJSONArray("names");
//	                 JSONObject oj = tokenList.getJSONObject(0);
//	                 String token = oj.getString("name"); 
//	             }
	            logger.info("Status on SMS sent to using exotel is "+entity.toString());
	            httpStatusCode = response.getStatusLine().getStatusCode();
	        } catch (Exception e) {
			 e.printStackTrace();
		 }
		return httpStatusCode;	
	}
	
	private static HttpPost createPostRequest(ArrayList<NameValuePair> postParameters){
		ExOTel eXoTel=new ExOTel();
        HttpPost post = new HttpPost(eXoTel.getUrl());
        post.setHeader("Authorization", "Basic " + eXoTel.getAuthStringEnc());
        try {
            post.setEntity(new UrlEncodedFormEntity(postParameters));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
		return post;
	}
	
	private static ArrayList<NameValuePair> getPostParameters(String mobileNumber, String content, String priority){
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        //postParameters.add(new BasicNameValuePair("From", "9999069200"));
        postParameters.add(new BasicNameValuePair("To", mobileNumber)); 
        postParameters.add(new BasicNameValuePair("Priority", priority));
        String out = null;
		try {
			out = new String(content.getBytes("UTF-8"), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        postParameters.add(new BasicNameValuePair("Body", out));
		return postParameters;    
	}
}
