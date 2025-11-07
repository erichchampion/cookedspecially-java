/**
 * 
 */
package com.cookedspecially.service.impl;

import com.cookedspecially.dao.CheckDAO;
import com.cookedspecially.dao.CustomerDAO;
import com.cookedspecially.dao.OrderDAO;
import com.cookedspecially.dao.SeatingTableDAO;
import com.cookedspecially.domain.*;
import com.cookedspecially.dto.DiscountDTO;
import com.cookedspecially.dto.OrderDTO;
import com.cookedspecially.dto.OrderDishDTO;
import com.cookedspecially.enums.check.CheckType;
import com.cookedspecially.enums.credit.CustomerCreditAccountStatus;
import com.cookedspecially.enums.order.Status;
import com.cookedspecially.service.*;
import com.cookedspecially.utility.DateUtil;
import com.cookedspecially.utility.MailerUtility;
import com.cookedspecially.utility.StringUtility;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author shashank, rahul
 *
 */
@Service
public class OrderServiceImpl implements OrderService {

	final static Logger logger = Logger.getLogger(OrderServiceImpl.class);
	@Autowired
	private OrderDAO orderDAO;

	@Autowired
	private CheckDAO checkDAO;

	@Autowired
	private UserService userService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private SeatingTableDAO seatingTableDAO;

	@Autowired
	private CustomerDAO customerDAO;

	@Autowired
	private RestaurantService restaurantServices;
	
	@Autowired
	private DishTypeService dishTypeService;
	
	@Autowired
	private JavaMailSenderImpl mailSender;
	
	@Autowired
	private SpringTemplateEngine templateEngine;
	
	@Autowired
	private CouponService couponService;
	
	@Override
	@Transactional
	public List<Order> listOrders(Map<String, Object> queryMap) {
		return orderDAO.listOrders(queryMap);
	}
	
	@Override
	@Transactional
	public void addOrder(Order order) {
		orderDAO.addOrder(order);
	}

	@Override
	@Transactional
	public void removeOrder(Integer id) throws Exception {
		orderDAO.removeOrder(id);
	}

	
	@Override
	@Transactional
	public Order getOrder(Integer id) {
		return orderDAO.getOrder(id);
	}

	@Override
	@Transactional
	public List<Integer> getAllOpenOrderCheckIds(Integer restaurantId) {
		return orderDAO.getAllOpenOrderCheckIds(restaurantId);
	}

/*	public List<Order> getDailyDeliveryBoyInvoice(Integer restaurantId,Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return orderDAO.getDailyDeliveryBoyInvoice(restaurantId, startDate, endDate);
	}*/

	@Override
	@Transactional
	public List<OrderDTO> getOrders(Integer restaurantId,List<String> orderType, String ordersOfDay){
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("restaurantId", restaurantId);

		List<Status> orderStatusList = new ArrayList<>();
		for(String status : orderType){
			orderStatusList.add(Enum.valueOf(Status.class, status.toUpperCase()));
		}
		Date fromDate=null;
		Date toDate =null;
		if(orderStatusList.size() > 0 )
			queryMap.put("status",orderStatusList);

		Restaurant restaurant  =  restaurantServices.getRestaurant(restaurantId);
		Date now = null;

		if(ordersOfDay.equalsIgnoreCase("future")){
			 now = DateUtil.nowInSpecifiedTimeZone(restaurant.getTimeZone());
			 now = DateUtil.addToDate(now,1,0,0,restaurant.getTimeZone());
			 fromDate = DateUtil.getStartOfDay(now,restaurant.getTimeZone());
			 toDate = DateUtil.getEndOfDay(now,restaurant.getTimeZone());
		}else{
			now = DateUtil.yesterdaySpecifiedTimeZone(restaurant.getTimeZone());
			fromDate = DateUtil.getStartOfDay(now,restaurant.getTimeZone());
			now = DateUtil.nowInSpecifiedTimeZone(restaurant.getTimeZone());
			toDate = DateUtil.getEndOfDay(now,restaurant.getTimeZone());
		}

		queryMap.put("fromDate", fromDate);
		queryMap.put("toDate", toDate);

		List<Order> orderList= orderDAO.getOpenOrders(queryMap);
		List<OrderDTO> dtoList = new ArrayList<OrderDTO>();
		for(Order order : orderList){
			OrderDTO dto = getOrderDTOFromOrder(order);
			if(dto != null)
				dtoList.add(dto);
			dto = null;
		}
		orderList = null;
		System.gc();
		return dtoList;
	}

	private OrderDTO getOrderDTOFromOrder(final Order order) {

		Check check = checkDAO.getCheck(order.getCheckId());
		if(check == null)
			return null;

		OrderDTO dto = new OrderDTO();

		dto.id = order.getOrderId().toString();
		dto.checkId = order.getCheckId().toString();
		if(check.getCheckType() != null)
			dto.orderType = check.getCheckType().name();
		dto.deliveryTime = check.getDeliveryTime();
		dto.orderTime = order.getCreatedTime();
		
		dto.lastModified = order.getModifiedTime();
		dto.deliveryAddress = check.getDeliveryAddress();
		dto.fulfillmentCenterId = check.getKitchenScreenId();
		dto.changeAmount = new Float(order.getMoneyOut()).longValue();
		if(order.getDeliveryAgent()!=null){
		if(Character.isDigit(order.getDeliveryAgent().charAt(0))){
			Integer agentId = Integer.parseInt(order.getDeliveryAgent());
			User deliveryBoy= userService.getUser(agentId);
		if(deliveryBoy!=null){
			dto.deliveryAgent = deliveryBoy.getFirstName()+" "+deliveryBoy.getLastName()+" "+" ("+deliveryBoy.getContact()+")";
			dto.deliveryAgentId = deliveryBoy.getUserId();
		}
		}else {
			dto.deliveryAgent = order.getDeliveryAgent();
		}
		}
		dto.instructions = check.getDeliveryInst();
		dto.taxJsonObj = check.getTaxJsonObject();
		dto.discountList = getDiscountList(check.getDiscount_Charge());
		dto.deliveryCharges = Math.round(check.getOutCircleDeliveryCharges());

		dto.customerName = check.getName();
		dto.customerMobNo = check.getPhone();
		dto.customerId = check.getCustomerId();
		dto.orderSource = check.getOrderSource();
		dto.status = order.getStatus().name();
		dto.invoiceId = check.getInvoiceId();
		dto.paymentStatus = check.getStatus();
		dto.orderAmount = check.getRoundOffTotal();
		dto.isfirstOrder=check.isFirstOrder();
		dto.allowEdit=check.isAllowEdit();
		if(Status.CANCELLED==order.getStatus() || check.getStatus()==com.cookedspecially.enums.check.Status.Cancel){
			dto.isEdited = false;
		}else if(check.getEditOrderRemark()!=null){
			dto.isEdited = true;
		}
		dto.creditBalance = check.getCreditBalance();
		if(order.getPaymentStatus() != null)
			dto.paymentMethod = order.getPaymentStatus();
		else
			dto.paymentMethod = "Unknown";

		if(check.getCoupon_Applied()!=null){
			List<Coupon> coupon = new ArrayList<>();
			for(Coupon coup : check.getCoupon_Applied()){
				coupon.add(couponService.getCouponById(coup.getCoupanId()));
			}
			dto.couponApplied = coupon;
			}
		Customer customer = customerDAO.getCustomer(check.getCustomerId());
		
		if(customer !=null){
		dto.customerEmail = customer.getEmail();

		if(dto.orderType == CheckType.Table.name()) {
			SeatingTable table = seatingTableDAO.getSeatingTable(check.getTableId());
			dto.table = table.getName();
		}
		else if(dto.orderType == CheckType.Delivery.name()){
			dto.deliveryArea = check.getDeliveryArea();
		}
		else if(dto.orderType == CheckType.MorningExpress.name()){
			dto.deliveryArea = check.getDeliveryArea();
		}
		else if(dto.orderType == CheckType.TakeAway.name()){
			dto.customerName = customer.getFirstName()+" "+customer.getLastName();
		}
		}
		else {
			logger.info("Customer is Null "+customer +" for check id :" +check.getCheckId());
		}
		List list1 = getItemsDTOFromOrder(order.getOrderDishes());
		check = null;
		dto.items = list1;
		list1 = null;
		return dto;
	}

	private List<DiscountDTO> getDiscountList(List<Order_DCList> discount_charge) {
		List<DiscountDTO> discountDTOList = new ArrayList<DiscountDTO>();

		for(Order_DCList dc : discount_charge){
			DiscountDTO dto = new DiscountDTO();
			dto.id = dc.getDcId();
			dto.name = dc.getName();
			dto.type = dc.getType();
			dto.value = dc.getValue();
			discountDTOList.add(dto);
		}
		return discountDTOList;
	}

	private List<OrderDishDTO> getItemsDTOFromOrder(List<OrderDish> orderList ){
		List<OrderDishDTO> list = new ArrayList();
		for (OrderDish dish : orderList ){
			OrderDishDTO dishDTO = new OrderDishDTO();
			dishDTO.name = dish.getName();
			dishDTO.itemId = dish.getDishId();
			dishDTO.quantity = dish.getQuantity();
			dishDTO.itemType = dish.getDishType();
			dishDTO.instructions = dish.getInstructions();
			dishDTO.price = dish.getPrice();
			dishDTO.dishSizeName = dish.getDishSize();
			if(dish.getDishSize()!=null && !("".equalsIgnoreCase(dish.getDishSize()))){
				List<Dish_Size> sizeList = dishTypeService.getDish_SizeListbyDishId(dish.getDishId());
				for(Dish_Size size :sizeList ){
				if(size.getName().equalsIgnoreCase(dish.getDishSize())){
					dishDTO.dishSizeId=size.getDishSizeId();
				}
				}
				
			}
			
			List<JsonAddOn> list1 = getAddonDTOFromOrder(dish.getOrderAddOn());
			dishDTO.addOns = list1;
			list.add(dishDTO);
			list1 = null;
			dishDTO = null;
		}
		return list;
	}

	private List<JsonAddOn> getAddonDTOFromOrder(List<OrderAddOn> addOnList ){
		List<JsonAddOn> list = new ArrayList();

		for (OrderAddOn addOn : addOnList ){
			JsonAddOn ja =  new JsonAddOn();
			OrderDishDTO dishDTO = new OrderDishDTO();
			ja.id = addOn.getAddOnId();
			ja.name = addOn.getName();
			ja.price = addOn.getPrice();
			ja.smallImageUrl = addOn.getSmallImageUrl();
			list.add(ja);
		}
		return list;
	}

	@Override
	@Transactional
	public List<OrderDTO> getDispatchedCancelOrders(Integer restaurantId,
			List<String> orderType, String ordersOfDay) {
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("restaurantId", restaurantId);

		List<Status> orderStatusList = new ArrayList<>();
		for(String status : orderType)
			orderStatusList.add(Enum.valueOf(Status.class, status.toUpperCase()));

		if(orderStatusList.size() > 0 )
			queryMap.put("status",orderStatusList);

		Restaurant restaurant  =  restaurantServices.getRestaurant(restaurantId);
		Date now = null;
		Date fromDate =null;
		Date toDate =null;

		if(ordersOfDay.equalsIgnoreCase("future")){
			 now = DateUtil.nowInSpecifiedTimeZone(restaurant.getTimeZone());
			 now = DateUtil.addToDate(now,1,0,0,restaurant.getTimeZone());
			 fromDate = DateUtil.getStartOfDay(now,restaurant.getTimeZone());
			 toDate = DateUtil.getEndOfDay(now,restaurant.getTimeZone());
		}else{
			now = DateUtil.yesterdaySpecifiedTimeZone(restaurant.getTimeZone());
			fromDate = DateUtil.getStartOfDay(now,restaurant.getTimeZone());
			now = DateUtil.nowInSpecifiedTimeZone(restaurant.getTimeZone());
			toDate = DateUtil.getEndOfDay(now,restaurant.getTimeZone());
		}
		
		queryMap.put("fromDate", fromDate);
		queryMap.put("toDate", toDate);
		
		

		List<Order> orderList= orderDAO.getOpenDispatchedCancelOrders(queryMap);
		List<OrderDTO> dtoList = new ArrayList<OrderDTO>();
		for(Order order : orderList){
			OrderDTO dto = getOrderDTOFromOrder(order);
			if(dto != null)
				dtoList.add(dto);
			dto = null;
		}
		orderList = null;
		System.gc();
		return dtoList;
	}
	
	public String emailCreditCheckFromServer(HttpServletRequest request,Check check,int customerId, String emailAddr, Status status,String refund, String subject, String sender,String reason,float extraAmount,String h) throws MessagingException, UnsupportedEncodingException{
 		// Prepare the evaluation context
 	    final Context ctx = new Context(request.getLocale());
 	    if(subject==null){
 	    	subject = "Your Credit Info";
 	    }
 	    
 	    String templateName="emailCreditBill";
 		logger.info("***********************sending check***********************************");
 		logger.info("Customer Credit Check emailling in process.. : check Id: "+check.getCheckId());
 		logger.info("Email Address: " + emailAddr);
 		User user = null;
 		Restaurant org=null;
 		//CreditBill creditBill=null;
 		Customer customer=null;
 			if (check != null) {
 				customer  = customerService.getCustomer(customerId);
 				String description  = setDescription(status,customer,check,refund,reason,extraAmount);
 				org=restaurantServices.getRestaurant(customer.getOrgId());
 				CustomerCreditBill customerCreditBill = new CustomerCreditBill(customer,check,description);
 				ctx.setVariable("customerCreditBill", customerCreditBill);
 				ctx.setVariable("user", org);
 				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(org.getTimeZone()));
 				cal.setTime(customerCreditBill.getDate());
 				DateFormat formatter1;
 				formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
 				formatter1.setTimeZone(cal.getTimeZone());
 				ctx.setVariable("creditBillDate", formatter1.format(cal.getTime()));
 				
 	    // Prepare message using a Spring helper
 	    final MimeMessage mimeMessage = mailSender.createMimeMessage();
 	    final MimeMessageHelper message =
 	        new MimeMessageHelper(mimeMessage, false, "UTF-8"); // true = multipart

 	    message.setSubject(subject);
 	    if (org != null) {
 	    	//String senderEmail = StringUtility.isNullOrEmpty(rest.getMailUsername()) ? user.getUsername() : rest.getMailUsername();
 	    	String senderEmail=org.getMailUsername();
 	    	InternetAddress restaurantEmailAddress = new InternetAddress(senderEmail, org.getBussinessName());
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
 	    if (!StringUtility.isNullOrEmpty(org.getMailUsername()) && !StringUtility.isNullOrEmpty(org.getMailPassword())) {
 	    	oldUsername = mailSender.getUsername(); 
 	    	oldPassword = mailSender.getPassword();
 	    	oldHost = mailSender.getHost();
 	    	oldProtocol = mailSender.getProtocol();
 	    	oldPort = mailSender.getPort();
 	    	if(sender!=null){
 	    		MailerUtility mu = new MailerUtility();
 	    		mailSender.setUsername(mu.username);
 		    	mailSender.setPassword(mu.password);
 	    	}else{
 	    	mailSender.setUsername(org.getMailUsername());
 	    	mailSender.setPassword(org.getMailPassword());
 	    	}
 	    	mailSender.setHost(org.getMailHost());
 	    	mailSender.setProtocol(org.getMailProtocol());
 	    	mailSender.setPort(org.getMailPort());
 	    }
 	    
 	    mailSender.send(mimeMessage);
         logger.info("emmail sent for email Id :" + customer.getEmail());
 	    if (!StringUtility.isNullOrEmpty(oldUsername) && !StringUtility.isNullOrEmpty(oldPassword)) {
 	    	mailSender.setUsername(oldUsername);
 	    	mailSender.setPassword(oldPassword);
 	    	mailSender.setHost(oldHost);
 	    	mailSender.setProtocol(oldProtocol);
 	    	mailSender.setPort(oldPort);
 	    }
 		} else {
 				logger.info("No check Found for "+emailAddr);
 				return "No check found";
 		}
 	    logger.info("***********************sending end***********************************");
 	    return "Email Sent Successfully";
 	}
    String setDescription(Status status,Customer customer,Check check,String refund,String reason,float extraAmount) {
    	if(customer.getCredit().getStatus()==CustomerCreditAccountStatus.ACTIVE){
    		Restaurant restaurant =  restaurantServices.getRestaurant(check.getRestaurantId());
	    	if(status==Status.CANCELLED && "CREDIT".equalsIgnoreCase(refund)){
	    		return "Your order No. "+check.getOrderId()+" (of  Total Rs."+check.getRoundOffTotal()+") has been cancelled. We owe you  Rs "+Math.abs(customer.getCredit().getCreditBalance())+"."
	    				+ " Which will be automatically deducted from your next order payment. "
	    				+ "Should there be any concern pls call at "+restaurant.getBussinessPhoneNo()+".  Assuming you of our best, always.";
	    	}else if(status==Status.CANCELLED && "CASH".equalsIgnoreCase(refund)){
	    		return "Your order with order No. "+check.getOrderId()+" has been cancelled. We have initiated the refund as CASH. One of our delivery boy will hand over it to you soon.";
	    	}else if("COMPLETED".equalsIgnoreCase(refund)&& "DELIVERED".equalsIgnoreCase(reason)){
	    		return "Your order No. "+check.getOrderId()+" (of  Total Rs."+check.getRoundOffTotal()+") has been DELIVERED. Your current balance with "+restaurant.getRestaurantName()+" is "+Math.abs(customer.getCredit().getCreditBalance())+"."
	    				+"Should there be any concern pls call at "+restaurant.getBussinessPhoneNo()+".  Assuming you of our best, always.";
	    	}else if("UPDATE".equalsIgnoreCase(refund) && "DISCOUNT".equalsIgnoreCase(reason)){
	    		return "Your order No. "+check.getOrderId()+" has been discounted by Rs "+extraAmount+". We owe you  Rs "+Math.abs(customer.getCredit().getCreditBalance())+"."
	    				+ " Which Will automatically deducted from your next order payment. Should there be any concern pls call at "+restaurant.getBussinessPhoneNo()+".  Assuming you of our best, always.";
	    	}else if("UPDATE".equalsIgnoreCase(refund) && "ADDED".equalsIgnoreCase(reason)){
	    		return "Your order No. "+check.getOrderId()+" has been discounted by Rs "+extraAmount+". You owe us  Rs "+Math.abs(customer.getCredit().getCreditBalance())+"."
	    				+ " Which will automatically added to your next order payment. Should there be any concern pls call at "+restaurant.getBussinessPhoneNo()+".  Assuming you of our best, always.";
	    	}else if("UPDATE".equalsIgnoreCase(refund) && "CHARGED".equalsIgnoreCase(reason)){
	    		return "Your order No. "+check.getOrderId()+" has been discounted by Rs "+extraAmount+". You owe us  Rs "+Math.abs(customer.getCredit().getCreditBalance())+"."
	    				+ " Which will automatically added to your next order payment. Should there be any concern pls call at "+restaurant.getBussinessPhoneNo()+".  Assuming you of our best, always.";
	    	}else if("PAYMENT_TYPE_CHANGED".equalsIgnoreCase(reason)){
	    		if(customer.getCredit().getCreditBalance()<0){
	    			return "Your order No. "+check.getOrderId()+" has been changed to "+check.getOrders().get(0).getPaymentStatus()+" from "+refund+". We owe you Rs "+Math.abs(customer.getCredit().getCreditBalance())+"."
		    				+ " Which will automatically deducted from your next order payment. Should there be any concern pls call at "+restaurant.getBussinessPhoneNo()+".  Assuming you of our best, always.";
	    		}else{
	    			return "Your order No. "+check.getOrderId()+" has been changed to "+check.getOrders().get(0).getPaymentStatus()+" from "+refund+". You owe us Rs "+Math.abs(customer.getCredit().getCreditBalance())+"."
		    				+ " Which will automatically added to your next order payment. Request you to make this payment at earliest. Should there be any concern pls call at "+restaurant.getBussinessPhoneNo()+".  Assuming you of our best, always.";
	    		}
      		} else if(status==Status.CANCELLED && refund==null){
      			if(customer.getCredit().getCreditBalance()<0){
      				return "Your order No. "+check.getOrderId()+" (of  Total Rs."+check.getRoundOffTotal()+") has been cancelled. We owe you Rs "+Math.abs(customer.getCredit().getCreditBalance())+"."
      						+ " Which will automatically deducted from your next order payment. Should there be any concern pls call at "+restaurant.getBussinessPhoneNo()+".  Assuming you of our best, always.";
      			}else if(customer.getCredit().getCreditBalance()>0){
      				return "Your order No. "+check.getOrderId()+" (of  Total Rs."+check.getRoundOffTotal()+") has been cancelled. You owe us Rs "+Math.abs(customer.getCredit().getCreditBalance())+"."
      						+ " Which will automatically added to your next order payment. Should there be any concern pls call at "+restaurant.getBussinessPhoneNo()+".  Assuming you of our best, always.";

      			}
	    	}
    		else if(refund==null){
	    		return "Your current balance with "+restaurant.getRestaurantName()+" is "+Math.abs(customer.getCredit().getCreditBalance())+"."
	    				+"Should there be any concern pls call at "+restaurant.getBussinessPhoneNo()+".  Assuming you of our best, always.";
	    	}
    }
		return null;
     }
}