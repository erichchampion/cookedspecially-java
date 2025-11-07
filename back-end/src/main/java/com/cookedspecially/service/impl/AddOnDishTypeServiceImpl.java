/**
 * 
 */
package com.cookedspecially.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cookedspecially.dao.AddOnDishTypeDAO;
import com.cookedspecially.dao.DishTypeDAO;
import com.cookedspecially.domain.AddOnDishType;
import com.cookedspecially.domain.DishType;
import com.cookedspecially.service.AddOnDishTypeService;
import com.cookedspecially.service.DishTypeService;

/**
 * @author rahul
 *
 */
@Service
public class AddOnDishTypeServiceImpl implements  AddOnDishTypeService {

	@Autowired
	 AddOnDishTypeDAO dishTypeDAO;
	
	@Override
	@Transactional
	public void addDishType(AddOnDishType dishType) {
		dishTypeDAO.addDishType(dishType);
	}

	@Override
	@Transactional
	public List<AddOnDishType> listDishTypes() {
		return dishTypeDAO.listDishTypes();
	}

	@Override
	@Transactional
	public List<AddOnDishType> listDishTypesByRestaurant(Integer restaurantId) {
		return dishTypeDAO.listDishTypesByRestaurantId(restaurantId);
	}

	@Override
	@Transactional
	public void removeDishType(Integer id) {
		dishTypeDAO.removeDishType(id);
	}

	@Override
	@Transactional
	public AddOnDishType getDishType(Integer id) {
		return dishTypeDAO.getDishType(id);
	}

}
