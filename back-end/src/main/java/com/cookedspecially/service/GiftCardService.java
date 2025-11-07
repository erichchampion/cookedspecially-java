package com.cookedspecially.service;

import com.cookedspecially.dto.ResponseDTO;
import com.cookedspecially.dto.giftCrad.GiftCardDTO;
import com.cookedspecially.dto.giftCrad.GiftCardInfoDTO;

import java.util.List;

/**
 * Created by Abhishek on 4/4/2017.
 */
public interface GiftCardService {

    List<GiftCardDTO> createGiftCardForPrint(float amount, String category, int expireAfterDays, int noOfCard, Integer userId, Integer orgId) throws Exception;

    List<GiftCardInfoDTO> listGiftCard(int orgId, String fromDate, String toDate, String inputDateTimeZone, String status, int userId) throws Exception;

    ResponseDTO loadMoneyAndActivate(int organisationId, int userId, String giftCardId, float amount, Integer customerId, String invoiceId, String message);

    List<GiftCardInfoDTO> listGiftCardOfCustomer(int customerId, String filter);

    ResponseDTO redeemGiftCard(String giftCardNo, int customerId, Integer userId);

    ResponseDTO deactivateGiftCard(String giftCardNo, int organisationId, int userId);

    GiftCardInfoDTO listGiftCardInfo(String giftCardId);

    ResponseDTO restoreGiftCard(String giftCardNo, int organisationId, int userId);



    /*public ResponseDTO activateGiftCardAndAssignToCustomer(String giftCardNo, float amount, String mobileNoOfRecipient, String emailNoOfRecipient,
                                                           String mobileNoOfPurchaser, String message, String paymentMode, int orgId, int userId, String paymentStatus);


    public ResponseDTO activateGiftCardAssignedToCustomer(String giftCardNo, int organisationId, int userId);

    public ResponseDTO redeemGiftCardByCustomer(String giftCardNo, int customerId);

    public ResponseDTO redeemGiftCardByManagement(String giftCardNo, int organisationId, int userId);

    void deleteTestGiftCard(List<GiftCardDTO> giftCardCreatedWhileTest);

    ResponseDTO buyGiftCard(float amount, String category, String emailIdOfRecipient, String message, String mobileNoOfRecipient, String paymentMode, String paymentStatus, int purchaserCustomerId);

    ResponseDTO createGiftCardAndAssignToCustomer(int organisationId, int userId, float amount, String category, String emailIdOfRecipient, String message, String mobileNoOfRecipient, int purchaserCustomerId, String paymentMode, String paymentStatus, String invoiceId);
*/
    void clearTestData();

	ResponseDTO createAndActivateGiftCard(float amount, String category, String msg, String invoiceId,
			int expireAfterDayCount, int userId, int organisationId, int userId2);



}
