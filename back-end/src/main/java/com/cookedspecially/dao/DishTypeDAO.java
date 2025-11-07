/**
 * 
 */
package com.cookedspecially.dao;

import java.util.List;

import com.cookedspecially.domain.AddOnDish_Size;
import com.cookedspecially.domain.DishSize;
import com.cookedspecially.domain.DishType;
import com.cookedspecially.domain.Dish_Size;

/**
 * @author shashank, rahul
 *
 */
public interface DishTypeDAO {
	public void addDishType(DishType dishType);
	public List<DishType> listDishTypes();
	public List<DishType> listDishTypesByRestaurantId(Integer restaurantId);
	public void removeDishType(Integer id);
	public DishType getDishType(Integer id);
	
	public void addDishSize(DishSize dishType);
	public List<DishSize> listDishSizeByRestaurantId(Integer restaurantId);
	public void removeDishSize(Integer id);
	public DishSize getDishSize(Integer id);
	
	public Dish_Size getDish_Size(Integer id,Integer dishId);
	public AddOnDish_Size getAddOnDish_Size(Integer id,Integer addOnDishId);
	public List<Dish_Size> getDish_SizeListbyDishId(Integer dishId);
	public List<AddOnDish_Size> getAddOnDish_SizeListbyDishId(Integer addOnDishId);
}
