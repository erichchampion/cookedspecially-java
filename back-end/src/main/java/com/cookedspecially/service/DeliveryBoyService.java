package com.cookedspecially.service;

import java.util.List;

import com.cookedspecially.domain.DeliveryBoy;

/**
 * @author Rahul
 *
 */

public interface DeliveryBoyService {

	public void addDeliveryBoy(DeliveryBoy deliveryBoy);
	public List<DeliveryBoy> listDeliveryBoys();
	public List<DeliveryBoy> listDeliveryBoyByUser(Integer userId);
	public void removeDeliveryBoy(Integer id);
	public DeliveryBoy getDeliveryBoy(Integer id);	
}
