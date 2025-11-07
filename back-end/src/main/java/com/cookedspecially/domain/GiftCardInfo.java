package com.cookedspecially.domain;

import java.util.Date;

/**
 * Created by Abhishek on 5/30/2017.
 */
public class GiftCardInfo {

    private String giftCardId;
    private int expiryDayCount;
    private float amount;
    private String category;
    private Date createdOn;
    private String status;
    private String formattedGiftCardId;
    private Date redeemedOn;
    private int redeemedByCustomerId;
    private int redeemedByUserId;
    private Date soldOn;
    private String invoiceId;
    private String mobileNoOfRecipient;
    private String emailIdOfRecipient;
    private String message;
    private String purchaserMobileNo;
    private String paymentMode;
    private String paymentStatus;


    public String getGiftCardId() {
        return giftCardId;
    }

    public void setGiftCardId(String giftCardId) {
        this.giftCardId = giftCardId;
    }

    public int getExpiryDayCount() {
        return expiryDayCount;
    }

    public void setExpiryDayCount(int expiryDayCount) {
        this.expiryDayCount = expiryDayCount;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFormattedGiftCardId() {
        return formattedGiftCardId;
    }

    public void setFormattedGiftCardId(String formattedGiftCardId) {
        this.formattedGiftCardId = formattedGiftCardId;
    }

    public Date getRedeemedOn() {
        return redeemedOn;
    }

    public void setRedeemedOn(Date redeemedOn) {
        this.redeemedOn = redeemedOn;
    }

    public int getRedeemedByCustomerId() {
        return redeemedByCustomerId;
    }

    public void setRedeemedByCustomerId(int redeemedByCustomerId) {
        this.redeemedByCustomerId = redeemedByCustomerId;
    }

    public int getRedeemedByUserId() {
        return redeemedByUserId;
    }

    public void setRedeemedByUserId(int redeemedByUserId) {
        this.redeemedByUserId = redeemedByUserId;
    }

    public Date getSoldOn() {
        return soldOn;
    }

    public void setSoldOn(Date soldOn) {
        this.soldOn = soldOn;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getMobileNoOfRecipient() {
        return mobileNoOfRecipient;
    }

    public void setMobileNoOfRecipient(String mobileNoOfRecipient) {
        this.mobileNoOfRecipient = mobileNoOfRecipient;
    }

    public String getEmailIdOfRecipient() {
        return emailIdOfRecipient;
    }

    public void setEmailIdOfRecipient(String emailIdOfRecipient) {
        this.emailIdOfRecipient = emailIdOfRecipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPurchaserMobileNo() {
        return purchaserMobileNo;
    }

    public void setPurchaserMobileNo(String purchaserMobileNo) {
        this.purchaserMobileNo = purchaserMobileNo;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }


}
