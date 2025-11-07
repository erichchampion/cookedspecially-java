/**
 * 
 */
package com.cookedspecially.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cookedspecially.dao.AddOnDishTypeDAO;
import com.cookedspecially.dao.DishTypeDAO;
import com.cookedspecially.domain.AddOnDishType;
import com.cookedspecially.domain.DishType;

/**
 * @author Rahul
 *
 */
@Repository
public class AddOnDishTypeDAOImpl implements AddOnDishTypeDAO {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void addDishType(AddOnDishType dishType) {
		sessionFactory.getCurrentSession().saveOrUpdate(dishType);
	}

	@Override
	public List<AddOnDishType> listDishTypes() {
		return sessionFactory.getCurrentSession().createCriteria(AddOnDishType.class).list();
	}

	@Override
	public List<AddOnDishType> listDishTypesByRestaurantId(Integer restaurantId) {
		return sessionFactory.getCurrentSession().createCriteria(AddOnDishType.class).add(Restrictions.eq("restaurantId", restaurantId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

	@Override
	public void removeDishType(Integer id) {
		AddOnDishType dishType = (AddOnDishType) sessionFactory.getCurrentSession().load(AddOnDishType.class, id);
		if (null != dishType) {
			sessionFactory.getCurrentSession().delete(dishType);
		}
	}

	@Override
	public AddOnDishType getDishType(Integer id) {
		return (AddOnDishType)sessionFactory.getCurrentSession().get(AddOnDishType.class, id);
	}

}
