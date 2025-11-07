package com.cookedspecially.dao;

import com.cookedspecially.domain.GiftCard;
import com.cookedspecially.domain.GiftCardSell;
import com.cookedspecially.dto.giftCrad.CustomerCreditAccountOpenINFODTO;

import java.util.Date;
import java.util.List;

/**
 * Created by Abhishek on 5/27/2017.
 */
public interface GiftCardDAO {

    public void saveOrUpdateGiftCard(GiftCard giftCard);

    public void saveOrUpdateGiftCard(List<GiftCard> giftCard);

    public List<GiftCard> listGiftCard(Date fromDateGMT, Date toDateGMT, String status, int orgId);

    public GiftCard getGiftCard(String giftCardNo);

    public void saveOrUpdateGiftCardSell(GiftCardSell giftCardSell);

    List<GiftCard> listGiftCardOfCustomer(int customerId, String filter);

    void deleteGiftCard(String giftCardId);

    CustomerCreditAccountOpenINFODTO fetchDetailsRequiredToOpenDefaultCreditAccount(Integer id, Integer customerId, String name);

    void clearTestData();

    void rollBack(GiftCard giftCard);
}
