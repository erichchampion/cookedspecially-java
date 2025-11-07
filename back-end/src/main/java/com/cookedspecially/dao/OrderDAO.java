/**
 * 
 */
package com.cookedspecially.dao;

import com.cookedspecially.domain.Order;

import java.util.List;
import java.util.Map;

/**
 * @author shashank
 *
 */
public interface OrderDAO {

	public List<Order> listOrders(Map<String, Object> queryMap);
	public void addOrder(Order order);
	public void removeOrder(Integer id) throws Exception;
	public Order getOrder(Integer id);
	public List<Integer> getAllOpenOrderCheckIds(Integer restaurantId);
/*	public List<Order> getDailyDeliveryBoyInvoice(Integer restaurantId, Date startDate, Date endDate);*/
    public List<Order> getOpenOrders(Map queryMap);
    public List<Order> getOpenDispatchedCancelOrders(Map queryMap);
}
