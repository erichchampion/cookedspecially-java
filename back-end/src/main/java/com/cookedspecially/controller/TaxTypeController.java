/**
 * 
 */
package com.cookedspecially.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import springfox.documentation.annotations.ApiIgnore;

import com.cookedspecially.domain.Menu;
import com.cookedspecially.domain.TaxType;
import com.cookedspecially.dto.ResponseDTO;
import com.cookedspecially.enums.Status;
import com.cookedspecially.enums.restaurant.ChargesType;
import com.cookedspecially.service.DishTypeService;
import com.cookedspecially.service.MenuService;
import com.cookedspecially.service.RestaurantService;
import com.cookedspecially.service.TaxTypeService;
import com.cookedspecially.service.ZomatoService;

/**
 * @author rahul
 *
 */
@Controller
@RequestMapping("/taxTypes")
@ApiIgnore
public class TaxTypeController {

	@Autowired
	TaxTypeService taxTypeService;
	 
	@Autowired
	DishTypeService dishTypeService;
	
	@Autowired
	RestaurantService restService;
	
	@Autowired
	ZomatoService zomatoService;
	
	@Autowired
	MenuService menuService;
	
	/*@RequestMapping("/getTaxes/{restaurantId}")
	public @ResponseBody List<TaxType> getlistTaxTypes(Map<String, Object> map, HttpServletRequest request, @PathVariable("restaurantId") Integer restId) {
		
		List<TaxType> taxTypeList =  taxTypeService.listTaxTypesByRestaurantId(restId);
		
		
		return taxTypeList;
	}*/
	
	@RequestMapping("/")
	@ApiIgnore
	public String listTaxTypes(Map<String, Object> map, HttpServletRequest request) {
		map.put("taxType", new TaxType());
		map.put("taxTypeList", taxTypeService.listTaxTypesByRestaurantId((Integer) request.getSession().getAttribute("restaurantId")));
		map.put("chargeTypes", ChargesType.values());
		map.put("dishType",dishTypeService.listDishTypesByRestaurantId((Integer) request.getSession().getAttribute("restaurantId")));
		return "taxType";
	}

	@RequestMapping("/edit/{taxTypeId}")
	@ApiIgnore
	public String editTaxType(Map<String, Object> map, HttpServletRequest request, @PathVariable("taxTypeId")	Integer taxTypeId) {

		TaxType taxType = taxTypeService.getTaxType(taxTypeId);
		map.put("taxType", taxType);
		map.put("chargeTypes", ChargesType.values());
		map.put("taxTypeList", taxTypeService.listTaxTypesByRestaurantId((Integer) request.getSession().getAttribute("restaurantId")));
		map.put("dishType",dishTypeService.listDishTypesByRestaurantId((Integer) request.getSession().getAttribute("restaurantId")));
		return "taxType";
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiIgnore
	public String addTaxType(@ModelAttribute("taxType")
	TaxType taxType, BindingResult result,HttpServletRequest request) {
		taxTypeService.addTaxType(taxType);
		updateZomatoMenu(taxType.getRestaurantId(), request);
		return "redirect:/taxTypes/";
	}

	@RequestMapping(value = "/addOver", method = RequestMethod.POST)
	@ApiIgnore
	public void addOverrideTaxType(@ModelAttribute("taxType")
	TaxType taxType, BindingResult result,HttpServletRequest request) {
		taxTypeService.addTaxType(taxType);
		updateZomatoMenu(taxType.getRestaurantId(), request);
	}
	@RequestMapping("/delete/{taxTypeId}")
	@ApiIgnore
	public String deleteTaxType(@PathVariable("taxTypeId")
	Integer taxTypeId,HttpServletRequest request) {
		TaxType taxType = taxTypeService.getTaxType(taxTypeId);
		taxTypeService.removeTaxType(taxTypeId);
		try {
			ResponseDTO response = zomatoService.inactivateTax(taxType,request);
			if("success".equalsIgnoreCase(response.message))
				restService.alertMail(response.message, taxType.getName()+" [Tax Inactivated] status: "+response.result, request);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "redirect:/taxTypes/";
	}
	public ResponseDTO updateZomatoMenu(Integer restaurantId, HttpServletRequest request){
		
		List<Menu> menuList =  menuService.allMenusByStatus(restaurantId, Status.ACTIVE);
			ResponseDTO response=null;
			for(Menu menu : menuList){
				if(!menu.isPosVisible() && menu.getZomatoStatus()==Status.ACTIVE){
					try {
						 response = zomatoService.updateZomatoMenu(menu,request);
						if("success".equalsIgnoreCase(response.message))
							restService.alertMail(response.message, menu.getName()+" [Menu Tax updated] status: "+response.result, request);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						try {
							restService.emailException(ExceptionUtils.getStackTrace(e),request);
						} catch (UnsupportedEncodingException
								| MessagingException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						try {
							restService.emailException(ExceptionUtils.getStackTrace(e),request);
						} catch (UnsupportedEncodingException
								| MessagingException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
		}
			return response;
		}
}
