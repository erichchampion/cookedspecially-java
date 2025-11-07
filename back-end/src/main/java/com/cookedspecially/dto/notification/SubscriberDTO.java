package com.cookedspecially.dto.notification;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import com.cookedspecially.validator.Phone;

/**
 * @author Abhishek 
 *
 */
public class SubscriberDTO {

 @Phone
 public String mobileNo;
 @Size(min=18)
 public String token;
 @Size(min=2)
 public String deviceType;
 @Min(1)
 public int customerId;
 @Size(min=5)
 public String appId;
}
