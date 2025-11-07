package com.cookedspecially.dao.impl;


import com.cookedspecially.dao.CustomerCreditDAO;
import com.cookedspecially.domain.*;
import com.cookedspecially.dto.credit.*;
import com.cookedspecially.enums.credit.BilligCycle;
import com.cookedspecially.enums.credit.CreditTransactionStatus;
import com.cookedspecially.enums.credit.CustomerCreditAccountStatus;
import com.cookedspecially.enums.till.TransactionCategory;
import com.cookedspecially.service.CustomerService;
import com.cookedspecially.utility.DateUtil;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * @author Abhishek
 *
 */
@Repository
public class CustomerCreditDAOImpl implements CustomerCreditDAO{
    final static Logger logger = Logger.getLogger(CustomerCreditDAOImpl.class);


    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public void saveOrUpdate(CustomerCredit customerCredit) {
        sessionFactory.getCurrentSession().saveOrUpdate(customerCredit);
    }

	@Override
	public CustomerCredit getCustomerCredit(int customerId) {
		return (CustomerCredit) sessionFactory.getCurrentSession().createCriteria(CustomerCredit.class).add(Restrictions.eq("customerId", customerId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).uniqueResult();
	}

	@Override
	public void removeCustomerCreditAccount(Integer customerId) {
		Query updateQuery=sessionFactory.getCurrentSession().createQuery("UPDATE CustomerCredit c set c.status =:inactiveStatus where c.customerId=:customerId");
		updateQuery.setParameter("inactiveStatus", CustomerCreditAccountStatus.INACTIVE);
		updateQuery.setInteger("customerId", customerId);
		updateQuery.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> listCustomerWithCredit(int orgId) {
		Query query=sessionFactory.getCurrentSession().createQuery("select c from Customer c, CustomerCredit ct where c.customerId=ct.customerId AND c.orgId=:orgId order by c.customerId");
		query.setInteger("orgId", orgId);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> listCustomerWithCredit(int orgId, CustomerCreditAccountStatus status) {
		Query query=sessionFactory.getCurrentSession().createQuery("select c from Customer c, CustomerCredit where c.customerId=ct.customerId AND c.orgId=:orgId AND ct.status=:status order by c.customerId");
		query.setInteger("orgId", orgId);
		query.setString("status", status.name());
		return query.list();
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<CustomerCredit> listCustomerCredit(int orgId, float creditBalance, String expression) {
		return sessionFactory.getCurrentSession().createCriteria(CustomerCredit.class, "customerCredit").createAlias("customerCredit.customer", "customer").add(Restrictions.eq("customer.orgId", orgId)).list();	
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CreditTransactions> listTransaction(int customerId, String fromDate, String toDate) {
		Query query = null;
		if (fromDate == null) {
			query = sessionFactory.getCurrentSession().createQuery("from CreditTransactions where customerId=:customerId AND date<=:toDate order by date DESC");
		} else {
			query = sessionFactory.getCurrentSession().createQuery("from CreditTransactions where customerId=:customerId AND date<=:toDate AND date>=:fromDate order by date DESC");
			query.setString("fromDate", fromDate);
		}
		query.setInteger("customerId", customerId);
		query.setString("toDate", toDate);
		return query.list();
	}


	@Override
	public CreditType getCreditType(int orgId, String name) {
		return (CreditType) sessionFactory.getCurrentSession().createCriteria(CreditType.class).add(Restrictions.and(Restrictions.eq("name", name),Restrictions.eq("orgId",orgId))).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).uniqueResult();
	}

    @Override
    public CreditType getCreditTypeWithBillingCycle(int orgId, String billingCycleName) {
        return (CreditType) sessionFactory.getCurrentSession().createCriteria(CreditType.class).add(Restrictions.and(Restrictions.eq("billingCycle", billingCycleName), Restrictions.eq("orgId", orgId))).setMaxResults(1).list();
    }

	@Override
	public CreditType getCreditType(int creditTypeId) {
		return (CreditType) sessionFactory.getCurrentSession().get(CreditType.class, creditTypeId);
	}

	@Override
	public void saveOrUpdate(CreditType creditType, boolean updateMaxLimit) throws Exception {
		if(updateMaxLimit){
			Query updateCreditType = sessionFactory.getCurrentSession().createSQLQuery("UPDATE CREDIT_TYPE CT set CT.maxLimit = :newMaxLimit, CT.type_name=:newType, CT.billingCycle=:newBillingCycle, CT.banner_title=:banner  where CT.id=:creditId AND (SELECT IFNULL(MAX(CC.creditBalance), 0) FROM CUSTOMER_CREDIT CC where CC.type=:creditId)<=:maxAmount");
			updateCreditType.setFloat("newMaxLimit", creditType.getMaxLimit());
			updateCreditType.setString("newType", creditType.getName());
			updateCreditType.setInteger("creditId", creditType.getId());
			updateCreditType.setFloat("maxAmount", creditType.getMaxLimit());
			updateCreditType.setString("banner", creditType.getBanner());
			updateCreditType.setString("newBillingCycle", creditType.getBillingCycle().name());
			if(Integer.parseInt(""+updateCreditType.executeUpdate())> 0){
				Query updateMaxLimitQuery = sessionFactory.getCurrentSession().createSQLQuery("UPDATE CUSTOMER_CREDIT set maxLimit = :newMaxLimit where type =:creditTypeId AND maxLimit>=(select maxLimit from CREDIT_TYPE where id="+creditType.getId()+")");
				updateMaxLimitQuery.setFloat("newMaxLimit", creditType.getMaxLimit());
				updateMaxLimitQuery.setInteger("creditTypeId", creditType.getId());
				updateMaxLimitQuery.executeUpdate();
			}else
				throw new Exception("Can't Update Credit Type, either Customer has consumed max limit or no changes are found.");
		}else{
			sessionFactory.getCurrentSession().saveOrUpdate(creditType);	

		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CreditType> listCustomerCreditType(int orgId) {
		return sessionFactory.getCurrentSession().createCriteria(CreditType.class).add(Restrictions.and(Restrictions.eq("orgId",orgId))).list();
	}

	@Override
	public void saveOrUpdate(CreditTransactions transaction) {
		sessionFactory.getCurrentSession().saveOrUpdate(transaction);	
	}

	@Override
	public void deleteCustomerCreditType(int creditTypeId, int orgId) throws Exception {
		BigInteger count = (BigInteger) sessionFactory.getCurrentSession().createSQLQuery("SELECT COUNT(*) FROM  CUSTOMER_CREDIT where type="+creditTypeId+" AND status!='INACTIVE'").setMaxResults(1).uniqueResult();
		if(count.intValue()==0)
			sessionFactory.getCurrentSession().createQuery("delete CreditType where id ="+creditTypeId+" AND orgId="+orgId).executeUpdate();
		else
			throw new Exception("Migrate "+count+ " customer having same Credit Type and then try delete.");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CreditTransactionDTO> getTransactionList(int customerId, Date fromDate, Date toDate) {
		Query listCreditTransaction=sessionFactory.getCurrentSession().createSQLQuery("select c.invoiceId, totals.* from "
				+ "(select t.*,IFNULL((select cc.creditBalance-SUM(CASE when ct.type='DEBIT' THEN ct.amount else 0 END) "
				+ "+SUM(CASE when ct.type='CREDIT' THEN ct.amount else 0 END)As balance  from CREDIT_TRANSACTION ct, "
				+ "CUSTOMER_CREDIT cc where cc.customerId=ct.customerId AND ct.status=:status1 AND ct.customerId=t.customerId AND ct.date>t.date),"
				+ "ccc.creditBalance) As runningBalance from CREDIT_TRANSACTION t, CUSTOMER_CREDIT ccc where ccc.customerId=t.customerId AND ccc.customerId=:customerId AND  t.status=:status AND t.date>=:fromDate AND t.date<=:toDate "
				+ "ORDER BY date ASC)AS totals left outer join CHECKS c on totals.checkId=c.id ORDER BY date ASC");
		listCreditTransaction.setInteger("customerId", customerId);
		listCreditTransaction.setString("status", "SUCCESS");
		listCreditTransaction.setString("status1", "SUCCESS");
		listCreditTransaction.setTimestamp("fromDate", fromDate);
		listCreditTransaction.setTimestamp("toDate", toDate);
		listCreditTransaction.setResultTransformer(Transformers.aliasToBean(CreditTransactionDTO.class));
		return (List<CreditTransactionDTO>)listCreditTransaction.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CreditBill> listCustomerCreditBills(int customerId, String fromDate, String toDate) {
		Query query=null;
		if(fromDate==null && toDate==null)
			query = sessionFactory.getCurrentSession().createQuery("from CreditBill where customerId=:customerId order by date DESC LIMIT 10");
		else
		{
			query = sessionFactory.getCurrentSession().createQuery("from CreditBill where customerId=:customerId AND date>=:fromDate AND date<=:toDate order by date DESC");    
			query.setString("fromDate", fromDate);
			query.setString("toDate", toDate);
		}
		query.setInteger("customerId", customerId); 
		return query.list();         
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CreditBillDTO> listAllCustomerCreditBills(int ffcId, String fromDate, String toDate) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery("select * from (select CCB.customerId,CCB.maxLimit, CCB.billId, CCB.generationTime, CCB.billAmount,CCB.billAmount-sum(case when CCB.paymentMade IS NOT NULL then CCB.paymentMade else 0 end) as currentBillAmount, CCB.status, CCB.updatedOn, sum(case when CCB.paymentMade IS NOT NULL then CCB.paymentMade else 0 end) as paymentMade, max(CCB.paymentDate) as lastPaymentDate,CCB.billingAddress,CCB.name, CCB.email, CCB.mobileNo, CCB.creditType, CCB.banner from(select cb.customerId, cb.billId,cb.status, cb.updatedDate as updatedOn, cb.date as generationTime, cb.amount as billAmount, cc.billingAddress, CONCAT(c.firstName,' ', c.lastName) as name, c.email,c.phone as mobileNo,cc.maxLimit, ct.type_name as creditType, ct.banner_title as banner,cbp.amount as paymentMade,cbp.date as paymentDate from CUSTOMER_CREDIT cc inner join CUSTOMERS c on cc.customerId=c.customerId inner join  CREDIT_TYPE ct on cc.type=ct.id inner join CREDIT_BILLS cb on cb.customerId=cc.customerId left outer join CREDIT_BILL_PAYMENTS cbp on cbp.billId=cb.billId where cc.ffcId=:ffcId and cb.date=(select max(cbb.date) from CREDIT_BILLS cbb where cbb.customerId=cb.customerId and date(cb.date)>=:fromDate and date(cb.date)<=:toDate) order by cb.date DESC) as CCB group by CCB.customerId )as FCCB");

        //Query query = sessionFactory.getCurrentSession().createSQLQuery("select * from (select CCB.customerId,CCB.maxLimit, CCB.billId, CCB.generationTime, CCB.billAmount,CCB.billAmount-sum(case when CCB.paymentMade>0 then CCB.paymentMade else 0 end) as currentBillAmount, CCB.status, CCB.updatedOn, sum(case when CCB.paymentMade>0 then CCB.paymentMade else 0 end) as paymentMade, max(CCB.paymentDate) as lastPaymentDate,CCB.billingAddress,CCB.name, CCB.email, CCB.mobileNo, CCB.creditType, CCB.banner from(select cb.customerId, cb.billId,cb.status, cb.updatedDate as updatedOn, cb.date as generationTime, cb.amount as billAmount, cc.billingAddress,c.firstName as name, c.email,c.phone as mobileNo,cc.maxLimit, ct.type_name as creditType, ct.banner_title as banner,cbp.amount as paymentMade,cbp.date as paymentDate from CUSTOMER_CREDIT cc inner join CUSTOMERS c on cc.customerId=c.customerId inner join  CREDIT_TYPE ct on cc.type=ct.id inner join CREDIT_BILLS cb on cb.customerId=cc.customerId left outer join CREDIT_BILL_PAYMENTS cbp on cbp.billId=cb.billId where cc.ffcId=:ffcId and cb.date=(select max(cbb.date) from CREDIT_BILLS cbb where cbb.customerId=cb.customerId and date(cb.date)>=:fromDate and date(cb.date)<=:toDate) order by cb.date DESC) as CCB group by CCB.customerId )as FCCB");
        //Query query=sessionFactory.getCurrentSession().createQuery("select CCBB.*, (CCBB.billAmount-CCBB.paymentMade) as currentBillAmount from(select cb.customerId, cb.billId,cb.status, cb.updatedDate as updatedOn, cb.date as generationTime, cb.amount as billAmount, cc.billingAddress, c.firstName as name, c.email,c.phone as mobileNo, cc.maxLimit, ct.type_name as creditType, ct.banner_title as banner, sum(case when ctran.status='SUCCESS' and ctran.type='CREDIT' then ctran.amount else 0 end) as paymentMade, max(case when ctran.status='SUCCESS' and ctran.type='CREDIT' then ctran.date else NULL end) as lastPaymentDate from CUSTOMER_CREDIT cc inner join CUSTOMERS c on cc.customerId=c.customerId inner join CREDIT_TYPE ct on cc.type=ct.id inner join CREDIT_BILLS cb on cb.customerId=cc.customerId left outer join CREDIT_TRANSACTION ctran on cb.customerId=ctran.customerId and ctran.date>=cb.date where cc.ffcId=:ffcId and cb.date=(select max(cbb.date) from CREDIT_BILLS cbb where cbb.customerId=cb.customerId and date(cb.date)>=:fromDate and date(cb.date)<=:toDate) group by cb.customerId order by cb.date DESC) as CCBB group by CCBB.customerId");
		query.setString("fromDate", fromDate);
		query.setString("toDate", toDate);
		query.setInteger("ffcId", ffcId);
		query.setResultTransformer(Transformers.aliasToBean(CreditBillDTO.class));
		return query.list();    
	}

	@Override
	public CreditBill getRecentCustomerCreditBIll(int customerId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from CreditBill where customerId=:customerId order by date DESC");
		query.setInteger("customerId", customerId);
		query.setMaxResults(1);
		return (CreditBill) query.uniqueResult();
	}

	@Override
	public CreditTransactions getLastCreditTransaction(int customerId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from CreditTransactions where customerId=:customerId AND status=:status AND type=:type order by date DESC");
		query.setInteger("customerId", customerId);
		query.setString("status", CreditTransactionStatus.PENDING.name());
		query.setString("type", TransactionCategory.CREDIT.name());
		query.setMaxResults(1);
		return (CreditTransactions) query.uniqueResult();
	}
	@Override
	public float getCreditBalance(int customerId) {
		return 0;
	}

	@Override
	public void saveOrUpdateCreditBill(CreditBill creditBill) {
		sessionFactory.getCurrentSession().saveOrUpdate(creditBill);	
	}

	@Override
	public void generateCreditBill(String name) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery("INSERT into CREDIT_BILLS select uuid(),cc.creditBalance,now(), cc.customerId, 'NEW', NULL from CUSTOMER_CREDIT cc,CREDIT_TYPE ct where cc.type=ct.id AND ct.billingCycle=:billingCycle");
		//query.setTimestamp("currentTime", DateUtil.getCurrentTimestampInGMT());
		query.setString("billingCycle", name);
		query.executeUpdate();

	}

	@Override
    public List<Integer> getCustomerList(String billingCycle) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery("select CC.customerId from CUSTOMER_CREDIT CC, CREDIT_BILLS CB, CREDIT_TYPE CT where CC.customerId=CB.customerId AND CC.type=CT.id AND CT.type_name=:billingCycle AND AND date(CC.date)==date(:currentDate)");
        query.setTimestamp("currentDate", DateUtil.getCurrentTimestampInGMT());
        query.setString("billingCycle", billingCycle);
        return query.list();
    }

    @Override
    public CreditBill generateCreditBill(int customerId) {
		Query query=sessionFactory.getCurrentSession().createSQLQuery("INSERT into CREDIT_BILLS select uuid(),cc.creditBalance,:currentTime, cc.customerId,'NEW', null from CUSTOMER_CREDIT cc where cc.customerId=:customerId");	
		query.setTimestamp("currentTime", DateUtil.getCurrentTimestampInGMT());
		query.setInteger("customerId", customerId);
		query.executeUpdate();
		return getRecentCustomerCreditBIll(customerId);
	}

	@Override
	public void saveOrUpdate(CreditPayment billPayment) {
		sessionFactory.getCurrentSession().saveOrUpdate(billPayment);	
	}

    @Override
    public float getTotalCreditBillPaymentReceived(String creditBillId) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery("select sum(cbp.amount) from CREDIT_BILL_PAYMENTS cbp where cbp.billId=:billId");
        query.setString("billId", creditBillId);
        Double paymentMade = (Double) query.uniqueResult();

		try {
			return paymentMade.floatValue();
		} catch (Exception e) {
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CreditDispatchedBillDTO> listDispatchedOrSuccessCreditBill(int ffcId, int userId, String fromTime) {
		Query query=sessionFactory.getCurrentSession().createSQLQuery("select * from (select u.firstName as deliveredBY, ud.firstName as deliveryBoy,st.transaction_amount as billAmount, st.status,c.firstName as customerName, c.phone as mobileNo, ct.type_name as creditType, cbh.creditBillId, st.transaction_time as time, ffc.name as ffcName, rt.restaurantName, t.tillName from credit_bill_handler cbh, sale_register_transactions st, customers c,user ud, user u, tills t, fulfillmentcenter ffc, credit_bills cb, restaurant rt, credit_type ct, till_map tm, customer_credit cc where cbh.creditBillId=st.credit_bill_Id and cbh.creditBillId=cb.billId and st.tillId=t.tillId and t.tillId=tm.tillId  and tm.fulfillmentCenterId=ffc.id and ffc.restaurantId=rt.restaurantId and cbh.userId=u.userId and cb.customerId=cc.customerId and cc.type=ct.id and cbh.deliveryBoyId=ud.userId and cbh.userId=u.userId and cb.customerId=c.customerId and st.transaction_time>=:fromTime and c.orgId=:orgId order by st.transaction_time desc) as cps group by mobileNo");	
		query.setString("fromTime", fromTime);
		query.setInteger("orgId", ffcId);
		query.setResultTransformer(Transformers.aliasToBean(CreditDispatchedBillDTO.class));
		return query.list();
	}

	@Override
	public boolean isValidFFC(Integer orgId, int ffcId) {
		Query query=sessionFactory.getCurrentSession().createSQLQuery("select ffc.restaurantId from FULFILLMENTCENTER ffc, RESTAURANT r where ffc.restaurantId=r.restaurantId and r.parentRestaurantId=:orgId and ffc.id=:ffcId");
		query.setInteger("orgId", orgId);
		query.setInteger("ffcId", ffcId);
		Integer resturantId=(Integer) query.uniqueResult();
		return (resturantId.intValue() > 0);
	}

	@Override
	public CreditBill getCreditBill(String creditBillId) {
		return (CreditBill) sessionFactory.getCurrentSession().get(CreditBill.class, creditBillId);
	}

	@Override
	public CreditBill getCustomerCredit(int customerId, String statementDate) {
		Query query = sessionFactory.getCurrentSession().createQuery("from CreditBill where customerId=:customerId AND date(date)=:date order by date DESC");
		query.setInteger("customerId", customerId);
		query.setString("date", statementDate);
		query.setMaxResults(1);
		return (CreditBill) query.uniqueResult();
	}

	@Override
	public CreditBill getPreviousCustomerCredit(int customerId, String statementDate) {
		Query query = sessionFactory.getCurrentSession().createQuery("from CreditBill where customerId=:customerId AND date<:date order by date DESC");
		query.setInteger("customerId", customerId);
		query.setString("date", statementDate);
		query.setMaxResults(1);
		return (CreditBill) query.uniqueResult();
	}

	@Override
	public CreditStatementDTO getCreditTransactionSummery(int customerId, String fromDate, String toDate) {
		Query query = null;
        logger.info("From =" + fromDate + "    : to date=" + toDate + "    customerId=" + customerId);
        if (fromDate == null && toDate != null) {
			query = sessionFactory.getCurrentSession().createSQLQuery("select CONCAT(c.firstName,' ', c.lastName) as name, c.email, c.phone as mobileNo, c.orgId, cc.billingAddress, cc.maxLimit, ty.type_name as creditName, ty.billingCycle as creditType, sum(case when ct.type='CREDIT' and ct.amount < 0 then IFNULL(ct.amount,0) * -1 else 0 end) as paymentReceived, sum(case when ct.type='DEBIT' then ct.amount when ct.type='CREDIT' and ct.amount > 0 then ct.amount else 0 end) as totalPurchases from CUSTOMER_CREDIT cc inner join CUSTOMERS c on cc.customerId=c.customerId inner join  CREDIT_TYPE ty on cc.type=ty.id left outer join (select cts.type, cts.date, cts.customerId, cts.status, cts.invoiceId, sum(case when cts.type='DEBIT' then IFNULL(cts.amount,0) when cts.type='CREDIT' then IFNULL(cts.amount,0) * -1 end) as amount, cts.remark, c.deliveryAddress as address from CREDIT_TRANSACTION cts left outer join CHECKS c on c.invoiceId=cts.invoiceId where cts.customerId=:customerId AND cts.date<=:toDate AND cts.status = 'SUCCESS' group by IFNULL(cts.invoiceId, CONCAT(cts.customerId, cts.date)) order by date DESC) as ct on ct.customerId=c.customerId where c.customerId=:customerId");
			query.setString("toDate", toDate);
        } else if (fromDate != null && toDate == null) {
			query = sessionFactory.getCurrentSession().createSQLQuery("select CONCAT(c.firstName,' ', c.lastName) as name, c.email, c.phone as mobileNo, c.orgId, cc.billingAddress, cc.maxLimit, ty.type_name as creditName, ty.billingCycle as creditType, sum(case when ct.type='CREDIT' and ct.amount < 0 then IFNULL(ct.amount,0) * -1 else 0 end) as paymentReceived, sum(case when ct.type='DEBIT' then ct.amount when ct.type='CREDIT' and ct.amount > 0 then ct.amount else 0 end) as totalPurchases from CUSTOMER_CREDIT cc inner join CUSTOMERS c on cc.customerId=c.customerId inner join  CREDIT_TYPE ty on cc.type=ty.id left outer join (select cts.type, cts.date, cts.customerId, cts.status, cts.invoiceId, sum(case when cts.type='DEBIT' then IFNULL(cts.amount,0) when cts.type='CREDIT' then IFNULL(cts.amount,0) * -1 end) as amount, cts.remark, c.deliveryAddress as address from CREDIT_TRANSACTION cts left outer join CHECKS c on c.invoiceId=cts.invoiceId where cts.customerId=:customerId AND cts.date>=:fromDate AND cts.status = 'SUCCESS' group by IFNULL(cts.invoiceId, CONCAT(cts.customerId, cts.date)) order by date DESC) as ct on ct.customerId=c.customerId where c.customerId=:customerId");
			query.setString("fromDate", fromDate);
		} else {
			query = sessionFactory.getCurrentSession().createSQLQuery("select CONCAT(c.firstName,' ', c.lastName) as name, c.email, c.phone as mobileNo, c.orgId, cc.billingAddress, cc.maxLimit, ty.type_name as creditName, ty.billingCycle as creditType, sum(case when ct.type='CREDIT' and ct.amount < 0 then IFNULL(ct.amount,0) * -1 else 0 end) as paymentReceived, sum(case when ct.type='DEBIT' then ct.amount when ct.type='CREDIT' and ct.amount > 0 then ct.amount else 0 end) as totalPurchases from CUSTOMER_CREDIT cc inner join CUSTOMERS c on cc.customerId=c.customerId inner join  CREDIT_TYPE ty on cc.type=ty.id left outer join (select cts.type, cts.date, cts.customerId, cts.status, cts.invoiceId, sum(case when cts.type='DEBIT' then IFNULL(cts.amount,0) when cts.type='CREDIT' then IFNULL(cts.amount,0) * -1 end) as amount, cts.remark, c.deliveryAddress as address from CREDIT_TRANSACTION cts left outer join CHECKS c on c.invoiceId=cts.invoiceId where cts.customerId=:customerId AND cts.date>=:fromDate AND cts.date<=:toDate AND cts.status = 'SUCCESS' group by IFNULL(cts.invoiceId, CONCAT(cts.customerId, cts.date)) order by date DESC) as ct on ct.customerId=c.customerId where c.customerId=:customerId");
			query.setString("fromDate", fromDate);
            query.setString("toDate", toDate);
		}
		query.setInteger("customerId", customerId);
		query.setResultTransformer(Transformers.aliasToBean(CreditStatementDTO.class));
		query.setMaxResults(1);
		return (CreditStatementDTO) query.uniqueResult();
	}

	@Override
	public List<String> listCreditStatementListDate(int customerId) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery("select CAST(cb.date as char) from CREDIT_BILLS cb where cb.customerId=:customerId order by date DESC");
		query.setInteger("customerId", customerId);
		return (List<String>) query.list();
	}

	@Override
	public List<AgedCreditDTO> listAgedOneOffCreditHolder(int ffcId, int dayCount) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery("select c.customerId,cc.billingAddress, CONCAT(c.firstName,' ', c.lastName) as name, c.email,c.phone as mobileNo, ct.type_name as creditType, cc.creditBalance, max(cct.date) as lastTransactionDate from CUSTOMERS c inner join CUSTOMER_CREDIT cc on c.customerId=cc.customerId inner join CREDIT_TYPE ct on cc.type=ct.id left join CREDIT_TRANSACTION cct on cc.customerId=cct.customerId where DATEDIFF(date(CURDATE()), date(cct.date))>=:dayCount AND ct.billingCycle='ONE_OFF' AND cc.ffcId=:ffcId group by c.customerId");
		query.setInteger("ffcId", ffcId);
		query.setInteger("dayCount", dayCount);
		query.setResultTransformer(Transformers.aliasToBean(AgedCreditDTO.class));
		return (List<AgedCreditDTO>) query.list();
	}

	@Override
	public void listCreditStatement(BilligCycle billingCycle, CustomerService customerService) {
		boolean next = true;
		int batch_size = 50;
		int count = 0;
		while (next) {
            Query query = sessionFactory.getCurrentSession().createSQLQuery("select CB.billId from CREDIT_BILLS CB, CREDIT_TYPE CT,CUSTOMER_CREDIT CC where date(CB.date)=date(now()) and CC.customerId=CB.customerId and CC.type=CT.id and CT.billingCycle=:type");
            query.setString("type", billingCycle.name());
			List<String> billList = query.setFirstResult(count).setMaxResults(batch_size).list();
            logger.info("Sending Auto generated mail to =" + billList.size() + " no of customer. Pagination=" + count + " Credit Type=" + billingCycle.name());
            count += batch_size;
            customerService.autoEmailCreditBillbyList(billList);
            if (billList.size() == 0)
				next = false;
		}
	}

    @Override
    public CreditTransactions getCreditTransaction(String invoiceId, TransactionCategory transactionType, int customerId) {
        return (CreditTransactions) sessionFactory.getCurrentSession().createCriteria(CreditTransactions.class).add(Restrictions.eq("customerId", customerId)).add(Restrictions.eq("invoiceId", invoiceId)).add(Restrictions.eq("type", transactionType)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).uniqueResult();
    }

    @Override
    public CreditInfoDTO getCreditInfo(int customerId) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery("select CC.creditBalance , CC.maxLimit , CC.maxLimit-CC.creditBalance as availableCredit, CT.amount as recentPaymentAmount, CT.date as recentPaymentDate from CUSTOMER_CREDIT CC, CREDIT_TRANSACTION CT where CT.customerId=CC.customerId and CC.customerId=:customerId and CT.type='CREDIT' and CT.status = 'SUCCESS' order by CT.date desc limit 1");
		query.setInteger("customerId", customerId);
        query.setResultTransformer(Transformers.aliasToBean(CreditInfoDTO.class));
        return (CreditInfoDTO) query.uniqueResult();
    }


    @Override
    public List<CreditStatementTransactionDTO> creditStatementTransaction(int customerId, String fromDate, String toDate) {
        Query query = null;
        if (fromDate == null) {
			query = sessionFactory.getCurrentSession().createSQLQuery("select CS.date as transactionDate, CS.address, CS.invoiceId, CS.amount, CS.type, (CASE when CS.amount=0 and CS.type = 'CREDIT' then 'Cancelled order' when CS.amount > 0 and CS.type = 'CREDIT' then 'Account Settled. Money paid out to customer.' when CS.amount < 0 and CS.invoiceId is null then 'Credit Bill Payment made.Thank YOU.' when CS.amount < 0 and CS.invoiceId is not null and remark like '%Created a default account%' then 'Added gift card against invoice' when CS.amount < 0 and CS.invoiceId is not null then 'Credit Bill Payment made along with Order' when CS.amount > 0 then 'New order is placed.' end) as description from ( select ct.type, ct.date, ct.customerId, ct.status, ct.invoiceId, sum(case when ct.type='DEBIT' then IFNULL(ct.amount,0) when ct.type='CREDIT' then IFNULL(ct.amount,0) * -1 end) as amount, remark, c.deliveryAddress as address from CREDIT_TRANSACTION ct left outer join CHECKS c on c.invoiceId=ct.invoiceId where ct.customerId=:customerId AND date<=:toDate AND ct.status = 'SUCCESS' group by IFNULL(ct.invoiceId, CONCAT(ct.customerId, ct.date)) order by date DESC) as CS");
		} else {
			query = sessionFactory.getCurrentSession().createSQLQuery("select CS.date as transactionDate, CS.address, CS.invoiceId, CS.amount, CS.type, (CASE when CS.amount=0 and CS.type = 'CREDIT' then 'Cancelled order' when CS.amount > 0 and CS.type = 'CREDIT' then 'Account Settled. Money paid out to customer.' when CS.amount < 0 and CS.invoiceId is null then 'Credit Bill Payment made.Thank YOU.' when CS.amount < 0 and CS.invoiceId is not null and remark like '%Created a default account%' then 'Added gift card against invoice' when CS.amount < 0 and CS.invoiceId is not null then 'Credit Bill Payment made along with Order' when CS.amount > 0 then 'New order is placed.' end) as description from ( select ct.type, ct.date, ct.customerId, ct.status, ct.invoiceId, sum(case when ct.type='DEBIT' then IFNULL(ct.amount,0) when ct.type='CREDIT' then IFNULL(ct.amount,0) * -1 end) as amount, remark, c.deliveryAddress as address from CREDIT_TRANSACTION ct left outer join CHECKS c on c.invoiceId=ct.invoiceId where ct.customerId=:customerId AND date>=:fromDate AND date<=:toDate AND ct.status = 'SUCCESS' group by IFNULL(ct.invoiceId, CONCAT(ct.customerId, ct.date)) order by date DESC) as CS");
			query.setString("fromDate", fromDate);
        }
        query.setInteger("customerId", customerId);
        query.setString("toDate", toDate);
        query.setResultTransformer(Transformers.aliasToBean(CreditStatementTransactionDTO.class));
        return query.list();
    }

    @Override
    public void deleteCreditAccount(Integer customerId) {
        Query query = sessionFactory.getCurrentSession().createQuery("delete CustomerCredit where customerId = :ID");
        query.setParameter("ID", customerId);
        query.executeUpdate();
    }

}
