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

import com.cookedspecially.dao.AddOnDishDAO;
import com.cookedspecially.dao.DishAddOnDAO;
import com.cookedspecially.dao.DishDAO;
import com.cookedspecially.domain.AddOnDish;
import com.cookedspecially.domain.Dish;
import com.cookedspecially.domain.DishAddOn;

/**
 * @author rahul
 *
 */
@Repository
public class DishAddOnDAOImpl implements DishAddOnDAO {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void addDish(DishAddOn dish) {
		sessionFactory.getCurrentSession().saveOrUpdate(dish);

	}

	@Override
	public void updateMenuModificationTime(Integer dishId) {
		//List<Integer> menuIds = sessionFactory.getCurrentSession().createSQLQuery("select distinct(menuId) FROM MENU_SECTION JOIN (select distinct(sectionId) as sectionId FROM SECTION_DISH where dishId=" + dishId + " ) sec where sec.sectionId = MENU_SECTION.sectionId").list();
		sessionFactory.getCurrentSession().createSQLQuery("update MENUS SET modifiedTime=CURRENT_TIMESTAMP() where menuId IN (select distinct(menuId) FROM MENU_SECTION JOIN (select distinct(sectionId) as sectionId FROM SECTION_DISH where dishId=" + dishId + " ) sec where sec.sectionId = MENU_SECTION.sectionId)").executeUpdate();
		
		
	}
	@Override
	public List<DishAddOn> listDishAddOn(Integer restaurantId) {
		return sessionFactory.getCurrentSession().createQuery("from DishAddOn").list();
	}

	@Override
	public List<DishAddOn> listDishAddOnByDish(Integer dishId) {
		return sessionFactory.getCurrentSession().createCriteria(DishAddOn.class).add(Restrictions.eq("dishId", dishId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

	@Override
	public void removeDishAddOn(Integer id) throws Exception{
		
		sessionFactory.getCurrentSession().createSQLQuery("SET SQL_SAFE_UPDATES=0;").executeUpdate();
		sessionFactory.getCurrentSession().createSQLQuery("DELETE  FROM DISHADDON WHERE  dishId=" + id ).executeUpdate();
	}
/*
	@Override
	public List<AddOnDish> getDishes(Integer[] ids) {
		return (List<AddOnDish>) sessionFactory.getCurrentSession().createCriteria(AddOnDish.class).add(Restrictions.in("dishId", ids)).list();

	}

	@Override
	public AddOnDish getDish(Integer id) {
		return (AddOnDish) sessionFactory.getCurrentSession().get(AddOnDish.class, id);
	}*/
	@Override
	public List<AddOnDish> listDishAddOnByRestaurant(Integer restaurantId) {
		return sessionFactory.getCurrentSession().createCriteria(AddOnDish.class).add(Restrictions.eq("restaurantId", restaurantId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

	@Override
	public List<DishAddOn> listDishAddOnByFulfillmentCenter(Integer fulfilllmentCenterId) {
		//return sessionFactory.getCurrentSession().createCriteria(DishAddOn.class).add(Restrictions.eq("fulfillmentCenterId", fulfilllmentCenterId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	  return null;
	}
}
