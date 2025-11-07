/**
 * 
 */
package com.cookedspecially.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cookedspecially.dao.OrderDishDAO;
import com.cookedspecially.service.OrderDishService;

/**
 * @author shashank
 *
 */
@Service
public class OrderDishServiceImpl implements OrderDishService {

	@Autowired
	private OrderDishDAO orderDishDAO;
	
	
	@Override
	@Transactional
	public void removeOrderDishes(List<Integer> orderDishIds) {
		orderDishDAO.removeOrderDishes(orderDishIds);

	}

}
