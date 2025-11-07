package com.cookedspecially.controller;

import io.swagger.annotations.Api;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cookedspecially.domain.Check;
import com.cookedspecially.domain.Customer;
import com.cookedspecially.domain.Customers;
import com.cookedspecially.domain.RestaurantInfo;
import com.cookedspecially.dto.PlaceOrderDTO;
import com.cookedspecially.dto.zomato.common.ZomatoOrderStatus;
import com.cookedspecially.dto.zomato.order.ZomatoOrderDTO;
import com.cookedspecially.enums.check.Status;
import com.cookedspecially.service.CheckService;
import com.cookedspecially.service.CustomerService;
import com.cookedspecially.service.DeliveryAreaService;
import com.cookedspecially.service.OrderService;
import com.cookedspecially.service.RestaurantService;
import com.cookedspecially.service.ZomatoService;

@Controller
@RequestMapping("/zomato")
@Api(description="Place Zomato Order REST API's")
public class ZomatoController {

	final static Logger logger = Logger.getLogger(ZomatoController.class);
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	CheckService checkService;
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	OrderController orderCotroller;
	
	@Autowired
	CustomerController customerCotroller;
	
	@Autowired
	ZomatoService zomatoService;
	
	@Autowired
	RestaurantService restaurantService;
	
	@Autowired
	RestaurantController restaurantController;
	
	@Autowired
	DeliveryAreaService deliveryAreaService;
	
	@RequestMapping(value="/placeOrderFromZomato",method=RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public @ResponseBody Map<String,Object> placeOrderFromZomato(@RequestBody ZomatoOrderDTO body,HttpServletRequest request, HttpServletResponse response,Model model) throws Exception {
   	 	Map<String,Object> map =  new HashMap<String, Object>();
   	 	PlaceOrderDTO orderBody =  null;
   	 	RestaurantInfo restaurant =  restaurantController.getReataurantInfo(request,response,body.order.outlet_id);
   	 	logger.info("Order request recieved for "+body.order.outlet_id);
   	 	Check getCheck = zomatoService.getZomatoOrderById(body.order.order_id);
   	 	if(getCheck==null){
	   	 	if(body.order.customer_details.phone_number!=null){
	   			if(body.order.customer_details.phone_number.length()==10){
	   				if(restaurant.getCountryCode()!=null){
	   					body.order.customer_details.phone_number =restaurant.getCountryCode()+""+body.order.customer_details.phone_number;
	   				}else{
	   					body.order.customer_details.phone_number ="+91"+body.order.customer_details.phone_number;
	   				}
	   			}
	   			Customers customer = customerCotroller.getCustomerInfoJSON(body.order.customer_details.phone_number,null, body.order.outlet_id, request, response);
	   			orderBody = zomatoService.convertZomatoJsonToPlaceOrderDTO(body.order);
	   			Customer cust = customer.getCustomers().get(0);
	   			orderBody.customer.id=cust.getCustomerId();
	   			map = orderCotroller.placeOrder(orderBody, model, request, response, body.order.outlet_id.toString());
	   			String status = (String) map.get("status");
	   			Check check =  checkService.getCheck((Integer) map.get("checkId"));
	   			if("success".equalsIgnoreCase((String) map.get("status"))){
	   				check.setZomatoOrderId(body.order.order_id);
	   				checkService.addCheck(check);
	   				zomatoService.zomatoOrderConfirm(check,Integer.parseInt(body.order.order_date_time),request);
	   			}else{
	   				zomatoService.zomatoOrderReject(check,request);
	   			}
	   			if("error".equalsIgnoreCase(status)){
	   				logger.info(map.get("error"));
	   			}
	   			else {
	   				logger.info("Order placed!");
	   			}
	   		}
	   	 	return map;
   	 	}else{
   	 		logger.info(getCheck.getInvoiceId()+" Duplicate Order hence avoided! "+body.order.order_id);
   	 		return map;
   	 	}
    }
	
	@RequestMapping(value="/zomatoOrderStatus",method=RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public void zomatoOrderStatus(@RequestBody ZomatoOrderStatus body,HttpServletRequest request, HttpServletResponse response,Model model) throws Exception {
	
		Check check = zomatoService.getZomatoOrderById(Integer.parseInt(body.order_id));
		check.setBill(0);
		check.setRoundOffTotal(0);
		check.setCreditBalance(0);
		check.setOutCircleDeliveryCharges(0);
		check.setStatus(Status.Cancel);
		if(check.getOrders()!=null){
			check.getOrders().get(0).setStatus(com.cookedspecially.enums.order.Status.CANCELLED);
		}
		check.setEditOrderRemark(body.reason);
		if("timeout".equalsIgnoreCase(body.action)){
			logger.info("Zomato Order Timeout InvoiceId: "+check.getInvoiceId());
			checkService.addCheck(check);
		}else if("reject".equalsIgnoreCase(body.action)){
			
			if(!(check.getOrders().get(0).getStatus()==com.cookedspecially.enums.order.Status.OUTDELIVERY || check.getOrders().get(0).getStatus()==com.cookedspecially.enums.order.Status.DELIVERED)){
				logger.info("Zomato Order cancelled InvoiceId: "+check.getInvoiceId());
				checkService.addCheck(check);
			}
		}
		
	}
}
