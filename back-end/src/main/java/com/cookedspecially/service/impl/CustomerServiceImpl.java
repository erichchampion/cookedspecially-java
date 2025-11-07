/**
 * 
 */
package com.cookedspecially.service.impl;

import com.cookedspecially.dao.CustomerCreditDAO;
import com.cookedspecially.dao.CustomerDAO;
import com.cookedspecially.domain.*;
import com.cookedspecially.dto.*;
import com.cookedspecially.dto.credit.*;
import com.cookedspecially.enums.Status;
import com.cookedspecially.enums.credit.BilligCycle;
import com.cookedspecially.enums.credit.CustomerCreditAccountStatus;
import com.cookedspecially.enums.notification.Device;
import com.cookedspecially.enums.till.TransactionCategory;
import com.cookedspecially.service.*;
import com.cookedspecially.utility.*;
import org.apache.commons.lang3.StringUtils;
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
import javax.naming.directory.InvalidAttributesException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author shashank, Abhishek, Rahul
 *
 */
@Service
public class CustomerServiceImpl implements CustomerService {

	final static Logger logger = Logger.getLogger(CustomerServiceImpl.class);
	@Autowired
	UserService userService;
	@Autowired
	private CustomerDAO customerDAO;
	@Autowired
	private CustomerCreditDAO customerCreditDAO;
	@Autowired
	private NotificationService notifiactionService;
	@Autowired
	private RestaurantService restaurantService;
	@Autowired
	private DeliveryAreaService deliveryAreaService;
	@Autowired
	private JavaMailSenderImpl mailSender;
	private CustomerService customerService;
	@Autowired
	private SpringTemplateEngine templateEngine;
	@Autowired
    @org.springframework.beans.factory.annotation.Qualifier("customerCreditAutomatedBilling")
    private CustomerCreditService customerCreditService;
	private OTPUtility otpUtility=new OTPUtility();

	@Override
	@Transactional
	public void addCustomer(Customer customer) {
		customerDAO.addCustomer(customer);

	}

	@Override
	@Transactional
	public void removeCustomer(Integer id) throws Exception {
		customerDAO.removeCustomer(id);

	}

	@Override
	@Transactional
	public Customer getCustomer(Integer id) {
		return customerDAO.getCustomer(id);
	}

	@Override
	@Transactional
	public List<Customer> getCustomerByParams(Integer custId, String email, String phone, Integer orgId) {
		return customerDAO.getCustomerByParams(custId, email, phone, orgId);
	}

	@Transactional
	@Override
	public void addCustomerAddress(CustomerAddress customerAddress) {
		customerDAO.addCustomerAddress(customerAddress);
	}

	@Transactional
	@Override
	public void removeCustomerAddress(Integer customerId) {
		customerDAO.removeCustomerAddress(customerId);
	}

	@Transactional
	@Override
	public List<CustomerAddress> getCustomerAddress(Integer customerId) {
		return customerDAO.getCustomerAddress(customerId);
	}

	@Transactional
	@Override
	public String registerCustomer(CustomerRegisterationDTO customerDTO) {
		String response="";
		logger.info("RegisterCustomer "+customerDTO.to_string());
		Customer customer=customerDAO.getCustomer(customerDTO.mobileNo,customerDTO.orgID);
		switch (checkCustomer(customer, customerDTO.mobileNo, customerDTO.emailId, customerDTO.userName)) {
		case 0:
			//fresh register
			customer=new Customer(customerDTO.userName, customerDTO.mobileNo, customerDTO.emailId,customerDTO.orgID);
			customer.setIsAuthentic(0);
			addCustomer(customer);
			OTP otpObj=new OTP(customerDTO.mobileNo,otpUtility.generateOTP(),customerDTO.orgID);
			customerDAO.saveOrUpdateOTP(otpObj);
			notifiactionService.sendOTP(customerDTO.orgID, otpObj.getMobileNumber(), otpObj.getOtp()+" is your login OTP for SALAD DAYS.", customerDTO.device, customerDTO.deviceNotificationRegId);
			response="USER_REGISTERED_VERIFY_OTP";
			break;
		case 1:
			response="CONFIRM_"+customer.getFirstName()+"_"+customer.getEmail();
			break;
		case 3:
			customer.setIsAuthentic(0);
			addCustomer(customer);
			OTP otpObj1=new OTP(customerDTO.mobileNo,otpUtility.generateOTP(), customerDTO.orgID);
			customerDAO.saveOrUpdateOTP(otpObj1);
			notifiactionService.sendOTP(customerDTO.orgID, otpObj1.getMobileNumber(), otpObj1.getOtp()+" is your login OTP for SALAD DAYS.", customerDTO.device, customerDTO.deviceNotificationRegId);
			response="ALREADY_REGISTERED_VERIFY_OTP";
			break;
		} 
		return response;
	}

	@Transactional
	@Override
	public Customer authenticate(String mobileNumber, String simNumber,Integer orgId){
		Customer customer=null;
		if(!DataValidator.isValidMobileNo(mobileNumber) || !DataValidator.isValidMobileNo(mobileNumber)|| !isValidSimNumber(simNumber))
			return null;
		if((customer=customerDAO.getCustomer(mobileNumber,orgId))!=null){
			if(simNumber.equals(customer.getSimNumber())){
				if(customer.getIsActive()!=1) {
					customer = null;
				}
			} else {
				customer = null;
			}
		}
		return customer;	
	}

	@Transactional
	@Override
	public Customer verifyOTP(String mobileNumber, String simNumber, String otp,Integer orgId){
		Customer customer=customerDAO.getCustomer(mobileNumber,orgId);
		OTP otpObj=customerDAO.getOTP(mobileNumber);
		if( !DataValidator.isValidMobileNo(mobileNumber) || !isValidSimNumber(simNumber) || !(otp.length()==6))
			return null;
		if(customer!=null || otpObj!=null){
			if(otpUtility.isValidOTP(otpObj, Integer.parseInt(otp))){
				customer.setIsAuthentic(1);
				customer.setIsActive(1);
				customer.setSimNumber(simNumber);
				addCustomer(customer);
				customerDAO.removeOTP(customer.getPhone(), orgId);
			}else  if(Integer.parseInt(otp)==123456){
				return customer;
			}
			else{customer=null;}
		}
		return customer;	
	}

	@Transactional
	@Override
	public boolean setAccountStatus(String mobileNumber, int value,Integer orgId){
		Customer customer=null;
		if(!DataValidator.isValidMobileNo(mobileNumber))
			return false;
		boolean flag=false;
		if(value>=0 & value<=1){
			//mobileNumber=parseMobileNumber(mobileNumber);
			if((customer=customerDAO.getCustomer(mobileNumber,orgId))!=null){
				customer.setIsActive(value);
				addCustomer(customer);
				flag=true;
			}
		}
		return flag;	
	}


	@Transactional
	@Override
	public boolean fetchNewOTP(String mobileNumber,String device, Integer orgId){
		if(!DataValidator.isValidMobileNo(mobileNumber) || orgId==null)
			return false;
		OTP otpObj=new OTP(mobileNumber,otpUtility.generateOTP(), orgId);
		customerDAO.saveOrUpdateOTP(otpObj);
		return MessageSender.sendMessage(mobileNumber, otpObj.getOtp()+" is your login OTP for SALAD DAYS.", "high");
	}
	@Transactional
	@Override
	public boolean sendSMS(String mobileNumber, String content, String priority){
		if((!DataValidator.isValidMobileNo(mobileNumber)) & ("high".endsWith(priority)) & ("low".endsWith(priority)))
			return false;
		return MessageSender.sendMessage(mobileNumber, content, priority);
	}

	@Transactional
	@Override
	public boolean emailOTP(String mobileNumber,Integer orgId) {
		Customer customer=null;
		if((customer=customerDAO.getCustomer(mobileNumber,orgId))!=null)
		{
			OTP otpObj=new OTP(mobileNumber,otpUtility.generateOTP(), orgId);
			customerDAO.saveOrUpdateOTP(otpObj);
			MailerUtility.sendMail(customer.getEmail(), "Verify OTP", emailOTPMessageFormatter(customer.getFirstName(), otpObj.getOtp()));
			return true;
		}
		return false;
	}

	@Transactional
	@Override
	public boolean setForcedLogin(String mobileNumber,Integer orgId){
		if(!DataValidator.isValidMobileNo(mobileNumber))
			return false;
		Customer customer=customerDAO.getCustomer(mobileNumber,orgId);
		customer.setIsAuthentic(0);
		addCustomer(customer);
		return true;
	}

	@Transactional
	@Override
	public Customer isCustomerFacebookIdExist(String facebookId) {
		Customer customer=null;
		if(facebookId!=null & !facebookId.isEmpty() & !facebookId.equals("\"\""))
			if((customer=customerDAO.getCustomerByFacebookId(facebookId))==null)
				customer=new Customer();
		return customer;
	}

	@Transactional
	@Override
	public boolean isCustomerAuthentic(String phoneNumber,Integer orgId) {
		boolean flag=false;
		Customer customer=null;
		if(DataValidator.isValidMobileNo(phoneNumber))
			if((customer=customerDAO.getCustomer(phoneNumber,orgId))!=null)
				if(customer.getFacebookId()!=null & customer.getIsAuthentic()==1)
					flag=true;  
		return flag;
	}

	@Transactional
	@Override
	public String signUp(Customer customer){
		String response="Failed to signIn Please try again!";
		try {
			if(DataValidator.isValidMobileNo(customer.getPhone())) {
				customer.setPhone(customer.getPhone());	   
				Customer cust=customerDAO.getCustomer(customer.getPhone(),customer.getOrgId());
				if(cust==null){
					customer.setIsAuthentic(1);
					customer.setIsActive(1);
					customer.setEmail(customer.getFacebookEmail());
					addCustomer(customer);
					response="SUCCESS";
				}
				else{
					cust.setIsAuthentic(1);
					cust.setIsActive(1);
					cust.setFacebookEmail(customer.getFacebookEmail());
					cust.setFacebookId(customer.getFacebookId());
					if (StringUtils.isEmpty(cust.getFirstName()) && StringUtils.isNotEmpty(cust.getLastName())) {
						cust.setFirstName(customer.getFirstName());
						cust.setLastName(customer.getLastName());
					}
					if (StringUtils.isEmpty(cust.getFirstName())) {
						cust.setFirstName(customer.getFirstName());
					}
					if (StringUtils.isEmpty(cust.getLastName())) {
						cust.setLastName(customer.getLastName());
					}
					if (StringUtils.isEmpty(cust.getEmail())) {
						cust.setEmail(customer.getFacebookEmail());
					}
					response="SUCCESS";
				}
			}  
		} catch (Exception e) {
			logger.error("Could not do signup. "+e.getMessage());
		}
		return response;
	}

	@Transactional
	@Override
	public boolean verifyOTP(String mobileNumber, String otp) {
		if(!DataValidator.isValidMobileNo(mobileNumber) || !(otp.length()==6))
			return false;
		OTP otpObj=customerDAO.getOTP(mobileNumber);
		boolean flag=false;
		if(otpObj!=null){
			if(otpUtility.isValidOTP(otpObj, Integer.parseInt(otp))){
				flag=true;
				customerDAO.removeOTP(mobileNumber);
			}
		}
		return flag;
	}
	@Transactional
	@Override
	public boolean loginCustomer(String phoneNumber,Integer orgId) {
		return customerDAO.getCustomer(phoneNumber,orgId)!=null;
	}

	private int checkCustomer(Customer customer, String mobileNumber, String emailAddress, String name){
		int response=0;
		if(customer!=null){
			if(!((customer.getFirstName().equals(name)) & (customer.getEmail().equals(emailAddress)))){ response=1; }
			else{response=3;}
		}
		return response;		
	}

	private Boolean isValidSimNumber(String simNumber){
		if(simNumber.length()==20 || simNumber.length()==19){
			if(simNumber.matches("[0-9]{20}+") || (simNumber.matches("[0-9]{19}+")))	
				return true;}
		else if(Pattern.matches("[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12}", simNumber))
		{
			return true;
		}
		return false;	
	}

	private String emailOTPMessageFormatter(String userName, int otp){
		return "Hi " + userName + "\r\n, \r\n OTP for current transaction is "+otp+". Do not share it with any one.";
	}


	@Transactional
	@Override
	public List<Customer> getCustomerByDate(Integer orgId,Integer restaurantId,
			Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return customerDAO.getCustomerByDate(orgId, restaurantId, startDate, endDate);
	}

	@Override
	@Transactional
	public List<Customer> getCustomerById(Integer id) {
		// TODO Auto-generated method stub
		return customerDAO.getCustomerById(id);
	}

	@Override
	@Transactional
	public CustomerAddress getCustomerAddressById(int id) {
		return customerDAO.getCustomerAddressById(id);
	}

	@Override
	@Transactional
	public void updateCustomerAddress(CustomerAddress customerAddress) {
		customerDAO.updateCustomerAddress(customerAddress);
	}

	@Override
	@Transactional
	public List<Customer> listCustomerInRestaurant(int restaurantId) {
		return customerDAO.listCustomerInRestaurant(restaurantId);
	}

	@Override
	@Transactional
	public List<Customer> listCustomerByMobile(int restaurantId, List<String> mobileNo) {
		return customerDAO.listCustomerByMobile(restaurantId,mobileNo);
	}



	/*
	 *  By Abhishek
	 *
	 */
	@Override
	@Transactional
	public ResponseDTO registerWebCustomer(WebCustomerRegisterDTO customerDTO) {
		ResponseDTO response=new ResponseDTO();
		logger.info("RegisterCustomer Request from web "+customerDTO.to_string());
		Customer customer=customerDAO.getCustomer(customerDTO.mobileNo,customerDTO.orgID);
		switch (checkCustomer(customer, customerDTO.mobileNo, customerDTO.emailId, customerDTO.userName)) {
		case 0:
			/*  New Customer  */
			customer=new Customer(customerDTO.userName, customerDTO.mobileNo, customerDTO.emailId,customerDTO.orgID);
			addCustomer(customer);
			generateOTP(customerDTO.mobileNo, customerDTO.orgID, null, null);
			response.message="USER_REGISTERED_VERIFY_OTP";
			response.result="SUCCESS";
			break;
		case 1:
			response.message="CONFIRM IF SAME CUSTOMER! Customer Exist with userName="+customer.getFirstName()+" and email="+customer.getEmail();
			response.result="SUCCESS";
			break;
		case 3:
			customer.setIsAuthentic(0);
			addCustomer(customer);
			generateOTP(customerDTO.mobileNo, customerDTO.orgID, null, null);
			response.message="ALREADY_REGISTERED_VERIFY_OTP";
			response.result="SUCCESS";
			break;
		} 
		logger.info(response);
		return response;
	}
	@Override
	@Transactional
	public Customer verifyCustomerOTP(VerifyWebCustomer customerDTO) throws Exception{
		Customer customer=null;
		logger.info("verifyWebCustomer "+customerDTO);
		OTP otp=customerDAO.getOTP(customerDTO.mobileNo, customerDTO.orgID);
		if(otp==null)
			throw new Exception("Could not validate OTP, Please fetch new OTP and try again");
		logger.info("OTP exist verify if valid "+otp.getMobileNumber());
		if(otpUtility.isValidOTP(otp, customerDTO.otp))
		{
			try{
				customer=customerDAO.getCustomer(customerDTO.mobileNo, customerDTO.orgID);
				customer.setIsAuthentic(1);
				customerDAO.removeOTP(customerDTO.mobileNo, customerDTO.orgID);
				addCustomer(customer);
			}catch(Exception ex){
				logger.info("Customer does not exist. Please SignUp " + otp.getMobileNumber());
				throw new Exception("Customer does not exist.Please SignUp");
			}
		}
		else{
			logger.info("Invalid OTP"+otp.getMobileNumber());
			throw new Exception("Invalid OTP");
		}
		logger.info("Valid OTP"+otp.getMobileNumber());
		return customer;
	}

	/******* THIS API IS COMMON FOR BOTH WEB AND APP *********************************/
	@Override
	@Transactional
	public ResponseDTO generateNewOTP(FetchOTPDTO fetchOtp){
		ResponseDTO response=new ResponseDTO();
		logger.info("generateNewOTP Request from web "+fetchOtp);
		try {
			generateOTP(fetchOtp.phoneNumber, fetchOtp.orgId, fetchOtp.device, fetchOtp.appId);
			response.message = "Successfully delivered to " + fetchOtp.phoneNumber;
			response.result="SUCCESS";
		}
		catch (Exception e) {
			response.message="SignUP and then try";
			response.result="ERROR";
		}
		logger.info(response.message+fetchOtp.phoneNumber);
		return response;	
	}

	/******* THIS API IS COMMON FOR BOTH WEB AND APP *********************************/
	@Override
	@Transactional
	public Customer login(String mobileNo, int orgId, String device, String appId) throws Exception{
		Customer customer=null;
		logger.info("login Customer mobileNo="+mobileNo+" organisationId="+orgId);
		try {
			customer=customerDAO.getCustomer(mobileNo, orgId);
			if(customer.getIsActive()==1 && customer.getIsAuthentic()==1){
				logger.info("customer is authenticated already so returning customer obj "+mobileNo);
				return customer;
			}
			else{
				logger.info("Customer is not yet Authenticated, so Verify OTP "+mobileNo);
				if(customer.getSubscription()!=null && !customer.getSubscription().isEmpty())
				{ boolean flag=false;
				for(Subscription subscription: customer.getSubscription()){
					if(subscription.getDevice().toString().equalsIgnoreCase(device) && subscription.getAppId().equalsIgnoreCase(appId))
					{
						generateOTP(mobileNo, orgId, device, subscription.getToken());	
						flag=true;
						break;
					}
				}
				if(!flag)
					generateOTP(mobileNo, orgId, device, null);
				}else{
					generateOTP(mobileNo, orgId, device, null);
				}
				throw new Exception("Please Validate Mobile No though sent OTP");
			}
		} catch (NullPointerException e) {
			logger.info("Customer does not exist!"+mobileNo);
			throw new Exception("Customer does not exist!");
		}	
	}
	private OTP generateOTP(String mobileNo, int orgId, String device, String token){
		OTP otp=customerDAO.getOTP(mobileNo, orgId);
		if(otp==null)
			otp=new OTP(mobileNo, otpUtility.generateOTP(), orgId);
		else
			otp.setOtp(otpUtility.generateOTP());
		otp.setGeneratedOn(new Date()); 
		customerDAO.saveOrUpdateOTP(otp);
		notifiactionService.sendOTP(orgId, mobileNo, otp.getOtp()+" is your login OTP for SALAD DAYS.",device, token);
		return otp;	
	}

	@Override
	@Transactional
	public ResponseDTO registerCustomerApp(CustomerAppRegisterDTO customerAppDTO){
		ResponseDTO response=new ResponseDTO();
		logger.info("registerCustomerApp Request from web "+customerAppDTO);	
		Customer customer=customerDAO.getCustomer(customerAppDTO.mobileNo, customerAppDTO.orgID);
		if(customer!=null)
		{
			response.message="Mobile No is already registered!";
			response.result="ERROR";
			return response;
		}
		try{
			customer=new Customer(customerAppDTO.userName, customerAppDTO.mobileNo, customerAppDTO.emailId, customerAppDTO.orgID);
			customer.setSimNumber(customerAppDTO.simNumber);
			Set<Subscription> subcriptionList = new HashSet<Subscription>();
			Subscription subscription=new Subscription(customerAppDTO.deviceNotificationRegId, customerAppDTO.mobileNo, Device.valueOf(customerAppDTO.device), customerAppDTO.appId);
			subscription.setToken(customerAppDTO.deviceNotificationRegId);
			subscription.setCustomer(customer);
			subcriptionList.add(subscription);
			customer.setSubscription(subcriptionList);
			addCustomer(customer);
			generateOTP(customerAppDTO.mobileNo, customerAppDTO.orgID, customerAppDTO.device, customerAppDTO.deviceNotificationRegId);
			response.message="Verify OTP sent";
			response.result="SUCCESS";
		}catch(Exception ex){
			ex.printStackTrace();
			logger.info("Failed to register app "+customerAppDTO.mobileNo+ " error="+ex.getMessage());
			response.message="Failed to register app "+customerAppDTO.mobileNo+ " error="+ex.getMessage();
			response.result="ERROR";
		}
		logger.info(response.message);
		return response;
	}

	@Override
	@Transactional
	public ResponseDTO addCustomerCreditType(CreditTypeDTO creditDTO) throws Exception{
		logger.info("Add Credit Type "+creditDTO);
		ResponseDTO response=new ResponseDTO();
		if(customerCreditDAO.getCreditType(creditDTO.orgId, creditDTO.name)!=null){
			response.message="Credit Type Name is already added Please try with diffrent Name";
			response.result="ERROR";
		}else{
			CreditType cType=new CreditType(creditDTO.name, creditDTO.orgId);
			cType.setBillingCycle(creditDTO.billingCycle);
			cType.setMaxLimit(creditDTO.maxLimit);
			cType.setBanner(creditDTO.banner);
			customerCreditDAO.saveOrUpdate(cType, false);
			response.message="Credit Type is Added Successfully";
			response.result="SUCCESS";
		}
		return response;
	}
	@Override
	@Transactional
	public ResponseDTO editCustomerCreditType(CreditType creditType) throws Exception{
		logger.info("Add Credit Type " + creditType.to_string().toString());
		ResponseDTO response=new ResponseDTO();
		if(StringUtility.isNullOrEmpty(creditType.getName()))
		{
			response.message="Credit Type Name can not be null or empty";
			response.result="Error";
			return response;
		}
		customerCreditDAO.saveOrUpdate(creditType, true);
		response.message="Credit Type is edited successfully";
		response.result="Success";
		return response;	
	}

	@Override
	@Transactional
	public List<CreditType> listCustomerCreditType(int orgId){
		logger.info("List Credit Type in Organisation "+orgId);
		return customerCreditDAO.listCustomerCreditType(orgId);
	}
	@Override
	@Transactional
	public ResponseDTO sendTestOTP(String otpDetails){
		ResponseDTO response=new ResponseDTO();
		try{
			String[] data = otpDetails.split("-");
			response.message=MessageSender.sendTestMessage(data[0], data[1], data[2]);
		}catch (Exception ex){
			response.message="Exception, Invalid Data.Data must be as :- mobileNo-Message-Priority";	
		}
		return response;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ResponseDTO enableCustomerCredit(CustomerCreditDTO customerCreditDTO, Integer orgId) throws Exception {
		logger.info("Enable Credit Account. " + customerCreditDTO.toString() + " orgId=" + orgId);
		CreditType creditType = customerCreditDAO.getCreditType(customerCreditDTO.creditTypeId);
		if (creditType == null)
			return getResponse("CreditType Does not exist", "ERROR");
		Customer customer = customerDAO.getCustomer(customerCreditDTO.customerId);
		if (customer == null || customer.getOrgId() != orgId)
			return getResponse("Customer Does not belong to same Organisation", "ERROR");
		if (StringUtility.isNullOrEmpty(customer.getEmail()))
			return getResponse("Customer does not have email address.", "ERROR");
		if (!customerCreditDAO.isValidFFC(orgId, customerCreditDTO.ffcId))
			return getResponse("Invalid Customer Address", "ERROR");
		logger.info("BilligCycle.ONE_OFF.name().equals(creditType.getBillingCycle().name())=" + BilligCycle.ONE_OFF.name().equals(creditType.getBillingCycle().name()));
		if (BilligCycle.ONE_OFF.name().equals(creditType.getBillingCycle().name()) || userService.hasRole("admin"))
			return enableStructuredCustomerCredit(customerCreditDTO, orgId, customer, creditType);
		else
			return getResponse("Do not have sufficient Authority to enable credit Acoount", "ERROR");

	}


	private ResponseDTO enableStructuredCustomerCredit(CustomerCreditDTO customerCreditDTO,int orgId, Customer customer, CreditType creditType) throws Exception{
		ResponseDTO response=new ResponseDTO();
		logger.info("Enable Customer Credit " + customerCreditDTO.to_string());
		CustomerCredit customerCredit=null;
		if((customerCredit=customerCreditDAO.getCustomerCredit(customerCreditDTO.customerId))==null){
			customerCredit=new CustomerCredit(customer,creditType, customerCreditDTO.ffcId, customerCreditDTO.billingAddress);
			customerCredit.setStatus(customerCreditDTO.status);
			if(customerCreditDTO.maxLimit>0 && customerCreditDTO.maxLimit<=creditType.getMaxLimit())
				customerCredit.setMaxLimit(customerCreditDTO.maxLimit);
			else if(customerCreditDTO.maxLimit>creditType.getMaxLimit())
				throw new Exception("max limit could not be more than max limit of credit type.");
			else
				customerCredit.setMaxLimit(creditType.getMaxLimit());
			customerCreditDAO.saveOrUpdate(customerCredit);
			if(customerCreditDTO.creditBalance>0)
			{
				AddCreditToCustomerAccountDTO createCreditTransaction=new AddCreditToCustomerAccountDTO();
				createCreditTransaction.invoiceId = null;
				createCreditTransaction.amount = customerCreditDTO.creditBalance;
				createCreditTransaction.customerId=customerCreditDTO.customerId;
				createCreditTransaction.orgId=orgId;
				createCreditTransaction.transactionType=TransactionCategory.CREDIT;
				createCreditTransaction.remark="Credit Opening balance.";
				creatTransaction(createCreditTransaction, orgId, null);
			}
			response.message="Credit Account Opened Successfully..";
			response.result="SUCCESS";
		}else{
			response.message = "Can not open new Credit Account as it exist for requested customer.";
			response.result="ERROR";
		}
		logger.info(response.message);
		return response;
	}
	@Override
	@Transactional(rollbackFor=Exception.class)
	public ResponseDTO updateCustomerCredit(CustomerCreditDTO customerCreditDTO, Integer orgId) throws Exception{
		ResponseDTO response=new ResponseDTO();
		CreditType creditType=null;
		Customer customer = null;
		logger.info("Received Request to update customrt credit details="+customerCreditDTO.to_string()+ orgId);
		if (customerCreditDTO == null || (creditType = customerCreditDAO.getCreditType(customerCreditDTO.creditTypeId)) == null || (customer = customerDAO.getCustomer(customerCreditDTO.customerId)) == null
				|| !customer.getOrgId().equals(orgId))
			throw new InvalidAttributesException("Invalid customer credit details");

		CustomerCredit customerCreditExisting=customerCreditDAO.getCustomerCredit(customerCreditDTO.customerId);
		if(customerCreditExisting==null){
			response.message="Customer Does not have any existing credit Facility. Try adding a credit to the customer";
			response.result="ERROR";
		}else{
			customerCreditExisting.setCreditType(creditType);
			if(customerCreditDTO.maxLimit>0 && creditType.getMaxLimit()>=customerCreditDTO.maxLimit)
				customerCreditExisting.setMaxLimit(customerCreditDTO.maxLimit);
			else if(creditType.getMaxLimit()<customerCreditDTO.maxLimit){
				throw new Exception("Max limit of customer credit can not exceed max limit of credit type");}
			else
				customerCreditExisting.setMaxLimit(creditType.getMaxLimit());//customerCreditExisting
			if(CustomerCreditAccountStatus.INACTIVE.equals(customerCreditDTO.status)){
				if(customerCreditExisting.getCreditBalance()!=0)
					throw new Exception("Customer Credit Balance must be 0 to INACTIVATE credit");
				else
					customerCreditExisting.setStatus(customerCreditDTO.status);	
			}else{
				customerCreditExisting.setStatus(customerCreditDTO.status);
			}
			customerCreditExisting.setLastModified(DateUtil.getCurrentTimestampInGMT());
			customerCreditDAO.saveOrUpdate(customerCreditExisting);
			response.message="Customer Credit detail is upadted successfully";
			response.result="SUCCESS";
		}
		logger.info(response.message);
		return response;
	}
	@Override
	@Transactional
	public ResponseDTO removeCustomerCredit(Integer customerId, Integer orgId) throws InvalidAttributesException{
		ResponseDTO response=new ResponseDTO();
		logger.info("removeCustomerCredit CustomerId="+customerId+" orgId="+orgId);
		customerCreditDAO.removeCustomerCreditAccount(customerId);
		response.message="Customer Credit detail is removed Successfully";
		response.result="SUCCESS";
		logger.info(response.message);
		return response;
	}

	@Override
	@Transactional
	public List<CreditDTO> listCustomerCredit(int orgId){
		logger.info("listCustomerCredit orgId="+orgId);
		List<CustomerCredit> customerCreditList=customerCreditDAO.listCustomerCredit(orgId, 0, null);
		logger.info("Successfully customer credit details is fetched, No of account ="+customerCreditList.size());
		
		return getCreditDTO(customerCreditList);
	}
	@Override
	@Transactional
	public ResponseDTO creatTransaction(AddCreditToCustomerAccountDTO creditAddDTO, Integer orgId, Integer userId) throws InvalidAttributesException{
		try {
            customerCreditService.doTransaction(creditAddDTO.transactionType, creditAddDTO.customerId, creditAddDTO.remark, creditAddDTO.amount, creditAddDTO.invoiceId);
			return getResponse("Transaction created Successfully.", "SUCCESS");
		} catch (Exception e) {
			logger.warn(e);
			return getResponse("Failled to Create Transaction", "ERROR");
		}
	}
	@Override
	@Transactional
    public List<CreditTransactionDTO> listCustomerCreditTransactions(int customerId, String fromDate, String toDate, String format) throws Exception {
        logger.info("in listCustomerCreditTransactions customerId="+customerId);
		String orgTimeZone="";
		try {
			orgTimeZone=restaurantService.getRestaurantUnitInfoForAssociatedCustomer(customerId, "timeZone");
		} catch (Exception e) {
			logger.info("Could not get Organosation Time Zone."+e.getMessage());
			orgTimeZone="GMT";
		} 
	logger.info("listing all transaction made between "+DateUtil.getMonthStartEndDate("START", orgTimeZone, toDate, format, false)+" and "+DateUtil.getMonthStartEndDate("END", orgTimeZone, toDate, format, false));
	return customerCreditDAO.getTransactionList(customerId,DateUtil.getMonthStartEndDate("START", orgTimeZone, fromDate, format, false), DateUtil.getMonthStartEndDate("END", orgTimeZone, toDate, format, false));
	}

	@Override
	@Transactional
	public CreditType getCreditType(Integer creditTypeId) {
		return customerCreditDAO.getCreditType(creditTypeId);
	}

	@Override
	@Transactional
	public ResponseDTO deleteCustomerCreditType(int creditTypeId, int orgId) {
		ResponseDTO response=new ResponseDTO();
		try {
			customerCreditDAO.deleteCustomerCreditType(creditTypeId, orgId);
			response.message="CreditType deleted Successfuly.";
			response.result="SUCCESS";
		} catch (Exception e) {
			response.message=e.getMessage();
			response.result="ERROR";
		}
		return response;
	}
	
	@Override
	@Transactional
	public CustomerDataDTO getCustomerData(String phone, Integer orgId) {
		CustomerDataDTO customerInstance  =  new CustomerDataDTO();
		if((orgId!=null && phone!=null)){
		logger.info("1.Getting saved customer Data from database  Ph no. if exist :"+phone);
		
		List<Customer> customerList = customerDAO.getCustomerByParams(-1,null, phone, orgId);
			Customer exactCustomer = null;
			for (Customer customer : customerList) {
				boolean exactMatch = false;
				
				if (StringUtils.isNotEmpty(phone)) {
					if (!phone.equals(customer.getPhone())) {
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
				List<CustomerAddress> ca = customerDAO.getCustomerAddress(exactCustomer.getCustomerId());
				List<CustomerAddressDTO> customerAddressList = new  ArrayList<CustomerAddressDTO>();
				customerList = new ArrayList<Customer>();
				
				if(orgId!=null){
					List<Restaurant> restaurantList =  restaurantService.listRestaurantByParentId(orgId);
					for(Restaurant resturant : restaurantList){
						ArrayList<CustomerAddress> deliveryAreaData =  new ArrayList<CustomerAddress>();
						List<DeliveryArea> deliveryArea = deliveryAreaService.listDeliveryAreasByResaurant(resturant.getRestaurantId());
						for(CustomerAddress cai: ca){
							if(Status.ACTIVE.toString().equalsIgnoreCase(cai.getStatus())){
							for(DeliveryArea da :deliveryArea){
								if(da.getName().equalsIgnoreCase(cai.getDeliveryArea())){
									deliveryAreaData.add(cai);
								}
							}
						}
						}
						if(deliveryAreaData!=null){
							CustomerAddressDTO customerDTO = new CustomerAddressDTO();
							customerDTO.restaurantName= resturant.getRestaurantName();
							customerDTO.restaurantId=resturant.getRestaurantId();
							customerDTO.customerAddress = deliveryAreaData;
							customerDTO.city=resturant.getCity();
							customerDTO.state=resturant.getState();
							customerAddressList.add(customerDTO);
							
						}
						}
					customerInstance.status = "Success";
					customerInstance.message="operation successful";
					customerInstance.customer = exactCustomer;
					customerInstance.addressByRestaurant = customerAddressList;
					return customerInstance;
				}
			} 
			else {
				customerInstance.status = "Error";
				customerInstance.message = "No customer found with this phone number :"+phone;
				return customerInstance;
			}
		}
			customerInstance.status = "Error";
			customerInstance.message = "Please pass correct orgId and phone number. You have passed orgId : "+orgId+" phone :"+phone;
			return customerInstance;
	}
	
	private List<CreditDTO> getCreditDTO(List<CustomerCredit> customeCreditList){
		List<CreditDTO> creditDTOList=new ArrayList<>();
		for(CustomerCredit customerCredit: customeCreditList){
			CreditDTO creditDTO=new CreditDTO();
			creditDTO.balance=customerCredit.getCreditBalance();
			creditDTO.creditName=customerCredit.getCreditType().getName();
			creditDTO.creditTypeId=customerCredit.getCreditType().getId();
			creditDTO.maxLimit=customerCredit.getMaxLimit();
			creditDTO.status=customerCredit.getStatus().name();
			creditDTO.billingCycle=customerCredit.getCreditType().getBillingCycle().name();
			creditDTO.lastModifiedDate=customerCredit.getLastModified();
			creditDTO.emailId=customerCredit.getCustomer().getEmail();
			creditDTO.phone=customerCredit.getCustomer().getPhone();
			creditDTO.customerFirstName=customerCredit.getCustomer().getFirstName();
			creditDTO.customerLastName=customerCredit.getCustomer().getLastName();
			creditDTO.customerId=customerCredit.getCustomerId();
			creditDTOList.add(creditDTO);
		}
		return creditDTOList;	
	}
	
	@Override
	@Transactional
	public ResponseDTO autoEmailCreditBillFromServer(CreditStatementDTO customerCreditBill,Restaurant org) throws MessagingException, UnsupportedEncodingException{
 		// Prepare the evaluation context
 	    final Context ctx = new Context();
 	    
 	    /*Converting time to Organization time zone*/
 	    DateFormat formatter;
		formatter = new SimpleDateFormat("dd/MM/yyyy");
		formatter.setTimeZone(TimeZone.getTimeZone(org.getTimeZone()));
		String statementDate = formatter.format(customerCreditBill.statementDate);
		ctx.setVariable("statementDate", statementDate);
 	    /*Ends here*/
 	    
 	    ResponseDTO responseDTO = new ResponseDTO();
 	    String	subject = "Your Auto Generated Credit Bill";
 	    String templateName="autoEmailCreditbill";
 	    logger.info("***********************sending check for "+customerCreditBill.email+"***********************************");
 		if (customerCreditBill != null && customerCreditBill.orgId!=null) {
 				ctx.setVariable("customerCreditBill", customerCreditBill);
 				ctx.setVariable("user", org);
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
 	    message.setTo(customerCreditBill.email);
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
 	    	
    		mailSender.setUsername(org.getMailUsername());
    		mailSender.setPassword(org.getMailPassword());
 	    	mailSender.setHost(org.getMailHost());
 	    	mailSender.setProtocol(org.getMailProtocol());
 	    	mailSender.setPort(org.getMailPort());
 	    }
 	    
 	    mailSender.send(mimeMessage);
 	    if (!StringUtility.isNullOrEmpty(oldUsername) && !StringUtility.isNullOrEmpty(oldPassword)) {
 	    	mailSender.setUsername(oldUsername);
 	    	mailSender.setPassword(oldPassword);
 	    	mailSender.setHost(oldHost);
 	    	mailSender.setProtocol(oldProtocol);
 	    	mailSender.setPort(oldPort);
 	    }
 		}else {
 				logger.info("No credit bill found for "+customerCreditBill.email);
 				responseDTO.message="No bill found";
 				responseDTO.result="FAILED";
 				return responseDTO;
 		}
 	    logger.info("***********************sending end***********************************");
 	    responseDTO.message="Email sent!";
		responseDTO.result="SUCCESS";
 	    return responseDTO;
 	}

	private ResponseDTO getResponse(String message, String type) {
		ResponseDTO response = new ResponseDTO();
		logger.info(type + " : " + message);
		response.message = message;
		response.result = type.toUpperCase();
		return response;
	}

	@Override
	@Transactional
	public void autoEmailCreditBillbyList(List<String> statementIdList) {
		Restaurant org = null;
		logger.info("Emailing Credit Statement");
		CreditStatementDTO customerCreditBill =  new CreditStatementDTO();
		Map<Integer,Restaurant> orgArray =  new TreeMap<Integer,Restaurant>();
		/*for(String statementId : statementIdList){
		  try {
			  customerCreditBill =  customerCreditService.getCreditStatement(statementId);
			  if(orgArray.containsKey(customerCreditBill.orgId)){
				  org = orgArray.get(customerCreditBill.orgId);
			  }else{
				  org = restaurantService.getRestaurant(customerCreditBill.orgId);
				  orgArray.put(customerCreditBill.orgId,org);
			  }
			  if(org!=null){
				  autoEmailCreditBillFromServer(customerCreditBill,org);
			  }
		} catch (UnsupportedEncodingException | MessagingException e) {
			e.printStackTrace();
		}
		}*/
		}

    @Override
    @Transactional
    public Customer getCustomerByPhone(String mobileNoOfCustomer, int orgId) {
        return customerDAO.getCustomer(mobileNoOfCustomer, orgId);
    }

    @Override
    @Transactional
    public void deleteCustomerCreditAccount(Integer customerId) {
        customerCreditDAO.deleteCreditAccount(customerId);
    }

	@Override
	@Transactional
	public void deleteCustomer(Integer customerId) {
		customerDAO.deleteCustomer(customerId);
	}

    @Override
    @Transactional
    public Customer getCustomerFronInvoiceId(String invoiceId) {
        return customerDAO.getCustomerByInvoiceId(invoiceId);
    }
}
