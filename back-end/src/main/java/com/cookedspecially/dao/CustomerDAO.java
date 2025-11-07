package com.cookedspecially.dao;

import com.cookedspecially.domain.Customer;
import com.cookedspecially.domain.CustomerAddress;
import com.cookedspecially.domain.OTP;

import java.util.Date;
import java.util.List;

public interface CustomerDAO {

	public void addCustomer(Customer customer);
	public void removeCustomer(Integer id) throws Exception;
	public Customer getCustomer(Integer id);
	public List<Customer> getCustomerById(Integer id);
	public List<Customer> getCustomerByParams(Integer custId, String email, String phone, Integer restaurantId);
	public void addCustomerAddress(CustomerAddress customerAddress);
	public void removeCustomerAddress(Integer customerId);
	public List<CustomerAddress> getCustomerAddress(Integer customerId);
	public Customer getCustomer(String mobileNumber,Integer orgId);
	/*old*/
	public OTP getOTP(String mobileNumber);
	public OTP getOTP(String mobileNumber, int orgId);
	/*new */
	public void saveOrUpdateOTP(OTP otp);
	public Customer getCustomerByFacebookId(String facebookId);
	public void removeOTP(String phone);
	public void updateCustomerAddress(CustomerAddress customerAddress);
	
	public List<Customer> getCustomerByDate(Integer orgId,Integer restaurantId, Date startDate, Date endDate);
	public CustomerAddress getCustomerAddressById(int id) ;
	public List<Customer> listCustomerInRestaurant(int restaurantId);
	public List<Customer> listCustomerByMobile(int restaurantId, List<String> mobileNo);
	void removeOTP(String mobileNo, int orgID);

    void deleteCustomer(Integer customerId);

    public Customer getCustomerByInvoiceId(String invoiceId);

	
	
	/*-----------------------Customer Credit----------------------*/
	
}
