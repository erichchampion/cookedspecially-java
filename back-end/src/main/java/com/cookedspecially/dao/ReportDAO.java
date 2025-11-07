package com.cookedspecially.dao;

import java.util.Date;
import java.util.List;

public interface ReportDAO {

	List<Object> listDeliveryBoyReport(Date fromDateGMT,
			Date toDateGMT, int ffcId);
	List<Object> listInvoceDelivered(Date from, Date to, int ffcId, int deliveryBoyId);
	List topDishList(String level, Date from, Date to, int id, int count);
	List<Object> listDishCategory(String level, int id);

}
