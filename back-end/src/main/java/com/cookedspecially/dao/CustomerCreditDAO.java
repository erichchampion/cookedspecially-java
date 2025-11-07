package com.cookedspecially.dao;


import com.cookedspecially.domain.*;
import com.cookedspecially.dto.credit.*;
import com.cookedspecially.enums.credit.BilligCycle;
import com.cookedspecially.enums.credit.CustomerCreditAccountStatus;
import com.cookedspecially.enums.till.TransactionCategory;
import com.cookedspecially.service.CustomerService;

import java.util.Date;
import java.util.List;

public interface CustomerCreditDAO {

	void saveOrUpdate(CustomerCredit customerCredit);

	CustomerCredit getCustomerCredit(int customerId);

	List<Customer> listCustomerWithCredit(int orgId);

	List<Customer> listCustomerWithCredit(int orgId, CustomerCreditAccountStatus status);

	List<CustomerCredit> listCustomerCredit(int orgId, float creditBalance, String expression);

    List<CreditTransactions> listTransaction(int customerId, String fromDate, String toDate);

	CreditType getCreditType(int orgId, String name);

    CreditType getCreditTypeWithBillingCycle(int orgId, String billingCycleName);

	List<CreditType> listCustomerCreditType(int orgId);

	CreditType getCreditType(int creditTypeId);

	void removeCustomerCreditAccount(Integer customerId);

	void saveOrUpdate(CreditTransactions transaction);

	void deleteCustomerCreditType(int creditTypeId, int orgId) throws Exception;

	void saveOrUpdate(CreditType creditType, boolean updateMaxLimit) throws Exception;

	List<CreditTransactionDTO> getTransactionList(int customerId, Date fromDate, Date toDate);

	List<CreditBill> listCustomerCreditBills(int customerId, String fromDate, String toDate);

	List<CreditBillDTO> listAllCustomerCreditBills(int ffcId, String fromDate, String toDate);

	CreditBill getRecentCustomerCreditBIll(int customerId);

	CreditTransactions getLastCreditTransaction(int customerId);

	float getCreditBalance(int customerId);

	void saveOrUpdateCreditBill(CreditBill creditBill);

	void generateCreditBill(String name);

    List<Integer> getCustomerList(String billing_cycle);

	CreditBill generateCreditBill(int customerId);

	List<CreditDispatchedBillDTO> listDispatchedOrSuccessCreditBill(int organisationId, int userId, String fromDate);

	boolean isValidFFC(Integer orgId, int ffcId);

	CreditBill getCreditBill(String creditBillId);

	void saveOrUpdate(CreditPayment billPayment);

	float getTotalCreditBillPaymentReceived(String creditBillId);

    CreditBill getCustomerCredit(int customerId, String statementDate);

	CreditBill getPreviousCustomerCredit(int customerId, String s);

    CreditStatementDTO getCreditTransactionSummery(int customerId, String fromDate, String toaDate);

    List<String> listCreditStatementListDate(int customerId);

    List<AgedCreditDTO> listAgedOneOffCreditHolder(int ffcId, int dayCount);

	void listCreditStatement(BilligCycle billingCycle, CustomerService customerService);

    CreditTransactions getCreditTransaction(String invoiceId, TransactionCategory transactionType, int customerId);

    CreditInfoDTO getCreditInfo(int customerId);

    List<CreditStatementTransactionDTO> creditStatementTransaction(int customerId, String fromDate, String toDate);

    void deleteCreditAccount(Integer customerId);
}
