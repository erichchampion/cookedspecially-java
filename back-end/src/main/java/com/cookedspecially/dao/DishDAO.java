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
public interface DishDAO {

	public void addDish(Dish dish);
	public void updateMenuModificationTime(Integer dishId);
	public List<Dish> listDish();
	public List<Dish> listDishByResaurant(Integer restaurantId);
	public void removeDish(Integer id) throws Exception;
	public List<Dish> getDishes(Integer[] ids);
	public Dish getDish(Integer id);
	
}
