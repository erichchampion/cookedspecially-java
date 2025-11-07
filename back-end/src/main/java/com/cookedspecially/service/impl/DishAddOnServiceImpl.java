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
public class DishAddOnServiceImpl implements DishAddOnService {

	@Autowired
	private DishAddOnDAO dishDAO;
	
	@Override
	@Transactional
	public void addDish(DishAddOn dish) {
		dishDAO.addDish(dish);
	}


	@Override
	@Transactional
	public void updateMenuModificationTime(Integer dishId) {
		dishDAO.updateMenuModificationTime(dishId);
	}
	@Override
	@Transactional
	public List<DishAddOn> listDishAddOn(Integer restaurantId) {
		return dishDAO.listDishAddOn(restaurantId);
	}

	@Override
	@Transactional
	public List<DishAddOn> listDishAddOnByDish(Integer dishId) {
		return dishDAO.listDishAddOnByDish(dishId);
	}

	@Override
	@Transactional
	public void removeDishAddOn(Integer id) throws Exception{
		dishDAO.removeDishAddOn(id);

	}


	@Override
	@Transactional
	public List<AddOnDish> listDishAddOnByRestaurant(Integer restaurantId) {
      return dishDAO.listDishAddOnByRestaurant(restaurantId);
	}


	@Override
	@Transactional
	public List<DishAddOn> listDishAddOnByFulfillmentCenter(Integer fulfillmentCenterId) {
		return dishDAO.listDishAddOnByFulfillmentCenter(fulfillmentCenterId);
	}


	/*@Override
	public List<AddOnDish> getDishes(Integer restaurantId) {
		// TODO Auto-generated method stub
		return null;
	}*/


	/*@Override
	@Transactional
	public List<AddOnDish> getDishes(Integer id) {
		return dishDAO.getDishes(id);
	}*/

	/*@Override
	@Transactional
	public AddOnDish getDish(Integer id) {
		return dishDAO.getDish(id);
	}*/
}
