package com.cookedspecially.dao;

import java.util.List;

import com.cookedspecially.domain.Notifier;
import com.cookedspecially.domain.Subscription;
import com.cookedspecially.enums.notification.Device;

public interface NotificationDAO {
	
 public void addAndUpdateNotifier(Notifier notifier);
 public Notifier getNotifier(int resturantId, Device device);
 public List<Notifier> getNotifier(int resturantId);
 public Notifier getNotifierById(int notifierId);
 public void deleteNotifier(int notifierId);

 
 public void addAndUpdateSubscriber(Subscription suscriber);
 public List<Subscription> getSubscriberList(List<Integer> customerId);
 public List<Subscription> getSubscriber(String mobileNo);
 public void deleteSubscriber(int subscriberId);
 public void deleteSubscriber(String mobileNo);
 public void deSubscriber(int customerId);
 public Subscription getSubscriber(int subscriberId);
 public void deleteNotifierR(int restaurantId);
 public List<Subscription> getSubscriberC(int customerId);
public Subscription isRegstered(int customerId, String token, Device deviceType);
public Notifier isNotifierRegistered(int restaurantId, Device device);
public void deSubscriberToken(String token);


 
 
}
