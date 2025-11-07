/**
 * 
 */
package com.cookedspecially.dao.impl;

import com.cookedspecially.dao.CustomerDAO;
import com.cookedspecially.domain.Customer;
import com.cookedspecially.domain.CustomerAddress;
import com.cookedspecially.domain.OTP;
import com.cookedspecially.utility.StringUtility;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author shashank, Abhishek, rahul
 *
 */
@Repository
public class CustomerDAOImpl implements CustomerDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void addCustomer(Customer customer) {
		sessionFactory.getCurrentSession().saveOrUpdate(customer);
	}
	
	@Override
	public void removeCustomer(Integer id) throws Exception {
		Customer customer = (Customer) sessionFactory.getCurrentSession().load(Customer.class, id);
		if (null != customer) {
			sessionFactory.getCurrentSession().delete(customer);
		}
	}

	@Override
	public Customer getCustomer(Integer id) {
		return (Customer)sessionFactory.getCurrentSession().get(Customer.class, id);
	}
	
	/*public List<CustomerAddress> getCustomerAddress(int custId){
		return sessionFactory.getCurrentSession().createQuery("from CustomerAddress where customerId="+custId).list();
	}*/
	
	@Override
	public List<Customer> getCustomerByParams(Integer custId, String email, String phone, Integer orgId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Customer.class);
		if (orgId != null && orgId > 0) {
			criteria = criteria.add(Restrictions.eq("orgId", orgId));
		}
		boolean disjunctionPresent = false;
		Disjunction disjunction = Restrictions.disjunction();
		if (custId != null && custId > 0) {
			disjunction.add(Restrictions.eq("customerId", custId));
			disjunctionPresent = true;
		}
//		if (restaurantId != null && restaurantId > 0) {
//			disjunction.add(Restrictions.eq("restaurantId", restaurantId));
//		}
		if (!StringUtility.isNullOrEmpty(email)) {
			disjunction.add(Restrictions.eq("email", email));
			disjunctionPresent = true;
		}
		if (!StringUtility.isNullOrEmpty(phone)) {
			disjunction.add(Restrictions.eq("phone", phone));
			disjunctionPresent = true;
		}
		if (disjunctionPresent) {
			criteria = criteria.add(disjunction);
		}
		return criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}
	
	@Override
	public void addCustomerAddress(CustomerAddress customerAddress) {
		sessionFactory.getCurrentSession().save(customerAddress);
	}
	
	@Override
	public void updateCustomerAddress(CustomerAddress customerAddress) {
		sessionFactory.getCurrentSession().update(customerAddress);
	}


	@Override
	public void removeCustomerAddress(Integer customerId) {
		CustomerAddress customerAddress = (CustomerAddress)sessionFactory.getCurrentSession().load(CustomerAddress.class, customerId);
		if(customerAddress !=null){
			sessionFactory.getCurrentSession().delete(customerAddress);
		}
	}

	@Override
	public List<CustomerAddress> getCustomerAddress(Integer customerId) {
		List<CustomerAddress> dsd =  (List<CustomerAddress>) sessionFactory.getCurrentSession().createQuery("from CustomerAddress where customerId="+customerId).list();
		return dsd;
	}

	@Override
	public Customer getCustomer(String mobileNumber,Integer orgId) {
		return (Customer) sessionFactory.getCurrentSession().createCriteria(Customer.class).add(Restrictions.and(Restrictions.eq("phone", mobileNumber),Restrictions.eq("orgId",orgId))).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).uniqueResult();
	}
	
	/* old */
	@Override
	public OTP getOTP(String mobileNumber) {
		return (OTP) sessionFactory.getCurrentSession().createCriteria(OTP.class).add(Restrictions.eq("mobileNumber", mobileNumber)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).uniqueResult();
	}
	
	@Override
	public OTP getOTP(String mobileNumber, int orgId) {
		return (OTP) sessionFactory.getCurrentSession().createCriteria(OTP.class).add(Restrictions.eq("mobileNumber", mobileNumber)).add(Restrictions.eq("orgID", orgId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).uniqueResult();
	}
	@Override
	public void removeOTP(String mobileNo, int orgID){
		Query query = sessionFactory.getCurrentSession().createQuery("delete OTP where orgID=:orgId AND mobileNumber=:mobileNo");
		query.setString("mobileNo",mobileNo);
		query.setInteger("orgId", orgID);
		query.executeUpdate();
	}

    @Override
    public void deleteCustomer(Integer customerId) {
        Query query = sessionFactory.getCurrentSession().createQuery("delete Customer where customerId = :ID");
        query.setParameter("ID", customerId);
        query.executeUpdate();
    }
    //------------------------------------

	@Override
	public void saveOrUpdateOTP(OTP otp) {
		sessionFactory.getCurrentSession().saveOrUpdate(otp);
	}

	@Override
	public Customer getCustomerByFacebookId(String facebookId) {
		return (Customer) sessionFactory.getCurrentSession().createCriteria(Customer.class).add(Restrictions.eq("facebookId", facebookId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).uniqueResult();
	}

	@Override
	public void removeOTP(String phone) {
		sessionFactory.getCurrentSession().delete(getOTP(phone));	
	}

	@Override
	public List<Customer> getCustomerByDate(Integer orgId,Integer restaurantId,
			Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return  sessionFactory.getCurrentSession().createCriteria(Customer.class).add(Restrictions.and(Restrictions.eq("orgId", orgId), Restrictions.gt("createdTime", startDate), Restrictions.lt("createdTime", endDate))).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();

	}

	@Override
	public List<Customer> getCustomerById(Integer id) {
		return sessionFactory.getCurrentSession().createQuery("from Customer where customerId="+id).list();
	}

	@Override
	public CustomerAddress getCustomerAddressById(int id) {
		return (CustomerAddress) sessionFactory.getCurrentSession().createQuery("from CustomerAddress where id="+id).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> listCustomerInRestaurant(int restaurantId) {
		return (List<Customer>) sessionFactory.getCurrentSession().createQuery("from Customer where restaurantId="+restaurantId).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> listCustomerByMobile(int restaurantId, List<String> mobileNo) {
		Query query=sessionFactory.getCurrentSession().createQuery("from Customer where restaurantId="+restaurantId+" AND phone IN :listMobileNo");
		query.setParameterList("listMobileNo", mobileNo);
		return query.list();
	}

    @Override
    public Customer getCustomerByInvoiceId(String invoiceId) {
        Query query = sessionFactory.getCurrentSession().createQuery("select C from Customer C, Check CK where CK.customerId=C.customerId AND CK.invoiceId=:invoiceId");
        query.setString("invoiceId", invoiceId);
        query.setMaxResults(1);
        return (Customer) query.uniqueResult();
    }
}
