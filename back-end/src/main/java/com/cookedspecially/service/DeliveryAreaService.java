/**
 * 
 */
package com.cookedspecially.service;

import java.util.List;

import com.cookedspecially.domain.DeliveryArea;

/**
 * @author shashank
 *
 */
public interface DeliveryAreaService {
	
	//public static String defaultDeliveryArea = "Others";
	
	public void addDeliveryArea(DeliveryArea deliveryArea);
	public List<DeliveryArea> listDeliveryAreas();
	public DeliveryArea getDeliveryAreaByName(String name, Integer fulfillmentCenterId,Integer restaurantId);
	public List<DeliveryArea> listDeliveryAreasByResaurant(Integer restaurantId);
	public List<DeliveryArea> listDeliveryAreasByFulfillmentCenter(Integer fulfillmentCenterId);
	public void removeDeliveryArea(Integer id);
	public DeliveryArea getDeliveryArea(Integer id);
}
