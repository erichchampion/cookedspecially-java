/**
 * 
 */
package com.cookedspecially.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cookedspecially.dao.OrderDishDAO;
import com.cookedspecially.domain.OrderDish;

/**
 * @author shashank
 *
 */
@Repository
public class OrderDishDAOImpl implements OrderDishDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void removeOrderDishes(List<Integer> orderDishIds) {
		Integer[] orderDishIdsArr = new Integer[orderDishIds.size()];
		List<OrderDish> orderDishes = (List<OrderDish>)sessionFactory.getCurrentSession().createCriteria(OrderDish.class).add(Restrictions.in("orderDishId", orderDishIds.toArray(orderDishIdsArr))).list();
		for (OrderDish orderDish : orderDishes) {
			sessionFactory.getCurrentSession().delete(orderDish);
		}

	}

}
