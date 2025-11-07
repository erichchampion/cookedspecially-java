/**
 * 
 */
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

import com.cookedspecially.domain.DishSize;
import com.cookedspecially.domain.DishType;
import com.cookedspecially.enums.restaurant.ChargesType;
import com.cookedspecially.service.AddOnDishService;
import com.cookedspecially.service.AddOnDishTypeService;
import com.cookedspecially.service.DishTypeService;
import com.cookedspecially.service.TaxTypeService;

/**
 * @author shashank
 *
 */
@Controller
@RequestMapping("/dishTypes")
@ApiIgnore
public class DishTypeController {

	@Autowired
	DishTypeService dishTypeService;
	
	@Autowired
	TaxTypeService taxTypeServices;
	
	@Autowired
	AddOnDishTypeService addOnDishTypeServices;
	
	@RequestMapping("/")
	public String listDishTypes(Map<String, Object> map, HttpServletRequest request) {

		map.put("dishType", new DishType());
		map.put("dishTypeList", dishTypeService.listDishTypesByRestaurantId((Integer) request.getSession().getAttribute("restaurantId")));
		//map.put("taxesList", taxTypeServices.listTaxTypesByRestaurantId((Integer) request.getSession().getAttribute("userId")));
		//map.put("chargeTypes", ChargesType.values());
		return "dishType";
	}

	@RequestMapping("/edit/{dishTypeId}")
	public String editDishType(Map<String, Object> map, HttpServletRequest request, @PathVariable("dishTypeId")	Integer dishTypeId) {

		DishType dishType = dishTypeService.getDishType(dishTypeId);
		map.put("dishType", dishType);
		map.put("dishTypeList", dishTypeService.listDishTypesByRestaurantId((Integer) request.getSession().getAttribute("restaurantId")));
		//map.put("taxesList", taxTypeServices.listTaxTypesByRestaurantId((Integer) request.getSession().getAttribute("userId")));
		//map.put("chargeTypes", ChargesType.values());
		return "dishType";
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addDishType(@ModelAttribute("dishType")
	DishType dishType, BindingResult result) {
		dishTypeService.addDishType(dishType);

		return "redirect:/dishTypes/";
	}

	@RequestMapping("/delete/{dishTypeId}")
	public String deleteDishType(@PathVariable("dishTypeId")
	Integer dishTypeId) {
		dishTypeService.removeDishType(dishTypeId);
		return "redirect:/dishTypes/";
	}
	
	@RequestMapping("/listDishSize")
	public String listDishSize(Map<String, Object> map, HttpServletRequest request) {

		map.put("dishSize", new DishSize());
		map.put("dishSizeList", dishTypeService.listDishSizeByRestaurantId((Integer) request.getSession().getAttribute("restaurantId")));
		map.put("dishTypeList", dishTypeService.listDishTypesByRestaurantId((Integer) request.getSession().getAttribute("restaurantId")));
		map.put("addOnDishType", addOnDishTypeServices.listDishTypesByRestaurant((Integer) request.getSession().getAttribute("restaurantId")));
		return "manageDishSize";
	}

	@RequestMapping("/editDishSize/{dishSizeId}")
	public String editDishSize(Map<String, Object> map, HttpServletRequest request, @PathVariable("dishSizeId")	Integer dishSizeId) {

		DishSize dishSize = dishTypeService.getDishSize(dishSizeId);
		map.put("dishSize", dishSize);
		map.put("dishTypeList", dishTypeService.listDishTypesByRestaurantId((Integer) request.getSession().getAttribute("restaurantId")));
		map.put("dishSizeList", dishTypeService.listDishSizeByRestaurantId((Integer) request.getSession().getAttribute("restaurantId")));
		map.put("addOnDishType", addOnDishTypeServices.listDishTypesByRestaurant((Integer) request.getSession().getAttribute("restaurantId")));

		return "manageDishSize";
	}
	
	@RequestMapping(value = "/addDishSize", method = RequestMethod.POST)
	public String addDishSize(@ModelAttribute("dishSize")
	DishSize dishSize, BindingResult result) {
		dishTypeService.addDishSize(dishSize);

		return "redirect:/dishTypes/listDishSize";
	}

	@RequestMapping("/deleteDishSize/{dishSizeId}")
	public String deleteDishSize(@PathVariable("dishSizeId")
	Integer dishSizeId) {
		dishTypeService.removeDishSize(dishSizeId);
		return "redirect:/dishTypes/listDishSize";
	}
	
	
	
}
