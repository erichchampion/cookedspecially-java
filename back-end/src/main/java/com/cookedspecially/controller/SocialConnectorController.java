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

import com.cookedspecially.domain.SocialConnector;
import com.cookedspecially.service.SocialConnectorService;

@Controller
@RequestMapping("/socialConnector")
public class SocialConnectorController {


	@Autowired
	private SocialConnectorService socialConnectorService;
	
	@RequestMapping("/")
	public String listDishTypes(Map<String, Object> map, HttpServletRequest request) {

		map.put("connector", new SocialConnector());
		map.put("editable",false);
		map.put("connectorTypes", com.cookedspecially.enums.SocialConnector.values());
		map.put("connectorsList", socialConnectorService.listSocialConnectorByOrgId((Integer) request.getSession().getAttribute("organisationId")));
		map.put("statusTypes", com.cookedspecially.enums.Status.values());
		return "socialConnectors";
	}

	@RequestMapping("/edit/{id}")
	public String editDishType(Map<String, Object> map, HttpServletRequest request, @PathVariable("id")	Integer id) {

		SocialConnector socialConnector = socialConnectorService.getSocialConnector(id);
		map.put("connector",socialConnector);
		map.put("editable",true);
		map.put("connectorTypes", com.cookedspecially.enums.SocialConnector.values());
		map.put("connectorsList", socialConnectorService.listSocialConnectorByOrgId(((Integer) request.getSession().getAttribute("organisationId"))));
		map.put("statusTypes", com.cookedspecially.enums.Status.values());
		return "socialConnectors";
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addDishType(@ModelAttribute("socialConnector")
	SocialConnector socialConnector, BindingResult result) {
		socialConnectorService.addSocialConnector(socialConnector);
		return "redirect:/socialConnector/";
	}

	@RequestMapping("/delete/{id}")
	public String deleteDishType(@PathVariable("id")
	Integer id) {
		try {
			socialConnectorService.removeSocialConnector(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "redirect:/socialConnector/";
	}
}
