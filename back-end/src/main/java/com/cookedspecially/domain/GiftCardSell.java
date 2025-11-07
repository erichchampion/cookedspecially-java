package com.cookedspecially.domain;

import com.cookedspecially.utility.DateUtil;
import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Abhishek on 5/27/2017.
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "GIFT_CARD_SELL")
public class GiftCardSell implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "giftCardId")
    @JsonBackReference(value = "giftCardSold")
    public GiftCard giftCard;
    @Id
    @GeneratedValue(generator = "giftCardPK")
    @GenericGenerator(name = "giftCardPK", strategy = "foreign", parameters = @Parameter(name = "property", value = "giftCard"))
    @Column(name = "giftCardId")
    private String giftCardId;
    @Column(name = "soldOn")
    private Date soldOn;
    @Column(name = "invoiceId")
    private String invoiceId;
    @Column(name = "mobileNoOfRecipient")
    private String mobileNoOfRecipient;
    @Column(name = "emailIdOfRecipient")
    private String emailIdOfRecipient;
    @Column(name = "message")
    private String message;
    @Column(name = "mobileNoOfPurchaser")
    private String purchaserMobileNo;


    public GiftCardSell() {

    }

    public GiftCardSell(String phone, GiftCard giftCard, String invoiceId) {
        this.invoiceId = invoiceId;
        this.giftCard = giftCard;
        this.purchaserMobileNo = phone;
        this.soldOn = DateUtil.getCurrentTimestampInGMT();
    }

    public String getGiftCardId() {
        return giftCardId;
    }

    public void setGiftCardId(String giftCardId) {
        this.giftCardId = giftCardId;
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

    public GiftCard getGiftCardSold() {
        return giftCard;
    }

    public void setGiftCardSold(GiftCard giftCardSold) {
        this.giftCard = giftCardSold;
    }
}
