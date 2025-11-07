/**
 * 
 */
package com.cookedspecially.dao;

import java.util.List;

import com.cookedspecially.domain.Discount_Charges;
import com.cookedspecially.domain.Nutrientes;
import com.cookedspecially.domain.OrderSource;
import com.cookedspecially.domain.PaymentType;
import com.cookedspecially.domain.Restaurant;
import com.cookedspecially.domain.User;

/**
 * @author shashank, rahul
 *
 */
public interface RestaurantDAO {

	public void addRestaurant(Restaurant restaurant);
	public List<Restaurant> listRestaurant();
	public List<Restaurant> listRestaurantById(Integer restaurantId);
	public List<Restaurant> listRestaurantByParentId(Integer parentId);
	public void removeRestaurant(Integer id);
	public Restaurant getRestaurant(Integer id);
	public void saveResaurant(Restaurant rest);
	public Restaurant getRestaurantByName(String restaurantName);
	
	
	public List<Discount_Charges> listDiscountCharges(Integer restId);
	public void addDC(Discount_Charges discount_Charges);
	public void removeDC(Integer id);
	public Discount_Charges getDCById(Integer id);
	
	public List<Nutrientes> getNutirentList(Integer restId);
	public void addNutrientes(Nutrientes nutrientes);
	public Nutrientes getByNutrientesByNameType(String name, String dishType, Integer restId);
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
	public PaymentType getPaymentTypeByName(String paymentTypeName, int orgId);
	public List<Integer> getAllOrganisation();
	public String getRestaurantUnitInfoForAssociatedCustomer(int customerId, String property);
}
