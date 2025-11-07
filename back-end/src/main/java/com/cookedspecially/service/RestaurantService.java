/**
 * 
 */
package com.cookedspecially.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import com.cookedspecially.domain.Check;
import com.cookedspecially.domain.Discount_Charges;
import com.cookedspecially.domain.Nutrientes;
import com.cookedspecially.domain.OrderSource;
import com.cookedspecially.domain.PaymentType;
import com.cookedspecially.domain.Restaurant;
import com.cookedspecially.dto.OrderStatusDTO;

/**
 * @author shashank, rahul
 *
 */
public interface RestaurantService {

	public void addRestaurant(Restaurant restaurant);
	public List<Restaurant> listRestaurant();
	public List<Restaurant> listRestaurantById(Integer restaurantId);
	public List<Restaurant> listRestaurantByParentId(Integer parentId);
	public void removeRestaurant(Integer id);
	public Restaurant getRestaurant(Integer id);
	public Restaurant getRestaurantByName(String restaurantName);
	
	public List<Discount_Charges> listDiscountCharges(Integer restId);
	public void addDC(Discount_Charges discount_Charges);
	public void removeDC(Integer id);
	public Discount_Charges getDCById(Integer id);
	
	public List<Nutrientes> getNutirentList(Integer restId);
	public Nutrientes getByNutrientesByNameType(String name, String dishType, Integer restId);
	public void addNutrientes(Nutrientes nutrientes);
	public void removeNutrientes(Integer id);
	public Nutrientes getNutrientes(Integer id);
	
	public void addOrderSource(OrderSource orderSource);
	public List<OrderSource> listOrderSourcesByOrgId(Integer orgId);
	public void removeOrderSources(Integer id);
	public OrderSource getOrderSources(Integer id);
	
	public void addPaymentType(PaymentType paymentType);
	public List<PaymentType> listPaymentTypeByOrgId(Integer orgId);
	public void removePaymentType(Integer id);
	public PaymentType getPaymentType(Integer id);
	public Restaurant getParentRestaurant(int restaurantId);
	public PaymentType getPaymentTypeByName(Integer restaurantId, String name);
	public List<Integer> getAllOrganisation();
	public String getRestaurantUnitInfoForAssociatedCustomer(int customerId, String string);
	
	public OrderStatusDTO checkPaytmOrderStatus(Integer orderId) throws IOException;
	public OrderStatusDTO checkCitrusOrderStatus(Integer orderId) throws IOException;
	
	public void emailException(String excpetionLogs,HttpServletRequest request) throws MessagingException, UnsupportedEncodingException;
	public void alertMail(String message,String subject,HttpServletRequest request) throws MessagingException, UnsupportedEncodingException;
	public String resolveCitrusResponseCode(String pgRespCode);
	public Check cancelInvoice(Check check);
}