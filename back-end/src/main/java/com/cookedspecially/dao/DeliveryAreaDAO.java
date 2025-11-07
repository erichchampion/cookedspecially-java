/**
 * 
 */
package com.cookedspecially.dao;

import java.util.List;

import com.cookedspecially.domain.DeliveryArea;

/**
 * @author shashank
 *
 */
public interface DeliveryAreaDAO {
	public void addDeliveryArea(DeliveryArea deliveryArea);
	public List<DeliveryArea> listDeliveryArea();
	public DeliveryArea getDeliveryAreaByName(String name, Integer fulfillmentCenterId,Integer restaurantId);
	public List<DeliveryArea> listDeliveryAreaByRestaurant(Integer restaurantId);
	public List<DeliveryArea> listDeliveryAreaByFulfillmentCenter(Integer fulfillmentCenterId);
	public void removeDeliveryArea(Integer id);
	public DeliveryArea getDeliveryArea(Integer id);
}
