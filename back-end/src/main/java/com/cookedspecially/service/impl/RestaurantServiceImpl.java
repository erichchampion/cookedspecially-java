/**
 * 
 */
package com.cookedspecially.service.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import com.cookedspecially.controller.CashRegisterController;
import com.cookedspecially.controller.OrderController;
import com.cookedspecially.dao.RestaurantDAO;
import com.cookedspecially.domain.Check;
import com.cookedspecially.domain.CreditTransactions;
import com.cookedspecially.domain.Discount_Charges;
import com.cookedspecially.domain.Nutrientes;
import com.cookedspecially.domain.OrderSource;
import com.cookedspecially.domain.PaymentType;
import com.cookedspecially.domain.Restaurant;
import com.cookedspecially.dto.OrderStatusDTO;
import com.cookedspecially.enums.check.PaymentMode;
import com.cookedspecially.enums.check.Status;
import com.cookedspecially.enums.credit.CreditTransactionStatus;
import com.cookedspecially.service.CheckService;
import com.cookedspecially.service.CustomerCreditService;
import com.cookedspecially.service.RestaurantService;
import com.cookedspecially.service.UserService;
import com.cookedspecially.utility.MailerUtility;
import com.cookedspecially.utility.StringUtility;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author shashank , rahul, Abhishek
 *
 */
@Service
public class RestaurantServiceImpl implements RestaurantService {

	final static Logger logger = Logger.getLogger(CashRegisterController.class);

	@Autowired
	private RestaurantDAO restaurantDAO;
	
	@Autowired
	private JavaMailSenderImpl mailSender;
	
	@Autowired
	private SpringTemplateEngine templateEngine;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CheckService checkService;

	@Autowired
	private OrderController orderController;
	
	@Autowired
	@Qualifier("customerCreditAutomatedBilling") 
	private CustomerCreditService customerCreditService;
	
	@Override
	@Transactional
	public void addRestaurant(Restaurant restaurant) {
		restaurantDAO.addRestaurant(restaurant);
		if(restaurant.getParentRestaurantId()!=null)
		    userService.createServiceUser(restaurant);
	}

	@Override
	@Transactional
	public List<Restaurant> listRestaurant() {
		return restaurantDAO.listRestaurant();
	}

    @Override
    @Transactional
	public List<Restaurant> listRestaurantById(Integer restaurantId) {
		//return restaurantDAO.listRestaurantById(restaurantId);
		return restaurantDAO.listRestaurantById(restaurantId);
	}

	@Override
	@Transactional
	public void removeRestaurant(Integer id) {
		restaurantDAO.removeRestaurant(id);
	}

    @Override
    @Transactional
	public Restaurant getRestaurant(Integer id) {
		return restaurantDAO.getRestaurant(id);
	}

	@Override
	@Transactional
	public List<Restaurant> listRestaurantByParentId(Integer parentId){
        return restaurantDAO.listRestaurantByParentId(parentId);
    }

	@Override
	@Transactional
	public Restaurant getRestaurantByName(String restaurantName) {
		return restaurantDAO.getRestaurantByName(restaurantName);
	}

	@Override
	@Transactional
	public List<Discount_Charges> listDiscountCharges(Integer restId) {
		// TODO Auto-generated method stub
		return restaurantDAO.listDiscountCharges(restId);
	}

	@Override
	@Transactional
	public void addDC(Discount_Charges discount_Charges) {
		// TODO Auto-generated method stub
		restaurantDAO.addDC(discount_Charges);
	}

	@Override
	@Transactional
	public void removeDC(Integer id) {
		// TODO Auto-generated method stub
		restaurantDAO.removeDC(id);
	}

	@Override
	@Transactional
	public Discount_Charges getDCById(Integer id) {
		// TODO Auto-generated method stub
		return restaurantDAO.getDCById(id);
	}

	@Override
	@Transactional
	public List<Nutrientes> getNutirentList(Integer restId) {
		// TODO Auto-generated method stub
		return restaurantDAO.getNutirentList(restId);
	}

	@Override
	@Transactional
	public void removeNutrientes(Integer id) {
		// TODO Auto-generated method stub
		restaurantDAO.removeNutrientes(id);

    }

	@Override
	@Transactional
	public void addNutrientes(Nutrientes nutrientes) {
		// TODO Auto-generated method stub
		restaurantDAO.addNutrientes(nutrientes);
	}

	@Override
	@Transactional
	public Nutrientes getNutrientes(Integer id) {
		// TODO Auto-generated method stub
		return restaurantDAO.getNutrientes(id);
	}

	@Override
	@Transactional
	public Nutrientes getByNutrientesByNameType(String name, String dishType,
			Integer restId) {
		// TODO Auto-generated method stub
		return restaurantDAO.getByNutrientesByNameType(name, dishType, restId);
	}

	@Override
	@Transactional
	public void addOrderSource(OrderSource orderSource) {
		// TODO Auto-generated method stub
		restaurantDAO.addOrderSource(orderSource);
	}

	@Override
	@Transactional
	public List<OrderSource> listOrderSourcesByOrgId(Integer orgId) {
		// TODO Auto-generated method stub
		return restaurantDAO.listOrderSourcesByOrgId(orgId);
	}

	@Override
	@Transactional
	public void removeOrderSources(Integer id) {
		// TODO Auto-generated method stub
		restaurantDAO.removeOrderSources(id);
	}

	@Override
	@Transactional
	public OrderSource getOrderSources(Integer id) {
		// TODO Auto-generated method stub
		return restaurantDAO.getOrderSources(id);
	}

	@Override
	@Transactional
	public void addPaymentType(PaymentType paymentType) {
		// TODO Auto-generated method stub
		restaurantDAO.addPaymentType(paymentType);
	}

	@Override
	@Transactional
	public List<PaymentType> listPaymentTypeByOrgId(Integer orgId) {
		// TODO Auto-generated method stub
		return restaurantDAO.listPaymentTypeByOrgId(orgId);
	}

	@Override
	@Transactional
	public void removePaymentType(Integer id) {
		// TODO Auto-generated method stub
		restaurantDAO.removePaymentType(id);
	}

	@Override
	@Transactional
	public PaymentType getPaymentType(Integer id) {
		return restaurantDAO.getPaymentType(id);
	}

    @Override
    @Transactional
	public PaymentType getPaymentTypeByName(Integer restaurantId, String name) {
		try{
		return restaurantDAO.getPaymentTypeByName(name, getParentRestaurant(restaurantId).getRestaurantId());
		}catch(NullPointerException nExp){
			logger.info("NE, Could not get Parent Restaurant of Restaurant id="+restaurantId);
		}
		catch (Exception e) {
			logger.info("Could not get Parent Restaurant of Restaurant id="+restaurantId);
		}
		return null;
    }

	@Override
	@Transactional
	public Restaurant getParentRestaurant(int restaurantId) {
		Restaurant restaurant=getRestaurant(restaurantId);
		if(restaurant.getParentRestaurantId()!=null && restaurantId!=restaurant.getParentRestaurantId())
		{
			return getRestaurant(restaurant.getParentRestaurantId());
		}
		return restaurant;
	}

	@Override
	@Transactional
	public List<Integer> getAllOrganisation() {
		logger.info("Getting list organisation list");
		return restaurantDAO.getAllOrganisation();
	}

	@Override
	public String getRestaurantUnitInfoForAssociatedCustomer(int customerId, String property) {
		return restaurantDAO.getRestaurantUnitInfoForAssociatedCustomer(customerId,property);
	}

	@Override
	public OrderStatusDTO checkPaytmOrderStatus(Integer orderId) throws IOException {
		JsonObject json = new JsonObject();
		OrderStatusDTO paymentStatus=  new OrderStatusDTO();
		if(orderId!=null){
			String propFileName = "paytmDetails.properties";
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			Properties prop = new Properties();
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
			 String mid = prop.getProperty("MID");
			 String statusURL=prop.getProperty("statusURL");
			 json.addProperty("MID",mid);
			 json.addProperty("ORDERID",orderId.toString());
			 String url = statusURL+json.toString();
			 String result = callURL(url,null);
			 return parseNSavePaytm(result);
		}else{
			paymentStatus.responseMsz = "Invalid orderId";
		  return paymentStatus;
		}
	}
	
	@Override
	public OrderStatusDTO checkCitrusOrderStatus(Integer orderId) throws IOException {
		OrderStatusDTO paymentStatus=  new OrderStatusDTO();
		if(orderId!=null){
			String propFileName = "citrusDetails.properties";
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			Properties prop = new Properties();
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
			 String statusURL=prop.getProperty("statusAPI");
			 String access_key = prop.getProperty("access_key");
			 String url = statusURL+orderId;
			 String result = null;
			try {
				result = callURL(url,access_key);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JSONObject jsonObject = null;
			try {
				 jsonObject = XML.toJSONObject(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 return parseNSaveCitrus(jsonObject.toString());
		}else{
			paymentStatus.responseMsz = "Invalid orderId";
		  return paymentStatus;
		}
	}

	public OrderStatusDTO parseNSaveCitrus(String jsonLine) {
		
		OrderStatusDTO paymentStatus=  new OrderStatusDTO();
		     JsonElement jelement = new JsonParser().parse(jsonLine);
		     JsonObject  jobjectReal = jelement.getAsJsonObject();
		     JsonObject txnEnquiryResponse = jobjectReal.getAsJsonObject("txnEnquiryResponse");
		     JsonObject  enquiryResponse = txnEnquiryResponse.getAsJsonObject("enquiryResponse");
		     JsonObject  jobObject = enquiryResponse.getAsJsonObject();
		     logger.info("Josn Object: "+jobObject);
		     JsonElement amount = jobObject.get("amount");
		     JsonElement respCode  = jobObject.get("respCode");
		     JsonElement message = jobObject.get("respMsg");
		     JsonElement checkId = jobObject.get("merchantTxnId");
		     if(checkId!=null){
			     paymentStatus.checkId = checkId.getAsString();
				 paymentStatus.orderAmount = amount.getAsString();
				 
				 String status = resolveCitrusResponseCode(respCode.getAsString());
				 
				 logger.info("Other status: : "+status);
				 if("0".equalsIgnoreCase(respCode.getAsString()) || "14".equalsIgnoreCase(respCode.getAsString())){
					 paymentStatus.orderStatus = "SUCCESS";
					 if(checkId!=null){
					    Check check = checkService.getCheck(Integer.parseInt(checkId.getAsString()));
					    logger.info("Success :"+check.getCheckId());
					    if(check !=null && !("SUCCESS".equalsIgnoreCase(check.getTransactionStatus()))){
					    	check.setTransactionStatus("SUCCESS");
					    	check.setResponseCode(respCode.getAsString());
					    	check.setStatus(Status.Paid);
							check.getOrders().get(0).setPaymentStatus(PaymentMode.PG.toString());
					    	checkService.addCheck(check);
					    }
					 }
				 }/*else if("3".equalsIgnoreCase(respCode.getAsString()) || 
						  "2".equalsIgnoreCase(respCode.getAsString()) ||
						  "5".equalsIgnoreCase(respCode.getAsString()) || 
						  "7".equalsIgnoreCase(respCode.getAsString()) ||
						  "4".equalsIgnoreCase(respCode.getAsString()) ||
						  "1".equalsIgnoreCase(respCode.getAsString())){
					 Check check = checkService.getCheck(Integer.parseInt(checkId.getAsString()));
					 if(check !=null){
						 check.setTransactionStatus(status);
					     check.setResponseCode(respCode.getAsString());
					     check = cancelInvoice(check);
					     checkService.addCheck(check);
						 paymentStatus.orderStatus = status;
					 }
				}*/else{
					Check check = checkService.getCheck(Integer.parseInt(checkId.getAsString()));
					 if(check !=null){
						 check.setTransactionStatus(status);
					     check.setResponseCode(respCode.getAsString());
					     checkService.addCheck(check);
						 paymentStatus.orderStatus =status;
					 }
				}
				paymentStatus.responseMsz =message.getAsString();
		     }else{
		    	 paymentStatus.responseMsz="Can't fine any order with this checkId.";
		    	 paymentStatus.orderStatus="Error";
		     }
		    return paymentStatus;
		}
	
	@Override
	public String resolveCitrusResponseCode(String pgRespCode){
		String status="";
		if("0".equalsIgnoreCase(pgRespCode)){
			 status = "SUCCESS";
		 } else if("1".equalsIgnoreCase(pgRespCode)){
			 status = "FAILED";
		 }else if("2".equalsIgnoreCase(pgRespCode)){
			 status = "User Dropped";
		 }else if("3".equalsIgnoreCase(pgRespCode)){
			 status = "CANCELED";
		 }else if("4".equalsIgnoreCase(pgRespCode)){
			 status = "FORWARDED";
		 }else if("5".equalsIgnoreCase(pgRespCode)){
			 status ="PG_FORWARD_FAIL";
		 }else if("7".equalsIgnoreCase(pgRespCode)){
			 status = "SESSION_EXPIRED";
		 }else if("14".equalsIgnoreCase(pgRespCode)){
			 status = "SUCCESS_ON_VERIFICATION";
		 }else if("16".equalsIgnoreCase(pgRespCode)){
			 status = "REVERSED";
		 }else if("24".equalsIgnoreCase(pgRespCode)){
			 status = "User Request arrived";
		 }else if("25".equalsIgnoreCase(pgRespCode)){
			 status = "Checkout page rendered";
		 }else{
			 status = "Pending";
		 }
		return status;
	}
	public Check cancelInvoice(Check check){
		try {
			orderController.restoreStock(check,0);
		} catch (ParseException e) {
			e.printStackTrace();
			//emailException(ExceptionUtils.getStackTrace(e),request);
			logger.info("Exception mail sent");
		}
		check.setBill(0);
		check.setRoundOffTotal(0);
		CreditTransactions ct = customerCreditService.getLastPendingTransaction(check.getCustomerId());
		if(ct!=null){
			if(ct.getStatus()==CreditTransactionStatus.PENDING){
			try {
				customerCreditService.updateBillRecoveryTransaction("FAILED",check.getCustomerId(), check.getCreditBalance(),"CREDIT", "Setting status failed of existing pending transaction");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				///restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
				logger.info("Exception mail sent");
			}
		}
		}
		check.setCreditBalance(0);
		check.setOutCircleDeliveryCharges(0);
		check.setStatus(Status.Cancel);
		check.getOrders().get(0).setStatus(com.cookedspecially.enums.order.Status.CANCELLED);
		logger.info("This order has been cancelled :  InvoiceId"+ check.getInvoiceId() );
		return check;
	}
	
    public OrderStatusDTO parseNSavePaytm(String jsonLine) {
    	 OrderStatusDTO paymentStatus=  new OrderStatusDTO();
	     JsonElement jelement = new JsonParser().parse(jsonLine);
	     JsonObject  jobject = jelement.getAsJsonObject();
	     JsonElement amount = jobject.get("TXNAMOUNT");
	     JsonElement status  = jobject.get("STATUS");
	     JsonElement message = jobject.get("RESPMSG");
	     JsonElement checkId = jobject.get("ORDERID");
	     JsonElement responseCode = jobject.get("RESPCODE");
	     paymentStatus.checkId = checkId.getAsString();
		 paymentStatus.orderAmount = amount.getAsString();
		 if("TXN_SUCCESS".equalsIgnoreCase(status.getAsString())){
			 paymentStatus.orderStatus = status.getAsString();
			 if(checkId!=null){
			    Check check = checkService.getCheck(Integer.parseInt(checkId.getAsString()));
			    if(check !=null && !("TXN_SUCCESS".equalsIgnoreCase(check.getTransactionStatus()))){
			    	check.setTransactionStatus(status.getAsString());
			    	check.setResponseCode(responseCode.getAsString());
			    	check.setStatus(Status.Paid);
					check.getOrders().get(0).setPaymentStatus(PaymentMode.PAYTM.toString());
			    	checkService.addCheck(check);
			    }
			 }
		 }else {
			 Check check = checkService.getCheck(Integer.parseInt(checkId.getAsString()));
			 if(check !=null){
				 check.setTransactionStatus(status.getAsString());
			     check.setResponseCode(responseCode.getAsString());
			     checkService.addCheck(check);
				 paymentStatus.orderStatus = status.getAsString();
			 }
			 }
		 paymentStatus.responseMsz =message.getAsString();
	    return paymentStatus;
	}
    
    public static String callURL(String myURL,String access_keyCitrus) {
        StringBuilder sb = new StringBuilder();
        URLConnection urlConn = null;
        InputStreamReader in = null;
        try {
            URL url = new URL(myURL);
            urlConn = url.openConnection();
            if(access_keyCitrus!=null){
            	urlConn.setRequestProperty("access_key",access_keyCitrus);
            	urlConn.setRequestProperty("Content-Type","application/json");
            }
            if (urlConn != null)
                urlConn.setReadTimeout(60 * 1000);
            if (urlConn != null && urlConn.getInputStream() != null) {
                in = new InputStreamReader(urlConn.getInputStream(),
                        Charset.defaultCharset());
                BufferedReader bufferedReader = new BufferedReader(in);
                if (bufferedReader != null) {
                    int cp;
                    while ((cp = bufferedReader.read()) != -1) {
                        sb.append((char) cp);
                    }
                    bufferedReader.close();
                }
            }
            in.close();
        } catch (Exception e) {
            throw new RuntimeException("Exception while calling URL:" + myURL, e);
        }

        return sb.toString();
    }
    
    public void emailException(String exceptionLogs, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException{
    	
    	String resultPath;
    	if(request!=null){
	    	//String scheme = request.getScheme();
			String serverName = request.getServerName();
			resultPath =serverName;
    	}else{
    		resultPath  = "Exception Logs";
    	}
    	
    	MailerUtility mailUtil =  new MailerUtility();
	    final Context ctx = new Context();
	    String subject = resultPath+" : "+"Exception Alert !";
	    ctx.setVariable("exceptionLogs", exceptionLogs);
        logger.info("***********************serverName sending exception***********************************");
		
	    final MimeMessage mimeMessage = mailSender.createMimeMessage();
	    final MimeMessageHelper message =
	        new MimeMessageHelper(mimeMessage, false, "UTF-8"); // true = multipart

	        message.setSubject(subject);
	        String senderEmail = mailUtil.username;
	    	InternetAddress restaurantEmailAddress = new InternetAddress(senderEmail,"Exception Express!");
	    	message.setFrom(restaurantEmailAddress);
	    	message.setReplyTo(restaurantEmailAddress);
	    
	    	if("www.cookedspecially.com".equalsIgnoreCase(resultPath)){
	    		message.setTo("akshay@cookedspecially.com");
	    		message.addCc("erich@cookedspecially.com");
	    		message.addCc("support@cookedspecially.com");
	    	}else{
	    		message.setTo("support@cookedspecially.com");
	    	}

	    final String htmlContent = templateEngine.process("exceptionExpress", ctx);
	    message.setText(htmlContent, true); 
	    String oldUsername = null;
	    String oldPassword = null;
	    String oldHost = null;
	    String oldProtocol = null;
	    Integer oldPort = -1;
	    if (!StringUtility.isNullOrEmpty(mailUtil.username) && !StringUtility.isNullOrEmpty(mailUtil.password)) {
            oldUsername = mailSender.getUsername();
            oldPassword = mailSender.getPassword();
	    	oldHost = mailSender.getHost();
	    	oldProtocol = mailSender.getProtocol();
	    	oldPort = mailSender.getPort();
            mailSender.setUsername(oldUsername);
            mailSender.setPassword(oldPassword);
	    	mailSender.setHost(oldHost);
	    	mailSender.setProtocol(oldProtocol);
	    	mailSender.setPort(oldPort);
	    }

	    mailSender.send(mimeMessage);
	    logger.info("***********************sending end***********************************");
    }
    
    /*
     * 
     * */
   public void alertMail(String emailMessage,String subject,HttpServletRequest request) throws MessagingException, UnsupportedEncodingException{
    	
    	MailerUtility mailUtil =  new MailerUtility();
	    final Context ctx = new Context();
	    //String subject = resultPath+" : "+"Exception Alert !";
	    ctx.setVariable("exceptionLogs", emailMessage);
        logger.info("***********************serverName sending exception***********************************");
		
	    final MimeMessage mimeMessage = mailSender.createMimeMessage();
	    final MimeMessageHelper message =
	        new MimeMessageHelper(mimeMessage, false, "UTF-8"); // true = multipart

	        message.setSubject(subject);
	        String senderEmail = mailUtil.username;
	    	InternetAddress restaurantEmailAddress = new InternetAddress(senderEmail,subject);
	    	message.setFrom(restaurantEmailAddress);
	    	message.setReplyTo(restaurantEmailAddress);
    		message.setTo("rahul@cookedspecially.com");
    		message.setTo("mihir@cookedspecially.com");
	    	

	    final String htmlContent = templateEngine.process("exceptionExpress", ctx);
	    message.setText(htmlContent, true); 
	    String oldUsername = null;
	    String oldPassword = null;
	    String oldHost = null;
	    String oldProtocol = null;
	    Integer oldPort = -1;
	    if (!StringUtility.isNullOrEmpty(mailUtil.username) && !StringUtility.isNullOrEmpty(mailUtil.password)) {
            oldUsername = mailSender.getUsername();
            oldPassword = mailSender.getPassword();
	    	oldHost = mailSender.getHost();
	    	oldProtocol = mailSender.getProtocol();
	    	oldPort = mailSender.getPort();
            mailSender.setUsername(oldUsername);
            mailSender.setPassword(oldPassword);
	    	mailSender.setHost(oldHost);
	    	mailSender.setProtocol(oldProtocol);
	    	mailSender.setPort(oldPort);
	    }

	    mailSender.send(mimeMessage);
	    logger.info("***********************sending end***********************************");
    }
}
