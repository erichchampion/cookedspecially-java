/**
 * 
 */
package com.cookedspecially.service;

import com.cookedspecially.domain.CreditType;
import com.cookedspecially.domain.Customer;
import com.cookedspecially.domain.CustomerAddress;
import com.cookedspecially.domain.Restaurant;
import com.cookedspecially.dto.*;
import com.cookedspecially.dto.credit.*;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.mail.MessagingException;
import javax.naming.directory.InvalidAttributesException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

/**
 * @author shashank, Abhishek
 *
 */
public interface CustomerService {

    void addCustomer(Customer customer);

    void removeCustomer(Integer id) throws Exception;

    Customer getCustomer(Integer id);

    List<Customer> getCustomerById(Integer id);

    List<Customer> getCustomerByParams(Integer custId, String email, String phone, Integer orgId);

    CustomerAddress getCustomerAddressById(int id);

    void addCustomerAddress(CustomerAddress customerAddress);

    void updateCustomerAddress(CustomerAddress customerAddress);

    void removeCustomerAddress(Integer customerId);

    List<CustomerAddress> getCustomerAddress(Integer customerId);

    List<Customer> getCustomerByDate(Integer orgId, Integer restaurantId, Date startDate, Date endDate);

    String registerCustomer(CustomerRegisterationDTO customer);

    Customer authenticate(String phoneNumber, String simNumber, Integer orgId);

    boolean fetchNewOTP(String mobileNumber, String device, Integer orgId);

    boolean sendSMS(String mobileNumber, String content, String priority);

    Customer verifyOTP(String mobileNumber, String simNumber, String otp, Integer orgId);

    boolean setAccountStatus(String mobileNumber, int value, Integer orgId);

    boolean setForcedLogin(String mobileNumber,Integer orgId);

    Customer isCustomerFacebookIdExist(String facebookId);

    boolean isCustomerAuthentic(String facebookId, Integer orgId);

    String signUp(Customer customer);

    boolean loginCustomer(String mobileNumber, Integer orgId);

    boolean verifyOTP(String mobileNumber, String otp);

    boolean emailOTP(String mobileNumber, Integer orgId);

    List<Customer> listCustomerInRestaurant(int restaurantId);

    List<Customer> listCustomerByMobile(int restaurantId, List<String> mobileNo);

    @PreAuthorize("hasAnyRole('restaurantManager','admin')")
    ResponseDTO sendTestOTP(String otpDetails);

    ResponseDTO enableCustomerCredit(CustomerCreditDTO customerCreditDTO, Integer orgId) throws Exception;

    @PreAuthorize("hasAnyRole('restaurantManager','admin')")
    ResponseDTO updateCustomerCredit(CustomerCreditDTO customerCredit, Integer orgId) throws Exception;

    @PreAuthorize("hasAnyRole('restaurantManager','admin')")
    ResponseDTO removeCustomerCredit(Integer customerId, Integer orgId) throws InvalidAttributesException;

    @PreAuthorize("hasAnyRole('restaurantManager','admin')")
    ResponseDTO addCustomerCreditType(CreditTypeDTO creditDTO) throws Exception;
   // public ResponseDTO updateCustomerCreditType(CreditType credit);
   List<CreditType> listCustomerCreditType(int orgId);

    CreditType getCreditType(Integer creditId);

    List<CreditDTO> listCustomerCredit(int orgId);

    ResponseDTO creatTransaction(AddCreditToCustomerAccountDTO creditAddDTO, Integer orgId, Integer userId) throws InvalidAttributesException;

    @PreAuthorize("hasAnyRole('restaurantManager','admin')")
    ResponseDTO editCustomerCreditType(CreditType creditType) throws Exception;

    List<CreditTransactionDTO> listCustomerCreditTransactions(int customerId, String fromDate, String toDate, String format) throws Exception;

    @PreAuthorize("hasAnyRole('restaurantManager','admin')")
    ResponseDTO deleteCustomerCreditType(int creditTypeId, int parseInt);



	/**************************************************************************************************/

    ResponseDTO registerWebCustomer(WebCustomerRegisterDTO customerDTO);

    Customer verifyCustomerOTP(VerifyWebCustomer customer) throws Exception;

    ResponseDTO generateNewOTP(FetchOTPDTO fetchOtp);

    Customer login(String mobileNo, int orgId, String device, String appId) throws Exception;

    ResponseDTO registerCustomerApp(CustomerAppRegisterDTO customerAppDTO);

    CustomerDataDTO getCustomerData(String phone, Integer orgId);

    ResponseDTO autoEmailCreditBillFromServer(CreditStatementDTO creditStatementDTO,Restaurant org) throws MessagingException, UnsupportedEncodingException;

    void autoEmailCreditBillbyList(List<String> statementIdList);


    Customer getCustomerByPhone(String mobileNoOfCustomer, int orgId);

    void deleteCustomerCreditAccount(Integer customerId);

    void deleteCustomer(Integer customerId);

    Customer getCustomerFronInvoiceId(String invoiceId);
}
