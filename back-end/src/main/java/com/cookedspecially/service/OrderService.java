/**
 * 
 */
package com.cookedspecially.service;

import com.cookedspecially.domain.Check;
import com.cookedspecially.domain.Order;
import com.cookedspecially.dto.OrderDTO;
import com.cookedspecially.enums.order.Status;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

/**
 * @author shashank
 *
 */
public interface OrderService {

	public List<Order> listOrders(Map<String, Object> queryMap);
	public void addOrder(Order order);
	public void removeOrder(Integer id) throws Exception;
	public Order getOrder(Integer id);
	public List<Integer> getAllOpenOrderCheckIds(Integer restaurantId);
	/*public List<Order> getDailyDeliveryBoyInvoice(Integer restaurantId, Date startDate, Date endDate);*/
	public List<OrderDTO> getOrders(Integer restaurantId, List<String> orderType,String ordersOfDay);
	public List<OrderDTO> getDispatchedCancelOrders(Integer restaurantId, List<String> orderType,String ordersOfDay);
	public String emailCreditCheckFromServer(HttpServletRequest request,Check check,int customerId, String emailAddr, Status status,String refund, String subject, String sender,String reason,float extraAmount,String h) throws MessagingException, UnsupportedEncodingException;

}
