/**
 * 
 */
package com.cookedspecially.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cookedspecially.dao.CategoryDAO;
import com.cookedspecially.domain.Category;
import com.cookedspecially.service.CategoryService;

/**
 * @author sagarwal
 *
 */
@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryDAO categoryDAO;

	@Override
	@Transactional
	public void addCategory(Category category) {
		categoryDAO.addCategory(category);

	}

	@Override
	@Transactional
	public List<Category> listCategory() {
		return categoryDAO.listCategory();
	}

	@Override
	@Transactional
	public List<Category> listCategoryByRestaurant(Integer restaurantId) {
		return categoryDAO.listCategoryByRestaurant(restaurantId);
	}
	
	@Override
	@Transactional
	public void removeCategory(Integer id) {
		categoryDAO.removeCategory(id);

	}

	@Override
	@Transactional
	public Category getCategory(Integer id) {
		return categoryDAO.getCategory(id);
	}

}
