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

import com.cookedspecially.dao.DishTypeDAO;
import com.cookedspecially.domain.AddOnDish_Size;
import com.cookedspecially.domain.DishSize;
import com.cookedspecially.domain.DishType;
import com.cookedspecially.domain.Dish_Size;

/**
 * @author shashank
 *
 */
@Repository
public class DishTypeDAOImpl implements DishTypeDAO {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void addDishType(DishType dishType) {
		sessionFactory.getCurrentSession().saveOrUpdate(dishType);
	}

	@Override
	public List<DishType> listDishTypes() {
		return sessionFactory.getCurrentSession().createCriteria(DishType.class).list();
	}

	@Override
	public List<DishType> listDishTypesByRestaurantId(Integer restaurantId) {
		return sessionFactory.getCurrentSession().createCriteria(DishType.class).add(Restrictions.eq("restaurantId", restaurantId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

	@Override
	public void removeDishType(Integer id) {
		DishType dishType = (DishType) sessionFactory.getCurrentSession().load(DishType.class, id);
		if (null != dishType) {
			sessionFactory.getCurrentSession().delete(dishType);
		}
	}

	@Override
	public DishType getDishType(Integer id) {
		return (DishType)sessionFactory.getCurrentSession().get(DishType.class, id);
	}

	@Override
	public void addDishSize(DishSize dishSize) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().saveOrUpdate(dishSize);
	}

	@Override
	public List<DishSize> listDishSizeByRestaurantId(Integer restaurantId) {
		// TODO Auto-generated method stub
		return sessionFactory.getCurrentSession().createCriteria(DishSize.class).add(Restrictions.eq("restaurantId", restaurantId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

	@Override
	public void removeDishSize(Integer id) {
		DishSize dishSize =  getDishSize(id);
		// TODO Auto-generated method stub
		if(dishSize !=null){
		sessionFactory.getCurrentSession().delete(dishSize);
	}
	}

	@Override
	public DishSize getDishSize(Integer id) {
		// TODO Auto-generated method stub
		return (DishSize) sessionFactory.getCurrentSession().get(DishSize.class, id);
	}

	@Override
	public Dish_Size getDish_Size(Integer id,Integer dishId) {
		// TODO Auto-generated method stub
		return (Dish_Size) sessionFactory.getCurrentSession().createCriteria(Dish_Size.class).add(Restrictions.and(Restrictions.eq("dishSizeId",id),Restrictions.eq("dishId",dishId))).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).uniqueResult();
	}
	
	@Override
	public AddOnDish_Size getAddOnDish_Size(Integer id,Integer addOnId) {
		// TODO Auto-generated method stub
		return (AddOnDish_Size) sessionFactory.getCurrentSession().createCriteria(AddOnDish_Size.class).add(Restrictions.and(Restrictions.eq("dishSizeId",id),Restrictions.eq("addOnDishId",addOnId))).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).uniqueResult();
	}

	@Override
	public List<Dish_Size> getDish_SizeListbyDishId(Integer dishId) {
		// TODO Auto-generated method stub
		return sessionFactory.getCurrentSession().createCriteria(Dish_Size.class).add(Restrictions.eq("dishId",dishId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

	@Override
	public List<AddOnDish_Size> getAddOnDish_SizeListbyDishId(
			Integer addOnDishId) {
		// TODO Auto-generated method stub
		return sessionFactory.getCurrentSession().createCriteria(AddOnDish_Size.class).add(Restrictions.eq("addOnDishId",addOnDishId)).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

}
