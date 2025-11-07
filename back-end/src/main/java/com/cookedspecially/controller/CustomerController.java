package com.cookedspecially.controller;

import com.cookedspecially.config.CSConstants;
import com.cookedspecially.domain.*;
import com.cookedspecially.dto.*;
import com.cookedspecially.dto.credit.*;
import com.cookedspecially.enums.Status;
import com.cookedspecially.service.*;
import com.cookedspecially.utility.StringUtility;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring4.SpringTemplateEngine;
import springfox.documentation.annotations.ApiIgnore;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author shashank , rahul, Abhishek
 *
 */
@Controller
@Api(value = "customers",description="Customer REST API's")
@RequestMapping("/customer")
public class CustomerController {

	final static Logger logger = Logger.getLogger(CustomerController.class);

	private final String apiPrefix="https://www.cookedspecially.com/CookedSpecially/order/displayCheck?invoiceId=";

	@Autowired
	private CustomerService customerService;

	@Autowired
	@Qualifier("customerCreditAutomatedBilling")
	private CustomerCreditService customerCreditService;

	@Autowired
	private DeliveryAreaService deliveryAreaService;

	@Autowired
	private RestaurantService restaurantServices;

	@Autowired
	private CheckService checkService;

	@Autowired
	private JavaMailSenderImpl mailSender;

	@Autowired
	private SpringTemplateEngine templateEngine;


	@RequestMapping("/")
	@ApiIgnore
	public String index(Map<String, Object> map, HttpServletRequest request) {
		map.put("customer", new Customer());
		return "customer";
	}

	@RequestMapping("/addCustomer")
	@ApiIgnore
	public String addCustomerJSONPage(Map<String, Object> map, HttpServletRequest request) {
		logger.info("addCustomerJsonPage api called");
		return "addCustomer";
	}

	@RequestMapping("/edit/{customerId}")
	@ApiIgnore
	public String editCustomer(Map<String, Object> map, HttpServletRequest request, @PathVariable("customerId") Integer customerId) {
		map.put("customer", customerService.getCustomer(customerId));
		logger.info("Edit customer inforamtion ID :"+customerId);
		return "customer";
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiIgnore
	public String addCustomer(Map<String, Object> map, @ModelAttribute("customer") Customer customer) {
		customer.setRestaurantId(customer.getUserId());
		customerService.addCustomer(customer);
		logger.info("Customer added :"+customer.getEmail());
		return "redirect:/customer/";
	} 


	@ApiOperation(value="To update customer's existing address and add new address.", notes="In case of adding new address you have to remove id filed from json")
	@RequestMapping(value = "/updateDeliveryAddress", method = RequestMethod.POST,consumes = "application/json", produces = "application/json")
	public @ResponseBody Map<String,String> updateDeliveryAddress(@RequestBody CustomerAddress customerAddress, HttpServletRequest request){	
		Map<String, String> msz =  new HashMap<String, String>();
		if(customerAddress.getCustomerId()>0){
			CustomerAddress customerAdd = null;
			if(customerAddress.getId()>0){
				customerAdd = customerService.getCustomerAddressById(customerAddress.getId());
			}
			if(customerAdd!=null){
				if(customerAddress.getCustomerId()==customerAdd.getCustomerId()){
					try{
						String city = customerAddress.getCity();
						String custAddress =customerAddress.getCustomerAddress();
						String state = customerAddress.getState();
						String deliveryArea = customerAddress.getDeliveryArea();
						if(city!="" && custAddress!="" && state!="" && deliveryArea!=""){
							customerService.updateCustomerAddress(customerAddress);	
							msz.put("status", "success");
						}
						else{
							msz.put("status", "error");	
							msz.put("error","Information is not valid");
						}

					}catch(Exception e){
						msz.put("status", "error");	
						msz.put("error","Information is not valid");
						try {
							restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
						} catch (UnsupportedEncodingException
								| MessagingException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						logger.info("Exception mail sent");
					}
				}else{
					msz.put("status", "error");	
					msz.put("error","Customer is not valid");
				}
			}
			else{
				try{
					String city = customerAddress.getCity();
					String custAddress =customerAddress.getCustomerAddress();
					String state = customerAddress.getState();
					String deliveryArea = customerAddress.getDeliveryArea();
					if(city!="" && custAddress!="" && state!="" && deliveryArea!=""){
						customerService.addCustomerAddress(customerAddress);
						msz.put("status", "success");
					}else{
						msz.put("status", "error");	
						msz.put("error","Information is not valid");
					}
				}catch(Exception e){
					msz.put("status", "error");	
					msz.put("error","Information is not valid");
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
		else {
			msz.put("status", "error");	
			msz.put("error","Customer  does not exist");
		}
		return msz;
	}

	@ApiOperation(value="To remove delivery Address",notes="Only id and customerId are mandatory fields ")
	@RequestMapping(value = "/removeDeliveryAddress", method = RequestMethod.POST,consumes = "application/json", produces = "application/json")
	public @ResponseBody Map<String,String> removeDeliveryAddress(@RequestBody CustomerAddress customerAddress){
		Map<String, String> msz =  new HashMap<String, String>();
		CustomerAddress customerAdd = customerService.getCustomerAddressById(customerAddress.getId());
		if(customerAdd!=null){
			if(customerAddress.getCustomerId()==customerAdd.getCustomerId()){
				customerAddress.setStatus(Status.INACTIVE.toString());
				customerService.updateCustomerAddress(customerAddress);
				msz.put("status", "success");
			}
			else{
				msz.put("status", "error");	
				msz.put("error","Information is not valid");
			}
		}
		else {
			msz.put("status", "error");	
			msz.put("error","Address  does not exist");
		}
		return msz;
	}

	@ApiOperation(value="[*] An API for a restaurant to update customer info Only.")
	@RequestMapping(value = "/setCustomerInfo.json", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public @ResponseBody Map<String,String>  setCustomerInfoJSON(@RequestBody(required=false) Customer customer, Model model, HttpServletRequest request) throws JSONException {
		Map<String,String> json = new HashMap<String,String>();
		try {
			String output = json.toString();
			String city = null;
			String state = null;
			Customer customerPrev =  customerService.getCustomer(customer.getCustomerId());
			Restaurant restaurant  =  restaurantServices.getRestaurant(customer.getRestaurantId());
			city =  restaurant.getCity();
			state = restaurant.getState();
			customer.setCity(city);
			customer.setAddress(customer.getAddress());
			customer.setCreatedTime(customerPrev.getCreatedTime());
			logger.info("restaurant Id :"+customer.getRestaurantId());
			CustomerAddress customerAddress =  new CustomerAddress();
			boolean prevCustAddress=false;
			if (customer !=null) {
				List<CustomerAddress> custAddress =  customerService.getCustomerAddress(customer.getCustomerId());
				customer.setRewardPoints(customerPrev.getRewardPoints());
				customer.setPhone(customerPrev.getPhone());
				if(customer.getDeliveryArea()!=null){
					int counter=0;
					if(custAddress.size()==0){
						if(customer.getDeliveryArea().equalsIgnoreCase(customerPrev.getDeliveryArea())){
							logger.info("Delivery Area Matched");
							if(customer.getAddress().equalsIgnoreCase(customerPrev.getAddress())){
								logger.info("Delivery Address Matched");
							}
							else if(customerPrev.getDeliveryArea()!=null && customerPrev.getAddress()!=null){
								prevCustAddress=true;
							}
						}
						else{
							if("null".equalsIgnoreCase(customerPrev.getAddress()) && "null".equalsIgnoreCase(customerPrev.getDeliveryArea())){
							}
							else if(customerPrev.getAddress()!=null && customerPrev.getDeliveryArea()!=null){
								prevCustAddress=true;
							}
						}
					}else if(custAddress.size()>0){
						for(CustomerAddress ca : custAddress){
							if(customer.getDeliveryArea().equalsIgnoreCase(ca.getDeliveryArea()) && customer.getAddress().equalsIgnoreCase(ca.getCustomerAddress())){
								customerService.addCustomer(customer);
								counter++;
							}
						}
					}
					if(counter==0){
						logger.info("New Delivery Address");
						customerAddress.setCustomerAddress(customer.getAddress());
						customerAddress.setCustomerId(customer.getCustomerId());
						customerAddress.setDeliveryArea(customer.getDeliveryArea());
						customerAddress.setCity(city);
						customerAddress.setState(state);
						customerService.addCustomerAddress(customerAddress);
						customerService.addCustomer(customer);
					}
					if(prevCustAddress){
						customerAddress.setCustomerAddress(customerPrev.getAddress());
						customerAddress.setCustomerId(customerPrev.getCustomerId());
						customerAddress.setDeliveryArea(customerPrev.getDeliveryArea());
						customerAddress.setCity(city);
						customerAddress.setState(state);
						customerService.addCustomerAddress(customerAddress);
						customerService.addCustomer(customer);
					}

					logger.info("4>Setting Customer Information from JSON : "+customer.getPhone()+" ---"+customer.getDeliveryTime());
					json.put("status","success");
					///output = json.toString();
					return json;
				}else {
					logger.info("Saving profile information ");
					if(customerPrev.getCreatedTime()!=null)
						customer.setCreatedTime(customerPrev.getCreatedTime());
					if (customerPrev.getAddress() != null)
							customer.setAddress(customerPrev.getAddress());
						if(customerPrev.getDeliveryArea()!=null)
							customer.setDeliveryArea(customerPrev.getDeliveryArea());
						if(customerPrev.getFacebookEmail()!=null)
							customer.setFacebookEmail(customerPrev.getFacebookEmail());
						if(customerPrev.getFacebookId()!=null)
							customer.setFacebookId(customerPrev.getFacebookId());
						if(customerPrev.getIsAuthentic()!=null)
							customer.setIsAuthentic(customerPrev.getIsAuthentic());
						if(customerPrev.getNumberOfOrders()!=null)
							customer.setNumberOfOrders(customerPrev.getNumberOfOrders());
						if(customerPrev.getOrgId()!=null)
							customer.setOrgId(customerPrev.getOrgId());
						if(customerPrev.getSimNumber()!=null)
							customer.setSimNumber(customerPrev.getSimNumber());
						if(customerPrev.getDeliveryTime()!=null)
							customer.setDeliveryTime(customerPrev.getDeliveryTime());
						customer.setIsActive(customerPrev.getIsActive());

						json.put("status","success");
						//output = json.toString();
						customerService.addCustomer(customer);
						return json;
				}
			}
		} catch (Exception e) {
			logger.error("Could not get customer json"+e.getMessage());
			try {
				restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
			} catch (UnsupportedEncodingException | MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			logger.info("Exception mail sent");
		}
		json.put("status","fail");
		//output = json.toString();
		return json;
	}

	@RequestMapping("/delete/{customerId}")
	@ApiIgnore
	public String deleteCustomer(Map<String, Object> map, HttpServletRequest request, 
			@PathVariable("customerId") Integer customerId) {
		try {
			customerService.removeCustomer(customerId);
			logger.info("Customer deleted, customer ID is :"+customerId);
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
		return index(map, request);
	}

	@ApiOperation(value = "[*] Gets a customer based on restaurantId/orgId and phone number", notes = "Retrieves a single customer", response = Customers.class)
	@RequestMapping(value = "/getCustomerInfo.json")
	public @ResponseBody Customers getCustomerInfoJSON(@ApiParam(value="Phone number with country code")@RequestParam(required=false) String phone,
			@ApiParam(value="Required if phone number is not available")
			@RequestParam(required=false) String custId, 
			@RequestParam String restaurantId , 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		String email = request.getParameter("email");
		//String phone = request.getParameter("phone");
		String custIdStr =custId;
		String restaurantIdStr = restaurantId;
		if((restaurantIdStr!=null && phone!=null)){
			logger.info("***************Customer Ordring Flow Started***********************");
			logger.info("1.Getting saved customer Information from database  Ph no. if exist :"+phone);
			Integer custID = -1;
			if (StringUtils.isNotEmpty(custIdStr)) {
				custID = Integer.parseInt(custIdStr);
			}
			Integer restaurantID = -1;
			Integer orgId = -1;
			if (StringUtils.isNotEmpty(restaurantIdStr)) {
				restaurantID = Integer.parseInt(restaurantIdStr);
				//restaurantId = (Integer)request.getSession().getAttribute("restaurantId");
			}else
				restaurantID = (Integer) request.getSession().getAttribute("restaurantId");

			Restaurant rest = restaurantServices.getRestaurant(restaurantID);
			if(rest!=null){
				if(rest.getParentRestaurantId()==null){
					orgId=rest.getRestaurantId();
				}else{
					orgId = rest.getParentRestaurantId();
				}
			}
			else {
				orgId = restaurantID;
			}
			List<Customer> customerList = customerService.getCustomerByParams(custID, email, phone, orgId);
			Customers customers = new Customers();
			if (customerList != null && customerList.size() < 1) {
				customerList = new ArrayList<Customer>();
				Customer customer = new Customer();
				customer.setPhone(phone);
				customer.setEmail(email);
				customer.setCreatedTime(new java.util.Date());
				if (restaurantID > 0) {
					customer.setRestaurantId(restaurantID);
				}
				if(orgId>0){
					customer.setOrgId(orgId);
				}
				customerService.addCustomer(customer);
				logger.info("Customer Name :"+customer.getFirstName()+""+customer.getLastName());
				logger.info("Customer Address :"+customer.getAddress());
				customerList.add(customer);
				customers.setExactMatch(false);
				customers.setNewCustomer(true);
			} else {
				customers.setNewCustomer(false);
				Customer exactCustomer = null;
				for (Customer customer : customerList) {
					boolean exactMatch = false;
					logger.info("2.Returning Customer name, address to client side:");
					if (custID > 0) {
						if (custID != customer.getCustomerId()) {
							continue;
						}
						exactMatch = true;
					}
					if (StringUtils.isNotEmpty(phone)) {
						if (!phone.equals(customer.getPhone())) {
							continue;
						}
						exactMatch = true;
					}
					if (StringUtils.isNotEmpty(email)) {
						if (!email.equals(customer.getEmail())) {
							continue;
						}
						exactMatch = true;
					}

					if (exactMatch) {
						exactCustomer = customer;
						break;
					}
				}
				if (exactCustomer != null) {
					Boolean deliveryAreaValidate=false;

					ArrayList<CustomerAddress> deliveryAreaData =  new ArrayList<CustomerAddress>();
					List<CustomerAddress> ca = customerService.getCustomerAddress(exactCustomer.getCustomerId());

					if(ca.size()<1){
						for(Customer cs : customerList){
							CustomerAddress customerAddress =  new CustomerAddress();
							if(cs.getDeliveryArea()!=null && cs.getAddress()!=null){
								customerAddress.setCity(rest.getCity());
								customerAddress.setCustomerAddress(cs.getAddress());
								customerAddress.setCustomerId(cs.getCustomerId());
								customerAddress.setDeliveryArea(cs.getDeliveryArea());
								customerAddress.setState(rest.getState());
								customerService.addCustomerAddress(customerAddress);
								ca = customerService.getCustomerAddress(exactCustomer.getCustomerId());
							}
						}
					}
					customerList = new ArrayList<Customer>();
					if(orgId==null && rest!=null){

						List<Restaurant> restaurantList =  restaurantServices.listRestaurantByParentId(rest.getRestaurantId());
						for(Restaurant resturant : restaurantList){
							List<DeliveryArea> deliveryArea = deliveryAreaService.listDeliveryAreasByResaurant(resturant.getRestaurantId());
							for(CustomerAddress cna: ca){
								if(Status.ACTIVE.toString().equalsIgnoreCase(cna.getStatus())){
									for(DeliveryArea da :deliveryArea){
										if(da.getName().equalsIgnoreCase(cna.getDeliveryArea())){
											deliveryAreaData.add(cna);
										}
									}
								}
							}
							if(exactCustomer.getDeliveryArea()!=null){
								for(DeliveryArea da :deliveryArea){
									if(da.getName().equalsIgnoreCase(exactCustomer.getDeliveryArea()) && (!da.getName().equalsIgnoreCase("zomato"))){
										deliveryAreaValidate=true;
									}
								}
							}
							if(deliveryAreaData!=null){
								customers.setCustomerAddress(deliveryAreaData);
							}
							if(!deliveryAreaValidate) {
								exactCustomer.setDeliveryArea(null);
								exactCustomer.setAddress(null);
								Customer cs  =  customerService.getCustomer(exactCustomer.getCustomerId());
								cs.setDeliveryArea(null);
								cs.setAddress(null);
								customerService.addCustomer(cs);
							}
						}
					}
					else{
						// it is not a Organization 
						List<DeliveryArea> deliveryArea = deliveryAreaService.listDeliveryAreasByResaurant(restaurantID);

						for(CustomerAddress cna: ca){
							if(Status.ACTIVE.toString().equalsIgnoreCase(cna.getStatus())){
								for(DeliveryArea da :deliveryArea){
									if(da.getName().equalsIgnoreCase(cna.getDeliveryArea())){
										deliveryAreaData.add(cna);
									}
								}
							}
						}
						if(exactCustomer.getDeliveryArea()!=null){
							for(DeliveryArea da :deliveryArea){
								if(da.getName().equalsIgnoreCase(exactCustomer.getDeliveryArea())){
									deliveryAreaValidate=true;
								}
							}
						}
						if(deliveryAreaData!=null){
							customers.setCustomerAddress(deliveryAreaData);
						}
						if(!deliveryAreaValidate && orgId>0) {
							exactCustomer.setDeliveryArea(null);
							exactCustomer.setAddress(null);
							Customer cs  =  customerService.getCustomer(exactCustomer.getCustomerId());
							cs.setDeliveryArea(null);
							cs.setAddress(null);
							customerService.addCustomer(cs);
						}
					}
					customerList.add(exactCustomer);
					customers.setExactMatch(true);

				} else {
					customers.setExactMatch(false);
				}
			}
			customers.setCustomers(customerList);
			logger.info("3>return values from Customer table to client side:");
			return customers;
		}
		return null;
	}

	@ApiOperation(value="To Fetch all saved customer information and address using customer Id")
	@RequestMapping(value = "/customerInfo.json",method = RequestMethod.GET)
	public @ResponseBody Customers getCustomerInfoByCustId(HttpServletRequest request, HttpServletResponse response,@RequestParam String custId) throws JSONException {

		String custIdStr = custId;
		if(custIdStr!=null){
			logger.info("***************Customer Ordring Flow Started***********************");
			Integer custID = -1;
			if (StringUtils.isNotEmpty(custIdStr)) {
				custID = Integer.parseInt(custIdStr);
			}
			Integer restaurantId = -1;
			Integer orgId = -1;

			Restaurant rest = null;

			List<Customer> customerList = customerService.getCustomerById(custID);
			Customers customers = new Customers();
			if (customerList != null && customerList.size() < 1) {
				customerList = new ArrayList<Customer>();
				Customer customer = new Customer();
				customer.setCreatedTime(new java.util.Date());
				if (restaurantId > 0) {
					customer.setRestaurantId(restaurantId);
				}
				if(orgId>0){
					customer.setOrgId(orgId);
				}
				customerService.addCustomer(customer);
				logger.info("Customer Name :"+customer.getFirstName()+""+customer.getLastName());
				logger.info("Customer Address :"+customer.getAddress());
				customerList.add(customer);
				customers.setExactMatch(false);
				customers.setNewCustomer(true);
			} else {
				customers.setNewCustomer(false);
				Customer exactCustomer = null;
				for (Customer customer : customerList) {
					orgId = customer.getOrgId();
					boolean exactMatch = false;
					logger.info("2.Returning Customer name, address to client side:");
					if (custID > 0) {
						if (!(custId.equals(customer.getCustomerId()))) {
							continue;
						}
						exactMatch = true;
					}

					rest = restaurantServices.getRestaurant(orgId);
					if (exactMatch) {
						exactCustomer = customer;
						break;
					}
				}
				if (exactCustomer != null) {
					Boolean deliveryAreaValidate=false;
					customerList = new ArrayList<Customer>();
					ArrayList<CustomerAddress> deliveryAreaData =  new ArrayList<CustomerAddress>();
					List<CustomerAddress> ca = customerService.getCustomerAddress(exactCustomer.getCustomerId());

					if(rest.getParentRestaurantId()==null){
						List<Restaurant> restaurantList =  restaurantServices.listRestaurantByParentId(rest.getRestaurantId());
						for(Restaurant resturant : restaurantList){
							List<DeliveryArea> deliveryArea = deliveryAreaService.listDeliveryAreasByResaurant(resturant.getRestaurantId());

							for(CustomerAddress cna: ca){
								if(Status.ACTIVE.toString().equalsIgnoreCase(cna.getStatus())){
									for(DeliveryArea da :deliveryArea){
										if(da.getName().equalsIgnoreCase(cna.getDeliveryArea())){
											deliveryAreaData.add(cna);
										}
									}
								}
							}
							if(exactCustomer.getDeliveryArea()!=null){
								for(DeliveryArea da :deliveryArea){
									if(da.getName().equalsIgnoreCase(exactCustomer.getDeliveryArea())){
										deliveryAreaValidate=true;
									}
								}
							}
							if(deliveryAreaData!=null){
								customers.setCustomerAddress(deliveryAreaData);
							}
							if(!deliveryAreaValidate) {
								exactCustomer.setDeliveryArea(null);
								exactCustomer.setAddress(null);
								Customer cs  =  customerService.getCustomer(exactCustomer.getCustomerId());
								cs.setDeliveryArea(null);
								cs.setAddress(null);
								customerService.addCustomer(cs);
							}
						}
					}
					customerList.add(exactCustomer);
					customers.setExactMatch(true);

				} else {
					customers.setExactMatch(false);
				}
			}
			customers.setCustomers(customerList);
			logger.info("3>return values from Customer table to client side:"+customers+"...."+customerList);
			return customers;
		}
		return null;
	}

	@ApiOperation(value = "[*] Gets a customer based on orgId and phone number", notes = "Retrieves a single customer", response = Customers.class)
	@RequestMapping(value = "/getCustomerData.json",method = RequestMethod.GET)
	public @ResponseBody CustomerDataDTO getCustomerData(HttpServletRequest request, HttpServletResponse response,@RequestParam String phone, @RequestParam String orgId) throws JSONException {
		//String phone = request.getParameter("phone");
		Integer orgID = Integer.parseInt(orgId);
		return customerService.getCustomerData(phone, orgID);
	}

	@RequestMapping(value = "/isSessionValid",method = RequestMethod.GET)
	public @ResponseBody Map<String,Boolean> isSessionValid(HttpServletRequest request) {
		Boolean valid = false;
		if(request.getSession().getAttribute(CSConstants.TOKEN) != null)
			valid = true;

		Map<String,Boolean> m = new HashMap<String,Boolean>();
		m.put("valid", valid);
		return m;
	}
	/*-----------------------UPDATED WEB CUSTOMER REGISTER AND VALIDATE- API  1.0.0  -------------------------------------------------*/
	@RequestMapping(value = "/registerWebCustomer", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseDTO registerWebCustomer(@Valid @RequestBody WebCustomerRegisterDTO customer, BindingResult result, HttpServletRequest request, HttpServletResponse response) {
		if (result.hasErrors()) {
			ResponseDTO response1=new ResponseDTO();
			response1.message="Invalid details. Please check";
			response1.result="ERROR";
			return response1;
		}
		return customerService.registerWebCustomer(customer);
	}

	@RequestMapping(value = "/verifyCustomerOTP", method = RequestMethod.POST , consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Object verifyCustomerOTP(@Valid @RequestBody VerifyWebCustomer customer,BindingResult result, HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			if (!result.hasErrors()) {
				return customerService.verifyCustomerOTP(customer);
			}
			ResponseDTO response1=new ResponseDTO();
			response1.message="Invalid details";
			response1.result="ERROR";
			return response1;
		} catch (Exception e) {
			ResponseDTO response1=new ResponseDTO();
			response1.message="Error="+e.getMessage();
			response1.result="ERROR";
			restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
			logger.info("Exception mail sent");
			return response1;
		}
	}
	@RequestMapping(value = "/generateNewOTP", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseDTO generateNewOTP(@Valid @RequestBody FetchOTPDTO fetchOtp,BindingResult result){
		if (result.hasErrors()) {
			ResponseDTO response=new ResponseDTO();
			response.message="Invalid details. Please check";
			response.result="ERROR";
			return response;
		}
		return customerService.generateNewOTP(fetchOtp);
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public Object loginCustomer(@Valid @RequestBody CustomerLoginDTO login,BindingResult result, HttpServletRequest request){
		try {
			if (!result.hasErrors()) {
				return customerService.login(login.mobileNo,login.orgId,login.device,login.appId);
			}
			StringUtility.getErrorString(result);
			ResponseDTO response1=new ResponseDTO();
			response1.message="Invalid details";
			response1.result="ERROR";
			return response1;
		} catch (Exception e) {
			ResponseDTO response1=new ResponseDTO();
			response1.message="Error="+e.getMessage();
			response1.result="ERROR";
			try {
				restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
			} catch (UnsupportedEncodingException | MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			logger.info("Exception mail sent");
			return response1;
		}
	}

	@RequestMapping(value = "/registerCustomerApp", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseDTO registerCustomerApp(@Valid @RequestBody CustomerAppRegisterDTO customerAppDTO,BindingResult result, HttpServletRequest request, HttpServletResponse response){
		if (result.hasErrors()) {
			ResponseDTO response1=new ResponseDTO();
			response1.message="Invalid details. Please check";
			response1.result="ERROR";
			return response1;
		}
		return customerService.registerCustomerApp(customerAppDTO);
	}


	/********************************************************** Credit Account *************************************************************************************/
	@RequestMapping(value = "/addCustomerCreditType", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	@ResponseBody
	@ApiOperation(value = "This API requires user login, please login with respective user. Note* this API get some data from user session")
	public ResponseDTO addCustomerCreditType(@Valid @RequestBody CreditTypeDTO creditDto,BindingResult result, HttpServletRequest request){
		ResponseDTO response=new ResponseDTO();
		Integer orgId=Integer.parseInt(request.getSession().getAttribute("organisationId").toString());
		if (result.hasErrors() || orgId==null || !(orgId > 0)) {
            response.message = "Invalid details.Only Admin is authorise to access this.Please login and try again";
            response.result="ERROR";
			return response;
		}
		creditDto.orgId=orgId;
		try {
			return customerService.addCustomerCreditType(creditDto);
		} catch (Exception e) {
			response.message="Something Went wrong while upadting."+e.getMessage();
			response.result="ERROR";
			try {
				restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
			} catch (UnsupportedEncodingException | MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			logger.info("Exception mail sent");
			return response;
		}
	}
	@RequestMapping(value = "/editCustomerCreditType", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	@ResponseBody
	@ApiOperation(value = "This API requires user login, please login with respective user. Note* this API get some data from user session")
	public ResponseDTO editCustomerCredit(@Valid @RequestBody CreditType creditType,BindingResult result, HttpServletRequest request){
		Integer orgId=Integer.parseInt(request.getSession().getAttribute("organisationId").toString());
		ResponseDTO response=new ResponseDTO();
		if (result.hasErrors()) {
			response.message="Invalid details. Please check";
			response.result="ERROR";
			return response;
		}
		else if (orgId==null || !(orgId > 0) || creditType.getOrgId()!=(orgId.intValue())) {
			response.message="Invalid details.Only Admin is authorise to access this.Please login and try agian";
			response.result="ERROR";
			return response;
		}

		try {
			return customerService.editCustomerCreditType(creditType);
		} catch (Exception e) {
			response.message="Something Went wrong while editing credit type."+e.getMessage();
			response.result="ERROR";
			try {
				restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
			} catch (UnsupportedEncodingException | MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			logger.info("Exception mail sent");
			return response;
		}
	}
	@RequestMapping(value = "/listCustomerCreditTypes", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "This API requires user login, please login with respective user. Note* this API get some data from user session")
	public Object listCustomerCreditTypes(HttpServletRequest request){
		Integer orgId=Integer.parseInt(request.getSession().getAttribute("organisationId").toString());
		if (orgId==null || !(orgId > 0)) {
			ResponseDTO response=new ResponseDTO();
			response.message="Only Admin is authorise to access this.Please login and try agian";
			response.result="ERROR";
			return response;
		}
		return customerService.listCustomerCreditType(orgId);
	}
	@RequestMapping(value = "/deleteCustomerCreditType/{creditTypeId}", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "This API requires user login, please login with respective user. Note* this API get some data from user session")
	public ResponseDTO deleteCustomerCreditType(@PathVariable("creditTypeId") int creditTypeId, HttpServletRequest request){
		try{
			return customerService.deleteCustomerCreditType(creditTypeId, Integer.parseInt(request.getSession().getAttribute("organisationId").toString()));
		} catch (Exception e) {
			ResponseDTO response=new ResponseDTO();
			response.message="Could not removeCustomerCredit,"+e.getMessage();
			response.result="ERROR";
			logger.info(response.message);
			try {
				restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
			} catch (UnsupportedEncodingException | MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			logger.info("Exception mail sent");
			return response;
		}
	}

	@RequestMapping(value = "/enableCustomerCredit", method = RequestMethod.PUT,consumes = "application/json", produces = "application/json")
	@ResponseBody
	@ApiOperation(value = "This API requires user login, please login with respective user. Note* this API get some data from user session")
	public ResponseDTO enableCustomerCredit(@RequestBody CustomerCreditDTO customerCreditDTO,  HttpServletRequest request){
		try{

			return customerService.enableCustomerCredit(customerCreditDTO, Integer.parseInt(request.getSession().getAttribute("organisationId").toString()));
		} catch (Exception e) {
			ResponseDTO response=new ResponseDTO();
			response.message="Could not process customer credit modification,"+e.getMessage();
			response.result="ERROR";
			logger.info(response.message);
			logger.info(e);
			try {
				restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
			} catch (UnsupportedEncodingException | MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			logger.info("Exception mail sent");
			return response;
		}
	}
	@RequestMapping(value = "/updateCustomerCredit", method = RequestMethod.PUT,consumes = "application/json", produces = "application/json")
	@ResponseBody
	@ApiOperation(value = "This API requires user login, please login with respective user. Note* this API get some data from user session")
	public ResponseDTO updateCustomerCredit(@RequestBody CustomerCreditDTO customerCredit, HttpServletRequest request){
		try{
			return customerService.updateCustomerCredit(customerCredit, Integer.parseInt(request.getSession().getAttribute("organisationId").toString()));
		} catch (Exception e) {
			ResponseDTO response=new ResponseDTO();
			logger.warn(e);
			response.message = "Could not update customer credit," + e.getMessage();
			response.result="ERROR";
			logger.info(response.message);
			try {
				restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
			} catch (UnsupportedEncodingException | MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			logger.info("Exception mail sent");
			return response;
		}
	}

	@RequestMapping(value = "/removeCustomerCredit", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	@ApiOperation(value = "This API requires user login, please login with respective user. Note* this API get some data from user session")
	public ResponseDTO removeCustomerCredit(@RequestBody Map<String, Integer> map, HttpServletRequest request) {
		try{
			return customerService.removeCustomerCredit(map.get("customerId").intValue(), Integer.parseInt(request.getSession().getAttribute("organisationId").toString()));
		} catch (Exception e) {
			ResponseDTO response=new ResponseDTO();
			response.message="Could not removeCustomerCredit,"+e.getMessage();
			response.result="ERROR";
			logger.info(response.message);
			try {
				restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
			} catch (UnsupportedEncodingException | MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			logger.info("Exception mail sent");
			return response;
		}
	}
	@RequestMapping(value = "/doTransaction", method = RequestMethod.PUT,consumes = "application/json", produces = "application/json")
	@ResponseBody
	@ApiOperation(value = "This API requires user login, please login with respective user. Note* this API get some data from user session")
	public ResponseDTO doTransaction(@RequestBody AddCreditToCustomerAccountDTO creditAddDTO, HttpServletRequest request) {
		try{
			if(request.getSession().getAttribute("organisationId")!=null && request.getSession().getAttribute("userId")!=null)
				return customerService.creatTransaction(creditAddDTO,Integer.parseInt(request.getSession().getAttribute("organisationId").toString()), Integer.parseInt(request.getSession().getAttribute("userId").toString()) );
			else
				return customerService.creatTransaction(creditAddDTO,null,null);
		} catch (Exception e) {
			ResponseDTO response=new ResponseDTO();
			response.message="Failed to do transaction."+e.getMessage();
			response.result="ERROR";
			logger.info(response.message);
			try {
				restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
			} catch (UnsupportedEncodingException | MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			logger.info("Exception mail sent");
			return response;
		}
	}
	@RequestMapping(value = "/listCustomerCredit", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "This API requires user login, please login with respective user. Note* this API get some data from user session")
	public List<CreditDTO> listCustomerCredit(HttpServletRequest request){
		try{
			return customerService.listCustomerCredit(Integer.parseInt(request.getSession().getAttribute("organisationId").toString()));
		} catch (Exception e) {
			logger.info(e.getMessage());
			try {
				restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
			} catch (UnsupportedEncodingException | MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			logger.info("Exception mail sent");
			return null;
		}
	}
	@RequestMapping(value = "/listCustomerCreditTransactions", method = RequestMethod.GET)
	@ResponseBody
	public List<CreditTransactionDTO> listCustomerCreditTransactions(HttpServletRequest request, @RequestParam String customerId, @RequestParam String fromDate,@RequestParam String toDate,@RequestParam String dateFormat){
		try{
			return customerService.listCustomerCreditTransactions(Integer.parseInt(customerId), fromDate,toDate, dateFormat);
		} catch (Exception e) {
			logger.info(e.getMessage());
			try {
				restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
			} catch (UnsupportedEncodingException | MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			logger.info("Exception mail sent");
			return null;
		}
	}

	@RequestMapping(value = "/listAllCustomerCreditBills/{ffcId}", method = RequestMethod.GET)
	@ResponseBody
	public List<CreditBillDTO> listAllCustomerCreditBills(@PathVariable("ffcId") int ffcId, HttpServletRequest request) throws Exception {
		return customerCreditService.listAllCustomerCreditBills(ffcId, request.getParameter("fromDate"),request.getParameter("toDate"));
	}

	@RequestMapping(value = "/listAgedOneOffCreditHolder/{ffcId}/{dayCount}", method = RequestMethod.GET)
	@ResponseBody
	public List<AgedCreditDTO> listAgedOneOffCreditHolder(@PathVariable("ffcId") int ffcId, @PathVariable("dayCount") int dayCount, HttpServletRequest request) throws Exception {
		return customerCreditService.listAgedOneOffCreditHolder(ffcId, dayCount);
	}

	@RequestMapping(value = "/listCustomerCreditBills/{customerId}", method = RequestMethod.GET)
	@ResponseBody
	public AllCreditStatements listCustomerCreditBills(@PathVariable("customerId") int customerId, HttpServletRequest request) throws Exception {
		return customerCreditService.listCustomerCreditBills(customerId, request.getParameter("fromDate"),request.getParameter("toDate"));
	}

	@RequestMapping(value = "/getRecentCustomerCreditBIll/{customerId}", method = RequestMethod.GET)
	@ResponseBody
	public CreditStatementDTO getRecentCustomerCreditBIll(@PathVariable("customerId") int customerId, HttpServletRequest request) throws Exception {
		return customerCreditService.getRecentCustomerCreditBIll(customerId);
	}

	@RequestMapping(value = "/generateCreditBill", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public CreditStatementDTO generateCreditBill(@RequestBody Map<String, Integer> map, HttpServletRequest request) throws Exception {
		if (map != null || map.size() != 0 || map.get("customerId") != null) {
			return customerCreditService.generateCreditBilling(map.get("customerId").intValue());
		} else
			throw new Exception("Please pass customerId");

	}

	@RequestMapping(value = "/listCustomerHavingCredit/{organisationId}", method = RequestMethod.GET)
	@ResponseBody
	public List<Customer> listCustomerHavingCredit(@PathVariable("organisationId") int organisationId ,HttpServletRequest request) throws Exception{
		return customerCreditService.listAllCustomerHavingCredit(organisationId);
	}
	@RequestMapping(value = "/payCreditBill", method = RequestMethod.PUT,consumes = "application/json", produces = "application/json")
	@ResponseBody
	@ApiOperation(value = "This API requires user login, please login with respective user. Note* this API get some data from user session")
	public ResponseDTO payCreditBill(@RequestBody CreditBillPaymentDTO billPayment, HttpServletRequest request) throws Exception {
		try {
			return customerCreditService.updateCreditBillTransaction(billPayment,Integer.parseInt(""+request.getSession().getAttribute("userId")));
		} catch (Exception e) {
			ResponseDTO response=new ResponseDTO();
			logger.warn(e);
			response.message = "Failed to make to payment, please Try Again!!";
			response.result = "ERROR";
			restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
			logger.info("Exception mail sent");
			return response;
		}
	}

	@RequestMapping(value = "/getCreditStatement/{statementId}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public CreditStatementDTO getCreditStatement(@PathVariable("statementId") String statementId, HttpServletRequest request) throws Exception {
		try {
		return customerCreditService.getCreditStatement(statementId);
		} catch (Exception e) {
			restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
			logger.info("Exception mail sent");
			logger.warn(e);
			return null;
		}
	}

	@RequestMapping(value = "/markCreditBillAsDelivered", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	@ApiOperation(value = "This API requires user login, please login with respective user. Note* this API get some data from user session")
	public ResponseDTO markCreditBillAsDelivered(@RequestBody Map<String, String> map, HttpServletRequest request) throws Exception {
		try {
			return customerCreditService.markCreditBillAsDelivered(map.get("creditBillId"), Integer.parseInt("" + request.getSession().getAttribute("userId")));
		} catch (Exception ex) {
			ResponseDTO response = new ResponseDTO();
			logger.warn(ex);
			response.message = "Failed to mark Credit Bill As Delivered!";
			response.result = "ERROR";
			restaurantServices.emailException(ExceptionUtils.getStackTrace(ex),request);
			logger.info("Exception mail sent");
			return response;
		}
	}

	@RequestMapping(value = "/listDispatchedOrSuccessCreditBill/{ffcId}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "This API requires user login, please login with respective user. Note* this API get some data from user session")
	public List<CreditDispatchedBillDTO> listDispatchedOrSuccessCreditBill(@PathVariable("ffcId") int ffcId, HttpServletRequest request) throws Exception {
		return customerCreditService.listDispatchedOrSuccessCreditBill(ffcId, Integer.parseInt("" + request.getSession().getAttribute("userId")));
	}

	@RequestMapping(value = "/getCreditInfo/{customerId}", method = RequestMethod.GET)
	@ResponseBody
	public CreditInfoDTO getCreditInfo(@PathVariable("customerId") int customerId, HttpServletRequest request) {
		return customerCreditService.getCreditInfo(customerId);
	}


	@RequestMapping(value = "/test-Bill", method = RequestMethod.GET)
	@ResponseBody
	public String testBill(HttpServletRequest request) {
		customerCreditService.testBill();
		return "TEST";
	}


	/*---------------------------------------App(Android, iphone, etc)---------------------------------------------------------*/
	/*---------------------------------------Authentication---------------------------------------------------------*/

	@RequestMapping(value = "/registerUser", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public String registerUser(@Valid @RequestBody CustomerRegisterationDTO customer,BindingResult result, HttpServletRequest request, HttpServletResponse response){
		if (result.hasErrors()) {
			return result.getAllErrors().toString();
		}
		return customerService.registerCustomer(customer);
	}

	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	@ResponseBody
	public Customer authenticate(HttpServletRequest request, HttpServletResponse response,@ApiParam(value="Phone no. with country code") @RequestParam String phoneNumber,@ApiParam(required=false ,value="Optional")@RequestParam(required=false) String simNumber, @RequestParam String orgId){
		return customerService.authenticate(phoneNumber, simNumber, Integer.parseInt(orgId));
	}

	@RequestMapping(value = "/verifyOTP", method = RequestMethod.POST)
	@ResponseBody
	public Customer verifyOTP(HttpServletRequest request, HttpServletResponse response,@ApiParam(value="Phone no. with country code") @RequestParam String phoneNumber, @ApiParam(required=false ,value="Optional") @RequestParam(required=false) String simNumber, @RequestParam String OTP, @RequestParam String orgId ){
		return customerService.verifyOTP(phoneNumber, simNumber, OTP, Integer.parseInt(orgId));
	}

	@RequestMapping(value = "/setAccountStatus", method = RequestMethod.POST)
	@ResponseBody
	public boolean setAccountStatus(HttpServletRequest request, HttpServletResponse response,@ApiParam(value="Phone no. with country code") @RequestParam String phoneNumber, @ApiParam(value="Either 0 or 1. SO the input by user will be true/false or  Active/Inactive") @RequestParam String status,@RequestParam String orgId ){
		return customerService.setAccountStatus(phoneNumber, Integer.parseInt(status), Integer.parseInt(orgId));
	}

	@RequestMapping(value = "/fetchNewOTP", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public boolean fetchNewOTP(HttpServletRequest request, HttpServletResponse response ,@RequestBody FetchOTPDTO fetchOtp){
		logger.info(fetchOtp.phoneNumber+"---"+fetchOtp.orgId);
		return customerService.fetchNewOTP(fetchOtp.phoneNumber,fetchOtp.device,fetchOtp.orgId);
	}

	@RequestMapping(value = "/setForcedLogin", method = RequestMethod.POST)
	@ResponseBody
	public boolean setForcedLogin(HttpServletRequest request, HttpServletResponse response,@ApiParam(value="Phone no. with country code") @RequestParam String phoneNumber,@RequestParam String orgId){
		return customerService.setForcedLogin(request.getParameter(phoneNumber), Integer.parseInt(orgId));
	}

	@RequestMapping(value = "/sendOTPEmail", method = RequestMethod.POST)
	@ResponseBody
	public boolean emailOTP(HttpServletRequest request, HttpServletResponse response,@ApiParam(value="Phone no. with country code") @RequestParam String phoneNumber,@RequestParam String orgId){
		return customerService.emailOTP(request.getParameter(phoneNumber), Integer.parseInt(orgId));
	}

	/*----------------------------------OAUTH----------------------------------------------------------*/
	@RequestMapping(value = "/isCustomerFacebookIdExist", method = RequestMethod.POST)
	@ResponseBody
	public Customer isCustomerFacebookIdExist(HttpServletRequest request, HttpServletResponse response,@RequestParam String facebookId){
		if(facebookId!=null)
			return customerService.isCustomerFacebookIdExist(facebookId);
		return null;
	}

	@RequestMapping(value = "/signUp", method = RequestMethod.POST)
	@ResponseBody
	public String signUp(@RequestBody Customer customer,HttpServletRequest request, HttpServletResponse response,@RequestParam String restaurantId){

		Integer restaurantID = Integer.parseInt(restaurantId);
		Restaurant rest = restaurantServices.getRestaurant(restaurantID);
		Integer orgId = (rest != null) ? rest.getParentRestaurantId() : restaurantID;
		customer.setOrgId(orgId);
		return customerService.signUp(customer);
	}

	@RequestMapping(value = "/verifyOauthOTP", method = RequestMethod.POST)
	@ResponseBody
	public boolean verifyOauthOTP(HttpServletRequest request, HttpServletResponse response, @RequestParam String phone, @RequestParam String OTP) throws JSONException{
		//String mobileNumber = request.getParameter("phone");
		return customerService.verifyOTP(phone, OTP);
	}

	@RequestMapping(value = "/isCustomerAuthentic", method = RequestMethod.POST)
	@ResponseBody
	public boolean isCustomerAuthentic(HttpServletRequest request, HttpServletResponse response,@RequestParam String phoneNumber, @RequestParam Integer orgId){
		//String phoneNumber=null;
		if(phoneNumber!=null)
			return customerService.isCustomerAuthentic(phoneNumber,orgId);
		return false;
	}

	/*   OTP through SMS Test API */
	@RequestMapping(value = "/sendTestOTP/{otpDetails}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseDTO sendTestOTP(@PathVariable String otpDetails){
		return customerService.sendTestOTP(otpDetails);
	}
	/********************************Order History***************************************/

	@ApiOperation(value="This API is used to get all orders of customer.Using Organization id , phone number, customer Id. You can either use Phone number or custId but orgId is mandatory.  For detailed information of orders you have to add another parameter inDetail=true")
	@RequestMapping(value="/getAllOrderHistory", method=RequestMethod.GET)
	public @ResponseBody OrderHistory getOrderHistory(HttpServletRequest request,@ApiParam(required = false, value="Phone number with country code")@RequestParam(required=false) String phone, 
			@ApiParam(required = false, value="Required if phone number is not available")@RequestParam(required=false) String custId, @RequestParam String orgId,@ApiParam(required = false, value="True or False")@RequestParam(required=false) String inDetail){
        int orderCount = 0;
        OrderHistory orderHistory = new OrderHistory();
        /*String phone = request.getParameter("phone");
		String custId =  request.getParameter("custId");
		String orgId= request.getParameter("orgId");
		String information =  request.getParameter("inDetail");*/
        String information =inDetail;
		boolean inDetailData=false;
		if(information!=null){
			inDetailData =  Boolean.parseBoolean(information);
		}
		List<Restaurant> restaurantList =null;
		List<Check> checkList =  new ArrayList<Check>();
		List<CheckSummery> summery =  new ArrayList<CheckSummery>();

		Integer customerId=null;
		if(orgId!=null){
			restaurantList =  restaurantServices.listRestaurantByParentId(Integer.parseInt(orgId));	
		}
		if(restaurantList!=null){
			for(Restaurant rest :restaurantList){
				if(custId!=null){
					customerId = Integer.parseInt(custId);
				}
				List<Check> list  =  checkService.getCustomersCheckList(phone,customerId,rest.getRestaurantId());
				List<Check> filteredList =  new ArrayList<Check>();	
				if(list!=null){
					if(inDetailData){
						for(Check check:list){
							if(check.getOrders().size()>0){
								orderCount++;
								filteredList.add(check);
							}
						}
						checkList.addAll(filteredList);
					}
					else{
						for(Check check:list){
							if(check.getOrders().size()>0){
								CheckSummery checkSummery =  new  CheckSummery();
								checkSummery.setOpenTime(check.getOpenTime());
								checkSummery.setDeliveryAddress(check.getDeliveryAddress());
								checkSummery.setInvoiceAmount(check.getRoundOffTotal());
								checkSummery.setInvoiceId(check.getInvoiceId());
								checkSummery.setRewardPoints(check.getRewards());
								summery.add(checkSummery);
								orderCount++;
							}

						}
					}
				}

			}
			if(checkList.size()>1){
				Collections.sort(checkList, new Comparator<Check>() {
					public int compare(Check v1,Check v2) {
						return v2.getOpenTime().compareTo(v1.getOpenTime());
					}
				});
			}
			else if(summery.size()>1){
				Collections.sort(summery, new Comparator<CheckSummery>() {
					public int compare(CheckSummery v1,CheckSummery v2) {
						return v2.getOpenTime().compareTo(v1.getOpenTime());
					}
				});
			}
			orderHistory.setTotalOrders(orderCount);
			orderHistory.setOrdersDetail(checkList);
			orderHistory.setOrdersSummary(summery);
			orderHistory.setInvoiceLinkPrefix(apiPrefix);
		}
		return orderHistory;
	}

	@ApiOperation(value="This API is used to get limited orders of customer based on orderLimit parameters. Using Organization id , phone number, customer Id. You can either use phone number or custId but orgId is mandatory.  For detailed information of orders you have to add another parameter inDetail=true", notes="")
	@RequestMapping(value="/getLatestOrderHistory", method=RequestMethod.GET)
	public @ResponseBody OrderHistory getLatestOrders(HttpServletRequest request,@ApiParam(required = false,value="Phone number with country code") @RequestParam(required=false) String phone, 
			@ApiParam(required = false, value="Required if phone number is not available")@RequestParam(required=false) String custId, @RequestParam String orgId,
			@RequestParam String orderLimit,@ApiParam(required = false, value="True or False") @RequestParam(required=false) String inDetail){

		int orderCount=0;
		OrderHistory orderHistory = new OrderHistory();
		List<Check> latestOrders =  new ArrayList<Check>();
		List<CheckSummery> latestOrdersummery =  new ArrayList<CheckSummery>();
		/*String phone = request.getParameter("phone");
		String custId =  request.getParameter("custId");
		String orgId= request.getParameter("orgId");
		String orderLimit=request.getParameter("orderLimit");
		String information =  request.getParameter("inDetail");*/
		String information = inDetail;
		if(phone==null)
			phone=(String)request.getAttribute("phone");
		if(custId==null)
			custId = (String)request.getAttribute("custId");
		if(orgId==null)
			orgId = (String) request.getAttribute("orgId");
		if(orderLimit==null)
			orderLimit = (String)request.getAttribute("orderLimit");
		if(information==null)
			information =(String)request.getAttribute("inDetail");

		boolean inDetailData=false;
		if(information!=null){
			inDetailData =  Boolean.parseBoolean(inDetail);
		}

		int limit=0;
		if(orderLimit!=null){
			limit = Integer.parseInt(orderLimit);
		}
		List<Restaurant> restaurantList =null;
		List<Check> checkList =  new ArrayList<Check>();
		List<CheckSummery> summery =  new ArrayList<CheckSummery>();
		Integer customerId=null;
		if(orgId!=null){
			restaurantList =  restaurantServices.listRestaurantByParentId(Integer.parseInt(orgId));	
		}
		if(restaurantList!=null){
			for(Restaurant rest :restaurantList){
				if(custId!=null){
					customerId = Integer.parseInt(custId);
				}
				List<Check> list  =  checkService.getCustomersCheckList(phone,customerId,rest.getRestaurantId());
				List<Check> filteredList =  new ArrayList<Check>();	
				if(list!=null){
					if(inDetailData){
						for(Check check:list){
							if(check.getOrders().size()>0){
								filteredList.add(check);
								orderCount++;
							}
						}
						checkList.addAll(filteredList);
					}
					else{
						for(Check check:list){
							if(check.getOrders().size()>0){
								CheckSummery checkSummery =  new  CheckSummery();
								checkSummery.setOpenTime(check.getOpenTime());
								checkSummery.setDeliveryAddress(check.getDeliveryAddress());
								checkSummery.setInvoiceAmount(check.getRoundOffTotal());
								checkSummery.setInvoiceId(check.getInvoiceId());
								checkSummery.setRewardPoints(check.getRewards());
								summery.add(checkSummery);
								orderCount++;
							}
						}
					}
				}
			}
			if(checkList.size()>1){
				Collections.sort(checkList, new Comparator<Check>() {
					public int compare(Check v1,Check v2) {
						return v2.getOpenTime().compareTo(v1.getOpenTime());
					}
				});}
			if(summery.size()>1){
				Collections.sort(summery, new Comparator<CheckSummery>() {
					public int compare(CheckSummery v1,CheckSummery v2) {
						return v2.getOpenTime().compareTo(v1.getOpenTime());
					}
				});}

			if(inDetailData){
				if(checkList.size()>=limit){
					for(int i=0;i<limit;i++){
						latestOrders.add(checkList.get(i));	
					}
				}
				else{
					latestOrders.addAll(checkList);
				}}
			else{
				if(summery.size()>=limit){
					for(int i=0;i<limit;i++){
						latestOrdersummery.add(summery.get(i));	
					}
				}
				else{
					latestOrdersummery.addAll(summery);
				}	
			}
			orderHistory.setTotalOrders(orderCount);
			orderHistory.setOrdersDetail(latestOrders);
			orderHistory.setOrdersSummary(latestOrdersummery);
			orderHistory.setInvoiceLinkPrefix(apiPrefix);
		}
		return orderHistory;
	}

	@RequestMapping(value="/getOrdersListByYear", method=RequestMethod.GET)
	@ApiOperation(value="This API is used to get orders of compleate year. Using Organization id , phone number, customer Id. You can either use phone number or custId but orgId and year  is mandatory.  For detailed information of orders you have to add another parameter inDetail=true")
	public @ResponseBody OrderHistory getOrdersListByYear(HttpServletRequest request, @ApiParam(required = false, value="Phone number with country code") @RequestParam(required=false) String phone, 
			     @ApiParam(required = false, value="Required if phone number is not available")@RequestParam(required=false) String custId,@RequestParam String orgId,
			     @ApiParam(defaultValue="2017") @RequestParam String year, @ApiParam(required = false, value="True or False")@RequestParam(required=false) String inDetail) throws ParseException{
		int orderCount=0;
		OrderHistory orderHistory = new OrderHistory();
		/*String phone = request.getParameter("phone");
		String custId =  request.getParameter("custId");
		String orgId= request.getParameter("orgId");
		String year =  request.getParameter("year");
		String information =  request.getParameter("inDetail");*/
		String information = inDetail;
		String startYearDate = null;
		String endYearDate = null;
		if(year !=null){
			startYearDate  = year+"-00-00";
			Integer endYear = Integer.parseInt(year);
			if(endYear>0){
				endYear++;
				endYearDate =endYear+"-00-00";
			}
		}
		Date sdate = new SimpleDateFormat("yyyy-mm-dd").parse(startYearDate);
		Date edate = new SimpleDateFormat("yyyy-mm-dd").parse(endYearDate);
		boolean inDetailData=false;
		if(information!=null){
			inDetailData =  Boolean.parseBoolean(information);
		}
		List<Restaurant> restaurantList =null;
		List<Check> checkList =  new ArrayList<Check>();
		List<CheckSummery> summery =  new ArrayList<CheckSummery>();

		Integer customerId=null;
		if(orgId!=null){
			restaurantList =  restaurantServices.listRestaurantByParentId(Integer.parseInt(orgId));	
		}
		if(restaurantList!=null){
			for(Restaurant rest :restaurantList){
				if(custId!=null){
					customerId = Integer.parseInt(custId);
				}
				List<Check> list  =  checkService.getCustomersCheckListByYear(phone, customerId,rest.getRestaurantId(), sdate,edate);//
				List<Check> filteredList =  new ArrayList<Check>();		
				if(list!=null){
					if(inDetailData){
						for(Check check:list){
							if(check.getOrders().size()>0){
								filteredList.add(check);
								orderCount++;
							}
						}
						checkList.addAll(filteredList);
					}
					else{
						for(Check check:list){
							if(check.getOrders().size()>0){
								CheckSummery checkSummery =  new  CheckSummery();
								checkSummery.setOpenTime(check.getOpenTime());
								checkSummery.setDeliveryAddress(check.getDeliveryAddress());
								checkSummery.setInvoiceAmount(check.getRoundOffTotal());
								checkSummery.setInvoiceId(check.getInvoiceId());
								checkSummery.setRewardPoints(check.getRewards());
								summery.add(checkSummery);
								orderCount++;
							}

						}
					}
				}

			}
			if(checkList.size()>1){
				Collections.sort(checkList, new Comparator<Check>() {
					public int compare(Check v1,Check v2) {
						return v2.getOpenTime().compareTo(v1.getOpenTime());
					}
				});
			}
			else if(summery.size()>1){
				Collections.sort(summery, new Comparator<CheckSummery>() {
					public int compare(CheckSummery v1,CheckSummery v2) {
						return v2.getOpenTime().compareTo(v1.getOpenTime());
					}
				});
			}
			orderHistory.setTotalOrders(orderCount);
			orderHistory.setOrdersDetail(checkList);
			orderHistory.setOrdersSummary(summery);
			orderHistory.setInvoiceLinkPrefix(apiPrefix);
		}
		return orderHistory;
	}
	@RequestMapping(value = "/generateCustomerCreditBillPrint", method = RequestMethod.GET)
	@ApiOperation(value="To print Customer Credit Bill")
	public String generateCheckForPrint(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response, @RequestParam String statementId ) {
	//	String statementId = request.getParameter("statementId");
		CreditStatementDTO customerCreditBill=null;
		try {
			customerCreditBill =  customerCreditService.getCreditStatement(statementId);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				restaurantServices.emailException(ExceptionUtils.getStackTrace(e),request);
			} catch (UnsupportedEncodingException | MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			logger.info("Exception mail sent");
			return "No bill found";
		}
		if (customerCreditBill != null) {
			Restaurant org = restaurantServices.getRestaurant(customerCreditBill.orgId);
			DateFormat formatter;
			formatter = new SimpleDateFormat("dd/MM/yyyy");
			formatter.setTimeZone(TimeZone.getTimeZone(org.getTimeZone()));
			String statementDate = formatter.format(customerCreditBill.statementDate);
			map.put("statementDate", statementDate);
			map.put("customerCreditBill", customerCreditBill);
		}else {
			return "No bill found";
		}
		return "custom/creditDefaultbill";
	}
	@RequestMapping(value = "/autoEmailCreditBillFromServer", method = RequestMethod.GET)
	@ApiOperation(value="You can email Customer Credit bill using this API")
	public ResponseDTO autoEmailCreditBillFromServer(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response, @RequestParam String statementId) throws UnsupportedEncodingException, MessagingException {
		ResponseDTO  responseDTO= new ResponseDTO();
		//String statementId = request.getParameter("statementId");
		CreditStatementDTO customerCreditBill=null;
		Restaurant org = null;
		if(statementId !=null){
			 customerCreditBill =  customerCreditService.getCreditStatement(statementId);
		}else{
			responseDTO.message="StatementId should not be Null";
			responseDTO.result="FAILED";
			return responseDTO;
		}
		if(customerCreditBill.orgId!=null){
			 org =  restaurantServices.getRestaurant(customerCreditBill.orgId);
		}else{
			responseDTO.message="No organization found for this statement ";
			responseDTO.result="FAILED";
			return responseDTO;
		}
		if(customerCreditBill !=null && org !=null){
			return customerService.autoEmailCreditBillFromServer(customerCreditBill,org);
		}else{
			responseDTO.message="No bill/Organization found";
			responseDTO.result="FAILED";
			return responseDTO;
		}
	}
}
