/**
 * 
 */
package com.cookedspecially.dao;

import java.util.List;

import com.cookedspecially.domain.AddOnDishType;
import com.cookedspecially.domain.DishType;

/**
 * @author rahul
 *
 */
public interface AddOnDishTypeDAO {
	public void addDishType(AddOnDishType dishType);
	public List<AddOnDishType> listDishTypes();
	public List<AddOnDishType> listDishTypesByRestaurantId(Integer restaurantId);
	public void removeDishType(Integer id);
	public AddOnDishType getDishType(Integer id);
}
