/**
 * 
 */
package com.cookedspecially.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cookedspecially.dao.DishDAO;
import com.cookedspecially.domain.Dish;
import com.cookedspecially.service.DishService;

/**
 * @author sagarwal
 *
 */
@Service
public class DishServiceImpl implements DishService {

	@Autowired
	private DishDAO dishDAO;
	
	@Override
	@Transactional
	public void addDish(Dish dish) {
		dishDAO.addDish(dish);
	}


	@Override
	@Transactional
	public void updateMenuModificationTime(Integer dishId) {
		dishDAO.updateMenuModificationTime(dishId);
	}
	@Override
	@Transactional
	public List<Dish> listDish() {
		return dishDAO.listDish();
	}

	@Override
	@Transactional
	public List<Dish> listDishByResaurant(Integer restaurantId) {
		return dishDAO.listDishByResaurant(restaurantId);
	}

	@Override
	@Transactional
	public void removeDish(Integer id) throws Exception{
		dishDAO.removeDish(id);

	}


	@Override
	@Transactional
	public List<Dish> getDishes(Integer[] ids) {
		return dishDAO.getDishes(ids);
	}

	@Override
	@Transactional
	public Dish getDish(Integer id) {
		return dishDAO.getDish(id);
	}
}
