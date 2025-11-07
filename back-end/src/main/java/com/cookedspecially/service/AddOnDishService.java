/**
 * 
 */
package com.cookedspecially.service;

import java.util.List;

import com.cookedspecially.domain.AddOnDish;
import com.cookedspecially.domain.Dish;

/**
 * @author rahul
 *
 */
public interface AddOnDishService {

	public void addDish(AddOnDish dish);
	public void updateMenuModificationTime(Integer dishId);
	public List<AddOnDish> listDish();
	public List<AddOnDish> listDishByRestaurant(Integer restId);
	public void removeDish(Integer id) throws Exception;
	public List<AddOnDish> getDishes(Integer[] ids);
	public AddOnDish getDish(Integer id);
}
