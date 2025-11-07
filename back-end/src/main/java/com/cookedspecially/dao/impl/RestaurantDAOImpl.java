/**
 * 
 */
package com.cookedspecially.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cookedspecially.dao.RestaurantDAO;
import com.cookedspecially.domain.Discount_Charges;
import com.cookedspecially.domain.DishType;
import com.cookedspecially.domain.Nutrientes;
import com.cookedspecially.domain.OrderSource;
import com.cookedspecially.domain.PaymentType;
import com.cookedspecially.domain.Restaurant;
import com.cookedspecially.domain.TaxType;
import com.cookedspecially.domain.User;
import com.cookedspecially.enums.Status;

/**
 * @author shashank
 *
 */
@Repository
public class RestaurantDAOImpl implements RestaurantDAO {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void addRestaurant(Restaurant restaurant) {
		sessionFactory.getCurrentSession().saveOrUpdate(restaurant);
	}

	@Override
	public List<Restaurant> listRestaurant() {
		return sessionFactory.getCurrentSession().createQuery("from Restaurant").list();
	}

	@Override
	public List<Restaurant> listRestaurantById(Integer restaurantId) {
		return sessionFactory.getCurrentSession().createCriteria(Restaurant.class).add(Restrictions.eq("restaurantId", restaurantId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

	@Override
	public List<Restaurant> listRestaurantByParentId(Integer parentId) {
		return sessionFactory.getCurrentSession().createCriteria(Restaurant.class).add(Restrictions.eq("parentRestaurantId", parentId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}
	
	@Override
	public void removeRestaurant(Integer id) {
		Restaurant restaurant = (Restaurant) sessionFactory.getCurrentSession().load(Restaurant.class, id);
		if (null != restaurant) {
			sessionFactory.getCurrentSession().delete(restaurant);
		}
	}

	@Override
	public Restaurant getRestaurant(Integer id) {
		return (Restaurant) sessionFactory.getCurrentSession().get(Restaurant.class, id);
	}

	@Override
	public void saveResaurant(Restaurant rest) {
		sessionFactory.getCurrentSession().saveOrUpdate(rest);
	}

	@Override
	public Restaurant getRestaurantByName(String restaurantName) {
		return (Restaurant) sessionFactory.getCurrentSession().createCriteria(Restaurant.class).add(Restrictions.eq("restaurantName", restaurantName)).uniqueResult();
	}

	@Override
	public List<Discount_Charges> listDiscountCharges(Integer restId) {
		// TODO Auto-generated method stub
	 return sessionFactory.getCurrentSession().createCriteria(Discount_Charges.class).add(Restrictions.eq("restaurantId", restId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();

	}

	@Override
	public void addDC(Discount_Charges discount_Charges) {
		sessionFactory.getCurrentSession().saveOrUpdate(discount_Charges);
		
	}

	@Override
	public void removeDC(Integer id) {
		Discount_Charges dc  =  (Discount_Charges) sessionFactory.getCurrentSession().load(Discount_Charges.class, id);
		if (null != dc) {
			sessionFactory.getCurrentSession().delete(dc);
			}
		
	}

	@Override
	public Discount_Charges getDCById(Integer id) {
		// TODO Auto-generated method stub
		return (Discount_Charges) sessionFactory.getCurrentSession().get(Discount_Charges.class, id);
	}

	@Override
	public List<Nutrientes> getNutirentList(Integer restId) {
		// TODO Auto-generated method stub
		return sessionFactory.getCurrentSession().createCriteria(Nutrientes.class).add(Restrictions.eq("restaurantId",restId)).list();
	}


	@Override
	public void removeNutrientes(Integer id) {
		// TODO Auto-generated method stub
		Nutrientes nutrientes = (Nutrientes) sessionFactory.getCurrentSession().load(Nutrientes.class, id);
		if(nutrientes !=null){
			sessionFactory.getCurrentSession().delete(nutrientes);
		}
	}

	@Override
	public Nutrientes getNutrientes(Integer id) {
		// TODO Auto-generated method stub
		return (Nutrientes) sessionFactory.getCurrentSession().get(Nutrientes.class,id);
	}

	@Override
	public void addNutrientes(Nutrientes nutrientes) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().saveOrUpdate(nutrientes);
		
	}

	@Override
	public Nutrientes getByNutrientesByNameType(String name, String dishType,
			Integer restId) {
		// TODO Auto-generated method stub
		return (Nutrientes) sessionFactory.getCurrentSession().createCriteria(Nutrientes.class).add(Restrictions.and(Restrictions.eq("restaurantId",restId), Restrictions.eq("name", name), Restrictions.eq("dishType", dishType))).uniqueResult();
	}

	
	
	@Override
	public void addOrderSource(OrderSource orderSource) {
		sessionFactory.getCurrentSession().saveOrUpdate(orderSource);
		
	}

	@Override
	public List<OrderSource> listOrderSourcesByOrgId(Integer orgId) {
		// TODO Auto-generated method stub
		return sessionFactory.getCurrentSession().createCriteria(OrderSource.class).add(Restrictions.eq("orgId", orgId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

	@Override
	public void removeOrderSources(Integer id) {
		// TODO Auto-generated method stub
		OrderSource orderSource = (OrderSource) sessionFactory.getCurrentSession().load(OrderSource.class, id);
		if (null != orderSource ) {
			sessionFactory.getCurrentSession().delete(orderSource);
		}
	}

	@Override
	public OrderSource getOrderSources(Integer id) {
		// TODO Auto-generated method stub
		return (OrderSource)sessionFactory.getCurrentSession().get(OrderSource.class, id);
	}

	@Override
	public void addPaymentType(PaymentType paymentType) {
		sessionFactory.getCurrentSession().saveOrUpdate(paymentType);
		
	}

	@Override
	public List<PaymentType> listPaymentTypeByOrgId(Integer orgId) {
		return sessionFactory.getCurrentSession().createCriteria(PaymentType.class).add(Restrictions.eq("orgId", orgId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

	@Override
	public void removePaymentType(Integer id) {
		// TODO Auto-generated method stub
		PaymentType paymentType= (PaymentType) sessionFactory.getCurrentSession().load(PaymentType.class, id);
		if (null != paymentType) {
			sessionFactory.getCurrentSession().delete(paymentType);
		}
		
	}

	@Override
	public PaymentType getPaymentType(Integer id) {
		// TODO Auto-generated method stub
		return (PaymentType)sessionFactory.getCurrentSession().get(PaymentType.class, id);
		
	}
	@Override
	public PaymentType getPaymentTypeByName(String paymentTypeName, int orgId) {
		Query query=sessionFactory.getCurrentSession().createQuery("from PAYMENTTYPE where orgId=:restId AND name=:name");
		query.setParameter("name", paymentTypeName);
		query.setInteger("restId", orgId);
		query.setMaxResults(1);
        return (PaymentType) query.uniqueResult();		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getAllOrganisation() {
		return sessionFactory.getCurrentSession().createSQLQuery("SELECT r.restaurantId FROM  RESTAURANT r where r.parentRestaurantId IS NULL or r.restaurantId=r.parentRestaurantId").list();
	}

	@Override
	public String getRestaurantUnitInfoForAssociatedCustomer(int customerId, String property) {
		return (String) sessionFactory.getCurrentSession().createSQLQuery("SELECT org."+property+" from RESTAURANT org, CUSTOMERS customer where org.restaurantId=customer.orgId AND customer.customerId="+customerId).uniqueResult();
	}
	
}
