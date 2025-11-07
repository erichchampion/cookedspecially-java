package com.cookedspecially.service;

import java.util.List;

import com.cookedspecially.domain.Notifier;
import com.cookedspecially.dto.notification.PushNotificationDTO;
import com.cookedspecially.dto.notification.ResultDTO;
import com.cookedspecially.dto.notification.SubscriberDTO;
/**
 * @author Abhishek 
 *
 */
public interface NotificationService {

/***********************   NOTIFIER   *******************/
 public void registerNotifier(Notifier notifier) throws Exception;
 public List<Notifier> updateNotifier(Notifier notifier);
 public List<Notifier> getListNotifier(int restaurantId);
 public void deRegisterNotifier(int restaurantId);
 public void deRegisterNotifierBYID(int notifierId);
 
 /***********************   SUBSCRIBER    *******************/
 public String subscribe(SubscriberDTO subscriberDTO);
 public String deSubscribe(int customerId);
 public ResultDTO sendNotification(PushNotificationDTO pushDTO);
 //public List<Subscriber> listSubscriber(int restaurantId);
 public String deSubscribeS(int parseInt);
 public String deSubscribeT(String token);
 public String deSubscribeMobile(String mobile);
void sendOTP(int organisationId, String mobileNo, String otp, String device,
		String deviceAppNotificationRegisterationId);


}
