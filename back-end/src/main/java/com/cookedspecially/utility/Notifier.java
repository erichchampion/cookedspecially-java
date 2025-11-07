package com.cookedspecially.utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import com.cookedspecially.dto.notification.ResultDTO;
import com.google.android.gcm.server.InvalidRequestException;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import javapns.Push;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.notification.PushedNotification;
import javapns.notification.ResponsePacket;

/**
 * @author Abhishek 
 *
 */
public class Notifier {
	final static Logger logger = Logger.getLogger(Notifier.class);

	final static int retries = 3;
	 
	public static void sendNotificationToIOS(ResultDTO resultDTO,String heading,String message, List<String> devices, String key) {
		String fileName = "/"+key;
        List<PushedNotification> notifications = new ArrayList<PushedNotification>();
		try {
			notifications = Push.alert(message, new File(fileName.toString()), "", false, devices);
		} catch (CommunicationException | KeystoreException e) {
			logger.warn("Could not read Key file(P12) on given location "+e.getMessage());
		}
		catch (Exception e) {
			logger.warn("Could not sent IOS notifiaction "+e.getMessage());
		}
        for (PushedNotification notification : notifications) {
            if (notification.isSuccessful()) 
            {
                logger.debug("Successfuly notification has been sent");
            } 
            else 
            {
               // String invalidToken = notification.getDevice().getToken();
                Exception theProblem = notification.getException();
                logger.debug(theProblem.getMessage());
                ResponsePacket theErrorResponse = notification.getResponse();
                if (theErrorResponse != null) 
                {
                    logger.debug(theErrorResponse.getMessage());
                }
            }
        }
	}
        
	
    public static void  sendNotificationToAndroid(ResultDTO resultDTO, String alertMessage, String message, List<String> device, String app_key){
    	if(device!=null && device.size()>0){
    	try {	
			Sender sender = new Sender(app_key);
			Message notificationMessage = new Message.Builder().addData("title",alertMessage).addData("message", message).build();
		    MulticastResult multicastresult = sender.send(notificationMessage, device, retries);
    		for(Result result: multicastresult.getResults()){
    			logger.info("Notification send Respones ErrorCode="+result.getErrorCodeName()+" "+result.getFailedRegistrationIds());
    	    	if(!StringUtility.isNullOrEmpty(result.getErrorCodeName())){
    	    		resultDTO.mobileNo.addAll(result.getFailedRegistrationIds());
    	    	}
    	    }
			logger.info("Notification is successfully sent to all customer");	
		} catch (InvalidRequestException e) {
			logger.warn("Could not send notification to android. InvalidRequestException.");
		} catch (IOException e) {
			logger.warn("Could not send notification to android. IOException."+e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.warn("Could not send notification to android. IllegalArgumentException."+e.getMessage());
		} catch (Exception e) {
			logger.warn("Could not send notification to android. Exception."+e.getMessage());
		}
    	}else{
			logger.info("No customer is configured for Android Notification");
    	}
		
	}
    public static void  sendNotificationToAndroid(ResultDTO resultDTO, String alertMessage, String message, String device, String app_key){
    	if(StringUtility.isNullOrEmpty(device)){
    	try {	
			Sender sender = new Sender(app_key);
			Message notificationMessage = new Message.Builder().addData("title",alertMessage).addData("message", message).build();
		    Result result = sender.send(notificationMessage, device, retries);
    			logger.info("Notification send Respones ErrorCode="+result.getErrorCodeName()+" for device="+device);
    	    	if(!StringUtility.isNullOrEmpty(result.getErrorCodeName())){
    	    		resultDTO.mobileNo.addAll(result.getFailedRegistrationIds());
    	    	}
		} catch (InvalidRequestException e) {
			logger.warn("Could not send notification to android. InvalidRequestException.");
		} catch (IOException e) {
			logger.warn("Could not send notification to android. IOException."+e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.warn("Could not send notification to android. IllegalArgumentException."+e.getMessage());
		} catch (Exception e) {
			logger.warn("Could not send notification to android. Exception."+e.getMessage());
		}
    	}else{
			logger.info("No customer is configured for Android Notification");
    	}
		
	}
    public static void  sendNotificationBySMS(ResultDTO resultDTO, String alertMessage, String message, List<String> mobileNo){   
    	for(String mobile: mobileNo){
    	  if(DataValidator.isValidMobileNo(mobile) && MessageSender.sendMessage(mobile, message, "low"))
    		  continue;
    	  resultDTO.mobileNo.add(mobile);
      }
    }
    public static void  sendNotificationByEmail(ResultDTO resultDTO, String alertMessage, String message, List<String> emailList, String senderEmail, String password){
    	for(String email: emailList){
    		try{
    	  if(DataValidator.isValidEmail(email)){
  			MailerUtility.sendHTMLMail(email,alertMessage,message,senderEmail,password);
    	  }
    	  }catch (Exception e){
    		  logger.warn("Could not send mail. "+e.getMessage());
    	  }
      }
    }

}
