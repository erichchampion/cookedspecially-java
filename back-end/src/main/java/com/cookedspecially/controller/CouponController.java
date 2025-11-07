/**
 * 
 */
package com.cookedspecially.controller;


import io.swagger.annotations.Api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import springfox.documentation.annotations.ApiIgnore;

import com.cookedspecially.domain.Check;
import com.cookedspecially.domain.Coupon;
import com.cookedspecially.domain.CouponResponse;
import com.cookedspecially.domain.JsonCouponInfo;
import com.cookedspecially.domain.OrderSource;
import com.cookedspecially.domain.PaymentType;
import com.cookedspecially.domain.Restaurant;
import com.cookedspecially.enums.CouponState;
import com.cookedspecially.service.CouponService;
import com.cookedspecially.service.RestaurantService;
import com.cookedspecially.service.DeliveryAreaService;



@Controller
@Component
@RequestMapping("/coupon")
@Api(description="Coupon REST API's")
public class CouponController {

	final static Logger logger = Logger.getLogger(CouponController.class);
	@Autowired
	private CouponService couponService;
	
	@Autowired
	private RestaurantService restaurantService;
	
	@Autowired
	private DeliveryAreaService deliveryAreaService;
	
	//For setting multiple delivery Area
		 @InitBinder
	protected void initBinder(WebDataBinder binder) {
		        binder.registerCustomEditor(List.class, "flatRules.deliveryAreas", new CustomCollectionEditor(List.class)
		          {
		            @Override
		            protected Object convertElement(Object element)
		            {
		                Integer id = null;

		                if(element instanceof String && !((String)element).equals("")){
		                    //From the JSP 'element' will be a String
		                    try{
		                        id = Integer.parseInt((String) element);
		                    }
		                    catch (NumberFormatException e) {
		                        System.out.println("Element was " + ((String) element));
		                        e.printStackTrace();
		                    }
		                }
		                else if(element instanceof Integer) {
		                    //From the database 'element' will be a Long
		                    id = (Integer) element;
		                }

		                return id != null ? deliveryAreaService.getDeliveryArea(id) : null;
		            }
		          });
		 }
		
	
		 
	@RequestMapping(value = "/listCoupon")
	@ApiIgnore
	public String listCoupon(Map<String, Object> map, HttpServletRequest request) {		
		return listCouponWithFilter(map,request,"Enabled|Disabled");
		
	}	
	@RequestMapping(value = "/listCoupon/{filterstring}")
	@ApiIgnore
	public String listCouponWithFilter(Map<String, Object> map, HttpServletRequest request,@PathVariable("filterstring") String filterstring) {		
		
		Integer restId=(Integer)request.getSession().getAttribute("restaurantId");
		if(filterstring.isEmpty())
			filterstring = "Enabled|Disabled";
		
		List<Coupon> couponList = filterCoupon(filterstring,restId);		
		
		map.put("couponList", couponList);		
		map.put("filterValue", filterstring);
		
		//returning page
		return "listCoupon";//return jsp page
	}	
	
    
	@RequestMapping("/delete/{couponId}")
	@ApiIgnore
	public String deleteCoupon(Map<String, Object> map, HttpServletRequest request, @PathVariable("couponId") Integer couponId) {

		try {
			Coupon coup = couponService.getCouponById(couponId);
					
			if (coup != null) {
				
				//dont edit coupon which already applied to existing checks
				if(coup.getCheck_used().size()>0)
				{
					//Since we cannot edit the coupon which already applied to any checks
					//make previous coupon disabled and inactive; it will be used only for repoeting
					makeCouponNonActive(coup);
				}
				else{
				couponService.removeCoupon(couponId);
				}
				
			}
			
		
		} catch (Exception e) {
			map.put("errorMsg", "Sorry, something went wrong and we could not delete this AddOn.");
		}
		
		//redirecting to api call to function (not jsp page)
		return "redirect:/coupon/listCoupon";//createCoupon(map, request);
	}
	@RequestMapping("/disableEnable/{couponId}")
	@ApiIgnore
	public String disableEnableCoupon(Map<String, Object> map, HttpServletRequest request, @PathVariable("couponId") Integer couponId,RedirectAttributes redirectAttributes) {

		try {
			Coupon coup = couponService.getCouponById(couponId);
			
			if(coup != null )
			{
				if(coup.getState() == CouponState.Disabled)
				{
				//only one previous coupon can exist enabled
					Coupon existingCoup=couponService.getEnabledCouponByCode(coup.getCouponCode(), coup.getRestaurantID());				
					if(existingCoup!=null )//if already existing enabled coupon with same code
					{
						redirectAttributes.addFlashAttribute("errorMsg", "Error: Coupon with same coupon code has already been enabled. Either disable or delete the previous one first.");
						//map.put("errorMsg", "Sorry, something went wrong and we could not Enable this Coupon.");					
						return "redirect:/coupon/listCoupon";
					}	
					coup.setState(CouponState.Enabled);
				}
				else
				{
					coup.setState(CouponState.Disabled);
					
				}
				couponService.updateCoupon(coup);
			}			
			
		
		} catch (Exception e) {
			map.put("errorMsg", "Sorry, something went wrong and we could not disable/enable this AddOn.");
		}
		
		//redirecting to api call to function (not jsp page)
		return "redirect:/coupon/listCoupon";//createCoupon(map, request);
	}
	
	@RequestMapping("/view/{couponId}")
	@ApiIgnore
	public String viewCoupon(Map<String, Object> map, HttpServletRequest request, @PathVariable("couponId") Integer couponId) {
		
		Coupon cpn = couponService.getCouponById(couponId);
		Integer restaurantId = (Integer)request.getSession().getAttribute("restaurantId");
		Restaurant restaurant  =  restaurantService.getRestaurant(restaurantId);
		
		map.put("coupon", cpn);				
		map.put("repeatTypes", com.cookedspecially.enums.CouponRepeatRule.values());
		map.put("orderSourceTypes",getOrderSourceNameList(restaurant.getParentRestaurantId()));
		map.put("paymentModeTypes", getPaymentModelist(restaurant.getParentRestaurantId()));
		map.put("deliveryArealist",deliveryAreaService.listDeliveryAreasByResaurant(restaurantId));
		map.put("selDeliveryArealist",cpn.getFlatRules().getDeliveryAreas());
		//map.put("checks",cpn.getCheck_used());
		map.put("editMode", 1);
		
		return "viewCoupon";//return jsp page
	}
	
	@RequestMapping(value = "/createCoupon")
	@ApiIgnore
	public String createCoupon(Map<String, Object> map, HttpServletRequest request) {
		Integer restaurantId = (Integer)request.getSession().getAttribute("restaurantId");
		Restaurant restaurant  =  restaurantService.getRestaurant(restaurantId);
		map.put("coupon", new Coupon());		
		map.put("repeatTypes", com.cookedspecially.enums.CouponRepeatRule.values());
		map.put("orderSourceTypes",getOrderSourceNameList(restaurant.getParentRestaurantId()));
		map.put("paymentModeTypes",getPaymentModelist(restaurant.getParentRestaurantId()));	
		map.put("deliveryArealist",deliveryAreaService.listDeliveryAreasByResaurant(restaurantId));	
		
		return "addCoupon";//return jsp page
	}	
	@RequestMapping("/edit/{couponId}")
	@ApiIgnore
	public String editCoupon(Map<String, Object> map, HttpServletRequest request, @PathVariable("couponId") Integer couponId) {
		
		Coupon cpn = couponService.getCouponById(couponId);
		Integer restaurantId = (Integer)request.getSession().getAttribute("restaurantId");
		Restaurant restaurant  =  restaurantService.getRestaurant(restaurantId);
		
		map.put("coupon", cpn);				
		map.put("repeatTypes", com.cookedspecially.enums.CouponRepeatRule.values());
		map.put("orderSourceTypes",getOrderSourceNameList(restaurant.getParentRestaurantId()));
		map.put("paymentModeTypes", getPaymentModelist(restaurant.getParentRestaurantId()));
		map.put("deliveryArealist",deliveryAreaService.listDeliveryAreasByResaurant(restaurantId));
		map.put("selDeliveryArealist",cpn.getFlatRules().getDeliveryAreas());
		//map.put("checks",cpn.getCheck_used());
		map.put("editMode", 1);
		
		return "addCoupon";//return jsp page
	}
	@RequestMapping("/copy/{couponId}")
	@ApiIgnore
	public String copyCoupon(Map<String, Object> map, HttpServletRequest request, @PathVariable("couponId") Integer couponId) {
		
		Coupon cpn = couponService.getCouponById(couponId);
		//Now to copy it as new coupon
		cpn.setCoupanId(null);
		cpn.setCouponRuleID(null);
		cpn.getFlatRules().setCoupanRuleId(null);
		
		Integer restaurantId = (Integer)request.getSession().getAttribute("restaurantId");
		Restaurant restaurant  =  restaurantService.getRestaurant(restaurantId);
		map.put("coupon", cpn);				
		map.put("repeatTypes", com.cookedspecially.enums.CouponRepeatRule.values());
		map.put("orderSourceTypes",getOrderSourceNameList(restaurant.getParentRestaurantId()));
		map.put("paymentModeTypes", getPaymentModelist(restaurant.getParentRestaurantId()));
		map.put("deliveryArealist",deliveryAreaService.listDeliveryAreasByResaurant(restaurantId));
		map.put("selDeliveryArealist",cpn.getFlatRules().getDeliveryAreas());
		//map.put("checks",cpn.getCheck_used());
		//map.put("editMode", 1);
		
		return "addCoupon"; //return jsp page
	}
	@RequestMapping(value = "/addCoupon")
	@ApiIgnore
	public String addCoupon(Map<String, Object> map,HttpServletRequest request,@RequestParam(value = "saveSubmitbtn") String strEdit, @ModelAttribute("coupon") Coupon coupon, 
			BindingResult result) {		
		
		//make coupon code to upper case
		coupon.setCouponCode(coupon.getCouponCode().toUpperCase());
		
		if(coupon.getCoupanId() != null)// implies it was in edit mode since we have previous coupon id
		{
			Coupon existingCoup=couponService.getCouponById(coupon.getCoupanId());
			
			//dont edit coupon which already applied to existing checks
			if(existingCoup.getCheck_used().size()>0)
			{
				//Since we cannot edit the coupon which already applied to any checks
				//make previous coupon disabled and inactive; it will be used only for repoeting
				makeCouponNonActive(existingCoup);
				
				//Now Add it as new coupon
				coupon.setCoupanId(null);
				coupon.setCouponRuleID(null);
				coupon.getFlatRules().setCoupanRuleId(null);
				coupon.setParentCouponId(existingCoup.getCoupanId());//maintain history
				couponService.addCoupon(coupon);
			}
			else// if not applied not existing check; allow editing
			{
				List<Check> list = new ArrayList<Check>();			
				for(Check check : existingCoup.getCheck_used()){
					list.add(check);
				}
				coupon.setCheck_used(list);			
				couponService.updateCoupon(coupon);	// just update the same coupon with edited fields
			}
		}
		else //implies new coupon added
		{
			//ALLOW same coupon code for multiple coupons in a restaurant
			
			//check if any coupon exist with same coupon code and same restaurant
//			Coupon existingCoup=couponService.getCouponByCode(coupon.getCouponCode(), restaurantId);
//			if(existingCoup!=null )//if already existing
//			{
//				logger.info("addCoupon-" + coupon.getCouponCode() + "-already exist, so cannot save");
//				result.rejectValue("couponCode", "couponCode", "Coupon Code already Exist! Try with diffrent code.");
//				
//				map.put("repeatTypes", com.cookedspecially.enums.CouponRepeatRule.values());
//				map.put("orderSourceTypes", getOrderSourceNameList(restaurant.getParentRestaurantId()));
//				map.put("paymentModeTypes", getPaymentModelist(restaurant.getParentRestaurantId()));		
//				return "addCoupon";//"redirect:/coupon/createCoupon";
//				
//			}
			couponService.addCoupon(coupon);				
		}
		
		//redirecting to api call to function (not jsp page)
		return "redirect:/coupon/listCoupon"	;
		
		
		//boolean bsavebtn = strEdit.equalsIgnoreCase("Save Coupon");
		//if(bsavebtn)// if save button clicked and coupon with this code already exist		
		
	}
	
 	@RequestMapping(value = "/getCouponDef.json", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
 	public @ResponseBody CouponResponse getCouponDef(@RequestBody JsonCouponInfo coupInfo,Model model, HttpServletRequest request) throws Exception {
 		
 		/*
 		 * 1. Find coupon
 		 * 2. Validate coupon
 		 * 3. return coupon def to client
 		 * 		If all well it return Coupon definition
 		 * 		Otherwise it will return error
 		*/			
 		logger.info("getCouponDef-Coupon Input Info " + "CouponCode:" + coupInfo.getCouponCode());
 		CouponResponse coupResp = couponService.getCouponDef(coupInfo);
 		return coupResp;
 	}
 	
	public List<String> getOrderSourceNameList(Integer orgId){
		List<String> osList =  new ArrayList<>();
			
			List<OrderSource>  orderSourceList = restaurantService.listOrderSourcesByOrgId(orgId);
			for(OrderSource orderSource : orderSourceList){
				osList.add(orderSource.getName());
			}
			for(com.cookedspecially.enums.check.OrderSource orderSource : com.cookedspecially.enums.check.OrderSource.values()){
				osList.add(orderSource.toString());
			}
			return osList;
	}
	public List<String> getPaymentModelist(Integer orgId){
		List<String> pmList =  new ArrayList<>();
		
			List<PaymentType> paymentTypeList = restaurantService.listPaymentTypeByOrgId(orgId);
			for(PaymentType paymentType : paymentTypeList){
				pmList .add(paymentType.getName());
			}
			pmList.add("Any");
			return pmList;
	}
	///Called from delete or editing coupon when it is already applied to few checks
	//TO make particular coupon Hidden or NonActive
	//which will be used only for reporting
	private void makeCouponNonActive(Coupon coup){
		coup.setState(CouponState.NonActive);//make it nonactive, which makes it disabled too
		couponService.updateCoupon(coup);		
	}
	private List<Coupon> filterCoupon(String filterstring, Integer restId){			
			
			List<Coupon> couponList = new ArrayList<Coupon>();
			
			
			String[] strEnums = filterstring.split("\\|");
			for(String str:strEnums){
				CouponState cs = CouponState.None;
				try {
					cs = CouponState.valueOf(str);
			    } catch (IllegalArgumentException iae) {
			        cs= null;
			    }
				
				if(cs != null){
					List<Coupon> temp =couponService.getCouponListByCouponState(cs, restId);
					
					couponList.addAll(temp);
					
					}			
			}		
			return couponList;		
	    //your code
	    }
		

}
