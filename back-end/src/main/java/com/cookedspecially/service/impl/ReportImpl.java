package com.cookedspecially.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cookedspecially.dao.ReportDAO;
import com.cookedspecially.service.Report;
import com.cookedspecially.utility.DateUtil;

@Transactional
@Service
public class ReportImpl implements Report{

	final static Logger logger = Logger.getLogger(ReportImpl.class);
	private final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

	@Autowired
	private ReportDAO reportDao;

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getDeliveryBoyReport(int ffcId,
			String fromDate, String toDate, int userId, String inputDateTimeZone) {
		logger.info("getDeliveryReport ../ ffcId= " + ffcId + " userId=" + userId + " timeZone = " + inputDateTimeZone + " fromDate = " + fromDate + " toDate=" + toDate);
		List<Object> data = new ArrayList<Object>(); 
		try{
			Date fromDateGMT = DateUtil.convertTimeInMilliSecToTimestampInGMT(DateUtil.addToDate(DateUtil.getCurrentTimestampInGMT(), -30, 0, 0, "GMT").getTime());
			Date toDateGMT = DateUtil.getCurrentTimestampInGMT();

			if (fromDate != null)
				fromDateGMT = DateUtil.convertDateStringIntoTimeStampGMT(fromDate + " 00:00:00", DEFAULT_DATE_FORMAT, inputDateTimeZone);

			if (toDate != null)
				toDateGMT = DateUtil.convertDateStringIntoTimeStampGMT(toDate + " 23:59:59", DEFAULT_DATE_FORMAT, inputDateTimeZone);

			logger.info("From Date  =  " + fromDateGMT + ",    toDate  =  " + toDateGMT);
			data.add(Arrays.asList("UserId:number","UserName","Name", "MobileNo", "Last-Order-Delivered-On:datetime","First-Order-Delivered-On:datetime","Order-Count:number"));
			data=ListUtils.union(data,reportDao.listDeliveryBoyReport(fromDateGMT, toDateGMT, ffcId));;
		}catch(Exception ex){
			logger.error("Could not find any record");
		}
		return data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> listInvoiceDeliveredByDeliveryBoy(int ffcId,
			int deliveryBoyId, String fromDate, String toDate, int userId,
			String inputDateTimeZone) {
		logger.info("listInvoiceDeliveredByDeliveryBoy ../ ffcId= " + ffcId + " deliveryBoyId="+deliveryBoyId+" userId=" + userId + " timeZone = " + inputDateTimeZone + " fromDate = " + fromDate + " toDate=" + toDate);
		List<Object> data = new ArrayList<Object>(); 
		try{
			Date fromDateGMT = DateUtil.convertTimeInMilliSecToTimestampInGMT(DateUtil.addToDate(DateUtil.getCurrentTimestampInGMT(), -30, 0, 0, "GMT").getTime());
			Date toDateGMT = DateUtil.getCurrentTimestampInGMT();

			if (fromDate != null)
				fromDateGMT = DateUtil.convertDateStringIntoTimeStampGMT(fromDate + " 00:00:00", DEFAULT_DATE_FORMAT, inputDateTimeZone);

			if (toDate != null)
				toDateGMT = DateUtil.convertDateStringIntoTimeStampGMT(toDate + " 23:59:59", DEFAULT_DATE_FORMAT, inputDateTimeZone);

			data.add(Arrays.asList("InvoiceId","Payment-Type", "OrderPlace-On:datetime", "OrderDelivered-On:datetime", "DeliveryArea","DeliveryAddress", "Bill-Amount:number"));
			data=ListUtils.union(data,reportDao.listInvoceDelivered(fromDateGMT, toDateGMT, ffcId, deliveryBoyId));
			logger.info("From Date  =  " + fromDateGMT + ",    toDate  =  " + toDateGMT);
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Could not find any record for selected deliveryBoy");
		}
		return data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> topDishes(int id, int count, String fromDate, String toDate, int userId, String inputDateTimeZone, String level) {
		logger.info("topDishes ../ id= " + id +" level="+level+ " count="+count+" userId=" + userId + " timeZone = " + inputDateTimeZone + " fromDate = " + fromDate + " toDate=" + toDate);
		try{
			Date fromDateGMT = DateUtil.convertTimeInMilliSecToTimestampInGMT(DateUtil.addToDate(DateUtil.getCurrentTimestampInGMT(), -30, 0, 0, "GMT").getTime());
			Date toDateGMT = DateUtil.getCurrentTimestampInGMT();

			if (fromDate != null)
				fromDateGMT = DateUtil.convertDateStringIntoTimeStampGMT(fromDate + " 00:00:00", DEFAULT_DATE_FORMAT, inputDateTimeZone);

			if (toDate != null)
				toDateGMT = DateUtil.convertDateStringIntoTimeStampGMT(toDate + " 23:59:59", DEFAULT_DATE_FORMAT, inputDateTimeZone);
			
			logger.info("From Date  =  " + fromDateGMT + ",    toDate  =  " + toDateGMT);
			List<Object> dishList = new ArrayList<>(); 
			dishList.add(Arrays.asList("Dish-Id:number", "Dish-Name","Category", "Unit-Price:number", "Quantity:number", "TotalPrice:number"));
			return ListUtils.union(dishList,reportDao.topDishList(level, fromDateGMT, toDateGMT, id, 0));
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Could not find top Dishes record for selected level"+level);
		}
		return null;	
		}

	@Override
	public List<Object> listDishCategory(int id, int userId, String level) {
		logger.info("listDishCategory ../ id= " + id +" level="+level+" userId=" + userId );
		try{
			return reportDao.listDishCategory(level,id);
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Could not find top Dishes record for selected level"+level);
		}
		return null;	
	}




}
