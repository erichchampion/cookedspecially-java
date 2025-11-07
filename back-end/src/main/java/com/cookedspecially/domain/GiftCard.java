package com.cookedspecially.domain;

import com.cookedspecially.enums.giftCard.GiftCardStatus;
import com.cookedspecially.utility.DateUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * Created by Abhishek on 4/4/2017.
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "GIFT_CARD")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GiftCard implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "giftCardIdGenerator", strategy = "com.cookedspecially.utility.GiftCardIdGenerator")
    @GeneratedValue(generator = "giftCardIdGenerator")
    private String giftCardId;

    @Column(name = "amount")
    private float amount;

    @Column(name = "createdOn")
    private Date createdOn;

    @Column(name = "orgId")
    private int orgId;

    @Column(name = "status")
    private String status;

    @Column(name = "expiryDayCount")
    private int expiryDayCount;

    @Column(name = "category")
    private String category;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "giftCard")
    @JsonManagedReference(value = "giftCardSold")
    private GiftCardSell giftCardSold;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "giftCard")
    @JsonManagedReference(value = "giftCardRedeemed")
    private GiftCardRedemption giftCardRedemption;

    public GiftCard() {
    }

    public GiftCard(float amount, String category, int expireAfterDays, GiftCardStatus status, Integer orgId) {
        this.amount = amount;
        this.category = category;
        this.expiryDayCount = expireAfterDays;
        this.createdOn = DateUtil.getCurrentTimestampInGMT();
        this.status = status.name();
        this.orgId = orgId;
    }

    public String getGiftCardId() {
        return giftCardId;
    }

    public void setGiftCardId(String giftCardId) {
        this.giftCardId = giftCardId;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getExpiryDayCount() {
        return expiryDayCount;
    }

    public void setExpiryDayCount(int expiryDayCount) {
        this.expiryDayCount = expiryDayCount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public GiftCardSell getGiftCardSold() {
        return giftCardSold;
    }

    public void setGiftCardSold(GiftCardSell giftCardSell) {
        this.giftCardSold = giftCardSell;
    }

    public GiftCardRedemption getGiftCardRedemption() {
        return giftCardRedemption;
    }

    public void setGiftCardRedemption(GiftCardRedemption giftCardRedemption) {
        this.giftCardRedemption = giftCardRedemption;
    }
}
