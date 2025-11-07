package com.cookedspecially.dao.impl;

import com.cookedspecially.dao.CashRegisterDAO;
import com.cookedspecially.domain.*;
import com.cookedspecially.dto.saleRegister.ConflictedSale;
import com.cookedspecially.dto.saleRegister.PaymentHistoryDTO;
import com.cookedspecially.enums.till.TillTransactionStatus;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class CashRegisterDAOImpl implements CashRegisterDAO{


	@Autowired
	private SessionFactory sessionFactory;


	//********************************************* Transaction *****************************************************************************************
	@SuppressWarnings("unchecked")
	@Override
	public List<Transaction> fetchTransactionList(String creditBillId) {
		return (List<Transaction>) sessionFactory.getCurrentSession().createCriteria(Transaction.class).add(Restrictions.eq("creditBillId", creditBillId)).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Transaction> fetchTransactionList(int checkId) {
		return (List<Transaction>) sessionFactory.getCurrentSession().createCriteria(Transaction.class).add(Restrictions.eq("checkId", checkId)).list();
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Transaction> fetchAllTransactionList(String tillId) {
		return (List<Transaction>) sessionFactory.getCurrentSession().createCriteria(Transaction.class).add(Restrictions.eq("tillId", tillId)).list();
	}

	@Override
    public Transaction getTransaction(boolean isCredit, Integer checkId, String tillTransactionType, String tillId, int userId) {
        if (checkId != null)
            return (Transaction) sessionFactory.getCurrentSession().createCriteria(Transaction.class).add(Restrictions.eq("tillId", tillId)).add(Restrictions.eq("checkId", checkId)).add(Restrictions.eq("isCreditTransaction", isCredit)).add(Restrictions.eq("transactionType", tillTransactionType)).add(Restrictions.ne("status", TillTransactionStatus.CANCELLED.name())).addOrder(Order.desc("transactionTime")).setMaxResults(1).uniqueResult();
        return (Transaction) sessionFactory.getCurrentSession().createCriteria(Transaction.class).add(Restrictions.eq("tillId", tillId)).add(Restrictions.eq("isCreditTransaction", isCredit)).add(Restrictions.eq("transactionType", tillTransactionType)).add(Restrictions.ne("status", TillTransactionStatus.CANCELLED.name())).addOrder(Order.desc("transactionTime")).setMaxResults(1).uniqueResult();
    }
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Transaction> fetchTransactionList(String transactionStatus, boolean flag, int userId, String tillId, Timestamp fromTime,
			Timestamp toTime) {	
		List<Transaction> transactionList=new ArrayList<Transaction>();
		if(flag && transactionStatus !=null)
			transactionList= sessionFactory.getCurrentSession().createCriteria(Transaction.class).add(Restrictions.eq("tillId", tillId)).add(Restrictions.eq("userId", userId)).add(Restrictions.between("transactionTime", fromTime, toTime)).add(Restrictions.eq("status", transactionStatus)).list();
		else if(flag && transactionStatus==null)
			transactionList= sessionFactory.getCurrentSession().createCriteria(Transaction.class).add(Restrictions.eq("tillId", tillId)).add(Restrictions.eq("userId", userId)).add(Restrictions.between("transactionTime", fromTime, toTime)).list();
		else if(!flag && transactionStatus !=null)
			transactionList= sessionFactory.getCurrentSession().createCriteria(Transaction.class).add(Restrictions.eq("tillId", tillId)).add(Restrictions.between("transactionTime", fromTime, toTime)).add(Restrictions.eq("status", transactionStatus)).list();
		else if(!flag && transactionStatus==null)
			transactionList= sessionFactory.getCurrentSession().createCriteria(Transaction.class).add(Restrictions.eq("tillId", tillId)).add(Restrictions.between("transactionTime", fromTime, toTime)).list();
		return transactionList;
	}

    @Override
    public Transaction getTransaction(int checkId, String paymentType, Integer userId, TillTransactionStatus status, boolean isCredit) {
        return (Transaction) sessionFactory.getCurrentSession().createCriteria(Transaction.class).add(Restrictions.eq("checkId", checkId)).add(Restrictions.eq("transactionType", paymentType)).add(Restrictions.eq("status", status.name())).add(Restrictions.eq("isCreditTransaction", isCredit)).uniqueResult();
    }

	@Override
	public void saveTransaction(Transaction transaction) {
		sessionFactory.getCurrentSession().saveOrUpdate(transaction);
	}
	
	@Override
	public void handOverTransactions(String tillId, int fromId, int toId, String transactionStatus, Timestamp fromTime, Timestamp toTime ) {
		Query updateQuery = sessionFactory.getCurrentSession().createSQLQuery("UPDATE SALE_REGISTER_TRANSACTIONS set PREVIOUS_USERID=:fromUserId, userId=:toUserId where tillId=:tillId and userId=:userId and status=:status and TRANSACTION_TIME between :fromTime and :toTime");
		updateQuery.setString("tillId", tillId);
		updateQuery.setString("status", transactionStatus);
		updateQuery.setParameter("fromTime", fromTime);
		updateQuery.setParameter("toTime", toTime);
		updateQuery.setInteger("fromUserId", fromId);
		updateQuery.setInteger("toUserId", toId);
		updateQuery.setInteger("userId", fromId);
		updateQuery.executeUpdate();;
	}
	//************************************************************** Till ************************************************************************************
	@Override
	public void addTill(Till till) {
		sessionFactory.getCurrentSession().saveOrUpdate(till);
	}

	@Override
	public Till getTillByName(String tillName) {
		return (Till) sessionFactory.getCurrentSession().createCriteria(Till.class).add(Restrictions.eq("tillName", tillName)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).uniqueResult();
	} 
	@Override
	public void  updateTillBalance(String tillId, float amount){
		Query updateQuery=sessionFactory.getCurrentSession().createSQLQuery("UPDATE TILLS set balance=balance+"+amount+" where tillId=:tillId");
		updateQuery.setString("tillId", tillId);
		updateQuery.executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getListOfTillIDByFulfillmentcenter(Integer fulfillmentcenterId) {
		return sessionFactory.getCurrentSession().createCriteria(Till.class, "till").createAlias("till.fulfillmentcenter", "fulfillmentcenter").add(Restrictions.eq("fulfillmentcenter.id", fulfillmentcenterId)).setProjection(Projections.property("tillId")).list();	
	}

	@Override
	public Till getTill(String tillId) {
		return (Till) sessionFactory.getCurrentSession().get(Till.class, tillId);
	}

	@Override
	public void updateTill(Till till) {
		sessionFactory.getCurrentSession().saveOrUpdate(till);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Till> getTillByFulfillmentcenter(Integer fulfillmentcenterId) {
		return sessionFactory.getCurrentSession().createCriteria(Till.class, "till").createAlias("till.fulfillmentcenter", "fulfillmentcenter").add(Restrictions.eq("fulfillmentcenter.id", fulfillmentcenterId)).list();	
	}
	@Override
	public Float getCurrentBalance(String tillId) {
		return (Float) sessionFactory.getCurrentSession().createCriteria(Till.class).add(Restrictions.eq("tillId", tillId)).setProjection(Projections.property("balance")).uniqueResult();
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Till> getActiveTillByFulfillmentcenter(Integer fulfillmentCenterId) {
		return sessionFactory.getCurrentSession().createCriteria(Till.class, "till").createAlias("till.fulfillmentcenter", "fulfillmentcenter").add(Restrictions.eq("fulfillmentcenter.id", fulfillmentCenterId)).add(Restrictions.ne("status", "INACTIVE")).list();
	}
	//************************************************************* Handover Request ***************************************************************************************
	@Override
	public void addHandoverRequest(HandoverRequest handoverRequest) {
		sessionFactory.getCurrentSession().saveOrUpdate(handoverRequest);
		sessionFactory.getCurrentSession().flush();
		sessionFactory.getCurrentSession().clear();
	}

	@Override
	public HandoverRequest getHandoverRequest(String tillId, int userId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from HandoverRequest WHERE tillId=:tillId AND userId=:userId order by time DESC");
		query.setParameter("tillId", tillId);
		query.setParameter("userId", userId);
		query.setMaxResults(1);
		return (HandoverRequest) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<HandoverRequest> getHandoverRequestList(String tillId, Integer userId) {
		return (List<HandoverRequest>) sessionFactory.getCurrentSession().createCriteria(HandoverRequest.class).add(Restrictions.eq("tillId", tillId)).add(Restrictions.eq("userId", userId)).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<HandoverRequest> getHandoverRequestList(String tillId) {
		return (List<HandoverRequest>) sessionFactory.getCurrentSession().createCriteria(HandoverRequest.class).add(Restrictions.eq("tillId", tillId)).list();
	}

	@Override
	public HandoverRequest getHandoverRequest(Integer requestId) {
		return (HandoverRequest) sessionFactory.getCurrentSession().get(HandoverRequest.class, requestId);
	}

	@Override
	public HandoverRequest getLastHandoverRequest(String tillId) {
		Query query = sessionFactory.getCurrentSession().createQuery("from HandoverRequest WHERE tillId=:tillId order by handoverId DESC");
		query.setParameter("tillId", tillId);
		query.setMaxResults(1);
		return (HandoverRequest) query.uniqueResult();
	}

	@Override
	public void updateHandoverRequest(HandoverRequest handoverRequest) {
		sessionFactory.getCurrentSession().saveOrUpdate(handoverRequest);
	}

	@Override
	public HandoverRequest getFirstHandoverRequest(String tillId, Timestamp startTime) {
		Query query = sessionFactory.getCurrentSession().createQuery("from HandoverRequest WHERE tillId=:tillId AND time > :startTime order by time ASC");
		query.setParameter("tillId", tillId);
		query.setParameter("startTime", startTime);
		query.setMaxResults(1);
		return (HandoverRequest) query.uniqueResult();
	}
	//******************************************************************************************************************************************************************

	@SuppressWarnings("unchecked")
	@Override
	public List<Transaction> getTransactionListByDateRange(String tillId,
			Date startDate, Date endDate) {
		return sessionFactory.getCurrentSession().createCriteria(Transaction.class).add(Restrictions.eq("tillId", tillId)).add(Restrictions.between("transactionTime", startDate, endDate)).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ConflictedSale> listConfliactedSale(int orgId) {
		Query saleList = sessionFactory.getCurrentSession().createSQLQuery("select r.restaurantId, r.restaurantName,fc.name as fulfillmentCenterName, TM.fulfillmentCenterId, O.orderId, ELT(O.status+1, 'NEW', 'PENDING', 'READY','DELIVERED','PAID','CANCELLED','OUTDELIVERY','CONFIRMDELIVERY','EDITMONEYOUT','EDITMONEYIN') as orderStatus,c.id as checkId,  st.transactionId,st.transaction_type,st.transaction_amount, st.transaction_time,st.status, c.invoiceId, c.bill as orderAmount,ELT(c.status+1, 'Paid', 'Unpaid', 'Readytopay', 'Cancel','Pending') as check_status,  c.phone, c.roundOffTotal from SALE_REGISTER_TRANSACTIONS st, CHECKS c, TILL_MAP TM, FULFILLMENTCENTER fc, RESTAURANT r, ORDERS O where st.status IN (SELECT checkId from SALE_REGISTER_TRANSACTIONS where status=:transactionStatus) AND c.id=st.checkId AND c.status != 4 AND TM.tillId=st.tillId AND O.checkId=c.id AND fc.id=TM.fulfillmentCenterId AND fc.restaurantId=r.restaurantId  AND r.parentRestaurantId=:orgId AND O.status != 6 ORDER BY checkId ASC;");
		saleList.setString("transactionStatus", "PENDING");
		saleList.setInteger("orgId", orgId);
		saleList.setResultTransformer(Transformers.aliasToBean(ConflictedSale.class));
		return saleList.list();
	}

	@Override
	public Transaction getTransaction(String transactionId) {
		return (Transaction) sessionFactory.getCurrentSession().createCriteria(Transaction.class).add(Restrictions.eq("transactionId", transactionId)).add(Restrictions.eq("status", "PENDING")).uniqueResult();
	}

	@Override
	public String getPaymentCategory(Integer orgId, String paymentType) {	
		return (String) sessionFactory.getCurrentSession().createCriteria(PaymentType.class).add(Restrictions.eq("orgId", orgId)).add(Restrictions.eq("name", paymentType)).setProjection(Projections.property("type")).uniqueResult();
	}

	@Override
	public Integer getRestaurantIdOfTill(String tillId) {
		Query query=sessionFactory.getCurrentSession().createSQLQuery("select restaurantId from FULFILLMENTCENTER where id=(select fulfillmentCenterId from TILL_MAP where tillId=:tillId)");
	    query.setString("tillId", tillId);
	   return  (Integer) query.uniqueResult();
	}

	@Override
	public Integer getRestaurantIdOfFFC(Integer fulfillmentcenterId) {
		return (Integer) sessionFactory.getCurrentSession().createCriteria(FulfillmentCenter.class).add(Restrictions.eq("id", fulfillmentcenterId)).setProjection(Projections.property("restaurantId")).uniqueResult();
	}


    @Override
    public List getBalanceSummery(String tillId, Timestamp from, Timestamp to, int userId) {
        Query balanceSummery = sessionFactory.getCurrentSession().createQuery("select sum(transactionAmount) as balance, transactionType, transactionCategory, status, isCreditTransaction as isCredit from Transaction where (status='SUCCESS' or status='PENDING') AND transactionTime>=:startTime AND transactionTime<=:endTime AND tillId=:tillId AND userId=:userId  group by transactionType, transactionCategory, status, isCreditTransaction");
        balanceSummery.setString("tillId", tillId);
		balanceSummery.setTimestamp("startTime", from);
		balanceSummery.setTimestamp("endTime", to);
		balanceSummery.setInteger("userId", userId);
        PaymentHistoryDTO transactionHistory = new PaymentHistoryDTO();
        return balanceSummery.list();
      /*for(Object obj: balanceSummery.list()){
          Object[] details=(Object[]) obj;
    	  boolean found=false;
        	for(PaymentHistoryDTO transactionHis: transactionHistory){
        		if(transactionHis.name.equals(details[1])){
                    PaymentCategoryDTO tran=null;
                    tran=new PaymentCategoryDTO();
                    tran.category = (TransactionCategory) details[2];
                    tran.status=TillTransactionStatus.valueOf((String) details[3]);
					tran.amount=Float.parseFloat(""+details[0]);

					tran.isCreditRecovery=(boolean)details[4];
//                    if (!StringUtility.isNullOrEmpty((String) details[4]))
//						tran.isCreditRecovery = true;
					transactionHis.transactionsSummery.add(tran);
                    found=true;
        		}
        	}
        	if(!found){
        		PaymentHistoryDTO pDTO=new PaymentHistoryDTO();
        		pDTO.name=(String) details[1];
        		PaymentCategoryDTO tran=new PaymentCategoryDTO();
                tran.category = (TransactionCategory) details[2];
                tran.status=TillTransactionStatus.valueOf((String) details[3]);
				tran.amount=Float.parseFloat(""+details[0]);
                tran.isCreditRecovery=(boolean)details[4];
                *//*if (!StringUtility.isNullOrEmpty((String) details[4]))
                    tran.isCreditRecovery = true;*//*
                pDTO.transactionsSummery.add(tran);
                transactionHistory.add(pDTO);
        	}
        }
		return transactionHistory;	*/
	}


}
