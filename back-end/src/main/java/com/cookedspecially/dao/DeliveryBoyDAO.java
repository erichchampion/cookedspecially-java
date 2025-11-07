package com.cookedspecially.dao;
import java.util.List;

import com.cookedspecially.domain.DeliveryBoy;

/**
 * @author rahul
 *
 */

public interface DeliveryBoyDAO
{
	public void addDeliveryBoy(DeliveryBoy deliveryBoy);
	public List<DeliveryBoy> listDeliveryBoy();
	public DeliveryBoy getDeliveryBoy(Integer id);
	public List<DeliveryBoy> listDeliveryBoyByUser(Integer userId);
	public void removeDeliveryBoy(Integer id);
}
