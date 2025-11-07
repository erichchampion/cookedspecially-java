/**
 * 
 */
package com.cookedspecially.dao.impl;

import com.cookedspecially.dao.OrderDAO;
import com.cookedspecially.domain.Order;
import com.cookedspecially.enums.check.PaymentMode;
import com.cookedspecially.enums.order.Status;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author shashank
 *
 */
@Repository
public class OrderDAOImpl implements OrderDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<Order> listOrders(Map<String, Object> queryMap) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Order.class).add(Restrictions.allEq(queryMap));
		return criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();

//		String hql = "from Order where";
//		int i = 0;
//		for (Entry<String, Object> entry : queryMap.entrySet()) {
//			if (i > 0) {
//				hql += " and ";
//			}
//			hql += " " + entry.getKey() + " = :" + entry.getKey();
//			i++;
//		}
//		Query query = sessionFactory.getCurrentSession().createQuery(hql);
//		for (Entry<String, Object> entry : queryMap.entrySet()) {
//			query.setParameter(entry.getKey(), entry.getValue());
//		}
//		return query.list();
	}

	@Override
	public void addOrder(Order order) {
		sessionFactory.getCurrentSession().saveOrUpdate(order);

	}

	@Override
	public void removeOrder(Integer id) throws Exception {
		Order order = (Order) sessionFactory.getCurrentSession().load(Order.class, id);
		if (order != null) {
			sessionFactory.getCurrentSession().delete(order);
		}
	}

	@Override
	public Order getOrder(Integer id) {
		return (Order) sessionFactory.getCurrentSession().get(Order.class, id);
	}

	@Override
	public List<Integer> getAllOpenOrderCheckIds(Integer restaurantId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Order.class).add(Restrictions.and(Restrictions.eq("restaurantId", restaurantId), Restrictions.ne("status", Status.PAID), Restrictions.ne("status", Status.CANCELLED), Restrictions.ne("status", Status.DELIVERED)));
		criteria.setProjection( Projections.projectionList().add( Projections.property("checkId")));

		List<Integer> ids=criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
		return ids;
	}

	/*public List<Order> getDailyDeliveryBoyInvoice(Integer restaurantId,
			Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return sessionFactory.getCurrentSession().createCriteria(Order.class).add(Restrictions.and(Restrictions.eq("restaurantId", restaurantId), Restrictions.gt("openTime", startDate), Restrictions.lt("openTime", endDate), Restrictions.ne("status", Status.CANCELLED))).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}*/
	@Override
	public List<Order> getOpenOrders(Map queryMap){

		String sql = "Select id from Check where deliveryTime >= :fromDate and deliveryTime <= :toDate";
		Query query = sessionFactory.getCurrentSession().createQuery(sql).setParameter("fromDate", queryMap.get("fromDate")).setParameter("toDate", queryMap.get("toDate"));
		List ids = query.list();

		if (ids.size() > 0) {

			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Order.class).add(Restrictions.and(
					Restrictions.eq("restaurantId", queryMap.get("restaurantId")),
					Restrictions.in("checkId", ids),
					Restrictions.in("status", (List) queryMap.get("status"))));
			return criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
		}
		return new ArrayList<Order>();
	}

	@Override
	public List<Order> getOpenDispatchedCancelOrders(Map queryMap) {
		String sql = "Select id from Check where deliveryTime >= :fromDate and deliveryTime <= :toDate";
		Query query = sessionFactory.getCurrentSession().createQuery(sql).setParameter("fromDate", queryMap.get("fromDate")).setParameter("toDate", queryMap.get("toDate"));
		List ids = query.list();

		if (ids.size() > 0) {

			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Order.class).add(Restrictions.and(
					Restrictions.eq("restaurantId", queryMap.get("restaurantId")),
					Restrictions.in("checkId", ids),
					Restrictions.gt("moneyOut",0.0f),
					Restrictions.eq("moneyIn",0.0f),
					Restrictions.in("status", (List) queryMap.get("status"))));
					
			return criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
		}
		return new ArrayList<Order>();
	}
}
