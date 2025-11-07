package com.cookedspecially.service;

import com.cookedspecially.domain.FulfillmentCenter;
import com.cookedspecially.domain.Transaction;
import com.cookedspecially.dto.ResponseDTO;
import com.cookedspecially.dto.saleRegister.*;
import com.cookedspecially.enums.till.TillTransactionStatus;
import org.springframework.security.access.prepost.PreAuthorize;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Abhishek 
 *
 */
public interface CashRegisterService {	
	Map<String, Object> createNewTill(TillDTO tillDTO, Integer userId);
	Map<String, Object> addCashIntoTill(boolean addCash, String tillId, Float balance, String remark, Integer attribute);
	Map<String, Object> getTillList(Integer userId);

    TransactionDTO openCloseTill(boolean openFlag, String remarks, String tillId, Integer userId) throws Exception;

    Map<String, String> editTill(String tillId, String tillName, Integer userId);
	Map<String, String> deleteTill(String tillId, String remraks, Integer userId);
	Map<String, String> checkCashBalance(String tillId, Integer userId);
	
	ResponseDTO updateCash(TillCashUpdateDTO updateDTO, Integer userId) throws Exception;
	
	TransactionDTO listTransactions(String transactionStatus,String tillId, Long fromTime, Long toTime, Integer userId);
	TransactionDTO fetchTransactionsByCheck(Integer userId, Integer checkId);
	void handoverPendingSales(Integer userId, SaleHandoverDTO handOverDTO) throws Exception;
	
	boolean validateTillAccess(String tillId, Integer userId);
	List<Transaction> getAllTransactionList(Integer userId, String tillId, String fromDate, String toDate) throws ParseException;
	
	List<Transaction> getTransactionListByDateRange(String till, Date startDate,Date endDate);
	Map<String,Object>  getTillListByFFCId(FulfillmentCenter fc);
	
	@PreAuthorize("hasAnyRole('restaurantManager','admin')")
	ResponseDTO refundOrder(Integer userId, int checkId);
	
	@PreAuthorize("hasAnyRole('restaurantManager','admin')")
	ResponseDTO applyDiscount(Integer userId, float discountedAmount, int checkId, String remarks, String paymentType);
	
	@PreAuthorize("hasAnyRole('admin')")
	ConflictedSaleDTO listConflictedSaleTransaction(int intValue);
	@PreAuthorize("hasAnyRole('admin')")
	ResponseDTO forceUpdateTransaction(String transactionId);
	//public ResponseDTO updateCashNew(TillCashUpdateDTO updateDTO, Integer userId) throws Exception;
    TillBalanceSummeryDTO getBalanceSummary(String tillId, int userId) throws Exception;

	ResponseDTO cancelOrderTransaction(int checkId, String paymentType, TillTransactionStatus status, boolean createCreditTransaction, Integer userId) throws Exception;

}
