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

import com.cookedspecially.domain.AddOnDishType;
import com.cookedspecially.service.AddOnDishTypeService;

/**
 * @author rahul
 *
 */
@Controller
@ApiIgnore
@RequestMapping("/addOnDishTypes")
public class AddOnDishTypeController {

	@Autowired
	AddOnDishTypeService dishTypeService;
	
	@RequestMapping("/")
	public String listDishTypes(Map<String, Object> map, HttpServletRequest request) {

		map.put("dishType", new AddOnDishType());
		map.put("dishTypeList", dishTypeService.listDishTypesByRestaurant((Integer) request.getSession().getAttribute("restaurantId")));
		return "addOnDishType";
	}

	@RequestMapping("/edit/{dishTypeId}")
	public String editDishType(Map<String, Object> map, HttpServletRequest request, @PathVariable("dishTypeId")	Integer dishTypeId) {

		AddOnDishType dishType = dishTypeService.getDishType(dishTypeId);
		map.put("dishType", dishType);
		map.put("dishTypeList", dishTypeService.listDishTypesByRestaurant((Integer) request.getSession().getAttribute("restaurantId")));
		return "addOnDishType";
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addDishType(@ModelAttribute("dishType")
	AddOnDishType dishType, BindingResult result) {
		dishTypeService.addDishType(dishType);
		return "redirect:/addOnDishTypes/";
	}

	@RequestMapping("/delete/{dishTypeId}")
	public String deleteDishType(@PathVariable("dishTypeId")
	Integer dishTypeId) {
		dishTypeService.removeDishType(dishTypeId);
		return "redirect:/addOnDishTypes/";
	}
}
