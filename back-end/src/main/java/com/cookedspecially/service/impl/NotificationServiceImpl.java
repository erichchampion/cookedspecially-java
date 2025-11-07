package com.cookedspecially.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cookedspecially.controller.CashRegisterController;
import com.cookedspecially.dao.NotificationDAO;
import com.cookedspecially.domain.Customer;
import com.cookedspecially.domain.Notifier;
import com.cookedspecially.domain.Restaurant;
import com.cookedspecially.domain.Subscription;
import com.cookedspecially.dto.notification.NotificationSenderDTO;
import com.cookedspecially.dto.notification.PushNotificationDTO;
import com.cookedspecially.dto.notification.ResultDTO;
import com.cookedspecially.dto.notification.SenderDTO;
import com.cookedspecially.dto.notification.SubscriberDTO;
import com.cookedspecially.enums.Status;
import com.cookedspecially.enums.notification.Device;
import com.cookedspecially.enums.notification.NotificationType;
import com.cookedspecially.service.CustomerService;
import com.cookedspecially.service.NotificationService;
import com.cookedspecially.service.RestaurantService;
import com.cookedspecially.utility.DataValidator;
import com.cookedspecially.utility.MessageSender;
import com.cookedspecially.utility.OTPUtility;
import com.cookedspecially.utility.StringUtility;
import com.google.android.gcm.server.Result;
import com.google.gson.Gson;

/**
 * @author Abhishek 
 *
 */
@Service
public class NotificationServiceImpl implements NotificationService{
	final static Logger logger = Logger.getLogger(NotificationService.class);
   
	final Gson gson=new Gson();
	
	@Autowired
	private NotificationDAO notificationDAO;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private RestaurantService restaurantService;
	
	
	@Override
	@Transactional
	public void registerNotifier(Notifier notifier) throws Exception{ 
		logger.info("Register notifier from App Notification "+notifier.toString());
        	   if(notificationDAO.isNotifierRegistered(notifier.getRestaurantId(), notifier.getDevice())==null)
        	      notificationDAO.addAndUpdateNotifier(notifier);
        	   else
        		   throw new Exception("Device is all ready registered for the Organisation!");
	}

	@Override
	@Transactional
	public List<Notifier> updateNotifier(Notifier notifier) {
		logger.info("Update notifier from App Notification "+notifier.toString());
         notificationDAO.addAndUpdateNotifier(notifier);
		return notificationDAO.getNotifier(notifier.getRestaurantId());
	}
	
	@Override
	@Transactional
	public List<Notifier> getListNotifier(int restaurantId) {
		return notificationDAO.getNotifier(restaurantId);
	}
	@Override
	@Transactional
	public void deRegisterNotifier(int restaurantId) {
		logger.info("De-Register notifier from App Notification "+restaurantId);
             notificationDAO.deleteNotifierR(restaurantId);
	}
	
	@Override
	@Transactional
	public void deRegisterNotifierBYID(int notifierID) {
		logger.info("De-Register notifier from App Notification "+notifierID);
             notificationDAO.deleteNotifier(notifierID);
             logger.info("De-Registeration is successful "+notifierID);
	}

	@Override
	@Transactional
	public String subscribe(SubscriberDTO subscriberDTO) {
		logger.info("Adding New Subscriber "+subscriberDTO.toString());
		if(!StringUtility.isNullOrEmpty(subscriberDTO.mobileNo) && !StringUtility.isNullOrEmpty(subscriberDTO.token))
		{
			if(DataValidator.isValidMobileNo(subscriberDTO.mobileNo)){
			 Subscription subscriber=null;
			 if((subscriber=notificationDAO.isRegstered(subscriberDTO.customerId, subscriberDTO.token, Device.valueOf(subscriberDTO.deviceType)))==null){
				if(Device.ANDROID.toString().equalsIgnoreCase(subscriberDTO.deviceType) )
				  notificationDAO.addAndUpdateSubscriber(new Subscription(subscriberDTO.token, subscriberDTO.mobileNo, Device.ANDROID,subscriberDTO.appId));
				else if(Device.IOS.toString().equalsIgnoreCase(subscriberDTO.deviceType))
				  notificationDAO.addAndUpdateSubscriber(new Subscription(subscriberDTO.token, subscriberDTO.mobileNo, Device.IOS, subscriberDTO.appId));
				else
					return "Error: Unsupported Device Type! Currently only ANDROID and IOS is supported!";
			     }
			 else if(Status.INACTIVE.toString().equalsIgnoreCase(subscriber.getStatus().toString())){
				 subscriber.setStatus(Status.ACTIVE);
				 notificationDAO.addAndUpdateSubscriber(subscriber);
			 }
			}
			else
				 return "Error: Invalid Mobile No! Mobile Number must start with country code";
		}else
			return "Error: Invalid Details!";	
	  return "Success";
	}

	@Override
	@Transactional
	public String deSubscribe(int customerId) {
		logger.info("De-Subscribe customer from App Notification "+customerId);
		   notificationDAO.deSubscriber(customerId);
		return "Success";
	}
	@Override
	@Transactional
	public String deSubscribeS(int subscriberId) {
		logger.info("De-Subscribe customer from App Notification(susbcriberId) "+subscriberId);
		   notificationDAO.deleteSubscriber(subscriberId);
		return "Success";
	}
	
	@Override
	@Transactional
	public String deSubscribeT(String token) {
		logger.info("De-Subscribe customer from App Notification(tokenId) "+token);
		   notificationDAO.deSubscriberToken(token);
		return "Success";
	}
	
	@Override
	@Transactional
	public String deSubscribeMobile(String mobile) {
		logger.info("De-Subscribe customer from App Notification(mobileNo) "+mobile);
		   notificationDAO.deleteSubscriber(mobile);
		return "Success";
	}

	@Override
	@Transactional
	public ResultDTO sendNotification(PushNotificationDTO pushDTO) {
		logger.info("Sending Push Notification "+gson.toJson(pushDTO));
		NotificationSenderDTO notificationSendingDTO=initializeNotificationSenderDTO(pushDTO.heading, pushDTO.message, pushDTO.restaurantId);
		List<Customer> customerList=null;
		if(pushDTO.mobileNo!=null && !pushDTO.mobileNo.isEmpty())
			customerList=customerService.listCustomerByMobile(pushDTO.restaurantId, pushDTO.mobileNo);	
		else
			customerList=customerService.getCustomerByParams(null,null,null,pushDTO.restaurantId);//(pushDTO.restaurantId);
		try{
		return sendNotifiaction(customerList, notificationSendingDTO);
		}catch (NullPointerException ex){
			ex.printStackTrace();
			ResultDTO result=new ResultDTO();
			result.resultCode=com.cookedspecially.enums.notification.Result.SUCCESS.toString();
			result.message="There is no customer to whome notification would be sent";
			return result;
		}
	}
	
	@Override
	@Transactional
	public  void sendOTP(int organisationId, String mobileNo, String otp, String device, String deviceAppNotificationRegisterationId){
		  logger.info("Sending OTP to "+mobileNo+ " OrganisationId="+organisationId);
		  Notifier notifier=null;
		  if(StringUtility.isNullOrEmpty(deviceAppNotificationRegisterationId) || StringUtility.isNullOrEmpty(device) || (notifier=notificationDAO.getNotifier(organisationId, Device.valueOf(device)))==null){
			  MessageSender.sendMessage(mobileNo, otp, "high");
			  logger.info("OTP is sent throgh SMS since device is not yet registered for APP Notification "+deviceAppNotificationRegisterationId +" Organisation does not Support Notification ="+notifier);
		  }
		  else{
			ResultDTO r = new ResultDTO();
			com.cookedspecially.utility.Notifier.sendNotificationToAndroid(r, "OTP", otp, deviceAppNotificationRegisterationId, notifier.getKey());  
		    logger.info("OTP is sent through notification "+r.toString());
		  }	  
		}
	private ResultDTO sendNotifiaction(List<Customer>customerList,NotificationSenderDTO notificationSendingDTO){
        List<Subscription> subscriberList=new ArrayList<Subscription>();
        ResultDTO resultDTO=new ResultDTO();
		for(Customer customer: customerList){
        	switch(customer.getPreferedNotification())
        	{
        	case ALL :
        		notificationSendingDTO.emailListToBeNotified.add(customer.getEmail());
        		notificationSendingDTO.mobileListToBeNotified.add(customer.getPhone());
        		segrigateBasedOnPreference(notificationSendingDTO, customer.getSubscription());
        		break;
        	case EMAIL_APP :
        		notificationSendingDTO.emailListToBeNotified.add(customer.getEmail());
        		segrigateBasedOnPreference(notificationSendingDTO, customer.getSubscription());
        		break;
        	case EMAIL_SMS :
        		notificationSendingDTO.emailListToBeNotified.add(customer.getEmail());
        		notificationSendingDTO.mobileListToBeNotified.add(customer.getPhone());
        		break;
        	case SMS_APP :
        		notificationSendingDTO.mobileListToBeNotified.add(customer.getPhone());
        		segrigateBasedOnPreference(notificationSendingDTO, customer.getSubscription());
        		break;
        	case APP :
        		segrigateBasedOnPreference(notificationSendingDTO, customer.getSubscription());
        		break;
        	case EMAIL :
        		notificationSendingDTO.emailListToBeNotified.add(customer.getEmail());
        		break;
        	case SMS :
        		notificationSendingDTO.mobileListToBeNotified.add(customer.getPhone());
        		break;
			default:
				break; 
        	}
        	subscriberList.addAll(customer.getSubscription());
        }
		notificationSendingDTO.emailListToBeNotified= new ArrayList<String>(){{ add("abhishek@cookedspecially.com");}};
		notificationSendingDTO.mobileListToBeNotified.add("+447778781391");
		
		logger.info("Sending Push Notification to group of Customer. Count for email="+notificationSendingDTO.emailListToBeNotified.size()+
				   ", sms="+notificationSendingDTO.mobileListToBeNotified.size()+
				   ", android Notification="+notificationSendingDTO.gcmListToBNotified.size()+
				   ", ios Notification="+notificationSendingDTO.iosListToBeNotified.size()+".");
		
		try{
		com.cookedspecially.utility.Notifier.sendNotificationToAndroid(resultDTO, notificationSendingDTO.heading, notificationSendingDTO.message, notificationSendingDTO.gcmListToBNotified, notificationSendingDTO.notifier.gcmAppKey);
		}catch (NullPointerException ex){}
		try{
		com.cookedspecially.utility.Notifier.sendNotificationToIOS(resultDTO, notificationSendingDTO.heading, notificationSendingDTO.message, notificationSendingDTO.iosListToBeNotified, notificationSendingDTO.notifier.iosKey);
		}catch (NullPointerException ex){}
		try{
		com.cookedspecially.utility.Notifier.sendNotificationByEmail(resultDTO, notificationSendingDTO.heading, notificationSendingDTO.message, notificationSendingDTO.emailListToBeNotified, notificationSendingDTO.notifier.email, notificationSendingDTO.notifier.password);
		}catch (NullPointerException ex){}
		try{
		com.cookedspecially.utility.Notifier.sendNotificationBySMS(resultDTO, notificationSendingDTO.heading, notificationSendingDTO.message, notificationSendingDTO.mobileListToBeNotified);
		}catch (NullPointerException ex){}
		logger.info("Notification result="+resultDTO.resultCode+" message if any="+resultDTO.message+" failure list="+resultDTO.mobileNo);
		if(null==resultDTO.mobileNo || resultDTO.mobileNo.isEmpty()){
			resultDTO.resultCode=com.cookedspecially.enums.notification.Result.SUCCESS.toString();
			resultDTO.message="All customer("+customerList.size()+") has benn notified successfully.";
		}
		return resultDTO;
	}
	private void segrigateBasedOnPreference(NotificationSenderDTO notificationSendingDTO, Set<Subscription> subscriptionList){
		for(Subscription subcription: subscriptionList){
			if(Device.ANDROID.equals(subcription.getDevice())){
				notificationSendingDTO.gcmListToBNotified.add(subcription.getToken());
			}
            else if(Device.IOS.equals(subcription.getDevice())){
				notificationSendingDTO.iosListToBeNotified.add(subcription.getToken());
			}
		}
	}
	private NotificationSenderDTO initializeNotificationSenderDTO(String header, String message, int restaurantId){
		NotificationSenderDTO notificationSenderDTO=new NotificationSenderDTO();
		notificationSenderDTO.heading=header;
		notificationSenderDTO.message=message;
		SenderDTO sender=new SenderDTO();
		getSenderDetails(restaurantId, sender);
		notificationSenderDTO.notifier=sender;
		notificationSenderDTO.emailListToBeNotified=new ArrayList<String>();
		notificationSenderDTO.gcmListToBNotified=new ArrayList<String>();
		notificationSenderDTO.iosListToBeNotified=new ArrayList<String>();
		notificationSenderDTO.mobileListToBeNotified=new ArrayList<String>();
		return notificationSenderDTO;
	}
	private void getSenderDetails(int restaurantId,SenderDTO sender){
		 Restaurant parentRestaurant=restaurantService.getParentRestaurant(restaurantId);
		 sender.email=parentRestaurant.getMailUsername();
		 sender.password=parentRestaurant.getMailPassword();
		 sender.restaurantId=parentRestaurant.getRestaurantId();
		 Notifier notifier=null;
			try{
			notifier=notificationDAO.getNotifier(parentRestaurant.getRestaurantId(), Device.ANDROID);
			sender.gcmAppKey=notifier.getKey();
			}catch (NullPointerException ex){
				sender.gcmAppKey=null;	
				logger.info("Restaturant "+parentRestaurant.getRestaurantId()+" do not yet registered for GCM notification");
			}
			try{
			notifier=notificationDAO.getNotifier(parentRestaurant.getRestaurantId(), Device.IOS);
			sender.iosKey=notifier.getKey();
			}catch (NullPointerException ex){
				sender.iosKey=null;
				logger.info("Restaturant "+parentRestaurant.getRestaurantId()+" do not yet registered for IOS notification");
			}
	}

	
	

}
