/**
 * 
 */
package com.cookedspecially.service;

import java.util.List;

import com.cookedspecially.domain.AddOnDishType;
import com.cookedspecially.domain.DishType;

/**
 * @author shashank
 *
 */
public interface AddOnDishTypeService {
	public void addDishType(AddOnDishType dishType);
	public List<AddOnDishType> listDishTypes();
	public List<AddOnDishType> listDishTypesByRestaurant(Integer restaurantId);
	public void removeDishType(Integer id);
	public AddOnDishType getDishType(Integer id);
}
