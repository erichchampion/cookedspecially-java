package com.cookedspecially.enums.notification;

import javax.naming.directory.InvalidAttributesException;

/**
 * @author Abhishek 
 *
 */
public enum NotificationType {
	
	EMAIL(1), SMS(2), EMAIL_SMS(3), APP(4), EMAIL_APP(5), SMS_APP(6), ALL(7);
	
    private int value;

	private NotificationType(int value) {
		this.value = value;
	}
	public static NotificationType getEnumName(int value) throws InvalidAttributesException {
		  for(NotificationType e: NotificationType.values()) {
		    if(e.value == value) {
		      return e;
		    }
		  }
		  throw new InvalidAttributesException("Enum not defined for given value="+value);// not found
		}
	
}
