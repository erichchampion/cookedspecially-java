/**
 * 
 */
package com.cookedspecially.dao;

import java.util.List;

import com.cookedspecially.domain.AddOnDish;
import com.cookedspecially.domain.Dish;
import com.cookedspecially.domain.DishAddOn;

/**
 * @author rahul
 *
 */
public interface DishAddOnDAO {

	public void addDish(DishAddOn dish);
	public void updateMenuModificationTime(Integer dishId);
	public List<DishAddOn> listDishAddOn(Integer restaurantId);
	public List<DishAddOn> listDishAddOnByDish(Integer dishId);
	public List<AddOnDish> listDishAddOnByRestaurant(Integer restaurantId);
	public List<DishAddOn> listDishAddOnByFulfillmentCenter(Integer fulfilllmentCenterId);
	public void removeDishAddOn(Integer id) throws Exception;
	/*public List<AddOnDish> getDishes(Integer id);*/
//	public AddOnDish getDish(Integer id);
}
