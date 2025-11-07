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
import com.cookedspecially.dao.StockManagementDAO;
import com.cookedspecially.domain.DeliveryArea;
import com.cookedspecially.domain.Dish;
import com.cookedspecially.domain.StockManagement;

/**
 * @author sagarwal
 *
 */
@Repository
public class StockManagementDAOImpl implements StockManagementDAO {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void addStockDish(StockManagement stockManage) {
		sessionFactory.getCurrentSession().saveOrUpdate(stockManage);

	}
	
	@Override
	public void removeStockDish(Integer id){
		StockManagement stockDish = (StockManagement) sessionFactory.getCurrentSession().load(StockManagement.class, id);
		if (null != stockDish) {
			sessionFactory.getCurrentSession().delete(stockDish );
		}
	}

	@Override
	public List<Dish> getStockedDishes(Integer restaurantId) {
		// TODO Auto-generated method stub
		return (List<Dish>) sessionFactory.getCurrentSession().createCriteria(Dish.class).add(Restrictions.sqlRestriction("manageStock=true && restaurantId="+restaurantId)).list();

	}

	@Override
	public List<StockManagement> getFromStockManagement(Integer restaurantId) {
		// TODO Auto-generated method stub
		return (List<StockManagement>) sessionFactory.getCurrentSession().createCriteria(StockManagement.class).add(Restrictions.eq("restaurantId",restaurantId)).list();
	}

	@Override
	public StockManagement getStockedDish(Integer id) {
		// TODO Auto-generated method stub
		return (StockManagement) sessionFactory.getCurrentSession().get(StockManagement.class, id);
	}

	@Override
	public StockManagement getStockedDishbyDishId(Integer dishId, Integer fulfillmentCenterId) {
		// TODO Auto-generated method stub
		return (StockManagement)sessionFactory.getCurrentSession().createCriteria(StockManagement.class).add(Restrictions.sqlRestriction("dishId="+dishId+" && fulfillmentCenterId="+fulfillmentCenterId)).uniqueResult();
	}

	@Override
	public List<StockManagement> listStockedDishbyDishId(Integer dishId,
			Integer fulfillmentCenterId) {
		// TODO Auto-generated method stub
		return  (List<StockManagement>)sessionFactory.getCurrentSession().createCriteria(StockManagement.class).add(Restrictions.sqlRestriction("dishId="+dishId+" && fulfillmentCenterId="+fulfillmentCenterId)).list();
	}

}
