package com.cookedspecially.dto.giftCrad;

import java.util.Date;

/**
 * Created by Abhishek on 5/25/2017.
 */
public class GiftCardSellDTO extends GiftCardDTO {

    public Date soldOn;
    public String invoiceId;
    public String mobileNoOfRecipient;
    public String emailIdOfRecipient;
    public String message;
    public String purchaserMobileNo;
    public String paymentMode;
    public String paymentStatus;
}
