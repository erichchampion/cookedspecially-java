package com.cookedspecially.service;

import com.cookedspecially.domain.CreditBill;
import com.cookedspecially.domain.CreditTransactions;
import com.cookedspecially.domain.Customer;
import com.cookedspecially.dto.ResponseDTO;
import com.cookedspecially.dto.credit.*;
import com.cookedspecially.enums.till.TransactionCategory;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface CustomerCreditService {

	AllCreditStatements listCustomerCreditBills(int customerId, String fromDate, String toDate);

	List<CreditBillDTO> listAllCustomerCreditBills(int ffcId, String fromDate, String toDate);

	CreditStatementDTO getRecentCustomerCreditBIll(int customerId);
	@PreAuthorize("hasAnyRole('restaurantManager','admin')")
    CreditStatementDTO generateCreditBilling(int customerId);

	void doBillingOnSUN();

	void doBillingOn1stDayOfMonth();

	void doBillingOn15thOfEveryMonth();

	void doOneOffBilling();

	CreditBill createCreditBill(String date, int customerId);

	boolean createBillRecoveryTransaction(int customerId, String paymentType, String invoiceId, float billAmount,
										  String remarks);

	void updateBillRecoveryTransaction(String status, int customerId, float amount, String paymentType, String remarks)
			throws Exception;

	void doTransaction(TransactionCategory transactionType, int customerId, String remarks, float amount, String invoiceId) throws Exception;

	void updateTransaction(TransactionCategory transactionType, int customerId, String remarks, float amount, String invoiceId, String updateType) throws Exception;

	CreditTransactions getLastPendingTransaction(Integer customerId);

	List<Customer> listAllCustomerHavingCredit(int organisationId);

	List<CreditDispatchedBillDTO> listDispatchedOrSuccessCreditBill(int ffcId, int userId);
	@PreAuthorize("hasAnyRole('restaurantManager','admin')")
	ResponseDTO updateCreditBillTransaction(CreditBillPaymentDTO billPaymentDTO, int userId) throws Exception;
	@PreAuthorize("hasAnyRole('restaurantManager','admin')")
	ResponseDTO markCreditBillAsDelivered(String creditBillId, int parseInt);

	CreditStatementDTO getCreditStatement(String statementDate);

    List<AgedCreditDTO> listAgedOneOffCreditHolder(int ffcId, int dayCount);


	void testBill();

	CreditInfoDTO getCreditInfo(int customerId);
}
