/**
 * 
 */
package com.cookedspecially.controller;

import com.cookedspecially.config.CSConstants;
import com.cookedspecially.domain.*;
import com.cookedspecially.dto.ResponseDTO;
import com.cookedspecially.dto.credit.CreditTypeDTO;
import com.cookedspecially.enums.Status;
import com.cookedspecially.enums.check.BasePaymentType;
import com.cookedspecially.enums.credit.BilligCycle;
import com.cookedspecially.enums.restaurant.ChargesType;
import com.cookedspecially.service.*;
import com.cookedspecially.utility.StringUtility;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.spring4.SpringTemplateEngine;

import springfox.documentation.annotations.ApiIgnore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

//import org.thymeleaf.spring3.SpringTemplateEngine;

/**
 * @author shashank, rahul, abhishek
 *
 */
@Component
@RequestMapping("/organization")
@Api(description="Organization REST API's")
public class OrganizationController {

	final static Logger logger = Logger.getLogger(OrganizationController.class);
    private static int MAXFILESIZE = 5;
    String Website_name;
    String MID;
    String Merchant_Key;
    String Industry_type_ID;
    String Channel_ID;
    @Autowired
	private RestaurantService restaurantService;
	@Autowired
	private TaxTypeService taxTypeService;
	@Autowired
	private DishTypeService dishTypeService;
	@Autowired
	private JavaMailSenderImpl mailSender;
	@Autowired
	private SpringTemplateEngine templateEngine;
	@Autowired
	private FulfillmentCenterService fulfillmentCenterService;
	@Autowired
	private MicroKitchenScreenService microKitchenScreenService;
	@Autowired
	private CustomerController customerControler;
    @Autowired
    private UserService userService;
	@Autowired
	private CheckService checkService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private DeliveryAreaService deliveryAreaService;
	@Autowired
	private KitchenScreenService kitchenService;
	
	@Autowired
	private SocialConnectorService socialConnectorService;
	
	@Autowired
	private ZomatoService zomatoService;
	
	@RequestMapping("/org/{restId}")
	@ApiIgnore
	public String listRestaurants(Map<String, Object> map, HttpServletRequest request, @PathVariable("restId")
	Integer restId) {
		Restaurant resturant = restaurantService.getRestaurant(restId);
		if(resturant.getRestaurantId() != null){
			request.getSession().setAttribute("restaurantId",resturant.getRestaurantId());
			request.getSession().setAttribute("parentRestaurantId", resturant.getParentRestaurantId());
			request.getSession().setAttribute("childRestaurantIdList", restaurantService.listRestaurantByParentId(restId));
			request.getSession().setAttribute("fulfillmentCenterList", fulfillmentCenterService.getKitchenScreens(restId));
			request.getSession().setAttribute("orgActive", fulfillmentCenterService.getKitchenScreens(restId));
			request.getSession().setAttribute("restaurantName",resturant.getRestaurantName());
			if(fulfillmentCenterService.getKitchenScreens(restId).size()>0){
			request.getSession().setAttribute("fulfillmentCenterId", fulfillmentCenterService.getKitchenScreens(restId).get(0).getId());
			}
			
			map.put("coupon/createCoupon","Manage Coupons");
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
			map.put("restaurant/edit","Edit Restaurant");
			map.put("restaurant/listDiscountCharges","Manage Discount/Charges");
			map.put("restaurant/listNutrientes","Manage Nutritional Info");
			map.put("dish/stockedDishes","Manage Stock");
			map.put("giftCard/","Manage Gift-Card");
			
			}
		request.getSession().setAttribute("link", map);
		String redirectPath = (String) request.getSession().getAttribute("requestpath");
		if (!StringUtility.isNullOrEmpty(redirectPath)) {
			request.getSession().removeAttribute("requestpath");
		} else {
			redirectPath = "/";
		}
		return "redirect:/manageRestaurant.jsp";
	}
	
	
	@RequestMapping("/employee")
	@ApiIgnore
	public String listEmployee(Map<String, Object> map, HttpServletRequest request) {
		
		List<FulfillmentCenter> fulfillmentCenterList  =  new ArrayList<FulfillmentCenter>();
		List<MicroKitchenScreen> microKitchenSrc =  new ArrayList<MicroKitchenScreen>();
		map.put("user", new User());
		map.put("userList", userService.listUserByOrg((Integer)request.getSession().getAttribute("parentRestaurantId")));
		List<Restaurant> restaurantList = restaurantService.listRestaurantByParentId((Integer)request.getSession().getAttribute("parentRestaurantId"));
		map.put("restaurantList",restaurantList);
		for(Restaurant rest :  restaurantList){
			List<FulfillmentCenter> fulfillCL =  fulfillmentCenterService.getKitchenScreens(rest.getRestaurantId());
			fulfillmentCenterList.addAll(fulfillCL);
			for(FulfillmentCenter fc  : fulfillCL){
				List<MicroKitchenScreen> microKitchenList = microKitchenScreenService.getMicroKitchenScreensByKitchen(fc.getId());
				microKitchenSrc.addAll(microKitchenList);
			}
		}
		map.put("fulfillmentCenterList",fulfillmentCenterList);
		map.put("microKitchenScreenList",microKitchenSrc);
		map.put("userRoles",userService.getUserRole());
		return "addUser";
	}
	
	@RequestMapping("/editEmployee/{userId}")
	@ApiIgnore
	public String editEmployee(Map<String, Object> map, HttpServletRequest request, @PathVariable("userId")
	Integer userId) {
		List<FulfillmentCenter> fulfillmentCenterList  =  new ArrayList<FulfillmentCenter>();
		List<MicroKitchenScreen> microKitchenSrc =  new ArrayList<MicroKitchenScreen>();
		map.put("user", userService.getUser(userId));
		map.put("userList", userService.listUserByOrg((Integer)request.getSession().getAttribute("parentRestaurantId")));
		List<Restaurant> restaurantList = restaurantService.listRestaurantByParentId((Integer)request.getSession().getAttribute("parentRestaurantId"));
		map.put("restaurantList",restaurantList);
		for(Restaurant rest :  restaurantList){
			List<FulfillmentCenter> fulfillCL =  fulfillmentCenterService.getKitchenScreens(rest.getRestaurantId());
			fulfillmentCenterList.addAll(fulfillCL);
			for(FulfillmentCenter fc  : fulfillCL){
				List<MicroKitchenScreen> microKitchenList = microKitchenScreenService.getMicroKitchenScreensByKitchen(fc.getId());
				microKitchenSrc.addAll(microKitchenList);
			}
		}
		map.put("fulfillmentCenterList",fulfillmentCenterList);
		map.put("microKitchenScreenList",microKitchenSrc);
		map.put("action", "updateUser");
		//map.put("kitchenScreenList", fulfillmentCenterService.getKitchenScreens((Integer) request.getSession().getAttribute("restaurantId")));
		//map.put("microKitchenScreenList", microKitchenScreenService.getMicroKitchenScreensByKitchen((Integer) request.getSession().getAttribute("restaurantId")));
		map.put("userRoles",userService.getUserRole());
		return "addUser";
	}
	
	@RequestMapping(value = "/addEmployee")
	@ApiIgnore
	public String addEmployee(@ModelAttribute("user")
	User user, BindingResult result,Map<String, Object> map , HttpServletRequest request) {
	   User existingUser=userService.getUserByUsername(user.getUserName());
	   if(existingUser!=null){
		   result.rejectValue("userName", "userName", "User Name All ready Exist! Try with diffrent username.");
		   return "addUser"; 
	   }
		user.getRole().setId(Integer.parseInt(request.getParameter("role")));
		user.setPasswordHash(userService.getHash(user.getPasswordHash()));
		FulfillmentCenter fc =null;
		Set<Integer> restId = new HashSet<Integer>() ; 
		List<Integer> restaurantIdList = new ArrayList<Integer>();
		List<Integer> ffcId = user.getKitchenId();
		if(ffcId!=null){
			for(int ks :ffcId){
				fc = fulfillmentCenterService.getKitchenScreen(ks);
				
				restId.add(fc.getRestaurantId());
			}
			restaurantIdList.addAll(restId);
			user.setRestaurantId(restaurantIdList);
			}
		user.setOrgId((Integer)request.getSession().getAttribute("parentRestaurantId"));
		userService.addUser(user);
		return "redirect:/organization/employee";
	}
	@RequestMapping(value = "/updateEmployee")
	@ApiIgnore
	public String updateEmployee(@ModelAttribute("user")
	User user, BindingResult result,Map<String, Object> map , HttpServletRequest request) {
		user.getRole().setId(Integer.parseInt(request.getParameter("role")));
		FulfillmentCenter fc =null;
		Set<Integer> restId = new HashSet<Integer>() ; 
		List<Integer> restaurantIdList = new ArrayList<Integer>();
		List<Integer> ffcId = user.getKitchenId();
		if(ffcId!=null){
			for(int ks :ffcId){
				fc = fulfillmentCenterService.getKitchenScreen(ks);
				restId.add(fc.getRestaurantId());
			}
			restaurantIdList.addAll(restId);
			user.setRestaurantId(restaurantIdList);
			}
		user.setOrgId((Integer)request.getSession().getAttribute("parentRestaurantId"));
		userService.updateUser(user);
		return "redirect:/organization/employee";
	}
	
	@RequestMapping("/deleteEmployee/{employeeId}")
	@ApiIgnore
	public String deleteEmployee(@PathVariable("employeeId") Integer employeeId) {
		userService.removeUser(employeeId);
		return "redirect:/organization/employee";
	}

	@RequestMapping(value = "/createRestaurant")
	@ApiIgnore
	public String createRestaurant(Map<String, Object> map  ,@ModelAttribute("restaurant") Restaurant restaurant, BindingResult result) {
		map.put("restaurant", new Restaurant());
		map.put("chargeTypes", ChargesType.values());
		map.put("timeZones", CSConstants.timeZoneIds);
		map.put("openFlag", CSConstants.openFlag);
		map.put("statusTypes", com.cookedspecially.enums.Status.values());
		ArrayList<String> countryName = new ArrayList<String>();
		String[] locales = Locale.getISOCountries();
		for (String countryCode : locales) {
		    Locale obj = new Locale("", countryCode);
		    countryName.add(obj.getDisplayCountry(Locale.ENGLISH));
		 }
		map.put("countryList", countryName);
		return "addRestaurant";
	}
	@RequestMapping(value = "/addRestaurant", method = RequestMethod.POST)
	@ApiIgnore
	public String addRestaurant(Map<String, Object> map ,HttpServletRequest request,HttpServletResponse response, @ModelAttribute("restaurant") Restaurant restaurant, BindingResult result) {
		map.clear();
		Integer orgId =  (Integer) request.getSession().getAttribute("parentRestaurantId");
		Restaurant existingRest=restaurantService.getRestaurantByName(restaurant.getRestaurantName());
		ArrayList<String> countryName = new ArrayList<String>();
		String[] locales = Locale.getISOCountries();
		TreeMap<String, Object>  map1 = new TreeMap<String, Object>();
		for (String countryCode : locales) {
		    Locale obj = new Locale("", countryCode);
		    countryName.add(obj.getDisplayCountry(Locale.ENGLISH));
		 }
		map1.put("countryList", countryName);
		if(existingRest!=null){
			map.put("restaurant", restaurant);
			result.rejectValue("restaurantName", "restaurantName", "Restaurant Name All ready Exist! Try with diffrent Name.");
		   return "addRestaurant";
		}
		User user=userService.getUser((Integer) request.getSession().getAttribute("userId"));
		restaurantService.addRestaurant(restaurant);
		if(restaurant.getParentRestaurantId()==null){
			List<Integer> ls =   new ArrayList<Integer>();
			ls.add(restaurant.getRestaurantId());
			user.setRestaurantId(ls);
			userService.addUser(user);
			UserController uc =  new UserController();
			return uc.logout(request, response);
		}else{
			List<Integer> list = user.getRestaurantId();
			list.add(restaurant.getRestaurantId());
			userService.addUser(user);
		}
		map.put("organization/employee","Manage Employees");
		map.put("organization/edit","Edit Organization");
		map.put("organization/createRestaurant"," Add Restaurant");
		map.put("organization/orderSource", "Add Order Source");
		map.put("organization/paymentType", "Add Payment Type");
		List<Restaurant> restaurantList =  restaurantService.listRestaurantByParentId(orgId);
		for(Restaurant rest :  restaurantList){
			map.put("organization/org/"+rest.getRestaurantId(),rest.getRestaurantName());
		}
		 request.getSession().setAttribute("orgLink", map);
		return "redirect:/";	
	}
	

	@RequestMapping("/delete/{restaurantId}")
	@ApiIgnore
	public String deleteRestaurant(@PathVariable("restaurantId") Integer restaurantId) {
		restaurantService.removeRestaurant(restaurantId);
		return "redirect:/restaurant/";
	}
	
	@ApiOperation(value="[*] Get all restaurant and organization inforamation. And OrderSource and Payment Type list based on organization.")
	@RequestMapping(value="getOrganizationInfo", method=RequestMethod.GET)
	public  @ResponseBody OrganizationInfo getResturantsByOrgId(HttpServletRequest request , HttpServletResponse response, @RequestParam(required=false) String orgId){
		String orgIdParam =orgId;
		Integer restId;
		if(orgIdParam != null && !orgIdParam.equals("")){
			restId = Integer.parseInt(orgIdParam);
		}
		else{
			restId = (Integer) request.getSession().getAttribute("organisationId");
		}

		//it is a organization
		Restaurant rest=restaurantService.getRestaurant(restId);
		OrganizationInfo orgInfo = new OrganizationInfo(rest);
		List<RestaurantInfo> setList =  new ArrayList<RestaurantInfo>();
		List<Restaurant> restaurantList = restaurantService.listRestaurantByParentId(rest.getRestaurantId());
		List<OrderSource> orderSource =  restaurantService.listOrderSourcesByOrgId(restId);
		List<OrderSource> orderSourceList  = new ArrayList<OrderSource>();
		for(OrderSource os:orderSource){
			if(com.cookedspecially.enums.Status.INACTIVE.toString().equalsIgnoreCase(os.getStatus())){
				continue;
			}
			else {
				orderSourceList.add(os);
			}
		}
		List<PaymentType> filteredPaymentType =  new ArrayList<PaymentType>(); 
		List<PaymentType> paymentType = restaurantService.listPaymentTypeByOrgId(restId);
		for(PaymentType pt : paymentType){
			if(com.cookedspecially.enums.Status.INACTIVE.toString().equalsIgnoreCase(pt.getStatus())){
				continue;
			}
			filteredPaymentType.add(pt);
		}
		
		for(Restaurant restaurant : restaurantList){
			if(restaurant.getStatus()==com.cookedspecially.enums.Status.ACTIVE){
				List<TaxType> taxList = taxTypeService.listTaxTypesByRestaurantId(restaurant.getRestaurantId());
				List<FulfillmentCenter> ffCenter  =  fulfillmentCenterService.getKitchenScreens(restaurant.getRestaurantId());
				restaurant.setTaxList(taxList);
				restaurant.setFfCenter(ffCenter);
				RestaurantInfo restInfo  = new  RestaurantInfo(restaurant); 
				setList.add(restInfo);
			}
		}
		if(orgId!=null){
			List<SocialConnector> socialConnectorList= new ArrayList<SocialConnector>();
			List<SocialConnector> connectorList= socialConnectorService.listSocialConnectorByOrgId(Integer.parseInt(orgId));
			for(SocialConnector sc :  connectorList){
				if(sc.getStatus()==Status.ACTIVE){
					socialConnectorList.add(sc);
				}
			}
			orgInfo.setSocialConnectors(socialConnectorList);
		}
		orgInfo.setOrderSource(orderSourceList);
		orgInfo.setRestaurants(setList);
		orgInfo.setPaymentType(filteredPaymentType);
		return orgInfo;
	}
	
	@RequestMapping("/orderSource")
	@ApiIgnore
	public String listOrderSource(Map<String, Object> map, HttpServletRequest request) {

		map.put("orderSource", new OrderSource());
		com.cookedspecially.enums.check.OrderSource[] os =  new com.cookedspecially.enums.check.OrderSource[4];
		int count = 0;
		for(com.cookedspecially.enums.check.OrderSource orderS : com.cookedspecially.enums.check.OrderSource.values()){
		 if(orderS.name().equalsIgnoreCase("Third_Party") || orderS.name().equalsIgnoreCase("Any")){
			 continue;
		 }else{
			 os[count] = orderS;
			
		}
		 count++;
		}
		map.put("baseOrderSource",os);
		map.put("orderSourceList", restaurantService.listOrderSourcesByOrgId((Integer)request.getSession().getAttribute("parentRestaurantId")));
		map.put("statusTypes", com.cookedspecially.enums.Status.values());
		return "orderSource";
	}

	@RequestMapping(value = "/addOrderSource", method = RequestMethod.POST)
	@ApiIgnore
	public String addOrderSource(@ModelAttribute("orderSource")
	OrderSource orderSource , BindingResult result, Map<String, Object> map) {
		restaurantService.addOrderSource(orderSource);
		map.put("statusTypes", com.cookedspecially.enums.Status.values());
		return "redirect:/organization/orderSource/";
	}
	
	@RequestMapping("/editOrderSource/{id}")
	@ApiIgnore
	public String editOrderSource(Map<String, Object> map, HttpServletRequest request, @PathVariable("id")	Integer id) {

		OrderSource orderSource = restaurantService.getOrderSources(id);
		map.put("orderSource", orderSource);
		com.cookedspecially.enums.check.OrderSource[] os =  new com.cookedspecially.enums.check.OrderSource[4];
		int count = 0;
		for(com.cookedspecially.enums.check.OrderSource orderS : com.cookedspecially.enums.check.OrderSource.values()){
			
		 if(orderS.name().equalsIgnoreCase("Third_Party") ||  orderS.name().equalsIgnoreCase("Any")){
			 continue;
		 }else{
			 os[count] = orderS;

		}
		 count++;
		}
		map.put("baseOrderSource",os);
		map.put("orderSourceList", restaurantService.listOrderSourcesByOrgId((Integer)request.getSession().getAttribute("parentRestaurantId")));
		map.put("statusTypes", com.cookedspecially.enums.Status.values());
		return "orderSource";
	}
	
	@RequestMapping("/deleteOrderSource/{id}")
	@ApiIgnore
	public String deleteOrderSource(@PathVariable("id")
	Integer id) {
		restaurantService.removeOrderSources(id);
		return "redirect:/organization/orderSource/";
	}
	
	@RequestMapping("/paymentType")
	@ApiIgnore
	public String listPaymentType(Map<String, Object> map, HttpServletRequest request) {
		map.put("paymentType", new PaymentType());
		map.put("basePaymentType", BasePaymentType.values());
		map.put("statusTypes", com.cookedspecially.enums.Status.values());
		map.put("paymentTypeList", restaurantService.listPaymentTypeByOrgId((Integer)request.getSession().getAttribute("parentRestaurantId")));
		return "paymentType";
	}

	@RequestMapping("/editPaymentType/{id}")
	@ApiIgnore
	public String editPaymentType(Map<String, Object> map, HttpServletRequest request, @PathVariable("id")	Integer id) {
		PaymentType paymentType = restaurantService.getPaymentType(id);
		map.put("paymentType", paymentType);
		map.put("basePaymentType", BasePaymentType.values());
		map.put("statusTypes", com.cookedspecially.enums.Status.values());
		map.put("paymentTypeList", restaurantService.listPaymentTypeByOrgId((Integer)request.getSession().getAttribute("parentRestaurantId")));
		return "paymentType";
	}
	
	@RequestMapping(value = "/addPaymentType", method = RequestMethod.POST)
	@ApiIgnore
	public String addPaymentType(@ModelAttribute("PaymentType") PaymentType paymentType, BindingResult result) {
		restaurantService.addPaymentType(paymentType);
		return "redirect:/organization/paymentType/";
	}

	@RequestMapping("/deletePaymentType/{id}")
	@ApiIgnore
	public String deletePaymentType(@PathVariable("id") Integer id) {
		restaurantService.removePaymentType(id);
		return "redirect:/organization/paymentType/";
	}
	
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ApiIgnore
	public String updateUser(HttpServletRequest request,Map<String, Object> map, @ModelAttribute("user")
	Restaurant restaurant, BindingResult result, @RequestParam("files[0]") MultipartFile portraitImage, @RequestParam("files[1]") MultipartFile landscapeImage, 
	@RequestParam("files[2]") MultipartFile appCacheIcon, @RequestParam("files[3]") MultipartFile buttonIcon, @RequestParam("files[4]") MultipartFile marketingImage, @RequestParam("files[5]") MultipartFile closeImageLink) {
		FileOutputStream fos = null;
		ArrayList<MultipartFile> files = new ArrayList<MultipartFile>();
		files.add(portraitImage);
		files.add(landscapeImage);
		files.add(appCacheIcon);
		files.add(buttonIcon);
		files.add(marketingImage);
		files.add(closeImageLink);
		//Restaurant oldRestaurantData = null;
		/*if(restaurant.getRestaurantId()!=null){
		 oldRestaurantData =  restaurantService.getRestaurant(restaurant.getRestaurantId());
		}
		if(oldRestaurantData.isEnableCustCredit() && (!restaurant.isEnableCustCredit())){
			System.out.println(restaurant.getDefaultCreditType());
			if(restaurant.getDefaultCreditType() !=null){
			List<Integer> idList = customerService.listCustomerId(restaurant.getRestaurantId());
			if(restaurant.getRestaurantId()!=null){
			for(Integer custId : idList){
				try {
				customerService.removeCustomerCredit(custId,restaurant.getRestaurantId());
			} catch (Exception e) {
				System.out.println("Exception");
			}
			}
			}
		}
		}else if((!oldRestaurantData.isEnableCustCredit() && restaurant.isEnableCustCredit())){
			if(restaurant.getDefaultCreditType() !=null){
			List<Integer> idList = customerService.listCustomerId(restaurant.getRestaurantId());
			CustomerCreditDTO  customerCreditDTO =  new  CustomerCreditDTO();
			CreditType creditType= customerService.getCreditType(restaurant.getDefaultCreditType());
			if(creditType!=null){
			for(Integer custId : idList){
				customerCreditDTO.creditTypeId =creditType.getId(); 
				customerCreditDTO.customerId=custId;
				customerCreditDTO.creditBalance=0;
				customerCreditDTO.maxLimit=creditType.getMaxLimit();
			try {
				customerService.enableCustomerCredit(customerCreditDTO,restaurant.getRestaurantId());
			} catch (Exception e) {
				System.out.println("Exception");
			}
			}
			System.out.println(idList.size());
		}
		}
		}*/
		
		if (files != null && files.size() == 6) {
			String[] fileUrls = new String[6];
			int iter = 0;
			for (MultipartFile file : files) {
				String fileUrl = null;
				if (iter==0) {
					fileUrl = restaurant.getBusinessPortraitImageUrl();
				} else if (iter==1) {
					fileUrl = restaurant.getBusinessLandscapeImageUrl();
				} else if (iter == 2) {
					fileUrl = restaurant.getAppCacheIconUrl();
				}
				else if (iter == 4) {
					fileUrl = restaurant.getMarketingImage();
				}
				else if (iter == 5) {
					fileUrl = restaurant.getCloseImageLink();
				}
				else {
					fileUrl = restaurant.getButtonIconUrl();
				}
				
				if (!file.isEmpty()) {
					if (file.getSize() > MAXFILESIZE*1000*1000) {
						String rejectValueName = null;
						if (iter == 0) {
							rejectValueName = "businessPortraitImageUrl";
						} else if (iter == 1) {
							rejectValueName = "businessLandscapeImageUrl";
						} else if (iter == 2) {
							rejectValueName = "appCacheIconUrl";
						}
						else if (iter == 4) {
							rejectValueName = "marketingImage";
						}
						else if (iter == 5) {
							rejectValueName = "closeImageLink";
						}
						else {
							rejectValueName = "buttonIconUrl";
						}
						result.rejectValue(rejectValueName, "error.upload.sizeExceeded", "You cannot upload the file of more than " + MAXFILESIZE + " MB");
						map.put("user", restaurant);
						return "editOrgUser";
					}
					try {
						byte[] bytes = file.getBytes();
						String fileDir = File.separator + "static" + File.separator + restaurant.getRestaurantId() + File.separator ;
						String filePrefix = null;
						if (iter == 0) {
							filePrefix = "portrait";
						} else if (iter == 1) {
							filePrefix = "landscape";
						} else if (iter == 2) {
							filePrefix = "appCache";
						}
						 else if (iter == 4) {
							filePrefix = "marketingImage";
						}
						 else if (iter == 5) {
							filePrefix = "closeImageLink";
						}
						else {
							filePrefix = "button";
						}
						
						fileUrl = fileDir + filePrefix + "_" + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9_.]", "_");
						fileUrls[iter] = fileUrl;
						File dir = new File("webapps" + fileDir);
						if (!dir.exists()) { 
							dir.mkdirs();
						}
						File outfile = new File("webapps" + fileUrl); 
						fos = new FileOutputStream(outfile);
						fos.write(bytes);
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						if (fos != null) {
							try {
								fos.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
				iter++;
			}
			
			for (iter = 0; iter < 6; iter++) {
				String existingImageUrl = null;
				if (iter==0) {
					existingImageUrl = restaurant.getBusinessPortraitImageUrl();
				} else if (iter == 1) {
					existingImageUrl = restaurant.getBusinessLandscapeImageUrl();
				} else if (iter == 2) {
					existingImageUrl = restaurant.getAppCacheIconUrl();
				}
				else if (iter == 4) {
					existingImageUrl = restaurant.getMarketingImage();
				}
				else if (iter == 5) {
					existingImageUrl = restaurant.getCloseImageLink();
				} 
				else {
					existingImageUrl = restaurant.getButtonIconUrl();
				}
				
				String fileUrl = fileUrls[iter];
				if(!StringUtility.isNullOrEmpty(fileUrl)) {
					if (!fileUrl.equals(existingImageUrl) && !StringUtility.isNullOrEmpty(existingImageUrl) && existingImageUrl.startsWith("/")) {
						File oldFile = new File("webapps" + existingImageUrl);
						if (oldFile.exists()) {
							oldFile.delete();
						}
					}
					if (iter == 0) {
						restaurant.setBusinessPortraitImageUrl(fileUrl);
					} else if (iter == 1) {
						restaurant.setBusinessLandscapeImageUrl(fileUrl);
					} else if (iter == 2) {
						restaurant.setAppCacheIconUrl(fileUrl);
					} 
					else if (iter == 4) {
						restaurant.setMarketingImage(fileUrl);
					}
					else if (iter == 5) {
						restaurant.setCloseImageLink(fileUrl);
					}
					else {
						restaurant.setButtonIconUrl(fileUrl);
					}
				}
			}
		}

		Restaurant dbRestaurant= restaurantService.getRestaurant(restaurant.getRestaurantId());
		if(dbRestaurant != null){
			restaurant.setInvoicePrefix(dbRestaurant.getInvoicePrefix());
			restaurant.setInvoiceStartCounter(dbRestaurant.getInvoiceStartCounter());
			//dbRestaurant = null;
		}

		if("Closed".equalsIgnoreCase(dbRestaurant.getOpenFlag()) && ("Always Open".equalsIgnoreCase(restaurant.getOpenFlag())|| "Open During Normal Hours".equalsIgnoreCase(restaurant.getOpenFlag()))){
			ResponseDTO resp  = zomatoService.setZomatoRestaurantStatus(restaurant, request);
			logger.info("Opening restaurant : "+resp.result);
		}else if(("Always Open".equalsIgnoreCase(dbRestaurant.getOpenFlag()) || "Open During Normal Hours".equalsIgnoreCase(dbRestaurant.getOpenFlag())) && "Closed".equalsIgnoreCase(restaurant.getOpenFlag())){
			ResponseDTO resp = zomatoService.setZomatoRestaurantStatus(restaurant, request);
			logger.info("Closing restaurant : "+resp.result);
		}
		
		restaurantService.addRestaurant(restaurant);    
		return "redirect:/";
	}

	@RequestMapping(value="/edit", method=RequestMethod.GET)
	@ApiIgnore
	public String editUser(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) {
		Object userIdObj = request.getSession().getAttribute("parentRestaurantId");
		
		if(userIdObj != null) {
			map.put("user", restaurantService.getRestaurant((Integer) userIdObj));
			map.put("chargeTypes", ChargesType.values());
			map.put("timeZones", CSConstants.timeZoneIds);
			map.put("openFlag", CSConstants.openFlag);
			map.put("statusTypes", com.cookedspecially.enums.Status.values());
			ArrayList<String> countryName = new ArrayList<String>();
			String[] locales = Locale.getISOCountries();
			for (String countryCode : locales) {
			    Locale obj = new Locale("", countryCode);
			    countryName.add(obj.getDisplayCountry(Locale.ENGLISH));
			}
			map.put("countryList", countryName);
			List<CreditType> creditTypeList =  customerService.listCustomerCreditType((Integer)userIdObj);
			map.put("creditTypeList",creditTypeList);
			return "editOrgUser";
		}
		
		return "redirect:/";
	}
	@RequestMapping(value="/removeImage",method=RequestMethod.GET )
	@ApiIgnore
	public String removeCloseImage(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response){
	String parameter = request.getParameter("parameter");
	Object userIdObj = request.getSession().getAttribute("parentRestaurantId");
		if(userIdObj != null) {
			Restaurant rest = restaurantService.getRestaurant((Integer) userIdObj);
			if(parameter.equalsIgnoreCase("businessLandscapeImageUrl")){
				rest.setBusinessLandscapeImageUrl("");
			}else if(parameter.equalsIgnoreCase("businessPortraitImageUrl")){
				rest.setBusinessPortraitImageUrl("");
			}
			else if(parameter.equalsIgnoreCase("closeImageLink")){
				rest.setCloseImageLink("");
			}
			else if(parameter.equalsIgnoreCase("marketingImage")){
				rest.setMarketingImage("");
			}
			restaurantService.addRestaurant(rest);
		}
		return "redirect:/organization/edit/";
	}
	
	@RequestMapping(value="/creditType",method=RequestMethod.GET)
	@ApiIgnore
	public String creditCard(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response){
		String parameter = request.getParameter("parameter");
		Object orgId = request.getSession().getAttribute("parentRestaurantId");
			CreditType creditType =  new  CreditType();
			map.put("creditType", creditType);
			SortedMap<BilligCycle,String> billingCycle =  new TreeMap<BilligCycle,String>();
			for(BilligCycle bc : BilligCycle.values()){
				if(bc==BilligCycle.on15thOfEveryMonth){
					billingCycle.put(bc, "Monthly billing (billed 15th of every month)");
				}else if(bc == BilligCycle.on1stAnd15thOfEveryMonth){
					billingCycle.put(bc,"Twice a month billing (billed 1st and 15th of every month)");
				}else if(bc==BilligCycle.on1stDayOfMonth){
					billingCycle.put(bc,"Monthly billing (billed 1st of every month)");
				}else if(bc==BilligCycle.ONE_OFF){
					billingCycle.put(bc,"Rolling billing (will get added to next invoice)");
				}else if(bc==BilligCycle.onEachSUN){
					billingCycle.put(bc,"Weekly billing (every Monday)");
				}
			}
			map.put("billingCycles",billingCycle);
			List<CreditType>  cc =  (List<CreditType>)customerControler.listCustomerCreditTypes(request);
			map.put("listCreditType", cc);
			return "creditType";
		}
	
	@RequestMapping(value = "/addCreditType", method = RequestMethod.POST)
	@ApiIgnore
	public String addCreditType(Map<String, Object> map, @ModelAttribute("creditType") CreditType creditType,BindingResult result, HttpServletRequest request) throws Exception {
	
		CreditTypeDTO creditTypeDTO = new CreditTypeDTO();
		creditTypeDTO.billingCycle = creditType.getBillingCycle();
		creditTypeDTO.name=creditType.getName();
		creditTypeDTO.banner=creditType.getBanner();
		creditTypeDTO.maxLimit=creditType.getMaxLimit();
		ResponseDTO responseDto= null;
		if(creditType.getId()==0){
			 responseDto = customerControler.addCustomerCreditType(creditTypeDTO, result, request);
		}else{
			 responseDto = customerControler.editCustomerCredit(creditType, result, request);
		}
		map.put("result",responseDto.result);
		map.put("message",responseDto.message);
		return "redirect:/organization/creditType";
	}
	
	@RequestMapping("/editCreditType/{creditTypeId}")
	@ApiIgnore
	public String editCreditType(Map<String, Object> map, HttpServletRequest request, @PathVariable("creditTypeId") Integer creditTypeId) {
		CreditType creditType = customerService.getCreditType(creditTypeId);
		map.put("creditType",creditType);
		SortedMap<BilligCycle,String> billingCycle =  new TreeMap<BilligCycle,String>();
		for(BilligCycle bc : BilligCycle.values()){
			if(bc==BilligCycle.on15thOfEveryMonth){
				billingCycle.put(bc, "Monthly billing (billed 15th of every month)");
			}else if(bc == BilligCycle.on1stAnd15thOfEveryMonth){
				billingCycle.put(bc,"Twice a month billing (billed 1st and 15th of every month)");
			}else if(bc==BilligCycle.on1stDayOfMonth){
				billingCycle.put(bc,"Monthly billing (billed 1st of every month)");
			}else if(bc==BilligCycle.ONE_OFF){
				billingCycle.put(bc,"Rolling billing (will get added to next invoice)");
			}else if(bc==BilligCycle.onEachSUN){
				billingCycle.put(bc,"Weekly billing (every Monday)");
			}
		}
		map.put("billingCycles",billingCycle);
		List<CreditType>  cc =  (List<CreditType>)customerControler.listCustomerCreditTypes(request);
		map.put("listCreditType", cc);
		return "creditType";
	}
	
	@RequestMapping("/deleteCustomerCreditType/{creditTypeId}")
	@ApiIgnore
	public String deleteCreditType(Map<String, Object> map, HttpServletRequest request, @PathVariable("creditTypeId") int creditTypeId) {
		ResponseDTO responseDto =  customerControler.deleteCustomerCreditType(creditTypeId, request);	
		map.put("result",responseDto.result);
		map.put("message",responseDto.message);
		return "redirect:/organization/creditType";
	}

}

