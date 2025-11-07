package com.cookedspecially.dto.notification;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Abhishek 
 *
 */
public class PushNotificationDTO {

 @NotNull
 @Min(value=1)
 public int restaurantId;
 
 public List<String> mobileNo;
 @NotNull
 @NotEmpty
 public String message;
 
 @NotNull
 @NotEmpty
 @Size(min=5, max=50)
 public String heading;
}
