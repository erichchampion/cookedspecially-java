package com.cookedspecially.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cookedspecially.dao.UtilityDAO;
import com.cookedspecially.domain.Check;
import com.cookedspecially.domain.Customer;

@Repository
public class UtilityDAOImpl implements UtilityDAO{
	
	@Autowired
	private SessionFactory sessionFactory;

	
	@Override
	public int getLastCustomerId() {
		int customerId = (int) sessionFactory.getCurrentSession().createSQLQuery("SELECT customerId FROM  CUSTOMERS order by customerId DESC").setMaxResults(1).uniqueResult();
		return customerId;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> getAllCustomer(int lowerCount, int upperCount, boolean allFlag){
		SQLQuery sqlQuery=null;
		if(lowerCount>0 && upperCount>0 && !allFlag)
		    sqlQuery = sessionFactory.getCurrentSession().createSQLQuery("SELECT * FROM CUSTOMERS where customerId between "+lowerCount+" AND "+upperCount+" AND phone IS NOT NULL");
		else if(lowerCount>0 && upperCount>0 && allFlag)
		    sqlQuery = sessionFactory.getCurrentSession().createSQLQuery("SELECT * FROM CUSTOMERS where customerId between "+lowerCount+" AND "+upperCount);
		else
		    sqlQuery = sessionFactory.getCurrentSession().createSQLQuery("SELECT * FROM CUSTOMERS");	
		sqlQuery.addEntity(Customer.class);
		return sqlQuery.list();
	}
	@Override
	public void updateData(List<Object> dataList) {
		int batchCount=0;
		for(Object data: dataList){
			sessionFactory.getCurrentSession().saveOrUpdate(data);
			 if ( ++batchCount == 200 ) {
				 sessionFactory.getCurrentSession().flush();
				 sessionFactory.getCurrentSession().clear();
			    }
		}
	}

	@Override
	public void removeCustomer(List<Integer> customerList) {
		int batchCount=0;
		Query query = sessionFactory.getCurrentSession().createQuery("delete Customer where customerId =:customerIds");
		for(Integer customerId: customerList){
			 query.setInteger("customerIds", customerId);
			 query.executeUpdate();
			 if ( ++batchCount == 200 ) {
				 sessionFactory.getCurrentSession().flush();
				 sessionFactory.getCurrentSession().clear();
			    }
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Check> getAllChecks(int lowerCount, int upperCount) {
		SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery("SELECT * FROM CHECKS where id between "+lowerCount+" AND "+upperCount+" AND phone !=0 AND phone IS NOT NULL");
		sqlQuery.addEntity(Check.class);
		return sqlQuery.list();
	}
	@Override
	public Check getLastChecks() {
		Query query = sessionFactory.getCurrentSession().createQuery("FROM Check order by checkId DESC");
		query.setMaxResults(1);
		return (Check) query.uniqueResult(); 
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<String> listDuplicateCustomer(Integer orgId) {
		//SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery("SELECT  a.phone, b.totalCount AS Duplicate FROM CUSTOMERS a INNER JOIN (SELECT  phone, COUNT(*) totalCount FROM CUSTOMERS GROUP BY phone) b ON a.phone = b.phone WHERE b.totalCount >=2");
		SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery("SELECT  a.phone AS Duplicate FROM CUSTOMERS a INNER JOIN (SELECT  phone,orgId, COUNT(*) totalCount FROM CUSTOMERS GROUP BY phone) b ON a.phone = b.phone AND a.orgId=b.orgId WHERE b.totalCount >=2");
//		HashMap<String, Integer> result=new HashMap<>();
//		@SuppressWarnings("rawtypes")
//		Iterator iterator=sqlQuery.list().iterator();
//		while(iterator.hasNext()){
//			Object[] obj=(Object[]) iterator.next();
//			BigInteger bigInt=(BigInteger) obj[1];
//			result.put((String)obj[0], (int)bigInt.intValue());
//		}
		
		return sqlQuery.list();
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> listAllDuplicateCustomer(String mobileNo) {
		return sessionFactory.getCurrentSession().createCriteria(Customer.class).add(Restrictions.eq("phone", mobileNo)).addOrder(Order.asc("customerId")).list();
	}
	
	@Override
	public void updateCustomerDetails(HashMap<Integer, Set<Integer>> duplicateCustomer){
		String updateAddress = "UPDATE CustomerAddress set customerId = :newCustomerId where customerId IN (:oldCustomerIds)";
		String updateCheck = "UPDATE Check set customerId = :newCustomerId where customerId IN (:oldCustomerIds)";
		for(Integer customerId: duplicateCustomer.keySet()){
			 Query updateAddressQuery = sessionFactory.getCurrentSession().createQuery(updateAddress);
			 updateAddressQuery.setInteger("newCustomerId", customerId);
			 updateAddressQuery.setParameterList("oldCustomerIds", duplicateCustomer.get(customerId));
			 Query updateCheckQuery = sessionFactory.getCurrentSession().createQuery(updateCheck);
			 updateCheckQuery.setInteger("newCustomerId", customerId);
			 updateCheckQuery.setParameterList("oldCustomerIds", duplicateCustomer.get(customerId));
			 updateAddressQuery.executeUpdate();
			 sessionFactory.getCurrentSession().clear();
			 updateCheckQuery.executeUpdate();
		}
	}
}
