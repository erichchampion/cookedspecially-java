package com.cookedspecially.service.impl;

import com.cookedspecially.dao.CustomerCreditDAO;
import com.cookedspecially.domain.*;
import com.cookedspecially.dto.ResponseDTO;
import com.cookedspecially.dto.credit.*;
import com.cookedspecially.enums.credit.BilligCycle;
import com.cookedspecially.enums.credit.CreditBillStatus;
import com.cookedspecially.enums.credit.CreditTransactionStatus;
import com.cookedspecially.enums.credit.CustomerCreditAccountStatus;
import com.cookedspecially.enums.till.TransactionCategory;
import com.cookedspecially.service.*;
import com.cookedspecially.utility.DateUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.directory.InvalidAttributesException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


/**
 * @author Abhishek
 *
 */
@Service
public class CustomerCreditServiceImpl implements CustomerCreditService{

	final static Logger logger = Logger.getLogger(CustomerCreditServiceImpl.class);

	@Autowired
	private CustomerCreditDAO creditDAO;

	@Autowired
	private RestaurantService restaurantService;

	@Autowired
	private UserService userService;

	@Autowired
	private CashRegisterService cashRegisterService;

	@Autowired
	private CustomerService customerService;


	@Override
	@Transactional
	public void doBillingOnSUN() {
		logger.info("Doing credit billing of credit with billing cycle = onEachSUN");
		creditDAO.generateCreditBill(BilligCycle.onEachSUN.name());
		creditDAO.listCreditStatement(BilligCycle.onEachSUN, customerService);
	}


	@Override
	@Transactional
	public void doBillingOn1stDayOfMonth() {
		logger.info("Doing credit billing of credit with billing cycle = on1stDayOfMonth");
		creditDAO.generateCreditBill(BilligCycle.on1stDayOfMonth.name());
		creditDAO.listCreditStatement(BilligCycle.on1stDayOfMonth, customerService);
	}


	@Override
	@Transactional
	public void doBillingOn15thOfEveryMonth() {
		logger.info("Doing credit billing of credit with billing cycle = on15thOfEveryMonth");
		creditDAO.generateCreditBill(BilligCycle.on15thOfEveryMonth.name());
		creditDAO.listCreditStatement(BilligCycle.on15thOfEveryMonth, customerService);

	}

	@Override
	@Transactional
	public void doOneOffBilling() {

	}

	@Override
	@Transactional
	public CreditBill createCreditBill(String date, int customerId) {
		return null;
	}

	@Override
	@Transactional
	public AllCreditStatements listCustomerCreditBills(int customerId, String fromDate, String toDate) {
		logger.debug("in listCustomerCreditBills.");
		try {
			AllCreditStatements credit = new AllCreditStatements();
            credit.latestStatement = listLatestAccountDetails(customerId);
            credit.creditBills = creditDAO.listCustomerCreditBills(customerId, fromDate, toDate);
			return credit;
		} catch (Exception e) {
			logger.debug("Expection occured while retriving credit information.");
			return new AllCreditStatements();
		}
	}

	@Override
	@Transactional
	public List<CreditBillDTO> listAllCustomerCreditBills(int ffcId, String fromDate, String toDate) {
		logger.info("List Credit Bills ffcId=" + ffcId);
		Calendar cal = Calendar.getInstance();
		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		if (fromDate == null) {
			if (dayOfMonth < 15) {
				cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
				cal.set(Calendar.DAY_OF_MONTH, 15);
				fromDate = format.format(cal.getTime());
			} else {
				cal.set(Calendar.DAY_OF_MONTH, 1);
				fromDate = format.format(cal.getTime());
			}
		}
		if (toDate == null) {
			cal = Calendar.getInstance();
			toDate = format.format(cal.getTime());
		}
		logger.info("List Credit Bills ffcId=" + ffcId + " fromDate=" + fromDate + " to Date=" + toDate);
		return creditDAO.listAllCustomerCreditBills(ffcId, fromDate, toDate);
	}

	@Override
	@Transactional
	public CreditStatementDTO getRecentCustomerCreditBIll(int customerId) {
		CreditBill creditBill = creditDAO.getRecentCustomerCreditBIll(customerId);
		if (creditBill != null)
			return getCreditStatement(creditBill.getBillId());
		else
			return null;
	}

	@Override
	@Transactional
	public CreditStatementDTO generateCreditBilling(int customerId) {
		logger.info("Creating Credit Bill for customer =" + customerId);

		/*try {
            CreditBill cb=creditDAO.getRecentCustomerCreditBIll(customerId);
            List<CreditTransactions> cc = new ArrayList<>();
            cc = creditDAO.listTransaction(customerId, cb.getDate().toString(), DateUtil.getCurrentTimestampInGMT().toString());
            if (cc.size() == 0) {
                logger.info("Returning Old Credit Bill since no transaction were made after creating of the current credit Bill.");
                return getRecentCustomerCreditBIll(customerId);
            }
		} catch (Exception e) {
			logger.warn(e);
            logger.warn(e.getStackTrace());
        }*/
        creditDAO.generateCreditBill(customerId);
		return getRecentCustomerCreditBIll(customerId);
	}

	public void doTransaction(TransactionCategory transactionType, int customerId, String remarks, float amount, String invoiceId) throws Exception {
		logger.info("doTransaction type=" + transactionType.toString() + ", customerId=" + customerId + ", remarks=" + remarks + " amount=" + amount + ", invoiceId=" + invoiceId);
		CustomerCredit customerCreditAccount = creditDAO.getCustomerCredit(customerId);
		int orgId = customerCreditAccount.getCustomer().getOrgId();
		if (customerCreditAccount == null || (!customerCreditAccount.getStatus().equals(CustomerCreditAccountStatus.ACTIVE) && TransactionCategory.DEBIT.equals(transactionType)))
			throw new InvalidAttributesException("Credit Account is not ACTIVE. Please contact admin to ACTIVATE.");
		if (amount == 0)
			throw new InvalidAttributesException("Credit Transaction Amount must not be 0");
		if (transactionType == null)
			throw new InvalidAttributesException("Transaction Type must be either CREDIT/DEBIT");
		if (orgId <= 0)
			throw new Exception("Failed to get Organisation Id from customer");

		logger.info("Creating Transaction Customer Credit Account for customerId=" + customerId + ", orgId=" + orgId);
		if (TransactionCategory.CREDIT.equals(transactionType)) {
			createCreditTransaction(new CreditTransactions(transactionType, customerCreditAccount.getCustomerId(), remarks, amount), remarks, invoiceId);
			createCreditBillAndUpdateCreditAccount(customerCreditAccount, 0 - amount, customerCreditAccount.getCreditType().getBillingCycle().name().equals(BilligCycle.ONE_OFF.name()));
			logger.info("Customer Credit Balance is updated Successfully, current balance=" + customerCreditAccount.getCreditBalance());
		} else if (TransactionCategory.DEBIT.equals(transactionType)) {
			if (customerCreditAccount.getMaxLimit() >= (customerCreditAccount.getCreditBalance() + amount)) {
				createCreditTransaction(new CreditTransactions(transactionType, customerCreditAccount.getCustomerId(), remarks, amount), remarks, invoiceId);
				createCreditBillAndUpdateCreditAccount(customerCreditAccount, amount, customerCreditAccount.getCreditType().getBillingCycle().name().equals(BilligCycle.ONE_OFF.name()));
				logger.info("Customer Credit Balance is updated Successfully, current balance="+customerCreditAccount.getCreditBalance());
			} else {
				logger.info("Insufficient balance in account.Remaining Current Balance=" + (customerCreditAccount.getMaxLimit() - customerCreditAccount.getCreditBalance()));
				throw new Exception("Insufficient balance in account.Remaining Current Balance=" + (customerCreditAccount.getMaxLimit() - customerCreditAccount.getCreditBalance()));
			}
		} else {
			logger.info("Invalid TransactionType " + transactionType.name());
			throw new Exception("Invalid TransactionType" + transactionType.name());
		}
	}

    /**
     * @param transactionType
     * @param customerId
     * @param remarks
     * @param amount
     * @param invoiceId
     * @param updateType
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTransaction(TransactionCategory transactionType, int customerId, String remarks, float amount, String invoiceId, String updateType) throws Exception {
        CreditTransactions existingTransaction = creditDAO.getCreditTransaction(invoiceId, transactionType, customerId);
        if (existingTransaction == null)
            throw new Exception("Transaction does not exist!");
        if (!existingTransaction.getType().equals(TransactionCategory.DEBIT))
            throw new Exception("Transaction type only DEBIT is supported for update!");

        CustomerCredit creditAccount = creditDAO.getCustomerCredit(customerId);
        switch (updateType) {
            case "UPDATE":
                if (creditAccount.getMaxLimit() < (creditAccount.getCreditBalance() + amount - existingTransaction.getAmount()))
                    throw new Exception("Insufficient Balance in Credit account");
                CreditTransactions newTransaction = new CreditTransactions(existingTransaction.getType(), customerId, invoiceId, amount);
                newTransaction.setRemark(remarks);
                creditAccount.setCreditBalance(creditAccount.getCreditBalance() + amount - existingTransaction.getAmount());
                break;
            case "CANCEL":
                creditAccount.setCreditBalance(creditAccount.getCreditBalance() - amount);
                break;
            default:
                throw new Exception("Either you can update or cancel transaction!." + updateType + " is not supported.");
        }
        creditDAO.saveOrUpdate(creditAccount);
        existingTransaction.setStatus(CreditTransactionStatus.CANCELED);
        creditDAO.saveOrUpdate(existingTransaction);
    }

	private void createCreditTransaction(CreditTransactions transaction, String remarks, String invoiceId) {
		logger.info("In Create Credit Transaction=" + transaction.toString());
		transaction.setInvoiceId(invoiceId);
		transaction.setRemark(remarks);
		creditDAO.saveOrUpdate(transaction);
	}

	private void createCreditBillAndUpdateCreditAccount(CustomerCredit customerCreditAccount, float amount, boolean createBill) {
		logger.info("In Create Bill and update Credit Account, create bill=" + createBill + " credit account=" + customerCreditAccount.toString() + " current transaction amount=" + amount);
		customerCreditAccount.setCreditBalance(customerCreditAccount.getCreditBalance() + amount);
		creditDAO.saveOrUpdate(customerCreditAccount);
		logger.info("Customer Credit Account updated for=" + customerCreditAccount.getCreditBalance() + " got created.");
	}

	/**
	 * @param customerId
	 * @param paymentType
	 * @param invoiceId
	 * @param billAmount
	 * @param remarks
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean createBillRecoveryTransaction(int customerId, String paymentType, String invoiceId, float billAmount, String remarks) {
		logger.info("In CreateBillRecoveryTransaction customerId=" + customerId + ", current Credit Balance=" + billAmount);
		CreditTransactions lastCreditTransaction = creditDAO.getLastCreditTransaction(customerId);
		if (lastCreditTransaction == null || (((lastCreditTransaction.getDate().getTime() - DateUtil.getCurrentTimestampInGMT().getTime()) / 3600 > 4))) {
			if(lastCreditTransaction != null) {
				lastCreditTransaction.setStatus(CreditTransactionStatus.FAILED);
				creditDAO.saveOrUpdate(lastCreditTransaction);
				logger.info("Cancel previous pending Transaction id=" + lastCreditTransaction.getId() + ", Amount=" + lastCreditTransaction.getAmount());
				logger.info((lastCreditTransaction.getDate().getTime() - DateUtil.getCurrentTimestampInGMT().getTime()) / 3600);
			}
			CreditTransactions newCreditTransaction = new CreditTransactions(TransactionCategory.valueOf(paymentType), customerId, remarks, billAmount);
			newCreditTransaction.setInvoiceId(invoiceId);
			newCreditTransaction.setStatus(CreditTransactionStatus.PENDING);
			creditDAO.saveOrUpdate(newCreditTransaction);
			logger.info("Transaction created successfully, transactionId=" + newCreditTransaction.getId());
			return true;
		}
		return false;
	}

	/**
	 *
	 * @param customerId
	 * @param amount
	 * @param paymentType
	 * @param remarks
	 * @throws Exception
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateBillRecoveryTransaction(String status, int customerId, float amount, String paymentType, String remarks) throws Exception {
		logger.info("UpdateBillRecoveryTransaction Status=" + status + ", customerId=" + customerId + ", amount=" + amount + ", paymentType=" + paymentType + ", remarks=" + remarks);
		CreditTransactions lastCreditTransaction = creditDAO.getLastCreditTransaction(customerId);
		if (lastCreditTransaction != null) {
            logger.info("Fund credit transaction with pending status");
            CustomerCredit customerCreditAccount = creditDAO.getCustomerCredit(customerId);
			float billAmount = 0;
			if (CreditTransactionStatus.SUCCESS.name().equals(status))
				billAmount = 0 - amount;
			logger.info("payment received=" + billAmount);
			lastCreditTransaction.setStatus(CreditTransactionStatus.valueOf(status));
			lastCreditTransaction.setAmount(amount);
			createCreditBillAndUpdateCreditAccount(customerCreditAccount, billAmount, customerCreditAccount.getCreditType().getBillingCycle().name().equals(BilligCycle.ONE_OFF.name()));
			createCreditTransaction(lastCreditTransaction, "Credit Bill Paid.", lastCreditTransaction.getInvoiceId());
		} else
			throw new Exception("Transaction does not exist");
	}
	@Override
	@Transactional
	public CreditTransactions getLastPendingTransaction(Integer customerId) {
		return creditDAO.getLastCreditTransaction(customerId);
	}
	@Override
	@Transactional
	public List<Customer> listAllCustomerHavingCredit(int organisationId) {
		return creditDAO.listCustomerWithCredit(organisationId);
	}
	@Override
	@Transactional(rollbackFor=Exception.class)
	public ResponseDTO updateCreditBillTransaction(CreditBillPaymentDTO billPaymentDTO, int userId) throws Exception {
		logger.info("Create transaction to dispatch credit bill " + billPaymentDTO.to_String() + " userId=" + userId);
		ResponseDTO response=new ResponseDTO();

		/* Fix for trello Board 8:-  id 58735efb52504b7105493699 Refund management:Through settle credit account in Customer Management Screen.
		if(billPaymentDTO.billAmount<=0)
	    	throw new Exception("Invalid Credit Amount!");*/

        CreditBill creditBill = creditDAO.getCreditBill(billPaymentDTO.creditBillId);
        CreditBill latestCreditBill = creditDAO.getRecentCustomerCreditBIll(creditBill.getCustomerId());
        if (!latestCreditBill.getBillId().equals(creditBill.getBillId()))
            throw new Exception("Old credit Bill Id. Please Pay using latest credit Bill");

        creditBill = creditDAO.getCreditBill(billPaymentDTO.creditBillId);
        if (creditBill != null) {
			CreditPayment payment = new CreditPayment(billPaymentDTO.creditBillId, billPaymentDTO.billAmount, billPaymentDTO.paymentType, billPaymentDTO.remark);
			doTransaction(TransactionCategory.CREDIT, creditBill.getCustomerId(), "Credit Bill Payment made.Thank YOU.", billPaymentDTO.billAmount, null);
			float paymentReceived = 0;
			if (!(creditBill.getStatus().equals(CreditBillStatus.NEW) && creditBill.getAmount() == billPaymentDTO.billAmount)) {
				paymentReceived = creditDAO.getTotalCreditBillPaymentReceived(billPaymentDTO.creditBillId);
			}
			paymentReceived = paymentReceived + billPaymentDTO.billAmount;
			if (creditBill.getAmount() > paymentReceived)
				creditBill.setStatus(CreditBillStatus.PARTIALLY_PAID);
			else if (creditBill.getAmount() <= paymentReceived)
				creditBill.setStatus(CreditBillStatus.PAID);
			creditBill.setUpdatedOn(DateUtil.getCurrentTimestampInGMT());
			creditDAO.saveOrUpdateCreditBill(creditBill);
			creditDAO.saveOrUpdate(payment);
            logger.info("Payment done successfully.");
        } else
			throw new Exception("Invalid Credit Bill-Id");
        response.message = "Payment done successfully.";
        response.result = "SUCCESS";
		return response;
	}
	@Override
	@Transactional
	public List<CreditDispatchedBillDTO> listDispatchedOrSuccessCreditBill(int ffcId, int userId) {
		logger.info("listDispatchedOrSuccessCreditBill ffcId=" + ffcId);
		Calendar cal = Calendar.getInstance();
		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String fromDate;
		if (dayOfMonth < 15) {
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
			cal.set(Calendar.DAY_OF_MONTH, 15);
			fromDate = format.format(cal.getTime());
		} else {
			cal.set(Calendar.DAY_OF_MONTH, 1);
			fromDate = format.format(cal.getTime());
		}
		return creditDAO.listDispatchedOrSuccessCreditBill(ffcId, userId, fromDate);
	}

	@Override
	@Transactional
	public ResponseDTO markCreditBillAsDelivered(String creditBillId, int parseInt) {
		ResponseDTO response=new ResponseDTO();
		CreditBill creditBill = creditDAO.getCreditBill(creditBillId);
		try {
			creditBill.setStatus(CreditBillStatus.DELIVERED);
			creditBill.setUpdatedOn(DateUtil.getCurrentTimestampInGMT());
			creditDAO.saveOrUpdateCreditBill(creditBill);
		} catch (Exception e) {
			response.message="Credit Bill is marked as DELIVERED failed., Please Check if valid credit Bill";
			response.result = "ERROR";
		}
		response.message="Credit Bill is marked as DELIVERED successfully.";
		response.result = "SUCCESS";
		return response;
	}

	/**
	 * This method will give you all details of all
	 * activity done at credit account
	 *
	 * @param statementId
	 * @return CreditStatementDTO
	 */
	@Override
	@Transactional
	public CreditStatementDTO getCreditStatement(String statementId) {
		logger.info("Get Credit Statement StatementId = " + statementId);
		try {
			CreditBill currentBill = creditDAO.getCreditBill(statementId);
			CreditBill previousCreditBill = creditDAO.getPreviousCustomerCredit(currentBill.getCustomerId(), currentBill.getDate().toString());
			CreditStatementDTO creditStatement = creditDAO.getCreditTransactionSummery(currentBill.getCustomerId(), previousCreditBill == null ? null : previousCreditBill.getDate().toString(), currentBill.getDate().toString());
			creditStatement.availableCredit = creditStatement.maxLimit - currentBill.getAmount();
			creditStatement.fromDate = previousCreditBill == null ? null : previousCreditBill.getDate();
			creditStatement.toDate = currentBill.getDate();
			creditStatement.openingBanalce = (previousCreditBill == null) ? 0 : previousCreditBill.getAmount();
			creditStatement.outStandingBalance = currentBill.getAmount();
			creditStatement.statementDate = currentBill.getDate();
            //creditStatement.transactions = creditDAO.listTransaction(currentBill.getCustomerId(), previousCreditBill == null ? null : previousCreditBill.getDate().toString(), currentBill.getDate().toString());
            creditStatement.transactions = creditDAO.creditStatementTransaction(currentBill.getCustomerId(), previousCreditBill == null ? null : previousCreditBill.getDate().toString(), currentBill.getDate().toString());
			return creditStatement;
		} catch (Exception e) {
			logger.warn(e);
			logger.warn(e.getLocalizedMessage());
		}
		return null;
	}

	@Override
	@Transactional
	public List<AgedCreditDTO> listAgedOneOffCreditHolder(int ffcId, int dayCount) {
		return creditDAO.listAgedOneOffCreditHolder(ffcId, dayCount);
	}

    private CreditStatementDTO listLatestAccountDetails(int customerId) {
        CreditBill latestCreditBill = creditDAO.getRecentCustomerCreditBIll(customerId);
        logger.info("listLatestAccountDetails latestCreditBill" + latestCreditBill);
        CreditStatementDTO creditStatementDTO = null;
		if (latestCreditBill != null)
            creditStatementDTO = creditDAO.getCreditTransactionSummery(customerId, latestCreditBill.getDate().toString(), DateUtil.getCurrentTimestampInGMT().toString());
        else
            creditStatementDTO = creditDAO.getCreditTransactionSummery(customerId, null, DateUtil.getCurrentTimestampInGMT().toString());
        creditStatementDTO.fromDate = (latestCreditBill == null) ? null : latestCreditBill.getDate();
		String from = (latestCreditBill == null) ? null : latestCreditBill.getDate().toString();
		creditStatementDTO.toDate = DateUtil.getCurrentTimestampInGMT();
		creditStatementDTO.openingBanalce = (latestCreditBill == null) ? 0 : latestCreditBill.getAmount();
		creditStatementDTO.availableCredit = creditStatementDTO.maxLimit - creditStatementDTO.totalPurchases + creditStatementDTO.paymentReceived - creditStatementDTO.openingBanalce;
        creditStatementDTO.outStandingBalance = (latestCreditBill == null) ? creditStatementDTO.totalPurchases - creditStatementDTO.paymentReceived : creditStatementDTO.totalPurchases - creditStatementDTO.paymentReceived + latestCreditBill.getAmount();
        creditStatementDTO.statementDate = null;
        logger.info("list transaction between from " + creditStatementDTO.fromDate + " and to date=" + creditStatementDTO.toDate + " customerId=" + customerId);
        creditStatementDTO.transactions = creditDAO.creditStatementTransaction(customerId, from, creditStatementDTO.toDate.toString());
        return creditStatementDTO;
	}


	@Override
	@Transactional
	public void testBill() {
		creditDAO.listCreditStatement(BilligCycle.on1stAnd15thOfEveryMonth, customerService);
    }

    @Override
    @Transactional
    public CreditInfoDTO getCreditInfo(int customerId) {
        return creditDAO.getCreditInfo(customerId);
    }
}
