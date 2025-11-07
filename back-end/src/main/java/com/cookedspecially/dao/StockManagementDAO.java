/**
 * 
 */
package com.cookedspecially.dao;

import java.util.List;

import com.cookedspecially.domain.Dish;
import com.cookedspecially.domain.StockManagement;

/**
 * @author sagarwal
 *
 */
public interface StockManagementDAO {

	public void addStockDish(StockManagement stockManagement);
	public List<Dish> getStockedDishes(Integer restaurantId);
	public void removeStockDish(Integer id);
	public List<StockManagement> getFromStockManagement(Integer restaurantId);
	public StockManagement getStockedDish(Integer id);
	public StockManagement getStockedDishbyDishId(Integer dishId, Integer fulfillmentCenterId);
	public List<StockManagement> listStockedDishbyDishId(Integer dishId, Integer fulfillmentCenterId);
}
