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
@Table(name = "GIFT_CARD_REDEMPTION")
public class GiftCardRedemption implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "giftCardId")
    @JsonBackReference(value = "giftCardRedeemed")
    public GiftCard giftCard;
    @Id
    @GeneratedValue(generator = "giftCardPK")
    @GenericGenerator(name = "giftCardPK", strategy = "foreign", parameters = @Parameter(name = "property", value = "giftCard"))
    @Column(name = "giftCardId")
    private String giftCardId;
    @Column(name = "redeemedOn")
    private Date redeemedOn;
    @Column(name = "redeemedByCustomerId")
    private Integer redeemedByCustomerId;
    @Column(name = "redeemedByUserId")
    private Integer redeemedByUserId;

    public GiftCardRedemption(GiftCard giftCard, Integer customerId, Integer userId) {
        this.redeemedByCustomerId = customerId;
        this.redeemedByUserId = userId;
        this.redeemedOn = DateUtil.getCurrentTimestampInGMT();
        this.giftCard = giftCard;
    }

    public GiftCardRedemption() {
    }

    public String getGiftCardId() {
        return giftCardId;
    }

    public void setGiftCardId(String giftCardId) {
        this.giftCardId = giftCardId;
    }

    public Date getRedeemedOn() {
        return redeemedOn;
    }

    public void setRedeemedOn(Date redeemedOn) {
        this.redeemedOn = redeemedOn;
    }

    public Integer getRedeemedByCustomerId() {
        return redeemedByCustomerId;
    }

    public void setRedeemedByCustomerId(Integer redeemedByCustomerId) {
        this.redeemedByCustomerId = redeemedByCustomerId;
    }

    public Integer getRedeemedByUserId() {
        return redeemedByUserId;
    }

    public void setRedeemedByUserId(Integer redeemedByUserId) {
        this.redeemedByUserId = redeemedByUserId;
    }

    public GiftCard getGiftCard() {
        return giftCard;
    }

    public void setGiftCard(GiftCard giftCardRedeemed) {
        this.giftCard = giftCardRedeemed;
    }
}
