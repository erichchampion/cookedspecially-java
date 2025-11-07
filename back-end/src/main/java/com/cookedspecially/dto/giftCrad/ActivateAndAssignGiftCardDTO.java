package com.cookedspecially.dto.giftCrad;

import com.cookedspecially.validator.Email;
import com.cookedspecially.validator.Phone;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by Abhishek on 5/28/2017.
 */
public class ActivateAndAssignGiftCardDTO {


    @Min(16)
    @Max(16)
    public String giftCardNo;

    @Phone(message = "Invalid mobile no! Please enter mobile no followed by country code.")
    public String mobileNoOfPurchaser;

    @NotNull
    @Phone(message = "Invalid mobile no! Please enter mobile no followed by country code.")
    public String mobileNoOfRecipient;

    @NotNull
    @Email(message = "Invalid EmailId! Please enter valid emailId.")
    public String emailNoOfRecipient;

    public String message;

    @NotNull
    @Min(1)
    public float amount;

    @NotNull
    @Min(3)
    public String paymentMode;

    @NotNull
    @Min(3)
    public String paymentStatus;


}
