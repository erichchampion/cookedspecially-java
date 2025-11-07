/**
 * 
 */
package com.cookedspecially.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cookedspecially.dao.DishDAO;
import com.cookedspecially.dao.StockManagementDAO;
import com.cookedspecially.domain.Dish;
import com.cookedspecially.domain.StockManagement;
import com.cookedspecially.service.DishService;
import com.cookedspecially.service.StockManagementService;

/**
 * @author Rahul
 *
 */
@Service
public class StockManagementServiceImpl implements StockManagementService{

	@Autowired
	private StockManagementDAO stockManagementDAO;

	@Override
	@Transactional
	public void addStockDish(StockManagement stockManagement) {
		stockManagementDAO.addStockDish(stockManagement);
	}

	@Override
	@Transactional
	public List<Dish> getStockedDishes(Integer restaurantId) {
		return stockManagementDAO.getStockedDishes(restaurantId);
	}

	@Override
	@Transactional
	public void removeStockDish(Integer id) {
		stockManagementDAO.removeStockDish(id);
		
	}

	@Override
	@Transactional
	public List<StockManagement> getFromStockManagement(Integer restaurantId) {
		return stockManagementDAO.getFromStockManagement(restaurantId);
	}

	@Override
	@Transactional
	public StockManagement getStockedDish(Integer id) {
		// TODO Auto-generated method stub
		return stockManagementDAO.getStockedDish(id);
	}

	@Override
	@Transactional
	public StockManagement getStockedDishbyDishId(Integer dishId, Integer fulfillmentCenterId){
		// TODO Auto-generated method stub
		return stockManagementDAO.getStockedDishbyDishId(dishId, fulfillmentCenterId);
	}

	@Override
	@Transactional
	public List<StockManagement> listStockedDishbyDishId(Integer dishId,
			Integer fulfillmentCenterId) {
		// TODO Auto-generated method stub
		return stockManagementDAO.listStockedDishbyDishId(dishId, fulfillmentCenterId);
	}
	
}
