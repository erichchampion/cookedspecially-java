package com.cookedspecially.dto.giftCrad;

import com.cookedspecially.validator.Phone;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by Abhishek on 5/25/2017.
 */
public class BuyGiftCardDTO {

    @Min(0)
    public float amount;

    @Phone(message = "Please provide valid mobile no with country code")
    public String mobileNoOfRecipient;

    @NotNull
    @Min(2)
    public String category;

    public String message;

    @Email
    public String emailIdOfRecipient;

    public int purchaserCustomerId;

    public String paymentMode;

    public String paymentStatus = "UNPAID";

    public String invoiceId;

}
