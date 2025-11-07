package com.cookedspecially.dao;

import com.cookedspecially.domain.HandoverRequest;
import com.cookedspecially.domain.Till;
import com.cookedspecially.domain.Transaction;
import com.cookedspecially.dto.saleRegister.ConflictedSale;
import com.cookedspecially.enums.till.TillTransactionStatus;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public interface CashRegisterDAO {

	public void addHandoverRequest(HandoverRequest handoverRequest);
	public void updateHandoverRequest(HandoverRequest handoverRequest);
	public HandoverRequest getHandoverRequest(String tillId, int userId);
	public HandoverRequest getFirstHandoverRequest(String tillId, Timestamp fromStartTime);
	public HandoverRequest getHandoverRequest(Integer requestId);
	public HandoverRequest getLastHandoverRequest(String tillId);
	public List<HandoverRequest> getHandoverRequestList(String tillId, Integer userId);
	public List<HandoverRequest> getHandoverRequestList(String tillId);
	
	
	public void addTill(Till till);
	public 	void updateTill(Till till);
	public void updateTillBalance(String tillId, float amount);
	public Till getTill(String tillId);
	public Till getTillByName(String tillName);
	public List<Till> getActiveTillByFulfillmentcenter(Integer fulfillmentCenterId);
	public List<Till> getTillByFulfillmentcenter(Integer fulfillmentcenterId);

	
	public List<String> getListOfTillIDByFulfillmentcenter(Integer fulfillmentcenterId);
	public Float getCurrentBalance(String tillId);

    public List getBalanceSummery(String tillId, Timestamp from, Timestamp to, int userId);

	void saveTransaction(Transaction transaction);
	public Transaction getTransaction(String transactionId);
	public List<Transaction> fetchAllTransactionList(String tillId);
	public List<Transaction> fetchTransactionList(String creditBillId);
	public List<Transaction> fetchTransactionList(int checkId);
	public List<Transaction> getTransactionListByDateRange(String till, Date startDate,Date endDate);

    public Transaction getTransaction(boolean isCredit, Integer checkId, String tillTransactionType, String tillId, int userId);

    public List<Transaction> fetchTransactionList(String status, boolean flag, int userId, String tillId, Timestamp fromTime, Timestamp toTime);

	public List<ConflictedSale> listConfliactedSale(int orgId);
	
	public String getPaymentCategory(Integer orgId, String paymentType);
	public Integer getRestaurantIdOfTill(String tillId);
	public Integer getRestaurantIdOfFFC(Integer fulfillmentcenterId);


    public Transaction getTransaction(int checkId, String paymentType, Integer userId, TillTransactionStatus status, boolean isCredit);
	void handOverTransactions(String tillId, int fromId, int toId, String trsnactionStatus, Timestamp fromTime,
			Timestamp toTime);
}
