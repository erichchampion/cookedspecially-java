/**
 * 
 */
package com.cookedspecially.dao;

import java.util.List;

import com.cookedspecially.domain.Category;

/**
 * @author sagarwal
 *
 */
public interface CategoryDAO {
	public void addCategory(Category category);
	public List<Category> listCategory();
	public List<Category> listCategoryByRestaurant(Integer restaurantId);
	public void removeCategory(Integer id);
	public Category getCategory(Integer id);
}
