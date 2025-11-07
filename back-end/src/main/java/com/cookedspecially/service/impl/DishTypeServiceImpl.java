/**
 * 
 */
package com.cookedspecially.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cookedspecially.dao.DishTypeDAO;
import com.cookedspecially.domain.AddOnDish_Size;
import com.cookedspecially.domain.DishSize;
import com.cookedspecially.domain.DishType;
import com.cookedspecially.domain.Dish_Size;
import com.cookedspecially.service.DishTypeService;

/**
 * @author shashank
 *
 */
@Service
public class DishTypeServiceImpl implements DishTypeService {

	@Autowired
	DishTypeDAO dishTypeDAO;
	
	@Override
	@Transactional
	public void addDishType(DishType dishType) {
		dishTypeDAO.addDishType(dishType);
	}

	@Override
	@Transactional
	public List<DishType> listDishTypes() {
		return dishTypeDAO.listDishTypes();
	}

	@Override
	@Transactional
	public List<DishType> listDishTypesByRestaurantId(Integer restaurantId) {
		return dishTypeDAO.listDishTypesByRestaurantId(restaurantId);
	}

	@Override
	@Transactional
	public void removeDishType(Integer id) {
		dishTypeDAO.removeDishType(id);
	}

	@Override
	@Transactional
	public DishType getDishType(Integer id) {
		return dishTypeDAO.getDishType(id);
	}

	@Override
	@Transactional
	public void addDishSize(DishSize dishSize) {
		// TODO Auto-generated method stub
		dishTypeDAO.addDishSize(dishSize);
	}

	@Override
	@Transactional
	public List<DishSize> listDishSizeByRestaurantId(Integer restaurantId) {
		// TODO Auto-generated method stub
		return dishTypeDAO.listDishSizeByRestaurantId(restaurantId);
	}

	@Override
	@Transactional
	public void removeDishSize(Integer id) {
		// TODO Auto-generated method stub
		dishTypeDAO.removeDishSize(id);
	}

	@Override
	@Transactional
	public DishSize getDishSize(Integer id) {
		// TODO Auto-generated method stub
		return dishTypeDAO.getDishSize(id);
	}

	@Override
	@Transactional
	public Dish_Size getDish_Size(Integer id,Integer dishId) {
		// TODO Auto-generated method stub
		return dishTypeDAO.getDish_Size(id,dishId);
	}

	@Override
	@Transactional
	public AddOnDish_Size getAddOnDish_Size(Integer id,Integer adonDishId) {
		// TODO Auto-generated method stub
		return dishTypeDAO.getAddOnDish_Size(id,adonDishId);
	}

	@Override
	@Transactional
	public List<Dish_Size> getDish_SizeListbyDishId(Integer dishId) {
		// TODO Auto-generated method stub
		return dishTypeDAO.getDish_SizeListbyDishId(dishId);
	}

	@Override
	@Transactional
	public List<AddOnDish_Size> getAddOnDish_SizeListbyDishId(
			Integer addOnDishId) {
		// TODO Auto-generated method stub
		return dishTypeDAO.getAddOnDish_SizeListbyDishId(addOnDishId);
	}
}
