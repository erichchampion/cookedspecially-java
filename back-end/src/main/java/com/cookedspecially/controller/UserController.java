/**
 * 
 */
package com.cookedspecially.controller;

import io.swagger.annotations.Api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cookedspecially.domain.Check;
import com.cookedspecially.domain.FulfillmentCenter;
import com.cookedspecially.domain.Restaurant;
import com.cookedspecially.domain.Role;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import springfox.documentation.annotations.ApiIgnore;

import com.cookedspecially.config.CSConstants;
import com.cookedspecially.domain.User;
import com.cookedspecially.enums.Status;
import com.cookedspecially.enums.restaurant.ChargesType;
import com.cookedspecially.service.CheckService;
import com.cookedspecially.service.FulfillmentCenterService;
import com.cookedspecially.service.RestaurantService;
import com.cookedspecially.service.UserService;
import com.cookedspecially.utility.MailerUtility;
import com.cookedspecially.utility.StringUtility;
/**
 * @author abhishek, Rahul
 * userId
 */
@Controller
@RequestMapping("/user")
@Api(description="User REST API's")
public class UserController {
	final static Logger logger = Logger.getLogger(UserController.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RestaurantService resturantService;
	
	@Autowired
	private FulfillmentCenterService kitchenService;
	
	@Autowired
	CheckService checkService ;
	
	@RequestMapping(value="/secureLogin", method=RequestMethod.GET)
	@ApiIgnore
	public String login(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response ) {
		return "login";
	}
	
	@RequestMapping(value="/signup", method=RequestMethod.GET)
	@ApiIgnore
	public String signup(Map<String, Object> map) {
		map.put("user", new User());
		ArrayList<String> countryName = new ArrayList<String>();
		String[] locales = Locale.getISOCountries();
		for (String countryCode : locales) {
		    Locale obj = new Locale("", countryCode);
		    countryName.add(obj.getDisplayCountry(Locale.ENGLISH));
		 }
		map.put("countryList", countryName);
		return "signup";
	}
	
	@RequestMapping(value="/403", method=RequestMethod.GET)
	@ApiIgnore
	public String accessDenied(Map<String, Object> map) {
		return "redirect:/403.jsp";
	}
	
	@RequestMapping(value="/signup", method=RequestMethod.POST)
	public String signup(@ModelAttribute("user") User user, HttpServletRequest request, BindingResult result, @RequestParam("password") String password,Map<String, Object> map) {
		user.setPasswordHash(userService.getHash(password));
		User existingUser = userService.getUserByUsername(user.getUserName());
		if (existingUser != null) {
			result.rejectValue("userName", "userName", "User Name All ready Exist! Try with diffrent username.");
			return "signup";
		} else {
			Role r=new Role();
			r.setId(1); 
			user.setRole(r);
			userService.addUser(user);
		}
		/*add user details in session so that user can add restaurant(auth in Intercepter) */
		 request.getSession().setAttribute(CSConstants.TOKEN, user.getPasswordHash());
		 request.getSession().setAttribute("userId", user.getUserId());
		 request.getSession().setAttribute("username", user.getUserName());
		/* User Has Been Added Successfully now Add Restaurant (open Add Restaurant View)*/
		map.put("restaurant", new Restaurant());
		map.put("chargeTypes", ChargesType.values());
		map.put("timeZones", CSConstants.timeZoneIds);
		map.put("openFlag", CSConstants.openFlag);
		map.put("statusTypes", Status.values());
		ArrayList<String> countryName = new ArrayList<String>();
		String[] locales = Locale.getISOCountries();
		for (String countryCode : locales) {
		    Locale obj = new Locale("", countryCode);
		    countryName.add(obj.getDisplayCountry(Locale.ENGLISH));
		 }
		map.put("countryList", countryName);
		return "organizationInfo";
	}
	
	@RequestMapping(value="/logout", method=RequestMethod.GET)
	@ApiIgnore
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		session.invalidate();
		return "login";	
	}
	
	
	
	/*@RequestMapping(value="/edit", method=RequestMethod.GET)
	public String editUser(HttpServletRequest request, HttpServletResponse response) {
		Integer userId = (Integer) request.getSession().getAttribute("userId");
		if(userId != null) {
			request.getSession().setAttribute("User", userService.getUser(userId));
			return "editUser";
		}
		return "redirect:/";
	}*/
	
	@RequestMapping(value="/edit", method=RequestMethod.GET)
	@ApiIgnore
	public String editUser(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) {
		Object userIdObj = request.getSession().getAttribute("userId");
		
		if(userIdObj != null) {
			map.put("user", userService.getUser((Integer) userIdObj));
			map.put("chargeTypes", ChargesType.values());
			map.put("timeZones", CSConstants.timeZoneIds);
			map.put("openFlag", CSConstants.openFlag);
			return "editUser";
		}
		return "redirect:/";
	}
	
	@RequestMapping(value="/update", method=RequestMethod.GET)
	@ApiIgnore
	public String update(Map<String, Object> map,HttpServletRequest request, HttpServletResponse response) {
		Integer userId = (Integer) request.getSession().getAttribute("userId");
		if(userId != null) {
			
			map.put("sucess", "User Details Updated Sucessfully !");
			return "updateUser";
		}
		map.put("error", "Could not retrive User Details please contact admin !");
		return "updateUser";
	}
	
	@RequestMapping(value="/removeUser", method=RequestMethod.GET)
	@ApiIgnore
	public String remove(Map<String, Object> map,HttpServletRequest request, HttpServletResponse response) {
		return null;
	}
    
	private String userLogin(HttpServletRequest request,String username, String password, Map<String, Object> map1) {
		TreeMap<String, String>  map = new TreeMap<String, String>();
		 request.getSession().setAttribute(CSConstants.USERNAME, username);
		if(!StringUtility.isNullOrEmpty(username)) {
			User user = userService.getUserByUsername(username);
			logger.info("User just logged-in :"+username);
			 request.getSession().setAttribute("userName", username);
			if (user != null) {
				 request.getSession().setAttribute(CSConstants.TOKEN, user.getPasswordHash());
				 request.getSession().setAttribute("userId", user.getUserId());
					String role=user.getRole().getRole();
					if(!StringUtility.isNullOrEmpty(role)){
				    request.getSession().setAttribute("role", role);
				    switch (role) {
					case "admin":
						if(user.getOrgId() != null){
							request.getSession().setAttribute("restaurantId",user.getOrgId());
							request.getSession().setAttribute("organisationId", user.getOrgId());
							request.getSession().setAttribute("parentRestaurantId",user.getOrgId());
							map.put("organization/employee","Manage Employees");
							map.put("organization/edit","Edit Organization");
							map.put("organization/createRestaurant"," Add Restaurant");
							map.put("organization/orderSource", "Add Order Source");
							map.put("organization/paymentType", "Add Payment Type");
							map.put("organization/creditType", "Add Credit Type");
							map.put("socialConnector/", "Add Social Connectors");
							List<Restaurant> restaurantList =  resturantService.listRestaurantByParentId(user.getOrgId());
							for(Restaurant rest :  restaurantList){
								map.put("organization/org/"+rest.getRestaurantId(),rest.getRestaurantName());
							}
							 request.getSession().setAttribute("orgLink", map);
							return "redirect:/";
						}
						else{
							map1.put("restaurant", new Restaurant());
							map1.put("chargeTypes", ChargesType.values());
							map1.put("timeZones", CSConstants.timeZoneIds);
							map1.put("openFlag", CSConstants.openFlag);
							map1.put("statusTypes", Status.values());
							ArrayList<String> countryName = new ArrayList<String>();
							String[] locales = Locale.getISOCountries();
							for (String countryCode : locales) {
							    Locale obj = new Locale("", countryCode);
							    countryName.add(obj.getDisplayCountry(Locale.ENGLISH));
							 }
							map1.put("countryList", countryName);
							return "organizationInfo";
						}
					case "restaurantManager":
						request.getSession().setAttribute("role", role);
						request.getSession().setAttribute("organisationId", user.getOrgId());
						request.getSession().setAttribute("parentRestaurantId", user.getOrgId());
						List<Integer> managRestList = user.getRestaurantId();
						if(managRestList.size()>1){
							for(Integer rest : managRestList){
								Restaurant restaurant  =  resturantService.getRestaurant(rest);
								request.getSession().setAttribute("restaurantName",restaurant.getRestaurantName());
								map.put("organization/org/"+restaurant.getRestaurantId(),restaurant.getRestaurantName());
								}
							request.getSession().setAttribute("orgLink", map);
							return "redirect:/";
						}
						else{
							Integer id = managRestList.get(0);
							request.getSession().setAttribute("restaurantId",id);
							request.getSession().setAttribute("parentRestaurantId", user.getOrgId());
							map.put("menu/","Menus");
							map.put("dish/","Dishes");
							map.put("addOnDish/"," Add On Dishes");
							map.put("dishTypes/","Manage Dish Type");
							map.put("dishTypes/listDishSize","Manage Dish Size");
							map.put("taxTypes/","Manage Tax Type");
							map.put("addOnDishTypes/","Manage Add On DishTypes");
							map.put("seatingTable/","Manage Tables");
							map.put("restaurant/deliveryAreas","Manage Delivery Areas");
							map.put("restaurant/kitchenScreens","Manage Fulfillment Centers");
							map.put("restaurant/microKitchenScreens","Manage Micro-Kitchen Screen");
							map.put("restaurant/users","Manage User");
							map.put("restaurant/edit","Edit Restaurant");
							map.put("restaurant/listDiscountCharges","Manage Discount/Charges");
							map.put("restaurant/listNutrientes","Manage Nutritional Info");
							map.put("dish/stockedDishes","Manage Stock");
							map.put("giftCard/","Manage Gift-Card");
							
						}
						break;	
                   case "fulfillmentCenterManager":
                	    request.getSession().setAttribute("role", role);
					    request.getSession().setAttribute("organisationId", user.getOrgId());
					    request.getSession().setAttribute("kitchenId", user.getKitchenId());
						if(user.getKitchenId().size()>0){
						request.getSession().setAttribute("restaurantId", kitchenService.getKitchenScreen(user.getKitchenId().get(0)).getRestaurantId());
						}
						map.put("restaurant/deliveryAreas","Manage Delivery Areas");
						map.put("restaurant/kitchenScreens","Manage Fulfillment Centers");
						map.put("restaurant/microKitchenScreens","Manage Micro-Kitchen Screen");
                        break;
                    case "deliveryManager":
                    	request.getSession().setAttribute("role", role);
						request.getSession().setAttribute("kitchenId", user.getKitchenId());
						if(user.getKitchenId().size()>0){
						request.getSession().setAttribute("restaurantId", kitchenService.getKitchenScreen(user.getKitchenId().get(0)).getRestaurantId());
						}
						return "redirect:/deliveryDashboard.jsp";
                     case "microKitchenManager":
                    	 break;
                     case "headChef" :
                    	request.getSession().setAttribute("role", role);
 						request.getSession().setAttribute("kitchenId", user.getKitchenId());
 						if(user.getKitchenId().size()>0){
 						request.getSession().setAttribute("restaurantId", kitchenService.getKitchenScreen(user.getKitchenId().get(0)).getRestaurantId());
 						}
 						return "redirect:/kitchenDashboard.jsp";
                     case "Call_Center_Associate" :
                        request.getSession().setAttribute("role", role);
                        request.getSession().setAttribute("organisationId", user.getOrgId());
                        request.getSession().setAttribute("userName", user.getUserName());
                        request.getSession().setAttribute("restaurantId", user.getRestaurantId());
                        return "redirect:/posDashboard.jsp";
				    }
				    request.getSession().setAttribute("link", map);
					String redirectPath = (String) request.getSession().getAttribute("requestpath");
					if (!StringUtility.isNullOrEmpty(redirectPath)) {
						request.getSession().removeAttribute("requestpath");
					} else {
						redirectPath = "/manageRestaurant.jsp";
					}
					return "redirect:"+redirectPath;
				}
			}
		}
		return "login";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	@ApiIgnore
	public String login(HttpServletRequest request, HttpServletResponse response,Map<String, Object> map) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username=auth.getName(); //request.getParameter(CSConstants.USERNAME);
		String password = (String) auth.getCredentials();//"";//request.getParameter(CSConstants.PASSWORD);
		String returnPath = userLogin(request, username, password, map);
		if (!StringUtility.isNullOrEmpty(returnPath)) {
			return returnPath;
		}
		return "login";
	}
	
	@RequestMapping(value="/forgotPassword", method=RequestMethod.GET)
	@ApiIgnore
	public String getForgotPassword(HttpServletRequest request, HttpServletResponse response) {
		return "forgotPassword";
	}
	
	@RequestMapping(value="/forgotPassword", method=RequestMethod.POST)
	@ApiIgnore
	public String forgotPassword(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) {
		String username = request.getParameter(CSConstants.USERNAME);
		String completeUrl = request.getRequestURL().toString();
		String resetPasswordUrl = "";
		User user = userService.getUserByUsername(username);
		if (user != null) {			
			resetPasswordUrl = completeUrl.substring(0, completeUrl.lastIndexOf("/forgotPassword")) + "/resetPassword.html?userId=" + user.getUserId() + "&code=" + user.getPasswordHash();
			MailerUtility.sendMail(user.getUserName(), "Password Reset Email", "Hi, Please click on this url " + resetPasswordUrl );
			map.put("message", "Password Reset link is Successfully sent on your email");
		} else {
		 map.put("error", "User Not found, Contact Admin to resolve.");
		}
		return "forgotPassword";
	}
	
	@RequestMapping(value="/resetPassword", method=RequestMethod.GET)
	@ApiIgnore
	public String resetPassword(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) {
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		String code = request.getParameter("code");
		User user = userService.getUser(userId);
		if (code.equals(user.getPasswordHash())) {
			map.put("userId", userId);
			map.put("code", code);
		}
		return "resetPassword";
	}
	
	@RequestMapping(value="/updatePassword", method=RequestMethod.POST)
	@ApiIgnore
	public String updatePassword(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) {
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		String code = request.getParameter("code");
		String newPassword = request.getParameter("newPassword");
		User user = userService.getUser(userId);
		if (code.equals(user.getPasswordHash())) {
			user.setPasswordHash(userService.getHash(newPassword));
			userService.updatePassword(user);
		}
		map.put("message", "Updated Password");
		return "updatePassword";
	}
	

	@RequestMapping(value="/getEmployeeDetails", method=RequestMethod.GET)
	@ApiIgnore
	@ResponseBody
	public TreeMap<String, Object> getEmployeeDetails(HttpServletRequest request, HttpServletResponse response) throws JSONException{
		if(request.getSession().getAttribute("userId")==null)
			return null;
		Integer userId = (Integer) request.getSession().getAttribute("userId");
		 return userService.getEmployeeDetails(userId);
		
	}
	
	@RequestMapping(value="/isAuthenticate", method=RequestMethod.POST)
	private @ResponseBody Map<String,String> userLoginAuth(HttpServletRequest request, @RequestParam String username, @RequestParam String password) {
		Map map = new HashMap();
		//String username =  request.getParameter("username");
		//String password = request.getParameter("password");
		if(!StringUtility.isNullOrEmpty(username) || !StringUtility.isNullOrEmpty(password)) {
			User user = userService.getUserByUsername(username);
			if (user != null) {
				    boolean isValid = userService.isValidUser(user, password);
					String role=user.getRole().getRole();
					if(!StringUtility.isNullOrEmpty(role)&&isValid){
						map.put("status","true");
					return map;
				}
			}
			else {
				map.put("status","flase");
				return map;
			}
		}
		 map.put("status","false");
		 return map;
	}
	@RequestMapping(value="/getEditAccess", method=RequestMethod.POST)
	public @ResponseBody Map<String,String> getEditAccess(HttpServletRequest request,@RequestParam String username,@RequestParam String password,
			@RequestParam String checkId, @RequestParam String ffcId, @RequestParam String remarks) {
		Map map = new HashMap();
		/*String username =  request.getParameter("username");
		String password = request.getParameter("password");
		String checkId = request.getParameter("checkId");
		String ffcId = request.getParameter("ffcId");*/
		String editRemark =remarks; //request.getParameter("remarks");
		boolean allow=false;
		if(!StringUtility.isNullOrEmpty(username) || !StringUtility.isNullOrEmpty(password)) {
			User user = userService.getUserByUsername(username);
			if (user != null) {
				    boolean isValid = userService.isValidUser(user, password);
					String role=user.getRole().getRole();
					if(!StringUtility.isNullOrEmpty(role)&&isValid){
					if(role.equalsIgnoreCase("fulfillmentCenterManager")){
						int id=Integer.parseInt(checkId);
						Check check = checkService.getCheck(id);
						Integer ffc = Integer.parseInt(ffcId);
						List<Integer> ffcList =  user.getKitchenId();
						for(Integer kId : ffcList){
							if(kId==ffc){
								allow=true;
								break;
							}
						}
						if(allow){
							check.setEditOrderRemark(username+" : "+editRemark);
							checkService.addCheck(check);
							map.put("status","true");
							return map;
						}else{
							map.put("status","false");
							map.put("message","You don't have access to this FFC");
							return map;
						}
					}else if(role.equalsIgnoreCase("restaurantManager")){
						int id=Integer.parseInt(checkId);
						Check check = checkService.getCheck(id);
						Integer ffc = Integer.parseInt(ffcId);
						List<Integer> restList =  user.getRestaurantId();
						for(Integer restId : restList){
							List<FulfillmentCenter> ffClist = kitchenService.getKitchenScreens(restId);
							for(FulfillmentCenter ffcL:ffClist){
								if(ffcL.getId()==ffc){
									allow=true;
									break;
								}
							}
						}
						if(allow){
							check.setEditOrderRemark(username+" : "+editRemark);
							checkService.addCheck(check);
							map.put("status","true");
							return map;
						}
						else{
							map.put("status","false");
							map.put("message","You don't have access to this FFC");
							return map;
					}
					}else if(role.equalsIgnoreCase("admin")){
							int id=Integer.parseInt(checkId);
							Check check = checkService.getCheck(id);
							check.setEditOrderRemark(username+" : "+editRemark);
					    	checkService.addCheck(check);
					    	map.put("status","true");
						return map;
					}
					else{
						map.put("status","false");
						map.put("message", "You don't have access please contact ffcManager or restaurantManager");
						return map;
					}
				}
			}
			else {
				map.put("status","flase");
				map.put("message", "No user found with this username");
				return map;
			}
		}
		 map.put("status","false");
		 map.put("message", "Please enter correct username or password");
		 return map;
		
	}
	
}
