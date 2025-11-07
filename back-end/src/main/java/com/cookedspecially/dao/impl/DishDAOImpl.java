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

import com.cookedspecially.dao.DishDAO;
import com.cookedspecially.domain.Dish;

/**
 * @author sagarwal
 *
 */
@Repository
public class DishDAOImpl implements DishDAO {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void addDish(Dish dish) {
		sessionFactory.getCurrentSession().saveOrUpdate(dish);

	}

	@Override
	public void updateMenuModificationTime(Integer dishId) {
		//List<Integer> menuIds = sessionFactory.getCurrentSession().createSQLQuery("select distinct(menuId) FROM MENU_SECTION JOIN (select distinct(sectionId) as sectionId FROM SECTION_DISH where dishId=" + dishId + " ) sec where sec.sectionId = MENU_SECTION.sectionId").list();
		sessionFactory.getCurrentSession().createSQLQuery("update MENUS SET modifiedTime=CURRENT_TIMESTAMP() where menuId IN (select distinct(menuId) FROM MENU_SECTION JOIN (select distinct(sectionId) as sectionId FROM SECTION_DISH where dishId=" + dishId + " ) sec where sec.sectionId = MENU_SECTION.sectionId)").executeUpdate();
		
		
	}
	@Override
	public List<Dish> listDish() {
		return sessionFactory.getCurrentSession().createQuery("from Dish").list();
	}

	@Override
	public List<Dish> listDishByResaurant(Integer restaurantId) {
		return sessionFactory.getCurrentSession().createCriteria(Dish.class).add(Restrictions.eq("restaurantId", restaurantId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

	
	@Override
	public void removeDish(Integer id) throws Exception{
		Dish dish = (Dish) sessionFactory.getCurrentSession().load(Dish.class, id);
		if (null != dish) {
			sessionFactory.getCurrentSession().delete(dish);
		}

	}

	@Override
	public List<Dish> getDishes(Integer[] ids) {
		return (List<Dish>) sessionFactory.getCurrentSession().createCriteria(Dish.class).add(Restrictions.in("dishId", ids)).list();

	}

	@Override
	public Dish getDish(Integer id) {
		return (Dish) sessionFactory.getCurrentSession().get(Dish.class, id);
	}

}
