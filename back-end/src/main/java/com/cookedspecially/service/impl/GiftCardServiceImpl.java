package com.cookedspecially.service.impl;

import com.cookedspecially.dao.GiftCardDAO;
import com.cookedspecially.domain.*;
import com.cookedspecially.dto.ResponseDTO;
import com.cookedspecially.dto.credit.CustomerCreditDTO;
import com.cookedspecially.dto.giftCrad.CustomerCreditAccountOpenINFODTO;
import com.cookedspecially.dto.giftCrad.GiftCardDTO;
import com.cookedspecially.dto.giftCrad.GiftCardInfoDTO;
import com.cookedspecially.enums.check.Status;
import com.cookedspecially.enums.credit.BilligCycle;
import com.cookedspecially.enums.giftCard.GiftCardStatus;
import com.cookedspecially.enums.till.TransactionCategory;
import com.cookedspecially.service.*;
import com.cookedspecially.utility.DateUtil;
import com.cookedspecially.utility.DtoUtility;
import com.cookedspecially.utility.GiftCardIdGenerator;
import com.cookedspecially.utility.StringUtility;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Abhishek on 4/4/2017.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GiftCardServiceImpl implements GiftCardService {


    final static Logger logger = Logger.getLogger(GiftCardServiceImpl.class);

    private final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    //private final String DEFAULT_DATE_FORMAT_1 = "yyyy-MM-dd";

    //private final List<String> ROLE_LIST_GE_CCA = new ArrayList<String>(Arrays.asList("admin", "restaurantManager", "fulfillmentCenterManager", "deliveryManager", "Call_Center_Associate"));

    @Autowired
    private GiftCardDAO giftCardDAO;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private CheckService checkService;


    @Autowired
    @Qualifier("customerCreditAutomatedBilling")
    private CustomerCreditService customerCreditService;


    @Override
    public List<GiftCardDTO> createGiftCardForPrint(float amount, String category, int expireAfterDays, int noOfCard, Integer userId, Integer orgId) throws Exception {
        logger.info("------------------------------------createGiftCardForPrint amount = " + amount + ", category = " + category + " expireAfterDays = " + expireAfterDays + " noOfCard = " + noOfCard + "UserId =" + userId + " orgId=" + orgId + "------------------------------------");
        List<GiftCardDTO> createdGiftCardDTOList = new ArrayList<>();
        User user = userService.getUser(userId);
        if (user.getOrgId() != orgId) {
            throw new Exception("Mismatch organisationId!");
        }
        if (!(userService.hasRole("admin") || userService.hasRole("restaurantManager"))) {
            throw new Exception("Invalid Role, Only ADMIN AND RESTAURANT MANAGER is authorised for this service request!");
        }
        while (noOfCard > 0) {
            GiftCard giftCard = new GiftCard(amount, category, expireAfterDays, GiftCardStatus.PRINT, orgId);
            logger.info(giftCard.getGiftCardId());
            noOfCard -= 1;
            giftCardDAO.saveOrUpdateGiftCard(giftCard);
            logger.info("Gift Card has been generated successfully with Id = " + giftCard.getGiftCardId());
            GiftCardDTO giftCardDTO = (GiftCardDTO) DtoUtility.convertToDto(giftCard, GiftCardDTO.class);
            giftCardDTO.formattedGiftCardId = GiftCardIdGenerator.formateGiftCardId(giftCard.getGiftCardId());
            createdGiftCardDTOList.add(giftCardDTO);
        }
        logger.info("------------------------------------------------------------------------------------------------------------");
        return createdGiftCardDTOList;
    }

    @Override
    public List<GiftCardInfoDTO> listGiftCard(int orgId, String fromDate, String toDate, String inputDateTimeZone, String status, int userId) throws Exception {
        logger.info("listGiftCard ../ orgId= " + orgId + " userId=" + userId + " timeZone = " + inputDateTimeZone + " fromDate = " + fromDate + " toDate=" + toDate);
        if (status != null) {
            try {
                GiftCardStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                logger.info("Invalid Status Passed!!!. Status can be one among : " + java.util.Arrays.asList(GiftCardStatus.values()));
                throw new Exception("Invalid Status Passed!!!. Status can be one among : " + java.util.Arrays.asList(GiftCardStatus.values()));
            }
        }

        Date fromDateGMT = DateUtil.addToDate(DateUtil.getCurrentTimestampInGMT(), -30, 0, 0, "GMT");
        Date toDateGMT = DateUtil.getCurrentTimestampInGMT();

        if (fromDate != null)
            fromDateGMT = DateUtil.convertDateStringIntoTimeStampGMT(fromDate + " 00:00:00", DEFAULT_DATE_FORMAT, inputDateTimeZone);
        if (toDate != null)
            toDateGMT = DateUtil.convertDateStringIntoTimeStampGMT(toDate + " 23:59:59", DEFAULT_DATE_FORMAT, inputDateTimeZone);

        logger.info("From Date  =  " + fromDateGMT + ",    toDate  =  " + toDateGMT);

        List<GiftCardInfoDTO> convertedObjList = new ArrayList<>();
        for (GiftCard giftCard : giftCardDAO.listGiftCard(fromDateGMT, toDateGMT, status, orgId)) {
            GiftCardInfo giftCardInfo = new GiftCardInfo();
            DtoUtility.copyEntity(giftCard, giftCardInfo);
            DtoUtility.copyEntity(giftCard.getGiftCardSold(), giftCardInfo);
            DtoUtility.copyEntity(giftCard.getGiftCardRedemption(), giftCardInfo);
            GiftCardInfoDTO dto = (GiftCardInfoDTO) DtoUtility.convertToDto(giftCardInfo, GiftCardInfoDTO.class);
            dto.formattedGiftCardId = GiftCardIdGenerator.formateGiftCardId(dto.giftCardId);
            convertedObjList.add(dto);
        }
        logger.info("List====" + convertedObjList.size());
        return convertedObjList;
    }


    @Override
    public ResponseDTO loadMoneyAndActivate(int organisationId, int userId, String giftCardId, float amount, Integer customerId, String invoiceId, String message) {
        logger.info("--------------loadMoneyAndActivate     START---------");
        logger.info(" organisationId=" + organisationId + ", userId=" + userId + ", giftCardId=" + giftCardId + ", amount=" + amount + ", customerId=" + customerId + ", invoiceId=" + invoiceId);
        ResponseDTO responseDTO = new ResponseDTO();

        Customer customer = null;
        if (StringUtility.isNullOrEmpty(invoiceId)) {
            responseDTO.message = "InvoiceId can not be Null or Empty!";
            return responseDTO;
        }
        //Get Invoice And Check Status 
        List<Check> checkList = checkService.getCheckByInvoiceId(invoiceId);
        if(checkList.isEmpty()){
        	responseDTO.message = "InvoiceId does not exist!";
    	    return responseDTO;
    	}
        logger.info(checkList.size());
        for(Check check: checkList){
        	logger.info(check.getStatus().equals(Status.Cancel.name().toString()));
        	if(check.getStatus().equals(Status.Cancel))
        	{
        		responseDTO.message = "Invalid Invoice Status!";
            	return responseDTO;
        	}
            logger.info("CheckIs="+check.getCheckId()+": Status="+check.getStatus());
        }
        
        if (customerId != null)
            customer = customerService.getCustomer(customerId);
        else {
            customer = customerService.getCustomerFronInvoiceId(invoiceId);
            logger.info("customer="+customer);
        }
        
        GiftCard giftCard = giftCardDAO.getGiftCard(giftCardId);
        if(customer == null)
        	responseDTO.message = "Cound not find Associated Customer in Invoice, Please contact support team!";
        else if (!giftCard.getStatus().equalsIgnoreCase(GiftCardStatus.PRINT.name()))
            responseDTO.message = "Invalid Gift Card. Gift Card status must be " + GiftCardStatus.PRINT.name() + " but found " + giftCard.getStatus();
        else if (giftCard.getOrgId() != organisationId)
            responseDTO.message = "Conflict in Organisation Associated with Gift Card and employee!";
        else if (customer.getOrgId() != organisationId)
            responseDTO.message = "Conflict in Organisation Associated with Gift Card and Customer!" + organisationId + "  " + customer.getOrgId();
        else {
            giftCard.setGiftCardSold(new GiftCardSell(customer.getPhone(), giftCard, invoiceId));
            giftCard.setStatus(GiftCardStatus.ACTIVE.name());
            giftCard.setAmount(amount);
            giftCard.getGiftCardSold().setMessage(message);
            giftCardDAO.saveOrUpdateGiftCard(giftCard);
            responseDTO.result = "SUCCESS";
            responseDTO.message = "Amount has been added to gift card and activated Successfully.";
        }
        logger.info("Result =" + responseDTO.result + ", message=" + responseDTO.message);
        logger.info("================loadMoneyAndActivate   DONE===========");
        return responseDTO;
    }

    @Override
    public ResponseDTO redeemGiftCard(String giftCardNo, int customerId, Integer userId) {
        logger.info("--------------redeemGiftCard     START---------");
        logger.info("giftCardId=" + giftCardNo + " customerId=" + customerId + " userId=" + userId);
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.message="Failed to reddem Gift Card, Please try again!";
        Date currentDate=DateUtil.getCurrentTimestampInGMT();
        long diffInDays=0;
        GiftCard giftCard =null;
        try{
        giftCard = giftCardDAO.getGiftCard(giftCardNo);
        diffInDays=(currentDate.getTime()- giftCard.getCreatedOn().getTime())/(1000 * 60 * 60 * 24);
        if(giftCard.getExpiryDayCount()<=diffInDays){
        	responseDTO.message = "Gift Card has been expired!";
        	return responseDTO;
        }
        }catch(Exception e){
        	responseDTO.message = "Invalid GiftCardId! Could not find gift Card with given GiftCardId";
        	return responseDTO;
        }
        Customer customer=null;
        try{
        	customer = customerService.getCustomer(customerId);
            if(customer==null){
            	responseDTO.message = "Invalid Customer! Could not find customer with given customerId.";
        		return responseDTO;
            }
        	}catch(Exception e){
            	responseDTO.message = "Could not find Customer Details!";
            	return responseDTO;
            }
        
        if (!giftCard.getStatus().equalsIgnoreCase(GiftCardStatus.ACTIVE.name()))
            responseDTO.message = "Gift Card Status must be " + GiftCardStatus.ACTIVE.name() + " to Redeem, but found=" + giftCard.getStatus();
        else {
            try {
            	
                redeemGiftCard(customer, giftCard, userId);
                responseDTO.message = "Gift Card has been Redeemed Succesfully and amount has been credited to credit account.";
                responseDTO.result = "SUCCESS";
            } catch (Exception e) {
            	logger.error("Failed to redeem Gidt Card.");
                responseDTO.message = e.getMessage();
            }
        }
        logger.info("Result =" + responseDTO.result + ", message=" + responseDTO.message);
        logger.info("--------------redeemGiftCard     DONE---------");
        return responseDTO;
    }

    @Override
    public List<GiftCardInfoDTO> listGiftCardOfCustomer(int customerId, String filter) {
        List<GiftCardInfoDTO> convertedObjList = new ArrayList<>();
        for (GiftCard giftCard : giftCardDAO.listGiftCardOfCustomer(customerId, filter)) {
            GiftCardInfo giftCardInfo = new GiftCardInfo();
            DtoUtility.copyEntity(giftCard, giftCardInfo);
            DtoUtility.copyEntity(giftCard.getGiftCardSold(), giftCardInfo);
            DtoUtility.copyEntity(giftCard.getGiftCardRedemption(), giftCardInfo);
            convertedObjList.add((GiftCardInfoDTO) DtoUtility.convertToDto(giftCardInfo, GiftCardInfoDTO.class));
        }
        return convertedObjList;
    }

    @Override
    public ResponseDTO deactivateGiftCard(String giftCardNo, int organisationId, int userId) {
        logger.info("--------------DeactivateGiftCard              START----------------");
        logger.info("giftCardNo=" + giftCardDAO);
        ResponseDTO response = new ResponseDTO();
        try {
            User user = userService.getUser(userId);
            GiftCard giftCard = giftCardDAO.getGiftCard(giftCardNo);
            if (giftCard == null)
                response.message = "Invalid Gift Card No, Please provide valid gift card no!";
            else if (!(user.getRole().getRole().equalsIgnoreCase("ADMIN") || user.getRole().getRole().equalsIgnoreCase("RESTAURANTMANAGER")) && !(userService.hasRole("admin") || userService.hasRole("restaurantManager")))
                response.message = "User with Role ADMIN OR RESTAURANT-MANAGER is only authorised to perform this action!!!";
            else if (!giftCard.getStatus().equalsIgnoreCase(GiftCardStatus.REDEEMED.name())) {
                giftCard.setStatus(GiftCardStatus.INACTIVE.name());
                giftCardDAO.saveOrUpdateGiftCard(giftCard);
                response.message = "SUCCESSFULLY INACTIVATED GIFT-CARD.";
                response.result = "SUCCESS";
            } else
                response.message = "Failed to INACTIVATE GIFT-CARD since status of gift is '" + giftCard.getStatus() + "', but It must not be 'REDEEMED'";
        } catch (Exception e) {
            response.message = e.getMessage();
        }
        logger.info("Result =" + response.result + ", Message=" + response.message);
        logger.info("--------------DeactivateGiftCard              DONE----------------");
        return response;
    }

    @Override
    public GiftCardInfoDTO listGiftCardInfo(String giftCardId) {
        logger.info("List Gift Card INFO for giftCardNo=" + giftCardDAO);
        GiftCard giftCard = giftCardDAO.getGiftCard(giftCardId);
        GiftCardInfo giftCardInfo = new GiftCardInfo();
        DtoUtility.copyEntity(giftCard, giftCardInfo);
        DtoUtility.copyEntity(giftCard.getGiftCardSold(), giftCardInfo);
        DtoUtility.copyEntity(giftCard.getGiftCardRedemption(), giftCardInfo);
        return (GiftCardInfoDTO) DtoUtility.convertToDto(giftCardInfo, GiftCardInfoDTO.class);
    }

    @Override
    public ResponseDTO restoreGiftCard(String giftCardNo, int organisationId, int userId) {
        logger.info("--------------ReStoreGiftCard              START----------------");
        logger.info("giftCardNo=" + giftCardDAO);
        ResponseDTO response = new ResponseDTO();
        try {
            User user = userService.getUser(userId);
            GiftCard giftCard = giftCardDAO.getGiftCard(giftCardNo);
            if (giftCard == null)
                response.message = "Invalid Gift Card No, Please provide valid gift card no!";
            else if (!(user.getRole().getRole().equalsIgnoreCase("ADMIN") || user.getRole().getRole().equalsIgnoreCase("RESTAURANTMANAGER")) && !(userService.hasRole("admin") || userService.hasRole("restaurantManager")))
                response.message = "User with Role ADMIN OR RESTAURANT-MANAGER is only authorised to perform this action!!!";
            else if (giftCard.getStatus().equalsIgnoreCase(GiftCardStatus.INACTIVE.name())) {
                giftCard.setStatus(GiftCardStatus.PRINT.name());
                //giftCard.setAmount(0);
                giftCard.setCreatedOn(DateUtil.getCurrentTimestampInGMT());
                giftCard.setGiftCardRedemption(null);
                giftCard.setGiftCardSold(null);
                giftCardDAO.rollBack(giftCard);
                response.message = "SUCCESSFULLY RESTORED GIFT-CARD.";
                response.result = "SUCCESS";
            } else
                response.message = "Failed to ReStore GIFT-CARD, Status should not be REDDEMED!";
        } catch (Exception e) {
            response.message = e.getMessage();
        }
        logger.info("Result =" + response.result + ", Message=" + response.message);
        logger.info("--------------DeactivateGiftCard              DONE----------------");
        return response;
    }

    @Override
    public void clearTestData() {
        giftCardDAO.clearTestData();
    }

    private void redeemGiftCard(Customer customer, GiftCard giftCard, Integer userId) throws Exception {
        //Open Credit Account if Customer do not have Credit Account
        if (customer.getCredit() == null)
            customerService.enableCustomerCredit(createCustomerCreditDTO(customer), customer.getOrgId());
         logger.info("Credit Account Enabled..");
        //Add Gift Card Balance to Credit Account
        customerCreditService.doTransaction(TransactionCategory.CREDIT, customer.getCustomerId(), "Gift Card Redemption with gift card id =" + giftCard.getGiftCardId() + ".", giftCard.getAmount(), giftCard.getGiftCardSold().getInvoiceId());

        logger.info("Ammount has been Credited to the Credit Account..");
        //Update Redeemer Details in Gift Card Sold
        giftCard.getGiftCardSold().setEmailIdOfRecipient(customer.getEmail());
        giftCard.getGiftCardSold().setMobileNoOfRecipient(customer.getPhone());
        
        //Update Redemption Details
        GiftCardRedemption giftCardRedemption = new GiftCardRedemption(giftCard, customer.getCustomerId(), userId);
        giftCard.setGiftCardRedemption(giftCardRedemption);
        giftCard.setStatus(GiftCardStatus.REDEEMED.name());
        giftCardDAO.saveOrUpdateGiftCard(giftCard);
        logger.info("Successfully redeemed....");
        //Mail Customer
       /* Restaurant restaurant = restaurantService.getRestaurant(customer.getOrgId());
        StringBuilder stringBuilder = new StringBuilder();
        if (customer.getFirstName() == null)
            stringBuilder.append("Hi Customer");
        else
            stringBuilder.append(customer.getFirstName());
        stringBuilder.append("<p></p>");
        stringBuilder.append("<p> Your Gift Gift with Gift Card No " + GiftCardIdGenerator.formateGiftCardId(giftCard.getGiftCardId()) + "" +
                "has been redeemed Successfully and amount has been credited to you Credit Account.");
        stringBuilder.append("<p></p>");
        stringBuilder.append("<p>Regards</p>");
        stringBuilder.append("<p>" + restaurant.getRestaurantName() + "</p>");

        sendMail(customer.getEmail(), "Gift Card Redemption", stringBuilder.toString(), customer.getOrgId());*/

    }


    private CustomerCreditDTO createCustomerCreditDTO(Customer customer) {
        CustomerCreditDTO customerCreditDTO = new CustomerCreditDTO();
        CustomerCreditAccountOpenINFODTO basicInfo = giftCardDAO.fetchDetailsRequiredToOpenDefaultCreditAccount(customer.getCustomerId(), customer.getOrgId(), BilligCycle.ONE_OFF.name());
        customerCreditDTO.customerId = customer.getCustomerId();
        customerCreditDTO.billingAddress = basicInfo.address;
        customerCreditDTO.creditBalance = 0;
        customerCreditDTO.creditTypeId = basicInfo.creditAccountTypeId;
        customerCreditDTO.ffcId = basicInfo.fulfillmentCenterId;
        return customerCreditDTO;
    }

	@Override
	public ResponseDTO createAndActivateGiftCard(float amount, String category, String msg, String invoiceId,
			int expireAfterDayCount, int userId, int organisationId, int userId2){
		logger.info("--------Start Create and Activate Gift Card -----------------------");
		ResponseDTO response = new ResponseDTO();
		try {
			List<GiftCardDTO> giftCradDTOList = createGiftCardForPrint(0, category, expireAfterDayCount, 1, userId, organisationId);
		    response = loadMoneyAndActivate(organisationId, userId, giftCradDTOList.get(0).giftCardId, amount, null, invoiceId, msg);
		} catch (Exception e) {
			logger.error("failed to create and activate gift card : "+e.getStackTrace());
			response.message=e.getMessage();
		}
		logger.info("--------DONE with Create and Activate Gift Card -----------------------"+response.message);
		return response;
	}


    /*private void sendMail(String toAddress, String subject, String body, int orgId) {
        Restaurant restaurantOrg = restaurantService.getRestaurant(orgId);
        List<String> addressList = new ArrayList<>(Arrays.asList(toAddress));
    }*/

}
