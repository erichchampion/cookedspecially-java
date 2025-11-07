
package com.cookedspecially.controller;

import com.cookedspecially.dao.CashRegisterDAO;
import com.cookedspecially.dao.CouponRuleDAO;
import com.cookedspecially.domain.*;
import com.cookedspecially.dto.*;
import com.cookedspecially.dto.credit.AddCreditToCustomerAccountDTO;
import com.cookedspecially.dto.credit.CustomerCreditDTO;
import com.cookedspecially.dto.saleRegister.SaleTransaction;
import com.cookedspecially.dto.saleRegister.TillCashUpdateDTO;
import com.cookedspecially.dto.saleRegister.TillDTO;
import com.cookedspecially.dto.saleRegister.TransactionDTO;
import com.cookedspecially.enums.check.*;
import com.cookedspecially.enums.check.OrderSource;
import com.cookedspecially.enums.credit.BilligCycle;
import com.cookedspecially.enums.credit.CreditTransactionStatus;
import com.cookedspecially.enums.credit.CustomerCreditAccountStatus;
import com.cookedspecially.enums.order.DestinationType;
import com.cookedspecially.enums.order.SourceType;
import com.cookedspecially.enums.order.Status;
import com.cookedspecially.enums.till.TillTransaction;
import com.cookedspecially.enums.till.TillTransactionStatus;
import com.cookedspecially.enums.till.TransactionCategory;
import com.cookedspecially.service.*;
import com.cookedspecially.utility.MailerUtility;
import com.cookedspecially.utility.StringUtility;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.google.gson.Gson;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import springfox.documentation.annotations.ApiIgnore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

//import org.thymeleaf.spring3.SpringTemplateEngine;

/**
 * @author shashank ,rahul, abhishek,sushil
 * report
 */
@Controller
@RequestMapping("/order")
@Api(description="Place Order REST API's")
public class OrderController {

	final static Logger logger = Logger.getLogger(OrderController.class);
	@Autowired
	private UserService userService;

	private String rewardBase="Calories";
	
	@Autowired
	private RestaurantService restService;
	
	@Autowired
	private TaxTypeService taxTypeService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private SeatingTableService seatingTableService;
	
	@Autowired
	private OrderDishService orderDishService;

	@Autowired
    private CashRegisterController cashRegisterController; 
	
	@Autowired
	private CashRegisterService cashRegisterService;
	
	@Autowired
	private CashRegisterDAO cashRegisterDAO;
	
	@Autowired
	private MenuService menuService;
	
	@Autowired
	private CheckService checkService;
	
	@Autowired
	private CustomerService customerService;

	@Autowired
    private	DeliveryAreaService deliveryAreaService;
	
	@Autowired
	private JavaMailSenderImpl mailSender;
	
	@Autowired
	private SpringTemplateEngine templateEngine;
	
	@Autowired
	private FulfillmentCenterService kitchenScreenService;
	
	@Autowired
	private ZomatoService zomatoService;
	
	/*@Autowired
	private RestaurantController restaurantController;*/
	
	@Autowired
	@Qualifier("customerCreditAutomatedBilling")
	private CustomerCreditService customerCreditService;
	
	@Autowired
	private StockManagementService stockManagementService; 
	
	@Autowired
	private DishService  dishService;

	@Autowired
	private CustomerController customerController;

	@Autowired
	private RestaurantService restaurantServices;
	
	@Autowired
	private AddOnDishService addOnDishService;
	
	@Autowired
	private DishTypeService dishTypeService;
	
	@Autowired
	private  CouponService couponService; 

	public HttpServletRequest REQUEST;
	
    public static long compareTo(java.util.Date date1, java.util.Date date2) {
        return date1.getTime() - date2.getTime();
    }

	@RequestMapping("/")
	@ApiIgnore
	public String listOrders(Map<String, Object> map, HttpServletRequest request) {
		Map<String, Object> queryMap = new HashMap<String, Object>();
		Integer restaurantId = (Integer) request.getSession().getAttribute("restaurantId");
		queryMap.put("restaurantId", restaurantId);
		map.put("orders", orderService.listOrders(queryMap));
		return "orders";
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiIgnore
	public String o(Map<String, Object> map, @ModelAttribute("order") Order order ) {
		order.setRestaurantId(order.getUserId());
		order.setStatus(Status.NEW);
		orderService.addOrder(order);
		logger.info("Order Added (orderId :" + order.getOrderId() + ")");
		return "redirect:/orders/";
	}

    @RequestMapping(value="/editCheck/{checkId}", method = RequestMethod.GET)
	public String editCheck(Map<String, Object> map, @PathVariable("checkId") Integer checkId, HttpServletRequest request) {
    	REQUEST = request;
    	Check check = checkService.getCheck(checkId);
		Restaurant rest =  restService.getRestaurant(check.getRestaurantId());
		logger.info("Edit Check (check Id:/ incoiceId" + checkId + ") / "+check.getInvoiceId());
		if (check != null) {
			Float waiveOff =null;
			if(check.getOutCircleDeliveryCharges()==0){
				waiveOff = identifyWaiveOffCharges(check);
			}
			double discountAmountPercent=0;
			CheckResponse checkResponse = new CheckResponse(check,taxTypeService,waiveOff,rest);
			Map<String, JsonDish> itemsMap = new TreeMap<String, JsonDish>();
			List<CheckDishResponse> items = checkResponse.getItems();
			float checkBill = 0;
			for (CheckDishResponse item : items) {
				if (itemsMap.containsKey(item.getName())) {
					JsonDish jsonDish = itemsMap.get(item.getName());
					jsonDish.setPrice(jsonDish.getPrice() + item.getPrice());
					jsonDish.setQuantity(jsonDish.getQuantity() + 1);
				} else {
					JsonDish jsonDish = new JsonDish();
					jsonDish.setQuantity(1);
					jsonDish.setName(item.getName());
					// TODO: Erich: Potential problem; doesn't deal with dish size
					jsonDish.setPrice(item.getPrice());
					itemsMap.put(item.getName(), jsonDish);
				}
				checkBill += item.getPrice();
			}
			checkResponse.setAmount(checkBill);
			if (check.getDiscountPercent() > 0) {
				checkResponse.setDiscountAmount(checkBill * check.getDiscountPercent() / 100);
			}
			 checkResponse.setAmountAfterDiscount(checkBill - checkResponse.getDiscountAmount());
			 SortedMap<String,Float> taxD = new TreeMap<String,Float>();

			List<TaxType> tTS =taxTypeService.listTaxTypesByRestaurantId(check.getRestaurantId());
			float taxSum=0.0f;
			if(tTS!=null){
			for(TaxType tax:tTS){
				if(tax.getDishType().equalsIgnoreCase("Default")){
				taxD.put(tax.getName(),(float)tax.getTaxCharge(checkBill -checkResponse.getDiscountAmount(),tax.getChargeType(),tax.getTaxValue()));
				taxSum+=tax.getTaxCharge(checkBill-checkResponse.getDiscountAmount(),tax.getChargeType(),tax.getTaxValue());
				}
				}
			}
			checkResponse.setTotal(taxSum + checkResponse.getAmountAfterDiscount() + check.getOutCircleDeliveryCharges());
			checkResponse.setTaxDetails(taxD);
			checkResponse.setOutCircleDeliveryCharges(check.getOutCircleDeliveryCharges());
			checkResponse.setRoundedOffTotal(Math.round(taxSum + checkResponse.getAmountAfterDiscount() + check.getOutCircleDeliveryCharges()));
			SortedMap<String,Float> val =  checkResponse.getTaxDetails();
            Set<String> keys = val.keySet();
            JSONObject obj = new JSONObject();
			for(String iterate : keys){
				if(iterate!=null){
				try {
					obj.put(iterate, val.get(iterate));

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					try {
						restaurantServices.emailException(ExceptionUtils.getStackTrace(e),REQUEST);
					} catch (UnsupportedEncodingException | MessagingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					logger.info("Exception mail sent");
				}
				}
			}
		    check.setTaxJsonObject(obj.toString());
			checkService.addCheck(check);
			map.put("checkResponse", checkResponse);
			map.put("itemsMap", itemsMap);
		}
		map.put("statusTypes", com.cookedspecially.enums.check.Status.values());
		return "editCheck";
    }

    @RequestMapping(value="/updateCheck",method = RequestMethod.GET)
    @ApiIgnore
    @Deprecated
	public String updateCheck(HttpServletRequest request, HttpServletResponse response, @RequestParam String checkId) {
		Integer checkID  = Integer.parseInt(checkId);
		Check check = checkService.getCheck(checkID);
		logger.info("Update Check ID :" + checkId +" and "+check.getInvoiceId());
        if (check != null) {
            String discountPercentStr = request.getParameter("discountPercent");
			if (!StringUtility.isNullOrEmpty(discountPercentStr)) {
				check.setDiscountPercent(Float.parseFloat(discountPercentStr));
			}
			String discountAmountString = request.getParameter("discountAmount");
			if (!StringUtility.isNullOrEmpty(discountAmountString)) {
				check.setDiscountAmount(Float.parseFloat(discountAmountString));
			}
			String statusStr = request.getParameter("status");
			com.cookedspecially.enums.check.Status status = com.cookedspecially.enums.check.Status.valueOf(com.cookedspecially.enums.check.Status.class, statusStr);
			check.setStatus(status);
			if (status == com.cookedspecially.enums.check.Status.Cancel) {
				// cancel all orders within
				List<Order> orders = check.getOrders();
				for (Order order: orders) {
					order.setStatus(Status.CANCELLED);
				}
			}
			float checkBill = 0;
			List<Order> orders = check.getOrders();
			if (orders != null) {
				for(Order order : orders)
				{
					if (order.getStatus() == com.cookedspecially.enums.order.Status.CANCELLED) {
						continue;
					}
					List<OrderDish> orderDishes = order.getOrderDishes();
					if (orderDishes != null) {
						for (OrderDish orderDish : orderDishes) {
							checkBill += (orderDish.getQuantity() * orderDish.getPrice());
						}
					}
	 			}
			}
			check.setBill(checkBill);
			if (check.getDiscountPercent() > 0) {
				check.setDiscountAmount(checkBill * check.getDiscountPercent() / 100);
			}
			checkService.addCheck(check);
			logger.info("Check Added : "+check );
		}

		return "redirect:/order/editCheck/"+checkId;
	}

	@RequestMapping(value="/displayCheck",method = RequestMethod.GET)
	public String getCheckDisplay(HttpServletRequest request, HttpServletResponse response,Map<String, Object> map, @RequestParam String invoiceId){

		//String invoiceId = request.getParameter("invoiceId");
		String templateName="saladdaysbill";

		List<Check> checks = checkService.getCheckByInvoiceId(invoiceId);
		//	Check check= checkService.getCheck();
		for(Check check :checks){
			if (check != null) {
				//User user = userService.getUser(check.getRestaurantId());
				Restaurant rest=restService.getRestaurant(check.getRestaurantId());
				map.put("restaurant",rest);
				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
				cal.setTime(check.getOpenTime());
				//logger.info("Check Open Time :"+check.getOpenTime());
				DateFormat formatter1;
				formatter1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				formatter1.setTimeZone(cal.getTimeZone());
				map.put("checkDate", formatter1.format(cal.getTime()));
				Float waveOff =null;
				if(check.getOutCircleDeliveryCharges()==0){
					waveOff = identifyWaiveOffCharges(check);
				}
				PrintCheckFromDB checkResponse = new PrintCheckFromDB(check,taxTypeService,waveOff,rest);
				if(rest.isRoundOffAmount()){
					//checkResponse.setTotal(Math.round(checkResponse.getTotal()));
					checkResponse.setRoundedOffTotal(Math.round(checkResponse.getRoundedOffTotal()));
				}
				map.put("checkRespone", checkResponse);
				if(rest.getParentRestaurantId()!=32){
					templateName = "defaultbill";
				}
				if (check.getCustomerId() > 0) {
					Customer customer = customerService.getCustomer(check.getCustomerId());
					if(check.getCheckType()==CheckType.TakeAway){
						customer.setAddress("");
						customer.setCity("");
						customer.setDeliveryArea("");
					}
					map.put("customer", customer);
				} else if (check.getTableId() > 0) {
					map.put("tableId", check.getTableId());
				}

				Map<String, JsonDish> itemsMap = new TreeMap<String, JsonDish>();
				List<CheckDishResponse> items = checkResponse.getItems();
				List<JsonAddOn> jsonAdd =  new ArrayList<JsonAddOn>();
				for (CheckDishResponse item : items) {

					if (itemsMap.containsKey(item.getDishId()+""+item.getDishSize().replaceAll("\\s",""))) {
						JsonDish jsonDish = itemsMap.get(item.getDishId()+""+item.getDishSize().replaceAll("\\s",""));
						jsonDish.setPrice(jsonDish.getPrice() + item.getPrice());
						jsonDish.setQuantity(jsonDish.getQuantity() + 1);
					}
					else {
						JsonDish jsonDish = new JsonDish();
						jsonDish.setQuantity(1);
						jsonDish.setName(item.getName());
						jsonDish.setId(item.getDishId());
						jsonDish.setPrice(item.getPrice());
						jsonDish.setDishSize(item.getDishSize());
						List<OrderAddOn> orderAddOn = item.getAddOnresponse();
						if(orderAddOn!=null){
						for( OrderAddOn oad : orderAddOn){
							JsonAddOn jsonAddOn = new JsonAddOn();
							jsonAddOn.setId(oad.getAddOnId());
							jsonAddOn.setDishId(item.getDishId());
							jsonAddOn.setName(oad.getName());
							jsonAddOn.setPrice(oad.getPrice());
							jsonAddOn.setQuantity(oad.getQuantity());
							jsonAddOn.setDishSize(oad.getDishSize());
							jsonAdd.add(jsonAddOn);
							jsonDish.setAddOns(jsonAdd);
						}
					}
						itemsMap.put(item.getDishId()+""+item.getDishSize().replaceAll("\\s",""), jsonDish);
					}
				}
				map.put("itemsMap", itemsMap);
			}
		}
		return "custom/"+templateName;
	}

    @RequestMapping(value="/closeCheck", method = RequestMethod.GET)
	public @ResponseBody String closeCheck(HttpServletRequest request, HttpServletResponse response , @RequestParam Integer tableId, @RequestParam Integer restaurantId ) {
		//int tableID = Integer.parseInt(tableId);
		SeatingTable seatingTable = seatingTableService.getSeatingTable(tableId);
		Check check = checkService.getCheckByTableId(restaurantId, tableId);
		logger.info("Close Check ID/ invoicId :" +check.getCheckId()+"/"+check.getInvoiceId());
		if (seatingTable != null) {
			seatingTable.setStatus(com.cookedspecially.enums.table.Status.Available);

			if (check != null) {
				check.setStatus(com.cookedspecially.enums.check.Status.Paid);
				check.setCloseTime(new Date());
				check.setModifiedTime(new Date());
				checkService.addCheck(check);
			}
			seatingTableService.addSeatingTable(seatingTable);
		}
		String result = "";
		if (seatingTable != null && check != null) {
			result = "Checked out table: " + tableId + " check Id : " + check.getCheckId();
		} else {
			result = "Nothing to checkout";
		}
		return result;
	}
	
	@RequestMapping(value="/checkout", method = RequestMethod.GET)
	public @ResponseBody String checkout(HttpServletRequest request, HttpServletResponse response, @RequestParam Integer tableId, @RequestParam Integer restaurantId ) {
		/*int tableId = Integer.parseInt(request.getParameter("tableId"));
		int restaurantId = Integer.parseInt(request.getParameter("restaurantId"));*/
		Check check = checkService.getCheckByTableId(restaurantId, tableId);

		if (check != null) {
			check.setStatus(com.cookedspecially.enums.check.Status.Readytopay);
			check.setModifiedTime(new Date());
			checkService.addCheck(check);
		}

		String result = "";
		if (check != null) {
			result = "Checked out table: " + tableId + " check Id : " + check.getCheckId();
		} else {
			result = "Nothing to checkout";
		}
		return result;
	}
	
	@RequestMapping(value="/checkoutTable", method = RequestMethod.GET)
	public @ResponseBody String checkoutTable(HttpServletRequest request, HttpServletResponse response, @RequestParam Integer tableId, @RequestParam Integer restaurantId ){
		/*int tableId = Integer.parseInt(request.getParameter("tableId"));
		int restaurantId = Integer.parseInt(request.getParameter("restaurantId"));*/
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("restaurantId", restaurantId);
		queryMap.put("sourceType", SourceType.TABLE);
		queryMap.put("sourceId", tableId);
		queryMap.put("status", Status.NEW);
		List<Order> orders = orderService.listOrders(queryMap);
		String paidOrders = "";
		for(Order order : orders) {
			order.setStatus(Status.PAID);
			orderService.addOrder(order);
			if (!StringUtility.isNullOrEmpty(paidOrders)) {
				paidOrders += ',';
			}
			paidOrders += order.getOrderId();
		}
		return "Checked out table: " + tableId +" Orders:" + paidOrders;
	}
	
	@RequestMapping(value="/getactiveorders",method = RequestMethod.GET)
	@ApiOperation(value="This API requires user login session, we're getting some data from session")
	public @ResponseBody List<Order> getActiveOrdersJsonByTable(HttpServletRequest request, HttpServletResponse response, @RequestParam Integer tableId){
		//int tableId = Integer.parseInt(request.getParameter("tableId"));
		Map<String, Object> queryMap = new HashMap<String, Object>();
		Integer restaurantId = (Integer) request.getSession().getAttribute("restaurantId");
		queryMap.put("userId", restaurantId);
		queryMap.put("sourceType", SourceType.TABLE);
		queryMap.put("sourceId", tableId);
		queryMap.put("status", Status.NEW);
        return orderService.listOrders(queryMap);
    }
	
	@RequestMapping(value = "/submitTableOrder", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ApiOperation(value="This API requires user login session, we're getting some data from session")
	public @ResponseBody OrderResponse submitOrder(@RequestBody JsonOrder order, Model model, HttpServletRequest request) {
		Order targetOrder = new Order();
		Integer restaurantId = (Integer) request.getSession().getAttribute("resataurantId");
		Integer userId=(Integer) request.getSession().getAttribute("userId");
		targetOrder.setUserId(userId);
		targetOrder.setRestaurantId(restaurantId);
		targetOrder.setCreatedTime(new Date());
		targetOrder.setSourceType(SourceType.TABLE);
		targetOrder.setSourceId(order.getTableId());
		targetOrder.setDestinationType(DestinationType.TABLE);
		targetOrder.setDestinationId(order.getTableId());
		targetOrder.setStatus(Status.NEW);
		List<JsonDish> jsonDishes = order.getItems();
		Float bill = 0.0f;
		HashMap<Integer, OrderDish> orderDishMap = new HashMap<Integer, OrderDish>();
		for (JsonDish jsonDish  : jsonDishes) {
			Dish  val  = dishService.getDish(jsonDish.getId());
			if (orderDishMap.get(jsonDish.getId()) != null) {
				orderDishMap.get(jsonDish.getId()).addMore(1);
            } else {
                OrderDish orderDish = new OrderDish();
				orderDish.setDishId(jsonDish.getId());
				orderDish.setQuantity(1);
				orderDish.setPrice(jsonDish.getPrice());
				logger.info("setting dish Type to ordered dish :"+val.getDishType());
				orderDish.setDishType(StringUtility.isNullOrEmpty(jsonDish.getDishType())?val.getDishType():jsonDish.getDishType());
				orderDishMap.put(orderDish.getDishId(), orderDish);
			}
			bill += jsonDish.getPrice();
		}
		targetOrder.setBill(bill);
		if (orderDishMap.size() > 0) {
			targetOrder.setOrderDishes(new ArrayList<OrderDish>(orderDishMap.values()));
		}
		orderService.addOrder(targetOrder);
		OrderResponse orderResp = new OrderResponse();
		orderResp.setOrderId(targetOrder.getOrderId());
		return orderResp;
	}

	@RequestMapping(value = "/tableOrder",method = RequestMethod.GET)
	@ApiOperation(value="This API requires user login session, we're getting some data from session")
	public String tableOrder(Map<String, Object> map, HttpServletRequest request,@RequestParam String menuId) {
		Integer menuID = Integer.getInteger(menuId);
		map.put("menu", menuService.getMenu(menuID ));
		Integer userId = (Integer) request.getSession().getAttribute("userId");
		List<SeatingTable> tables = seatingTableService.listSeatingTableByUser(userId);
		map.put("tables", tables);

		return "orders";
	}
	
	@RequestMapping(value="/cancel/{orderId}" , method=RequestMethod.POST)
	@ApiParam(value="This API is specific to Dine-in functionality")
	public String cancelOrder(Map<String, Object> map, HttpServletRequest request, @PathVariable("orderId") Integer orderId) throws UnsupportedEncodingException, MessagingException {
		
		try {
			Order order = orderService.getOrder(orderId);
			logger.info("Cancled Order, ID :"+orderId);
			order.setStatus(Status.CANCELLED);
			orderService.addOrder(order);
		} catch (Exception e) {
			e.printStackTrace();
			restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
			logger.info("Exception mail sent");
		}
		return listOrders(map, request);
	}

	@RequestMapping(value="/delete/{orderId}", method=RequestMethod.POST)
	@ApiParam(value="This API is specific to Dine-in functionality")
	public String deleteOrder(Map<String, Object> map, HttpServletRequest request, @PathVariable("orderId") Integer orderId) throws UnsupportedEncodingException, MessagingException {

		try {
			orderService.removeOrder(orderId);
			logger.info("Deleted Order, ID" + orderId);
		} catch (Exception e) {
			e.printStackTrace();
			restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
			logger.info("Exception mail sent");
		}
		return listOrders(map, request);
	}

	@RequestMapping(value="/deleteCheck/{checkId}", method = RequestMethod.GET)
	@ApiParam(value="To delete CHECK from the database.")
	public @ResponseBody String deleteCheck(Map<String, Object> map, HttpServletRequest request, @PathVariable("checkId") Integer checkId) {
		try {
			checkService.removeCheck(checkId);
			logger.info("Deleted Check, ID : "+checkId);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
			} catch (UnsupportedEncodingException | MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			logger.info("Exception mail sent");
		}
		return "Success: Deleted the Check with id:" +checkId;
	}

    @RequestMapping(value = "/searchChecks", method = RequestMethod.GET)
    @ApiParam(value="To search check using checkId or InvoiceId.")
	public String searchChecks(Map<String, Object> map, HttpServletRequest request,@RequestParam(required=false) String checkId,@RequestParam(required=false) String invoiceId) {
		//String checkIdString = request.getParameter("checkId");
		//String invoiceId = request.getParameter("invoiceId");
		if (!StringUtility.isNullOrEmpty(checkId)) {
			Integer checkID  = Integer.parseInt(checkId);
			try {
				Check check = checkService.getCheck(checkID);
				Restaurant rest =  restService.getRestaurant(check.getRestaurantId());
				Float waiveOff =null;
				if(check.getOutCircleDeliveryCharges()==0){
					waiveOff = identifyWaiveOffCharges(check);
				}
				map.put("checkList", Arrays.asList(new CheckResponse(check,taxTypeService,waiveOff,rest)));
			} catch (Exception e) {
				e.printStackTrace();
				try {
					restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
				} catch (UnsupportedEncodingException | MessagingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				logger.info("Exception mail sent");
            }
        } else if (!StringUtility.isNullOrEmpty(invoiceId)) {
			try {
				List<Check> checks = checkService.getCheckByInvoiceId(invoiceId);
				List<CheckResponse> checkResponses = new ArrayList<CheckResponse>();
				for (Check check : checks) {
					Restaurant rest =  restService.getRestaurant(check.getRestaurantId());
					Float waiveOff =null;
					if(check.getOutCircleDeliveryCharges()==0){
						waiveOff = identifyWaiveOffCharges(check);
					}
					checkResponses.add(new CheckResponse(check,taxTypeService,waiveOff,rest));
				}
				map.put("checkList", checkResponses);
			} catch (Exception e) {
				e.printStackTrace();
				try {
					restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
				} catch (UnsupportedEncodingException | MessagingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				logger.info("Exception mail sent");
			}
		}

		return "searchChecks";
	}
	
	private Check getCheckOnly(String custIdStr, Integer restaurantId) {
		Integer custId = -1;
		Check  check = new Check();
		if (!StringUtility.isNullOrEmpty(custIdStr)) {
			custId = Integer.parseInt(custIdStr);
			check = checkService.getCheckByCustId(restaurantId, custId);
        }
        return check;
	}
	
	private Check getCheck(String tableIdStr, String custIdStr, Integer restaurantId) {
		Check check = null;
		Integer tableId = -1;
		Integer custId = -1;
		Customer customer= null;
		logger.info("6>getCheck() api called (custID :"+custIdStr+") (:resturant ID :"+restaurantId+")");
		if (!StringUtility.isNullOrEmpty(tableIdStr)) {
			tableId = Integer.parseInt(tableIdStr);
			check = checkService.getCheckByTableId(restaurantId, tableId);
		} else if (!StringUtility.isNullOrEmpty(custIdStr)) {
			custId = Integer.parseInt(custIdStr);
			check = checkService.getCheckByCustId(restaurantId, custId);
        }
        if (check == null) {
			check = new Check();
			// Need to add logic to decide on what checktype to set.
			if (tableId > 0) {
				check.setCheckType(CheckType.Table);
			}
			check.setRestaurantId(restaurantId);
			check.setOpenTime(new Date());
			// Erich: had to manually set closeTime
			check.setCloseTime(new Date());
			check.setModifiedTime(new Date());

			// We use restaurant Id as userId currently. will have to fix this in future.
			Integer userId = restaurantId;
            customer = customerService.getCustomer(custId);
            User user = userService.getUser(userId);
			check.setStatus(com.cookedspecially.enums.check.Status.Unpaid);
			SeatingTable table = null;
			if (tableId > 0) {
				table = seatingTableService.getSeatingTable(tableId);
				if (table != null) {
					check.setTableId(tableId);
					//table.setStatus(com.cookedspecially.enums.table.Status.Busy);
					seatingTableService.addSeatingTable(table);
				}
			}
			if (custId > 0) {
				customer = customerService.getCustomer(custId);
				if (customer != null) {
					check.setCustomerId(custId);
				}
			}
			if (table != null || customer != null) {
				checkService.addCheck(check);
			}
		} else {
			List<Order> orders = check.getOrders();
			if (orders != null && orders.size() > 0 && check.getBill() == 0) {
				// This is a case where migration is not performed.
				// We use restaurant Id as userId currently. will have to fix this in future.
				logger.info("if orders !=null and orders.size >0 && check.getBill() ==0 then..");
				Integer restaurantId1 = check.getRestaurantId();
				Restaurant rest = restService.getRestaurant(restaurantId1);
				if (rest != null) {
					float checkBill = 0;
					for (Order order : orders) {
						if (order.getStatus() != Status.CANCELLED) {
							checkBill += order.getBill();
						}
					}
					check.setBill(checkBill);
					logger.info("SetBill to Check table :"+checkBill);
					checkService.addCheck(check);
				}
			}
		}
		logger.info("9>return check/invoiceId :" +check.getCheckId() +"/"+check.getInvoiceId());
		return check;
	}
	
	private CheckResponse getCheckResponse(String tableIdStr, String custIdStr, Integer restaurantId) {
		Check check = getCheck(tableIdStr, custIdStr, restaurantId);
		Restaurant rest =  restService.getRestaurant(check.getRestaurantId());
		if (check != null) {
			Float waiveOff =null;
			if(check.getOutCircleDeliveryCharges()==0){
				waiveOff = identifyWaiveOffCharges(check);
			}
			return new CheckResponse(check,taxTypeService,waiveOff,rest);
		} else {
			return null;
		}
	}
	
	private CheckResponse getCheckResponseByCheckId(String checkIdStr) {
		if (!StringUtility.isNullOrEmpty(checkIdStr)) {
			Integer checkId = Integer.parseInt(checkIdStr);
			Check check = checkService.getCheck(checkId);
			Restaurant rest =  restService.getRestaurant(check.getRestaurantId());
			if (check != null) {
				Float waiveOff =null;
				if(check.getOutCircleDeliveryCharges()==0){
					waiveOff = identifyWaiveOffCharges(check);
				}
				return new CheckResponse(check,taxTypeService,waiveOff,rest);
			}
		}
		return null;
	}
	
	private Float identifyWaiveOffCharges(Check check){

		List<DeliveryArea>  deliveryArea  = deliveryAreaService.listDeliveryAreasByResaurant(check.getRestaurantId());
		Float  waiveOffCharges = null;
		if(check.getOutCircleDeliveryCharges()==0){
		for(DeliveryArea  area : deliveryArea ){
			if(check.getDeliveryArea()!=null){
			if(check.getDeliveryArea().equals(area.getName())){
				waiveOffCharges = area.getDeliveryCharges();
			}
			}
		}
		}


        return waiveOffCharges;
	}
	
	@RequestMapping(value = "/getCheckWithOrders.json", method = RequestMethod.GET)
	public @ResponseBody Check getCheckWithOrderJSON(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required=false) String tableId, 
			@RequestParam(required=false) String  custId, @RequestParam String restaurantId) {
		//String tableIdStr = tableId;
		//String custIdStr = custId;
		if(custId == null)
			custId = (String)request.getAttribute("custId");

		logger.info("5>getCheckWithOrderJson api Called custIdstr :"+custId);
		String restaurantIdStr = restaurantId;
		if(restaurantIdStr == null)
			restaurantIdStr = (String)request.getAttribute("restaurantId");

		Integer restaurantID = Integer.parseInt(restaurantIdStr);
		return getCheck(tableId, custId, restaurantID);
	}
	
	@RequestMapping(value = "/getCheckOnly.json", method = RequestMethod.GET)
	public @ResponseBody Check getCheckOnlyJSON(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String  custId,@RequestParam Integer  restaurantId) {
		//String tableIdStr = request.getParameter("tableId");
		//String custIdStr = request.getParameter("custId");
		//logger.info("5>getCheckWithOrderJson api Called custIdstr :"+custIdStr);
		//Integer restaurantID = Integer.parseInt(restaurantId);
		return getCheckOnly(custId, restaurantId);
	}
	
	@RequestMapping(value = "/getCheck.json", method = RequestMethod.GET)
	public @ResponseBody CheckResponse getCheckJSON(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required=false) String  custId,
			@RequestParam(required=false) String  tableId,
			@RequestParam Integer  restaurantId) {
		//String tableIdStr = request.getParameter("tableId");
		//String custIdStr = request.getParameter("custId");
		//Integer restaurantId = Integer.parseInt(restaurantId);
		logger.info("getCheck.json called custIdstr :"+custId);
		return getCheckResponse(tableId, custId, restaurantId);
	}
	
	@RequestMapping(value = "/generateCheckForPrint", method = RequestMethod.GET)
	public String generateCheckForPrint(Map<String, Object> map, HttpServletRequest request,
			HttpServletResponse response, 
			@ApiParam(value="Template names are : defaultbill (for all restaurants except saladdays use saladdaysbill for saladdays") @RequestParam String templateName,
			@RequestParam String checkId ) {
		String billTemplateName = templateName;
		Integer checkID = Integer.parseInt(checkId);
		Check check = checkService.getCheck(checkID);
		/* List<Coupon> applied_Coupon= new ArrayList<>();
	        if(check.getCoupon_Applied().size()>0){
	        	for(Coupon coupon :  check.getCoupon_Applied()){
	        		applied_Coupon.add(couponService.getCouponById(coupon.getCoupanId()));
	        	}
	        }
	        check.setCoupon_Applied(applied_Coupon);*/
		//logger.info("***************Printing Check***********************");
		//logger.info("generateCheckForPrint api, checkId :"+checkId);
		if (check != null) {
			//User user = userService.getUser(check.getRestaurantId());
			Restaurant rest=restService.getRestaurant(check.getRestaurantId());
			map.put("restaurant",rest);
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
			cal.setTime(check.getOpenTime());
			//logger.info("Check Open Time :"+check.getOpenTime());
			DateFormat formatter1;
			formatter1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			formatter1.setTimeZone(cal.getTimeZone());
			map.put("checkDate", formatter1.format(cal.getTime()));
			Float waveOff =null;
			if(check.getOutCircleDeliveryCharges()==0){
				waveOff = identifyWaiveOffCharges(check);
			}
			PrintCheckFromDB checkResponse = new PrintCheckFromDB(check,taxTypeService,waveOff,rest);
			if(rest.isRoundOffAmount()){
				//checkResponse.setTotal(Math.round(checkResponse.getTotal()));
				checkResponse.setRoundedOffTotal(Math.round(checkResponse.getRoundedOffTotal()));
			}
			map.put("checkRespone", checkResponse);
			if(rest.getParentRestaurantId()!=32){
				billTemplateName = "defaultbill";
			}
			if (check.getCustomerId() > 0) {
				Customer customer = customerService.getCustomer(check.getCustomerId());
				if(check.getCheckType()==CheckType.TakeAway){
					customer.setAddress("");
					customer.setCity("");
					customer.setDeliveryArea("");
				}
				map.put("customer", customer);
			} else if (check.getTableId() > 0) {
				map.put("tableId", check.getTableId());
			}

			Map<String, JsonDish> itemsMap = new TreeMap<String, JsonDish>();
			List<CheckDishResponse> items = checkResponse.getItems();
			List<JsonAddOn> jsonAdd =  new ArrayList<JsonAddOn>();
			for (CheckDishResponse item : items) {

				if (itemsMap.containsKey(item.getDishId()+""+item.getDishSize().replaceAll("\\s",""))) {
					JsonDish jsonDish = itemsMap.get(item.getDishId()+""+item.getDishSize().replaceAll("\\s",""));
					jsonDish.setPrice(jsonDish.getPrice() + item.getPrice());
					jsonDish.setQuantity(jsonDish.getQuantity() + 1);
				}
				else {
					JsonDish jsonDish = new JsonDish();
					jsonDish.setQuantity(1);
					jsonDish.setName(item.getName());
					jsonDish.setId(item.getDishId());
					jsonDish.setPrice(item.getPrice());
					jsonDish.setDishSize(item.getDishSize());
					List<OrderAddOn> orderAddOn = item.getAddOnresponse();
					if(orderAddOn!=null){
					for( OrderAddOn oad : orderAddOn){
						JsonAddOn jsonAddOn = new JsonAddOn();
						jsonAddOn.setId(oad.getAddOnId());
						jsonAddOn.setDishId(item.getDishId());
						jsonAddOn.setName(oad.getName());
						jsonAddOn.setPrice(oad.getPrice());
						jsonAddOn.setQuantity(oad.getQuantity());
						jsonAddOn.setDishSize(oad.getDishSize());
						jsonAdd.add(jsonAddOn);
						jsonDish.setAddOns(jsonAdd);
					}
				}
					itemsMap.put(item.getDishId()+""+item.getDishSize().replaceAll("\\s",""), jsonDish);
				}
			}
			map.put("itemsMap", itemsMap);
		}


        return "custom/" + billTemplateName;
	}

	@RequestMapping(value = "/emailNewCheck", method = RequestMethod.GET)
	@ApiIgnore
	@Deprecated
	public @ResponseBody String emailNewCheck(HttpServletRequest request, HttpServletResponse response) throws MessagingException {
		String emailAddr = request.getParameter("email");
	    // Prepare the evaluation context
	    final Context ctx = new Context(request.getLocale());
	    ctx.setVariable("name", "Shashank");
	    ctx.setVariable("subscriptionDate", new Date());
	    ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
	    //ctx.setVariable("imageResourceName", imageResourceName); // so that we can reference it from HTML

	    // Prepare message using a Spring helper
	    final MimeMessage mimeMessage = mailSender.createMimeMessage();
	    final MimeMessageHelper message =
	        new MimeMessageHelper(mimeMessage, false, "UTF-8"); // true = multipart

	    message.setSubject("Example HTML email with inline image");
	    message.setFrom("thymeleaf@example.com");
	    message.setTo(emailAddr);

	    // Create the HTML body using Thymeleaf
	    final String htmlContent = templateEngine.process("email-inlineimage", ctx);
	    message.setText(htmlContent, true); // true = isHtml

	    // Add the inline image, referenced from the HTML code as "cid:${imageResourceName}"
	    //final InputStreamSource imageSource = new ByteArrayResource(imageBytes);
	    //message.addInline(imageResourceName, imageSource, imageContentType);

	    // Send mail
	    mailSender.send(mimeMessage);

		return "sucess";
	}

    @RequestMapping(value = "/emailTemplatedCheck", method = RequestMethod.GET)
	public @ResponseBody String emailTemplatedCheck(HttpServletRequest request, HttpServletResponse response,
			@ApiParam(value="Template names are : defaultemailbill (for all restaurants except saladdays,"
					+ " use saladdaysemailbill for saladdays") @RequestParam String templateName,
			@RequestParam String checkId,@RequestParam String email) throws MessagingException, UnsupportedEncodingException {

		//String emailAddr = request.getParameter("email");
		//String templateName =request.getParameter("templateName");
		Integer checkID = Integer.parseInt(checkId);


        // Prepare the evaluation context
	    final Context ctx = new Context(request.getLocale());

        Check check = checkService.getCheck(checkID);


		logger.info("Customer Check emailling in process.. : check Id/invoiceId : "+checkId+"/"+check.getInvoiceId());
		//User user = null;
		Restaurant rest=null;
		if (check != null) {

			Float waiveOff =null;
			if(check.getOutCircleDeliveryCharges()==0){
				waiveOff = identifyWaiveOffCharges(check);
			}
			logger.info("Customer Ph. no :"+check.getPhone());
			rest=restService.getRestaurant(check.getRestaurantId());
			CheckResponse checkResponse = new CheckResponse(check,taxTypeService,waiveOff,rest);
			ctx.setVariable("checkRespone", checkResponse);
			if (check.getCustomerId() > 0) {
				Customer customer = customerService.getCustomer(check.getCustomerId());
				ctx.setVariable("customer", customer);
			} else if (check.getTableId() > 0) {
				ctx.setVariable("tableId", check.getTableId());
			}
			//user = userService.getUser(check.getRestaurantId());

			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
			cal.setTime(check.getOpenTime());
			DateFormat formatter1;
			formatter1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			formatter1.setTimeZone(cal.getTimeZone());
			ctx.setVariable("checkDate", formatter1.format(cal.getTime()));
			Map<String, JsonDish> itemsMap = new TreeMap<String, JsonDish>();
			List<CheckDishResponse> items = checkResponse.getItems();
			/*for (CheckDishResponse item : items) {
				if (itemsMap.containsKey(item.getName())) {
					JsonDish jsonDish = itemsMap.get(item.getName());
					jsonDish.setPrice(jsonDish.getPrice() + item.getPrice());
					jsonDish.setQuantity(jsonDish.getQuantity() + 1);
				} else {
					JsonDish jsonDish = new JsonDish();
					jsonDish.setQuantity(1);
					jsonDish.setName(item.getName());
					jsonDish.setPrice(item.getPrice());
					itemsMap.put(item.getName(), jsonDish);
				}
			}*/

			List<JsonAddOn> jsonAdd =  new ArrayList<JsonAddOn>();
			for (CheckDishResponse item : items) {
				if (itemsMap.containsKey(item.getName())) {
					JsonDish jsonDish = itemsMap.get(item.getName());
					jsonDish.setPrice(jsonDish.getPrice() + item.getPrice());
					jsonDish.setQuantity(jsonDish.getQuantity() + 1);
				}
				else {
					JsonDish jsonDish = new JsonDish();
					jsonDish.setQuantity(1);
					jsonDish.setName(item.getName());
					jsonDish.setId(item.getDishId());
					jsonDish.setPrice(item.getPrice());
					jsonDish.setDishSize(item.getDishSize());
					List<OrderAddOn> orderAddOn = item.getAddOnresponse();

					if(orderAddOn!=null){
					for( OrderAddOn oad : orderAddOn){
						JsonAddOn jsonAddOn = new JsonAddOn();
						jsonAddOn.setId(oad.getAddOnId());
						jsonAddOn.setDishId(item.getDishId());
						jsonAddOn.setName(oad.getName());
						jsonAddOn.setQuantity(oad.getQuantity());
						jsonAddOn.setPrice(oad.getPrice());
						jsonAddOn.setDishSize(oad.getDishSize());
						jsonAdd.add(jsonAddOn);
						jsonDish.setAddOns(jsonAdd);
					}
				}
					itemsMap.put(item.getName(), jsonDish);
				}
			}

			ctx.setVariable("itemsMap", itemsMap);
		} else {
			return "No check found";
		}

	    // Prepare message using a Spring helper
	    final MimeMessage mimeMessage = mailSender.createMimeMessage();
	    final MimeMessageHelper message =
	        new MimeMessageHelper(mimeMessage, false, "UTF-8"); // true = multipart

	    message.setSubject("Your Receipt");
	    if (rest != null) {
	    	//String senderEmail = StringUtility.isNullOrEmpty(rest.getMailUsername()) ? rest.getRestaurantName() : rest.getMailUsername();
	    	String senderEmail=rest.getMailUsername();
	    	InternetAddress restaurantEmailAddress = new InternetAddress(senderEmail, rest.getBussinessName());
	    	message.setFrom(restaurantEmailAddress);
	    	message.setReplyTo(restaurantEmailAddress);
	    }
	    message.setTo(email);
	    // Create the HTML body using Thymeleaf
	    final String htmlContent = templateEngine.process(templateName, ctx);
	    message.setText(htmlContent, true); // true = isHtml
	    String oldUsername = null;
	    String oldPassword = null;
	    String oldHost = null;
	    String oldProtocol = null;
	    Integer oldPort = -1;
	    if (!StringUtility.isNullOrEmpty(rest.getMailUsername()) && !StringUtility.isNullOrEmpty(rest.getMailPassword())) {
            oldUsername = mailSender.getUsername();
            oldPassword = mailSender.getPassword();
	    	oldHost = mailSender.getHost();
	    	oldProtocol = mailSender.getProtocol();
	    	oldPort = mailSender.getPort();
	    	mailSender.setUsername(rest.getMailUsername());
	    	mailSender.setPassword(rest.getMailPassword());
	    	mailSender.setHost(rest.getMailHost());
	    	mailSender.setProtocol(rest.getMailProtocol());
	    	mailSender.setPort(rest.getMailPort());
	    }

	    //mailSender.setUsername("hello@saladdays.co");
	    //mailSender.setPassword("tendulkar_100");

	    // Send mail
	    mailSender.send(mimeMessage);
        logger.info("email sent for checkId :" + checkId+"/"+check.getInvoiceId());
	    if (!StringUtility.isNullOrEmpty(oldUsername) && !StringUtility.isNullOrEmpty(oldPassword)) {
	    	mailSender.setUsername(oldUsername);
	    	mailSender.setPassword(oldPassword);
	    	mailSender.setHost(oldHost);
	    	mailSender.setProtocol(oldProtocol);
	    	mailSender.setPort(oldPort);
	    }

	    return "Email Sent Successfully";
	}
	
    
    String setDescription(Status status,Customer customer,Check check,String refund,String reason,float extraAmount) {

        if (customer.getCredit().getStatus() == CustomerCreditAccountStatus.ACTIVE && customer.getCredit().getCreditType().getBillingCycle() == BilligCycle.ONE_OFF) {
            Restaurant restaurant = restaurantServices.getRestaurant(check.getRestaurantId());
            if(status==Status.CANCELLED && "CREDIT".equalsIgnoreCase(refund)){
	    		return "Your order no."+check.getOrderId()+" (of  Total Rs."+check.getRoundOffTotal()+") has been cancelled. We owe you  Rs "+Math.abs(customer.getCredit().getCreditBalance())+","
	    				+ "which will be automatically deducted from your next order amount."
	    				+ "Should there be any concern pls call at "+restaurant.getBussinessPhoneNo()+".  Assuring you of our best, always";
	    	}else if(status==Status.CANCELLED && "CASH".equalsIgnoreCase(refund)){
	    		return "Your order with order No. "+check.getOrderId()+" has been cancelled. We have initiated the refund as CASH. One of our delivery boy will hand over it to you soon";
	    	}else if("COMPLETED".equalsIgnoreCase(refund)&& "DELIVERED".equalsIgnoreCase(reason)){
	    		return "Your order no."+check.getOrderId()+" (of  Total Rs."+check.getRoundOffTotal()+") has been DELIVERED. Your current balance with "+restaurant.getRestaurantName()+" is "+Math.abs(customer.getCredit().getCreditBalance())+"."
	    				+"Should there be any concern pls call at "+restaurant.getBussinessPhoneNo()+".  Assuring you of our best, always";
	    	}else if("UPDATE".equalsIgnoreCase(refund) && "DISCOUNT".equalsIgnoreCase(reason)){
	    		return "Your order 	no."+check.getOrderId()+" has been discounted by Rs "+extraAmount+". We owe you  Rs "+Math.abs(customer.getCredit().getCreditBalance())+"."
	    				+ "which will be automatically deducted from your next order amount. Should there be any concern pls call at "+restaurant.getBussinessPhoneNo()+".  Assuring you of our best, always";
	    	}else if("UPDATE".equalsIgnoreCase(refund) && "ADDED".equalsIgnoreCase(reason)){
	    		return "Your order no."+check.getOrderId()+" (item of Rs "+extraAmount+" has been cancelled). We owe you  Rs "+Math.abs(customer.getCredit().getCreditBalance())+","
	    				+ "which will be automatically deducted to your next order amount. Should there be any concern pls call at "+restaurant.getBussinessPhoneNo()+".  Assuring you of our best, always";
	    	}else if("UPDATE".equalsIgnoreCase(refund) && "CHARGED".equalsIgnoreCase(reason)){
	    		return "Your order no."+check.getOrderId()+" has been updated (item of Rs "+extraAmount+" has been added). You owe us  Rs "+Math.abs(customer.getCredit().getCreditBalance())+""
	    				+ ",which will be automatically added to your next order amount. Should there be any concern pls call at "+restaurant.getBussinessPhoneNo()+".  Assuring you of our best, always";
	    	}else if("PAYMENT_TYPE_CHANGED".equalsIgnoreCase(reason) && refund!=null){
	    		if(customer.getCredit().getCreditBalance()<0){
	    			return "Payment type for order no."+check.getOrderId()+" has been changed to "+check.getOrders().get(0).getPaymentStatus()+" from "+refund+". We owe you Rs "+Math.abs(customer.getCredit().getCreditBalance())+","
		    				+ "which will be automatically deducted from your next order amount. Should there be any concern pls call at "+restaurant.getBussinessPhoneNo()+".  Assuring you of our best, always";
	    		}else if(customer.getCredit().getCreditBalance()>0){
	    			return "Payment type for order no. "+check.getOrderId()+" has been changed to "+check.getOrders().get(0).getPaymentStatus()+" from "+refund+". You owe us Rs "+Math.abs(customer.getCredit().getCreditBalance())+","
		    				+ "which will be automatically added to your next order amount.We request you to make this payment at the earliest. Should there be any concern pls call at "+restaurant.getBussinessPhoneNo()+".  Assuring you of our best, always";
	    		}else{
	    			return "Payment type for order no. "+check.getOrderId()+" has been changed to "+check.getOrders().get(0).getPaymentStatus()+" from "+refund+".You owe us Rs "+Math.abs(customer.getCredit().getCreditBalance())+"."
	    					+ "Should there be any concern pls call at "+restaurant.getBussinessPhoneNo()+".  Assuring you of our best, always";

	    		}
      		}
    		else {
    			return "Thanks for choosing Salad Days. While our kitchen is busy whipping up your order, here is your order summary for order no."+check.getOrderId();
	    	}
    }else {
    	Restaurant restaurant =  restaurantServices.getRestaurant(check.getRestaurantId());
		if(customer.getCredit().getStatus()==CustomerCreditAccountStatus.ACTIVE && customer.getCredit().getCreditType().getBillingCycle()!=BilligCycle.ONE_OFF){
			if(status==Status.CANCELLED){
				if(check.getStatus()==com.cookedspecially.enums.check.Status.Paid){
					return "Your order No. "+check.getOrderId()+" (of  Total Rs."+check.getRoundOffTotal()+") has been cancelled. This amount has been adjusted to your current credit balance. Now you owe us Rs "+Math.abs(customer.getCredit().getCreditBalance())+"."
	    				+ "Should there be any concern pls call at "+restaurant.getBussinessPhoneNo()+".  Assuring you of our best, always.";
				}else{
					return "";
				}
				}else{
				return "Thanks for choosing Salad Days. While our kitchen is busy whipping up your order, here is your order summary for order no."+check.getOrderId();
			}
		}else{
			return "Thanks for choosing Salad Days. While our kitchen is busy whipping up your order, here is your order summary for order no."+check.getOrderId();
		}
		}
     }
    
    
	public String emailCheckFromServer(HttpServletRequest request,int checkId, String emailAddr, String templateName, String subject, String sender, String refund, String reason, float extraAmount) throws MessagingException, UnsupportedEncodingException{

		// Prepare the evaluation context
		if(emailAddr!=null && (!"".equalsIgnoreCase(emailAddr))){
		    final Context ctx = new Context();
	       
		    logger.info("checkId : "+checkId);
			logger.info(" get locale  " + request.getLocale());
	
			logger.info(" getCheck  " + checkService.getCheck(checkId));
	        Check check = checkService.getCheck(checkId);
	        logger.info("invoiceId : "+check.getInvoiceId());
	       /* List<Coupon> applied_Coupon= new ArrayList<>();
	        if(check.getCoupon_Applied().size()>0){
	        	for(Coupon coupon :  check.getCoupon_Applied()){
	        		applied_Coupon.add(couponService.getCouponById(coupon.getCoupanId()));
	        	}
	        }
	        check.setCoupon_Applied(applied_Coupon);*/
	        logger.info("***********************sending check***********************************");
			logger.info("Customer Check emailling in process.. : check Id/invoiceId: "+checkId+"/"+check.getInvoiceId());
	
			logger.info("Check email Address: " + emailAddr);
	
			User user = null;
			Restaurant rest=null;
			if (check != null) {
	
				Float waiveOff =null;
				if(check.getOutCircleDeliveryCharges()==0){
					waiveOff = identifyWaiveOffCharges(check);
				}
				logger.info("Customer Ph. no :"+check.getPhone());
				rest=restService.getRestaurant(check.getRestaurantId());
				CheckResponse checkResponse = new CheckResponse(check,taxTypeService,waiveOff,rest);
	
				if(rest.getParentRestaurantId()!=32){
					templateName="defaultemailbill";
					subject=rest.getBussinessName()+" : Receipt";
				}else if(rest.getParentRestaurantId()==32){
			    	subject = "Salad Days : Receipt";
			    }
				ctx.setVariable("checkRespone", checkResponse);
				if (check.getCustomerId() > 0) {
					Customer customer = customerService.getCustomer(check.getCustomerId());
					String description="";
					if(customer.getCredit()!=null){
						if(customer.getCredit().getCreditBalance()!=0){
						  description  = setDescription(check.getOrders().get(0).getStatus(),customer,check,refund,reason,extraAmount);
						}else if(check.getOrders().get(0).getStatus()==Status.CANCELLED){
							description="Thanks for choosing Saladdays";
						}
					}else{
						if(check.getOrders().get(0).getStatus()!=Status.CANCELLED){
							description = "Thanks for choosing Salad Days. While our kitchen is busy whipping up your order, here is your order summary for order no."+check.getOrderId();
						}else if(check.getOrders().get(0).getStatus()==Status.CANCELLED){
							description="Thanks for choosing Saladdays";
						}
					}
					ctx.setVariable("description", description);
					ctx.setVariable("customer", customer);
				} else if (check.getTableId() > 0) {
					ctx.setVariable("tableId", check.getTableId());
				}
				//user = userService.getUser(check.getRestaurantId());
				if(rest!=null){
					if(rest.isRoundOffAmount()){
						//checkResponse.setTotal(Math.round(checkResponse.getTotal()));
						check.setRoundOffTotal(Math.round(checkResponse.getRoundedOffTotal()));
						checkResponse.setRoundedOffTotal(Math.round(checkResponse.getRoundedOffTotal()));
					}else{
				    	check.setRoundOffTotal(checkResponse.getRoundedOffTotal());
					}
					}
				ctx.setVariable("user", rest);
				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
				cal.setTime(check.getOpenTime());
				DateFormat formatter1;
				formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				formatter1.setTimeZone(cal.getTimeZone());
				ctx.setVariable("checkDate", formatter1.format(cal.getTime()));
				Map<String, JsonDish> itemsMap = new TreeMap<String, JsonDish>();
				List<CheckDishResponse> items = checkResponse.getItems();
	
				List<JsonAddOn> jsonAdd =  new ArrayList<JsonAddOn>();
				for (CheckDishResponse item : items) {
					if (itemsMap.containsKey(item.getDishId()+""+item.getDishSize().replaceAll("\\s",""))) {
						JsonDish jsonDish = itemsMap.get(item.getDishId()+""+item.getDishSize().replaceAll("\\s",""));
						jsonDish.setPrice(jsonDish.getPrice() + item.getPrice());
						jsonDish.setQuantity(jsonDish.getQuantity() + 1);
					}
					else {
						JsonDish jsonDish = new JsonDish();
						jsonDish.setQuantity(1);
						jsonDish.setName(item.getName());
						jsonDish.setId(item.getDishId());
						jsonDish.setPrice(item.getPrice());
						jsonDish.setDishSize(item.getDishSize());
						List<OrderAddOn> orderAddOn = item.getAddOnresponse();
						if(orderAddOn!=null){
						for( OrderAddOn oad : orderAddOn){
							JsonAddOn jsonAddOn = new JsonAddOn();
							jsonAddOn.setId(oad.getAddOnId());
							jsonAddOn.setDishId(item.getDishId());
							jsonAddOn.setName(oad.getName());
							jsonAddOn.setQuantity(oad.getQuantity());
							jsonAddOn.setPrice(oad.getPrice());
							jsonAddOn.setDishSize(oad.getDishSize());
							jsonAdd.add(jsonAddOn);
							jsonDish.setAddOns(jsonAdd);
						}
					}
						itemsMap.put(item.getDishId()+""+item.getDishSize().replaceAll("\\s",""), jsonDish);
					}
				}
	
				ctx.setVariable("itemsMap", itemsMap);
			} else {
				logger.info("No check Found for "+emailAddr);
				return "No check found";
			}
		    // Prepare message using a Spring helper
		    final MimeMessage mimeMessage = mailSender.createMimeMessage();
		    final MimeMessageHelper message =
		        new MimeMessageHelper(mimeMessage, false, "UTF-8"); // true = multipart
	
		    message.setSubject(subject);
		    if (rest != null) {
		    	//String senderEmail = StringUtility.isNullOrEmpty(rest.getMailUsername()) ? user.getUsername() : rest.getMailUsername();
		    	String senderEmail=rest.getMailUsername();
		    	InternetAddress restaurantEmailAddress = new InternetAddress(senderEmail, rest.getBussinessName());
		    	message.setFrom(restaurantEmailAddress);
		    	message.setReplyTo(restaurantEmailAddress);
		    }
		    message.setTo(emailAddr);
		    // Create the HTML body using Thymeleaf
	
		    final String htmlContent = templateEngine.process(templateName, ctx);
		    message.setText(htmlContent, true); // true = isHtml
		    String oldUsername = null;
		    String oldPassword = null;
		    String oldHost = null;
		    String oldProtocol = null;
		    Integer oldPort = -1;
		    if (!StringUtility.isNullOrEmpty(rest.getMailUsername()) && !StringUtility.isNullOrEmpty(rest.getMailPassword())) {
	            oldUsername = mailSender.getUsername();
	            oldPassword = mailSender.getPassword();
		    	oldHost = mailSender.getHost();
		    	oldProtocol = mailSender.getProtocol();
		    	oldPort = mailSender.getPort();
		    	if(sender!=null){
		    		MailerUtility mu = new MailerUtility();
	                mailSender.setUsername(MailerUtility.username);
	                mailSender.setPassword(MailerUtility.password);
	            }else{
		    	mailSender.setUsername(rest.getMailUsername());
		    	mailSender.setPassword(rest.getMailPassword());
		    	}
		    	mailSender.setHost(rest.getMailHost());
		    	mailSender.setProtocol(rest.getMailProtocol());
		    	mailSender.setPort(rest.getMailPort());
		    }
	
		    //mailSender.setUsername("hello@saladdays.co");
		    //mailSender.setPassword("tendulkar_100");
	
		    // Send mail
		    mailSender.send(mimeMessage);
	        logger.info("email sent for checkId :" + checkId);
		    if (!StringUtility.isNullOrEmpty(oldUsername) && !StringUtility.isNullOrEmpty(oldPassword)) {
		    	mailSender.setUsername(oldUsername);
		    	mailSender.setPassword(oldPassword);
		    	mailSender.setHost(oldHost);
		    	mailSender.setProtocol(oldProtocol);
		    	mailSender.setPort(oldPort);
		    }
		    logger.info("***********************sending end***********************************");
		    return "Email Sent Successfully";
		}
		return "Email sent Failed because of this email Address:" +emailAddr;
	}

	@RequestMapping(value = "/sendMarketingEmail", method = RequestMethod.POST)
	@Deprecated
	@ApiIgnore
	public @ResponseBody String sendMarketingEmail(HttpServletRequest request, HttpServletResponse response) throws MessagingException, UnsupportedEncodingException {
		String emailAddr = request.getParameter("email");
		String emailBody =request.getParameter("emailBody");
		if(emailBody.contains("<br>") && !emailBody.contains("</br>")) {
			emailBody = emailBody.replace("<br>", "<br/>");
		}
		String restIdstr  = request.getParameter("restaurantId");
        String subject = request.getParameter("subject");
        String templateName = "marketingEmail";
		// Prepare the evaluation context
	    final Context ctx = new Context(request.getLocale());

		ctx.setVariable("email", emailAddr);
		ctx.setVariable("emailBody", emailBody);
	    // Prepare message using a Spring helper
	    final MimeMessage mimeMessage = mailSender.createMimeMessage();
	    final MimeMessageHelper message =
	        new MimeMessageHelper(mimeMessage, false, "UTF-8"); // true = multipart

	    message.setSubject(subject);
	    //Integer userId = (Integer) request.getSession().getAttribute("userId");
	    Integer restId = Integer.parseInt(restIdstr);
	   // User user = userService.getUser(userId);
	    Restaurant rest=restService.getRestaurant(restId);
	    if (rest != null) {
	    	//String senderEmail = StringUtility.isNullOrEmpty(user.getMailUsername()) ? user.getUsername() : user.getMailUsername();
	    	String senderEmail=rest.getMailUsername();
	    	InternetAddress restaurantEmailAddress = new InternetAddress(senderEmail, rest.getBussinessName());
	    	message.setFrom(restaurantEmailAddress);
	    	message.setReplyTo(restaurantEmailAddress);
	    }
	    message.setTo(emailAddr);
	    message.setBcc("shashankagarwal1706@gmail.com");
	    // Create the HTML body using Thymeleaf
	    final String htmlContent = templateEngine.process(templateName, ctx);
	    message.setText(htmlContent, true); // true = isHtml


        String oldUsername = null;
	    String oldPassword = null;
	    String oldHost = null;
	    String oldProtocol = null;
	    Integer oldPort = -1;
	    if (rest.getMailPassword() != null) {
            oldUsername = mailSender.getUsername();
            oldPassword = mailSender.getPassword();
	    	oldHost = mailSender.getHost();
	    	oldProtocol = mailSender.getProtocol();
	    	oldPort = mailSender.getPort();
	    	mailSender.setUsername(rest.getMailUsername());
	    	mailSender.setPassword(rest.getMailPassword());
	    	mailSender.setHost(rest.getMailHost());
	    	mailSender.setProtocol(rest.getMailProtocol());
	    	mailSender.setPort(rest.getMailPort());
	    }

	    //mailSender.setUsername("hello@saladdays.co");
	    //mailSender.setPassword("tendulkar_100");

	    // Send mail
	    mailSender.send(mimeMessage);

	    if (!StringUtility.isNullOrEmpty(oldUsername) && !StringUtility.isNullOrEmpty(oldPassword)) {
	    	mailSender.setUsername(oldUsername);
	    	mailSender.setPassword(oldPassword);
	    	mailSender.setHost(oldHost);
	    	mailSender.setProtocol(oldProtocol);
	    	mailSender.setPort(oldPort);
	    }

	    return "Email Sent Successfully";
	}

    @RequestMapping("/marketingEmailForm")
    @Deprecated
    @ApiIgnore
	public String marketingEmailForm(HttpServletRequest request, HttpServletResponse response) {
		return "marketingemailForm";
	}

	@RequestMapping(value = "/emailCheck", method = RequestMethod.GET)
	@Deprecated
	@ApiIgnore
	public @ResponseBody String emailCheck(HttpServletRequest request, HttpServletResponse response) {
		String tableIdStr = request.getParameter("tableId");
		String custIdStr = request.getParameter("custId");
		String checkIdStr = request.getParameter("checkId");
		String emailAddr = request.getParameter("email");
		Integer restaurantId = Integer.parseInt(request.getParameter("restaurantId"));
		CheckResponse checkResponse = null;

		if (StringUtility.isNullOrEmpty(checkIdStr)){
			checkResponse = getCheckResponse(tableIdStr, custIdStr, restaurantId);
		} else {
			checkResponse = getCheckResponseByCheckId(checkIdStr);
		}
		if (checkResponse != null) {
			//User user = userService.getUser(restaurantId);
			Restaurant rest=restService.getRestaurant(restaurantId);
			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append("<html><body>");
			if (rest != null) {
				strBuilder.append("Restaurant Name : " + rest.getBussinessName() + "<br />");
			}
			//strBuilder.append("Check Id : " + checkResponse.getId() + "<br />");
//			if (checkResponse.getTableId() != null && checkResponse.getTableId() > 0) {
//				strBuilder.append("Table Id : " + checkResponse.getTableId() + "<br />");
//			}
//			if (checkResponse.getCustomerId() != null) {
//				strBuilder.append("Customer Id : " + checkResponse.getCustomerId() + "<br /><br /> ");
//			}
			strBuilder.append("<b><i>Items</i></b> <br />");
			strBuilder.append("<table><tr><th>Name</th><th>Price</th></tr>");
			List<CheckDishResponse> dishes = checkResponse.getItems();
			for (CheckDishResponse dish : dishes) {
				strBuilder.append("<tr>");
				strBuilder.append("<td>" + dish.getName() + "</td>");
				strBuilder.append("<td>" + dish.getPrice() + "</td>");
				strBuilder.append("</tr>");
			}
			strBuilder.append("</table>");
			strBuilder.append("<br/>");
			strBuilder.append("<table>");

			strBuilder.append("<tr>");
			strBuilder.append("<td>");
			strBuilder.append("Total: ");
			strBuilder.append("</td>");
			strBuilder.append("<td>");
			strBuilder.append(checkResponse.getAmount());
			strBuilder.append("</td>");
			strBuilder.append("</tr>");
			strBuilder.append("<tr>");
			strBuilder.append("<td>");
			strBuilder.append("Grand Total: ");
			strBuilder.append("</td>");
			strBuilder.append("<td>");
			strBuilder.append(checkResponse.getTotal());
			strBuilder.append("</td>");
			strBuilder.append("</tr>");
			strBuilder.append("</table>");
			strBuilder.append("</body></html>");
			MailerUtility.sendHTMLMail(emailAddr, "Order Reciept", strBuilder.toString(),rest.getMailUsername(), rest.getMailPassword());
		} else {
			return "Error: No check found";
		}

		return "Email Sent Successfully";
	}

    @RequestMapping(value="/getOpenFlag.json", method = RequestMethod.GET)
	public @ResponseBody Map<String,Object> getOpenFlag(HttpServletRequest request, HttpServletResponse response,@RequestParam String restaurantId) throws ParseException, JSONException {

		Map<String,Object> json = new HashMap<String,Object>();
		int restaurantID =Integer.parseInt(restaurantId);
		//User user= userService.getUser(restaurantId);
		Restaurant rest=restService.getRestaurant(restaurantID);
		String flag=rest.getOpenFlag();

        if(flag.equalsIgnoreCase("Always Open")){
			json.put("status", "Always");
		}
		else if(flag.equalsIgnoreCase("Open During Normal Hours")){
			json.put("status", "Normal_Hours");
			json.put("statusText",rest.getClosedText());
		}
		else if(flag.equalsIgnoreCase("Closed")){
			json.put("status", "Closed");
			json.put("statusText",rest.getClosedText());
		}
        //String data = json.toString();
        return json;
	}
	
	@RequestMapping(value = "/time.json", method = RequestMethod.GET)
	@Deprecated
	public @ResponseBody  Map<String,Object> getTime(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String  day,@ApiParam(value="Time zone  e.g IST, EST etc")@RequestParam String  tz) throws ParseException, JSONException {
		String val =day;
		String zone =tz;

        TimeZone  tZ =  TimeZone.getTimeZone(zone);
	    ArrayList<String> list = new ArrayList<String>();

        if("Today".equalsIgnoreCase(val)){
	    	Calendar cal = Calendar.getInstance(tZ,Locale.ENGLISH );
	    	Calendar calCompare = Calendar.getInstance(tZ,Locale.ENGLISH );
	        int unroundedMinutes = cal.get(Calendar.MINUTE);
	        int mod = unroundedMinutes % 15;
	        cal.add(Calendar.MINUTE, 45);
	        //cal.add(Calendar.MINUTE,(15-mod));
            cal.add(Calendar.MINUTE, mod < 8 ? -mod : (15 - mod));
            Date date = cal.getTime();
	        Date compDate =calCompare.getTime();
	        SimpleDateFormat sdfa = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
	        sdfa.setTimeZone(TimeZone.getTimeZone(zone));
	        String formatdate = sdfa.format(date);
	      //for 12 AM comparison date format
	        String compNightDate=sdfa.format(compDate);
	        String morning = "09:44 AM";
	        String nightM = "09:45 AM";

            String evening = "10:46 PM";
	        String format = "dd-MM-yyyy hh:mm a";
	        String dayNightDiff="12:00 AM";

            SimpleDateFormat sdf = new SimpleDateFormat(format);

	        Date currentAdded = sdf.parse(formatdate);
            //for 12 AM comparison date parse
            Date finalNightCompare =sdf.parse(compNightDate);

            String currentDate = currentAdded.getDate()+"-"+(currentAdded.getMonth()+1)+"-"+cal.get(Calendar.YEAR);
	        Date nightDT = sdf.parse(currentDate+" "+evening);

            Date morningDT =sdf.parse(currentDate+" "+morning);

            Date nightMDT =sdf.parse(currentDate+" "+nightM);

            Date newDayObj=sdf.parse(currentDate+" "+dayNightDiff);

            long dif = currentAdded.getTime();
	        Map<String,Object> json = new HashMap<String, Object>();
	        if(currentAdded.after(morningDT)&&currentAdded.before(nightDT)){
	        while (dif < nightDT.getTime()) {
	        	Date slot = new Date(dif);
	            String time= ""+String.format("%02d",slot.getHours())+":"+String.format("%02d",slot.getMinutes());
	            list.add(time);
	            dif += 900000;
	        }
	        }
	       else if(currentAdded.before(nightMDT)&&finalNightCompare.after(newDayObj) ){

                long diff = nightMDT.getTime();
		        while (diff < nightDT.getTime()) {
		            Date slot = new Date(diff);
		            String time= ""+String.format("%02d",slot.getHours())+":"+String.format("%02d",slot.getMinutes());
		            list.add(time);
		            diff += 900000;
		        }
	        }
	        Date da = new Date(dif);
	        String dates=(da.getMonth()+1)+"-"+da.getDate()+"-"+cal.get(Calendar.YEAR);
	        json.put("date",dates);
	        json.put("dateList",list);

            //String output = json.toString();

            return json;
	    }
	   else if("Tomorrow".equalsIgnoreCase(val)){
		   Calendar startDate = Calendar.getInstance(tZ,Locale.ENGLISH);
	    	startDate.setLenient(false);
	    	startDate.add(Calendar.DATE, 1);
	        Date date = startDate.getTime();
	        SimpleDateFormat sdfa = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
	        sdfa.setTimeZone(TimeZone.getTimeZone(zone));
	        String formatdate = sdfa.format(date);
	        String format = "dd-MM-yyyy hh:mm a";

            SimpleDateFormat sdf = new SimpleDateFormat(format);
	        Date dateObj = sdf.parse(formatdate);
	        String date2 = dateObj.getDate()+"-"+(dateObj.getMonth()+1)+"-"+startDate.get(Calendar.YEAR);
	        String time1="09:45 AM";
	        String time2 = "10:46 PM";

	        Date dateObj1 = sdf.parse(date2+" "+time1);
	        Date dateObj2 = sdf.parse(date2+" "+time2);


            long dif = dateObj1.getTime();
	        Map<String,Object> json = new HashMap<String, Object>();
	        while (dif < dateObj2.getTime()) {
	            Date slot = new Date(dif);
	            String time= ""+String.format("%02d",slot.getHours())+":"+String.format("%02d",slot.getMinutes());
	            list.add(time);
	            dif += 900000;
	        }
	        Date da = new Date(dif);
	        String dates=(da.getMonth()+1)+"-"+da.getDate()+"-"+startDate.get(Calendar.YEAR);
	        json.put("date",dates);
	        json.put("dateList",list);
	        String output = json.toString();
		return json;
	    }
		return null;
	}

    @RequestMapping(value = "/allOpenChecks.json", method = RequestMethod.GET)
	public @ResponseBody List<Check> getAllOpenChecksJSON(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String restaurantId) {
		Integer restaurantID = Integer.parseInt(restaurantId);
		return checkService.getAllOpenChecks(restaurantID);
	}

    @RequestMapping(value = "/allDeliveryBoy.json", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody @JsonRawValue Map<String,List<Map>> getAllDeliveryBoy(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required=false) String restaurantId) throws JSONException {
		Integer roleId=0;
		List<User> deliveryBoyList = null;
		//Integer restaurantId = Integer.parseInt(request.getParameter("restaurantId"));
		Integer fulfillmentCenterId;
		String fulId = restaurantId;
		if(fulId != null)
			fulfillmentCenterId = Integer.parseInt(fulId);
		else
			fulfillmentCenterId = (Integer)request.getSession().getAttribute("restaurantId");
		Map<String,List<Map>> json = new HashMap<String, List<Map>>();
		List<Role> roles = userService.getUserRole();
		if(roles.size()>0){
		for(Role roleVal : roles){
			if("deliveryBoy".equalsIgnoreCase(roleVal.getRole())){
				roleId=roleVal.getId();
			}
		}
		deliveryBoyList = userService.listUserByRole(fulfillmentCenterId,roleId);
		/*Collections.sort(deliveryBoyList, new Comparator<User>() {
		    public int compare(User v1,User  v2) {
		        return v1.getUserName().compareTo(v2.getUserName());
		    }
		});*/
		}

        List<Map> kd = new ArrayList<Map>();
		for(User us : deliveryBoyList){
			Map<String,Object> d =  new HashMap<String,Object>();
			d.put("name", us.getFirstName() +" "+us.getLastName());
			d.put("id",us.getUserId().toString());
			d.put("fulfillmentCenterId",us.getKitchenId().toArray());
			kd.add(d);
		}

        json.put("deliveryBoy", kd);
		String data = String.format(json.toString());
		return json;
	}

    @RequestMapping(value = "/allChecksWithOpenOrders.json", method = RequestMethod.GET)
	public @ResponseBody List<Check> getAllChecksWithOpenOrdersJSON(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String restaurantId) {
		Integer restaurantID = Integer.parseInt(restaurantId);
		List<Integer> checkIds = orderService.getAllOpenOrderCheckIds(restaurantID);
		logger.info("retriving all check with open Orders from database");
		return checkService.getAllChecks(checkIds);
	}

    @RequestMapping(value = "/getOrderbyOrderId.json", method = RequestMethod.GET)
	public @ResponseBody Order getOrderbyOrderId(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String orderId) {
		Integer orderID = Integer.parseInt(orderId);

        return orderService.getOrder(orderID);
	}
	
	@RequestMapping(value = "/getReceipt", method = RequestMethod.GET)
	@Deprecated
	public String getReceipt(Map<String, Object> map, HttpServletRequest request, 
			@RequestParam String tableId,@RequestParam String custId, @RequestParam String restaurantId) {
		//String tableIdStr = request.getParameter("tableId");
		//String custIdStr = request.getParameter("custId");
		Integer restaurantID = Integer.parseInt(restaurantId);
		CheckResponse checkResponse = getCheckResponse(tableId, custId, restaurantID);
		map.put("checkResp", checkResponse);
		return "receipt";
	}
	
	@RequestMapping(value = "/setOrderPaymentType")
	@ApiOperation(value="This API requires user login session, because we're getting some data from session")
	public @ResponseBody Map<String, Object> setOrderPaymentType(HttpServletRequest request, HttpServletResponse response
			,@RequestParam String orderId,@RequestParam(required=false) String tillId,
			@RequestParam(required=false) String remarks,@RequestParam String paymentType) throws UnsupportedEncodingException, MessagingException {
		Map<String,Object> map = new TreeMap<String, Object>();
		REQUEST = request;
        Integer orderID = Integer.parseInt(orderId);
		//String statusStr = request.getParameter("status");
		//String tillId = request.getParameter("tillId");
		//zString remarks = request.getParameter("remarks");
		String role = (String)request.getSession().getAttribute("role");
        String userName = (String)request.getSession().getAttribute("username");
		Order order = orderService.getOrder(orderID);
		Check check = checkService.getCheck(order.getCheckId());
		Restaurant rest =  restaurantServices.getRestaurant(check.getRestaurantId());
		List<PaymentType> listPy = restaurantServices.listPaymentTypeByOrgId(rest.getParentRestaurantId());
		//String paymentType = request.getParameter("paymentType");
		 boolean sendEmail=true;
		 boolean isPartial=false;
		 if(request.getAttribute("sendEmail")!=null){
			 sendEmail= (boolean)request.getAttribute("sendEmail");
		 }
		if(request.getAttribute("partialPaymentType")!=null){
			 paymentType = (String) request.getAttribute("partialPaymentType");
			 tillId = (String) request.getAttribute("tillId");
			 isPartial=true;
		}
		boolean isValid=false;
		PaymentType lastOrderPyObj=null;
		String lastOrderPaymentType=null;
		if(paymentType!=null && !(paymentType.equalsIgnoreCase("undefined"))){
			for(PaymentType py : listPy){
				if(paymentType.equalsIgnoreCase(py.getName())){
					isValid=true;
				}
			}
		}else {
			map.put("status","error");
			map.put("message","Invalid Payment Type");
			return map;
		}

        if(order.getPaymentStatus().equalsIgnoreCase(paymentType)){
			map.put("status","error");
			map.put("message","It's already marked as : "+paymentType);
			return map;
		}

		logger.info(order.getPaymentStatus() + " and updated payment type:" + paymentType + ": order id :" + orderId + " System User Role : " + role + " username :" + userName);
		if(isValid){
			ResponseDTO responseDTO =  null;
		if(!StringUtility.isNullOrEmpty(paymentType)){
			Order or = orderService.getOrder(orderID);
			or.setPaymentStatus(paymentType);
			lastOrderPaymentType = order.getPaymentStatus();
			if(!(order.getPaymentStatus().toString().equalsIgnoreCase("CUSTOMER CREDIT")) && paymentType.equalsIgnoreCase("CUSTOMER CREDIT")){
				Customer customer = customerService.getCustomer(check.getCustomerId());
				if(customer.getCredit()==null){
					if(check.getCreditBalance()<0){
                        responseDTO = openDefaultCreditAccount(rest.getParentRestaurantId(), check, TransactionCategory.DEBIT, (float) Math.abs(check.getRoundOffTotal() + check.getCreditBalance()));
                    } else {
                        responseDTO  = openDefaultCreditAccount(rest.getParentRestaurantId(),check,TransactionCategory.DEBIT,null);
					}
				}else{
					if(check.getCreditBalance()<0){
							responseDTO = creditDebitAmountToAccount(customer,check,rest.getParentRestaurantId(),TransactionCategory.DEBIT,(float)Math.abs(check.getRoundOffTotal()+check.getCreditBalance()),false);
						}else{
							responseDTO = creditDebitAmountToAccount(customer,check,rest.getParentRestaurantId(),TransactionCategory.DEBIT,(float)(check.getRoundOffTotal()),false);
				}
				}
			}
			else if(order.getPaymentStatus().toString().equalsIgnoreCase("CUSTOMER CREDIT") && (!paymentType.equalsIgnoreCase("CUSTOMER CREDIT"))){
				   Customer customer = customerService.getCustomer(check.getCustomerId());
				   responseDTO  = creditDebitAmountToAccount(customer,check,rest.getParentRestaurantId(),TransactionCategory.CREDIT,(float)check.getRoundOffTotal(),true);
			}
			if(responseDTO!=null){
				if("ERROR".equalsIgnoreCase(responseDTO.result)){
					System.out.println("error");
					map.put("status","error");
					map.put("message",responseDTO.message);
					return map;
				}
			}
			orderService.addOrder(or);
			if(!paymentType.equalsIgnoreCase(order.getPaymentStatus())){
				Customer cust  = customerService.getCustomer(check.getCustomerId());
				String lastPaymentStatus = order.getPaymentStatus();
                order.setPaymentStatus(paymentType);
                try {
                	
	                	if(check.getCreditBalance()<0){
	                		updateCash(TillTransaction.UPDATE.toString(),(float)check.getRoundOffTotal()+check.getCreditBalance(), check, request,false,lastPaymentStatus, "Edit order transaction checkId="+check.getCheckId(),tillId,paymentType);
	                	}else{
	                		updateCash(TillTransaction.UPDATE.toString(),(float)check.getRoundOffTotal(), check, request,false,lastPaymentStatus, "Edit order transaction checkId="+check.getCheckId(),tillId,paymentType);
	                	}
                	
					List<PaymentType> paymentList = restaurantServices.listPaymentTypeByOrgId(rest.getParentRestaurantId());
					for(PaymentType pt : paymentList){
						if(pt.getName().equalsIgnoreCase(lastPaymentStatus)){
							lastOrderPyObj = pt;
						}
					}
					if(check.getCreditBalance()!=0){
		                for(PaymentType pt : paymentList){
							if(pt.getName().equalsIgnoreCase(paymentType)){
								if(pt.getType().equalsIgnoreCase(BasePaymentType.CASH.toString())){
									updateCash(TillTransaction.UPDATE.toString(), (float) check.getCreditBalance(), check, request,true,lastPaymentStatus, "Edit order transaction checkId=" + check.getCheckId(), tillId,paymentType);
								}
								else if(pt.getType().equalsIgnoreCase(BasePaymentType.CREDIT.toString()) && !("CUSTOMER CREDIT".equalsIgnoreCase(paymentType))){
									updateCash(TillTransaction.UPDATE.toString(), (float) check.getCreditBalance(), check, request,true,lastPaymentStatus, "Edit order transaction checkId=" + check.getCheckId(), tillId,paymentType);
								}else if("CUSTOMER CREDIT".equalsIgnoreCase(paymentType)){
									if(lastOrderPyObj!=null){
										//if(lastOrderPyObj.getType().equalsIgnoreCase(BasePaymentType.CREDIT.toString())){
											updateCash(TillTransaction.CANCEL.toString(), (float) check.getCreditBalance(), check, request,true,lastPaymentStatus, " Order with orderId=" + order.getOrderId(), tillId, paymentType);
										
										}
									if(check.getCreditBalance()<0){
										customerCreditService.updateBillRecoveryTransaction("SUCCESS",cust.getCustomerId(), check.getCreditBalance(), "CREDIT", "Setting Status Failed");
									}else{
										customerCreditService.updateBillRecoveryTransaction("FAILED",cust.getCustomerId(), check.getCreditBalance(), "CREDIT", "Setting Status Failed");
									}
									check.setCreditBalance(0);
								}
							}
							}
							}
                }catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					map.put("status","error");
					map.put("message","Exception arrived while creating transaction. Hence operation failed!.");
					//restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
					logger.info("Exception mail sent");
					return map;
				}
				if(remarks==null){
                    remarks = order.getStatus().toString() + " Payment type of this Invoice has been changed to " + paymentType + ": edited by :" + request.getSession().getAttribute("username");
                }
				check.setEditOrderRemark(remarks);
				checkService.addCheck(check);
				orderService.addOrder(order);
                try{
                	if(sendEmail){
                		emailCheckFromServer(request,check.getCheckId(),rest.getAlertMail(),"saladdaysemailbill","Payment type of this Invoice has been changed to "+paymentType+" from "+lastPaymentStatus+" Remarks: "+remarks,"cs",null,null,0);
						emailCheckFromServer(request,check.getCheckId(),cust.getEmail(),"saladdaysemailbill","Salad Days : Edited Payment Mode",null,lastPaymentStatus,"PAYMENT_TYPE_CHANGED",0);
                	}
                }catch(Exception e){
					e.printStackTrace();
					//restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
					logger.info("Exception mail sent");
				}
				map.put("status","success");
			}else {
				map.put("status","error");
				map.put("message","Payment type  already exist");
			}
		}
	}else {
		map.put("status","error");
		map.put("message","Payment Type does not exist");
		return map;
	}
		return map;

    }
	
	public ResponseDTO validateRefundForCustomerCredit(HttpServletRequest request,Status status, Order order, Customer customer,Check check, Restaurant rest, String refund){
		ResponseDTO responseDTO = null;
		REQUEST=request;
		List<com.cookedspecially.domain.OrderSource> orderSource = new  ArrayList<>();
		orderSource  =  restaurantServices.listOrderSourcesByOrgId(rest.getParentRestaurantId());
		for(com.cookedspecially.domain.OrderSource os : orderSource  ){
			if(os.getName().equalsIgnoreCase(check.getOrderSource()) && os.getStatus().equalsIgnoreCase("Active")){
				logger.info("We're avoiding opening credit account for orderSource : "+os.getName()+" and  check Id/invoiceId : "+check.getCheckId()+"/"+check.getInvoiceId());
				logger.info("Customer :"+customer.getPhone() +" & " + customer.getCustomerId());
				return responseDTO;
			}
		}
		
		 if("CUSTOMER CREDIT".equalsIgnoreCase(order.getPaymentStatus()) && status==Status.CANCELLED){
			 float amount  = (float)check.getRoundOffTotal();
				if(customer.getCredit()!=null && customer.getCredit().getStatus()==CustomerCreditAccountStatus.ACTIVE){
					responseDTO = creditDebitAmountToAccount(customer,check,rest.getParentRestaurantId(),TransactionCategory.CREDIT,amount,false);
				}
				if("ERROR".equalsIgnoreCase(responseDTO.result)){
					return responseDTO;
				}
				try {
						CreditTransactions ct = customerCreditService.getLastPendingTransaction(customer.getCustomerId());
						if(ct!=null){
                            if (ct.getStatus() == CreditTransactionStatus.PENDING) {
                                Check ck = checkService.getCheckByInvoiceId(ct.getInvoiceId()).get(0);
                                try{
								customerCreditService.updateBillRecoveryTransaction("FAILED",customer.getCustomerId(), ck.getCreditBalance(),"CREDIT", "Setting status failed of existing pending transaction");
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
								logger.info("Exception mail sent");
							}
							ck.setCreditBalance(0);
							ck.setStatus(com.cookedspecially.enums.check.Status.Cancel);
							checkService.addCheck(ck);
						}
					}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						try {
							restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
						} catch (UnsupportedEncodingException
								| MessagingException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						logger.info("Exception mail sent");
					}
				}
		 else if("PG".equalsIgnoreCase(order.getPaymentStatus()) && status==Status.CANCELLED){
		       System.out.println("here 2");
			 		if("CREDIT".equalsIgnoreCase(refund)){
				 			if(customer.getCredit()==null){
				 				responseDTO = openDefaultCreditAccount(rest.getParentRestaurantId(),check,TransactionCategory.CREDIT,null);
				 			}else if(customer.getCredit()!=null && customer.getCredit().getStatus()==CustomerCreditAccountStatus.ACTIVE){
				 				responseDTO = creditDebitAmountToAccount(customer,check,rest.getParentRestaurantId(),TransactionCategory.CREDIT,(float)check.getRoundOffTotal(),true);
				 			}
				 			if("ERROR".equalsIgnoreCase(responseDTO.result)){
								return responseDTO;
							}
			 		}else if("CASH".equalsIgnoreCase(refund)){
			 			
			 		}
		 			CreditTransactions ct = customerCreditService.getLastPendingTransaction(customer.getCustomerId());
					if(ct!=null){
						if(ct.getStatus()==CreditTransactionStatus.PENDING){
                            Check ck = checkService.getCheckByInvoiceId(ct.getInvoiceId()).get(0);

						try {
							customerCreditService.updateBillRecoveryTransaction("SUCCESS",customer.getCustomerId(), ck.getCreditBalance(),"CREDIT", "Setting status success of existing pending transaction");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							try {
								restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
							} catch (UnsupportedEncodingException
									| MessagingException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							logger.info("Exception mail sent");
						}
						ck.setCreditBalance(0);
						ck.setStatus(com.cookedspecially.enums.check.Status.Cancel);
						checkService.addCheck(ck);
					}
				}
		 }else if(status==Status.CANCELLED && check.getStatus()==com.cookedspecially.enums.check.Status.Paid) {
             
		       System.out.println("here3");
			 if("CREDIT".equalsIgnoreCase(refund)){
                 if(customer.getCredit()==null){
                	 	responseDTO = openDefaultCreditAccount(rest.getParentRestaurantId(),check,TransactionCategory.CREDIT,null);
		 			}else if(customer.getCredit()!=null && customer.getCredit().getStatus()==CustomerCreditAccountStatus.ACTIVE){
		 				responseDTO = creditDebitAmountToAccount(customer,check,rest.getParentRestaurantId(),TransactionCategory.CREDIT,(float)check.getRoundOffTotal(),true);
		 			}
                 if("ERROR".equalsIgnoreCase(responseDTO.result)){
 					return responseDTO;
 				}
	 		}else if("CASH".equalsIgnoreCase(refund)){
	 			
	 		}
			CreditTransactions ct = customerCreditService.getLastPendingTransaction(customer.getCustomerId());
			if(ct!=null){
				if(ct.getStatus()==CreditTransactionStatus.PENDING){
                    Check ck = checkService.getCheckByInvoiceId(ct.getInvoiceId()).get(0);
                    try {
					customerCreditService.updateBillRecoveryTransaction("SUCCESS",customer.getCustomerId(), ck.getCreditBalance(),"CREDIT", "Setting status success of existing pending transaction");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					try {
						restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
					} catch (UnsupportedEncodingException | MessagingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					logger.info("Exception mail sent");
				}
				ck.setCreditBalance(0);
				ck.setStatus(com.cookedspecially.enums.check.Status.Cancel);
				checkService.addCheck(ck);
			}
		}
		}
		 return responseDTO;
	}

    public void creditCancellationAndEmail(HttpServletRequest request, Check check,Status status, Status oldStatus,Restaurant rest,String refund, String remarks){
    	
    	if(status==Status.CANCELLED){
    		Customer cust  = customerService.getCustomer(check.getCustomerId());
    		if(check.getZomatoOrderId()!=null){
    			/*Trying to reject zomato order. */
    			try{
    				zomatoService.zomatoOrderReject(check,request);
    			}catch(Exception e){
    				logger.info("Can't cancel zomato order");
    			}
    		}
		if(oldStatus==Status.OUTDELIVERY ){
            //String remarks = request.getParameter("remarks");
            if(cust!=null)  
			try{
				emailCheckFromServer(request,check.getCheckId(),cust.getEmail(),"saladdaysCancelEmail","Salad Days : Order No. "+check.getOrderId()+" has been cancelled.",null,refund,"CANCELLED",0);
			}catch(Exception e){
				logger.info("Email exception for checkID/InvoiceId :"+check.getCheckId()+"/"+check.getInvoiceId());
				try {
					restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
				} catch (UnsupportedEncodingException | MessagingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				logger.info("Exception mail sent");
			}
		if(remarks==null){
				remarks="No remarks";
			}
			check.setEditOrderRemark(remarks);
			try{
			emailCheckFromServer(request,check.getCheckId(),rest.getAlertMail(),"saladdaysemailbill","This invoice has been Cancelled. Remarks: "+remarks,"cs",refund,"CANCELLED",0);
			}catch(Exception e){
				logger.info("Email exception for checkID/InvoiceId :"+check.getCheckId()+"/"+check.getInvoiceId());
				try {
					restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
				} catch (UnsupportedEncodingException | MessagingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				logger.info("Exception mail sent");
			}
		}else if (oldStatus==Status.DELIVERED ){
			if(cust!=null)
				try{
					emailCheckFromServer(request,check.getCheckId(),cust.getEmail(),"saladdaysCancelEmail","Salad Days : Order No. "+check.getOrderId()+" has been cancelled.",null,refund,"CANCELLED",0);
					emailCheckFromServer(request,check.getCheckId(),rest.getAlertMail(),"saladdaysemailbill","This invoice has been Cancelled after marking it DELIVERED .","cs",refund,"CANCELLED",0);
				}catch(Exception e){
					logger.info("Email exception for checkID/InvoiceId :"+check.getCheckId()+"/"+check.getInvoiceId());
					try {
						restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
					} catch (UnsupportedEncodingException | MessagingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					logger.info("Exception mail sent");
				}
		}else {
			if(cust!=null && (!check.getOrders().get(0).getPaymentStatus().equalsIgnoreCase("PG_PENDING")) && (!check.getOrders().get(0).getPaymentStatus().equalsIgnoreCase("WALLET_PENDING"))&&(!check.getOrders().get(0).getPaymentStatus().equalsIgnoreCase("PAYTM_PENDING")) && (!check.getOrders().get(0).getPaymentStatus().equalsIgnoreCase("PENDING"))  ){
				try{
				emailCheckFromServer(request,check.getCheckId(),cust.getEmail(),"saladdaysCancelEmail","Salad Days : Order No. "+check.getOrderId()+" has been cancelled.",null,refund,"CANCELLED",0);
			}catch(Exception e){
				logger.info("Email exception for checkID/InvoiceId :"+check.getCheckId()+"/"+check.getInvoiceId());
				try {
					restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
				} catch (UnsupportedEncodingException | MessagingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				logger.info("Exception mail sent");
			}
			}
		}
		if(check.getCreditBalance()!=0){
			try{
				customerCreditService.updateBillRecoveryTransaction("FAILED",cust.getCustomerId(), check.getCreditBalance(),"CREDIT", "Setting Status Failed");
			}catch(Exception e){
				try {
					restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
				} catch (UnsupportedEncodingException | MessagingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				logger.info("Exception mail sent");
		}
		}
	}
	}

    public ResponseDTO updateTillTransactionForPartialPayment(Check check,Order order,String paymentType, String updateToPaymentType, HttpServletRequest request, String tillId, Restaurant rest,float orderAmountRecieved) throws Exception{
    	ResponseDTO responseDTO =null;
    	
    		 List<PaymentType> paymentList = restaurantServices.listPaymentTypeByOrgId(rest.getParentRestaurantId());
    		   for(PaymentType pt : paymentList){
					if(pt.getName().equalsIgnoreCase(paymentType)){
						if(pt.getType().equalsIgnoreCase(BasePaymentType.CASH.toString())){
							responseDTO = updateCash(TillTransaction.NEW.toString(), (float) orderAmountRecieved, check, request,true,paymentType, "New* Transaction CREDIT_BILL_IN_CASH for OrderId=" + order.getOrderId(), tillId, updateToPaymentType);
							if("SUCCESS".equalsIgnoreCase(responseDTO.result)){
								responseDTO = updateCash(TillTransaction.SUCCESS.toString(), (float) orderAmountRecieved, check, request,true,paymentType, "Success* Transaction CREDIT_BILL_IN_CASH for OrderId=" + order.getOrderId(), tillId, updateToPaymentType);
							}
							}
						else if(pt.getType().equalsIgnoreCase(BasePaymentType.CREDIT.toString())&& !("CUSTOMER CREDIT".equalsIgnoreCase(paymentType))){
							responseDTO = updateCash(TillTransaction.NEW.toString(), (float) orderAmountRecieved, check, request,true,paymentType, "New* Transaction CREDIT_BILL_IN_CREDIT for OrderId=" + order.getOrderId(), tillId, updateToPaymentType);
							if("SUCCESS".equalsIgnoreCase(responseDTO.result)){
								responseDTO = updateCash(TillTransaction.SUCCESS.toString(), (float) orderAmountRecieved, check, request,true,paymentType, "Success* Transaction CREDIT_BILL_IN_CREDIT for OrderId=" + order.getOrderId(), tillId, updateToPaymentType);
							}
						}
					}
					}
    		
    	return responseDTO;
    }
    
    ResponseDTO vaidateRefundForPartialPayment(HttpServletRequest request,Status status,Order order,Customer cust,Check check, Restaurant rest,String refund, float orderAmountRecieved){
    	REQUEST = request;
    	ResponseDTO responseDTO = null;
    	Customer customer  =  customerService.getCustomer(cust.getCustomerId());
		if(customer!=null) {
                if(customer.getCredit()==null){
               	 	     responseDTO = openDefaultCreditAccount(rest.getParentRestaurantId(),check,TransactionCategory.CREDIT,(float)orderAmountRecieved);
		 			}else if(customer.getCredit()!=null && customer.getCredit().getStatus()==CustomerCreditAccountStatus.ACTIVE){
		 				 responseDTO = creditDebitAmountToAccount(customer,check,rest.getParentRestaurantId(),TransactionCategory.CREDIT,(float)orderAmountRecieved,true);
		 		}
                if("ERROR".equalsIgnoreCase(responseDTO.result)){
					return responseDTO;
				}
	 		
		}
		 return responseDTO;
    }
    
    @RequestMapping(value = "/setOrderStatus",method = RequestMethod.GET)
    @ApiOperation(value="This API requires user login session, because we're getting some data from session")
	public @ResponseBody  Map<String, Object>  setOrderStatus(HttpServletRequest request, 
			HttpServletResponse response, @RequestParam String orderId,@RequestParam(required=false) String status,
			@RequestParam(required=false) String tillId,
			@ApiParam(value="This parameter required when"
					+ " you cancle the order and want to refund the amount, refund value will be ( CREDIT or CASH ). "
					+ "CASH if you want to return cash or CREDIT in case you want to add that amount to his CUSTOMER CREDIT."
					+ "right now we are only doing CREDIT internally.") @RequestParam(required=false) String refund,
			@ApiParam(value="If you want to mark PENDING order paid, send 'Paid' value as paramater.")@RequestParam(required=false) String paidStatus,
			@ApiParam(value="Money collected on cash collection/ money sent with delivery boy")@RequestParam(required=false) String money,
			@ApiParam(value="Payment type of order")@RequestParam(required=false) String paymentType,
			@ApiParam(value="Optionl, in case you want to send reason, why order has been edited")@RequestParam(required=false) String remarks,
			@ApiParam(value="DeliveryBoy name who took the order for delivery")@RequestParam(required=false) String deliveryBoy) throws UnsupportedEncodingException, MessagingException {
			
    	Map<String,Object> map = new TreeMap<String, Object>();
		Integer orderID = Integer.parseInt(orderId);
		//String statusStr = status;
		//String paidStatus = request.getParameter("paidStatus");
		//String refund =  request.getParameter("refund");
		//String tillId = request.getParameter("tillId");
		String role = (String)request.getSession().getAttribute("role");
        String userName = (String)request.getSession().getAttribute("username");
		Float moneyIn = Float.parseFloat(money);
		status = status.toUpperCase();
		Status orderStatus = Status.valueOf(Status.class, status);
		Order order = orderService.getOrder(orderID);
		Status oldStatus =  order.getStatus();
		Check check = checkService.getCheck(order.getCheckId());
		Restaurant rest =  restaurantServices.getRestaurant(check.getRestaurantId());
		logger.info("1>Setting order Status "+status+" for orderId : "+orderID);
		//String paymentType = request.getParameter("paymentType");
		boolean isPartialPayment=false;
		logger.info("Previous order status : "+oldStatus+"   : current status requested "+status+" : "+paymentType+": order id :"+orderId+" System User Role : "+role+" username :"+userName);
		String updateToPaymentType = "";
		if(order.getPaymentStatus()!=null && orderStatus  == Status.OUTDELIVERY){
			String [] pyType = order.getPaymentStatus().split("_");
			if(pyType.length>0){
				if(order.getPaymentStatus().equalsIgnoreCase(pyType[0]+"_PENDING")|| "WALLET_PENDING".equalsIgnoreCase(order.getPaymentStatus())){
					map.put("Error","You can't dispatch order with payment type: "+order.getPaymentStatus() +". Please resolve its status");
					logger.info("returning order Id"+order.getOrderId());
					return map;
				}
			}
		}
		 if(oldStatus==Status.CANCELLED && order.getMoneyOut()==0){
				map.put("Error","Order is already: "+Status.CANCELLED.toString());
				logger.info("returning order Id"+order.getOrderId());
				return map;
		}else if(orderStatus ==oldStatus){
			map.put("Error","Order is already marked as: "+status.toString()+". Please refresh you screen");
			logger.info("returning order Id"+order.getOrderId());
			return map;
		}
		else if(oldStatus==Status.OUTDELIVERY && (orderStatus ==Status.READY || orderStatus ==Status.PENDING || orderStatus ==Status.NEW)){
			map.put("Error","Order is already marked as: "+oldStatus.toString()+". Please refresh you screen");
			logger.info(status.toString()+" returning order Id"+order.getOrderId());
			return map;
		}

        if(oldStatus==Status.CANCELLED && orderStatus !=Status.DELIVERED){
			 map.put("Error","Order is already marked as: "+oldStatus.toString()+".");
			logger.info(status.toString()+" returning order Id"+order.getOrderId());
			return map;
		 }

        order.setStatus(orderStatus);
		 orderService.addOrder(order);
		 
        Customer customer = customerService.getCustomer(check.getCustomerId());
        
        /**
 			Partial Payment on construction
         */
        if(moneyIn>0){
	        if(orderStatus.equals(Status.DELIVERED) && moneyIn<(order.getMoneyOut()+check.getRoundOffTotal()+check.getCreditBalance())){
	        	try {
	        		if(moneyIn>Math.abs(check.getRoundOffTotal()+check.getCreditBalance()+order.getMoneyOut())){
	        			 map.put("status","error");
	    				 map.put("message","Partial amount should not be greater then order amount.");
	    				 return map;
	        		}
	        		ResponseDTO responseDTO=null;
	        		float moneyOut= order.getMoneyOut();
	        		float orderAmountRecieved = Math.abs(moneyIn-order.getMoneyOut());
	        		if(orderAmountRecieved>0){
	        		logger.info("entered into partial payment: amount "+orderAmountRecieved);
	        		request.setAttribute("tillId",tillId);
	        		request.setAttribute("sendEmail",false);
	        		request.setAttribute("partialPaymentType","CUSTOMER CREDIT");
	        		Map<String, Object> mapResult = setOrderPaymentType(request, response,order.getOrderId().toString(),tillId,"Partial payment process","CUSTOMER CREDIT");
	        		if("success".equalsIgnoreCase((String)mapResult.get("status"))){
	        			responseDTO =  updateTillTransactionForPartialPayment(check, order,paymentType, updateToPaymentType, request, tillId, rest,orderAmountRecieved);
	        			responseDTO =  vaidateRefundForPartialPayment(request,orderStatus , order, customer, check, rest, refund,orderAmountRecieved);
	        			paymentType="CUSTOMER CREDIT";
	        			isPartialPayment=true;
	        			if(check.getCreditBalance()==0){
	        				//check.setCreditBalance(-orderAmountRecieved);
	        			}else if(check.getCreditBalance()>0){
	        				//check.setCreditBalance(Math.abs(check.getCreditBalance()+(-orderAmountRecieved)));
	        			}else if(check.getCreditBalance()<0){
	        				//check.setCreditBalance(check.getCreditBalance()-orderAmountRecieved); 
	        			}
	        			order.setPaymentStatus(paymentType);
	        		}
	        	}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					//restaurantServices.emailException(ExceptionUtils.getStackTrace(e1),request);
					logger.info("Exception mail sent");
				}
	        	
	        }
        }
        /** Ends here */
        
        /** Validating and initiating refund for order cancellation */
		 ResponseDTO responseDTO = validateRefundForCustomerCredit(request,orderStatus , order, customer, check, rest, refund);
		 /**  Validating and initiating refund  ends here*/
		 if(responseDTO!=null){
			 if("ERROR".equalsIgnoreCase(responseDTO.result)){
				 map.put("status","error");
				 map.put("message",responseDTO.message);
				 return map;
		 }
		 }
        try{
            if(!StringUtility.isNullOrEmpty(paymentType)){
		/** updating payment type and till transaction as well */
            if(!paymentType.equalsIgnoreCase(order.getPaymentStatus())){
				String lastPaymentStatus = order.getPaymentStatus();
                order.setPaymentStatus(paymentType);
                updateCash(TillTransaction.UPDATE.toString(),(float)check.getRoundOffTotal(), check, request,false,lastPaymentStatus, "Edit order transaction checkId="+check.getCheckId(),tillId,paymentType);
                List<PaymentType> paymentList = restaurantServices.listPaymentTypeByOrgId(rest.getParentRestaurantId());
               if(check.getCreditBalance()>0){
                for(PaymentType pt : paymentList){
					if(pt.getName().equalsIgnoreCase(paymentType)){
						if(pt.getType().equalsIgnoreCase(BasePaymentType.CASH.toString())){
							updateCash(TillTransaction.UPDATE.toString(), (float) check.getCreditBalance(), check, request,true,lastPaymentStatus, "Edit order transaction checkId=" + check.getCheckId(), tillId, paymentType);
						}else if(pt.getType().equalsIgnoreCase(BasePaymentType.CREDIT.toString())&& !("CUSTOMER CREDIT".equalsIgnoreCase(paymentType))){
							updateCash(TillTransaction.UPDATE.toString(), (float) check.getCreditBalance(), check, request,true,lastPaymentStatus, "Edit order transaction checkId=" + check.getCheckId(), tillId, paymentType);
						}else if("CUSTOMER CREDIT".equalsIgnoreCase(paymentType)){
							updateCash(TillTransaction.CANCEL.toString(), (float) check.getCreditBalance(), check,request,true,lastPaymentStatus, " Order with orderId=" + order.getOrderId(), tillId, paymentType);
							checkService.addCheck(check);
							Customer cust  = customerService.getCustomer(check.getCustomerId());
							customerCreditService.updateBillRecoveryTransaction("FAILED",cust.getCustomerId(), check.getCreditBalance(), "CREDIT", "Setting Status Failed");
							check.setCreditBalance(0);
				        }
					}
				}
               }
              
                try{
					emailCheckFromServer(request,check.getCheckId(),rest.getAlertMail(),"saladdaysemailbill","Payment type of this Invoice has been changed to "+paymentType,"cs",paymentType,"PAYMENT_TYPE_CHANGED",0);
				}catch(Exception e){
					logger.info("Email exception for checkID/InvoiceId :"+check.getCheckId()+"/"+check.getInvoiceId());
					restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
					logger.info("Exception mail sent");
				}
                //String remarks = request.getParameter("remarks");
                if(remarks!=null){
					check.setEditOrderRemark(remarks);
				}
				if(rest.isDeliveryManagerEdit()){
					check.setAllowEdit(true);
				}
			}else {
                order.setPaymentStatus(paymentType);
            }
		/** updating payment type and till transaction ends here */
		}
		if(role!=null){
            creditCancellationAndEmail(request, check, orderStatus , oldStatus, rest,refund,remarks);
        }
		if(paidStatus !=null){
			markOrderPG(paidStatus, check, order);
		}
            /** Till and order status management */
			  map = tillAndStatusManagementForOrders(request, status, check, order, rest, paymentType, orderStatus , oldStatus, moneyIn, tillId, updateToPaymentType, isPartialPayment,deliveryBoy);
		if(map!=null){
			return map;
		}
		/** Till and order status management end */

            if(status.equals("EDITMONEYOUT") || status.equals("EDITMONEYIN")) {
			if(status.equals("EDITMONEYOUT"))
			order.setModifiedTime(new Date());
			orderService.addOrder(order);
			return map;
		}
		else {
			if(oldStatus!=Status.CANCELLED){
				order.setStatus(orderStatus);
			}
			order.setModifiedTime(new Date());
			orderService.addOrder(order);
			return map;
	}
	} catch (Exception e) {
			map.put("Error", e.getMessage());
			restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
			logger.info("Exception mail sent");
			return map;
		}
	}

    public void markOrderPG(String paidStatus, Check check,Order order){
		if("Paid".equalsIgnoreCase(paidStatus)){
			logger.info("Marking order paid  and setting PG:"+ order.getOrderId());
			check.setStatus(com.cookedspecially.enums.check.Status.Paid);
			check.setPayment(PaymentMode.PG);
			order.setPaymentStatus("PG");
			checkService.addCheck(check);
		}
	}

    public Map<String,Object> tillAndStatusManagementForOrders(HttpServletRequest request, String statusStr,Check check,
			Order order, Restaurant rest, String paymentType,Status status,Status oldStatus,float money,String tillId,
			String updateToPaymentType,boolean isPartialPayment, String deliveryBoy) throws Exception{
		Map<String,Object> map = new TreeMap<String, Object>();
		if(oldStatus==Status.CANCELLED){
			status = Status.CANCELLED;
		}
		if(statusStr.equalsIgnoreCase("OUTDELIVERY") || statusStr.equalsIgnoreCase("EDITMONEYOUT")){
			//String deliveryBoy = request.getParameter("deliveryBoy");
			/*Zomato api calling for status update*/
			/*if(check.getZomatoOrderId()!=null){
				try{
					zomatoService.zomatoOrderOutForDelivery(check,request);
				}catch(Exception e){
					logger.info("Zomto update order out status failed:  exception: "+e.getCause());
				}
			}*/
			
			logger.info("2>Setting status "+status+" for payment Type "+paymentType+" for orderId :"+order.getOrderId());
			List<PaymentType> paymentList = restaurantServices.listPaymentTypeByOrgId(rest.getParentRestaurantId());
					for(PaymentType pt : paymentList){
					if(pt.getName().equalsIgnoreCase(order.getPaymentStatus())){
						if(pt.getType().equalsIgnoreCase(BasePaymentType.CASH.toString())){
							if(money>0){
								ResponseDTO resp = updateCash(TillTransaction.NEW.toString(), money, check, request,false, CustomPaymentType.TRANSACTION_CASH.toString(), "Transaction Cash Out To Address Order orderId=" + order.getOrderId(), tillId, updateToPaymentType);
								if(resp.result.equalsIgnoreCase("Error")){
									 map.put("Error",resp.message);
									return map;
								}
							}
                           if(check.getCreditBalance()>0){
                        	   updateCash(TillTransaction.NEW.toString(), (float) check.getRoundOffTotal(), check, request,false, paymentType, "New Order with orderId=" + order.getOrderId(), tillId, updateToPaymentType);
							   updateCash(TillTransaction.NEW.toString(), (float) check.getCreditBalance(), check, request,true,paymentType, "New Order with orderId=" + order.getOrderId(), tillId, updateToPaymentType);
						   }else if(check.getCreditBalance()<0){
                            	 updateCash(TillTransaction.NEW.toString(), (float) Math.abs(check.getRoundOffTotal()+check.getCreditBalance()), check, request,false, paymentType, "New Order with orderId=" + order.getOrderId(), tillId, updateToPaymentType);
                            }else{
                            	 updateCash(TillTransaction.NEW.toString(), (float) check.getRoundOffTotal(), check, request,false, paymentType, "New Order with orderId=" + order.getOrderId(), tillId, updateToPaymentType);
                            }
                            check.setStatus(com.cookedspecially.enums.check.Status.Unpaid);
						} else if(pt.getType().equalsIgnoreCase(BasePaymentType.CREDIT.toString())){
                           if(check.getCreditBalance()>0){
                        	    updateCash(TillTransaction.NEW.toString(), (float) check.getRoundOffTotal(), check, request,false, paymentType, "New Order with orderId=" + order.getOrderId(), tillId, updateToPaymentType);
							   updateCash(TillTransaction.NEW.toString(), (float) check.getCreditBalance(), check, request,true, paymentType, "New Order with orderId=" + order.getOrderId(), tillId, updateToPaymentType);
						   }else if(check.getCreditBalance()<0){
                            	updateCash(TillTransaction.NEW.toString(),(float) Math.abs(check.getRoundOffTotal()+check.getCreditBalance()), check, request,false, paymentType, "New Order with orderId=" + order.getOrderId(), tillId, updateToPaymentType);
                            }else{
                            	  updateCash(TillTransaction.NEW.toString(), (float) check.getRoundOffTotal(), check, request,false, paymentType, "New Order with orderId=" + order.getOrderId(), tillId, updateToPaymentType);
                            }
                          //  check.setStatus(com.cookedspecially.enums.check.Status.Paid);
						} else if(pt.getType().equalsIgnoreCase(BasePaymentType.PREPAID.toString())){
							 if(check.getCreditBalance()>0){
	                        	    updateCash(TillTransaction.NEW.toString(), (float) check.getRoundOffTotal(), check, request,false, paymentType, "New Order with orderId=" + order.getOrderId(), tillId, updateToPaymentType);
								 updateCash(TillTransaction.NEW.toString(), (float) check.getCreditBalance(), check, request,false,paymentType, "New Order with orderId=" + order.getOrderId(), tillId, updateToPaymentType);
							 }else if(check.getCreditBalance()<0){
	                            	updateCash(TillTransaction.NEW.toString(),(float) Math.abs(check.getRoundOffTotal()+check.getCreditBalance()), check, request,false, paymentType, "New Order with orderId=" + order.getOrderId(), tillId, updateToPaymentType);
	                            }else{
	                            	  updateCash(TillTransaction.NEW.toString(), (float) check.getRoundOffTotal(), check, request,false, paymentType, "New Order with orderId=" + order.getOrderId(), tillId, updateToPaymentType);
	                            }
							//check.setStatus(com.cookedspecially.enums.check.Status.Paid);
						}else if(pt.getType().equalsIgnoreCase(BasePaymentType.DNC.toString())){
                            updateCash(TillTransaction.NEW.toString(), (float) check.getRoundOffTotal() + check.getCreditBalance(), check, request,false, paymentType, "New Order with orderId=" + order.getOrderId(), tillId, updateToPaymentType);
                        }
					}
				/*}*/
				checkService.addCheck(check);
			}
			order.setMoneyOut(money);
			logger.info("money out : "+money);
			order.setDeliveryAgent(deliveryBoy);
		}
		else if(statusStr.equalsIgnoreCase("CONFIRMDELIVERY") || statusStr.equalsIgnoreCase("EDITMONEYIN")){
			logger.info("money In : "+money);
			order.setMoneyIn(money);
		}
		if (status == Status.CANCELLED) {
			try {
				restoreStock(check,0);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
				logger.info("Exception mail sent");
			}
			if(oldStatus==Status.OUTDELIVERY){
				TransactionDTO tillList  = cashRegisterService.fetchTransactionsByCheck(check.getRestaurantId(),check.getCheckId());
				List<SaleTransaction> listTill =  tillList.transationList;
				tillId = tillList.tillDetails.tillId;
			   	List<PaymentType> paymentList = restaurantServices.listPaymentTypeByOrgId(rest.getParentRestaurantId());
				for(PaymentType pt : paymentList){
					if(pt.getName().equalsIgnoreCase(order.getPaymentStatus())){
						if(pt.getType().equalsIgnoreCase(BasePaymentType.CASH.toString())){
							if(check.getCreditBalance()>0){
                            	updateCash(TillTransaction.CANCEL.toString(), (float) check.getRoundOffTotal(), check, request,false, order.getPaymentStatus(), order.getPaymentStatus() + " Order with orderId=" + order.getOrderId(), tillId, updateToPaymentType);
								updateCash(TillTransaction.CANCEL.toString(), (float) check.getCreditBalance(), check, request,true,order.getPaymentStatus(), " Order with orderId=" + order.getOrderId(), tillId, updateToPaymentType);
							}
                            else if(check.getCreditBalance()<0){
                            	updateCash(TillTransaction.CANCEL.toString(), (float) Math.abs(check.getRoundOffTotal()+check.getCreditBalance()), check, request,false, order.getPaymentStatus(), order.getPaymentStatus() + " Order with orderId=" + order.getOrderId(), tillId, updateToPaymentType);
                            }else{
                            	updateCash(TillTransaction.CANCEL.toString(), (float) check.getRoundOffTotal(), check, request,false, order.getPaymentStatus(), order.getPaymentStatus() + " Order with orderId=" + order.getOrderId(), tillId, updateToPaymentType);
                            }
						}else if(pt.getType().equalsIgnoreCase(BasePaymentType.PREPAID.toString())){
							if(check.getCreditBalance()>0){
								updateCash(TillTransaction.CANCEL.toString(), (float)check.getRoundOffTotal(), check, request,false, order.getPaymentStatus(), order.getPaymentStatus()+" Order with OrderId="+order.getOrderId(),tillId,updateToPaymentType);
								updateCash(TillTransaction.CANCEL.toString(), (float) check.getCreditBalance(), check, request,true,order.getPaymentStatus(), "New Order with orderId=" + order.getOrderId(), tillId, updateToPaymentType);
							}else if(check.getCreditBalance()<0){
								updateCash(TillTransaction.CANCEL.toString(), (float)Math.abs(check.getRoundOffTotal()+check.getCreditBalance()), check, request,false, order.getPaymentStatus(), order.getPaymentStatus()+" Order with OrderId="+order.getOrderId(),tillId,updateToPaymentType);
							}else{
								updateCash(TillTransaction.CANCEL.toString(), (float)check.getRoundOffTotal(), check, request, false,order.getPaymentStatus(), order.getPaymentStatus()+" Order with OrderId="+order.getOrderId(),tillId,updateToPaymentType);
							}
						}else if(pt.getType().equalsIgnoreCase(BasePaymentType.CREDIT.toString())){
							if(check.getCreditBalance()>0){
								updateCash(TillTransaction.CANCEL.toString(), (float)check.getRoundOffTotal(), check, request,false, order.getPaymentStatus(), order.getPaymentStatus()+" Order with OrderId="+order.getOrderId(),tillId,updateToPaymentType);
								updateCash(TillTransaction.CANCEL.toString(), (float) check.getCreditBalance(), check, request,true, order.getPaymentStatus(), "New Order with orderId=" + order.getOrderId(), tillId, updateToPaymentType);
							}else if(check.getCreditBalance()<0){
								updateCash(TillTransaction.CANCEL.toString(), (float)Math.abs(check.getRoundOffTotal()+check.getCreditBalance()), check, request,false, order.getPaymentStatus(), order.getPaymentStatus()+" Order with OrderId="+order.getOrderId(),tillId,updateToPaymentType);
							}else{
								updateCash(TillTransaction.CANCEL.toString(), (float)check.getRoundOffTotal(), check, request,false, order.getPaymentStatus(), order.getPaymentStatus()+" Order with OrderId="+order.getOrderId(),tillId,updateToPaymentType);
							}
						}else if(pt.getType().equalsIgnoreCase(BasePaymentType.DNC.toString())){
							updateCash(TillTransaction.CANCEL.toString(), (float)check.getRoundOffTotal()+check.getCreditBalance(), check, request,false, order.getPaymentStatus(), order.getPaymentStatus()+" Order with OrderId="+order.getOrderId(),tillId,updateToPaymentType);
						}
					}
				}
			}
			else if(oldStatus==Status.CANCELLED || (status==Status.CANCELLED && oldStatus==Status.DELIVERED)){
				paymentType=order.getPaymentStatus();
				List<PaymentType> paymentList = restaurantServices.listPaymentTypeByOrgId(rest.getParentRestaurantId());
				for(PaymentType pt : paymentList){
					if(pt.getName().equalsIgnoreCase(order.getPaymentStatus())){
						if(pt.getType().equalsIgnoreCase(BasePaymentType.CASH.toString())){
							if(oldStatus==Status.DELIVERED){
									 String userName = (String)request.getSession().getAttribute("username");
									 User user =null;
									 if(userName !=null){
										 user =  userService.getUserByUsername(userName);
									 }
									 try{
										 ResponseDTO respDTO = cashRegisterService.cancelOrderTransaction(order.getCheckId(), paymentType, TillTransactionStatus.SUCCESS,true, user.getUserId());
										 logger.info("Response DTO for cancelled order: "+respDTO.message +""+respDTO.message);
									 }catch(Exception  e){
										e.printStackTrace();
										restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
										logger.info("Exception mail sent");
									}
									//updateCash(TillTransaction.CANCEL.toString(), (float) check.getRoundOffTotal(), check, request, paymentType, "Cancelling after deivered OrderId=" + order.getOrderId(), tillId, updateToPaymentType);
			                        /*}else if(check.getCreditBalance()<0){
			                        //	updateCash(TillTransaction.CANCEL.toString(), (float)Math.abs(check.getRoundOffTotal()+check.getCreditBalance()), check, request, paymentType, "Cancelling after deivered OrderId=" + order.getOrderId(), tillId, updateToPaymentType);
			                        }else{
			                        //	updateCash(TillTransaction.CANCEL.toString(), (float) check.getRoundOffTotal(), check, request, paymentType, "Transaction Cash In To from OrderId=" + order.getOrderId(), tillId, updateToPaymentType);
			                        }*/
							}else{
								if(order.getMoneyOut()>0){
									updateCash(TillTransaction.SUCCESS.toString(), order.getMoneyOut(), check, request,false, CustomPaymentType.TRANSACTION_CASH.toString(), "Transaction Cash In To Address Order orderId=" + order.getOrderId(), tillId, updateToPaymentType);
									order.setMoneyIn(order.getMoneyOut());
								}
							}

                        }else if(pt.getType().equalsIgnoreCase(BasePaymentType.PREPAID.toString())){
                        	if(oldStatus==Status.DELIVERED){
                        		String userName = (String)request.getSession().getAttribute("username");
								 User user =null;
								 if(userName !=null){
									 user =  userService.getUserByUsername(userName);
								 }
								 try{
									 ResponseDTO respDTO = cashRegisterService.cancelOrderTransaction(order.getCheckId(), paymentType, TillTransactionStatus.SUCCESS,true, user.getUserId());
									 logger.info("Response DTO for cancelled order: "+respDTO.message +""+respDTO.message);
								 }catch(Exception  e){
										e.printStackTrace();
										restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
										logger.info("Exception mail sent");
									}
								/*if(check.getCreditBalance()>0){
        							updateCash(TillTransaction.CANCEL.toString(), (float)check.getRoundOffTotal(), check, request, paymentType, paymentType+" Order with OrderId="+order.getOrderId(),tillId,updateToPaymentType);
                                }else if(check.getCreditBalance()<0){
                                	updateCash(TillTransaction.CANCEL.toString(), (float)Math.abs(check.getRoundOffTotal()+check.getCreditBalance()), check, request, paymentType, paymentType+" Order Cancelled with OrderId="+order.getOrderId(),tillId,updateToPaymentType);
                                }else{
                                	updateCash(TillTransaction.CANCEL.toString(), (float)check.getRoundOffTotal(), check, request, paymentType, paymentType+" Order Cancelled with OrderId="+order.getOrderId(),tillId,updateToPaymentType);
                                }*/
                        	}else{
	                        	if(order.getMoneyOut()>0){
									updateCash(TillTransaction.SUCCESS.toString(), order.getMoneyOut(), check, request,false, CustomPaymentType.TRANSACTION_CASH.toString(), "Transaction Cash In To Address Order orderId=" + order.getOrderId(), tillId, updateToPaymentType);
									order.setMoneyIn(order.getMoneyOut());
								}
                        	}
                        	
						}else if(pt.getType().equalsIgnoreCase(BasePaymentType.CREDIT.toString())){
							if(oldStatus==Status.DELIVERED){
								String userName = (String)request.getSession().getAttribute("username");
								 User user =null;
								 if(userName !=null){
									 user =  userService.getUserByUsername(userName);
								 }
								ResponseDTO respDTO =null;
								if("CUSTOMER CREDIT".equalsIgnoreCase(updateToPaymentType)){
									try{
										respDTO= cashRegisterService.cancelOrderTransaction(order.getCheckId(), paymentType, TillTransactionStatus.SUCCESS,false, user.getUserId());
									}catch(Exception  e){
										e.printStackTrace();
										restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
										logger.info("Exception mail sent");
									}
								}else{
									try{
									respDTO= cashRegisterService.cancelOrderTransaction(order.getCheckId(), paymentType, TillTransactionStatus.SUCCESS,true, user.getUserId());
								}catch(Exception  e){
									e.printStackTrace();
									restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
									logger.info("Exception mail sent");
								}
									}
								logger.info("Response DTO for cancelled order: "+respDTO.message +""+respDTO.message);
								/*if(check.getCreditBalance()>0){
									updateCash(TillTransaction.CANCEL.toString(), (float)check.getRoundOffTotal(), check, request, paymentType, paymentType+" Order with OrderId="+order.getOrderId(),tillId,updateToPaymentType);
		                        }else if(check.getCreditBalance()<0){
		                        	updateCash(TillTransaction.CANCEL.toString(), (float)Math.abs(check.getRoundOffTotal()+check.getCreditBalance()), check, request, paymentType, paymentType+" Order with OrderId="+order.getOrderId(),tillId,updateToPaymentType);
		                        }else{
		                        	updateCash(TillTransaction.CANCEL.toString(), (float)check.getRoundOffTotal(), check, request, paymentType, paymentType+" Order with OrderId="+order.getOrderId(),tillId,updateToPaymentType);
		                        }*/
							}else{
								if(order.getMoneyOut()>0){
									updateCash(TillTransaction.SUCCESS.toString(), order.getMoneyOut(), check, request,false, CustomPaymentType.TRANSACTION_CASH.toString(), "Transaction Cash In To Address Order orderId=" + order.getOrderId(), tillId, updateToPaymentType);
									order.setMoneyIn(order.getMoneyOut());
								}
							}
						}else if(pt.getType().equalsIgnoreCase(BasePaymentType.DNC.toString())){
							if(oldStatus==Status.DELIVERED){
								String userName = (String)request.getSession().getAttribute("username");
								 User user =null;
								 if(userName !=null){
									 user =  userService.getUserByUsername(userName);
								 }
								try{
									ResponseDTO respDTO = cashRegisterService.cancelOrderTransaction(order.getCheckId(), updateToPaymentType, TillTransactionStatus.SUCCESS,true, user.getUserId());
									logger.info("Response DTO for cancelled order: "+respDTO.message +""+respDTO.message);
								}catch(Exception  e){
									e.printStackTrace();
									restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
									logger.info("Exception mail sent");
								}
									//updateCash(TillTransaction.CANCEL.toString(), (float)check.getRoundOffTotal()+check.getCreditBalance(), check, request, paymentType, paymentType+" Order with OrderId="+order.getOrderId(),tillId,updateToPaymentType);
							}else{
								if(order.getMoneyOut()>0){
									updateCash(TillTransaction.SUCCESS.toString(), order.getMoneyOut(), check, request,false, CustomPaymentType.TRANSACTION_CASH.toString(), "Transaction Cash In To Address Order orderId=" + order.getOrderId(), tillId, updateToPaymentType);
									order.setMoneyIn(order.getMoneyOut());
								}
							}
						}
					}
				}
			}
			
			check.setStatus(com.cookedspecially.enums.check.Status.Cancel);
			check.setCreditBalance(0);
			checkService.addCheck(check);
            logger.info("0000> Invoice Id " + check.getInvoiceId() + " : Order Id " + order.getOrderId() + " :Status :" + status + " user Name : " + request.getAttribute("username"));
            updateCheckForCancelOrder(order);
		}
		if(status==Status.DELIVERED && check.getTableId() < 1){
			
			/*Zomato api calling for status update*/
			/*if(check.getZomatoOrderId()!=null){
				try{
					logger.info("Marking order Delivered for Zomato");
					zomatoService.zomatOrderDelivered(check,request);
				}catch(Exception e){
					logger.info("Zomto update order delivered status failed:  exception: "+e.getCause());
				}
			}*/
			List<PaymentType> paymentList = restaurantServices.listPaymentTypeByOrgId(rest.getParentRestaurantId());
			for(PaymentType pt : paymentList){
				if(pt.getName().equalsIgnoreCase(order.getPaymentStatus())){
					if(pt.getType().equalsIgnoreCase(BasePaymentType.CASH.toString())){
						float moneyIn = Math.abs((float) (money-(check.getRoundOffTotal()+check.getCreditBalance())));
                        if(check.getCreditBalance()>0){
                        	updateCash(TillTransaction.SUCCESS.toString(), (float) check.getRoundOffTotal(), check, request,false, paymentType, "Transaction Cash In To from OrderId=" + order.getOrderId(), tillId, updateToPaymentType);
							updateCash(TillTransaction.SUCCESS.toString(), (float) check.getCreditBalance(), check, request,true, paymentType, "Transaction Credit Cash In To from OrderId=" + order.getOrderId(), tillId, updateToPaymentType);
						}else if(check.getCreditBalance()<0){
                        	updateCash(TillTransaction.SUCCESS.toString(), (float)Math.abs(check.getRoundOffTotal()+check.getCreditBalance()), check, request,false, paymentType, "Transaction Cash In To from OrderId=" + order.getOrderId(), tillId, updateToPaymentType);
                        }else{
                        	updateCash(TillTransaction.SUCCESS.toString(), (float) check.getRoundOffTotal(), check, request,false, paymentType, "Transaction Cash In To from OrderId=" + order.getOrderId(), tillId, updateToPaymentType);
                        }
                        if(moneyIn>0){
							updateCash(TillTransaction.SUCCESS.toString(), moneyIn, check, request,false, CustomPaymentType.TRANSACTION_CASH.toString(), "Transaction Cash In To from OrderId=" + order.getOrderId(), tillId, updateToPaymentType);
					}
					}else if(pt.getType().equalsIgnoreCase(BasePaymentType.CREDIT.toString())){
						if(order.getMoneyOut()>0)
							updateCash(TillTransaction.SUCCESS.toString(), order.getMoneyOut(), check, request,false, CustomPaymentType.TRANSACTION_CASH.toString(), "Transaction Cash In To from OrderId=" + order.getOrderId(), tillId, updateToPaymentType);
						if(check.getCreditBalance()>0 && !isPartialPayment){
							updateCash(TillTransaction.SUCCESS.toString(), (float)check.getRoundOffTotal(), check, request,false, paymentType, paymentType+" Order with OrderId="+order.getOrderId(),tillId,updateToPaymentType);
							updateCash(TillTransaction.SUCCESS.toString(), (float) check.getCreditBalance(), check, request,true,paymentType, "Transaction Credit Cash In To from OrderId=" + order.getOrderId(), tillId, updateToPaymentType);
						}else if(check.getCreditBalance()<0 && !isPartialPayment){
                        	updateCash(TillTransaction.SUCCESS.toString(), (float)Math.abs(check.getRoundOffTotal()+check.getCreditBalance()), check, request,false, paymentType, paymentType+" Order with OrderId="+order.getOrderId(),tillId,updateToPaymentType);
                        }else{
                        	System.out.println();
                        	if(check.getCreditBalance()<0){
                        		updateCash(TillTransaction.SUCCESS.toString(), (float)Math.abs(check.getRoundOffTotal()+check.getCreditBalance()), check, request,false, paymentType, paymentType+" Order with OrderId="+order.getOrderId(),tillId,updateToPaymentType);
                        	}else{
                        		updateCash(TillTransaction.SUCCESS.toString(), (float)check.getRoundOffTotal(), check, request,false, paymentType, paymentType+" Order with OrderId="+order.getOrderId(),tillId,updateToPaymentType);

                        	}
                        	}
					}else if(pt.getType().equalsIgnoreCase(BasePaymentType.PREPAID.toString())){
						if(order.getMoneyOut()>0)
							updateCash(TillTransaction.SUCCESS.toString(), order.getMoneyOut(), check, request,false, CustomPaymentType.TRANSACTION_CASH.toString(), "Transaction Cash In To from OrderId=" + order.getOrderId(), tillId, updateToPaymentType);
						if(check.getCreditBalance()>0){
							updateCash(TillTransaction.SUCCESS.toString(), (float)check.getRoundOffTotal(), check, request,false, paymentType, paymentType+" Order with OrderId="+order.getOrderId(),tillId,updateToPaymentType);
							updateCash(TillTransaction.SUCCESS.toString(), (float) check.getCreditBalance(), check, request,true, paymentType, "Transaction Credit Cash In To from OrderId=" + order.getOrderId(), tillId, updateToPaymentType);
						}else if(check.getCreditBalance()<0){
                        	updateCash(TillTransaction.SUCCESS.toString(), (float)Math.abs(check.getRoundOffTotal()+check.getCreditBalance()), check, request,false, paymentType, paymentType+" Order with OrderId="+order.getOrderId(),tillId,updateToPaymentType);
                        }else{
                        	updateCash(TillTransaction.SUCCESS.toString(), (float)check.getRoundOffTotal(), check, request,false, paymentType, paymentType+" Order with OrderId="+order.getOrderId(),tillId,updateToPaymentType);
                        }
					}else if(pt.getType().equalsIgnoreCase(BasePaymentType.DNC.toString()))
							updateCash(TillTransaction.SUCCESS.toString(), (float)check.getRoundOffTotal()+check.getCreditBalance(), check, request,false, paymentType, paymentType+" Order with OrderId="+order.getOrderId(),tillId,updateToPaymentType);

                }
			}

            if(check.getCreditBalance()!=0 &&  !("CUSTOMER CREDIT".equalsIgnoreCase(paymentType))){
				Customer cust  = customerService.getCustomer(check.getCustomerId());
				customerCreditService.updateBillRecoveryTransaction("SUCCESS",cust.getCustomerId(), check.getCreditBalance(), "CREDIT", "Setting Status Succes");
				
            }else if(check.getCreditBalance()<0 &&  "CUSTOMER CREDIT".equalsIgnoreCase(paymentType)){
				Customer cust  = customerService.getCustomer(check.getCustomerId());
				try{
					customerCreditService.updateBillRecoveryTransaction("SUCCESS",cust.getCustomerId(), check.getCreditBalance(), "CREDIT", "Setting Status Succes");
				}
				catch(Exception e ){
					e.printStackTrace();
					restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
					logger.info("Exception mail sent");
				}
            }else if(check.getCreditBalance()>0 &&  "CUSTOMER CREDIT".equalsIgnoreCase(paymentType)){
				Customer cust  = customerService.getCustomer(check.getCustomerId());
				try{
					customerCreditService.updateBillRecoveryTransaction("FAILED",cust.getCustomerId(), check.getCreditBalance(), "CREDIT", "Setting Status Failed");
				}
				catch(Exception e ){
					e.printStackTrace();
					//restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
					logger.info("Exception mail sent");
				}
				if(!isPartialPayment){
					check.setCreditBalance(0);
				}
            }
			logger.info("money In : "+money);
			order.setMoneyIn(money);
			check.setStatus(com.cookedspecially.enums.check.Status.Paid);
			checkService.addCheck(check);
		}
		return null;
	}

	private ResponseDTO updateCash(String transactionType,Float amount,Check check,HttpServletRequest request,boolean isCredit,String paymentType,String remark,String tillId,String updateToPaymentType) throws Exception{
		TillCashUpdateDTO updateDTO =  new TillCashUpdateDTO();
        updateDTO.amount = amount;
        updateDTO.checkId = check.getCheckId();
		updateDTO.paymentType=paymentType;
		updateDTO.ffcId=check.getKitchenScreenId();
		updateDTO.remarks=remark;
		updateDTO.tillTransactionType=transactionType;
		updateDTO.updatedToPaymentType="";
		updateDTO.tillId = tillId;
		updateDTO.updatedToPaymentType=updateToPaymentType;
		updateDTO.isCredit = isCredit;
		 ResponseDTO response=null;
		 synchronized(this){
			 System.out.println("called this API");
		 response = cashRegisterController.updateCash(updateDTO, request);
		if(response.result.equals("Error"))
			try{
                throw new Exception(response.message);
            }catch(Exception e){
				e.printStackTrace();
				//restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
				logger.info("Exception: "+response.message);
				return response;
			}
	    }
		return response;
	}

    public void restoreStock(Check check, int restoreCount) throws ParseException{

        List<Order> orderList  =  check.getOrders();
		String formats ="yyyy-MM-dd HH:mm:ss";
		Integer dishSizeId=null;
		String stockDishId=null;
		Restaurant rest=null;
		if(check.getRestaurantId()!=null){
			rest = restService.getRestaurant(check.getRestaurantId());
		}
		for(Order order :  orderList){

            List<OrderDish> orderDishes = order.getOrderDishes();
			for(OrderDish orderDish:orderDishes){
				List<Dish_Size> dish_Size = dishTypeService.getDish_SizeListbyDishId(orderDish.getDishId());
				for(Dish_Size dishSize :dish_Size){
					if(dishSize.getName().equalsIgnoreCase(orderDish.getDishSize())){
					dishSizeId = dishSize.getDishSizeId();
					}
				}
				if(dishSizeId!=null){
					stockDishId = orderDish.getDishId()+""+dishSizeId;
				}
				List<StockManagement> stockList=  stockManagementService.listStockedDishbyDishId((stockDishId==null?orderDish.getDishId():Integer.parseInt(stockDishId)), check.getKitchenScreenId());

                if(stockList.size()>0){
				Collections.sort(stockList, new Comparator<StockManagement>() {
					public int compare(StockManagement v1,StockManagement  v2) {
				        return "null".equalsIgnoreCase(v1.getExpireDate())?'0':v1.getExpireDate().compareTo((String)("null".equalsIgnoreCase(v2.getExpireDate())?'0':v2.getExpireDate()));
				    }
				});
				SimpleDateFormat formatter = new SimpleDateFormat(formats);
				TimeZone tz =  TimeZone.getTimeZone(rest.getTimeZone());
				formatter.setTimeZone(tz);

                    Date today =  new Date();
				String currentIst = formatter.format(today);
				java.util.Date actualTime = formatter.parse(currentIst);
				logger.info("Actual Date time in restoreDish : "+actualTime);
				Date expireDate = new Date();
				for(StockManagement stock : stockList){
					SimpleDateFormat formatterD = new SimpleDateFormat(formats);
					TimeZone tzD =  TimeZone.getTimeZone(rest.getTimeZone());
					formatterD.setTimeZone(tzD);
					expireDate = formatterD.parse(stock.getExpireDate());
					Calendar cal = Calendar.getInstance();
				    cal.setTime(expireDate);
				    if(compareTo(actualTime,expireDate)<0){
				    	if(stock.getRemainingQuantity()>=0){
				    		logger.info("restoring existing "+orderDish.getName()+" stock  Quantity :"+orderDish.getQuantity());
				    		stock.setRemainingQuantity(stock.getRemainingQuantity()+Math.abs((restoreCount==0?orderDish.getQuantity():restoreCount)));
				    		stockManagementService.addStockDish(stock);
					    	break;
				    	}
				    }
		}
		}
		}
		}
	}
	
	//Admin Function
	@RequestMapping(value = "/syncChecksAndOrders",method = RequestMethod.GET)
	public @ResponseBody String syncChecksAndOrders(HttpServletRequest request, HttpServletResponse response) {
		List<Integer> checkIds = checkService.getAllCheckIds();
		for (Integer checkId: checkIds) {
			Check check = checkService.getCheck(checkId);
			for (Order order : check.getOrders()) {
				order.setCheckId(checkId);
				orderService.addOrder(order);
			}
		}
		return "Succesfully updated all orders with checkId";
	}
	
	public void updateCheckForCancelOrder(Order order) {
		// Fetch the check from order
		// Update check with calculations of removal of bill values etc.
		logger.info("UpdateChekForcancelOrder api called" + order.getCheckId());
		User user = userService.getUser(order.getRestaurantId());
		Check check = checkService.getCheck(order.getCheckId());
		float checkBill = check.getBill() - order.getBill();
		check.setOutCircleDeliveryCharges(0);
		if(checkBill<=0) {
			checkBill = 0;
		}

        check.setBill(checkBill);
		checkService.addCheck(check);
	}

    @RequestMapping(value = "/setCheckStatus",method = RequestMethod.GET)
    @Deprecated
	public @ResponseBody Check setCheckStatus(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String checkId, @RequestParam String status, @RequestParam String paymentType,@RequestParam String paymentDetail) {
		Integer checkID = Integer.parseInt(checkId);
		//String statusStr = request.getParameter("status");
		//String paymentType = request.getParameter("paymentType");
		//String paymentDetail = request.getParameter("paymentDetail");
		logger.info("payment Type  : "+paymentType+"/ payment Detail : "+paymentDetail +"/ checkId :"+checkID +"/ Status :"+status);
		com.cookedspecially.enums.check.Status checkStatus = com.cookedspecially.enums.check.Status.valueOf(com.cookedspecially.enums.check.Status.class, status);
		Check check = checkService.getCheck(checkID);

        if(paymentType!=null){
			// Setting payment card details for Howzatt here.
			check.setPaymentType(paymentType);
			if(paymentDetail !=null){
				check.setPaymentDetail(paymentDetail);
			}
		}
		//setting order status to COD
		Order setOrder= orderService.getOrder(check.getOrderId());
		setOrder.setPaymentStatus("COD");
		orderService.addOrder(setOrder);

        if(check.getStatus() == checkStatus){
			return check;
		}
		if (checkStatus == com.cookedspecially.enums.check.Status.Cancel) {
			// cancel all orders within
			List<Order> orders = check.getOrders();
			for (Order order: orders) {
				order.setStatus(Status.CANCELLED);
				logger.info("Marked order cancelled checkId/invoiceId/orderId :"+checkId+"/"+check.getInvoiceId()+"/"+order.getOrderId());
			}
		}
		check.setStatus(checkStatus);
		check.setModifiedTime(new Date());
		if (checkStatus == com.cookedspecially.enums.check.Status.Paid || checkStatus == com.cookedspecially.enums.check.Status.Unpaid || checkStatus == com.cookedspecially.enums.check.Status.Cancel || checkStatus == com.cookedspecially.enums.check.Status.Pending) {
			check.setCloseTime(new Date());
			if(check.getTableId()>0){
				check.setDeliveryTime(check.getCloseTime());
				logger.info("Setting up tables delivery time for final");
			}
		}
		checkService.addCheck(check);
		return check;
	}

    @RequestMapping(value = "/markAllOrdersAsPaid",method = RequestMethod.GET)
	public @ResponseBody Check markAllOrdersAsPaid(HttpServletRequest request, HttpServletResponse response,@RequestParam String checkId) {
		Integer checkID = Integer.parseInt(checkId);
		Check check = checkService.getCheck(checkID);
		if (check != null) {
			// cancel all orders within
			List<Order> orders = check.getOrders();
			for (Order order: orders) {
				order.setStatus(Status.PAID);
			}
			checkService.addCheck(check);
        }
        return check;
	}
	
	@RequestMapping(value = "/setCheckType",method = RequestMethod.GET)
	@ApiOperation(value="Set check type Table, TakeAway, Delivery")
	public @ResponseBody String setCheckType(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required=false) String checkId,@RequestParam String type) {
		//String checkIdStr = request.getParameter("checkId") ;
		if(checkId == null)
			checkId = (String)request.getAttribute("checkId");

		Integer checkID = Integer.parseInt(checkId);
		//String typeStr = request.getParameter("type");
		if(type == null)
			type= (String)request.getAttribute("type");

		logger.info("set CheckType api called checkId :"+checkID+" checkType :"+ type);
		CheckType checkType = CheckType.valueOf(CheckType.class, type);
		Check check = checkService.getCheck(checkID);
		
		logger.info("set CheckType api called checkId/invoiceId :"+checkId +"/"+check.getInvoiceId()+" checkType :"+ type);
		check.setCheckType(checkType);
		check.setModifiedTime(new Date());
		checkService.addCheck(check);
		return "Check Type Set";
	}
	
	@RequestMapping(value = "/setCheckPaymentType")
	@Deprecated
	@ApiIgnore
    public
    @ResponseBody
    Check setCheckPaymentType(HttpServletRequest request, HttpServletResponse response) {
		Integer checkId = Integer.parseInt(request.getParameter("checkId"));
		String paymentModeStr = request.getParameter("type");
		PaymentMode payment = PaymentMode.valueOf(PaymentMode.class, paymentModeStr);
		Check check = checkService.getCheck(checkId);
		check.setPayment(payment);
		check.setModifiedTime(new Date());
		checkService.addCheck(check);
		return check;
	}

    @RequestMapping(value = "/setDeliveryTime")
    @Deprecated
    @ApiIgnore
	public @ResponseBody String setCheckDeliveryTime(HttpServletRequest request, HttpServletResponse response) throws ParseException {
		Integer checkId  = Integer.parseInt(request.getParameter("checkId"));
		String deliveryTimeStr = request.getParameter("deliveryTime");
		Check check = checkService.getCheck(checkId);
		Customer customer=customerService.getCustomer(check.getCustomerId());
		logger.info("Setting delivery Time, checkID : " + check.getCheckId());
		Restaurant rest =  restService.getRestaurant(check.getRestaurantId());
		if (check != null) {
			if (StringUtility.isNullOrEmpty(deliveryTimeStr)) {
				return "wrong delivery time";
			}

            //User user = userService.getUser(check.getRestaurantId());
			String format ="yyyy-MM-dd HH:mm";
			SimpleDateFormat sdf =  new SimpleDateFormat(format);
			sdf.setTimeZone(TimeZone.getTimeZone(rest.getTimeZone()));
			check.setDeliveryTime(sdf.parse(customer.getDeliveryTime()));
			checkService.addCheck(check);
			return "recieved";
		}
		return "check not found";
	}

    @RequestMapping(value = "/setDeliveryArea")
    @Deprecated
    @ApiIgnore
	public @ResponseBody String setCheckDeliveryArea(HttpServletRequest request, HttpServletResponse response) throws ParseException {
		Integer checkId  = Integer.parseInt(request.getParameter("checkId"));
		String deliveryArea = request.getParameter("deliveryArea");
		Check check = checkService.getCheck(checkId);
		//User user =userService.getUser(check.getRestaurantId());
		logger.info("setDeliveryArea() checkID/invoiceId :"+check.getCheckId()+"/"+check.getInvoiceId());
		logger.info("delivery Area :"+check.getDeliveryArea());
		return "check not found";
	}
	
	@RequestMapping(value = "/setDeliveryAddress")
	@Deprecated
	@ApiIgnore
	public @ResponseBody String setCheckDeliveryAddress(HttpServletRequest request, HttpServletResponse response) throws ParseException {
		Integer checkId  = Integer.parseInt(request.getParameter("checkId"));
		String deliveryAddress = request.getParameter("deliveryAddress");
		Check check = checkService.getCheck(checkId);
		logger.info("Setting delivery Address, checkId/invoiceId :"+check.getCheckId()+"/"+check.getInvoiceId());
		if (check != null) {
			check.setDeliveryAddress(deliveryAddress);
			checkService.addCheck(check);
			return "recieved";
		}
		return "check not found";
	}

    @RequestMapping(value = "/setDeliveryDetails")
    @Deprecated
    @ApiIgnore
	public @ResponseBody String setCheckDeliveryDetails(HttpServletRequest request, HttpServletResponse response) throws ParseException {
		Integer checkId  = Integer.parseInt(request.getParameter("checkId"));
		String deliveryAddress = request.getParameter("deliveryAddress");
		String deliveryArea = request.getParameter("deliveryArea");
		String deliveryTimeStr = request.getParameter("deliveryTime");

        Check check = checkService.getCheck(checkId);
		Customer customer=customerService.getCustomer(check.getCustomerId());
		logger.info("Setting deivery Detail to check /InvoiceId: "+check.getCheckId()+"/"+check.getInvoiceId());
		if (check != null) {
			check.setDeliveryAddress(deliveryAddress);
			logger.info("setting Delivery address :"+deliveryAddress);
			logger.info("setting delivery Area :"+deliveryArea);
			check.setDeliveryArea(deliveryArea);
			if (!StringUtility.isNullOrEmpty(deliveryTimeStr)) {
				DateFormat formatter;
				formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				Restaurant rest=restService.getRestaurant(check.getRestaurantId());
				formatter.setTimeZone(TimeZone.getTimeZone(rest.getTimeZone()));
				logger.info("setting delivery Time :"+customer.getDeliveryTime());
				String format ="yyyy-MM-dd HH:mm";
				SimpleDateFormat sdf =  new SimpleDateFormat(format);
				sdf.setTimeZone(TimeZone.getTimeZone(rest.getTimeZone()));
				check.setDeliveryTime(sdf.parse(customer.getDeliveryTime()));

            }
			checkService.addCheck(check);
			return "recieved";
		}
		return "check not found";
	}

    public String stockNotification(JsonDish jsonDish, Check check, StockManagement sm,HttpServletRequest request, String message ,boolean mailOnly) throws Exception {
		Restaurant restaurant = restService.getRestaurant(check.getRestaurantId());
		//Creating Check
		Check kitchenCheck = new Check();
		kitchenCheck.setRestaurantId(check.getRestaurantId());
		checkService.addCheck(kitchenCheck);
		String itemName =jsonDish.getName();
		//Creating Order
		Order order = new Order();
        //Creating dish
        OrderDish orderDish =  new OrderDish();
		List<OrderDish> od =  new ArrayList<OrderDish>();
		List<Order> kitchenOrder  =  new ArrayList<Order>();
		orderDish.setDishType("Alert");
		orderDish.setName(itemName);
		orderDish.setInstructions("");
		orderDish.setQuantity(1);
		orderDish.setPrice(0.0f);
		od.add(orderDish);
		//dish created
		HashMap<Integer, OrderDish> orderDishMap = new HashMap<Integer, OrderDish>();

        orderDishMap.put(orderDish.getDishId(),orderDish);
		order.setOrderDishes(new ArrayList<OrderDish>(orderDishMap.values()));
		order.setRestaurantId(check.getRestaurantId());
		order.setCheckId(kitchenCheck.getCheckId());
		order.setSourceType(SourceType.ONLINE);
		order.setDestinationType(DestinationType.COUNTER);
		order.setBill(0.0f);
		order.setPaymentStatus("SUBSCRIPTION");
		order.setStatus(Status.NEW);
		order.setCreatedTime(new Date());
		// order created

		FulfillmentCenter fulfillmentCenter  = kitchenScreenService.getKitchenScreen(sm.getFulfillmentCenterId());
		String fulfillmentCenterName = fulfillmentCenter.getName();
		//creating customer
		Customer cust = new Customer();
		cust.setFirstName("");
		cust.setLastName(fulfillmentCenterName);
		cust.setDeliveryArea(fulfillmentCenterName);
		cust.setCity("");
		cust.setAddress((fulfillmentCenter.getAddress()==null?"":fulfillmentCenter.getAddress()) +" "+fulfillmentCenter.getLocation());
		cust.setPhone(restaurant.getBussinessPhoneNo());
		cust.setCreatedTime(new Date());
		cust.setEmail("support@cookedspecially.com");
		//customer created

        kitchenOrder.add(order);
		kitchenCheck.setName(fulfillmentCenterName);
		kitchenCheck.setPhone(restaurant.getBussinessPhoneNo());
		kitchenCheck.setOrderId(order.getOrderId());
		kitchenCheck.setKitchenScreenId(sm.getFulfillmentCenterId());
		kitchenCheck.setCheckType(CheckType.Delivery);
		kitchenCheck.setDeliveryInst(message);
		kitchenCheck.setDeliveryTime(new  Date());
		kitchenCheck.setOrderSource("Website");
		kitchenCheck.setStatus(com.cookedspecially.enums.check.Status.Unpaid);
		kitchenCheck.setDeliveryArea(fulfillmentCenterName);
		kitchenCheck.setDeliveryAddress(fulfillmentCenter.getAddress()==null?"":fulfillmentCenter.getAddress() +" "+fulfillmentCenter.getLocation());
		kitchenCheck.setBill(0);
		kitchenCheck.setCustomerId(cust.getCustomerId());
		kitchenCheck.setOpenTime(new Date());
		if(!mailOnly){
		customerService.addCustomer(cust);
		orderService.addOrder(order);
		checkService.addCheck(kitchenCheck);
		}
		try{
		emailNotification(restaurant.getRestaurantId(),restaurant.getAlertMail(),itemName,fulfillmentCenterName,message, request);
		}
		catch(Exception e){
			e.printStackTrace();
			restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
			logger.info("Exception mail sent");
		}
		//stock email sent.
		return "success";
	}
	
	private String updateDishStock(JsonDish jsonDish, Check check, HttpServletRequest request,int emailSendCount) throws Exception{
        StockManagement manage = null;

		String updatedId = null;
	    if(jsonDish.getDishSizeId()!=null){
	    	updatedId = jsonDish.getId()+""+jsonDish.getDishSizeId();
	    }
	    Restaurant rest=null;
		if(check.getRestaurantId()!=null){
			rest = restService.getRestaurant(check.getRestaurantId());
		}
		List<StockManagement> stockList = stockManagementService.listStockedDishbyDishId(updatedId==null?jsonDish.getId():Integer.parseInt(updatedId), check.getKitchenScreenId());
		String formats ="yyyy-MM-dd HH:mm:ss";
		boolean mailOnly=false;
		String message = "";
		if(stockList!=null){
			Collections.sort(stockList, new Comparator<StockManagement>() {
				public int compare(StockManagement v1,StockManagement  v2) {
			        return "null".equalsIgnoreCase(v1.getExpireDate())?'0':v1.getExpireDate().compareTo((String)("null".equalsIgnoreCase(v2.getExpireDate())?'0':v2.getExpireDate()));
			    }
			});
		}
		SimpleDateFormat formatter = new SimpleDateFormat(formats);
		TimeZone tz =  TimeZone.getTimeZone(rest.getTimeZone());
		formatter.setTimeZone(tz);

        Date today =  new Date();
		String currentIst = formatter.format(today);
		java.util.Date actualTime = formatter.parse(currentIst);
		logger.info("Actual Date time : "+actualTime);
		Date expireDate = new Date();
		int stockQuantity = getStockQuantity(stockList,check,request,jsonDish);


        int innerCount=0;
		for(StockManagement stock :  stockList){
			SimpleDateFormat formatterD = new SimpleDateFormat(formats);
			TimeZone tzD =  TimeZone.getTimeZone(rest.getTimeZone());
			formatterD.setTimeZone(tzD);
			expireDate = formatterD.parse(stock.getExpireDate());
			Calendar cal = Calendar.getInstance();
		    cal.setTime(expireDate);
		    if(compareTo(actualTime,expireDate)<0){
		    	if(stock.getRemainingQuantity()>=jsonDish.getQuantity()){
                    manage = stock;
                    innerCount ++;
			    	break;
		    	}
		    	else if(stockList.size()>1 && stock.getRemainingQuantity()==0){
		    		mailOnly=true;
					logger.info("Stock finshed");
					message = "Alert!: Id "+stock.getId()+": "+jsonDish.getName() +" stock got finished. Item Qty: "+stock.getRemainingQuantity()+". Expire Date "+stock.getExpireDate()+". We just remove the finished quantity from the stock. Started deducting from fresh Stock.";
					try{ 
					stockNotification(jsonDish, check, stock, request,message,mailOnly);
					}catch(Exception e){
						e.printStackTrace();
					}
					stock.setRemainingQuantity(0);
					stockManagementService.removeStockDish(stock.getId());
		    	}
		    }
		    else {
		    	mailOnly=true;
				logger.info("Stock got expired");
				message = "Alert!: Id "+stock.getId()+": "+jsonDish.getName() +" stock got expired please update stock ASAP. Expired item Qty: "+stock.getRemainingQuantity()+". Expire Date "+stock.getExpireDate()+". We just remove the expired quantity from the stock. Total stock remaining :"+stockQuantity;
			try{
				stockNotification(jsonDish, check, stock, request,message,mailOnly);
		    }catch(Exception e){
				e.printStackTrace();
			}
				stock.setRemainingQuantity(0);
				stockManagementService.removeStockDish(stock.getId());
			}
		}
		if(stockList.size()>0){
			if(innerCount==0){
				return "stockOver";
			}
		}
		if(manage!=null){
			if(compareTo(actualTime,expireDate)<0){
			if(manage.getFulfillmentCenterId()==check.getKitchenScreenId()){
				if(manage.getRemainingQuantity()>=jsonDish.getQuantity()){
				manage.setRemainingQuantity(manage.getRemainingQuantity()-jsonDish.getQuantity());
				}
				else {
					message = "Alert!: This item is Out of Stock. Remaning Quantity :"+stockQuantity +", last  order Quantity :"+jsonDish.getQuantity();
					mailOnly=true;
				try{
					stockNotification(jsonDish, check, manage, request,message,mailOnly);
				}catch(Exception e){
					e.printStackTrace();
				}
					return "stockOver";
				}
			}
			if((stockQuantity-jsonDish.getQuantity())==manage.getAlertQuantity()){
				message = "Alert!: This item is below alert Quantity please re-fill ASAP. Remaning Quantity :"+(stockQuantity-jsonDish.getQuantity());
				try{
					stockNotification(jsonDish, check, manage, request,message,mailOnly);
				}catch(Exception e){
					e.printStackTrace();
				}
				}
			else if((stockQuantity-jsonDish.getQuantity())==emailSendCount && emailSendCount<=manage.getAlertQuantity()) {
				mailOnly=true;
				message = "Alert!: This item is below alert Quantity please re-fill ASAP. Remaning Quantity :"+(stockQuantity-jsonDish.getQuantity());
			try{
				stockNotification(jsonDish, check, manage, request,message,mailOnly);

			}catch(Exception e){
				e.printStackTrace();
			}
				
			}
			/*else if((stockQuantity-jsonDish.getQuantity())==1){
				message = "Alert!: Only 1 Item is remaining ";
				stockNotification(jsonDish, check, manage, request,message,mailOnly);
			}*/
			stockManagementService.addStockDish(manage);
		}
		}
		return "";
	}

    public String emailNotification(Integer restaurantId,String emailAddr,String itemName,String FulfillmentCenterName,String message,HttpServletRequest request){

        if (restaurantId>0) {
			Restaurant rest=restService.getRestaurant(restaurantId);
			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append("<html><body>");
			String username="";
			String password="";
			if (rest != null) {
				strBuilder.append("Restaurant Name : " + rest.getBussinessName() + "<br />");
                username = MailerUtility.username;
                password = MailerUtility.password;
            }
			strBuilder.append("<p style='align:center;'<b><i>Attention Required !</i></b></p><br />");
			strBuilder.append("<table><tr><td colspan='2'><hr></td></tr>");
			strBuilder.append("<br>");
			strBuilder.append("<tr>");
			strBuilder.append("<td><b>Fulfillment Center :</b></td>");
			strBuilder.append("<td>" + FulfillmentCenterName+ "</td>&nbsp;&nbsp;");
			strBuilder.append("</tr><tr>");
			strBuilder.append("<td><b>Dish :</b></td>");
			strBuilder.append("<td>" +itemName + "</td>&nbsp;&nbsp;");
			strBuilder.append("</tr><tr>");
			strBuilder.append("</tr>");
			strBuilder.append("<br>");
			strBuilder.append("<tr><td colspan='2'><hr></td></tr>");
			strBuilder.append("<br>");
			strBuilder.append("<tr>");
			strBuilder.append("<td colspan='3'>");
			strBuilder.append("<B align='center'>"+message+"</B>");
			strBuilder.append("</td>");
			strBuilder.append("</tr>");
			strBuilder.append("</table>");
			strBuilder.append("<br/>");
			strBuilder.append("<table>");
			strBuilder.append("<tr>");
			strBuilder.append("<td>");
			strBuilder.append("<tr>");
			strBuilder.append("<td>");
			strBuilder.append("</td>");
			strBuilder.append("</tr>");
			strBuilder.append("</table>");
			strBuilder.append("</body></html>");
			MailerUtility.sendHTMLMail(emailAddr, "Stock Notification", strBuilder.toString(),username,password);
		} else {
			return "Error: No check found";
		}

        return "Email Sent Successfully";
	}
	
	private  Date convertToGmt( Date date ){
	    TimeZone tz = TimeZone.getDefault();
	    Date ret = new Date( date.getTime() - tz.getRawOffset() );

	    if ( tz.inDaylightTime( ret )){
	        Date dstDate = new Date( ret.getTime() - tz.getDSTSavings() );
	        if ( tz.inDaylightTime( dstDate )){
	            ret = dstDate;
	        }
	     }
	     return ret;
	}
	
	private String validateDeliveryTime(Customer customer,Check check,String deliveryTime) throws ParseException{
		
		 Restaurant rest=null;
		if(check.getRestaurantId()!=null){
				rest = restService.getRestaurant(check.getRestaurantId());
			}
		String format ="MM-dd-yyyy HH:mm";
		SimpleDateFormat formatterD = new SimpleDateFormat(format);
		TimeZone tzD =  TimeZone.getTimeZone(rest.getTimeZone() );
		formatterD.setTimeZone(tzD);
		
		Date deliveryTimeD = formatterD.parse(deliveryTime);
		
		logger.info("Validate Delivery Time api called");
		
		Calendar cal = Calendar.getInstance();
	    cal.setTime(deliveryTimeD);
	    int year = cal.get(Calendar.YEAR);
	    int month = cal.get(Calendar.MONTH);
	    int day = cal.get(Calendar.DAY_OF_MONTH);
		
		String vvl = month+1+"-"+day+"-"+year+" "+"22:45";
		//logger.info("compare date : "+vvl +"  : "+formatterD.parse(vvl));
		Date overDate = formatterD.parse(vvl);
		
		Date today = new Date();
		Date newDeliveryTimeDate =   new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		TimeZone tz =  TimeZone.getTimeZone(rest.getTimeZone());
		formatter.setTimeZone(tz);
		
		//String currentIstDelTime = ;
		java.util.Date delTime = formatter.parse(deliveryTime );
		
		DeliveryArea deliveryArea  =  deliveryAreaService.getDeliveryAreaByName(check.getDeliveryArea(),check.getKitchenScreenId(),check.getRestaurantId());
		
		if(deliveryArea!=null){
		Integer minDeliveryTime = deliveryArea.getMinDeliveryTime(); 
		Integer minDelieryInterval =  deliveryArea.getDeliveryTimeInterval();
		if (minDeliveryTime !=null){
			today.setMinutes(today.getMinutes()+(minDeliveryTime-15));
		}else {
			today.setMinutes(today.getMinutes()); 
		}
		String currentIst = formatter.format(today);
		java.util.Date actualTime = formatter.parse(currentIst);
		
		String ndT = formatter.format(newDeliveryTimeDate);
		java.util.Date newDeliveryTime = formatter.parse(ndT);
		
		logger.info("actualTime :" + actualTime + "delTime :" + delTime + " exact deiveryTime default :" + deliveryTimeD);
		
		logger.info("Over DATE : "+overDate );

		if (compareTo( actualTime, deliveryTimeD) < 0 ){
		   check.setDeliveryTime(deliveryTimeD);
		}
		else if (compareTo(actualTime, deliveryTimeD ) > 0 ) {
		   if(minDeliveryTime!=null){
			   newDeliveryTime.setMinutes(newDeliveryTime.getMinutes()+minDeliveryTime);
			   newDeliveryTime.setMinutes((Math.round(newDeliveryTime.getMinutes()/minDelieryInterval) * minDelieryInterval) % 60);
			   check.setDeliveryTime(newDeliveryTime);
		   }
		   else {
			   newDeliveryTime.setMinutes(newDeliveryTime .getMinutes()+45);
			   newDeliveryTime.setMinutes((Math.round(newDeliveryTime .getMinutes()/15) * 15) % 60);
			   check.setDeliveryTime(newDeliveryTime );
		   }
		 
		}
		else{
			   newDeliveryTime.setMinutes(newDeliveryTime .getMinutes()+45);
			   newDeliveryTime.setMinutes((Math.round(newDeliveryTime .getMinutes()/15) * 15) % 60);
			   check.setDeliveryTime(newDeliveryTime );
		}
		}else{
			Integer minDeliveryTime=45;
			Integer minDelieryInterval=15;
			if (minDeliveryTime !=null){
				today.setMinutes(today.getMinutes()+(minDeliveryTime-15));
			}else {
				today.setMinutes(today.getMinutes()); 
			}
			String currentIst = formatter.format(today);
			java.util.Date actualTime = formatter.parse(currentIst);
			
			String ndT = formatter.format(newDeliveryTimeDate);
			java.util.Date newDeliveryTime = formatter.parse(ndT);
			
			logger.info("actualTime :" + actualTime + "delTime :" + delTime + " exact deiveryTime default :" + deliveryTimeD);
			
			logger.info("Over DATE : "+overDate );

			if (compareTo( actualTime, deliveryTimeD) < 0 ){
			   check.setDeliveryTime(deliveryTimeD);
			}
			else if (compareTo(actualTime, deliveryTimeD ) > 0 ) {
			   if(minDeliveryTime!=null){
				   newDeliveryTime.setMinutes(newDeliveryTime.getMinutes()+minDeliveryTime);
				   newDeliveryTime.setMinutes((Math.round(newDeliveryTime.getMinutes()/minDelieryInterval) * minDelieryInterval) % 60);
				   check.setDeliveryTime(newDeliveryTime);
			   }
			   else {
				   newDeliveryTime.setMinutes(newDeliveryTime .getMinutes()+45);
				   newDeliveryTime.setMinutes((Math.round(newDeliveryTime .getMinutes()/15) * 15) % 60);
				   check.setDeliveryTime(newDeliveryTime );
			   }
			 
			}
			else{
				   newDeliveryTime.setMinutes(newDeliveryTime .getMinutes()+45);
				   newDeliveryTime.setMinutes((Math.round(newDeliveryTime .getMinutes()/15) * 15) % 60);
				   check.setDeliveryTime(newDeliveryTime );
			}
			
		}
		 return "deliveryAreaError";
	}
	
	public double removeCalories(List<Order> orderList, Restaurant restaurant){
		HashMap<String, OrderDish> orderDishMap = new HashMap<String, OrderDish>();
		double caloriesCount = 0;
		float booster = 0.0f;
		for(Order order  : orderList){
			//order.getCheckId();
			Check check =  checkService.getCheck(order.getCheckId()); 
			List<OrderDish> orderDish= order.getOrderDishes();
		for (OrderDish oDish  : orderDish) {
			Dish dish = dishService.getDish(oDish.getDishId());
			List<com.cookedspecially.domain.OrderSource> os =  restaurantServices.listOrderSourcesByOrgId(restaurant.getParentRestaurantId());
			for(com.cookedspecially.domain.OrderSource orderS :os){
				if(orderS.getName().equalsIgnoreCase(check.getOrderSource())){
					booster =orderS.getPointbooster();
				}
			}
			
			List<NutrientInfo> nutrientInfo =  dish.getNutrientInfo();
			for(NutrientInfo info : nutrientInfo ){
				if(rewardBase.equalsIgnoreCase(info.getName())){
					try{
						caloriesCount +=(info.getValue()*oDish.getQuantity())*booster;
				}catch(Exception e ){
					try {
						restaurantServices.emailException(ExceptionUtils.getStackTrace(e),REQUEST);
					} catch (UnsupportedEncodingException | MessagingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					logger.info("Exception mail sent");
					e.printStackTrace();
				}
			}
			}
			
		if (orderDishMap.get(oDish.getDishId()+""+oDish.getDishSize().replaceAll("\\s","")) != null) {
			orderDishMap.get(oDish.getDishId()+""+oDish.getDishSize().replaceAll("\\s","")).addMore(oDish.getQuantity());
		} else {
			HashMap<Integer, OrderAddOn> orderAddOnDishMap = new HashMap<Integer, OrderAddOn>();
			OrderDish orderDishes = new OrderDish();
			List<OrderAddOn> addOnsList = oDish.getOrderAddOn();
			if(addOnsList!=null){
			for(OrderAddOn oAddOn :addOnsList){
				OrderAddOn orderAddOn = new OrderAddOn();
				AddOnDish addon =  addOnDishService.getDish(oAddOn .getDishId());
				if(addon !=null){
					List<AddOnNutrientInfo> addOnNutrientInfo =  addon.getNutritionalInfo();
					for(AddOnNutrientInfo info : addOnNutrientInfo ){
						if(rewardBase.equalsIgnoreCase(info.getName())){
							try{
							caloriesCount +=info.getValue()*oAddOn.getQuantity()*booster;
						}catch(Exception e ){
							e.printStackTrace();
							try {
								restaurantServices.emailException(ExceptionUtils.getStackTrace(e),REQUEST);
							} catch (UnsupportedEncodingException
									| MessagingException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							logger.info("Exception mail sent");
						}
					}
					}
				}
			}
		}
			orderDishMap.put(oDish.getDishId()+""+oDish.getDishSize().replaceAll("\\s",""), orderDishes);
	}
	}
	}
		return caloriesCount;
	}
	
	public void restoreDishStock(int dishId, int ffcId, int restoreCount) throws ParseException{
		List<StockManagement> stockList = stockManagementService.listStockedDishbyDishId(dishId, ffcId);
		String formats ="yyyy-MM-dd HH:mm:ss";
		if(stockList.size()>0){
			Collections.sort(stockList, new Comparator<StockManagement>() {
				public int compare(StockManagement v1,StockManagement  v2) {
			        return "null".equalsIgnoreCase(v1.getExpireDate())?'0':v1.getExpireDate().compareTo((String)("null".equalsIgnoreCase(v2.getExpireDate())?'0':v2.getExpireDate()));
			    }
			});
			SimpleDateFormat formatter = new SimpleDateFormat(formats);
			TimeZone tz =  TimeZone.getTimeZone("Asia/kolkata");
			formatter.setTimeZone(tz);
			
			Date today =  new Date();
			String currentIst = formatter.format(today);
			java.util.Date actualTime = formatter.parse(currentIst);
			logger.info("Actual Date time in restoreDish : "+actualTime);
			Date expireDate = new Date();
			for(StockManagement stock : stockList){
				Restaurant rest=null;
				if(stock.getRestaurantId()!=null){
					rest = restService.getRestaurant(stock.getRestaurantId());
				}
				SimpleDateFormat formatterD = new SimpleDateFormat(formats);
				TimeZone tzD =  TimeZone.getTimeZone(rest.getTimeZone());
				formatterD.setTimeZone(tzD);
				expireDate = formatterD.parse(stock.getExpireDate());
				Calendar cal = Calendar.getInstance();
			    cal.setTime(expireDate);
			    if(compareTo(actualTime,expireDate)<0){
			    	if(stock.getRemainingQuantity()>=0){
			    		logger.info("restoring existing "+stock.getDishName()+" stock  Quantity :"+restoreCount);
			    		stock.setRemainingQuantity(stock.getRemainingQuantity()+Math.abs((restoreCount==0?1:restoreCount)));
			    		stockManagementService.addStockDish(stock);
				    	break;
			    	}
			    }
	}
	}
		
		
	}
	
	private int getStockQuantity(List<StockManagement> stockList , Check check, HttpServletRequest request, JsonDish jsonDish) throws ParseException{
		int stockQuantity=0;
		
		 Restaurant rest=null;
			if(check.getRestaurantId()!=null){
				rest = restService.getRestaurant(check.getRestaurantId());
		}
		String formats ="yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat formatter = new SimpleDateFormat(formats);
		TimeZone tz =  TimeZone.getTimeZone("Asia/kolkata");
		formatter.setTimeZone(tz);
		
		Date today =  new Date();
		String currentIst = formatter.format(today);
		java.util.Date actualTime = formatter.parse(currentIst);
		logger.info("Actual Date time : "+actualTime);
		Date expireDate = new Date();
		// workshop
		for(StockManagement stock :  stockList){
			SimpleDateFormat formatterD = new SimpleDateFormat(formats);
			TimeZone tzD =  TimeZone.getTimeZone(rest.getTimeZone());
			formatterD.setTimeZone(tzD);
			expireDate = formatterD.parse(stock.getExpireDate());
			Calendar cal = Calendar.getInstance();
		    cal.setTime(expireDate);
		    String updatedId = null;
		    if(jsonDish.getDishSizeId()!=null){
		    	updatedId = jsonDish.getId()+""+jsonDish.getDishSizeId();
		    }
		    
			if(stock.getDishId()==(updatedId==null?jsonDish.getId():Integer.parseInt(updatedId))){
				  if(compareTo(actualTime,expireDate)<0){
					  stockQuantity +=stock.getRemainingQuantity(); 
				  }
			}
		}
		return stockQuantity;
		
	}
	
	@RequestMapping(value = "/addToCheck.json", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ApiIgnore
	public @ResponseBody OrderResponse addToCheckJSON(@RequestBody JsonOrder order,Model model, HttpServletRequest request, HttpServletResponse reponse) throws Exception {
		Check check = null;
		String error = "";
		boolean editedOrder=false;
		boolean allowDiscount=false;
		if(request.getAttribute("editOrderFlag")!=null)
		 editedOrder = (boolean) request.getAttribute("editOrderFlag");
		if(request.getAttribute("allowDiscount")!=null)
			allowDiscount = (boolean) request.getAttribute("allowDiscount");
		Restaurant restaurant=null;
		String checkTypeStr = request.getParameter("checkType");
		CheckType checkType=null;
		if(checkTypeStr !=null){
			if(checkTypeStr.equals(CheckType.TakeAway.toString())){
				checkType = CheckType.TakeAway;
			}else if(checkTypeStr .equals(CheckType.Table.toString())){
				checkType = CheckType.Table;
			}else{
				checkType = CheckType.Delivery;
			}
		}else{
			checkTypeStr  = (String) request.getAttribute("checkType");
			if(checkTypeStr !=null){
				if(checkTypeStr .equals(CheckType.TakeAway.toString())){
					checkType = CheckType.TakeAway;
				}else if(checkTypeStr .equals(CheckType.Table.toString())){
					checkType = CheckType.Table;
				}else{
					checkType = CheckType.Delivery;
				}
			}
		}
		Integer restaurantId = null;
		String paymentStatus=request.getParameter("paymentStatus");
		String deliveryInst=request.getParameter("deliveryInst");
		String paymentThirdParty=request.getParameter("paymentThirdParty");
		String orderSource=request.getParameter("orderSource");
		String paidStatus = (String)request.getAttribute("paidStatus");
		if(paymentStatus == null)
			paymentStatus = (String)request.getAttribute("paymentStatus");
		if(deliveryInst == null)
			deliveryInst = (String)request.getAttribute("deliveryInst");
		if(paymentThirdParty== null)
			paymentThirdParty = (String)request.getAttribute("paymentThirdParty");
		if(orderSource== null)
			orderSource=(String) request.getAttribute("orderSource");

		String emailbill=request.getParameter("emailbill");
		boolean bFirstOrder = false;
		logger.info("10>addToCheck.json called..");
		if (order.getCheckId() > 0) {
			check = checkService.getCheck(order.getCheckId());
			logger.info("11>If orders check Id/InvoiceId is > 0 then: "+check.getCheckId()+"/"+check.getInvoiceId()+"--"+check.getRoundOffTotal());
		}
		
		SeatingTable table = null;
		Customer customer = null;
		if (check == null) {
			logger.info("12>If check is null then.");
			if (order.getTableId() > 0) {
				table = seatingTableService.getSeatingTable(order.getTableId());
				if (table != null){
					restaurantId = table.getRestaurantId();
					check = checkService.getCheckByTableId(table.getRestaurantId(), table.getId());
				}
			}
			else if (order.getCustId() > 0) {
				customer = customerService.getCustomer(order.getCustId());
				logger.info("13>If Order customer Id >  0 then. :"+customer);
				if (customer != null) {
					restaurantId = customer.getRestaurantId();
					check = checkService.getCheckByCustId(restaurantId, customer.getCustomerId());
					logger.info("14>if customer != null :"+check);
				}
			}
		}
		if (check != null) {
			restaurantId = check.getRestaurantId();
			logger.info("12>if check != null resturant Id :"+restaurantId);
		}else {
			logger.info("12>if check == null or else");
			if (order.getTableId() > 0 && table == null) {
				error = "No table found";
			} else if (order.getCustId() > 0 && customer == null) {
				error = "No customer was found";
			} else {
				check = new Check();
				bFirstOrder = true;
				logger.info("be first Order :"+bFirstOrder);
				check.setOpenTime(new Date());
				check.setModifiedTime(new Date());
				check.setRestaurantId(restaurantId);
				check.setStatus(com.cookedspecially.enums.check.Status.Unpaid);
				if (table != null) {
					check.setTableId(table.getId());
					check.setCheckType(CheckType.Table);
					paymentStatus= "COD";
					check.setDeliveryTime(check.getOpenTime());
					logger.info("Table delivery Time been set:"+check.getOpenTime());
				} else if (customer != null) {
				 logger.info("If customer != null then: set checkType Takaway");
					check.setCustomerId(customer.getCustomerId());
					check.setCheckType(CheckType.Delivery);
					check.setDeliveryAddress(customer.getAddress() + "," + customer.getCity());
				}
				check.setUserId(restaurantId);
				// check's delivery area will be set only when first order is being placed and is for non-table orders
				// as non table orders can either be delivery.
				check.setDeliveryArea((order.getDeliveryArea()!= null && table == null)?order.getDeliveryArea():"");
				check.setDeliveryAddress((order.getDeliveryAddress()!= null && table == null)?order.getDeliveryAddress():"");
				check.setCheckType(order.getCheckType());
			}
		}
		
		Order targetOrder=null;
		OrderResponse orderResp = new OrderResponse();
		double caloriesCount = 0;
		int activeDeliveryAreaID=0;
		if (StringUtility.isNullOrEmpty(error)) {
			List<JsonDish> jsonDishes = order.getItems();
			HashSet<Integer> hs  =  new HashSet<Integer>();
			List<Integer> li = new ArrayList<Integer>();
			for (JsonDish jsonDish  : jsonDishes) {
				Dish dish = dishService.getDish(jsonDish.getId());
				if(dish !=null){
				int microScreenId = dish.getMicroScreen();
				hs.add(microScreenId);
				li.add(microScreenId);
			}else {
				orderResp.setError("Sorry! this  is not a  valid dish");
				orderResp.setStatus("Failed");
				return orderResp;
			}
			}
			for(int fixSceenId : hs){
					    targetOrder =  new Order();
					    targetOrder.setMicroKitchenId(fixSceenId);
						logger.info("13>using Order class instance setting order to order table here..");
						targetOrder.setUserId(check.getUserId());
						logger.info("14>check id/InvoiceId   :"+check.getCheckId()+"/"+check.getInvoiceId());
						targetOrder.setRestaurantId(restaurantId);
						targetOrder.setCheckId(check.getCheckId());
						targetOrder.setCreatedTime(new Date());
						targetOrder.setModifiedTime(new Date());
					if (check.getTableId() > 0) {
						targetOrder.setSourceType(SourceType.TABLE);
						targetOrder.setSourceId(order.getTableId());
						targetOrder.setDestinationType(DestinationType.TABLE);
						targetOrder.setDestinationId(order.getTableId());
						paymentStatus= "COD";
						check.setDeliveryTime(check.getOpenTime());
						logger.info(" Delivery time set as open time for table orders:"+check.getDeliveryTime());
					} 
					else if (check.getCustomerId() > 0) {
						targetOrder.setSourceType(SourceType.COUNTER);
						targetOrder.setSourceId(order.getCustId());
						targetOrder.setDestinationType(DestinationType.COUNTER);
						targetOrder.setDestinationId(order.getCustId());
						
						if(paymentStatus!=null){
							if("Online".equalsIgnoreCase(paymentStatus) || "OnlinePayPal".equalsIgnoreCase(paymentStatus)){
							targetOrder.setPaymentStatus("PG_PENDING");
							}else if("MobiKwikWallet".equals(paymentStatus)){
								targetOrder.setPaymentStatus("WALLET_PENDING");
							}else if("payTm".equals(paymentStatus)){
								targetOrder.setPaymentStatus("PAYTM_PENDING");
							}
							else{
							targetOrder.setPaymentStatus(paymentStatus);}
						}else {
								targetOrder.setPaymentStatus("COD");
							}
					}
					if(editedOrder){
						Status status = (Status) request.getAttribute("editOrderStatus");
						String deliveryBoy = (String)request.getAttribute("editOrderDeliverBoy");
						Float moneyIn = (Float)request.getAttribute("editOrderMoneyIn");
						Float moneyOut = (Float)request.getAttribute("editOrderMoneyOut");
						int microId = (int) request.getAttribute("editOrderMicroKitchen");
						
					    if(check.getStatus() !=com.cookedspecially.enums.check.Status.Paid){
							check.setStatus(com.cookedspecially.enums.check.Status.Unpaid);
						}
						logger.info(" edit order status "+status);
						targetOrder.setStatus(status);
						if("Paid".equalsIgnoreCase(paidStatus)){
							if(PaymentMode.PAYTM_PENDING.toString().equalsIgnoreCase(targetOrder.getPaymentStatus())){
								targetOrder.setPaymentStatus(PaymentMode.PAYTM.toString());
							}else{
								targetOrder.setPaymentStatus("PG");
							}
						}
						targetOrder.setDeliveryAgent(deliveryBoy);
						targetOrder.setMoneyIn(moneyIn);
						targetOrder.setMoneyOut(moneyOut);
						targetOrder.setMicroKitchenId(microId);
						
					}else{
						targetOrder.setStatus(Status.NEW);
					}
			logger.info("15>Setting order status :"+Status.NEW);
			Float addOnBill =0.0f;
			Float bill = 0.0f;
			HashMap<String, OrderDish> orderDishMap = new HashMap<String, OrderDish>();
			String stockAlert=null;
			customer = customerService.getCustomer(check.getCustomerId());
			if(customer.getDeliveryArea()!=null){
				List<DeliveryArea>  deliveryArea  = deliveryAreaService.listDeliveryAreasByResaurant(restaurantId);
				for(DeliveryArea  area : deliveryArea ){
					if(customer.getDeliveryArea().equals(area.getName())){
						check.setKitchenScreenId(area.getFulfillmentCenterId());
						logger.info("setting delivery Screen for :"+area.getName()+" kitchen Screen Id :"+area.getFulfillmentCenterId());
					}
				}
				}
			if(orderSource == null || "".equals(orderSource)){
				check.setOrderSource("Website");
			}
			else {
				check.setOrderSource(orderSource);
			}
			check.setCheckType(checkType);
			int dishCount=0;
			String dishName="";
			String dish2="";
			boolean stockSize=true;
			int stockSizeCount=0;
			float dishPrice = 0.0f;
				for (JsonDish jsonDish  : jsonDishes) {
				int dishSize=0;
				if(stockSize){
					String updatedId = null;
				    if(jsonDish.getDishSizeId()!=null){
				    	updatedId = jsonDish.getId()+""+jsonDish.getDishSizeId();
				    }
				List<StockManagement> stockList = stockManagementService.listStockedDishbyDishId(updatedId==null?jsonDish.getId():Integer.parseInt(updatedId), check.getKitchenScreenId());
				stockSizeCount = getStockQuantity(stockList,check,request,jsonDish);
				stockSize=false;
				}
				dishSize=0;
				
				for(JsonDish jsonDishCount  : jsonDishes){
						if(jsonDish.getName().equalsIgnoreCase(jsonDishCount.getName()))
							dishSize++; 
					}
					dish2=dishName;
					dishName = jsonDish.getName();
					float booster = 0.0f; 
					Dish dish = dishService.getDish(jsonDish.getId());
					if(dish !=null){
						stockAlert = updateDishStock(jsonDish,check,request,Math.abs(stockSizeCount-dishSize));
						//if(stockAlert.equalsIgnoreCase("stockOver")){
								//orderResp.setError("Sorry! "+jsonDish.getName()+"  is out of Stock");
								//orderResp.setStatus("Failed");
								//if(dishCount!=0)
								//restoreDishStock(jsonDish.getId(),check.getKitchenScreenId(),dishCount);
								//return orderResp;
						//}
						if(dishName.equalsIgnoreCase(dish2)){
							dishCount++;
						}else{
							dishCount=0;
							dishCount++;
						}
						 restaurant =  restaurantServices.getRestaurant(check.getRestaurantId());
						List<com.cookedspecially.domain.OrderSource> os =  restaurantServices.listOrderSourcesByOrgId(restaurant.getParentRestaurantId());
						for(com.cookedspecially.domain.OrderSource orderS :os){
							if(orderS.getName().equalsIgnoreCase(check.getOrderSource())){
								booster =orderS.getPointbooster();
							}
						}
						
					List<NutrientInfo> nutrientInfo =  dish.getNutrientInfo();
					for(NutrientInfo info : nutrientInfo ){
						if(rewardBase.equalsIgnoreCase(info.getName())){
							try{
							caloriesCount +=(info.getValue()*jsonDish.getQuantity())*booster;
						}catch(Exception e ){
							e.printStackTrace();
							restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
							logger.info("Exception mail sent");
						}
					}
					}
				int microId = dish.getMicroScreen();
				if(microId == fixSceenId){
					addOnBill=0.0f;
					StringBuffer addOnId  =  new StringBuffer();
					addOnId.append("");
					String instStr ="";
					if(jsonDish.getInstructions()!=null){
					instStr = jsonDish.getInstructions().replaceAll("\\s+","");
					}
					if(jsonDish.getAddOns()!=null){
					for(JsonAddOn jsonAddon :  jsonDish.getAddOns()){
						addOnId.append(jsonAddon.getDishId());
					}
					}
				if (orderDishMap.get(jsonDish.getId()+""+jsonDish.getDishSizeId()+""+instStr+""+addOnId) != null) {
					orderDishMap.get(jsonDish.getId()+""+jsonDish.getDishSizeId()+""+instStr+""+addOnId).addMore(jsonDish.getQuantity());
				}
				else {
					HashMap<String, OrderAddOn> orderAddOnDishMap = new HashMap<String, OrderAddOn>();
					OrderDish orderDish = new OrderDish();
					List<JsonAddOn> jsonAddOns = jsonDish.getAddOns();
					
					if(jsonAddOns!=null){
					float addOnPrice=0.0f;
					for(JsonAddOn jsonAddOn :jsonAddOns){
						OrderAddOn orderAddOn = new OrderAddOn();
						AddOnDish addon =  addOnDishService.getDish(jsonAddOn.getId());
						if(addon !=null){
							List<AddOnNutrientInfo> addOnNutrientInfo =  addon.getNutritionalInfo();
							for(AddOnNutrientInfo info : addOnNutrientInfo ){
								if(rewardBase.equalsIgnoreCase(info.getName())){
									try{
									caloriesCount +=(info.getValue()*jsonAddOn.getQuantity())*booster;
								}catch(Exception e ){
									e.printStackTrace();
									restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
									logger.info("Exception mail sent");
								}
							}
							}	
						orderAddOn.setAddOnId(jsonAddOn.getId());
						orderAddOn.setDishId(jsonDish.getId());
						orderAddOn.setName(addon.getName());
						orderAddOn.setPrice(addon.getPrice());
						orderAddOn.setQuantity(jsonAddOn.getQuantity());
						orderAddOn.setSmallImageUrl(addon.getImageUrl());
						orderAddOn.setDishType(addon.getDishType());
						orderAddOnDishMap.put(jsonAddOn.getId()+""+jsonAddOn.getDishSizeId(), orderAddOn);
							if(jsonAddOn.getDishSizeId()==null){
								addOnPrice = addon.getPrice();
								logger.info(jsonDish.getName() +" - AddOn Dish size :  Default "+addOnPrice );
								orderAddOn.setPrice(addOnPrice);
								orderAddOn.setDishSize("");
							}else{
								AddOnDish_Size dishSz = dishTypeService.getAddOnDish_Size(jsonAddOn.getDishSizeId(),jsonAddOn.getId());
								addOnPrice = dishSz.getPrice();
								logger.info(jsonDish.getName() +" - AddOn Dish size :"+addOnPrice );
								orderAddOn.setPrice(addOnPrice);
								orderAddOn.setDishSize(dishSz.getName());
							}
						addOnBill+=addOnPrice*jsonAddOn.getQuantity();	
						}
					}
					}
					orderDish.setDishId(jsonDish.getId());
					orderDish.setQuantity(jsonDish.getQuantity());
					orderDish.setName(jsonDish.getName());
					 if(jsonDish.getDishSizeId()!=null){
							Dish_Size dishSz =  dishTypeService.getDish_Size(jsonDish.getDishSizeId(),jsonDish.getId());
							//logger.info(jsonDish.getName() +" - size : "+dishSz.getName());
							if(dishSz!=null){
							dishPrice = dishSz.getPrice();
							orderDish.setPrice(dishPrice);
							orderDish.setDishSize(dishSz.getName());
							}
							else{
								dishPrice = dish.getPriceByHappyHour(restaurant.getTimeZone());
								orderDish.setPrice(dishPrice);
								orderDish.setDishSize("");
							}
						}else {
							logger.info(jsonDish.getName() +" - Dish size :  Default" );
							dishPrice = dish.getPriceByHappyHour(restaurant.getTimeZone());
							orderDish.setPrice(dishPrice);
							orderDish.setDishSize("");
						}
					orderDish.setDishType(StringUtility.isNullOrEmpty(jsonDish.getDishType())?dish.getDishType():jsonDish.getDishType());
					orderDish.setInstructions(jsonDish.getInstructions());
					orderDish.setOrderAddOn(new ArrayList<OrderAddOn>(orderAddOnDishMap.values()));
					logger.info("16>Set instructions to order :"+jsonDish.getInstructions());
					orderDishMap.put(orderDish.getDishId()+""+jsonDish.getDishSizeId()+""+instStr+""+addOnId, orderDish);
				}
				bill += (dishPrice*jsonDish.getQuantity())+addOnBill;
			}
			}
			}
			targetOrder.setBill(bill);
			if (orderDishMap.size() > 0) {
				targetOrder.setOrderDishes(new ArrayList<OrderDish>(orderDishMap.values()));
				//targetOrder.setOrderAddOn(new ArrayList<OrderAddOn>(orderAddOnDishMap.values()));
			}
			//orderService.addOrder(targetOrder); Unpaid
			
			if (check != null) {
				logger.debug("17>If check status is !=null then");
				List<Order> orders = check.getOrders();
				orders.add(targetOrder);
				Float checkBill = check.getBill() + targetOrder.getBill();
				
				check.setBill(checkBill);
				check.setRewards(caloriesCount);
				customer.setRewardPoints(customer.getRewardPoints()+caloriesCount);
				// rest = restService.getRestaurant(check.getRestaurantId());
				logger.info("18>Resturant Id :"+restaurant.getRestaurantId());
				
				check.setModifiedTime(new Date());
				if(check.getTableId()<0){
				String firstName=customer.getFirstName();
				String lastName=customer.getLastName();
				String deliveryTime=customer.getDeliveryTime();
				String deliveryAddress = customer.getAddress();
				
				if(order.getCheckType()!=null){
					check.setCheckType(order.getCheckType());
				}
				//validating Delivery Area
				if(check.getCheckType()!=CheckType.TakeAway){
					check.setDeliveryAddress(deliveryAddress);
					List<DeliveryArea> deliveryAareList =  deliveryAreaService.listDeliveryAreasByResaurant(check.getRestaurantId());
					for(DeliveryArea deliveryArea :deliveryAareList ){
						if(deliveryArea.getName().equalsIgnoreCase(customer.getDeliveryArea())){
							check.setDeliveryArea(customer.getDeliveryArea());
					}
				}
				if(check.getDeliveryArea()==null){
					orderResp.setError("Delivery Area is missing !");
					orderResp.setStatus("Failed");
					return orderResp;
				}
				}
				logger.info("19>Setting values to Check table (avoiding race condition)");
				logger.info("Customer Name :"+firstName +" "+lastName);
				logger.info("Delivery Time :"+deliveryTime);
				logger.info("Delivery Address :"+deliveryAddress);
				check.setName(firstName+" "+lastName);
				check.setDeliveryInst(deliveryInst);
				if("Paid".equalsIgnoreCase(paidStatus)){
					check.setStatus(com.cookedspecially.enums.check.Status.Paid);
				}
				
				if(!editedOrder){
					if(deliveryTime!=null){
						 validateDeliveryTime(customer,check, deliveryTime);
					}
					else{
						logger.info(" wrong delivery time :"+deliveryTime);
						orderResp.setStatus("Failure");
						orderResp.setError("Delivery time is not valid !");
						return orderResp;
					}
				}
				else {
					logger.info("edited order from POS: "+check.getCheckId()+"/"+check.getInvoiceId());
					String format ="MM-dd-yyyy HH:mm";
					SimpleDateFormat formatterD = new SimpleDateFormat(format);
					TimeZone tzD =  TimeZone.getTimeZone(restaurant.getTimeZone());
					formatterD.setTimeZone(tzD);
					try{
					Date deliveryTimeD = formatterD.parse(deliveryTime);
					logger.info("Delivery Time updated : "+deliveryTimeD);
					check.setDeliveryTime(deliveryTimeD);
					}catch(Exception e){
						orderResp.setError("Incorrect delivery DateTime : "+deliveryTime +". Please try again with correct data.");
						orderResp.setStatus("Failure");
						restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
						logger.info("Exception mail sent");
						return orderResp;
					}
				}
				customerService.addCustomer(customer);
				logger.info("Delivery Area :"+customer.getDeliveryArea());
				check.setPhone(customer.getPhone());
				bill=check.getBill();
				logger.info("20>check database updated by customer :"+customer.getPhone());
			    List<DeliveryArea> deliveryAreas=deliveryAreaService.listDeliveryAreas();
			    if("Zero".equalsIgnoreCase((String)request.getAttribute("deliveryCharge"))){
			    	check.setOutCircleDeliveryCharges(0);
			    }
			    else if(check.getCheckType()!=CheckType.TakeAway){
			    for(DeliveryArea deliveryTo : deliveryAreas){
					if(deliveryTo.getName().equalsIgnoreCase(check.getDeliveryArea())){
							if(bill<deliveryTo.getMinDeliveryThreshold()){
							check.setOutCircleDeliveryCharges(deliveryTo.getDeliveryCharges());
							activeDeliveryAreaID=deliveryTo.getId();
							logger.info("21>Out circle delivery charges set :"+deliveryTo.getDeliveryCharges());
						}
					}
					}
			    }
				}
				check.setModifiedTime(new Date());
				checkService.addCheck(check);
				logger.info("22>Check added to database checkId/InvoiceId :"+check.getCheckId()+"/"+check.getInvoiceId());
				if (bFirstOrder) {
					Order orderToUpdate = check.getOrders().get(0);
					orderToUpdate.setCheckId(check.getCheckId());
					orderService.addOrder(orderToUpdate);
					targetOrder = orderToUpdate;		
				}
			}
		}
			if(targetOrder == null || targetOrder.getOrderId() == null) {
				orderResp.setStatus("Failed");
				orderResp.setError("No order was created");
			    logger.info("23>No order was created");
				} else {
				logger.info("23> else part where order has been created ");
				orderResp.setOrderId(targetOrder.getOrderId());
				check.setOrderId(targetOrder.getOrderId());
				//checkService.addCheck(check);
				if(check.getTableId() == null || check.getTableId() < 1) {
					orderResp.setTableId(0);
				} else {
					orderResp.setTableId(check.getTableId());
				}
				
				orderResp.setRestaurantId(restaurantId);
				orderResp.setCheckId(check.getCheckId());
				
				Float waiveOff =null;
				if(check.getOutCircleDeliveryCharges()==0){
					waiveOff = identifyWaiveOffCharges(check);
				}
				/* Coupon Management */
				List<Coupon> couponApplied = new ArrayList<Coupon>();
				logger.info(order.getCouponCode()+" : coupon code list  : ");
				if(order.getCouponCode()!=null && order.getCouponCode().size()>0){
					CheckResponse checkRespons =  new CheckResponse(check, taxTypeService,waiveOff,restaurant);
					//Logic for Coupon Management 
					check.setRoundOffTotal(checkRespons.getRoundedOffTotal());
					if(editedOrder){
						if(check.getCoupon_Applied()!=null && check.getCoupon_Applied().size()>0){
							List<Coupon> couponList =  new ArrayList<Coupon>();
							for(String coup: order.getCouponCode()){
								couponList.add(couponService.getEnabledCouponByCode(coup,check.getRestaurantId()));
							}
							check.setCoupon_Applied(couponList);
						}else if(check.getCoupon_Applied()==null || check.getCoupon_Applied().size()==0 ){
								couponApplied = validateCoupon(check,order.getCouponCode(),paymentStatus,activeDeliveryAreaID);
								if(couponApplied==null || couponApplied.size()==0 ){
										orderResp.setError("Either coupon has been expired or criteria doesn't match");
										orderResp.setStatus("Failed");
										orderResp.setCheckId(check.getCheckId());
										return orderResp;
								}else{
									check.setCoupon_Applied(couponApplied);
								}
							}
					}
					else{
						couponApplied = validateCoupon(check,order.getCouponCode(),paymentStatus,activeDeliveryAreaID);
						if(couponApplied==null || couponApplied.size()==0 ){
							orderResp.setError("Either coupon has been expired or criteria doesn't match");
							orderResp.setStatus("Failed");
							orderResp.setCheckId(check.getCheckId());
							return orderResp;
						}else{
							check.setCoupon_Applied(couponApplied);
						}
					}
				}else if(editedOrder && order.getCouponCode()==null && check.getCoupon_Applied()!=null){
						check.setCoupon_Applied(couponApplied);
				}
				CheckResponse checkRespons =  new CheckResponse(check, taxTypeService,waiveOff,restaurant);

				SortedMap<String,Float> val =  checkRespons.getTaxDetails();
				 Set<String> keys = val.keySet();	
				 JSONObject obj = new JSONObject();
				 StringWriter out = new StringWriter();
				 
				for(String iterate : keys){
					if(iterate!=null){
					TaxType taxType =  taxTypeService.getTaxTypeByName(iterate,check.getRestaurantId());
					if(taxType!=null){
					try {
						obj.put(taxType.getTaxTypeId().toString(), val.get(iterate));
					} catch (JSONException e) {
						e.printStackTrace();
						restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
						logger.info("Exception mail sent");
					}
					}
					}
				}
				
				if(restaurant!=null){
					if(restaurant.isRoundOffAmount()){
						check.setRoundOffTotal(Math.round(checkRespons.getRoundedOffTotal()));
						checkRespons.setRoundedOffTotal(Math.round(checkRespons.getRoundedOffTotal()));
					}else{
				    	check.setRoundOffTotal(checkRespons.getRoundedOffTotal());
					}
					}
				
			    check.setTaxJsonObject(obj.toString());
				logger.info("setting json tax object"+checkRespons.getRoundedOffTotal());
				logger.info("setting Invoice rounndOffTotal."+checkRespons.getRoundedOffTotal());
				if(restaurant!=null){
					if(restaurant.isRoundOffAmount()){
						check.setRoundOffTotal(Math.round(checkRespons.getRoundedOffTotal()));
						checkRespons.setRoundedOffTotal(Math.round(checkRespons.getRoundedOffTotal()));
					}else{
				    	check.setRoundOffTotal(checkRespons.getRoundedOffTotal());
					}
				}
				if(restaurant.getServiceTaxText()!=null && (!restaurant.getServiceTaxText().equalsIgnoreCase(""))){
					check.setAdditionalChargesName1(restaurant.getServiceTaxText());
					check.setAdditionalChargesValue1(restaurant.getServiceTaxValue());
				}
				/**
				 *@comment= Here we are validating customer's CREDIT Account for new order. 
				 */
				if(!editedOrder){
					if("CUSTOMER CREDIT".equalsIgnoreCase(targetOrder.getPaymentStatus())){
						Restaurant org =  restaurantServices.getRestaurant(restaurant.getParentRestaurantId());
						if(org.isEnableCustCredit()){
							orderResp = validateCustomerCredit(targetOrder,check,customer);
						if("Failure".equalsIgnoreCase(orderResp.getStatus())){
							checkService.removeCheck(check.getCheckId());
							return orderResp;
						}
						}
					}
				else {
					if(customer.getCredit()!=null){
						if(customer.getCredit().getStatus()==CustomerCreditAccountStatus.ACTIVE && 
							(check.getOrderSource().equalsIgnoreCase(OrderSource.Website.toString())
							|| check.getOrderSource().equalsIgnoreCase(OrderSource.IOS.toString())
							|| check.getOrderSource().equalsIgnoreCase(OrderSource.Android.toString())
							|| check.getOrderSource().equalsIgnoreCase(OrderSource.POS.toString()))){
							if(customer.getCredit().getCreditBalance()<0){
								if(Math.abs(customer.getCredit().getCreditBalance())<=check.getRoundOffTotal()){
                                    boolean saveData = customerCreditService.createBillRecoveryTransaction(customer.getCustomerId(), "CREDIT", check.getInvoiceId(), customer.getCredit().getCreditBalance(), "Creating pending transaction");
                                    if(saveData){
										check.setCreditBalance(customer.getCredit().getCreditBalance());
									}
								 }else  {
                                    boolean saveData = customerCreditService.createBillRecoveryTransaction(customer.getCustomerId(), "CREDIT", check.getInvoiceId(), (float) check.getRoundOffTotal(), "Creating pending transaction");
                                    if(saveData){
											check.setCreditBalance(-(float)check.getRoundOffTotal());
										}
                                    }
							}
							else if(customer.getCredit().getCreditBalance()>0){
								if(customer.getCredit().getCreditType().getBillingCycle()==BilligCycle.ONE_OFF){
                                    boolean saveData = customerCreditService.createBillRecoveryTransaction(customer.getCustomerId(), "CREDIT", check.getInvoiceId(), customer.getCredit().getCreditBalance(), "Creating pending transaction");
                                    if(saveData){
										check.setCreditBalance(customer.getCredit().getCreditBalance());
									}
								}
							}
						}
					}
					}
					}
				
				String lastPaymentType = (String) request.getAttribute("editOrderPaymentStatus");
				float amountCD=0.0f;
				String update=null;
				String reason=null;
				ResponseDTO responseDTO =null;
				/*Condition where current payment  type is CUSTOMER CREDIT and calculating things for edited and non edited orders **/
				if(targetOrder.getPaymentStatus().equalsIgnoreCase("CUSTOMER CREDIT")){
					 amountCD =0.0f; 
					if(customer.getCredit()==null && !editedOrder){/*if order is not edited and customer don't have CC account*/
						 responseDTO =  openDefaultCreditAccount(restaurant.getParentRestaurantId(),check,TransactionCategory.DEBIT,null);
						 if(customer.getCredit().getCreditType().getBillingCycle()==BilligCycle.ONE_OFF){
						 reason="ADDED";
						 amountCD=(float)check.getRoundOffTotal();
						 }
					}else if(customer.getCredit()!=null && !editedOrder){ /*if order is not edited and customer have CC account*/
						amountCD = (float)check.getRoundOffTotal();
						responseDTO = creditDebitAmountToAccount(customer,check,restaurant.getParentRestaurantId(),TransactionCategory.DEBIT,amountCD,true);
						if(customer.getCredit().getCreditType().getBillingCycle()==BilligCycle.ONE_OFF){
							 reason="ADDED";
							 amountCD=(float)check.getRoundOffTotal();
						}
					} else if(editedOrder){/*If order is edited*/
						Float lastcheckBill = (Float)request.getAttribute("editCheckBill");
						if(check.getLastInvoiceAmount()==0){
							check.setLastInvoiceAmount(lastcheckBill);
						}
						if(lastcheckBill !=null){ 
							if(lastPaymentType.equalsIgnoreCase("CUSTOMER CREDIT")){/*If last and current payment type is  CUSTOMER CREDIT*/
								if(lastcheckBill>(float)check.getRoundOffTotal()){
									amountCD = lastcheckBill-(float)check.getRoundOffTotal();
							        responseDTO = creditDebitAmountToAccount(customer,check,restaurant.getParentRestaurantId(),TransactionCategory.CREDIT,amountCD,true);
							        if(customer.getCredit().getCreditType().getBillingCycle()==BilligCycle.ONE_OFF){
										 update="UPDATE";
							        	 reason="ADDED";
							        }
								}else if(lastcheckBill<(float)check.getRoundOffTotal()){
									orderResp = validateCustomerCredit(targetOrder,check,customer);
									if("Failure".equalsIgnoreCase(orderResp.getStatus())){
										return orderResp;
									}else{
										amountCD = (float)check.getRoundOffTotal()-lastcheckBill;
										responseDTO = creditDebitAmountToAccount(customer,check,restaurant.getParentRestaurantId(),TransactionCategory.DEBIT,amountCD,true);
										if(customer.getCredit().getCreditType().getBillingCycle()==BilligCycle.ONE_OFF){
											 reason="CHARGED";
											 update="UPDATE";
										} 
									}
								}
						}else{ /*If last payment type is not CUSTOMR CREDIT*/
							if(customer.getCredit()==null){
								 responseDTO =  openDefaultCreditAccount(restaurant.getParentRestaurantId(),check,TransactionCategory.DEBIT,null);
								 customer = customerService.getCustomer(customer.getCustomerId());
								 if(customer.getCredit().getCreditType().getBillingCycle()==BilligCycle.ONE_OFF){
									 update="UPDATE";
									 reason="ADDED";
									 amountCD=(float)check.getRoundOffTotal();
								 }
							}else{
								orderResp = validateCustomerCredit(targetOrder,check,customer);
								if("Failure".equalsIgnoreCase(orderResp.getStatus())){
									//revertEditedOrder(check);
									return orderResp;
								}else{
								amountCD = (float)check.getRoundOffTotal();
								responseDTO = creditDebitAmountToAccount(customer,check,restaurant.getParentRestaurantId(),TransactionCategory.DEBIT,amountCD,true);
								if(customer.getCredit().getCreditType().getBillingCycle()==BilligCycle.ONE_OFF){
									update="UPDATE"; 
									reason="ADDED";
									 }
						}
						}
						}
						}
				}
				}
				/*Condition where last payment type was CUSTOMER CREDIT but current payment  type is not CUSTOMER CREDIT*/
				else if("CUSTOMER CREDIT".equalsIgnoreCase(lastPaymentType) && (!"CUSTOMER CREDIT".equalsIgnoreCase(targetOrder.getPaymentStatus()))){
					    Float checkBill = (Float)request.getAttribute("editCheckBill");
					    responseDTO = creditDebitAmountToAccount(customer,check,restaurant.getParentRestaurantId(),TransactionCategory.CREDIT,checkBill,true);
						 if(customer.getCredit().getCreditType().getBillingCycle()==BilligCycle.ONE_OFF){
							 update=lastPaymentType;
							 reason="PAYMENT_TYPE_CHANGED";
							 amountCD=0;
				 }
				}
				else if(editedOrder && targetOrder.getPaymentStatus()!=null){/*Credit Card calculation for all use cases except CUSTOMER CREDIT */
					//ResponseDTO responseDTO =null;
					Float lastcheckBill = (Float)request.getAttribute("editCheckBill");
					if(check.getLastInvoiceAmount()==0){
						check.setLastInvoiceAmount(lastcheckBill);
					}
					if(lastcheckBill>(float)check.getRoundOffTotal() && check.getStatus()== com.cookedspecially.enums.check.Status.Paid){
						amountCD = lastcheckBill-(float)check.getRoundOffTotal();
						if(customer.getCredit()==null){
							 responseDTO =  openDefaultCreditAccount(restaurant.getParentRestaurantId(),check,TransactionCategory.CREDIT,amountCD);
							 if(responseDTO.result.equalsIgnoreCase("SUCCESS")){
								 customer = customerService.getCustomer(customer.getCustomerId());
								 if(customer.getCredit().getCreditType().getBillingCycle()==BilligCycle.ONE_OFF){
									 update="UPDATE";
									 reason="ADDED";
									 //amountCD=(float)check.getRoundOffTotal();
								 }
								 
							 }
						}else{
							responseDTO = creditDebitAmountToAccount(customer,check,restaurant.getParentRestaurantId(),TransactionCategory.CREDIT,amountCD,true);
							if(customer.getCredit().getCreditType().getBillingCycle()==BilligCycle.ONE_OFF){
								 update="UPDATE";
								 reason="ADDED";
							 }
					}
					}else if(lastcheckBill<(float)check.getRoundOffTotal()&& check.getStatus()== com.cookedspecially.enums.check.Status.Paid){
						amountCD = (float)check.getRoundOffTotal()-lastcheckBill;
						if(customer.getCredit()==null){
							 responseDTO =  openDefaultCreditAccount(restaurant.getParentRestaurantId(),check,TransactionCategory.DEBIT,amountCD);
							 customer = customerService.getCustomer(customer.getCustomerId());
							 if(customer.getCredit().getCreditType().getBillingCycle()==BilligCycle.ONE_OFF){
								 update="UPDATE";
								 reason="CHARGED";
							 }
						}else{
							responseDTO = creditDebitAmountToAccount(customer,check,restaurant.getParentRestaurantId(),TransactionCategory.DEBIT,amountCD,true);
							if(customer.getCredit().getCreditType().getBillingCycle()==BilligCycle.ONE_OFF){
								 update="UPDATE";
								 reason="CHARGED";
							 } 
						}
					}
			}
			if(responseDTO!=null){
				if("ERROR".equalsIgnoreCase(responseDTO.result)){
						orderResp.setError(responseDTO.message);
						orderResp.setStatus("Failed");
						orderResp.setCheckId(check.getCheckId());
						return orderResp;
					}
			}
				logger.info("status :"+paymentStatus);
				//*** here I have added a piece of code for UPDATE transaction 
				if(editedOrder){
					Float lastorderBill = (Float)request.getAttribute("editBill");
					Float lastInvoiceBill = (Float)request.getAttribute("editCheckBill");
					if(lastInvoiceBill >0){
						if(check.getLastInvoiceAmount()==0){
							check.setLastInvoiceAmount(lastInvoiceBill);
						}
					}
					if(targetOrder.getStatus()==Status.OUTDELIVERY || targetOrder.getStatus()==Status.DELIVERED){
						TransactionDTO tillList  = cashRegisterService.fetchTransactionsByCheck(check.getRestaurantId(),check.getCheckId());
						String tillId = tillList.tillDetails.tillId;
					 	Float checkBill = (Float)request.getAttribute("editCheckBill");
					 	
					 if(allowDiscount || checkBill>check.getRoundOffTotal()){
						 	update="UPDATE"; 
							reason="DISCOUNT";
							amountCD=(float) (Math.abs(checkBill) - Math.abs(check.getRoundOffTotal()));
						 	cashRegisterService.applyDiscount((Integer)request.getSession().getAttribute("userId"),(float)(Math.abs(checkBill) - Math.abs(check.getRoundOffTotal())), check.getCheckId(), "giving discount",targetOrder.getPaymentStatus());
							try{
								emailCheckFromServer(request,check.getCheckId(),restaurant.getAlertMail(),"saladdaysemailbill","Special discount has been applied to this invoice. Status: "+targetOrder.getStatus().toString(),"cs",update,reason,amountCD);
							}catch(Exception e){
								logger.info("email not sent");
								restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
								logger.info("Exception mail sent");
							}
                         check.setEditOrderRemark("Special discount has been applied to this invoice: status: " + targetOrder.getStatus().toString() + " edited by :" + request.getSession().getAttribute("username"));
                     }
					if(lastorderBill!=null){
						String updatePaymentType = (String) request.getAttribute("editOrderPaymentStatus");
					if(lastorderBill!=targetOrder.getBill()){
						logger.info(updatePaymentType+"---"+tillId +"creating transaction for edited order with updated bill :"+targetOrder.getPaymentStatus());
						try {
						// updateCash(TillTransaction.UPDATE.toString(),(float)check.getRoundOffTotal(), check, request,false,updatePaymentType, "Edit order transaction checkId="+check.getCheckId(),tillId,targetOrder.getPaymentStatus());
						 List<PaymentType> paymentList = restaurantServices.listPaymentTypeByOrgId(restaurant.getParentRestaurantId());
			             if(!(targetOrder.getPaymentStatus().equalsIgnoreCase(updatePaymentType))){
						 if(check.getCreditBalance()>0){
			                for(PaymentType pt : paymentList){
								if(pt.getName().equalsIgnoreCase(targetOrder.getPaymentStatus())){
									if(pt.getType().equalsIgnoreCase(BasePaymentType.CASH.toString())){
										updateCash(TillTransaction.UPDATE.toString(), (float) check.getCreditBalance(), check, request,true,updatePaymentType, "Edit order transaction checkId=" + check.getCheckId(), tillId,targetOrder.getPaymentStatus());
									}
									else if(pt.getType().equalsIgnoreCase(BasePaymentType.CREDIT.toString())&& !("CUSTOMER CREDIT".equalsIgnoreCase(targetOrder.getPaymentStatus()))){
										updateCash(TillTransaction.UPDATE.toString(), (float) check.getCreditBalance(), check, request,true,updatePaymentType, "Edit order transaction checkId=" + check.getCheckId(), tillId, targetOrder.getPaymentStatus());
									}else if("CUSTOMER CREDIT".equalsIgnoreCase(targetOrder.getPaymentStatus())){
										updateCash(TillTransaction.CANCEL.toString(), (float) check.getCreditBalance(), check, request,true,updatePaymentType, " Order with orderId=" + targetOrder.getOrderId(), tillId, targetOrder.getPaymentStatus());
										customerCreditService.updateBillRecoveryTransaction("FAILED",customer.getCustomerId(), check.getCreditBalance(), "CREDIT", "Setting Status Failed");
										check.setCreditBalance(0);

									}
								}
								}
			               }
			             }
							check.setEditOrderRemark("This order has been edited. Status: " + targetOrder.getStatus().toString() + " edited by :" + request.getSession().getAttribute("username"));
                            emailCheckFromServer(request,check.getCheckId(),restaurant.getAlertMail(),"saladdaysemailbill","This order has been edited. Status: "+targetOrder.getStatus().toString(),"cs",update,reason,amountCD);
						} catch (Exception e) {
							logger.info("email not sent");
							restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
							logger.info("Exception mail sent");
						}
						}else if(!(targetOrder.getPaymentStatus().equalsIgnoreCase(updatePaymentType))){
							updateCash(TillTransaction.UPDATE.toString(),(float)check.getRoundOffTotal(), check, request,false,updatePaymentType, "Edit order transaction checkId="+check.getCheckId(),tillId,targetOrder.getPaymentStatus());
							List<PaymentType> paymentList = restaurantServices.listPaymentTypeByOrgId(restaurant.getParentRestaurantId());
							 if(check.getCreditBalance()>0){
					                for(PaymentType pt : paymentList){
										if(pt.getName().equalsIgnoreCase(targetOrder.getPaymentStatus())){
											if(pt.getType().equalsIgnoreCase(BasePaymentType.CASH.toString())){
												updateCash(TillTransaction.UPDATE.toString(), (float) check.getCreditBalance(), check, request,true,updatePaymentType, "Edit order transaction checkId=" + check.getCheckId(), tillId,targetOrder.getPaymentStatus());
											}else if(pt.getType().equalsIgnoreCase(BasePaymentType.CREDIT.toString())&& !("CUSTOMER CREDIT".equalsIgnoreCase(targetOrder.getPaymentStatus()))){
												updateCash(TillTransaction.UPDATE.toString(), (float) check.getCreditBalance(), check, request,true,updatePaymentType, "Edit order transaction checkId=" + check.getCheckId(), tillId,targetOrder.getPaymentStatus());
											}else if("CUSTOMER CREDIT".equalsIgnoreCase(targetOrder.getPaymentStatus())){
												updateCash(TillTransaction.CANCEL.toString(), (float) check.getCreditBalance(), check, request,true,updatePaymentType, " Order with orderId=" + targetOrder.getOrderId(), tillId, targetOrder.getPaymentStatus());
												customerCreditService.updateBillRecoveryTransaction("FAILED",customer.getCustomerId(), check.getCreditBalance(), "CREDIT", "Setting Status Failed");
												check.setCreditBalance(0);

											}
										}
										}
					               }
					             
								try{
									check.setEditOrderRemark("This order has been edited. Status: " + targetOrder.getStatus().toString() + " edited by :" + request.getSession().getAttribute("username"));
		                            emailCheckFromServer(request,check.getCheckId(),restaurant.getAlertMail(),"saladdaysemailbill","This order has been edited. Status: "+targetOrder.getStatus().toString(),"cs",update,reason,amountCD);
								} catch (Exception e) {
									logger.info("email not sent");
									restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
									logger.info("Exception mail sent");
								}
						}
					}
					}else{
                        check.setEditOrderRemark("This order has been edited. Status: " + targetOrder.getStatus().toString() + " edited by :" + request.getSession().getAttribute("username"));
					}
				}
				request.setAttribute("phone",check.getPhone());
				request.setAttribute("orgId",customer.getOrgId().toString());
				request.setAttribute("orderLimit", "1");
				OrderHistory orderHistory =  customerController.getLatestOrders(request,check.getPhone(),customer.getCustomerId().toString(),
																				customer.getOrgId().toString(),"1","false");
				if(orderHistory !=null){
					if(orderHistory.totalOrders==1){
						check.setFirstOrder(true);
						//checkService.addCheck(check);
					}
				}
				
				CheckResponse checkResponse =  new CheckResponse(check, taxTypeService,waiveOff,restaurant);
				checkService.addCheck(check);
				if(paymentStatus.equalsIgnoreCase("Online")){
				orderResp.setStatus("Online");
				logger.info("23>setting status Online ref.. Success");
				}else  if(paymentStatus.equalsIgnoreCase("MobiKwikWallet")){
					orderResp.setStatus("MobiKwikWallet");
				}else  if(paymentStatus.equals("payTm")){
					orderResp.setStatus("payTm");
				}else if(paymentStatus.equalsIgnoreCase("OnlinePayPal")){
					orderResp.setStatus("OnlinePayPal");
				}
				else if(paymentStatus.equalsIgnoreCase("Subscription")){
					orderResp.setStatus("COD");
					if(check.getTableId()<0){
					logger.info("email sent to  check Id/InvoiceId :"+check.getCheckId()+"/"+check.getInvoiceId()+"  Email Address"+customer.getEmail());
			       try{
			    	   if(emailbill!=null){
			    		   String value=  emailCheckFromServer(request,check.getCheckId(),customer.getEmail(),emailbill,null,null,update,reason,amountCD);
			    		   logger.info("email invoice of  :"+ emailbill +"  value : "+value); 
			    	   }
			    	   else{
							String value=  emailCheckFromServer(request,check.getCheckId(),customer.getEmail(),"saladdaysemailbill",( editedOrder== true  ? "Salad Days : Edited Order" : null),null,update,reason,amountCD);
							logger.info("email invoice of  saladdays "+value); 	   
			    	   }
			    	   }
			       catch(Exception e){
			    	   logger.info("Email sent fail");
			    	   e.printStackTrace();
			    	   restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
					   logger.info("Exception mail sent");
			       }
				}
					
					logger.info("23>SUBSCRIPTION Success");
				}
				else{
					orderResp.setStatus("COD");
					if(check.getTableId()<0){
					logger.info("email sent to  check Id :"+check.getCheckId()+"/"+check.getInvoiceId()+"  Email Address"+customer.getEmail());
			       try{
			    	   if(emailbill!=null){
			    		   String value=  emailCheckFromServer(request,check.getCheckId(),customer.getEmail(),emailbill,null,null,update,reason,amountCD);
			    		   logger.info("email invoice of  :"+ emailbill +"  value : "+value); 
			    	   }
			    	   else{
							String value=  emailCheckFromServer(request,check.getCheckId(),customer.getEmail(),"saladdaysemailbill",( editedOrder== true  ? "Salad Days : Edited Order" : null),null,update,reason,amountCD);
							logger.info("email invoice of  saladdays "+value); 	   
			    	   }
			    	   }
			       catch(Exception e){
			    	   logger.info("Email sent fail");
			    	   e.printStackTrace();
			    	   try{
				    	   restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
				    	   logger.info("Exception mail sent");
			    	   }catch(Exception e1){
			    		   logger.info("Exception mail sent failed");
			    	   }
					  
			       }
				}
					logger.info("23>COD Success");
				}
			}
		}
		else {
			orderResp.setStatus("Failed");
			logger.info("23>setting status Failed");
			orderResp.setError(error);
		}
		return orderResp;
	}
	
	public OrderResponse validateCustomerCredit(Order targetOrder,Check check, Customer customer){
		OrderResponse orderResp = new OrderResponse();
		if("CUSTOMER CREDIT".equalsIgnoreCase(targetOrder.getPaymentStatus())){
			if(customer.getCredit()!=null){
				if(customer.getCredit().getStatus()==CustomerCreditAccountStatus.ACTIVE){
					if(customer.getCredit().getCreditBalance() + check.getRoundOffTotal() > customer.getCredit().getMaxLimit()){
						orderResp.setStatus("Failed");
						logger.info("23>setting status Failed");
						orderResp.setError("Your Customer Credit limit is over. Remaining amount is :"+(customer.getCredit().getMaxLimit() - customer.getCredit().getCreditBalance()) +" and your bill amount is: "+check.getRoundOffTotal());
						return orderResp;
						
					}else {
						orderResp.setStatus("Success");
						return orderResp;
					}
				}
			}
		}
		return orderResp;
	}
	
	public ResponseDTO openDefaultCreditAccount(Integer orgId,Check check, TransactionCategory transactionType,Float withDefaultAmount){
		Restaurant org =  restService.getRestaurant(orgId);
		ResponseDTO responseDto = null;
		if(org.isEnableCustCredit()){
			CreditType creditType =	customerService.getCreditType(org.getDefaultCreditType());
			if(creditType!=null){
				CustomerCreditDTO customerCreditDTO =  new CustomerCreditDTO();
				customerCreditDTO.creditBalance=0;
				customerCreditDTO.creditTypeId=creditType.getId();
				customerCreditDTO.customerId=check.getCustomerId();
				customerCreditDTO.maxLimit=creditType.getMaxLimit();
				customerCreditDTO.ffcId=check.getKitchenScreenId();
				customerCreditDTO.billingAddress="";
			try {
				responseDto = customerService.enableCustomerCredit(customerCreditDTO,orgId);
				
				if(responseDto.result.equalsIgnoreCase("SUCCESS")){
					logger.info("Account has been created  status: "+responseDto.result);
					AddCreditToCustomerAccountDTO creditAddDTO = new AddCreditToCustomerAccountDTO();
					creditAddDTO.customerId=check.getCustomerId();
                    creditAddDTO.invoiceId = check.getInvoiceId();
                    if(withDefaultAmount!=null){
                    	creditAddDTO.amount=withDefaultAmount;
                    }else{
                    	creditAddDTO.amount=(float)check.getRoundOffTotal();
                    }
                    creditAddDTO.orgId=orgId;
					creditAddDTO.remark="Created a default account "+creditType.getName()+" and crediting to it";
					creditAddDTO.transactionType=transactionType;
					responseDto = customerService.creatTransaction(creditAddDTO, orgId,check.getCheckId());
					logger.info(" Transaction status : "+responseDto.result);
				}else {
					logger.info("Account status: "+responseDto.result+" Message : "+responseDto.message);
				}
			} catch (Exception e) {
				logger.info("Failed to open Default account for customer ");
				try {
					restaurantServices.emailException(ExceptionUtils.getStackTrace(e),REQUEST);
				} catch (UnsupportedEncodingException | MessagingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				logger.info("Exception mail sent");
			}
		}
		}
		return responseDto;
	}
	
	public ResponseDTO creditDebitAmountToAccount(Customer customer,Check check,Integer orgId, TransactionCategory transactionType,float amount,boolean saveCreditAmount){
		Restaurant org =  restService.getRestaurant(orgId);
		ResponseDTO responseDto = null;
		if(org.isEnableCustCredit()){
			try {
					AddCreditToCustomerAccountDTO creditAddDTO = new AddCreditToCustomerAccountDTO();
					creditAddDTO.customerId=check.getCustomerId();
					creditAddDTO.invoiceId = check.getInvoiceId();
					creditAddDTO.amount=amount;
					creditAddDTO.orgId=orgId;
					creditAddDTO.remark= transactionType.toString()+" Amount from/to  "+customer.getCredit().getCreditType().getName();
					creditAddDTO.transactionType=transactionType;
					responseDto = customerService.creatTransaction(creditAddDTO, orgId,check.getCheckId());
					logger.info(" Transaction status : "+responseDto.result +" transaction message : "+responseDto.message);
					if("SUCCESS".equalsIgnoreCase(responseDto.result)){
						if(customer.getCredit().getCreditType().getBillingCycle()==BilligCycle.ONE_OFF){
						if(transactionType==TransactionCategory.DEBIT){
							//Customer cust =  customerService.getCustomer(customer.getCustomerId());
							if(customer.getCredit().getCreditBalance()<0){
								if(Math.abs(customer.getCredit().getCreditBalance())<=check.getRoundOffTotal()){
								//	check.setCreditBalance(customer.getCredit().getCreditBalance());
								}else if(Math.abs(customer.getCredit().getCreditBalance())>check.getRoundOffTotal()) {
									//check.setCreditBalance(-(float)check.getRoundOffTotal());
								}
							}else{
								if(saveCreditAmount){
								//check.setCreditBalance(cust.getCredit().getCreditBalance());
						}
						}
						}else if(transactionType==TransactionCategory.CREDIT) {
							//Customer cust =  customerService.getCustomer(customer.getCustomerId());
							//check.setCreditBalance(cust.getCredit().getCreditBalance());
						}
						//checkService.addCheck(check);
					}
					}
						
			} catch (Exception e) {
				logger.info("Exception arrived during  transaction");
				try {
					restaurantServices.emailException(ExceptionUtils.getStackTrace(e),REQUEST);
				} catch (UnsupportedEncodingException | MessagingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				logger.info("Exception mail sent");
			}
		}
		return responseDto;
	}
	
	@RequestMapping(value = "/getMonthBillSummary", produces = "application/json", method=RequestMethod.GET)
	public @ResponseBody Map<String, Double> getMonthlyBillSummary(HttpServletRequest request, HttpServletResponse response
			,@RequestParam String restaurantId, @RequestParam String startDate, @RequestParam String endDate) throws ParseException {
		Map<String, Double> billMap = new LinkedHashMap<String, Double>();
		Integer restaurantID = Integer.parseInt(restaurantId);
		//String startDateStr = request.getParameter("startDate");
		//String endDateStr = request.getParameter("endDate");
		//User user = userService.getUser(restaurantId);
		Restaurant rest=restService.getRestaurant(restaurantID);
		DateFormat formatter;
		formatter = new SimpleDateFormat("yyyy-MM-dd");
		formatter.setTimeZone(TimeZone.getTimeZone(rest.getTimeZone()));
		Date startDateFormat = formatter.parse(startDate);
		Date endDateFormat = formatter.parse(endDate);
		List result = checkService.getMonthlyBillSummary(restaurantID, startDateFormat, endDateFormat);
		Object[] monthlyBillSummary = (Object[])result.get(0);
		billMap.put("Total", (Double)monthlyBillSummary[0]);
		billMap.put(rest.getAdditionalChargesName1(), (Double)monthlyBillSummary[1]);
		billMap.put(rest.getAdditionalChargesName2(), (Double)monthlyBillSummary[2]);
		billMap.put(rest.getAdditionalChargesName3(), (Double)monthlyBillSummary[3]);
		billMap.put("Total incl of taxes", (Double)(monthlyBillSummary[0]) + (Double)(monthlyBillSummary[1]) + (Double)(monthlyBillSummary[2]) + (Double)(monthlyBillSummary[3]));
		return billMap;
	}

	@RequestMapping(value = "/getOrdersByType", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<OrderDTO> getOrdersForMicroKitchenByType(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required=false) String restaurantId,
			@RequestParam(required=false) String[] orderType,
			@RequestParam(required=false) String ordersOfDay) throws Exception{

		Integer restaurantID ;
		//String restIdStr = request.getParameter("restaurantId");

		if(restaurantId != null && !restaurantId.equals(""))
			restaurantID = Integer.parseInt(restaurantId);
		else if(request.getSession().getAttribute("restaurantId") != null)
			restaurantID = (Integer)request.getSession().getAttribute("restaurantId");
		else
			throw new Exception("RestaurantId not found!!");

		List<String> orderTypeList = Arrays.asList(orderType);
        //String ordersOfDay = request.getParameter("ordersOfDay");
        if(ordersOfDay == null || ordersOfDay == "")
            ordersOfDay = "Today";
		return orderService.getOrders(restaurantID,orderTypeList,ordersOfDay);
	}
	
	@RequestMapping(value = "/getDispatchedCancelOrders", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<OrderDTO> getDispatchedCancelOrders(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required=false) String restaurantId,
			@RequestParam(required=false) String[] orderType,
			@RequestParam(required=false) String ordersOfDay) throws Exception{

		Integer restaurantID ;
		//String restIdStr = request.getParameter("restaurantId");

		if(restaurantId != null && !restaurantId.equals(""))
			restaurantID = Integer.parseInt(restaurantId);
		else if(request.getSession().getAttribute("restaurantId") != null)
			restaurantID = (Integer)request.getSession().getAttribute("restaurantId");
		else
			throw new Exception("RestaurantId not found!!");

		List<String> orderTypeList = Arrays.asList(orderType);
        //String ordersOfDay = request.getParameter("ordersOfDay");
        if(ordersOfDay == null || ordersOfDay == "")
            ordersOfDay = "Today";
		return orderService.getDispatchedCancelOrders(restaurantID,orderTypeList,ordersOfDay);
	}

	
	@RequestMapping(value = "/placeOrderFromPos", method = RequestMethod.POST,consumes = "application/json", produces = "application/json")
	public @ResponseBody Map<String,Object> placeOrder(@RequestBody PlaceOrderDTO orderBody,Model model, HttpServletRequest request,
			HttpServletResponse response,@RequestParam(required=false) String restaurantId) throws Exception{
		Map<String, Object> m = new HashMap<String, Object>();
		try {
			Integer restaurantID ;
			//String restIdStr = request.getParameter("restaurantId");

			if(restaurantId != null && !restaurantId.equals(""))
				restaurantID = Integer.parseInt(restaurantId);
			else if(request.getSession().getAttribute("restaurantId") != null)
				restaurantID = (Integer)request.getSession().getAttribute("restaurantId");
			else
				throw new Exception("RestaurantId not found!!");

			Restaurant restaurant  =  restaurantServices.getRestaurant(restaurantID);
			
			Customer customer = getCustomer(orderBody);
			
			Customer cust =  customerService.getCustomer(customer.getCustomerId());
			Restaurant rest =  restService.getRestaurant(restaurant.getParentRestaurantId());
			if(rest.isEnableCustCredit() && "CUSTOMER CREDIT".equalsIgnoreCase(orderBody.order.paymentMethod)){
				if(cust.getCredit()!=null){
				if(cust.getCredit().getCreditType().getBillingCycle()==BilligCycle.ONE_OFF && cust.getCredit().getCreditBalance()>0){
					m.put("status", "error");
					m.put("error","You can't place CUSTOMER CREDIT order for CREDIT Type ONE_OFF");
					return m;
				}
				}
			}
			
			customer.setDeliveryTime(orderBody.order.deliveryDateTime);
			
			customer.setRestaurantId(restaurantID);

			if(restaurant != null)
				customer.setOrgId(restaurant.getParentRestaurantId());
			else
				customer.setOrgId(restaurantID);

			customerController.setCustomerInfoJSON(customer, model, request);

			request.setAttribute("restaurantId", restaurantId.toString());
			
			request.setAttribute("custId", customer.getCustomerId().toString());
			
			Check check = getCheckWithOrderJSON(request, response,null,customer.getCustomerId().toString(),restaurantId.toString());

			List<Order_DCList> discount_Charge = getDCValue(orderBody);
			if(discount_Charge!=null)
			check.setDiscount_Charge(discount_Charge);
			checkService.addCheck(check);
			
			JsonOrder jsonOrder = getJsonOrder(orderBody, customer, check);
			request.setAttribute("paymentStatus",orderBody.order.paymentMethod);
			
			request.setAttribute("paymentThirdParty", orderBody.order.paymentMethod);
			
			if(orderBody.order.deliveryCharges!=null && orderBody.order.deliveryCharges==0){
				request.setAttribute("deliveryCharge","Zero");
			}
			
			logger.info("order Source :"+ orderBody.order.orderSource);
			if(orderBody.order.orderSource == null || orderBody.order.orderSource.equals(""))
				request.setAttribute("orderSource", "Website");
			else {
				request.setAttribute("orderSource", orderBody.order.orderSource);
			}
			
			if(orderBody.order.couponCode!=null){
				jsonOrder.setCouponCode(orderBody.order.couponCode);
			}
			
			request.setAttribute("deliveryInst", orderBody.order.instructions);
			
			if(orderBody.order.orderType!=null){
				 request.setAttribute("type",orderBody.order.orderType);
				 System.out.println(orderBody.order.orderType);
				 jsonOrder.setCheckType(CheckType.valueOf(orderBody.order.orderType));
				}else{
					request.setAttribute("type","Delivery");
					jsonOrder.setCheckType(CheckType.Delivery);
			}
			
			OrderResponse orderResponse = addToCheckJSON(jsonOrder, model, request,response);

			
			
			request.setAttribute("checkId", check.getCheckId().toString());
			String res = setCheckType(request, response,check.getCheckId().toString(),jsonOrder.getCheckType().toString());
			if("Failed".equalsIgnoreCase(orderResponse.getStatus())){
				if(orderResponse.getCheckId()>0){
					checkService.removeCheck(orderResponse.getCheckId());
				}
				m.put("status","error");
				m.put("error",orderResponse.getError());
			}
			else {
				m.put("status","success");	
				m.put("checkId",check.getCheckId());
				m.put("paymentType",orderResponse.getStatus());
			}
			
		}catch (Exception e){
			response.setStatus(500);
			m.put("status", "error");
			m.put("error","Unknown 500 error occured");
			restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
			logger.info("Exception mail sent");
			e.printStackTrace();
		}
		return m;
	}
	 public Check copyCheck(Check check){
		 Check checks = new Check();
		 checks.setAllowEdit(check.isAllowEdit());
		 checks.setAdditionalChargesName1(check.getAdditionalChargesName1());
		 checks.setAdditionalChargesName2(check.getAdditionalChargesName2());
		 checks.setAdditionalChargesName3(check.getAdditionalChargesName3());
		 checks.setAdditionalChargesValue1(check.getAdditionalChargesValue1());
		 checks.setAdditionalChargesValue2(check.getAdditionalChargesValue2());
		 checks.setAdditionalChargesValue3(check.getAdditionalChargesValue3());
		 checks.setBill(check.getBill());
		 checks.setPhone(check.getPhone());
		 checks.setRestaurantId(check.getRestaurantId());
		 checks.setInvoiceId(check.getInvoiceId());
		 checks.setCheckId(check.getCheckId());
		 checks.setPayment(check.getPayment());
		 checks.setCheckType(check.getCheckType());
		 checks.setCloseTime(check.getCloseTime());
		 checks.setCustomerId(check.getCustomerId());
		 checks.setDeliveryAddress(check.getDeliveryAddress());
		 checks.setDeliveryArea(check.getDeliveryArea());
		 checks.setDeliveryInst(check.getDeliveryInst());
		 checks.setDeliveryTime(check.getDeliveryTime());
		 checks.setDiscount_Charge(check.getDiscount_Charge());
		 checks.setDiscountAmount(check.getDiscountAmount());
		 checks.setDiscountPercent(check.getDiscountPercent());
		 checks.setEditOrderRemark(check.getEditOrderRemark());
		 checks.setFirstOrder(check.isFirstOrder());
		 checks.setGuests(check.getGuests());
		 checks.setKitchenScreenId(check.getKitchenScreenId());
		 checks.setModifiedTime(check.getModifiedTime());
		 checks.setName(check.getName());
		 checks.setOpenTime(check.getOpenTime());
		 //checks.setOrderId(check.getOrderId());
		 checks.setOrderSource(check.getOrderSource());
		 checks.setOutCircleDeliveryCharges(check.getOutCircleDeliveryCharges());
		 checks.setPayment(check.getPayment());
		 checks.setPaymentDetail(check.getPaymentDetail());
		 checks.setPaymentType(check.getPaymentType());
		 checks.setPhone(check.getPhone());
		 checks.setResponseCode(check.getResponseCode());
		 checks.setRestaurantId(check.getRestaurantId());
		 checks.setRewards(check.getRewards());
		 checks.setRoundOffTotal(check.getRoundOffTotal());
		 checks.setStatus(check.getStatus());
		 checks.setTableId(check.getTableId());
		 checks.setTaxJsonObject(check.getTaxJsonObject());
		 checks.setTransactionId(check.getTransactionId());
		 checks.setTransactionStatus(check.getTransactionStatus());
		 checks.setUserId(check.getUserId());
		 return checks;
	 }
	@RequestMapping(value = "/editOrder", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public @ResponseBody Map<String,Object> editOrder(@RequestBody PlaceOrderDTO orderBody,Model model, 
			HttpServletRequest request, HttpServletResponse response,@RequestParam(required=false) String restaurantId) throws Exception{

		REQUEST = request;
		Check check = checkService.getCheck(Integer.parseInt(orderBody.order.checkId));
		Check tempCheck = new Check();
		double lastSavedTotal =check.getRoundOffTotal();
		Gson gson =  new Gson();
		if(check!=null){
			String json = gson.toJson(check);
			EditChecks editCheck =  new EditChecks();
				editCheck.setCheckId(check.getCheckId());
				editCheck.setCheckJson(json);
				editCheck.setOpenTime(check.getModifiedTime());
				checkService.addEditCheck(editCheck);
		}
		tempCheck = copyCheck(check);
		if(check == null){
			throw new Exception("Order doesn't already exists");
		}
		logger.info("Entered into EDIT POS");
		
		Map m = new HashMap();
		request.setAttribute("editOrderFlag",true);
		request.setAttribute("lastSavedTotal",lastSavedTotal);
		String username= (String)request.getSession().getAttribute("username");
		try {
			Integer restaurantID ;
			//String restIdStr = request.getParameter("restaurantId");

			if(restaurantId != null && !restaurantId.equals(""))
				restaurantID = Integer.parseInt(restaurantId);
			else if(request.getSession().getAttribute("restaurantId") != null)
				restaurantID = (Integer)request.getSession().getAttribute("restaurantId");
			else
				throw new Exception("RestaurantId not found!!");

			Restaurant restaurant  =  restaurantServices.getRestaurant(restaurantID);
			String role= (String)request.getSession().getAttribute("role");
			Order lastOrder =  orderService.getOrder(Integer.parseInt(orderBody.order.id));
			if(lastOrder.getStatus() == Status.OUTDELIVERY ){
				m.put("status","error");
				m.put("error","This order has been dispatched.So you can't make any change.Please contact Restaurant Manager");
				return m;
			}
			else if(lastOrder.getStatus() == Status.DELIVERED ){
				boolean allow=false;
				if(check.getOutCircleDeliveryCharges()>0 && orderBody.order.deliveryCharges==0){
					allow=true;
				}
				if(orderBody.order.discountAmount>0 || allow){
					request.setAttribute("allowDiscount",true);
				}
			}
			logger.info("Check id : "+orderBody.order.checkId+"/"+check.getInvoiceId()+ "  System user Role : "+role+" user :"+username);

			double calories  = removeCalories(check.getOrders(),restaurant);
			Customer customer = getCustomer(orderBody);
			customer.setDeliveryTime(orderBody.order.deliveryDateTime);
			customer.setRestaurantId(restaurantID);

			if(restaurant != null)
				customer.setOrgId(restaurant.getParentRestaurantId());
			else
				customer.setOrgId(restaurantID);

			customerController.setCustomerInfoJSON(customer, model, request);
			request.setAttribute("restaurantId", restaurantID.toString());
			request.setAttribute("custId", customer.getCustomerId().toString());

			List<Order_DCList> discount_Charge = getDCValue(orderBody);
			if(discount_Charge!=null)
			check.setDiscount_Charge(discount_Charge);

			//CheckType checkType = CheckType.valueOf(CheckType.class, "Delivery");
			//check.setCheckType(CheckType.Delivery);
			request.setAttribute("checkType","Delivery");
			check.setBill(0.0f);
			check.setRoundOffTotal(0);
			check.setRewards(Math.abs(check.getRewards()-calories));
			checkService.addCheck(check);
			Status status = lastOrder.getStatus();
			String deliveryBoy = lastOrder.getDeliveryAgent();
			request.setAttribute("editOrderDeliverBoy",deliveryBoy);
			request.setAttribute("editOrderMoneyIn",lastOrder.getMoneyIn());
			request.setAttribute("editOrderMoneyOut",lastOrder.getMoneyOut());
			request.setAttribute("editOrderMicroKitchen",lastOrder.getMicroKitchenId());
			request.setAttribute("editOrderPaymentStatus",lastOrder.getPaymentStatus());
			request.setAttribute("editBill",lastOrder.getBill());
			request.setAttribute("editCheckBill",(float)tempCheck.getRoundOffTotal());
			
			if(orderBody.order.paidStatus!=null){
				request.setAttribute("paidStatus","Paid");
			}
			//this.lastOrder=lastOrder;
			orderService.removeOrder(Integer.parseInt(orderBody.order.id));

			JsonOrder jsonOrder = getJsonOrder(orderBody, customer, check);
			request.setAttribute("editOrderStatus",status);
			request.setAttribute("paymentStatus",orderBody.order.paymentMethod);
			
			request.setAttribute("paymentThirdParty", orderBody.order.paymentMethod);
			if(orderBody.order.deliveryCharges==0){
				request.setAttribute("deliveryCharge","Zero");
			}
			if(orderBody.order.orderSource == null || orderBody.order.orderSource.equals(""))
				request.setAttribute("orderSource", "Website");
			else {
				request.setAttribute("orderSource", orderBody.order.orderSource);
			}

			request.setAttribute("deliveryInst", orderBody.order.instructions);
			if(orderBody.order.couponCode!=null){
				if(orderBody.order.couponCode.size()>0){
					jsonOrder.setCouponCode(orderBody.order.couponCode);
				}
			}
			OrderResponse orderResponse = addToCheckJSON(jsonOrder, model, request,response);
			if("Failed".equalsIgnoreCase(orderResponse.getStatus()) || "Failure".equalsIgnoreCase(orderResponse.getStatus())){
				revertEditedOrder(tempCheck,lastOrder);
								m.put("status","error");
								m.put("error",orderResponse.getError());
							}
							else {
								m.put("status","success");	
			}
		}catch (Exception e){
			response.setStatus(500);
			m.put("status", "error");
			restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
			logger.info("Exception mail sent");
			e.printStackTrace();
		}
		return m;
	}

	/**
	 *
     * @param tempCheck,
     * @param lastOrder
     * @return boolean
	 * @comment This method we use to revert order's updated due to any excaption. 
	 */
	public boolean revertEditedOrder(Check tempCheck,Order lastOrder){
		if(lastOrder!=null){
			Order order = new Order();
			order.setBill(lastOrder.getBill());
			order.setCheckId(lastOrder.getCheckId());
			order.setCreatedTime(lastOrder.getCreatedTime());
			order.setDeliveryAgent(lastOrder.getDeliveryAgent());
			order.setDestinationId(lastOrder.getDestinationId());
			order.setMicroKitchenId(lastOrder.getMicroKitchenId());
			order.setModifiedTime(lastOrder.getModifiedTime());
			order.setMoneyIn(lastOrder.getMoneyIn());
			order.setMoneyOut(lastOrder.getMoneyOut());
			List<OrderDish> orderDishList = new ArrayList<OrderDish>();
			for(OrderDish orderDish  : lastOrder. getOrderDishes()){
				OrderDish orderD =  new OrderDish();
				orderD.setDishId(orderDish.getDishId());
				orderD.setDishSize(orderDish.getDishSize());
				orderD.setDishType(orderDish.getDishType());
				orderD.setInstructions(orderDish.getInstructions());
				orderD.setName(orderDish.getName());
				List<OrderAddOn> orderAddOnList= new ArrayList<OrderAddOn>();
				for(OrderAddOn orderAdd :  orderDish.getOrderAddOn()){
					OrderAddOn  orderAddOn =  new OrderAddOn();
					orderAddOn.setDishId(orderAdd.getDishId());
					orderAddOn.setDishSize(orderAdd.getDishSize());
					orderAddOn.setDishType(orderAdd.getDishType());
					orderAddOn.setName(orderAdd.getName());
					orderAddOn.setOrderDishId(orderAdd.getOrderDishId());
					orderAddOn.setPrice(orderAdd.getPrice());
					orderAddOn.setQuantity(orderAdd.getQuantity());
					orderAddOn.setSmallImageUrl(orderAdd.getSmallImageUrl());
					orderAddOnList.add(orderAddOn);
				}
				orderD.setOrderAddOn(orderAddOnList);
				orderD.setPrice(orderDish.getPrice());
				orderD.setQuantity(orderDish.getQuantity());
				orderDishList.add(orderD);
			}
			order.setOrderDishes(orderDishList);
			order.setPaid(lastOrder.getPaid());
			order.setPaymentStatus(lastOrder.getPaymentStatus());
			order.setRestaurantId(lastOrder.getRestaurantId());
			order.setSourceId(lastOrder.getSourceId());
			order.setSourceType(lastOrder.getSourceType());
			order.setStatus(lastOrder.getStatus());
			order.setUserId(lastOrder.getUserId());
			
			tempCheck.setOrderId(order.getOrderId());
			List<Order> orderList = new ArrayList<>();
			orderList.add(order);
			tempCheck.setOrders(orderList);
			checkService.addCheck(tempCheck);
			return true;
		}
		return false;
	}
	
	private JsonOrder getJsonOrder(PlaceOrderDTO orderBody, Customer customer, Check check) {
		
		List<Order> ord=  check.getOrders();
		
		List<JsonDish> jsonItems = new ArrayList<>();
		for(OrderDishDTO dto : orderBody.order.items){
			for(int i =0; i < dto.quantity; i++) {
				JsonDish dish = new JsonDish();
				Dish getDish  = new Dish();
				getDish = dishService.getDish(dto.itemId);
				if(getDish!=null){
					dish.setDishType(getDish.getDishType());
					logger.info("jsonItems - adding dish - dishType :" + dish.getDishType());
					dish.setId(dto.itemId);
					logger.info("jsonItems - adding dish - id :" + dto.itemId);
					dish.setInstructions(dto.instructions);
					logger.info("jsonItems - adding dish - instructions :" + dto.instructions);
					dish.setName(getDish.getName());
					logger.info("jsonItems - adding dish - name :" + dish.getName());
					dish.setAddOns(dto.addOns);
					dish.setDishSizeId(dto.dishSizeId);
					logger.info("jsonItems - adding dish - sizeId :" + dto.dishSizeId);

					// The following code is duplicated at OrderController:3916
					if(dto.dishSizeId!=null){
						Dish_Size dishSz =  dishTypeService.getDish_Size(dto.dishSizeId,dto.itemId);
						if(dishSz!=null){
							logger.info(dish.getName() +" - size : "+dishSz.getName());
							dish.setPrice(dishSz.getPrice());
							dish.setDishSize(dishSz.getName());
						}
						else{
							dish.setDishSizeId(null);
							dish.setDishSize("");
						}
					}else {
						logger.info(dish.getName() +" - size :  Default" );
						dish.setPrice(getDish.getPrice());
						dish.setDishSize("");
					}
					
					logger.info("jsonItems - adding dish - price :" + dish.getPrice());
					jsonItems.add(dish);
				}
			}
		}
		JsonOrder jsonOrder = new JsonOrder();
		jsonOrder.setCheckId(check.getCheckId());
		jsonOrder.setCustId(customer.getCustomerId());
		jsonOrder.setItems(jsonItems);
		//		jsonOrder.setPrice();
		return jsonOrder;
	}

	private List<Order_DCList> getDCValue(PlaceOrderDTO orderBody){
		
		Order_DCList dcd =  new Order_DCList();
		List<Order_DCList> dc =  new ArrayList<Order_DCList>();
		if(orderBody.order.discountList!=null){
		for(DiscountDTO dto : orderBody.order.discountList){
			DCJson dcV=new DCJson(dto);
			dcd.setCategory(dcV.getCategory());
			dcd.setDcId(dcV.getDcId());
			dcd.setName(dcV.getName());
			dcd.setType(dcV.getType());
			dcd.setValue(dcV.getValue());
			dc.add(dcd);
			dcd =  new Order_DCList();
		}
		}
		return dc;
	}
	
	private Customer getCustomer(PlaceOrderDTO order) throws Exception {
		Customer customer = new Customer();
		customer.setAddress(order.customer.address);
		customer.setCity(order.customer.city);
		customer.setCustomerId(order.customer.id);
		customer.setDeliveryArea(order.customer.deliveryArea);
		customer.setEmail(order.customer.email);
		List<String> names = new LinkedList<String>(Arrays.asList(order.customer.name.split(" ")));
		if(names.size()>0){
			customer.setFirstName(names.get(0));
			if(names.size() > 1) {
				names.remove(0);
				customer.setLastName("");
				for(String name : names)
					customer.setLastName(customer.getLastName()+" "+name);
					customer.setLastName(customer.getLastName().trim());
			}
		}
		customer.setPhone(order.customer.phone.toString());
		return customer;
	}
	
	@RequestMapping(value="/allowEdit",method=RequestMethod.POST)
	 @ResponseBody Map<String,String> allowEdit(HttpServletRequest request, HttpServletResponse response,
			 @RequestParam String checkId,@ApiParam(value="True or False.")@RequestParam String allowEdit, @RequestParam String username){
		Map<String,String> map  =  new HashMap<String, String>();
		//String checkId = request.getParameter("checkId");
		//String allowEdit = request.getParameter("allowEdit");
		//String userName = request.getParameter("username");
		if(checkId!=null){
			Check check = checkService.getCheck(Integer.parseInt(checkId));
			if(check!=null && username!=null){
				User user  =  userService.getUserByUsername(username);
				if(user!=null){
				Role role =  user.getRole();
				if(role.getRole().equalsIgnoreCase("fulfillmentCenterManager")||role.getRole().equalsIgnoreCase("restaurantManager")||role.getRole().equalsIgnoreCase("admin")){
					check.setAllowEdit(Boolean.parseBoolean(allowEdit));
					checkService.addCheck(check);
					map.put("status","true");	
				}else{
					map.put("status","false");
					map.put("message","You don't have access! please contact fulfillment center manager or restaurant manager");
				}
			}else {
				map.put("status","false");
				map.put("message","Please provide valid username");
			}
			}else{
				map.put("status","false");
				map.put("message","This check doesn't exist in our database");
			}
			}
		else {
			map.put("status","fail");
			map.put("message","Not a valid check Id value!");
		}
		 return map;
	 }
	
     @RequestMapping(value="/validateTillAccess",method=RequestMethod.POST)
	 @ResponseBody Map<String, String> validateTillAccess(HttpServletRequest request, HttpServletResponse response,
			 @RequestParam String checkId, @RequestParam  String username){
         //String checkId = request.getParameter("checkId");
         Map<String,String> map  =  new HashMap<String, String>();
		Check check = checkService.getCheck(Integer.parseInt(checkId));
		if(check!=null){
          //  String username = request.getParameter("username");
            Order order =  orderService.getOrder(check.getOrderId());
		if(order.getStatus()==Status.OUTDELIVERY){
		if(username!=null){
			User user  =  userService.getUserByUsername(username);
			if(user!=null){
			TransactionDTO transaction  =  cashRegisterService.fetchTransactionsByCheck(user.getUserId(),check.getCheckId());
			TillDTO till = transaction.tillDetails;
			boolean isValid =  cashRegisterService.validateTillAccess(till.tillId,user.getUserId());
			if(isValid){
				map.put("status","true");
				return map;
			}else {
				map.put("status","false");
				map.put("message","You don't have access to update this order. The person who has opened the sale register can edit this order");
				return map;
			}
		}
		}else {
			map.put("status","false");
			map.put("message","This user doesn't exist.");
			return map;
		}
		}else{
			map.put("status","true");
			return map;
		}
		}
		else {
			map.put("status","false");
			map.put("message","This check is not present in our database.");
			return map;
		}
		return map;
	}
     
     public List<Coupon> validateCoupon(Check check, List<String> couponCodeList, String paymentStatus,int activeDeliveryAreaId){
    	List<Coupon> applied_couponList =  new ArrayList<>();
    	for(String code: couponCodeList){
	    	JsonCouponInfo jsonCoupon =  new JsonCouponInfo();
			//jsonCoupon.setCheckId(check.getCheckId());
			jsonCoupon.setCouponCode(code);
			jsonCoupon.setCustomerId(check.getCustomerId());
			jsonCoupon.setRestaurantID(check.getRestaurantId());
			jsonCoupon.setOrderAmount(check.getBill());
			jsonCoupon.setDeliveryAreaId(activeDeliveryAreaId);
			
			if(paymentStatus!=null){
				if(check.getOrders().get(0)!=null){
					jsonCoupon.setPaymentMode(check.getOrders().get(0).getPaymentStatus());
				}
			}
			jsonCoupon.setOrderSource(check.getOrderSource());
			jsonCoupon.setOrderAmount(check.getRoundOffTotal());
	    	
	    	CouponResponse couponResponse  =  couponService.getCouponDef(jsonCoupon);
	    	logger.info(check.getRoundOffTotal()+"Amount and "+check.getCheckId()+"/"+check.getInvoiceId()+" checkId/InvoiceId  is getting error message : "+couponResponse.getError());
	    	if(couponResponse!=null && couponResponse.getIsValid()){
	    		if(couponResponse.isIsCouponApplicable()){
	    			CouponFlatRules couponRules = couponResponse.getRules();
	    			if(check.getRoundOffTotal()>=couponRules.getMinOrderPayment()){
		    			Coupon coupon = couponService.getEnabledCouponByCode(jsonCoupon.getCouponCode(),check.getRestaurantId());
			    		applied_couponList.add(coupon);
		    			/*if(couponRules.getIsForSelectedCustomer()){
		    				
		    			}*/
		    			if(!couponRules.getIsUsedOncePerCustomer()){
	    				//	Coupon coupon = couponService.getCouponByCode(jsonCoupon.getCouponCode(),check.getRestaurantId());
	    					//applied_couponList.add(coupon);
	    					
	    				}
		    		}
	    		}
	    	}
    	}
		return applied_couponList;
    	 
     }
     
}
