package com.cookedspecially.service;

import java.util.List;

public interface Report {

	List<Object> getDeliveryBoyReport(int ffcId, String fromDate, String toDate, int userId, String inputDateTimeZone);

	List<Object> listInvoiceDeliveredByDeliveryBoy(int ffcId,
			int deliveryBoyId, String fromDate, String toDate, int userId,
			String inputDateTimeZone);
	
	List<Object> topDishes(int id, int count, String fromDate, String toDate,
			int userId, String inputDateTimeZone, String level);

	List<Object> listDishCategory(int id, int userId, String level);

}
