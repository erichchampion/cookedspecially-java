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

import com.cookedspecially.dao.AddOnDishDAO;
import com.cookedspecially.dao.DishDAO;
import com.cookedspecially.domain.AddOnDish;
import com.cookedspecially.domain.Dish;
import com.cookedspecially.domain.DishAddOn;

/**
 * @author rahul
 *
 */
@Repository
public class AddOnDishDAOImpl implements AddOnDishDAO {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void addDish(AddOnDish dish) {
		sessionFactory.getCurrentSession().saveOrUpdate(dish);
		cleanUpdate(dish);
	}

	public void cleanUpdate(AddOnDish dish){
		Query query = 	sessionFactory.getCurrentSession().createQuery("update DishAddOn set name=:name, description=:description, shortDescription=:shortDescription,imageUrl=:imageUrl,  price=:price ,dishType=:dishType, vegetarian=:vegetarian, alcoholic=:alcoholic, disabled=:disabled, displayPrice=:displayPrice  WHERE addOnId="+dish.getAddOnId());
		query.setParameter("name",dish.getName());
		query.setParameter("description",dish.getDescription());
		query.setParameter("shortDescription",dish.getShortDescription());
		query.setParameter("imageUrl",dish.getImageUrl());
		query.setParameter("price",dish.getPrice());
		query.setParameter("dishType",dish.getDishType());
		query.setParameter("vegetarian",dish.getVegetarian());
		query.setParameter("alcoholic",dish.getVegetarian());
		query.setParameter("disabled",dish.getDisabled());
		query.setParameter("displayPrice",dish.getDisplayPrice());
		int result = query.executeUpdate();
	}
	
	@Override
	public void updateMenuModificationTime(Integer dishId) {
		sessionFactory.getCurrentSession().createSQLQuery("update MENUS SET modifiedTime=CURRENT_TIMESTAMP() where menuId IN (select distinct(menuId) FROM MENU_SECTION JOIN (select distinct(sectionId) as sectionId FROM SECTION_DISH where dishId=" + dishId + " ) sec where sec.sectionId = MENU_SECTION.sectionId)").executeUpdate();
	}
	@Override
	public List<AddOnDish> listDish() {
		return sessionFactory.getCurrentSession().createQuery("from Dish").list();
	}
	@Override
	public List<AddOnDish> listDishByResaurant(Integer restaurantId) {
		return sessionFactory.getCurrentSession().createCriteria(AddOnDish.class).add(Restrictions.eq("restaurantId", restaurantId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

	
	@Override
	public void removeDish(Integer id) throws Exception{
		AddOnDish dish = (AddOnDish) sessionFactory.getCurrentSession().load(AddOnDish.class, id);
		if (null != dish) {
			sessionFactory.getCurrentSession().delete(dish);
			cleanAddOn(id);
		}
				
	}
   public void cleanAddOn(int id){
	sessionFactory.getCurrentSession().createSQLQuery("SET SQL_SAFE_UPDATES=0;").executeUpdate();
	sessionFactory.getCurrentSession().createQuery("DELETE  FROM DishAddOn WHERE addOnId="+ id).executeUpdate();
}
	
	@Override
	public List<AddOnDish> getDishes(Integer[] ids) {
		return (List<AddOnDish>) sessionFactory.getCurrentSession().createCriteria(AddOnDish.class).add(Restrictions.in("addOnId", ids)).list();

	}

	@Override
	public AddOnDish getDish(Integer id) {
		return (AddOnDish) sessionFactory.getCurrentSession().get(AddOnDish.class, id);
	}
}
