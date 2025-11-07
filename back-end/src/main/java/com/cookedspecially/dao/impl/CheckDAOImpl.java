/**
 * 
 */
package com.cookedspecially.dao.impl;


import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cookedspecially.dao.CheckDAO;
import com.cookedspecially.domain.Check;
import com.cookedspecially.domain.Customer;
import com.cookedspecially.domain.Dish;
import com.cookedspecially.domain.EditChecks;
import com.cookedspecially.domain.OrderDish;
import com.cookedspecially.enums.check.Status;
import com.cookedspecially.utility.StringUtility;

/**
 * @author shashank
 *
 */
@Repository
public class CheckDAOImpl implements CheckDAO {

	final static Logger logger = Logger.getLogger(CheckDAOImpl.class);
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void addCheck(Check check) {
		sessionFactory.getCurrentSession().saveOrUpdate(check);
	}

	@Override
	public void removeCheck(Integer id) {
		Check check = (Check) sessionFactory.getCurrentSession().load(Check.class, id);
		if (null != check) {
			sessionFactory.getCurrentSession().delete(check);
		}		
	}

	@Override
	public Check getCheck(Integer id) {
		return (Check) sessionFactory.getCurrentSession().get(Check.class, id);
	}

	@Override
	public List<Check> getCheckByInvoiceId(String invoiceId) {
		return sessionFactory.getCurrentSession().createCriteria(Check.class).add(Restrictions.eq("invoiceId", invoiceId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}
	
	@Override
	public Check getCheckByTableId(Integer restaurantId, Integer tableId) {
		List<Check> checksForTable = sessionFactory.getCurrentSession().createCriteria(Check.class).add(Restrictions.and(Restrictions.eq("tableId", tableId), Restrictions.eq("restaurantId", restaurantId), Restrictions.ne("status", Status.Paid), Restrictions.ne("status", Status.Cancel))).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
		if (checksForTable != null && checksForTable.size() > 0) {
			return checksForTable.get(0);
		} else {
			return null;
		}
	}

	@Override
	public Check getCheckByCustId(Integer restaurantId, Integer custId) {
		List<Check> checksForTable = sessionFactory.getCurrentSession().createCriteria(Check.class).add(Restrictions.and(Restrictions.eq("customerId", custId), Restrictions.eq("restaurantId", restaurantId),Restrictions.ne("status", Status.Pending), Restrictions.ne("status", Status.Paid),Restrictions.ne("status", Status.Unpaid), Restrictions.ne("status", Status.Cancel))).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
		if (checksForTable != null && checksForTable.size() > 0) {
			return checksForTable.get(0);
		} else {
			return null;
		}
	}

	
	@Override
	public List<Check> getAllOpenChecks(Integer restaurantId) {
		return sessionFactory.getCurrentSession().createCriteria(Check.class).add(Restrictions.and(Restrictions.eq("restaurantId", restaurantId), Restrictions.ne("status", Status.Paid),Restrictions.ne("status", Status.Pending), Restrictions.ne("status", Status.Cancel))).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}
	
	@Override
	public List<Integer> getAllCheckIds() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Check.class);
		criteria.setProjection( Projections.projectionList().add( Projections.property("checkId")));

		List<Integer> ids=criteria.list();
		return ids;
	}
	
	@Override
	public List getClosedChecksByDate(Integer restaurantId, Date startDate, Date endDate) {
		//return sessionFactory.getCurrentSession().createCriteria(Check.class).add(Restrictions.and(Restrictions.eq("restaurantId", restaurantId), Restrictions.ne("status", Status.Unpaid), Restrictions.ne("status", Status.Cancel), Restrictions.between("closeTime", startDate, endDate))).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
		String sqlQuery = "SELECT sum(bill), checkType FROM Check WHERE restaurantId = :restaurantId AND closeTime > :startDate AND closeTime < :endDate group by checkType)";
		Query query = sessionFactory.getCurrentSession().createQuery(sqlQuery).setParameter("restaurantId", restaurantId).setParameter("startDate", startDate).setParameter("endDate", endDate);
		return query.list();

	}
	
	@Override
	public List<Check> getDailyInvoice(Integer restaurantId, Date startDate, Date endDate) {
		return sessionFactory.getCurrentSession().createCriteria(Check.class).add(Restrictions.and(Restrictions.eq("restaurantId", restaurantId), Restrictions.gt("deliveryTime", startDate), Restrictions.lt("deliveryTime", endDate))).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}
	
	@Override
	public List<String> getUniqueDishTypes(Integer restaurantId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderDish.class);
		criteria.setProjection( Projections.distinct(Projections.property("dishType")));

		List<String> ids=criteria.list();
		return ids;
		
	}
	
	@Override
	public List getDailySalesRecords(Integer restaurantId, Date startDate) {
		String sqlQuery = "select sum(c.bill), c.checkType, i.dishType, sum(i.price) FROM (SELECT bill, checkType FROM Check WHERE restaurantId = :restaurantId AND openTime > :startDate) c JOIN Check.orders o JOIN o.orderDishes i group by c.checkType, i.dishType)";
		Query query = sessionFactory.getCurrentSession().createQuery(sqlQuery).setParameter("restaurantId", restaurantId).setParameter("startDate", startDate);
		return query.list();
	}
	
	@Override
	public List<Check> getAllChecks(List<Integer> ids) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Check.class);
		criteria.add(Restrictions.in("id", ids));
		return criteria.list();
	}
	
	@Override
	public List getMonthlyBillSummary(Integer restaurantId, Date startDate, Date endDate) {
		String sqlQuery = "SELECT sum(bill) as totalBill, sum(additionalChargesValue1) as totalTax1, sum(additionalChargesValue2) as totalTax2, sum(additionalChargesValue3) as totalTax3, sum(bill+additionalChargesValue1+additionalChargesValue2+additionalChargesValue3) as totalInclTaxes FROM Check WHERE restaurantId = :restaurantId AND openTime >= :startDate AND openTime <= :endDate";
		Query query = sessionFactory.getCurrentSession().createQuery(sqlQuery).setParameter("restaurantId", restaurantId).setParameter("startDate", startDate).setParameter("endDate", endDate);
		return query.list();
	}

	@Override
	public List<Check> getCustomersCheckList(String phone, Integer custId,Integer restId) {
		// TODO Auto-generated method stub
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Check.class);
		if (restId != null && restId > 0) {
			criteria = criteria.add(Restrictions.eq("restaurantId", restId));
		}
		boolean disjunctionPresent = false;
		Disjunction disjunction = Restrictions.disjunction();
		if (custId != null && custId > 0) {
			disjunction.add(Restrictions.eq("customerId", custId));
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
	public List<Check> getCustomersCheckListByYear(String phone,
			Integer custId, Integer restId,Date startDate,Date endDate) {
		/*String sqlQuery = "from Check WHERE restaurantId = :restaurantId AND closeTime>:startDate AND closeTime <:endDate AND phone:phone AND Or )";
		Query query = sessionFactory.getCurrentSession().createQuery(sqlQuery).setParameter("restaurantId", restaurantId).setParameter("startDate", startDate).setParameter("endDate",endDate);
		return query.list();*/
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Check.class);
		if (restId != null && restId > 0) {
			criteria = criteria.add(Restrictions.eq("restaurantId", restId));
		}
		if (startDate!= null ) {
			criteria = criteria.add(Restrictions.gt("closeTime", startDate));
		}
		if (endDate!= null ) {
			criteria = criteria.add(Restrictions.le("closeTime", endDate));
		}
		boolean disjunctionPresent = false;
		Disjunction disjunction = Restrictions.disjunction();
		if (custId != null && custId > 0) {
			disjunction.add(Restrictions.eq("customerId", custId));
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
	public List<Check> getAllCheckIdByRestaurantId(Integer restId) {
		// TODO Auto-generated method stub
		return sessionFactory.getCurrentSession().createCriteria(Check.class).add(Restrictions.eq("restaurantId", restId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

	@Override
	public List<Check> getDailyInvoiceByFfc(Integer ffcId, Date startTime,Date endTime) {
		// TODO Auto-generated method stub
		return sessionFactory.getCurrentSession().createCriteria(Check.class).add(Restrictions.and(Restrictions.eq("kitchenScreenId", ffcId), Restrictions.gt("deliveryTime", startTime), Restrictions.lt("deliveryTime", endTime), Restrictions.ne("status", Status.Cancel))).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();

	}

	@Override
	public void addEditCheck(EditChecks editCheck) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().saveOrUpdate(editCheck);
	}

	@Override
	public void removeEditCheck(Integer id) {
		// TODO Auto-generated method stub
		EditChecks editCheck = (EditChecks) sessionFactory.getCurrentSession().load(Check.class, id);
		if (null != editCheck) {
			sessionFactory.getCurrentSession().delete(editCheck);
		}	
	}

	@Override
	public EditChecks getEditCheck(Integer id) {
		// TODO Auto-generated method stub
		return (EditChecks) sessionFactory.getCurrentSession().get(Check.class, id);
	}

	@Override
	public List<EditChecks> getEditCheckListBycheckId(Integer checkId) {
		// TODO Auto-generated method stub
		return sessionFactory.getCurrentSession().createCriteria(EditChecks.class).add(Restrictions.eq("checkId",checkId)).list();

	}

	@Override
	public Check getZomatoOrderById(Integer orderId) {
		// TODO Auto-generated method stub
		return (Check) sessionFactory.getCurrentSession().createCriteria(Check.class).add(Restrictions.eq("zomatoOrderId",orderId)).uniqueResult();

	}
}
