/**
 * 
 */
package com.cookedspecially.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cookedspecially.dao.AddOnDishDAO;
import com.cookedspecially.dao.DishAddOnDAO;
import com.cookedspecially.dao.DishDAO;
import com.cookedspecially.domain.AddOnDish;
import com.cookedspecially.domain.Dish;
import com.cookedspecially.domain.DishAddOn;
import com.cookedspecially.service.AddOnDishService;
import com.cookedspecially.service.DishAddOnService;
import com.cookedspecially.service.DishService;

/**
 * @author rahul
 *
 */
@Service
public class AddOnDishServiceImpl implements AddOnDishService {

	@Autowired
	private AddOnDishDAO dishDAO;
	
	@Override
	@Transactional
	public void addDish(AddOnDish dish) {
		dishDAO.addDish(dish);
		
	}


	@Override
	@Transactional
	public void updateMenuModificationTime(Integer dishId) {
		dishDAO.updateMenuModificationTime(dishId);
	}
	@Override
	@Transactional
	public List<AddOnDish> listDish() {
		return dishDAO.listDish();
	}

	@Override
	@Transactional
	public List<AddOnDish> listDishByRestaurant(Integer restaurantId) {
		return dishDAO.listDishByResaurant(restaurantId);
	}

	@Override
	@Transactional
	public void removeDish(Integer id) throws Exception{
		dishDAO.removeDish(id);

	}


	@Override
	@Transactional
	public List<AddOnDish> getDishes(Integer[] ids) {
		return dishDAO.getDishes(ids);
	}

	@Override
	@Transactional
	public AddOnDish getDish(Integer id) {
		return dishDAO.getDish(id);
	}
}
