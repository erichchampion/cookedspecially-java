package com.cookedspecially.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import springfox.documentation.annotations.ApiIgnore;

import com.cookedspecially.domain.Category;
import com.cookedspecially.service.CategoryService;

/**
 * @author sagarwal
 *
 */
@Controller
@ApiIgnore
@RequestMapping("/category")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;
	

	
	@RequestMapping("/")
	public String listCategories(Map<String, Object> map, HttpServletRequest request) {

		map.put("category", new Category());
		map.put("categoryList", categoryService.listCategoryByRestaurant((Integer) request.getSession().getAttribute("restaurantId")));
		return "category";
	}

	@RequestMapping("/edit/{categoryId}")
	public String editCategory(Map<String, Object> map, HttpServletRequest request, @PathVariable("categoryId")
	Integer categoryId) {

		map.put("category", categoryService.getCategory(categoryId));
		map.put("categoryList", categoryService.listCategoryByRestaurant((Integer) request.getSession().getAttribute("restaurantId")));
		return "category";
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addCategory(@ModelAttribute("category")
	Category category, BindingResult result) {
		categoryService.addCategory(category);

		return "redirect:/category/";
	}

	@RequestMapping("/delete/{categoryId}")
	public String deleteCategory(@PathVariable("categoryId")
	Integer categoryId) {

		categoryService.removeCategory(categoryId);

		return "redirect:/category/";
	}	
}
