package com.cookedspecially.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.cookedspecially.domain.Check;
import com.cookedspecially.domain.Menu;
import com.cookedspecially.domain.Restaurant;
import com.cookedspecially.domain.TaxType;
import com.cookedspecially.dto.PlaceOrderDTO;
import com.cookedspecially.dto.ResponseDTO;
import com.cookedspecially.dto.zomato.order.ZomatoOrderBody;

public interface ZomatoService {

	public ResponseDTO createZomatoMenu(Menu menu,HttpServletRequest request) throws IOException;
	public ResponseDTO updateZomatoMenu(Menu menu,HttpServletRequest request) throws IOException;
	public PlaceOrderDTO convertZomatoJsonToPlaceOrderDTO(ZomatoOrderBody orderDTO);
	public ResponseDTO inactivateTax(TaxType taxType,HttpServletRequest request) throws IOException;
	public ResponseDTO updateMenuWithSections(Menu oldMenu, Menu newMenu,HttpServletRequest request);
	
	public ResponseDTO zomatoOrderConfirm(Check check, Integer dateTime,HttpServletRequest request);
	public ResponseDTO zomatoOrderReject(Check check,HttpServletRequest request);
	public ResponseDTO zomatoOrderOutForDelivery(Check check,HttpServletRequest request);
	public ResponseDTO zomatoOrderUpdateDeliveryBoyInfo(Check check,HttpServletRequest request);
	public ResponseDTO zomatOrderDelivered(Check check,HttpServletRequest request);
	public Check getZomatoOrderById(Integer orderId);
	public ResponseDTO setZomatoRestaurantStatus(Restaurant restaurant,HttpServletRequest request);
	
}
