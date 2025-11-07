/**
 * 
 */
package com.cookedspecially.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cookedspecially.dao.DishTypeDAO;
import com.cookedspecially.dao.TaxTypeDAO;
import com.cookedspecially.domain.DishType;
import com.cookedspecially.domain.TaxType;
import com.cookedspecially.enums.Status;

/**
 * @author rahul
 *
 */
@Repository
public class TaxTypeDAOImpl implements TaxTypeDAO {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void addTaxType(TaxType taxType) {
		sessionFactory.getCurrentSession().saveOrUpdate(taxType);
	}

	@Override
	public List<TaxType> listTaxTypes() {
		return sessionFactory.getCurrentSession().createCriteria(TaxType.class).list();
	}

	@Override
	public List<TaxType> listTaxTypesByRestaurantId(Integer restaurantId) {
		return sessionFactory.getCurrentSession().createCriteria(TaxType.class).add(Restrictions.and(Restrictions.eq("restaurantId", restaurantId),Restrictions.eq("status", Status.ACTIVE))).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

	@Override
	public void removeTaxType(Integer id) {
		TaxType TaxType = (TaxType) sessionFactory.getCurrentSession().load(TaxType.class, id);
		if (null != TaxType) {
			TaxType.setStatus(Status.INACTIVE);
			sessionFactory.getCurrentSession().saveOrUpdate(TaxType);
			}
	}

	
	
	@Override
	public TaxType getTaxType(Integer id) {
		return (TaxType)sessionFactory.getCurrentSession().get(TaxType.class, id);
	}

	@Override
	public TaxType getTaxTypeByName(String name, Integer restId) {
		// TODO Auto-generated method stub
		 return (TaxType) sessionFactory.getCurrentSession().createCriteria(TaxType.class).add(Restrictions.and(Restrictions.eq("restaurantId", restId),Restrictions.eq("name",name))).uniqueResult();
	}

	@Override
	public List<TaxType> listAllActiveInactiveTaxesByRestaurantId(
			Integer restaurantId) {
		// TODO Auto-generated method stub
		 return sessionFactory.getCurrentSession().createCriteria(TaxType.class).add(Restrictions.eq("restaurantId", restaurantId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();

	}

}
