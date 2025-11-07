package com.cookedspecially.dto.notification;

import java.util.List;

/**
 * @author Abhishek 
 *
 */
public class NotificationSenderDTO {
public List<String> emailListToBeNotified;
public List<String> gcmListToBNotified;
public List<String> mobileListToBeNotified;
public List<String> iosListToBeNotified;
public SenderDTO notifier;
public String heading;
public String message;

}
