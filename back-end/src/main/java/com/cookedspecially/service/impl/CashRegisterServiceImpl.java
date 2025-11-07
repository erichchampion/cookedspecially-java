package com.cookedspecially.service.impl;

import com.cookedspecially.dao.CashRegisterDAO;
import com.cookedspecially.domain.*;
import com.cookedspecially.domain.Till.TILL_STATUS;
import com.cookedspecially.dto.ResponseDTO;
import com.cookedspecially.dto.saleRegister.*;
import com.cookedspecially.enums.check.CustomPaymentType;
import com.cookedspecially.enums.till.*;
import com.cookedspecially.service.*;
import com.cookedspecially.utility.DateUtil;
import com.cookedspecially.utility.StringUtility;
import com.cookedspecially.utility.TillTransactionMailler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Abhishek
 *
 */
@Service
public class CashRegisterServiceImpl implements CashRegisterService {
	final static Logger logger = Logger.getLogger(CashRegisterService.class);

	@Autowired
	CashRegisterDAO cashRegisterDao;


	TillTransactionMailler sendMail=new TillTransactionMailler();

	@Autowired
	FulfillmentCenterService fulfillmentcenterService;

	@Autowired
	RestaurantService restaurantService;

	@Autowired
	CustomerService customerService;

	@Autowired
	UserService userService;

	@Override
	@Transactional(rollbackFor=Exception.class)
	public Map<String, Object> createNewTill(TillDTO tillDTO, Integer userId) {
		logger.info("Creating New Till");
		TreeMap<String, Object> map = new TreeMap<String, Object>();
		List<Till> tillList = null;
		User user=userService.getUser(userId);
        if (!hasAccessToFulfillmentCenter(user, tillDTO.fulfillmentCenterId)) {
            map.put("Error", "You do not has access to the requested Fulfillmentcenter !");
			logger.error(map.toString());
			return map;
		} else if (cashRegisterDao.getTillByName(tillDTO.tillName) != null) {
			map.put("Error", "Till name allready exist, try with different Name!");
			logger.error(map.toString());
			return map;
		} else if (((tillList = cashRegisterDao.getActiveTillByFulfillmentcenter(tillDTO.fulfillmentCenterId)) != null
				&& !tillList.isEmpty())
				&& !fulfillmentcenterService.getKitchenScreen(tillDTO.fulfillmentCenterId).isMultiTillSupported()) {
			map.put("Error",
					"Multiple Till is not supported at the moment.Can not create new Till. Please make use of existing till");
			logger.error(map.toString());
			return map;
		}

		Till till = new Till(tillDTO.balance, tillDTO.tillName);
		till.setFulfillmentcenter(fulfillmentcenterService.getKitchenScreen(tillDTO.fulfillmentCenterId));
		cashRegisterDao.addTill(till);
		sendMail.sendTillActivationMail(till,getTillMap(till), userService.getUser(userId), true, getAlertMailId(user.getOrgId()));
		if (tillDTO.balance != null && tillDTO.balance > 0)
			map = addCashIntoTill(true, till.getTillId(), tillDTO.balance, "Add Opening Balance", userId);
		return map;
	}



	@Override
	@Transactional(rollbackFor=Exception.class)
	public TreeMap<String, Object> addCashIntoTill(boolean addCash, String tillId, Float balance, String remark,
			Integer userId) {
		logger.info("Add/Withdraw cash to/from till" + remark);
		TreeMap<String, Object> map = new TreeMap<String, Object>();
		Till till = cashRegisterDao.getTill(tillId);
		User user=userService.getUser(userId);
		if (till != null) {
			if (hasAccessToTill(till, user, 0)) {
				if (addCash) {
					try {
                        createNewTransaction(CustomPaymentType.ADD_CASH.name(), CustomPaymentType.ADD_CASH.value(), balance, user, remark, null, false, till);
                        sendMail.sendAddWithDrawCashMail(till,balance,getTillMap(till),user, remark, true,getAlertMailId(user.getOrgId()));
					} catch (Exception e) {
						logger.info("Error occurred while adding cash into TILL."+e.getMessage());
					}
				} else {
					try {
                        createNewTransaction(CustomPaymentType.WITHDRAW_CASH.name(), CustomPaymentType.WITHDRAW_CASH.value(), balance, user, remark, null, false, till);
                        sendMail.sendAddWithDrawCashMail(till,balance,getTillMap(till),user, remark, false, getAlertMailId(user.getOrgId()));
					} catch (Exception e) {
						logger.info("Error occurred while withdrawing cash into TILL."+e.getMessage());
						map.put("Error", e.getMessage());
					}
				}
			} else {
				map.put("Error", "You do not has access to the requested Fulfillmentcenter !");
				logger.error(map.toString());
			}
		} else {
			map.put("Error", "Till does not exist with given till id.Plaese enter valid till Id!");
			logger.error(map.toString());
		}
		if(map.containsKey("Error"))
			return map;
		return getTillMap(till);
	}

	@Override
	@Transactional
	public Map<String, Object> getTillList(Integer userId) {
		Map<String, Object> map = new TreeMap<>();
		User user = userService.getUser(userId);
		switch (user.getRole().getRole()) {
		case "admin":
			ArrayList<Map<String, Object>> tillListAtOrganization = new ArrayList<>();
			List<Restaurant> restaurantList = restaurantService.listRestaurantByParentId(user.getOrgId());
			for (Restaurant resturant : restaurantList) {
				tillListAtOrganization.add(getTillListByRestaurant(resturant));
			}
			map.put("restaurantList", tillListAtOrganization);
			break;
		case "restaurantManager":
			List<Integer> restaurantIdList = user.getRestaurantId();
			ArrayList<Map<String, Object>> tillListAtRestaurant = new ArrayList<>();
			for (Integer restaurantId : restaurantIdList) {
				tillListAtRestaurant.add(getTillListByRestaurant(restaurantService.getRestaurant(restaurantId)));
			}
			map.put("restaurantList", tillListAtRestaurant);
			break;
		case "fulfillmentCenterManager":
		case "deliveryManager":
			Set<Integer> resturantIdList = new TreeSet<>();
			ArrayList<Object> tillListAtFulfillmentcenter = new ArrayList<>();
			ArrayList<Object> tillListAtRestaurantF = new ArrayList<>();
			Map<String, Object> restaurantTill = new TreeMap<>();
			for (Integer fulfillmentcenterId : user.getKitchenId()) {
				FulfillmentCenter fulfillmentcenter = fulfillmentcenterService.getKitchenScreen(fulfillmentcenterId);
				resturantIdList.add(fulfillmentcenter.getRestaurantId());
			}
			for (Integer restaurantId : resturantIdList) {
				restaurantTill.put("restaurantId", restaurantId);
				restaurantTill.put("restaurantName", restaurantService.getRestaurant(restaurantId).getRestaurantName());
				for (FulfillmentCenter fulfillmentcenter : fulfillmentcenterService.getKitchenScreens(restaurantId)) {
					if (user.getKitchenId().contains(fulfillmentcenter.getId())) {
						tillListAtFulfillmentcenter.add(getTillListByFulfillmentcenter(fulfillmentcenter));
					}
				}
				restaurantTill.put("fulfillmentcenterList", tillListAtFulfillmentcenter);
			}
			tillListAtRestaurantF.add(restaurantTill);
			map.put("restaurantList", tillListAtRestaurantF);
			break;
		}
		map.put("organizationId", user.getOrgId());
		map.put("organizationName", restaurantService.getRestaurant(user.getOrgId()).getRestaurantName());
		return map;
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public Map<String, String> editTill(String tillId, String tillName, Integer userId) {
		Till till = null;
		logger.info("Edit Till");
		Map<String, String> response = new TreeMap<>();
		if ((till = cashRegisterDao.getTill(tillId)) == null || !hasAccessToTill(till, userService.getUser(userId), 2))
			response.put("Error", "Till with given Id does not exist Or you do not has access to it");
		else if (!till.getTillName().equalsIgnoreCase(tillName) && cashRegisterDao.getTillByName(tillName) != null) {
			response.put("Error",
					"Edited Till name " + tillName + " is not available please try with different name !");
		} else if (till.getTillName().equalsIgnoreCase(tillName)) {
			response.put("tillId", till.getTillId());
			response.put("tillName", till.getTillName());
		} else if (cashRegisterDao.getTillByName(tillName) != null) {
			response.put("Error", "Till with requested Till Name exist, Try editing with different name");
		} else {
			till.setTillName(tillName);
			cashRegisterDao.updateTill(till);
			response.put("tillId", till.getTillId());
			response.put("tillName", till.getTillName());
		}
		return response;
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public Map<String, String> deleteTill(String tillId, String remarks, Integer userId) {
		logger.info("Deactive Till " + tillId + ".By user " + userId);
		TreeMap<String, String> map = new TreeMap<String, String>();// userId=21;
		Till till = null;
		User user=null;
		if (((till = cashRegisterDao.getTill(tillId)) == null) &&((user=userService.getUser(userId))==null) && !hasAccessToTill(till, user, 2))
			map.put("Error", "You do not has access to the requested Till!");
		else {
			if ((till.getStatus().equals(Till.TILL_STATUS.CLOSE.toString())
					|| till.getStatus().equals(Till.TILL_STATUS.ACTIVE.toString())) && (till.getBalance() < 50)) {
				updateHandover(new HandoverRequest(userId, tillId, remarks, Till.TILL_STATUS.INACTIVE.toString(),
						till.getBalance()));
				map.put("Success", "Till Has been Deactivated");
				sendMail.sendTillActivationMail(till,getTillMap(till), user, false, getAlertMailId(user.getOrgId()));
			} else
				map.put("Error", "Till can not be deleted in state=" + till.getStatus());
		}
		return map;
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
    public TransactionDTO openCloseTill(boolean openFlag, String remarks, String tillId, Integer userId) throws Exception {
        Till till = cashRegisterDao.getTill(tillId);
		TransactionDTO transactionDTO = new TransactionDTO();
		User user = userService.getUser(userId);
		if (till != null && user != null && hasAccessToTill(till,user,3)) {
			if (openFlag) {
				if ((till.getStatus().equals(Till.TILL_STATUS.CLOSE.toString())
						|| till.getStatus().equals(Till.TILL_STATUS.ACTIVE.toString()))) {
					updateHandover(new HandoverRequest(userId, tillId, remarks,
							HandoverRequest.HANDOVER_TYPE.OPEN.toString(), till.getBalance()));
					transactionDTO.tillDetails.balance = till.getBalance();
					transactionDTO.tillDetails.tillId = till.getTillId();
					transactionDTO.tillDetails.tillName = till.getTillName();
					transactionDTO.tillDetails.fulfillmentCenterId = till.getFulfillmentcenter().getId();
					transactionDTO.message = "Till is opened Successfully";
					transactionDTO.result = "Success";
					transactionDTO.tillDetails.openingTime = DateUtil.getCurrentTimestampInGMT();
					sendMail.sendOpenCloseMail(till, user, true, getTillMap(till), null, remarks, getAlertMailId(user.getOrgId()));
				} else {
					transactionDTO.message = "Could not open Till in status " + till.getStatus();
					transactionDTO.result = "Error";
				}
			} else if (!openFlag && hasAccessToTill(till, user, 3)
					&& TILL_STATUS.OPEN.toString().equalsIgnoreCase(till.getStatus())) {
				transactionDTO = listTransactions(TillTransactionStatus.PENDING.toString(), tillId, null, null, userId);
				if (!transactionDTO.transationList.isEmpty()) {
					transactionDTO.message = "Could not Close TILL as it has PENDING sale(s), handover all pending sale(s)";
					transactionDTO.result = "Error-Pending-Sale";
					logger.info(transactionDTO.message);
				} else {
                    sendMail.sendOpenCloseMail(till, user, false, getTillMap(till), getBalanceSummary(tillId, userId), remarks, getAlertMailId(user.getOrgId()));
                    updateHandover(new HandoverRequest(userId, tillId, remarks,
							HandoverRequest.HANDOVER_TYPE.CLOSE.toString(), till.getBalance()));
				}
			} else if (!openFlag && TILL_STATUS.CLOSE.toString().equalsIgnoreCase(till.getStatus())) {
				try {
					User u = userService.getUser(cashRegisterDao.getLastHandoverRequest(tillId).getUserId());
					transactionDTO.message = "Till is CLOSED and closed by " + u.getFirstName() + " " + u.getLastName()
							+ "(" + u.getRole().getRole() + ").";
				} catch (NullPointerException ne) {
					logger.info("No Handover request is found in DB for tillId=" + tillId);
					transactionDTO.message = "Till is already CLOSED";
				} catch (Exception e) {
					logger.info("No Handover request is found in DB for tillId==" + tillId);
					transactionDTO.message = "Till is already CLOSED";
				}
				transactionDTO.result = "Error";
			}
		} else {
			transactionDTO.message = "Do not have access to the Fulfillmentcenter";
			transactionDTO.result = "Error-Pending-Sale";
		}
		logger.info(transactionDTO.toString());
		return transactionDTO;
	}

	@Override
	@Transactional
	public Map<String, String> checkCashBalance(String tillId, Integer userId) {
		TreeMap<String, String> map = new TreeMap<String, String>();
		map.put("balance", "" + cashRegisterDao.getTill(tillId).getBalance());
		return map;
	}

	@Override
	@Transactional
	public boolean validateTillAccess(String tillId, Integer userId) {
		return hasAccessToTill(cashRegisterDao.getTill(tillId), userService.getUser(userId), 0);
	}

	/**
	 * @param updateDTO
	 * @param userId
	 * @return ResponseDTO
	 * @exception Exception
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public ResponseDTO updateCash(TillCashUpdateDTO updateDTO, Integer userId) throws Exception {
        logger.info("Create/Update Transaction on till " + updateDTO.toString());
        Till till = null;
        ResponseDTO response=new ResponseDTO();
		User user=null;
		if (StringUtility.isNullOrEmpty(updateDTO.tillId) || ((till = cashRegisterDao.getTill(updateDTO.tillId)) == null) || ((user = userService.getUser(userId)) == null))
			throw new Exception("Invalid tillId!");
		boolean eflag=true;
		switch(updateDTO.tillTransactionType){
		case "NEW":
            createNewTransaction(updateDTO.paymentType, getPaymentCategory(updateDTO.paymentType, user.getOrgId()), updateDTO.amount, user, updateDTO.remarks, updateDTO.checkId, updateDTO.isCredit, till);
            response.message="New Transaction is created successfully.";
			break;
		case "UPDATE":
            Transaction transaction = cashRegisterDao.getTransaction(updateDTO.isCredit, updateDTO.checkId, updateDTO.paymentType, updateDTO.tillId, userId);
            updateTransaction(transaction, updateDTO.updatedToPaymentType, updateDTO.paymentType, getPaymentCategory(updateDTO.updatedToPaymentType, user.getOrgId()), updateDTO.amount, user, updateDTO.remarks, updateDTO.checkId, updateDTO.isCredit, till);
            response.message="Transaction is Updated successfully.";
			  break;
		case "SUCCESS":
            Transaction transactionn = cashRegisterDao.getTransaction(updateDTO.isCredit, updateDTO.checkId, updateDTO.paymentType, updateDTO.tillId, userId);
            if(transactionn.getTransactionAmount()!=updateDTO.amount)
				throw new Exception("Invalid transaction amount!");
            updateTransaction(transactionn, null, updateDTO.paymentType, null, updateDTO.amount, user, updateDTO.remarks, updateDTO.checkId, updateDTO.isCredit, till);
            response.message="Transaction is marked success successfully.";
			break;
		case "CANCEL":
            cancelTransaction(cashRegisterDao.getTransaction(updateDTO.isCredit, updateDTO.checkId, updateDTO.paymentType, updateDTO.tillId, userId), till, false);
            response.message="Transaction is cancelled successfully.";
			break;
		default:
			eflag=false;
		}
		if(!eflag)
			throw new Exception("Invalid Till Transaction Command Type.It support NEW/UPDATE/SUCCESS/CANCEL only!");
        response.result="SUCCESS";
        logger.info(response.message);
		return response;
	}

	@Override
	@Transactional
	public TransactionDTO listTransactions(String transactionStatus, String tillId, Long fromTime, Long toTime,
			Integer userId) {
		logger.info("transaction Status=" + transactionStatus + " tillId=" + tillId + "userId=" + userId);
		TransactionDTO transactionDTO = new TransactionDTO();
		if (hasAccessToTill(cashRegisterDao.getTill(tillId), userService.getUser(userId), 1)) {
			HandoverRequest lastHandover = cashRegisterDao.getLastHandoverRequest(tillId);
			if (lastHandover != null && lastHandover.getStatus().equals(Till.TILL_STATUS.OPEN.toString())) {
				for (Transaction transaction : cashRegisterDao.fetchTransactionList(transactionStatus, true,
						lastHandover.getUserId(), tillId, lastHandover.getTime(),
						DateUtil.getCurrentTimestampInGMT())) {
					SaleTransaction saleTrsanction = new SaleTransaction();
					saleTrsanction.amount = transaction.getTransactionAmount();
					saleTrsanction.checkId = transaction.getCheckId();
					saleTrsanction.paymentType = transaction.getTransactionType();
					saleTrsanction.time = DateUtil
							.convertTimeInMilliSecToTimestampInIST(transaction.getTransactionTime().getTime());
					saleTrsanction.transactionId = transaction.getTransactionId();
					transactionDTO.transationList.add(saleTrsanction);
				}
				transactionDTO.message = "Success";
				transactionDTO.result = "Success";
				transactionDTO.tillDetails = getTillDTO(cashRegisterDao.getTill(tillId));
				transactionDTO.tillDetails.openingTime = DateUtil
						.convertTimeInMilliSecToTimestampInIST(lastHandover.getTime().getTime());
			} else {
				transactionDTO.message = "No any till OPENing request found.";
				transactionDTO.result = "Error";
			}
		} else {
			transactionDTO.message = "You do not have access to the Till";
			transactionDTO.result = "Error";
		}
		return transactionDTO;
	}

	@Override
	@Transactional
	public TransactionDTO fetchTransactionsByCheck(Integer userId, Integer checkId) {
		TransactionDTO transactionDTO = new TransactionDTO();
		String tillId = null;
		for (Transaction transaction : cashRegisterDao.fetchTransactionList(checkId)) {
			SaleTransaction saleTransaction = new SaleTransaction();
			saleTransaction.amount = transaction.getTransactionAmount();
			saleTransaction.checkId = transaction.getCheckId();
			saleTransaction.paymentType = transaction.getTransactionType();
			saleTransaction.status=transaction.getStatus();
			saleTransaction.time = transaction.getTransactionTime();
			saleTransaction.transactionId = transaction.getTransactionId();
			transactionDTO.transationList.add(saleTransaction);
			if (tillId == null)
				tillId = transaction.getTillId();
		}
		TillDTO tillDTO = new TillDTO();
		tillDTO.tillId = tillId;
		transactionDTO.tillDetails = tillDTO;
		transactionDTO.result = "Success";
		return transactionDTO;
	}

	@Override
	@Transactional
	public void handoverPendingSales(Integer userId, SaleHandoverDTO handOverDTO) throws Exception {
		User user = userService.getUserByUsername(handOverDTO.userName);
		Till till = cashRegisterDao.getTill(handOverDTO.tillId);
		User preUser=userService.getUser(userId);
        if (hasAccessToTill(till, preUser, 1) && hasAccessToTill(till, user, 4)) {
            HandoverRequest handOver = cashRegisterDao.getLastHandoverRequest(handOverDTO.tillId);
				if (handOver != null && handOver.getStatus().equals(TILL_STATUS.OPEN.toString())) {
					userId = handOver.getUserId();
					/*List<Transaction> transactionList = cashRegisterDao.fetchTransactionList(
							TillTransactionStatus.PENDING.toString(), true, userId, handOverDTO.tillId,
							handOver.getTime(), DateUtil.getCurrentTimestampInGMT());*/
                    TillBalanceSummeryDTO balanceSummery = getBalanceSummary(handOverDTO.tillId, userId);
                    updateHandover(new HandoverRequest(userId, handOverDTO.tillId, handOverDTO.remark,
							Till.TILL_STATUS.CLOSE.toString(), till.getBalance()));
					updateHandover(new HandoverRequest(user.getUserId(), handOverDTO.tillId, handOverDTO.remark,
							Till.TILL_STATUS.OPEN.toString(), till.getBalance()));
/*					for (Transaction pendingTransaction : transactionList) {
						pendingTransaction.setPreviousUserId(userId);
						pendingTransaction.setUserId(user.getUserId());
						pendingTransaction.setTransactionTime(DateUtil.getCurrentTimestampInGMT());
						cashRegisterDao.saveTransaction(pendingTransaction);
					}*/
					cashRegisterDao.handOverTransactions(till.getTillId(), userId, user.getUserId(), TillTransactionStatus.PENDING.toString(), handOver.getTime(), DateUtil.getCurrentTimestampInGMT());
					sendMail.sendHandoverMail(till, preUser, user, balanceSummery, handOverDTO.remark, getAlertMailId(user.getOrgId()));
				} else
					throw new Exception("Could not process request,Status of till is not OPEN");
		} else
			throw new Exception("You are not authorized to perform this action");
	}

	@Override
	@Transactional
	public List<Transaction> getAllTransactionList(Integer userId,String tillId, String fromDate, String toDate)
			throws ParseException {
        String timeZomeString="IST";
       try{
    	   timeZomeString=restaurantService.getRestaurant(userService.getUser(userId).getOrgId()).getTimeZone();
       	}catch(Exception e){
	      logger.info("set defaut time zone to IST");
	   }
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		TimeZone timeZone=TimeZone.getTimeZone(timeZomeString);
        df.setTimeZone(timeZone);
		return cashRegisterDao.fetchTransactionList(null, false, 0, tillId, DateUtil.convertTimeInMilliSecToTimestampInGMT(DateUtil.getStartOfDay(df.parse(fromDate), timeZomeString).getTime()), DateUtil.convertTimeInMilliSecToTimestampInGMT(DateUtil.getEndOfDay(df.parse(fromDate), timeZomeString).getTime()));
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public ResponseDTO refundOrder(Integer userId, int checkId) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			for (Transaction transaction : cashRegisterDao.fetchTransactionList(checkId)) {
				/*if (!Arrays.asList(CustomePaymentType.values()).contains(transaction.getTransactionType())
						&& TillTransactionStatus.SUCCESS.toString().equalsIgnoreCase(transaction.getStatus())) {
					    try {
							createNewTransaction(CustomePaymentType.REFUND.name(), transaction.getTransactionCategory(), transaction.getTransactionAmount(), userService.getUser(userId), "Refund Order!", checkId, transaction.getCreditBillId(), cashRegisterDao.getTill(transaction.getTillId()));
						} catch (Exception e) {
                          responseDTO.message="Could not refund, Contact Admin!";
                          responseDTO.result="Error";
						}
						responseDTO.message = "Successfully refunded. Make sure amount equal to transaction amount is takenout from till.";
						responseDTO.result = "Success";
				}*/
			}
		}
		catch (NullPointerException nExp) {
			responseDTO.message = "Could not fing any Trasaction for given checkID";
			responseDTO.result = "Error";
		}
		return responseDTO;
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
    public ResponseDTO applyDiscount(Integer userId, float discountedAmount, int checkId, String remarks, String paymentType) {
        ResponseDTO responseDTO = new ResponseDTO();
        logger.info("applyDiscount checkId=" + checkId + " UserId=" + userId + " discountedAmount=" + discountedAmount);
        try {
			Transaction existingTransaction = cashRegisterDao.getTransaction(checkId,paymentType,userId, TillTransactionStatus.SUCCESS, false);
            if (discountedAmount < existingTransaction.getTransactionAmount()) {
                existingTransaction.setStatus(TransactionStatus.CANCELLED.name());
				cashRegisterDao.saveTransaction(existingTransaction);

                Transaction newTransaction = new Transaction(existingTransaction.getUserId(), existingTransaction.getTillId(), checkId, existingTransaction.getTransactionAmount() - discountedAmount, existingTransaction.getTransactionType(), existingTransaction.getTransactionCategory());
                newTransaction.setIsCreditTransaction(false);
                newTransaction.setStatus(TillTransactionStatus.SUCCESS.name());
                newTransaction.setRemarks("Discount has been applied by userId=" + userId);
                cashRegisterDao.saveTransaction(newTransaction);

                Transaction creditTransaction = new Transaction(existingTransaction.getUserId(), existingTransaction.getTillId(), checkId, discountedAmount, existingTransaction.getTransactionType(), existingTransaction.getTransactionCategory());
                creditTransaction.setIsCreditTransaction(true);
                creditTransaction.setRemarks("Add Discounted amount to credit of customer. Operated by userId=" + userId);
                creditTransaction.setStatus(TillTransactionStatus.SUCCESS.name());
                cashRegisterDao.saveTransaction(creditTransaction);
            }
			responseDTO.message = "Discount Amount must be less than order amount";
			responseDTO.result = "ERROR";
			return responseDTO;
		} catch (NullPointerException nExp) {
			responseDTO.message = "Could not find any Transaction for given checkID";
			responseDTO.result = "ERROR";
		}
		return responseDTO;
	}

	private boolean isCustomPaymentType(String paymentType) {
        for (CustomPaymentType customPaymentType : CustomPaymentType.values()) {
            if(customPaymentType.name().equals(paymentType))
        	  return true;
        }
		return false;
	}



	private TreeMap<String, Object> getTillMap(Till till) {
		TreeMap<String, Object> map = new TreeMap<String, Object>();
		map.put("tillId", till.getTillId());
		map.put("name", till.getTillName());
		map.put("balance", till.getBalance());
		map.put("openingTime", DateUtil.getCurrentTimestampInGMT());
		return map;
	}

	private TillDTO getTillDTO(Till till) {
		TillDTO tillDTO = new TillDTO();
		tillDTO.tillName = till.getTillName();
		tillDTO.tillId = till.getTillId();
		tillDTO.balance = till.getBalance();
		tillDTO.openingTime = DateUtil.getCurrentTimestampInGMT();
		return tillDTO;
	}

	private void updateHandover(HandoverRequest handoverRequest) {
		Till till = cashRegisterDao.getTill(handoverRequest.getTillId());
		till.setStatus(handoverRequest.getStatus());
		cashRegisterDao.updateHandoverRequest(handoverRequest);
		cashRegisterDao.addTill(till);
		logger.info("Till is updated to the state=" + till.getStatus());
		logger.info("new handoer request with state=" + handoverRequest.getStatus());
	}


    private void createNewTransaction(String type, String category, float balance, User user, String remark, Integer checkId, boolean isCredit, Till till) throws Exception {
        logger.info("Create New Transaction, payment type=" + type + " balance=" + balance + " checkId=" + checkId + " category of transaction=" + category + " tillId=" + till.getTillId());
        if (!hasAccessToTill(till, user, 0))
            throw new Exception("Do not have access to create transaction!");

        if (!isDuplicateTransaction(type, user.getUserId(), checkId, isCredit, till.getTillId(), balance)) {
            Transaction transaction = new Transaction(user.getUserId(), till.getTillId(), checkId, balance, type, category);
			transaction.setRemarks(remark);
            transaction.setIsCreditTransaction(isCredit);
            switch (type) {
			case "ADD_CASH":
				if(TransactionCategory.CASH.toString().equalsIgnoreCase(category)) {
					transaction.setStatus(TillTransactionStatus.SUCCESS.toString());
					till.setBalance(till.getBalance()+balance);
					cashRegisterDao.updateTill(till);
				}else
					throw new Exception("Invalid Transaction Category!");
				break;
			case "DISCOUNT":
			case "WITHDRAW_CASH":
			case "REFUND":
			case "TRANSACTION_CASH":
				if(TransactionCategory.CASH.toString().equalsIgnoreCase(category) || TransactionCategory.DEBIT.toString().equalsIgnoreCase(category)){
					if(till.getBalance()<balance)
						throw new Exception("Insufficient Balance in Till!");
					logger.info("Update till balance since  transaction category="+category);
				    till.setBalance(till.getBalance()-balance);
				    cashRegisterDao.updateTill(till);
				}
                if (CustomPaymentType.WITHDRAW_CASH.name().equals(type))
                    transaction.setStatus(TillTransactionStatus.SUCCESS.toString());
				break;
			default :
				transaction.setStatus(TillTransactionStatus.PENDING.toString());
				break;
			}
			cashRegisterDao.saveTransaction(transaction);
			logger.info("Successfully created Transaction("+transaction.getTransactionId()+").");
		}else {
			logger.info("Duplicate Transaction Request, Hence rejected checkId="+checkId);
			throw new Exception("Error! Duplicate Transaction Request, Hence rejected for checkId=" + checkId);
		}
	}
	private void cancelTransaction(Transaction transaction, Till till, boolean updateToCash) throws Exception{
		if(transaction==null)
			throw new Exception("Transaction does not exist!");
        logger.info("Transaction " + transaction.getTransactionId() + " of amount=" + transaction.getTransactionAmount() + ",CheckId=" + transaction.getCheckId() + ",creditBillId=" + transaction.getIsCreditTransaction() + ", till=" + transaction.getTillId() + "" + " is being cancelled.");
        if(transaction.getStatus().equals(TillTransactionStatus.SUCCESS.name())){
			if(transaction.getTransactionCategory().equals(TransactionCategory.CASH.name())){
				till.setBalance(till.getBalance()-transaction.getTransactionAmount());
				cashRegisterDao.addTill(till);
				logger.info("Deduct balance from till,since transaction status is success");
		    }else if(updateToCash){
		    	till.setBalance(till.getBalance()+transaction.getTransactionAmount());
		    	cashRegisterDao.addTill(till);
		    	logger.info("Add balance to till,since transaction status is success");
		   }
		}
		transaction.setTransactionTime(DateUtil.getCurrentTimestampInGMT());
		transaction.setStatus(TillTransactionStatus.CANCELLED.name());
		cashRegisterDao.saveTransaction(transaction);
        logger.info("Transaction " + transaction.getTransactionId() + " of amount=" + transaction.getTransactionAmount() + ",CheckId=" + transaction.getCheckId() + ",creditBillId=" + transaction.getIsCreditTransaction() + ", till=" + transaction.getTillId() + "" + " is cancelled.");
    }

    private void updateTransaction(Transaction transaction, String updateType, String type, String category, float balance, User user, String remark, Integer checkId, boolean isCredit, Till till) throws Exception {
        logger.info("Update Transaction, payment type=" + type + " balance=" + balance + " checkId=" + checkId + " category of transaction=" + category + "isCredit=" + isCredit + " tillId=" + till.getTillId());
        if(transaction==null)
			throw new Exception("Invalid Transaction!");

		if (transaction.getStatus().equals(TillTransactionStatus.SUCCESS.name())) {
					if(transaction.getTransactionAmount()==balance) {
						if (transaction.getTransactionType().equals(updateType))
							throw new Exception("Could not update transaction to same payment Type!");
                        else if (updateType != null && !isDuplicateTransaction(updateType, user.getUserId(), checkId, isCredit, till.getTillId(), balance)) {
                            createNewTransaction(updateType, category, transaction.getTransactionAmount(), user, remark, checkId, isCredit, till);
                            cancelTransaction(transaction, till, false);
                            updateTransaction(cashRegisterDao.getTransaction(isCredit, checkId, updateType, transaction.getTillId(), transaction.getUserId()), null, updateType, category, balance, user, remark, transaction.getCheckId(), isCredit, till);
                            sendMail.sendTransactionUpdateMail(user, remark, transaction.getCheckId(), balance, transaction.getTransactionType(), updateType, "DELIVERED", getAlertMailId(user.getOrgId()));
						} else
							throw new Exception("Could not update transaction, Check if duplicate transaction");
					}/*else if(userService.hasRole("admin")){
							createNewTransaction(updateType, category, balance, user, remark, checkId, creditBillId, till);
							cancelTransaction(transaction, till, false);
							updateTransaction(cashRegisterDao.getTransaction(creditBillId, checkId, updateType, transaction.getTillId(), transaction.getUserId()), null, updateType, category, balance, user, remark, transaction.getCheckId(), creditBillId, till);
							sendMail.sendTransactionUpdateMail(user, remark, transaction.getCheckId(), balance, transaction.getTransactionType(), updateType, "DELIVERED", getAlertMailId(user.getOrgId()));
					}*/ else {
						throw new Exception("Can not update amount after delivery.");
					}
				}else if(transaction.getStatus().equals(TillTransactionStatus.PENDING.name())){
            if (updateType != null && transaction.getTransactionAmount() == balance && !isDuplicateTransaction(updateType, user.getUserId(), checkId, isCredit, till.getTillId(), balance)) {
                createNewTransaction(updateType, category, balance, user, remark, checkId, isCredit, till);
                cancelTransaction(transaction, till, false);
					    sendMail.sendTransactionUpdateMail(user,remark,checkId,balance,transaction.getTransactionType(),updateType, "DISPATCHED", getAlertMailId(user.getOrgId()));
					}else if(updateType==null && transaction.getTransactionAmount()==balance && transaction.getTransactionType().equals(type)){
						transaction.setStatus(TillTransactionStatus.SUCCESS.name());
						transaction.setRemarks(transaction.getRemarks()+remark);
                if (transaction.getTransactionCategory().equals(TransactionCategory.CASH.name()) || transaction.getTransactionType().equals(CustomPaymentType.TRANSACTION_CASH.name())) {
                    till.setBalance(till.getBalance()+transaction.getTransactionAmount());
						    cashRegisterDao.addTill(till);
						    logger.info("Add cash to the till.Since Transaction is marked success from pending and holds cash");
						}
						cashRegisterDao.saveTransaction(transaction);
					    logger.info("Transaction is marked successful");
					}else if(transaction.getTransactionAmount()!=balance && updateType!=null){
					    logger.info("Update transaction amount");
                createNewTransaction(updateType, category, balance, user, remark, checkId, isCredit, till);
                cancelTransaction(transaction, till, false);
					    sendMail.sendTransactionAmountUpdateMail(user,remark,checkId,balance,transaction.getTransactionType(),transaction.getTransactionAmount(), "DISPATCHED", getAlertMailId(user.getOrgId()));
					    if(transaction.getTransactionType().equals(updateType))
					    sendMail.sendTransactionUpdateMail(user, remark, checkId, balance, transaction.getTransactionType(), updateType, "DISPATCHED", getAlertMailId(user.getOrgId()));
					}
					else
						throw new Exception("Invalid update request!");
				}else
					throw new Exception("Invalid update request!");
	}


    private boolean isDuplicateTransaction(String type, int userId, Integer checkId, boolean isCredit, String tillId, float amount) {
        if (checkId != null)
            return (cashRegisterDao.getTransaction(isCredit, checkId, type, tillId, userId) != null);
        return false;
    }

    private boolean hasAccessToFulfillmentCenter(User user, Integer fulfillmentCenterId) {
        return user.getRestaurantId().contains(cashRegisterDao.getRestaurantIdOfFFC(fulfillmentCenterId));
    }

	private boolean hasAccessToTill(Till till, User user, int accessType){
		boolean hasAccess=false;
		switch(accessType){
		case 0: //Transactional Access
			HandoverRequest hreq = cashRegisterDao.getLastHandoverRequest(till.getTillId());
			if (hreq != null && hreq.getUserId().intValue()==user.getUserId().intValue() && till.getStatus().equals(Till.TILL_STATUS.OPEN.toString())&& hreq.getStatus().equalsIgnoreCase(Till.TILL_STATUS.OPEN.toString()))
				hasAccess = true;
			break;
		case 1: // HandOver Access
			if(user.getRole().getRole().equals("admin"))
				return true;
            else
                return hasAccessToTill(till, user, 0);
            case 2: //Management Access
			if(user.getRole().getRole().equals("admin"))
				return true;
		   else if(user.getRole().getRole().equals("deliveryManager"))
				return false;
		   else
                return user.getRestaurantId().contains(cashRegisterDao.getRestaurantIdOfTill(till.getTillId())) && (user.getRole().getRole().equals("fulfillmentCenterManager") || user.getRole().getRole().equals("restaurantManager"));
            case 3: //Open-Close till Access
            if (till.getStatus().equals(Till.TILL_STATUS.OPEN.toString())) {
                return hasAccessToTill(till, user, 0) || user.getRole().getRole().equals("admin");
            } else {
                return user.getRole().getRole().equals("fulfillmentCenterManager") || user.getRole().getRole().equals("restaurantManager") || user.getRole().getRole().equals("admin") || user.getRole().getRole().equals("deliveryManager");
            }
            case 4: //HandOver To AccESS
                return user.getRole().getRole().equals("fulfillmentCenterManager") || user.getRole().getRole().equals("restaurantManager") || user.getRole().getRole().equals("admin") || user.getRole().getRole().equals("deliveryManager");
        }
		logger.info("Has access to till="+hasAccess);
		return hasAccess;
	}

	private Map<String, Object> getTillListByRestaurant(Restaurant restaurant) {
		ArrayList<Map<String, Object>> ffcList = new ArrayList<>();
		Map<String, Object> tillListAtRestaurant = new TreeMap<String, Object>();
		List<FulfillmentCenter> fulfillmentcenterList = fulfillmentcenterService
				.getKitchenScreens(restaurant.getRestaurantId());
		for (FulfillmentCenter fulfillmentcenter : fulfillmentcenterList) {
			ffcList.add(getTillListByFulfillmentcenter(fulfillmentcenter));
		}
		tillListAtRestaurant.put("restaurantId", restaurant.getRestaurantId());
		tillListAtRestaurant.put("restaurantName", restaurant.getRestaurantName());
		tillListAtRestaurant.put("fulfillmentcenterList", ffcList);
		return tillListAtRestaurant;
	}

	private Map<String, Object> getTillListByFulfillmentcenter(FulfillmentCenter fulfillmentcenter) {
		Map<String, Object> tillList = new TreeMap<String, Object>();
		tillList.put("fulfillmentcenterId", fulfillmentcenter.getId());
		tillList.put("fulfillmentcenterName", fulfillmentcenter.getName());
		ArrayList<Map<String, Object>> til = new ArrayList<>();
		for (Till till : cashRegisterDao.getTillByFulfillmentcenter(fulfillmentcenter.getId())) {
			Map<String, Object> tills = new TreeMap<>();
			tills.put("tillId", till.getTillId());
			tills.put("tillName", till.getTillName());
			tills.put("balance", till.getBalance());
			tills.put("status", till.getStatus());
			HandoverRequest handover = cashRegisterDao.getLastHandoverRequest(till.getTillId());
			if (handover != null) {
				User user = userService.getUser(handover.getUserId());
				if (user != null && handover.getStatus().equals(HandoverStatus.OPEN.toString())) {
					tills.put("openningTime", handover.getTime());
					tills.put("currentOwnerName", user.getFirstName() + " " + user.getLastName());
				} else if (user != null && handover.getStatus().equals(HandoverStatus.CLOSE.toString())) {
					tills.put("closingTime",
							DateUtil.convertTimeInMilliSecToTimestampInIST(handover.getTime().getTime()));
					tills.put("previousOwnerName", user.getFirstName() + " " + user.getLastName());
				}
			} else {
				tills.put("openningTime", null);
				tills.put("openningTime", null);
				tills.put("currentOwnerName", "Not Any User Found");
			}
			tills.put("creationTime", till.getDateCreated());
			til.add(tills);
		}
		tillList.put("tillList", til);
		return tillList;
	}

	private void getBalanceSummery(Map<String, Object> map, String status, String tillId, Timestamp fromTime,
			Timestamp toTime, Integer userId) {
        logger.info("Get Current Balance Summery. TillId=" + tillId + " Status=" + status);
        try {
            for (Transaction transaction : cashRegisterDao.fetchTransactionList(status, true, userId, tillId, fromTime,
                    toTime)) {
                if (map.containsKey(transaction.getTransactionType())) {
                    Float balance = (Float) map.get(transaction.getTransactionType());
                    map.put(transaction.getTransactionType(), balance + transaction.getTransactionAmount());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(e);
        }
        logger.debug("Summery==" + map);
    }

	@Override
	@Transactional
	public List<Transaction> getTransactionListByDateRange(String till, Date startDate, Date endDate) {
		return cashRegisterDao.getTransactionListByDateRange(till, startDate, endDate);
	}

	@Override
	@Transactional
	public Map<String, Object> getTillListByFFCId(FulfillmentCenter fc) {
		return getTillListByFulfillmentcenter(fc);
	}

	@Override
	@Transactional
	public ConflictedSaleDTO listConflictedSaleTransaction(int orgId) {
		List<ConflictedSale> saleList=cashRegisterDao.listConfliactedSale(orgId);
		ConflictedSaleDTO conflicatedSaleDTO=new ConflictedSaleDTO();
		conflicatedSaleDTO.conflictedSaleAtRestaurant=new ArrayList<>();
		for(ConflictedSale conflicatedSale: saleList){
			boolean found=false;
				for(ConflictedSaleByRestaurant cR: conflicatedSaleDTO.conflictedSaleAtRestaurant){
					if(cR.restaurantName.equals(conflicatedSale.restaurantName)){
						found=true;
						boolean cFFound=false;
						for(ConflictedSaleAtFulfillmentCenter cF: cR.conflictedSaleAtFulfillmentCenter){
							if(cF.fulfillmentCenterName.equals(conflicatedSale.fulfillmentCenterName)){
								cFFound=true;
								cF.conflictedSale.add(mapConflictedSale(conflicatedSale));
							}
						}
						if(!cFFound)
						cR.conflictedSaleAtFulfillmentCenter.add(mapToFulfillmentCenter(conflicatedSale));
					}
				}
				if(!found)
					conflicatedSaleDTO.conflictedSaleAtRestaurant.add(mapConflicatedSaleByRestaurant(conflicatedSale));
		}
		return conflicatedSaleDTO;
	}

	@Override
	@Transactional
	public ResponseDTO forceUpdateTransaction(String transactionId) {
		Transaction transaction=cashRegisterDao.getTransaction(transactionId);
		ResponseDTO responseDTO=new ResponseDTO();
        if (CustomPaymentType.TRANSACTION_CASH.name().equals(transaction.getTransactionType())) {
            Till till=cashRegisterDao.getTill(transaction.getTillId());
			till.setBalance(till.getBalance()+transaction.getTransactionAmount());
			transaction.setStatus(TillTransaction.SUCCESS.name());
			cashRegisterDao.addTill(till);
		}else
			transaction.setStatus(TillTransaction.CANCEL.name());
		cashRegisterDao.saveTransaction(transaction);
		responseDTO.result="SUCCESS";
		responseDTO.message="Transaction Canceled Successfully";
		cashRegisterDao.saveTransaction(transaction);
		return responseDTO;
	}
	private ConflictedSaleByRestaurant mapConflicatedSaleByRestaurant(ConflictedSale conflicatedSale){
		ConflictedSaleByRestaurant confliactedSaleByRestaurant=new ConflictedSaleByRestaurant();
		confliactedSaleByRestaurant.restaurantId=conflicatedSale.restaurantId;
		confliactedSaleByRestaurant.restaurantName=conflicatedSale.restaurantName;
		List<ConflictedSaleAtFulfillmentCenter> conFList=new ArrayList<>();
		conFList.add(mapToFulfillmentCenter(conflicatedSale));
		confliactedSaleByRestaurant.conflictedSaleAtFulfillmentCenter=conFList;
		return confliactedSaleByRestaurant;
	}
	private ConflictedSaleAtFulfillmentCenter mapToFulfillmentCenter(ConflictedSale conflicatedSale){
		ConflictedSaleAtFulfillmentCenter confFFC=new ConflictedSaleAtFulfillmentCenter();
		confFFC.fulfillmentCenterId=conflicatedSale.fulfillmentCenterId;
		confFFC.fulfillmentCenterName=conflicatedSale.fulfillmentCenterName;
		confFFC.conflictedSale=new ArrayList<>();
		confFFC.conflictedSale.add(mapConflictedSale(conflicatedSale));
		return confFFC;
	}
	private ConflictedSaleDetails mapConflictedSale(ConflictedSale conflicatedSale){
		ConflictedSaleDetails confSale=new ConflictedSaleDetails();
		confSale.orderId=conflicatedSale.orderId;
		confSale.orderStatus=conflicatedSale.orderStatus;
		confSale.orderAmount=conflicatedSale.orderAmount;
		confSale.checkStatus=conflicatedSale.check_status;
		confSale.checkId=conflicatedSale.checkId;
		confSale.invoiceId=conflicatedSale.invoiceId;
		confSale.transactionAmount=conflicatedSale.transaction_amount;
		confSale.transactionDate=conflicatedSale.transaction_time;
		confSale.transactionId=conflicatedSale.transactionId;
		confSale.transactionType=conflicatedSale.transaction_type;
		confSale.phone=conflicatedSale.phone;
		confSale.roundOffTotal=conflicatedSale.roundOffTotal;
		return confSale;
	}
	@SuppressWarnings("unused")
	private String getAlertMailId(int orgId){
		logger.info("get Alert mailId of the organisation"+orgId);
		try {
			return restaurantService.getRestaurant(orgId).getAlertMail();
		} catch (NullPointerException ex) {
			logger.error("Could not get Organisation MailId to send Till Alert Message.");
			return null;
		}
	}

	@Override
	@Transactional
    public TillBalanceSummeryDTO getBalanceSummary(String tillId, int userId) throws Exception {
        logger.info("Get BalanceSummery tillId=" + tillId);
        HandoverRequest hr = cashRegisterDao.getLastHandoverRequest(tillId);
        TillBalanceSummeryDTO saleSummary = new TillBalanceSummeryDTO();
        Till till = cashRegisterDao.getTill(tillId);
        User user = userService.getUser(hr.getUserId());
        saleSummary.Current_Cash_Balance = till.getBalance();
        saleSummary.Initial_Cash_Balance = hr.getBalance();
        saleSummary.tillName = till.getTillName();
        saleSummary.ffcName = till.getFulfillmentcenter().getName();
        saleSummary.Till_Owner = user.getFirstName() + " " + user.getLastName() + "(" + user.getRole().getRole()
                + ")";
        saleSummary.transactionSummary = initializeSummeryTable(user.getOrgId());
        for (Object transactionObj : cashRegisterDao.getBalanceSummery(tillId, hr.getTime(), DateUtil.getCurrentTimestampInGMT(), hr.getUserId())) {
            Object[] details = (Object[]) transactionObj;
            if (CustomPaymentType.ADD_CASH.name().equals((String) details[1]))
                saleSummary.ADD_CASH = Float.parseFloat("" + details[0]);
            else if (CustomPaymentType.WITHDRAW_CASH.name().equals((String) details[1]))
                saleSummary.WITHDRAW_CASH = Float.parseFloat("" + details[0]);
            else if (CustomPaymentType.TRANSACTION_CASH.name().equals((String) details[1]) && TillTransactionStatus.PENDING.name().equals((String) details[3]))
                saleSummary.TRANSACTION_CASH = Float.parseFloat("" + details[0]);
            else
                updateSummery(saleSummary.transactionSummary, (String) details[1], (TransactionCategory) details[2], Float.parseFloat("" + details[0]), (boolean) details[4], (String) details[3]);
        }
        return saleSummary;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO cancelOrderTransaction(int checkId, String paymentType, TillTransactionStatus status, boolean createCreditTransaction, Integer userId) throws Exception {
        logger.info("cancelOrderTransaction : " + checkId + ", paymentType=" + paymentType + ", status=" + status + ", createCreditTransaction=" + createCreditTransaction);
        ResponseDTO responseDTO = new ResponseDTO();
        Transaction transaction = cashRegisterDao.getTransaction(checkId, paymentType, userId, status, false);
        if (transaction != null && transaction.getStatus().equals(TillTransactionStatus.SUCCESS.name())) {
            transaction.setStatus(TillTransaction.CANCEL.name());
            transaction.setRemarks(transaction.getRemarks() + "\n" + "Cancel order after delivery. Open/Add order amount to credit account : " + createCreditTransaction + "." + "\n Action taken by userId=" + userId + ".");
            cashRegisterDao.saveTransaction(transaction);
            logger.info("Successfully cancelled transaction for checkId="+checkId);
            if (createCreditTransaction) {
                Transaction credit_transaction = new Transaction(transaction.getUserId(), transaction.getTillId(), checkId, transaction.getTransactionAmount(), transaction.getTransactionType(), transaction.getTransactionCategory());
                credit_transaction.setRemarks("Delivered order got canceled so adding money to Credit Account");
                credit_transaction.setStatus(TillTransactionStatus.SUCCESS.name());
                credit_transaction.setIsCreditTransaction(true);
                cashRegisterDao.saveTransaction(credit_transaction);
                logger.info("Successfully created  credit transaction for payment type = " + credit_transaction.getTransactionType()+" CheckId="+checkId+" Transaction id="+credit_transaction.getTransactionId());
            }
            responseDTO.message = "Order Transaction is canceled successfully!";
            responseDTO.result = "SUCCESS";
        }
       else {
            logger.info("Invalid checkId, No any transaction is found with given checkId and paymentType!");
            responseDTO.message = "Invalid checkId, No any transaction is found with given checkId and paymentType!";
            responseDTO.result = "ERROR";
        }
        return responseDTO;
    }

    private PaymentHistoryDTO initializeSummeryTable(int orgId) throws Exception {
        PaymentHistoryDTO history = new PaymentHistoryDTO();
        for (PaymentType paymentType : restaurantService.listPaymentTypeByOrgId(orgId)) {
            if (paymentType.getStatus().equalsIgnoreCase(com.cookedspecially.enums.Status.ACTIVE.toString())
                    || paymentType.getStatus().equalsIgnoreCase(com.cookedspecially.enums.Status.INACTIVE.toString())) {
                history.creditSummary.add(new PaymentCategoryDTO(paymentType.getType(), paymentType.getName()));
                history.saleSummary.add(new PaymentCategoryDTO(paymentType.getType(), paymentType.getName()));
            }
        }
		return history;
    }

    private void updateSummery(PaymentHistoryDTO paymentDto, String paymentType, TransactionCategory cat, float amount, boolean isCredit, String status) {
        if (isCredit) {
            if (status.equals(TillTransactionStatus.SUCCESS.name())) {
                for (PaymentCategoryDTO cs : paymentDto.creditSummary) {
                    if (cs.paymentTypeName.equals(paymentType))
                        cs.completedAmount = amount;
                }
            } else if (status.equals(TillTransactionStatus.PENDING.name())) {
                for (PaymentCategoryDTO cs : paymentDto.creditSummary) {
                    if (cs.paymentTypeName.equals(paymentType))
                        cs.pendingAmount = amount;
                }
            }
        } else {
            if (status.equals(TillTransactionStatus.SUCCESS.name())) {
				for (PaymentCategoryDTO ss : paymentDto.saleSummary) {
					if (ss.paymentTypeName.equals(paymentType))
						ss.completedAmount = amount;
				}
			} else if (status.equals(TillTransactionStatus.PENDING.name())) {
				for (PaymentCategoryDTO ss : paymentDto.saleSummary) {
					if (ss.paymentTypeName.equals(paymentType))
						ss.pendingAmount = amount;
				}
			}
        }
    }

    private String getPaymentCategory(String paymentType, Integer orgId) {
        try {
            if (CustomPaymentType.valueOf(paymentType) != null)
                return TransactionCategory.CASH.name();
        } catch (Exception e) {
        }
        try {
            return TransactionCategory.valueOf(cashRegisterDao.getPaymentCategory(orgId, paymentType)).name();
        } catch (Exception e) {
            return TransactionCategory.CREDIT.name();
        }
    }

}
