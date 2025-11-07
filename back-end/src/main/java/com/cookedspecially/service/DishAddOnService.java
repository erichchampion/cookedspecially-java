/**
 * 
 */
package com.cookedspecially.service;

import java.util.List;

import com.cookedspecially.domain.AddOnDish;
import com.cookedspecially.domain.Dish;
import com.cookedspecially.domain.DishAddOn;

/**
 * @author rahul
 *
 */
public interface DishAddOnService {

	public void addDish(DishAddOn dish);
	public void updateMenuModificationTime(Integer dishId);
	public List<DishAddOn > listDishAddOn(Integer restaurantId);
	public List<DishAddOn> listDishAddOnByDish(Integer dishId);
	public List<AddOnDish> listDishAddOnByRestaurant(Integer restaurantId);
	public List<DishAddOn> listDishAddOnByFulfillmentCenter(Integer fulfillmentCenterId);
	public void removeDishAddOn(Integer addOnId) throws Exception;
/*	public List<AddOnDish> getDishes(Integer restaurantId);*/
	//public AddOnDish getDish(Integer id);
}
