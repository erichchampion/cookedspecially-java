/**
 * 
 */
package com.cookedspecially.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import springfox.documentation.annotations.ApiIgnore;

import com.cookedspecially.domain.Check;
import com.cookedspecially.domain.Customer;
import com.cookedspecially.domain.Dish;
import com.cookedspecially.domain.DishType;
import com.cookedspecially.domain.FulfillmentCenter;
import com.cookedspecially.domain.OrderSource;
import com.cookedspecially.domain.PaymentType;
import com.cookedspecially.domain.Restaurant;
import com.cookedspecially.domain.TaxType;
import com.cookedspecially.domain.Transaction;
import com.cookedspecially.domain.User;
import com.cookedspecially.enums.Status;
import com.cookedspecially.enums.check.BasePaymentType;
import com.cookedspecially.enums.check.PaymentMode;
import com.cookedspecially.service.CashRegisterService;
import com.cookedspecially.service.CheckService;
import com.cookedspecially.service.CustomerService;
import com.cookedspecially.service.DishService;
import com.cookedspecially.service.DishTypeService;
import com.cookedspecially.service.FulfillmentCenterService;
import com.cookedspecially.service.OrderService;
import com.cookedspecially.service.RestaurantService;
import com.cookedspecially.service.TaxTypeService;
import com.cookedspecially.service.UserService;
import com.cookedspecially.utility.StringUtility;


/**
 * @author shashank
 *
 */
@Controller
@RequestMapping("/reports")
public class ReportingController {

	final static Logger logger = Logger.getLogger(ReportingController.class);
	@Autowired
	private CheckService checkService;
	
	@Autowired
	private TaxTypeService taxTypeService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private DishService dishService;
	
	@Autowired
	private RestaurantService restaurantService;
	//private UserService userService;
	
	@Autowired
	private FulfillmentCenterService fulfillmentService;
	
	@Autowired
	private DishTypeService dishTypeService;

	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private CashRegisterService cashRegisterService;
	
	@Autowired
	private FulfillmentCenterService kitchenService;
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("/")
	@ApiIgnore
	public String reportWithDateRange(Map<String, Object> map, HttpServletRequest request) {
		return "reports";
	}
	
	@RequestMapping(value = "checkReport.xls", method=RequestMethod.GET)
	@ApiIgnore
	public ModelAndView generateCheckReport(HttpServletRequest req,@RequestParam("restaurantId") Integer restaurantId, ModelAndView mav) {
		try{
				logger.info("User has generated the CHECK report : userName: "+req.getSession().getAttribute("userName"));
		}catch(Exception e){
			e.printStackTrace();
		}
		List<Check> checks = checkService.getAllOpenChecks(restaurantId);
		Map<String, Object> reportDataMap = new HashMap<String, Object>();
		reportDataMap.put("checks", checks);
		reportDataMap.put("reportName", "checkReport");
		//User user = userService.getUser(restaurantId);
		Restaurant rest=restaurantService.getRestaurant(restaurantId);
		reportDataMap.put("restaurantName", rest.getBussinessName());
		reportDataMap.put("user", rest);
		reportDataMap.put("Headers", Arrays.asList("Id", "price"));
		return new ModelAndView("ExcelReportView", "reportData", reportDataMap);
	}
	
	@RequestMapping(value = "dailyInvoice.xls", method=RequestMethod.GET)
	@ApiIgnore
	public ModelAndView dailyInvoiceExcel(HttpServletRequest req, ModelAndView mav) throws ParseException {
		
		try{
				logger.info("User has generated the dailyInvoice report : userName: "+req.getSession().getAttribute("userName"));
			}catch(Exception e){
				e.printStackTrace();
		}
		Integer restaurantId = Integer.parseInt(req.getParameter("restaurantId"));
		//User user = userService.getUser(restaurantId);
		Restaurant rest=restaurantService.getRestaurant(restaurantId);
		String backDaysString = req.getParameter("backDays");
		String startDateStr = req.getParameter("startDate");
		String endDateStr = req.getParameter("endDate");
		Date startDate = new Date();
		Date endDate = new Date();
		DateFormat formatter;
		formatter = new SimpleDateFormat("yyyy-MM-dd");
		formatter.setTimeZone(TimeZone.getTimeZone(rest.getTimeZone()));
		
		if (StringUtility.isNullOrEmpty(startDateStr)) {
			Calendar yesterday = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
			int minusDays = 0;
			if (!StringUtility.isNullOrEmpty(backDaysString)) {
				minusDays = -1 * Integer.parseInt(backDaysString);
			}
			yesterday.add(Calendar.DAY_OF_YEAR, minusDays);
			yesterday.set(Calendar.HOUR_OF_DAY, 0);
			yesterday.set(Calendar.MINUTE, 0);
			yesterday.set(Calendar.SECOND, 0);
			yesterday.set(Calendar.MILLISECOND, 0);
			startDate = yesterday.getTime();
		} else {
			startDate = formatter.parse(startDateStr);
		}
		
		if (StringUtility.isNullOrEmpty(endDateStr)) {
			Calendar tomorrow = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
			tomorrow.add(Calendar.DAY_OF_YEAR, 1);
			tomorrow.set(Calendar.HOUR_OF_DAY, 0);
			tomorrow.set(Calendar.MINUTE, 0);
			tomorrow.set(Calendar.SECOND, 0);
			tomorrow.set(Calendar.MILLISECOND, 0);
			endDate = tomorrow.getTime();	
		} else {
			endDate = formatter.parse(endDateStr);
		}
		
		List<Check> data = checkService.getDailyInvoice(restaurantId, startDate, endDate);
				
		Map<String, Object> reportDataMap = new HashMap<String, Object>();
		reportDataMap.put("data", data);
		reportDataMap.put("reportName", "Daily Invoice");
		reportDataMap.put("user", rest);
		List<String> headers = new ArrayList<String>(Arrays.asList("No.","Check Id", "Invoice#","Name","Phone No","email", "Fulfillment Center","Order Time","Delivery Time", "Delivery Area", "Type","Source","Sub Total","Coupon Discount Amount","Discount Amount")); 	
		
		List<String> taxList  =  new ArrayList<String>();
		List<TaxType> taxType =  taxTypeService.listAllActiveInactiveTaxesByRestaurantId(restaurantId);
		
		/*if(rest.getParentRestaurantId()==32){
			headers.add("VAT");
			taxList.add("VAT");
		}*/
		for(TaxType taxName:  taxType){
			/*if(taxName.getDishType().equalsIgnoreCase("Default") && taxName!=null){*/
			/*if(taxName.getStatus()==Status.ACTIVE){*/
				headers.add(taxName.getName());	
				taxList.add(taxName.getTaxTypeId().toString());
			/*}*/
			/*}*/
		}

	    headers.add("Delivery Charges");
	    headers.add("Total(incl. Taxes)");
	    headers.add("Customer Credit Bill");
	    headers.add("Invoice Total");
		//List<String> dishTypes = checkService.getUniqueDishTypes(restaurantId);
		List<DishType> dishTypes = dishTypeService.listDishTypesByRestaurantId(restaurantId);
		Set<String> dishTypesStrs = new HashSet<String>();
		for (DishType dishType : dishTypes) {
			dishTypesStrs.add(dishType.getName());
		}
		if (!dishTypesStrs.contains("OTHERS")) {
			dishTypesStrs.add("OTHERS");
		}
		
		headers.addAll(dishTypesStrs);
		
		List<PaymentType> pt = (List<PaymentType>) restaurantService.listPaymentTypeByOrgId(rest.getParentRestaurantId());
		for(PaymentType paymentType :pt ){
			headers.add(paymentType.getName());
		}
		headers.add("Order Status");
		headers.add("Customer Status");
		reportDataMap.put("paymentType",pt);
		headers.add("");
		List<Customer> customerData = customerService.getCustomerByParams(null, "", "", rest.getParentRestaurantId());
		
		reportDataMap.put("customerData", customerData);
		List<FulfillmentCenter> kitchens =  kitchenService.getKitchenScreens(restaurantId);
		reportDataMap.put("kitchenScreens",kitchens);
		reportDataMap.put("dishTypes", dishTypesStrs);
		reportDataMap.put("Headers", headers);
		reportDataMap.put("taxList", taxList);
		reportDataMap.put("restaurantName", rest.getBussinessName());
		return new ModelAndView("ExcelReportView", "reportData", reportDataMap);
	}
	
	@RequestMapping(value = "dailySalesSummaryNew.xls", method=RequestMethod.GET)
	@ApiIgnore
	public ModelAndView dailySalesSummaryExcelNew(HttpServletRequest req, ModelAndView mav) throws ParseException {
		
		try{
			logger.info("User has started generating the dailySalesSummaryNew report : userName: "+req.getSession().getAttribute("userName"));
		}catch(Exception e){
			e.printStackTrace();
		}
		Integer restaurantId = Integer.parseInt(req.getParameter("restaurantId"));
		//User user = userService.getUser(restaurantId);
		Restaurant rest=restaurantService.getRestaurant(restaurantId);
		String backDaysString = req.getParameter("backDays");
		
		String startDateStr = req.getParameter("startDate");
		String endDateStr = req.getParameter("endDate");
		Date startDate = new Date();
		Date endDate = new Date();
		DateFormat formatter;
		formatter = new SimpleDateFormat("yyyy-MM-dd");
		formatter.setTimeZone(TimeZone.getTimeZone(rest.getTimeZone()));
		
		if (StringUtility.isNullOrEmpty(startDateStr)) {
			Calendar yesterday = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
			int minusDays = 0;
			if (!StringUtility.isNullOrEmpty(backDaysString)) {
				minusDays = -1 * Integer.parseInt(backDaysString);
			}
			yesterday.add(Calendar.DAY_OF_YEAR, minusDays);
			yesterday.set(Calendar.HOUR_OF_DAY, 0);
			yesterday.set(Calendar.MINUTE, 0);
			yesterday.set(Calendar.SECOND, 0);
			yesterday.set(Calendar.MILLISECOND, 0);
			startDate = yesterday.getTime();
		} else {
			startDate = formatter.parse(startDateStr);
		}
		
		if (StringUtility.isNullOrEmpty(endDateStr)) {
			Calendar tomorrow = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
			tomorrow.add(Calendar.DAY_OF_YEAR, 1);
			tomorrow.set(Calendar.HOUR_OF_DAY, 0);
			tomorrow.set(Calendar.MINUTE, 0);
			tomorrow.set(Calendar.SECOND, 0);
			tomorrow.set(Calendar.MILLISECOND, 0);
			endDate = tomorrow.getTime();	
		} else {
			endDate = formatter.parse(endDateStr);
		}
		List<TaxType> taxType =  taxTypeService.listAllActiveInactiveTaxesByRestaurantId(restaurantId);
		List<Check> data = checkService.getDailyInvoice(restaurantId, startDate, endDate);
		
		Map<String, Object> reportDataMap = new HashMap<String, Object>();
		reportDataMap.put("data", data);
		reportDataMap.put("reportName", "Daily Sales Summary New");
		List<String> headers = new ArrayList<String>(Arrays.asList("Fulfillment Center"));
		//List<String> dishTypes = checkService.getUniqueDishTypes(restaurantId);
		List<DishType> dishTypes = dishTypeService.listDishTypesByRestaurantId(restaurantId);
		Set<String> dishTypesStrs = new HashSet<String>();
		for (DishType dishType : dishTypes) {
			dishTypesStrs.add(dishType.getName());
		}
		if (!dishTypesStrs.contains("OTHERS")) {
			dishTypesStrs.add("OTHERS");
		}
		
		List<FulfillmentCenter> kitchens =  kitchenService.getKitchenScreens(restaurantId);
		List<PaymentType> pt = (List<PaymentType>) restaurantService.listPaymentTypeByOrgId(rest.getParentRestaurantId());
		List<OrderSource> os  = restaurantService.listOrderSourcesByOrgId(rest.getParentRestaurantId());
		BasePaymentType[] bpt =  BasePaymentType.values();
		com.cookedspecially.enums.check.OrderSource[] orderSource = com.cookedspecially.enums.check.OrderSource.values();
		reportDataMap.put("orderSourceBase", orderSource);
		reportDataMap.put("basePaymentType",bpt);
		reportDataMap.put("kitchenScreens",kitchens);
		reportDataMap.put("dishTypes", dishTypesStrs);
		reportDataMap.put("taxType",taxType);
		reportDataMap.put("orderSource",os);
		reportDataMap.put("Headers", headers);
		reportDataMap.put("paymentType",pt);
		reportDataMap.put("restaurantName", rest.getBussinessName());
		reportDataMap.put("user", rest);
		
		return new ModelAndView("ExcelReportView", "reportData", reportDataMap);
	}
	
	
	@RequestMapping(value = "detailedInvoice.xls", method=RequestMethod.GET)
	@ApiIgnore
	public ModelAndView detailedInvoiceExcel(HttpServletRequest req, ModelAndView mav) throws ParseException {
		
		try{
			logger.info("User has started generating the detailedInvoice report : userName: "+req.getSession().getAttribute("userName"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		Integer restaurantId = Integer.parseInt(req.getParameter("restaurantId"));
		//User user = userService.getUser(restaurantId);
		Restaurant rest=restaurantService.getRestaurant(restaurantId);
		String backDaysString = req.getParameter("backDays");
		
		String startDateStr = req.getParameter("startDate");
		String endDateStr = req.getParameter("endDate");
		Date startDate = new Date();
		Date endDate = new Date();
		DateFormat formatter;
		formatter = new SimpleDateFormat("yyyy-MM-dd");
		formatter.setTimeZone(TimeZone.getTimeZone(rest.getTimeZone()));
		
		if (StringUtility.isNullOrEmpty(startDateStr)) {
			Calendar yesterday = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
			int minusDays = 0;
			if (!StringUtility.isNullOrEmpty(backDaysString)) {
				minusDays = -1 * Integer.parseInt(backDaysString);
			}
			yesterday.add(Calendar.DAY_OF_YEAR, minusDays);
			yesterday.set(Calendar.HOUR_OF_DAY, 0);
			yesterday.set(Calendar.MINUTE, 0);
			yesterday.set(Calendar.SECOND, 0);
			yesterday.set(Calendar.MILLISECOND, 0);
			startDate = yesterday.getTime();
		} else {
			startDate = formatter.parse(startDateStr);
		}
		
		if (StringUtility.isNullOrEmpty(endDateStr)) {
			Calendar tomorrow = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
			tomorrow.add(Calendar.DAY_OF_YEAR, 1);
			tomorrow.set(Calendar.HOUR_OF_DAY, 0);
			tomorrow.set(Calendar.MINUTE, 0);
			tomorrow.set(Calendar.SECOND, 0);
			tomorrow.set(Calendar.MILLISECOND, 0);
			endDate = tomorrow.getTime();	
		} else {
			endDate = formatter.parse(endDateStr);
		}
		
		List<Check> data = checkService.getDailyInvoice(restaurantId, startDate, endDate);
				
		Map<String, Object> reportDataMap = new HashMap<String, Object>();
		reportDataMap.put("data", data);
		reportDataMap.put("reportName", "Detailed Invoice");
		reportDataMap.put("user", rest);
		List<String> headers = new ArrayList<String>(Arrays.asList("Invoice#","Phone No ","Delivery Time","Dish Name", "Total Dishes Cost", "Dish Quantity")); 
		List<String> taxList  =  new ArrayList<String>();
		List<TaxType> taxType =  taxTypeService.listAllActiveInactiveTaxesByRestaurantId(restaurantId);
		for(TaxType taxName:  taxType){
			if(taxName.getDishType().equalsIgnoreCase("Default") && taxName!=null){
				taxList.add(taxName.getTaxTypeId().toString());
			}
		}
		reportDataMap.put("Headers", headers);
		reportDataMap.put("taxList", taxList);
		reportDataMap.put("restaurantName", rest.getBussinessName());
		
		return new ModelAndView("ExcelReportView", "reportData", reportDataMap);
	}
	
	@RequestMapping(value = "topDishes.xls", method=RequestMethod.GET)
	@ApiIgnore
	public ModelAndView topDishesExcel(HttpServletRequest req, ModelAndView mav) throws ParseException {
		
		try{
			logger.info("User has generated the topDishes report : userName: "+req.getSession().getAttribute("userName"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		Integer restaurantId = Integer.parseInt(req.getParameter("restaurantId"));
		//User user = userService.getUser(restaurantId);
		Restaurant rest=restaurantService.getRestaurant(restaurantId);
		String backDaysString = req.getParameter("backDays");
		
		String startDateStr = req.getParameter("startDate");
		String endDateStr = req.getParameter("endDate");
		Date startDate = new Date();
		Date endDate = new Date();
		DateFormat formatter;
		formatter = new SimpleDateFormat("yyyy-MM-dd");
		formatter.setTimeZone(TimeZone.getTimeZone(rest.getTimeZone()));
		
		if (StringUtility.isNullOrEmpty(startDateStr)) {
			Calendar yesterday = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
			int minusDays = 0;
			if (!StringUtility.isNullOrEmpty(backDaysString)) {
				minusDays = -1 * Integer.parseInt(backDaysString);
			}
			yesterday.add(Calendar.DAY_OF_YEAR, minusDays);
			yesterday.set(Calendar.HOUR_OF_DAY, 0);
			yesterday.set(Calendar.MINUTE, 0);
			yesterday.set(Calendar.SECOND, 0);
			yesterday.set(Calendar.MILLISECOND, 0);
			startDate = yesterday.getTime();
		} else {
			startDate = formatter.parse(startDateStr);
		}
		
		if (StringUtility.isNullOrEmpty(endDateStr)) {
			Calendar tomorrow = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
			tomorrow.add(Calendar.DAY_OF_YEAR, 1);
			tomorrow.set(Calendar.HOUR_OF_DAY, 0);
			tomorrow.set(Calendar.MINUTE, 0);
			tomorrow.set(Calendar.SECOND, 0);
			tomorrow.set(Calendar.MILLISECOND, 0);
			endDate = tomorrow.getTime();	
		} else {
			endDate = formatter.parse(endDateStr);
		}
		
		List<Check> data = checkService.getDailyInvoice(restaurantId, startDate, endDate);
	    Map<String, Object> reportDataMap = new HashMap<String, Object>();
	    
	    List<FulfillmentCenter> ffc = fulfillmentService.getKitchenScreens(restaurantId);
	    
	    Collections.sort(data, new Comparator<Check>() {
		    public int compare(Check v1,Check v2) {
		        return v2.getOpenTime().compareTo(v1.getOpenTime());
		    }
		});
		reportDataMap.put("data", data);
		reportDataMap.put("reportName", "Top Dishes");
		reportDataMap.put("user", rest);
		reportDataMap.put("kitchenScreens", ffc);
		List<String> headers = new ArrayList<String>(Arrays.asList("Fulfillment Center ","Dish Id", "Dish Name", "Total Sold", "Total Price")); 
		
		//List<String> dishTypes = checkService.getUniqueDishTypes(restaurantId);
		//List<DishType> dishTypes = dishTypeService.listDishTypesByRestaurantId(restaurantId);
		List<Dish> dishes = dishService.listDishByResaurant(restaurantId);
		
		
		reportDataMap.put("dishes", dishes);
		reportDataMap.put("Headers", headers);
		reportDataMap.put("restaurantName", rest.getBussinessName());
		
		return new ModelAndView("ExcelReportView", "reportData", reportDataMap);
	}
	
	@RequestMapping(value = "dailySalesSummary.xls", method=RequestMethod.GET)
	@ApiIgnore
	public ModelAndView dailySalesSummaryExcel(HttpServletRequest req, ModelAndView mav) throws ParseException {
		
		try{
			logger.info("User has started generating the dailySalesSummary report : userName: "+req.getSession().getAttribute("userName"));
		}catch(Exception e){
			e.printStackTrace();
		}
		Integer restaurantId = Integer.parseInt(req.getParameter("restaurantId"));
		//User user = userService.getUser(restaurantId);
		Restaurant rest=restaurantService.getRestaurant(restaurantId);
		String backDaysString = req.getParameter("backDays");
		
		String startDateStr = req.getParameter("startDate");
		String endDateStr = req.getParameter("endDate");
		Date startDate = new Date();
		Date endDate = new Date();
		DateFormat formatter;
		formatter = new SimpleDateFormat("yyyy-MM-dd");
		formatter.setTimeZone(TimeZone.getTimeZone(rest.getTimeZone()));
		
		if (StringUtility.isNullOrEmpty(startDateStr)) {
			Calendar yesterday = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
			int minusDays = 0;
			if (!StringUtility.isNullOrEmpty(backDaysString)) {
				minusDays = -1 * Integer.parseInt(backDaysString);
			}
			yesterday.add(Calendar.DAY_OF_YEAR, minusDays);
			yesterday.set(Calendar.HOUR_OF_DAY, 0);
			yesterday.set(Calendar.MINUTE, 0);
			yesterday.set(Calendar.SECOND, 0);
			yesterday.set(Calendar.MILLISECOND, 0);
			startDate = yesterday.getTime();
		} else {
			startDate = formatter.parse(startDateStr);
		}
		
		if (StringUtility.isNullOrEmpty(endDateStr)) {
			Calendar tomorrow = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
			tomorrow.add(Calendar.DAY_OF_YEAR, 1);
			tomorrow.set(Calendar.HOUR_OF_DAY, 0);
			tomorrow.set(Calendar.MINUTE, 0);
			tomorrow.set(Calendar.SECOND, 0);
			tomorrow.set(Calendar.MILLISECOND, 0);
			endDate = tomorrow.getTime();	
		} else {
			endDate = formatter.parse(endDateStr);
		}
		List<String> taxList  =  new ArrayList<String>();
		List<TaxType> taxType =  taxTypeService.listAllActiveInactiveTaxesByRestaurantId(restaurantId);
		List<Check> data = checkService.getDailyInvoice(restaurantId, startDate, endDate);
		
		Map<String, Object> reportDataMap = new HashMap<String, Object>();
		reportDataMap.put("data", data);
		reportDataMap.put("reportName", "Daily Sales Summary");
		List<String> headers = new ArrayList<String>(Arrays.asList("Gross Sales By Source"));
		headers.add("Kitchen");
		//List<String> dishTypes = checkService.getUniqueDishTypes(restaurantId);
		List<DishType> dishTypes = dishTypeService.listDishTypesByRestaurantId(restaurantId);
		Set<String> dishTypesStrs = new HashSet<String>();
		for (DishType dishType : dishTypes) {
			dishTypesStrs.add(dishType.getName());
		}
		if (!dishTypesStrs.contains("OTHERS")) {
			dishTypesStrs.add("OTHERS");
		}
		
		List<String> screenList=  new ArrayList<String>();
		List<FulfillmentCenter> kitchens =  kitchenService.getKitchenScreens(restaurantId);
		
		headers.addAll(dishTypesStrs);
		for(PaymentMode pm :PaymentMode.values()){
				if(pm==PaymentMode.PENDING || pm==PaymentMode.PG_PENDING){
					continue;
				}
		headers.add(pm.toString());
		}
		headers.add("Total");
		headers.add("No. of Checks");
		headers.add("Cost/Check");
		reportDataMap.put("kitchenScreens",kitchens);
		reportDataMap.put("dishTypes", dishTypesStrs);
		reportDataMap.put("taxType",taxType);
		reportDataMap.put("Headers", headers);
		reportDataMap.put("restaurantName", rest.getBussinessName());
		reportDataMap.put("user", rest);
		return new ModelAndView("ExcelReportView", "reportData", reportDataMap);
	}

	@RequestMapping(value = "customers.xls", method=RequestMethod.GET)
	@ApiIgnore
	public ModelAndView customerExcel(HttpServletRequest req, ModelAndView mav) throws ParseException {
		
		try{
			logger.info("User has generated the customers report : userName: "+req.getSession().getAttribute("userName"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		Integer restaurantId = Integer.parseInt(req.getParameter("restaurantId"));
		//User user = userService.getUser(restaurantId);
		Restaurant rest=restaurantService.getRestaurant(restaurantId);
		//List<Customer> data = customerService.getCustomerByParams(null, "", "", restaurantId);
		String backDaysString = req.getParameter("backDays");
		String startDateStr = req.getParameter("startDate");
		String endDateStr = req.getParameter("endDate");
		
		Date startDate = new Date();
		Date endDate = new Date();
		DateFormat formatter;
		formatter = new SimpleDateFormat("yyyy-MM-dd");
		formatter.setTimeZone(TimeZone.getTimeZone(rest.getTimeZone()));
		
		if (StringUtility.isNullOrEmpty(startDateStr)) {
			Calendar yesterday = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
			int minusDays = 0;
			if (!StringUtility.isNullOrEmpty(backDaysString)) {
				minusDays = -1 * Integer.parseInt(backDaysString);
			}
			yesterday.add(Calendar.DAY_OF_YEAR, minusDays);
			yesterday.set(Calendar.HOUR_OF_DAY, 0);
			yesterday.set(Calendar.MINUTE, 0);
			yesterday.set(Calendar.SECOND, 0);
			yesterday.set(Calendar.MILLISECOND, 0);
			startDate = yesterday.getTime();
		} else {
			startDate = formatter.parse(startDateStr);
		}
		
		if (StringUtility.isNullOrEmpty(endDateStr)) {
			Calendar tomorrow = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
			tomorrow.add(Calendar.DAY_OF_YEAR, 1);
			tomorrow.set(Calendar.HOUR_OF_DAY, 0);
			tomorrow.set(Calendar.MINUTE, 0);
			tomorrow.set(Calendar.SECOND, 0);
			tomorrow.set(Calendar.MILLISECOND, 0);
			endDate = tomorrow.getTime();	
		} else {
			endDate = formatter.parse(endDateStr);
		}
		
		List<Customer> data;
		if(rest.getParentRestaurantId()==null){
		  data = customerService.getCustomerByParams(null, "", "", restaurantId);
		//data = customerService.getCustomerByDate(rest.getParentRestaurantId(),rest.getRestaurantId(), startDate, endDate);
		}
		else{
	     data = customerService.getCustomerByDate(rest.getParentRestaurantId(),rest.getRestaurantId(), startDate, endDate);
		}
		Map<String, Object> reportDataMap = new HashMap<String, Object>();
		reportDataMap.put("data", data);
		reportDataMap.put("reportName", "Customers");
		List<String> headers = new ArrayList<String>(Arrays.asList("Customer Id","First Name", "Last Name", "Address", "City", "Phone", "Email","Created date")); 
		reportDataMap.put("Headers", headers);
		reportDataMap.put("restaurantName", rest.getBussinessName());
		return new ModelAndView("ExcelReportView", "reportData", reportDataMap);
	}
	
	@RequestMapping(value = "customersSummery.xls", method=RequestMethod.GET)
	@ApiIgnore
	public ModelAndView customerSummeryExcel(HttpServletRequest req, ModelAndView mav) throws ParseException {
		
		try{
			logger.info("User has generated the customersSummery report : userName: "+req.getSession().getAttribute("userName"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		Integer restaurantId = Integer.parseInt(req.getParameter("restaurantId"));
		//User user = userService.getUser(restaurantId);
		Restaurant rest=restaurantService.getRestaurant(restaurantId);
		
		//List<Customer> data = customerService.getCustomerByParams(null, "", "", restaurantId);
		String backDaysString = req.getParameter("backDays");
		String startDateStr = req.getParameter("startDate");
		String endDateStr = req.getParameter("endDate");
		
		Date startDate = new Date();
		Date endDate = new Date();
		DateFormat formatter;
		formatter = new SimpleDateFormat("yyyy-MM-dd");
		formatter.setTimeZone(TimeZone.getTimeZone(rest.getTimeZone()));
		
		if (StringUtility.isNullOrEmpty(startDateStr)) {
			Calendar yesterday = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
			int minusDays = 0;
			if (!StringUtility.isNullOrEmpty(backDaysString)) {
				minusDays = -1 * Integer.parseInt(backDaysString);
			}
			yesterday.add(Calendar.DAY_OF_YEAR, minusDays);
			yesterday.set(Calendar.HOUR_OF_DAY, 0);
			yesterday.set(Calendar.MINUTE, 0);
			yesterday.set(Calendar.SECOND, 0);
			yesterday.set(Calendar.MILLISECOND, 0);
			startDate = yesterday.getTime();
		} else {
			startDate = formatter.parse(startDateStr);
		}
		
		if (StringUtility.isNullOrEmpty(endDateStr)) {
			Calendar tomorrow = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
			tomorrow.add(Calendar.DAY_OF_YEAR, 1);
			tomorrow.set(Calendar.HOUR_OF_DAY, 0);
			tomorrow.set(Calendar.MINUTE, 0);
			tomorrow.set(Calendar.SECOND, 0);
			tomorrow.set(Calendar.MILLISECOND, 0);
			endDate = tomorrow.getTime();	
		} else {
			endDate = formatter.parse(endDateStr);
		}
		List<Customer> data=null;
		if(rest.getParentRestaurantId()==null){
		}
		data = customerService.getCustomerByDate(rest.getParentRestaurantId(),rest.getRestaurantId(), startDate, endDate);
		HashMap<Integer,List<Check>> customerSummery =  new HashMap<Integer,List<Check>>();
		for(Customer cust : data){
			if(cust.getCustomerId()!=null && cust.getCustomerId()>0){
				List<Check> list  =  checkService.getCustomersCheckList(cust.getPhone(),cust.getCustomerId(),null);
			    customerSummery.put(cust.getCustomerId(),list);
			}
		}
		Map<String, Object> reportDataMap = new HashMap<String, Object>();
		reportDataMap.put("data", data);
		reportDataMap.put("custSummery", customerSummery);
		reportDataMap.put("reportName", "CustomersSummery");
		List<String> headers = new ArrayList<String>(Arrays.asList("Customer Id","Phone", "Order List")); 
		reportDataMap.put("Headers", headers);
		reportDataMap.put("restaurantName", rest.getBussinessName());
		return new ModelAndView("ExcelReportView", "reportData", reportDataMap);
	}
	
	@RequestMapping(value = "salesSummary.xls", method=RequestMethod.GET)
	@ApiIgnore
	public ModelAndView salesSummaryExcel(HttpServletRequest req, ModelAndView mav) throws ParseException {
		
		try{
			logger.info("User has generated the salesSummary report : userName: "+req.getSession().getAttribute("userName"));
		}catch(Exception e){
			e.printStackTrace();
		}
		Integer restaurantId = Integer.parseInt(req.getParameter("restaurantId"));
			//User user = userService.getUser(restaurantId);
			Restaurant rest=restaurantService.getRestaurant(restaurantId);
			String backDaysString = req.getParameter("backDays");
			
			String startDateStr = req.getParameter("startDate");
			String endDateStr = req.getParameter("endDate");
			Date startDate = new Date();
			Date endDate = new Date();
			DateFormat formatter;
			formatter = new SimpleDateFormat("yyyy-MM-dd");
			formatter.setTimeZone(TimeZone.getTimeZone(rest.getTimeZone()));
			
			if (StringUtility.isNullOrEmpty(startDateStr)) {
				Calendar yesterday = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
				int minusDays = 0;
				if (!StringUtility.isNullOrEmpty(backDaysString)) {
					minusDays = -1 * Integer.parseInt(backDaysString);
				}
				yesterday.add(Calendar.DAY_OF_YEAR, minusDays);
				yesterday.set(Calendar.HOUR_OF_DAY, 0);
				yesterday.set(Calendar.MINUTE, 0);
				yesterday.set(Calendar.SECOND, 0);
				yesterday.set(Calendar.MILLISECOND, 0);
				startDate = yesterday.getTime();
			} else {
				startDate = formatter.parse(startDateStr);
			}
			
			if (StringUtility.isNullOrEmpty(endDateStr)) {
				Calendar tomorrow = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
				tomorrow.add(Calendar.DAY_OF_YEAR, 1);
				tomorrow.set(Calendar.HOUR_OF_DAY, 0);
				tomorrow.set(Calendar.MINUTE, 0);
				tomorrow.set(Calendar.SECOND, 0);
				tomorrow.set(Calendar.MILLISECOND, 0);
				endDate = tomorrow.getTime();	
			}else {
				endDate = formatter.parse(endDateStr);
			}
			List<TaxType> taxType =  taxTypeService.listAllActiveInactiveTaxesByRestaurantId(restaurantId);
			SortedMap<Date,List<Check>> data = new TreeMap<Date, List<Check>>();
			while(startDate.before(endDate)){
				List<Check> checkList = new ArrayList<Check>();
				Calendar dayAfter = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
				dayAfter.setTime(startDate);
				dayAfter.add(Calendar.DAY_OF_YEAR, 1);
				dayAfter.set(Calendar.HOUR_OF_DAY, 0);
				dayAfter.set(Calendar.MINUTE, 0);
				dayAfter.set(Calendar.SECOND, 0);
				dayAfter.set(Calendar.MILLISECOND,0);
				checkList = checkService.getDailyInvoice(restaurantId, startDate, dayAfter.getTime());
				data.put(startDate,checkList);
				Calendar tomorrow = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
				tomorrow.setTime(startDate);
				tomorrow.add(Calendar.DAY_OF_YEAR, 1);
				tomorrow.set(Calendar.HOUR_OF_DAY, 0);
				tomorrow.set(Calendar.MINUTE, 0);
				tomorrow.set(Calendar.SECOND, 0);
				tomorrow.set(Calendar.MILLISECOND,0);
				startDate = tomorrow.getTime();
			}
			Map<String, Object> reportDataMap = new HashMap<String, Object>();
			reportDataMap.put("dataList", data);
			reportDataMap.put("reportName", "Sales Summary");
			List<String> headers = new ArrayList<String>(Arrays.asList("Fulfillment Center"));
			List<DishType> dishTypes = dishTypeService.listDishTypesByRestaurantId(restaurantId);
			Set<String> dishTypesStrs = new HashSet<String>();
			for (DishType dishType : dishTypes) {
				dishTypesStrs.add(dishType.getName());
			}
			if (!dishTypesStrs.contains("OTHERS")) {
				dishTypesStrs.add("OTHERS");
			}
			
			List<FulfillmentCenter> kitchens =  kitchenService.getKitchenScreens(restaurantId);
			List<PaymentType> pt = (List<PaymentType>) restaurantService.listPaymentTypeByOrgId(rest.getParentRestaurantId());
			List<OrderSource> os  = restaurantService.listOrderSourcesByOrgId(rest.getParentRestaurantId());
			BasePaymentType[] bpt =  BasePaymentType.values();
			com.cookedspecially.enums.check.OrderSource[] orderSource = com.cookedspecially.enums.check.OrderSource.values();
			reportDataMap.put("orderSourceBase", orderSource);
			reportDataMap.put("restaurant",rest);
			reportDataMap.put("basePaymentType",bpt);
			reportDataMap.put("kitchenScreens",kitchens);
			reportDataMap.put("dishTypes", dishTypesStrs);
			reportDataMap.put("taxType",taxType);
			reportDataMap.put("orderSource",os);
			reportDataMap.put("Headers", headers);
			reportDataMap.put("paymentType",pt);
			reportDataMap.put("restaurantName", rest.getBussinessName());
			reportDataMap.put("user", rest);
			
			return new ModelAndView("ExcelReportView", "reportData", reportDataMap);
		}
	
	@RequestMapping(value = "salesRegisterReport.xls", method=RequestMethod.GET)
	@ApiIgnore
	public ModelAndView salesRegisterReport(HttpServletRequest req, ModelAndView mav) throws ParseException {
		
		try{
			logger.info("User has generated the salesRegisterReport report : userName: "+req.getSession().getAttribute("userName"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		Integer restaurantId = Integer.parseInt(req.getParameter("restaurantId"));
			//User user = userService.getUser(restaurantId);
			Restaurant rest=restaurantService.getRestaurant(restaurantId);
			String backDaysString = req.getParameter("backDays");
			
			String startDateStr = req.getParameter("startDate");
			String endDateStr = req.getParameter("endDate");
			Date startDate = new Date();
			Date endDate = new Date();
			DateFormat formatter;
			formatter = new SimpleDateFormat("yyyy-MM-dd");
			formatter.setTimeZone(TimeZone.getTimeZone(rest.getTimeZone()));
			List<User> listUser =  userService.listUserByOrg(rest.getParentRestaurantId());
			List<FulfillmentCenter> kitchens =  kitchenService.getKitchenScreens(restaurantId);
			Map<Integer, Map<String,Object> > tillList =  new TreeMap<Integer, Map<String,Object>>();
			SortedMap<Date,SortedMap<Integer,List<List<Transaction>>>> data = new TreeMap<Date,SortedMap<Integer,List<List<Transaction>>>>();
			for(FulfillmentCenter fc: kitchens){
				Map<String,Object> sc = cashRegisterService.getTillListByFFCId(fc);
				tillList.put(fc.getId(),sc);
			}
			if (StringUtility.isNullOrEmpty(startDateStr)) {
				Calendar yesterday = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
				int minusDays = 0;
				if (!StringUtility.isNullOrEmpty(backDaysString)) {
					minusDays = -1 * Integer.parseInt(backDaysString);
				}
				yesterday.add(Calendar.DAY_OF_YEAR, minusDays);
				yesterday.set(Calendar.HOUR_OF_DAY, 0);
				yesterday.set(Calendar.MINUTE, 0);
				yesterday.set(Calendar.SECOND, 0);
				yesterday.set(Calendar.MILLISECOND, 0);
				startDate = yesterday.getTime();
			} else {
				startDate = formatter.parse(startDateStr);
			}
			
			if (StringUtility.isNullOrEmpty(endDateStr)) {
				Calendar tomorrow = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
				tomorrow.add(Calendar.DAY_OF_YEAR, 1);
				tomorrow.set(Calendar.HOUR_OF_DAY, 0);
				tomorrow.set(Calendar.MINUTE, 0);
				tomorrow.set(Calendar.SECOND, 0);
				tomorrow.set(Calendar.MILLISECOND, 0);
				endDate = tomorrow.getTime();	
			}else {
				endDate = formatter.parse(endDateStr);
			}
			while(startDate.before(endDate)){
				Calendar dayAfter = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
				SortedMap<Integer,List<List<Transaction>>> reportData =  new TreeMap<Integer,List<List<Transaction>>>();
				dayAfter.setTime(startDate);
				dayAfter.add(Calendar.DAY_OF_YEAR, 1);
				dayAfter.set(Calendar.HOUR_OF_DAY, 0);
				dayAfter.set(Calendar.MINUTE, 0);
				dayAfter.set(Calendar.SECOND, 0);
				dayAfter.set(Calendar.MILLISECOND,0);
				for(Integer fc : tillList.keySet()){
					Map<String,Object> tillListData = tillList.get(fc);
					List<Map<String, Object>> tillData= (List<Map<String, Object>>) tillListData.get("tillList");
					List<List<Transaction>> transactionList =  new ArrayList<List<Transaction>>();
					for(Map<String, Object> tillD :tillData){
						transactionList.add(cashRegisterService.getTransactionListByDateRange((String)tillD.get("tillId"), startDate, dayAfter.getTime()));
					}
					reportData.put(fc,transactionList);
				}
				data.put(startDate, reportData);
				Calendar tomorrow = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
				tomorrow.setTime(startDate);
				tomorrow.add(Calendar.DAY_OF_YEAR, 1);
				tomorrow.set(Calendar.HOUR_OF_DAY, 0);
				tomorrow.set(Calendar.MINUTE, 0);
				tomorrow.set(Calendar.SECOND, 0);
				tomorrow.set(Calendar.MILLISECOND,0);
				startDate = tomorrow.getTime();
			}
			Map<String, Object> reportDataMap = new HashMap<String, Object>();
			reportDataMap.put("reportName", "Transaction details");
			List<String> headers = new ArrayList<String>(Arrays.asList("Fulfillment Center"));
			List<String> mapStatus =  new ArrayList<String>();
			mapStatus.add("ADD_CASH");
			mapStatus.add("WITHDRAW_CASH");
			reportDataMap.put("restaurant",rest);
			reportDataMap.put("transactionType", mapStatus);
			reportDataMap.put("kitchenScreens",kitchens);
			reportDataMap.put("transactionList",data);
			reportDataMap.put("Headers", headers);
			reportDataMap.put("listUser",listUser);
			reportDataMap.put("restaurantName", rest.getBussinessName());
			reportDataMap.put("user", rest);
			
			return new ModelAndView("ExcelReportView", "reportData", reportDataMap);
		}
}