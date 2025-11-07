/**
 * 
 */
package com.cookedspecially.view;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TimeZone;




//import org.apache.poi.xssf.usermodel.XSSFWorkbook; 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.cookedspecially.controller.OrderController;
import com.cookedspecially.domain.Check;
import com.cookedspecially.domain.Coupon;
import com.cookedspecially.domain.Customer;
import com.cookedspecially.domain.Dish;
import com.cookedspecially.domain.Dish_Size;
import com.cookedspecially.domain.FulfillmentCenter;
import com.cookedspecially.domain.Order;
import com.cookedspecially.domain.OrderDish;
import com.cookedspecially.domain.Order_DCList;
import com.cookedspecially.domain.PaymentType;
import com.cookedspecially.domain.Restaurant;
import com.cookedspecially.domain.TaxType;
import com.cookedspecially.domain.Transaction;
import com.cookedspecially.domain.User;
import com.cookedspecially.enums.check.AdditionalCategories;
import com.cookedspecially.enums.check.BasePaymentType;
import com.cookedspecially.enums.check.CheckType;
import com.cookedspecially.enums.check.OrderSource;
import com.cookedspecially.enums.check.PaymentMode;
import com.cookedspecially.enums.check.Status;
import com.cookedspecially.utility.StringUtility;
//import org.apache.poi.hssf.usermodel.Row;

/**
 * @author shashank , rahul
 *
 */
public class ExcelReportView extends AbstractExcelView {

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.view.document.AbstractExcelView#buildExcelDocument(java.util.Map, org.apache.poi.hssf.usermodel.HSSFWorkbook, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	final static Logger logger = Logger.getLogger(OrderController.class);
	
	@Override
	protected void buildExcelDocument(Map<String, Object> model,HSSFWorkbook workbooks, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Workbook workbook = (Workbook)workbooks;
		Map<String,Object> reportDataMap = (Map<String,Object>) model.get("reportData");
		List<String> headers = (List<String>)reportDataMap.get("Headers");
		String reportName = (String)reportDataMap.get("reportName");
		//create a worksheet
		Sheet sheet = workbook.createSheet("Report");
				
		List<String> taxList =  (List<String>)reportDataMap.get("taxList");
		List<TaxType> taxType =  (List<TaxType>)reportDataMap.get("taxType");
		
		List<FulfillmentCenter> kitchenScreen = (List<FulfillmentCenter>)reportDataMap.get("kitchenScreens");
		
		if (StringUtility.isNullOrEmpty(reportName)) {
			return;
		}
		// Fonts
		//HSSFCellStyle style=null;

	    Font defaultFont= workbook.createFont();
	    defaultFont.setFontHeightInPoints((short)15);
	    defaultFont.setFontName(HSSFFont.FONT_ARIAL);
	    defaultFont.setColor(HSSFColor.BLACK.index);
	    defaultFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	    defaultFont.setItalic(true);

	    CellStyle style=null;
	    style=workbook.createCellStyle();
	    //style.setFillBackgroundColor(HSSFColor.AUTOMATIC.index);
	    style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
	    style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	    //style.setWrapText(true);
	    style.setFont(defaultFont);
		
		
		int rowNum = 0;
		// create restaurant Name VAT @ 13.13%
		String restaurantName = (String)reportDataMap.get("restaurantName");
		if (!StringUtility.isNullOrEmpty(restaurantName)) {
			Row row = sheet.createRow(rowNum++);
			row.setRowStyle(style);
			//row.setHeight((short)0);
			Cell cell = row.createCell(0);
			cell.setCellValue(restaurantName);
			cell.setCellStyle(style);
			rowNum++;
			
			row.setHeightInPoints(-1);

		}
		
		// Create Report Name entry
		Row row = sheet.createRow(rowNum++);
		row.setRowStyle(style);
		//row.setHeight((short)0);
		Cell cell = row.createCell(0);
		cell.setCellValue(reportName);
		cell.setCellStyle(style);
		row.setHeightInPoints(-1);
		rowNum++;
		
		CellStyle headerStyle=workbook.createCellStyle();
	    //style.setFillBackgroundColor(HSSFColor.AUTOMATIC.index);
		headerStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		headerStyle.setAlignment(HSSFCellStyle.ALIGN_JUSTIFY);
	    //style.setWrapText(true);
		headerStyle.setFont(defaultFont);
		headerStyle.setBorderBottom((short)1);
		//headerStyle.setBorderLeft((short)1);
		headerStyle.setBorderTop((short)1);
		headerStyle.setBorderRight((short)1);
		headerStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		headerStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		headerStyle.setTopBorderColor(HSSFColor.BLACK.index);
		headerStyle.setRightBorderColor(HSSFColor.BLACK.index);
		// Create actual headers
		Row header = sheet.createRow(rowNum++);
	    int headerNum = 0;
		for (String headerValue : headers) {
			Cell headerCell = header.createCell(headerNum++);	
			headerCell.setCellValue(headerValue);
			headerCell.setCellStyle(headerStyle);
		}
		header.setHeightInPoints(-1);
		if (reportName.equals("checkReport")) {
			createCheckReport(sheet, reportDataMap, rowNum);
		} else if (reportName.equals("Daily Invoice")) {
			createDailyInvoiceReport(sheet, reportDataMap, rowNum,taxList,kitchenScreen);
		} else if (reportName.equals("Daily Sales Summary")) {
			createDailySalesSummaryReport(sheet, reportDataMap, rowNum,kitchenScreen,taxType);
		} else if (reportName.equals("Customers")) {
			createCustomerReport(sheet, reportDataMap, rowNum);
		} else if (reportName.equals("Top Dishes")) {
			createTopDishesReport(sheet, reportDataMap, rowNum,kitchenScreen);
		} else if (reportName.equals("Detailed Invoice")) {
			createDetailedInvoiceReport(sheet, reportDataMap, rowNum,taxList);
		} else if (reportName.equals("Daily Sales Summary New")) {
			createDailySalesSummaryReportNew(sheet, reportDataMap, rowNum,kitchenScreen,taxType,workbook);
		}else if (reportName.equals("CustomersSummery")) {
			createCustomerSummery(sheet, reportDataMap, rowNum);
			}
		else if(reportName.equals("Sales Summary")){
			salesSummaryReport(sheet, reportDataMap, rowNum,kitchenScreen,taxType, workbook);
		}
		else if(reportName.equals("Transaction details")){
			createTransactionSummery(sheet, reportDataMap, rowNum, workbook);
		}
	}
	
	
	private Map<String, Double> getDishTypeBillMap(Check check) {
		Map<String, Double> dishTypeBillMap = new HashMap<String, Double>();
		List<Order> orders = check.getOrders();
		for (Order order : orders) {
			if (order.getStatus() == com.cookedspecially.enums.order.Status.CANCELLED) {
				continue;
			}
			List<OrderDish> items = order.getOrderDishes();
			for (OrderDish item: items) {
				if(dishTypeBillMap.containsKey(item.getDishType())) {
					dishTypeBillMap.put(item.getDishType(), dishTypeBillMap.get(item.getDishType()) + (item.getPrice() * item.getQuantity()));
				} else {
					dishTypeBillMap.put(item.getDishType(), (double)item.getPrice() * item.getQuantity());
				}
			}
		}
		return dishTypeBillMap;
	}
	
	private Map<String, Double> getPaymentTypeBillMap(Check check,List<TaxType> taxList) {
		Map<String, Double> payTypeBillMap = new HashMap<String, Double>();
		List<Order> orders = check.getOrders();
		int countOrder=0;
		for(Order order : orders){
		int	checkId = order.getCheckId();
		if(checkId == check.getCheckId()){
				countOrder++;
		}
		if (order.getStatus() == com.cookedspecially.enums.order.Status.CANCELLED) {
				continue;
		}
		else if(countOrder>1){
			continue;
		}
		for(PaymentMode pm :PaymentMode.values()){
				if(pm==PaymentMode.PENDING || pm==PaymentMode.PG_PENDING){
					continue;
				}
		            double total = 0;		
					float taxSum=0.0f;
					if(check.getTaxJsonObject()!=null && check.getDiscountAmount()>0){
						LinkedHashMap<String,Float> taxD = new LinkedHashMap<String,Float>();
						if(taxList!=null){
						for(TaxType tax:taxList){
							//applying  default taxes here
							if(tax.getDishType().equalsIgnoreCase("Default")){
							taxD.put(tax.getName(),(float) tax.getTaxCharge(check.getBill() - check.getDiscountAmount(),tax.getChargeType(),tax.getTaxValue()));
							taxSum+=tax.getTaxCharge(check.getBill()-check.getDiscountAmount(),tax.getChargeType(),tax.getTaxValue());
						}
						}
						total =taxSum +(check.getBill()-check.getDiscountAmount())+check.getOutCircleDeliveryCharges();
					}
					}
					 else{
						 total = check.getRoundOffTotal();
					 }
			if(pm.toString().equalsIgnoreCase(order.getPaymentStatus())){
				payTypeBillMap.put(pm.toString(),payTypeBillMap.get(pm.toString())==null?0.0+
						total:payTypeBillMap.get(pm.toString())+total);
			}
			}
		}
		return payTypeBillMap;
	}
	
	private void createDailySalesSummaryReport(Sheet sheet, Map<String,Object> reportDataMap, int startRowNum, List<FulfillmentCenter> kitchenScreen,List<TaxType> taxType) {
		int rowNum = startRowNum;
		Set<String> dishTypes = (Set<String>)reportDataMap.get("dishTypes");
		List<Check> checks = (List<Check>)reportDataMap.get("data");
		List<Integer>  grossCount  =  new ArrayList<Integer>();
		List<PaymentType> paymentType = (List<PaymentType>)reportDataMap.get("paymentType");
		int cellNo=0;
		Integer totalCustomer = 0;
		//Map<String, Double> dishTypeTotalBillMap = new HashMap<String, Double>();
		Map<String, Map<String, Double>> checkTypevsDishTypeBillMap = new HashMap<String, Map<String,Double>>();
		Map<String, Map<String, Double>> allPaymentTypeBillMap = new HashMap<String, Map<String,Double>>();
		Map<String, Integer> checkTypeCountMap = new HashMap<String, Integer>();
		Map<String, Double> dishTypeTotalBillMap = new HashMap<String, Double>();
		Map<String, Double> paymentTypeTotalBillMap = new HashMap<String, Double>();
		Map<String, Double> grosseTotalBillMap = new HashMap<String, Double>();
		Map<String, Double> grossePaymentTotalBillMap = new HashMap<String, Double>();
		// Initialize
		for (String dishType: dishTypes) {
			grosseTotalBillMap.put(dishType, 0.0);
			}
		for (PaymentMode pm:  PaymentMode.values()) {
			grossePaymentTotalBillMap .put(pm.toString(), 0.0);
			}
		//If Restaurant have multiple Kitchens then if condition will work for all  calculations .
		if(kitchenScreen.size()!=0){
			
			for(FulfillmentCenter kS : kitchenScreen ){
				for (String dishType: dishTypes) {
					dishTypeTotalBillMap.put(dishType, 0.0);
					}
				for(PaymentMode pm :PaymentMode.values()){
					paymentTypeTotalBillMap.put(pm.name().toString(),0.0);
				}
				for(CheckType checkTypeEnum : CheckType.values()) {
					Map<String, Double> dishTypeBillMap = new HashMap<String, Double>();
					Map<String, Double> paymentTypeBillMap = new HashMap<String, Double>();
					for (String dishType : dishTypes) {
					dishTypeBillMap.put(dishType, 0.0);
					}
					for(PaymentMode pm : PaymentMode.values()){
						paymentTypeBillMap.put(pm.toString(), 0.0);
					}
					checkTypevsDishTypeBillMap.put(checkTypeEnum.toString(), dishTypeBillMap);
					allPaymentTypeBillMap.put(checkTypeEnum.toString(), paymentTypeBillMap);
					checkTypeCountMap.put(checkTypeEnum.toString(),0);
					}
		
				for(Check check : checks){
						if(check.getKitchenScreenId()==kS.getId()){
						if (check.getStatus() != Status.Paid) {
							continue;
						}
					
					Map<String, Double> paymentTypeBillMapValues = getPaymentTypeBillMap(check,taxType);
					 for (PaymentMode payType : PaymentMode.values()) {
							Double payTypeBill = Double.valueOf((paymentTypeBillMapValues.containsKey(payType.name().toString()))? paymentTypeBillMapValues.get(payType.name().toString()):0);
							Map<String, Double> paymentTypePriceMap = allPaymentTypeBillMap.get((check.getCheckType()==null)?CheckType.Delivery.toString():check.getCheckType().toString());
							paymentTypePriceMap.put(payType.name().toString(), paymentTypePriceMap.get(payType.name().toString()) + payTypeBill);
							paymentTypeTotalBillMap.put(payType.name().toString(),paymentTypeTotalBillMap.get(payType.name().toString()) + payTypeBill);
						}
						
					Map<String, Double> dishTypeBillMap = getDishTypeBillMap(check);
					for (String dishType : dishTypes) {
						Double dishTypeBill =Double.valueOf((dishTypeBillMap.containsKey(dishType))?dishTypeBillMap.get(dishType):0);
						Map<String, Double> dishTypePriceMap = checkTypevsDishTypeBillMap.get((check.getCheckType()==null)?CheckType.Delivery.toString():check.getCheckType().toString());
						dishTypePriceMap.put(dishType, dishTypePriceMap.get(dishType) + dishTypeBill);
						dishTypeTotalBillMap.put(dishType, dishTypeTotalBillMap.get(dishType) + dishTypeBill);
						}
						checkTypeCountMap.put((check.getCheckType()==null)?CheckType.Delivery.toString():check.getCheckType().toString(), checkTypeCountMap.get((check.getCheckType()==null)?CheckType.Delivery.toString():check.getCheckType().toString()) + 1);
						}
					}
				//	 cellNo = 0;
				 for (CheckType checkTypeEnum : CheckType.values()) {
						Row row = sheet.createRow(rowNum++);
						cellNo = 0;
						row.createCell(cellNo++).setCellValue(checkTypeEnum.toString());
						row.createCell(cellNo++).setCellValue(kS.getName());
						Double total = 0.0;
						Map<String, Double> dishTypeBillMap = checkTypevsDishTypeBillMap.get(checkTypeEnum.toString());
						for (String dishType : dishTypes) {
							row.createCell(cellNo++).setCellValue(dishTypeBillMap.get(dishType));
						}
						Map<String, Double> payTypeBillMap = allPaymentTypeBillMap.get(checkTypeEnum.toString());
						
						for (PaymentMode pm: PaymentMode.values()) {
								if(pm==PaymentMode.PENDING || pm==PaymentMode.PG_PENDING){
									continue;
								}
						    row.createCell(cellNo++).setCellValue(payTypeBillMap.get(pm.toString()));
						    total += payTypeBillMap.get(pm.toString());
						}
						row.createCell(cellNo++).setCellValue(total);
						Integer customers = checkTypeCountMap.get(checkTypeEnum.toString());
						row.createCell(cellNo++).setCellValue(customers);
						totalCustomer += customers;
						row.createCell(cellNo++).setCellValue(customers>0?total/customers:0);
				 	}
				for (String dishType : dishTypes) {
						grosseTotalBillMap.put(dishType, grosseTotalBillMap.get(dishType)+dishTypeTotalBillMap.get(dishType));
					}
				for (PaymentMode pm : PaymentMode.values()) {
					grossePaymentTotalBillMap .put(pm.toString(), grossePaymentTotalBillMap.get(pm.toString())+paymentTypeTotalBillMap.get(pm.toString()));
				}
		        }
			//Same condition as above but work in case of " Reports with date range" when restaurant switch form single kitchen to multiple kitchens.
			for (String dishType: dishTypes) {
				dishTypeTotalBillMap.put(dishType, 0.0);
				}
			for(PaymentMode pm :PaymentMode.values()){
				paymentTypeTotalBillMap.put(pm.name().toString(),0.0);
			}
			for(CheckType checkTypeEnum : CheckType.values()) {
				Map<String, Double> dishTypeBillMap = new HashMap<String, Double>();
				Map<String, Double> paymentTypeBillMap = new HashMap<String, Double>();
				for (String dishType : dishTypes) {
				dishTypeBillMap.put(dishType, 0.0);
				}
				for(PaymentMode pm : PaymentMode.values()){
					paymentTypeBillMap.put(pm.toString(), 0.0);
				}
				checkTypevsDishTypeBillMap.put(checkTypeEnum.toString(), dishTypeBillMap);
				allPaymentTypeBillMap.put(checkTypeEnum.toString(), paymentTypeBillMap);
				checkTypeCountMap.put(checkTypeEnum.toString(),0);
				}
	
			for(Check check : checks){
					if(check.getKitchenScreenId()==0){
					if (check.getStatus() != Status.Paid) {
						continue;
					}
				
				Map<String, Double> paymentTypeBillMapValues = getPaymentTypeBillMap(check,taxType);
				 for (PaymentMode payType : PaymentMode.values()) {
						Double payTypeBill =Double.valueOf((paymentTypeBillMapValues.containsKey(payType.name().toString()))?paymentTypeBillMapValues.get(payType.name().toString()):0);
						Map<String, Double> paymentTypePriceMap = allPaymentTypeBillMap.get((check.getCheckType()==null)?CheckType.Delivery.toString():check.getCheckType().toString());
						paymentTypePriceMap.put(payType.name().toString(), paymentTypePriceMap.get(payType.name().toString()) + payTypeBill);
						paymentTypeTotalBillMap.put(payType.name().toString(),paymentTypeTotalBillMap.get(payType.name().toString()) + payTypeBill);
					}
					
				Map<String, Double> dishTypeBillMap = getDishTypeBillMap(check);
				for (String dishType : dishTypes) {
					Double dishTypeBill =Double.valueOf((dishTypeBillMap.containsKey(dishType))?dishTypeBillMap.get(dishType):0);
					Map<String, Double> dishTypePriceMap = checkTypevsDishTypeBillMap.get((check.getCheckType()==null)?CheckType.Delivery.toString():check.getCheckType().toString());
					dishTypePriceMap.put(dishType, dishTypePriceMap.get(dishType) + dishTypeBill);
					dishTypeTotalBillMap.put(dishType, dishTypeTotalBillMap.get(dishType) + dishTypeBill);
					}
					checkTypeCountMap.put((check.getCheckType()==null)?CheckType.Delivery.toString():check.getCheckType().toString(), checkTypeCountMap.get((check.getCheckType()==null)?CheckType.Delivery.toString():check.getCheckType().toString()) + 1);
					}
				}
			//	 cellNo = 0;
			 for (CheckType checkTypeEnum : CheckType.values()) {
					Row row = sheet.createRow(rowNum++);
					cellNo = 0;
					row.createCell(cellNo++).setCellValue(checkTypeEnum.toString());
					row.createCell(cellNo++).setCellValue("Default");
					Double total = 0.0;
					Map<String, Double> dishTypeBillMap = checkTypevsDishTypeBillMap.get(checkTypeEnum.toString());
					for (String dishType : dishTypes) {
						row.createCell(cellNo++).setCellValue(dishTypeBillMap.get(dishType));
					}
					Map<String, Double> payTypeBillMap = allPaymentTypeBillMap.get(checkTypeEnum.toString());
					
					for (PaymentMode pm: PaymentMode.values()) {
							if(pm==PaymentMode.PENDING || pm==PaymentMode.PG_PENDING){
								continue;
							}
					    row.createCell(cellNo++).setCellValue(payTypeBillMap.get(pm.toString()));
					    total += payTypeBillMap.get(pm.toString());
					}
					row.createCell(cellNo++).setCellValue(total);
					Integer customers = checkTypeCountMap.get(checkTypeEnum.toString());
					row.createCell(cellNo++).setCellValue(customers);
					totalCustomer += customers;
					row.createCell(cellNo++).setCellValue(customers>0?total/customers:0);
			 	}
			for (String dishType : dishTypes) {
					grosseTotalBillMap.put(dishType, grosseTotalBillMap.get(dishType)+dishTypeTotalBillMap.get(dishType));
				}
			for (PaymentMode pm : PaymentMode.values()) {
				grossePaymentTotalBillMap .put(pm.toString(), grossePaymentTotalBillMap.get(pm.toString())+paymentTypeTotalBillMap.get(pm.toString()));
			}
		} // this "else" condition will work if restaurant has only one Kitchen or default kitchen
		else{
				for (String dishType: dishTypes) {
					dishTypeTotalBillMap.put(dishType, 0.0);
					}
				for(PaymentMode pm :PaymentMode.values()){
					paymentTypeTotalBillMap.put(pm.name().toString(),0.0);
				}
				for(CheckType checkTypeEnum : CheckType.values()) {
					Map<String, Double> dishTypeBillMap = new HashMap<String, Double>();
					Map<String, Double> paymentTypeBillMap = new HashMap<String, Double>();
					for (String dishType : dishTypes) {
					dishTypeBillMap.put(dishType, 0.0);
					}
					for(PaymentMode pm : PaymentMode.values()){
						paymentTypeBillMap.put(pm.toString(), 0.0);
					}
					checkTypevsDishTypeBillMap.put(checkTypeEnum.toString(), dishTypeBillMap);
					allPaymentTypeBillMap.put(checkTypeEnum.toString(), paymentTypeBillMap);
					checkTypeCountMap.put(checkTypeEnum.toString(),0);
					}
		
				for(Check check : checks){
						if(check.getKitchenScreenId()==0){
						if (check.getStatus() != Status.Paid) {
							continue;
						}
					
					Map<String, Double> paymentTypeBillMapValues = getPaymentTypeBillMap(check,taxType);
					 for (PaymentMode payType : PaymentMode.values()) {
							Double payTypeBill =Double.valueOf((paymentTypeBillMapValues.containsKey(payType.name().toString()))?paymentTypeBillMapValues.get(payType.name().toString()):0);
							Map<String, Double> paymentTypePriceMap = allPaymentTypeBillMap.get((check.getCheckType()==null)?CheckType.Delivery.toString():check.getCheckType().toString());
							paymentTypePriceMap.put(payType.name().toString(), paymentTypePriceMap.get(payType.name().toString()) + payTypeBill);
							paymentTypeTotalBillMap.put(payType.name().toString(),paymentTypeTotalBillMap.get(payType.name().toString()) + payTypeBill);
						}
						
					Map<String, Double> dishTypeBillMap = getDishTypeBillMap(check);
					for (String dishType : dishTypes) {
						Double dishTypeBill = Double.valueOf((dishTypeBillMap.containsKey(dishType))?dishTypeBillMap.get(dishType):0);
						Map<String, Double> dishTypePriceMap = checkTypevsDishTypeBillMap.get((check.getCheckType()==null)?CheckType.Delivery.toString():check.getCheckType().toString());
						dishTypePriceMap.put(dishType, dishTypePriceMap.get(dishType) + dishTypeBill);
						dishTypeTotalBillMap.put(dishType, dishTypeTotalBillMap.get(dishType) + dishTypeBill);
						}
						checkTypeCountMap.put((check.getCheckType()==null)?CheckType.Delivery.toString():check.getCheckType().toString(), checkTypeCountMap.get((check.getCheckType()==null)?CheckType.Delivery.toString():check.getCheckType().toString()) + 1);
						}
					}
				//	 cellNo = 0;
				 for (CheckType checkTypeEnum : CheckType.values()) {
						Row row = sheet.createRow(rowNum++);
						cellNo = 0;
						row.createCell(cellNo++).setCellValue(checkTypeEnum.toString());
						row.createCell(cellNo++).setCellValue(0);
						Double total = 0.0;
						Map<String, Double> dishTypeBillMap = checkTypevsDishTypeBillMap.get(checkTypeEnum.toString());
						for (String dishType : dishTypes) {
							row.createCell(cellNo++).setCellValue(dishTypeBillMap.get(dishType));
						}
						Map<String, Double> payTypeBillMap = allPaymentTypeBillMap.get(checkTypeEnum.toString());
						
						for (PaymentMode pm: PaymentMode.values()) {
							if(pm==PaymentMode.PENDING || pm==PaymentMode.PG_PENDING){
								continue;
							}
						    row.createCell(cellNo++).setCellValue(payTypeBillMap.get(pm.toString()));
						    total += payTypeBillMap.get(pm.toString());
						}
						row.createCell(cellNo++).setCellValue(total);
						Integer customers = checkTypeCountMap.get(checkTypeEnum.toString());
						row.createCell(cellNo++).setCellValue(customers);
						totalCustomer += customers;
						row.createCell(cellNo++).setCellValue(customers>0?total/customers:0);
				 	}
				for (String dishType : dishTypes) {
						grosseTotalBillMap.put(dishType, grosseTotalBillMap.get(dishType)+dishTypeTotalBillMap.get(dishType));
					}
				for (PaymentMode pm : PaymentMode.values()) {
					grossePaymentTotalBillMap .put(pm.toString(), grossePaymentTotalBillMap.get(pm.toString())+paymentTypeTotalBillMap.get(pm.toString()));
				}
		}
		// final row
		Row row = sheet.createRow(rowNum++);
		cellNo = 0;
		row.createCell(cellNo++).setCellValue("Gross Sales");
		row.createCell(cellNo++).setCellValue(0);
		Double total = 0.0;
		
		for (String dishType : dishTypes) {
			row.createCell(cellNo++).setCellValue(grosseTotalBillMap.get(dishType));
		}
		for(PaymentMode pm : PaymentMode.values()){
			if(pm==PaymentMode.PENDING || pm==PaymentMode.PG_PENDING){
				continue;
			}
			row.createCell(cellNo++).setCellValue(grossePaymentTotalBillMap.get(pm.toString()));
			total += grossePaymentTotalBillMap.get(pm.toString());
		}
		row.createCell(cellNo++).setCellValue(total);
		row.createCell(cellNo++).setCellValue(totalCustomer);
		row.createCell(cellNo++).setCellValue(totalCustomer>0?total/totalCustomer:0);
		for (int i = 0; i < cellNo; i++) {
			sheet.autoSizeColumn(i);
		}
	}
	
	private void salesSummaryReport(Sheet sheet, Map<String,Object> reportDataMap, int startRowNum, List<FulfillmentCenter> kitchenScreen,List<TaxType> taxType,Workbook workbook) {
		int rowNum = startRowNum;
		float basePaymentTotal = 0;
		int backUpRow;
		int kitchenRow = 0;
		HashSet<Integer> unDelivered =  new HashSet<Integer>();
		HashSet<Integer> delivered =  new HashSet<Integer>();
		HashSet<Integer> cancelled  =  new HashSet<Integer>();
		Font defaultFont= workbook.createFont();
		defaultFont.setFontHeightInPoints((short)10);
		defaultFont.setFontName(HSSFFont.FONT_ARIAL);
		defaultFont.setColor(HSSFColor.BLACK.index);
		defaultFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		defaultFont.setItalic(true);
		
		CellStyle style=null;
	    style=workbook.createCellStyle();
	    style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
	    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
	    style.setAlignment(CellStyle.ALIGN_RIGHT);
	    style.setWrapText(true);
	    style.setFont(defaultFont);
	    
	    style.setBorderBottom((short)1);
	    style.setBorderLeft((short)1);
	    style.setBorderTop((short)1);
		style.setBorderRight((short)1);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		
		int cellNo=0;
		Row row = sheet.createRow(rowNum++);
		String pm = null;
		Cell cell = row.createCell(cellNo);
		List<PaymentType> paymentType = (List<PaymentType>)reportDataMap.get("paymentType");
		List<FulfillmentCenter> ffcL =  (List<FulfillmentCenter>)reportDataMap.get("kitchenScreens");
		List<com.cookedspecially.domain.OrderSource> orderSource  = (List<com.cookedspecially.domain.OrderSource>)reportDataMap.get("orderSource"); 
		OrderSource[] orderSourceBase = (OrderSource[]) reportDataMap.get("orderSourceBase");
		Restaurant rest =  (Restaurant)reportDataMap.get("restaurant");
		Map<String, Double> paymentTypeTotal= new HashMap<String, Double>();
		Map<String, Double> sourceTypeTotal= new HashMap<String, Double>();
		
		SortedMap<Date,List<Check>> che = (SortedMap<Date, List<Check>>) reportDataMap.get("dataList");
		Set<String> orderSourceList = new HashSet<String>();
		HashMap<String,Double> subFfcTotal =  new LinkedHashMap<String, Double>();
		HashMap<String,Double> grandTotal =  new LinkedHashMap<String, Double>();
		Map<String, Double> FfcFinalSourceTotal= new HashMap<String, Double>();
		
		int count=0;
		subFfcTotal.put("ffcTotal",0.0);
		grandTotal.put("grandTotal", 0.0);
		for(PaymentType pt : paymentType){
			paymentTypeTotal.put(pt.getName(),0.0);
			subFfcTotal.put(pt.getName(),0.0);
			grandTotal.put(pt.getName(),0.0);
		}
		for(PaymentType pt : paymentType){
		for(com.cookedspecially.domain.OrderSource os : orderSource){
			FfcFinalSourceTotal.put(pt.getName()+"-"+os.getName(),0.0);
		}
		for(OrderSource os : orderSourceBase){
			FfcFinalSourceTotal.put(pt.getName()+"-"+os.toString(),0.0);
		}}
		 row = sheet.createRow(rowNum++);
		 pm = null;
		 cellNo=1;
		 cell = row.createCell(cellNo++);
		 for(PaymentType pt : paymentType){
				cell = row.createCell(cellNo++);
				cell.setCellValue(pt.getName());
				cell.setCellStyle(style);
			}
			cell = row.createCell(cellNo++);
			cell.setCellValue("Total");
			cell.setCellStyle(style);
		for(FulfillmentCenter ffc: ffcL){
			row = sheet.createRow(rowNum++);
			cellNo = 0;
			cell = row.createCell(cellNo++);
			cell.setCellValue(ffc.getName());
			cell.setCellStyle(style);
			//row = sheet.createRow(rowNum++);
			row.createCell(cellNo++).setCellValue("");
			for(Date date : che.keySet()){
				List<Check> checks = che.get(date);

				for(PaymentType pt : paymentType){
				for(com.cookedspecially.domain.OrderSource os : orderSource){
					sourceTypeTotal.put(pt.getName()+"-"+os.getName(),0.0);
					orderSourceList.add(os.getName());
				}
				for(OrderSource os : orderSourceBase){
					sourceTypeTotal.put(pt.getName()+"-"+os.toString(),0.0);
					orderSourceList.add(os.toString());
				}
				}
				cellNo =1;
				float taxCount=0.0f;
				row = sheet.createRow(rowNum++);
				for(PaymentType pt : paymentType){
					basePaymentTotal = 0.0f;
					
					for(String os : orderSourceList){
						for(Check check : checks) {
							taxCount=0;
							List<Order> order =  check.getOrders();
						
						String mapName;
						if(check.getOrderSource().equalsIgnoreCase("0")){
							mapName = "Website";
			        	}
			        	else if(check.getOrderSource().equalsIgnoreCase("1")){
			        		mapName = "POS";
			        	}
			        	else if(check.getOrderSource().equalsIgnoreCase("2")){
			        		mapName = "Android";
			        	}
			        	else if(check.getOrderSource().equalsIgnoreCase("3")){
			        		mapName = "IOS";
			        	}
			        	else if(check.getOrderSource().equalsIgnoreCase("4")){
			        		mapName = "Third_Party";
			        	}
			        	else {
			        		mapName= check.getOrderSource();
			        	}
						if(mapName.equalsIgnoreCase(os)){
							if(check.getBill()==0 || check.getStatus()== Status.Cancel || ("CANCELED".equalsIgnoreCase(check.getTransactionStatus()))){
								cancelled.add(check.getCheckId());
							}
							for(Order or: order){
								if(or.getStatus()!=com.cookedspecially.enums.order.Status.DELIVERED && or.getStatus()!=com.cookedspecially.enums.order.Status.CANCELLED){
									unDelivered.add(check.getCheckId());
								}else if((!"CANCELED".equalsIgnoreCase(check.getTransactionStatus())) && check.getBill()!=0 && check.getStatus()!= Status.Cancel && or.getStatus()==com.cookedspecially.enums.order.Status.DELIVERED){
									delivered.add(check.getCheckId());
								}
								if(or.getPaymentStatus().equalsIgnoreCase("0")){
									pm = "CREDIT";
								}
								if(or.getPaymentStatus().equalsIgnoreCase("1")){
									pm = "CASH";
								}
								if(or.getPaymentStatus().equalsIgnoreCase("4")){
									pm = "SUBSCRIPTION";
								}
								else if(or.getPaymentStatus().equalsIgnoreCase("5")){
									pm = "PG";
								}
								else if(or.getPaymentStatus().equalsIgnoreCase("6")){
									pm = "COD";
								}
								else if(or.getPaymentStatus().equalsIgnoreCase("11")){
									pm = "PG";
								}else {
									pm=or.getPaymentStatus();
								}
								}	
						float bill = check.getBill();
						if(check.getKitchenScreenId()==ffc.getId()){
							if (check.getStatus() != Status.Paid || (check.getStatus()==Status.Paid && check.getOrders().get(0).getStatus()!=com.cookedspecially.enums.order.Status.DELIVERED)) {
									continue;
							}
							if(pm.toString().equalsIgnoreCase(pt.getName())){
								TaxType   taxo  =  new TaxType();
								if(check.getDiscount_Charge().size()>0){
									for(Order_DCList dc : check.getDiscount_Charge()){
										bill -= taxo.getTaxCharge(bill,dc.getType(),dc.value);
									}
								}
								JSONObject obj = null;
								double billWithTaxes =0;
								if(check.getTaxJsonObject()!=null){
									float sumTax = 0.0f;
								for(TaxType taxlists : taxType){
									try {
										obj = new JSONObject(check.getTaxJsonObject());
										if(check.getBill()!=0){
										if(obj.has(taxlists.getTaxTypeId().toString())){
										float s  =  Float.parseFloat(obj.get(taxlists.getTaxTypeId().toString()).toString());
										taxCount+=s;
										count++;
											}
										}
										}
										catch (JSONException e) {
											e.printStackTrace();
											continue;
									}	
								}
								}
								float billAmount = Math.round((bill +taxCount +check.getOutCircleDeliveryCharges())+check.getCreditBalance());
				        	    sourceTypeTotal.put(pt.getName()+"-"+mapName,sourceTypeTotal.get(pt.getName()+"-"+mapName)+billAmount);
				        	    FfcFinalSourceTotal.put(pt.getName()+"-"+mapName,FfcFinalSourceTotal.get(pt.getName()+"-"+mapName)+billAmount);
							}
					}
				}
				}
				}
				}
				double totalOfSubTotal = 0;
				for(String st :orderSourceList){
					cellNo = 1;
					double subTotal=0;
					 for(PaymentType pt : paymentType){
						 subTotal+=sourceTypeTotal.get(pt.getName()+"-"+st);
					}
					 totalOfSubTotal+=subTotal;
				}
				subFfcTotal.put("ffcTotal",subFfcTotal.get("ffcTotal")+totalOfSubTotal);
				grandTotal .put("grandTotal",grandTotal .get("grandTotal")+totalOfSubTotal);
				cellNo = 1;
				cell = row.createCell(cellNo++);
				SimpleDateFormat sdfa = new SimpleDateFormat("MM-dd-yyyy");
		        sdfa.setTimeZone(TimeZone.getTimeZone(rest.getTimeZone()));
		        String dayDate = sdfa.format(date);
				cell.setCellValue(dayDate);
				cell.setCellStyle(style);
				for(PaymentType pt : paymentType){
					double sum =0;
					for(String st :orderSourceList){
						sum+=sourceTypeTotal.get(pt.getName()+"-"+st);
					}
					subFfcTotal.put(pt.getName(),subFfcTotal.get(pt.getName())+sum);
					cell = row.createCell(cellNo++);
					cell.setCellValue(sum);
				}
				cell = row.createCell(cellNo++);
				cell.setCellValue(subFfcTotal.get("ffcTotal"));
				cell.setCellStyle(style);
				for(PaymentType pt : paymentType){
					paymentTypeTotal.put(pt.getName(),0.0);
				}
				for(com.cookedspecially.domain.OrderSource os : orderSource){
					sourceTypeTotal.put(os.getName(),0.0);
				}
				for(OrderSource os : orderSourceBase){
					sourceTypeTotal.put(os.toString(),0.0);
				}
				subFfcTotal.put("ffcTotal",0.0);
			}
		}	
		row = sheet.createRow(rowNum++);
		cellNo = 0;
		
		cell = row.createCell(cellNo++);
		cell.setCellValue(rest.getRestaurantName());
		cell.setCellStyle(style);
		row = sheet.createRow(rowNum++);
		for(String st :orderSourceList){
			cellNo = 1;
			double subTotal=0;
			 for(PaymentType pt : paymentType){
				 subTotal+=FfcFinalSourceTotal.get(pt.getName()+"-"+st);
			}
		}
		cellNo = 1;
		cell = row.createCell(cellNo++);
		cell.setCellValue("Total");
		cell.setCellStyle(style);
		for(PaymentType pt : paymentType){
			double sum =0;
			for(String st :orderSourceList){
				sum+=FfcFinalSourceTotal.get(pt.getName()+"-"+st);
			}
			grandTotal.put(pt.getName(),grandTotal.get(pt.getName())+sum);
			cell = row.createCell(cellNo++);
			cell.setCellValue(sum);
			cell.setCellStyle(style);
		}
		cell = row.createCell(cellNo++);
		cell.setCellValue(grandTotal.get("grandTotal"));
		cell.setCellStyle(style);
		
		for (int i = 0; i < cellNo; i++) {
			sheet.autoSizeColumn(i);
		}
	}

	private void createDailySalesSummaryReportNew(Sheet sheet, Map<String,Object> reportDataMap, int startRowNum, List<FulfillmentCenter> kitchenScreen,List<TaxType> taxType,Workbook workbook) {
		int rowNum = startRowNum;
		float basePaymentTotal = 0;
		int backUpRow;
		int kitchenRow = 0;
		HashSet<Integer> unDelivered =  new HashSet<Integer>();
		HashSet<Integer> delivered =  new HashSet<Integer>();
		HashSet<Integer> cancelled  =  new HashSet<Integer>();
		Font defaultFont= workbook.createFont();
		defaultFont.setFontHeightInPoints((short)10);
		defaultFont.setFontName(HSSFFont.FONT_ARIAL);
		defaultFont.setColor(HSSFColor.BLACK.index);
		defaultFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		defaultFont.setItalic(true);
		
		CellStyle style=null;
	    style=workbook.createCellStyle();
	    style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
	    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
	    style.setAlignment(CellStyle.ALIGN_RIGHT);
	    style.setWrapText(true);
	    style.setFont(defaultFont);
	    
	    style.setBorderBottom((short)1);
	    style.setBorderLeft((short)1);
	    style.setBorderTop((short)1);
		style.setBorderRight((short)1);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		
		List<Check> checks = (List<Check>)reportDataMap.get("data");
		List<PaymentType> paymentType = (List<PaymentType>)reportDataMap.get("paymentType");
		List<FulfillmentCenter> ffcL =  (List<FulfillmentCenter>)reportDataMap.get("kitchenScreens");
		List<com.cookedspecially.domain.OrderSource> orderSource  = (List<com.cookedspecially.domain.OrderSource>)reportDataMap.get("orderSource"); 
		BasePaymentType[]  bpt =  (BasePaymentType[]) reportDataMap.get("basePaymentType");
		OrderSource[] orderSourceBase = (OrderSource[]) reportDataMap.get("orderSourceBase");
		
		int cellNo=0;
		Map<String, Double> paymentTypeTotal= new HashMap<String, Double>();
		Map<String, Double> sourceTypeTotal= new HashMap<String, Double>();
		Map<String, Double> sourceTypeCreditTotal= new HashMap<String, Double>();
		Set<String> orderSourceList = new HashSet<String>();
		HashMap<String,Double> subFfcTotal =  new LinkedHashMap<String, Double>();
		HashMap<String,Double> subFfcCreditTotal =  new LinkedHashMap<String, Double>();
		HashMap<String,Double> grandTotal =  new LinkedHashMap<String, Double>();
		HashMap<String,Double> grandTotalCredit =  new LinkedHashMap<String, Double>();
		Map<String, Double> FfcFinalSourceTotal= new HashMap<String, Double>();
		Map<String, Double> FfcFinalSourceCreditTotal= new HashMap<String, Double>();	
		Map<String, Double> sourceFinalTotal= new HashMap<String, Double>();
		float[] taxTotal = new float[50];
		int count=0;
		subFfcTotal.put("ffcTotal",0.0);
		subFfcCreditTotal.put("ffcCreditTotal", 0.0);
		grandTotal.put("grandTotal", 0.0);
		grandTotalCredit.put("grandTotalCredit", 0.0);
		for(PaymentType pt : paymentType){
			paymentTypeTotal.put(pt.getName(),0.0);
			subFfcTotal.put(pt.getName(),0.0);
			grandTotal.put(pt.getName(),0.0);
			grandTotalCredit.put(pt.getName(),0.0);
			subFfcCreditTotal.put(pt.getName(),0.0);
		}
		for(PaymentType pt : paymentType){
		for(com.cookedspecially.domain.OrderSource os : orderSource){
			FfcFinalSourceTotal.put(pt.getName()+"-"+os.getName(),0.0);
			FfcFinalSourceCreditTotal.put(pt.getName()+"-"+os.getName(),0.0);
		}
		for(OrderSource os : orderSourceBase){
			FfcFinalSourceTotal.put(pt.getName()+"-"+os.toString(),0.0);
			FfcFinalSourceCreditTotal.put(pt.getName()+"-"+os.toString(),0.0);
		}}
		
		Row row = sheet.createRow(rowNum++);
		String pm = null;
		Cell cell = row.createCell(cellNo);
		for(FulfillmentCenter ffc: ffcL){
			row = sheet.createRow(rowNum++);
			cellNo = 0;
			cell = row.createCell(cellNo++);
			cell.setCellValue(ffc.getName());
			cell.setCellStyle(style);
			row = sheet.createRow(rowNum++);
			row.createCell(cellNo++).setCellValue("");
			for(PaymentType pt : paymentType){
				row.createCell(cellNo++).setCellValue(pt.getName());
				for(com.cookedspecially.domain.OrderSource os : orderSource){
					sourceTypeTotal.put(pt.getName()+"-"+os.getName(),0.0);
					sourceTypeCreditTotal.put(pt.getName()+"-"+os.getName(),0.0);
					orderSourceList.add(os.getName());
				}
				for(OrderSource os : orderSourceBase){
					sourceTypeTotal.put(pt.getName()+"-"+os.toString(),0.0);
					sourceTypeCreditTotal.put(pt.getName()+"-"+os.toString(),0.0);
					orderSourceList.add(os.toString());
				}
				}
			    cell = row.createCell(cellNo++);
				cell.setCellValue("Total");
				cell.setCellStyle(style);
			    
				cellNo =1;
				float taxCount=0.0f;
				row = sheet.createRow(rowNum++);
				backUpRow = rowNum;
				for(PaymentType pt : paymentType){
					basePaymentTotal = 0.0f;
					
					for(String os : orderSourceList){
						for(Check check : checks) {
							taxCount=0;
							List<Order> order =  check.getOrders();
						
						String mapName;
						if(check.getOrderSource().equalsIgnoreCase("0")){
							mapName = "Website";
			        	}
			        	else if(check.getOrderSource().equalsIgnoreCase("1")){
			        		mapName = "POS";
			        	}
			        	else if(check.getOrderSource().equalsIgnoreCase("2")){
			        		mapName = "Android";
			        	}
			        	else if(check.getOrderSource().equalsIgnoreCase("3")){
			        		mapName = "IOS";
			        	}
			        	else if(check.getOrderSource().equalsIgnoreCase("4")){
			        		mapName = "Third_Party";
			        	}
			        	else {
			        		mapName= check.getOrderSource();
			        	}
						if(mapName.equalsIgnoreCase(os)){
							if(check.getBill()==0 || check.getStatus()== Status.Cancel || ("CANCELED".equalsIgnoreCase(check.getTransactionStatus()))){
								cancelled.add(check.getCheckId());
							}
							for(Order or: order){
								if(or.getStatus()!=com.cookedspecially.enums.order.Status.DELIVERED && or.getStatus()!=com.cookedspecially.enums.order.Status.CANCELLED){
									unDelivered.add(check.getCheckId());
								}else if((!"CANCELED".equalsIgnoreCase(check.getTransactionStatus())) && check.getBill()!=0 && check.getStatus()!= Status.Cancel && or.getStatus()==com.cookedspecially.enums.order.Status.DELIVERED){
									delivered.add(check.getCheckId());
								}
								
								if(or.getPaymentStatus().equalsIgnoreCase("0")){
									pm = "CREDIT";
								}
								if(or.getPaymentStatus().equalsIgnoreCase("1")){
									pm = "CASH";
								}
								if(or.getPaymentStatus().equalsIgnoreCase("4")){
									pm = "SUBSCRIPTION";
								}
								else if(or.getPaymentStatus().equalsIgnoreCase("5")){
									pm = "PG";
								}
								else if(or.getPaymentStatus().equalsIgnoreCase("6")){
									pm = "COD";
								}
								else if(or.getPaymentStatus().equalsIgnoreCase("11")){
									pm = "PG";
								}else {
									pm=or.getPaymentStatus();
								}
								
								}	
						float bill = check.getBill();
						
						if(check.getKitchenScreenId()==ffc.getId()){
							if (check.getStatus() != Status.Paid ||
								(check.getStatus()==Status.Paid && check.getOrders().size()>0?(check.getOrders().get(0).getStatus()!=com.cookedspecially.enums.order.Status.DELIVERED):true)) {
									continue;
							}
							if(pm.toString().equalsIgnoreCase(pt.getName())){
								TaxType   taxo  =  new TaxType();
								if(check.getDiscount_Charge().size()>0){
									for(Order_DCList dc : check.getDiscount_Charge()){
										bill -= taxo.getTaxCharge(bill,dc.getType(),dc.value);
									}
								}
								float couponDiscountAmount=0;
								if((check.getStatus()!=Status.Cancel && check.getBill()>0) && (!"CANCELED".equalsIgnoreCase(check.getTransactionStatus()))){
									if(check.getCoupon_Applied()!=null){
										if(check.getCoupon_Applied().size()>0){
											for(Coupon coupon : check.getCoupon_Applied()){
												coupon.getFlatRules().getIsAbsoluteDiscount();
														if(!coupon.getFlatRules().getIsAbsoluteDiscount()){
															double dicountValue = (bill*coupon.getFlatRules().getDiscountValue())/100;
															couponDiscountAmount+=dicountValue;
														}else{
															couponDiscountAmount+=coupon.getFlatRules().getDiscountValue();
														}
											}
										}
									}
									}
								bill -=couponDiscountAmount;
								JSONObject obj = null;
								double billWithTaxes =0;
								if(check.getTaxJsonObject()!=null){
									float sumTax = 0.0f; 
								for(TaxType taxlists : taxType){
									try {
										obj = new JSONObject(check.getTaxJsonObject());
										if(check.getBill()!=0){
										if(obj.has(taxlists.getTaxTypeId().toString())){
											float s  =  Float.parseFloat(obj.get(taxlists.getTaxTypeId().toString()).toString());
											taxCount+=s;
										//taxCount=sumTax;
										count++;
											}
										}}
										catch (JSONException e) {
										// TODO Auto-generated catch block
											e.printStackTrace();
											continue;
									}	
								}
								}
								
								///////////////////////////
								
								//bill-=couponDiscountAmount;
								float billAmount = Math.round((bill +taxCount +check.getOutCircleDeliveryCharges()));
								//bill AMOUNT :410.0tax :45.8237 OD 15.0 + CC
				        	    sourceTypeTotal.put(pt.getName()+"-"+mapName,sourceTypeTotal.get(pt.getName()+"-"+mapName)+billAmount);
				        	    sourceTypeCreditTotal.put(pt.getName()+"-"+mapName,sourceTypeCreditTotal.get(pt.getName()+"-"+mapName)+check.getCreditBalance());
				        	    FfcFinalSourceTotal.put(pt.getName()+"-"+mapName,FfcFinalSourceTotal.get(pt.getName()+"-"+mapName)+billAmount);
				        	    FfcFinalSourceCreditTotal.put(pt.getName()+"-"+mapName,FfcFinalSourceCreditTotal.get(pt.getName()+"-"+mapName)+check.getCreditBalance());
							}
							
					}
				}
				}
				}
				}
				double totalOfSubTotal = 0;
				double creditTotal=0;
				for(String st :orderSourceList){
					cellNo = 1;
					double subTotal=0;
					row.createCell(cellNo++).setCellValue(st);
					 for(PaymentType pt : paymentType){
						 row.createCell(cellNo++).setCellValue(sourceTypeTotal.get(pt.getName()+"-"+st));
						 subTotal+=sourceTypeTotal.get(pt.getName()+"-"+st);
					}
					 cell = row.createCell(cellNo++);
					 cell.setCellValue(subTotal);
					 cell.setCellStyle(style);
					 
					 totalOfSubTotal+=subTotal;
					 row = sheet.createRow(rowNum++);
				}
				subFfcTotal.put("ffcTotal",subFfcTotal.get("ffcTotal")+totalOfSubTotal);
				grandTotal.put("grandTotal",grandTotal .get("grandTotal")+totalOfSubTotal);
			
				
				cellNo = 1;
				cell = row.createCell(cellNo++);
				cell.setCellValue("Total");
				cell.setCellStyle(style);
				
				for(PaymentType pt : paymentType){
					double sum =0;
					for(String st :orderSourceList){
						sum+=sourceTypeTotal.get(pt.getName()+"-"+st);
					}
					subFfcTotal.put(pt.getName(),subFfcTotal.get(pt.getName())+sum);
					cell = row.createCell(cellNo++);
					cell.setCellValue(sum);
					cell.setCellStyle(style);
				}
				cell = row.createCell(cellNo++);
				cell.setCellValue(subFfcTotal.get("ffcTotal"));
				cell.setCellStyle(style);
				
				/* Adding credit total here  */ 
				
				row = sheet.createRow(rowNum++);
				cellNo = 1;
				cell = row.createCell(cellNo++);
				cell.setCellValue("Credit Total");
				cell.setCellStyle(style);
				double sumCredit=0.0;
				for(PaymentType pt : paymentType){
					double sum =0;
					for(String st :orderSourceList){
						sum+=sourceTypeCreditTotal.get(pt.getName()+"-"+st);
					}
					subFfcCreditTotal.put(pt.getName(),subFfcCreditTotal.get(pt.getName())+sum);
					cell = row.createCell(cellNo++);
					sumCredit+=sum;
					cell.setCellValue(sum);
					cell.setCellStyle(style);
				}
				cell = row.createCell(cellNo++);
				cell.setCellValue(sumCredit);
				cell.setCellStyle(style);
				
				/* Adding credit total Ends here  */ 
				
				/* Adding FFC final total here */
				row = sheet.createRow(rowNum++);
				cellNo = 1;
				cell = row.createCell(cellNo++);
				cell.setCellValue("Grand Total");
				cell.setCellStyle(style);
				sumCredit=0.0;
				for(PaymentType pt : paymentType){
					double sum =0;
					for(String st :orderSourceList){
						sum+=(sourceTypeTotal.get(pt.getName()+"-"+st)+sourceTypeCreditTotal.get(pt.getName()+"-"+st));
					}
					//subFfcTotal.put(pt.getName(),subFfcTotal.get(pt.getName())+sum);
					cell = row.createCell(cellNo++);
					cell.setCellValue(sum);
					cell.setCellStyle(style);
					sumCredit+=sum;
				}
				cell = row.createCell(cellNo++);
				cell.setCellValue(sumCredit);
				sumCredit=0;
				cell.setCellStyle(style);
				rowNum++;
				/* Adding final FFC total Ends here*/
				
				for(PaymentType pt : paymentType){
					paymentTypeTotal.put(pt.getName(),0.0);
				}
				for(com.cookedspecially.domain.OrderSource os : orderSource){
					sourceTypeTotal.put(os.getName(),0.0);
				}
				for(OrderSource os : orderSourceBase){
					sourceTypeTotal.put(os.toString(),0.0);
				}
				subFfcTotal.put("ffcTotal",0.0);
				subFfcCreditTotal.put("ffcCreditTotal",0.0);
	}
		
		// Time to create Total of all FFC(fulfillment Center)
		row = sheet.createRow(rowNum++);
		cellNo = 0;
		cell = row.createCell(cellNo++);
		cell.setCellValue("Final Result");
		cell.setCellStyle(style);
		rowNum++;
		row = sheet.createRow(rowNum++);
		cellNo=2;
		for(PaymentType pt : paymentType){
		row.createCell(cellNo++).setCellValue(pt.getName());
		}
		cell = row.createCell(cellNo++);
		cell.setCellValue("Total");
		cell.setCellStyle(style);
		row = sheet.createRow(rowNum++);
		for(String st :orderSourceList){
			cellNo = 1;
			double subTotal=0;
			row.createCell(cellNo++).setCellValue(st);
			 for(PaymentType pt : paymentType){
				 row.createCell(cellNo++).setCellValue(FfcFinalSourceTotal.get(pt.getName()+"-"+st));
				 subTotal+=FfcFinalSourceTotal.get(pt.getName()+"-"+st);
			}
			 cell = row.createCell(cellNo++);
			 cell.setCellValue(subTotal);
			 cell.setCellStyle(style);
			 row = sheet.createRow(rowNum++);
		}
		
		cellNo = 1;
		cell = row.createCell(cellNo++);
		cell.setCellValue("Total");
		cell.setCellStyle(style);
		for(PaymentType pt : paymentType){
			double sum =0;
			for(String st :orderSourceList){
				sum+=FfcFinalSourceTotal.get(pt.getName()+"-"+st);
			}
			grandTotal.put(pt.getName(),grandTotal.get(pt.getName())+sum);
			cell = row.createCell(cellNo++);
			cell.setCellValue(sum);
			cell.setCellStyle(style);
		}
		cell = row.createCell(cellNo++);
		cell.setCellValue(grandTotal.get("grandTotal"));
		cell.setCellStyle(style);
		
		/* Adding final total of customer credit*/
		row = sheet.createRow(rowNum++);
		cellNo = 1;
		cell = row.createCell(cellNo++);
		cell.setCellValue("Credit Total");
		cell.setCellStyle(style);
		double creditGrandTotal=0.0;
		for(PaymentType pt : paymentType){
			double sum =0;
			for(String st :orderSourceList){
				sum+=FfcFinalSourceCreditTotal.get(pt.getName()+"-"+st);
			}
			grandTotalCredit.put(pt.getName(),grandTotalCredit.get(pt.getName())+sum);
			creditGrandTotal+=sum;
			cell = row.createCell(cellNo++);
			cell.setCellValue(sum);
			cell.setCellStyle(style);
		}
		cell = row.createCell(cellNo++);
		cell.setCellValue(creditGrandTotal);
		cell.setCellStyle(style);
		/*Adding final total of customer credit Ends here*/
		
		/*Adding grand total of final total*/
		row = sheet.createRow(rowNum++);
		double sumGrandTotal =0.0;
		cellNo = 1;
		cell = row.createCell(cellNo++);
		cell.setCellValue("Grand Total");
		cell.setCellStyle(style);
		for(PaymentType pt : paymentType){
			double sum =0;
			for(String st :orderSourceList){
				sum+=(FfcFinalSourceTotal.get(pt.getName()+"-"+st)+FfcFinalSourceCreditTotal.get(pt.getName()+"-"+st));
			}
			cell = row.createCell(cellNo++);
			sumGrandTotal+=sum;
			cell.setCellValue(sum);
			cell.setCellStyle(style);
		}
		cell = row.createCell(cellNo++);
		cell.setCellValue(sumGrandTotal);
		cell.setCellStyle(style);
		sumGrandTotal=0.0;
		
		/*Adding grand total of final total Ends here*/
		
		for (int i = 0; i < cellNo; i++) {
			sheet.autoSizeColumn(i);
		}
		
		Row r = sheet.createRow((rowNum+2));
		cellNo =0;
		cell = r.createCell(cellNo++);
		cell.setCellValue("Delivered Orders Count.");
		cell.setCellStyle(style);
		r.createCell(cellNo++).setCellValue(delivered.size());
		
		Row rows = sheet.createRow((rowNum+3));
		cellNo =0;
		cell = rows.createCell(cellNo++);
		cell.setCellValue("Undelivered Orders Count.");
		cell.setCellStyle(style);
		rows.createCell(cellNo++).setCellValue(Math.abs(unDelivered.size()));
	
		Row ro = sheet.createRow((rowNum+4));
		cellNo =0;
		cell = ro.createCell(cellNo++);
		cell.setCellValue("Cancelled orders Count.");
		cell.setCellStyle(style);
		ro.createCell(cellNo++).setCellValue(cancelled.size());
		
		Row rl = sheet.createRow((rowNum+5));
		cellNo =0;
		cell = rl.createCell(cellNo++);
		cell.setCellValue("Total orders Count.");
		cell.setCellStyle(style);
		rl.createCell(cellNo++).setCellValue(delivered.size()+unDelivered.size()+cancelled.size());
		logger.info("Daily Sale Summary report has been generated");
	}
	class DishData {
		public int count;
		public double price;
	}
	
	private void createTopDishesReport(Sheet sheet, Map<String,Object> reportDataMap, int startRowNum, List<FulfillmentCenter> ffcL) {
		int rowNum = startRowNum;
		int cell = 0;
		List<Dish> dishes = (List<Dish>)reportDataMap.get("dishes");
		List<Check> checks = (List<Check>)reportDataMap.get("data");
		
		for(FulfillmentCenter ffc: ffcL){
		Row rows = sheet.createRow(rowNum++);
		Map<String, Dish> dishMap = new HashMap<String, Dish>();
		for(Dish dish : dishes) {
			if(dish.getDishSize().size()>0){
			for(Dish_Size size : dish.getDishSize()){
				dishMap.put(dish.getDishId()+","+(size.getName()==null?"":size.getName().replaceAll("\\s","")), dish);
			}
			}else{
				dishMap.put(dish.getDishId()+",", dish);
			}
		}
		for(Dish dish : dishes) {
			dishMap.put(dish.getDishId()+",",dish);
		}
		Map<String, DishData> dishDataMap = new HashMap<String, DishData>();
		for(Check check : checks) {
		if(check.getKitchenScreenId()==ffc.getId()){
			if (check.getStatus() != Status.Paid) {
				continue;
			}
			List<Order> orders = check.getOrders();
			for (Order order : orders) {
				if (order.getStatus() == com.cookedspecially.enums.order.Status.CANCELLED || check.getStatus() == Status.Cancel) {
					continue;
				} 
				List<OrderDish> items = order.getOrderDishes();
				for (OrderDish item: items) {
					if(dishDataMap.containsKey(item.getDishId()+","+(item.getDishSize()==null?"":item.getDishSize().replaceAll("\\s","")))) {
						DishData dishData = dishDataMap.get(item.getDishId()+","+(item.getDishSize()==null?"":item.getDishSize().replaceAll("\\s","")));
						dishData.count += item.getQuantity();
						dishData.price += (item.getPrice() * item.getQuantity());
					} else {
						DishData dishData = new DishData();
						dishData.count += item.getQuantity();
						dishData.price += ((double)item.getPrice() * item.getQuantity());
						dishDataMap.put(item.getDishId()+","+(item.getDishSize()==null?"":item.getDishSize().replaceAll("\\s","")), dishData);
					}
				}
			}
		}
		}
		
		rows.createCell(cell).setCellValue(ffc.getName());
		int cellNo=0;
		for(Entry<String, DishData> dishDataMapEntry : dishDataMap.entrySet()) {
			Row row = sheet.createRow(rowNum++);
			cellNo = 0;
			String dishId = dishDataMapEntry.getKey();
			String dishSize ="";
			int count=0;
			for(String val : dishId.split(",")){
				if(count>0){
				dishSize = val;
				}
				count++;
				}
			Dish dish = dishMap.remove(dishId);
			row.createCell(cellNo++).setCellValue("");
			if(dish!=null){
				row.createCell(cellNo++).setCellValue(dish.getDishId());
				row.createCell(cellNo++).setCellValue(dish.getName()+""+(dishSize!=""?"("+dishSize+")":""));
				row.createCell(cellNo++).setCellValue(dishDataMapEntry.getValue().count);
				row.createCell(cellNo++).setCellValue(dishDataMapEntry.getValue().price);
			}else{
				row.createCell(cellNo++).setCellValue("");
				row.createCell(cellNo++).setCellValue("UnKnown");
				row.createCell(cellNo++).setCellValue(dishDataMapEntry.getValue().count);
				row.createCell(cellNo++).setCellValue(dishDataMapEntry.getValue().price);
			}
			
		}
		for (int i = 0; i < cellNo; i++) {
			sheet.autoSizeColumn(i);
		}
		}
	}

	private void createCustomerReport(Sheet sheet, Map<String,Object> reportDataMap, int startRowNum) {
		int rowNum = startRowNum;
		List<Customer> customers = (List<Customer>)reportDataMap.get("data");
		int cellNo = 0;		
		for(Customer customer : customers) {
			
			if(customer.getFirstName()==null && customer.getLastName()==null){
				continue;
			}
			
			if("".equalsIgnoreCase(customer.getFirstName()) && "".equalsIgnoreCase(customer.getLastName())){
				continue;
			}
			Row row = sheet.createRow(rowNum++);
			cellNo = 0;
			row.createCell(cellNo++).setCellValue(customer.getCustomerId());
			if("".equalsIgnoreCase(customer.getFirstName()) || "null".equalsIgnoreCase(customer.getFirstName())){
				row.createCell(cellNo++).setCellValue(customer.getLastName());	
				row.createCell(cellNo++).setCellValue("");
			}
			else{
				row.createCell(cellNo++).setCellValue(customer.getFirstName());	
				row.createCell(cellNo++).setCellValue(customer.getLastName());
			}
			row.createCell(cellNo++).setCellValue(customer.getAddress());
			row.createCell(cellNo++).setCellValue(customer.getCity());
			row.createCell(cellNo++).setCellValue(customer.getPhone());
			row.createCell(cellNo++).setCellValue(customer.getEmail());
			row.createCell(cellNo++).setCellValue(customer.getCreatedTime().toLocaleString());
		}
		for (int i = 0; i < cellNo; i++) {
			sheet.autoSizeColumn(i);
		}
	}

	private void createDailyInvoiceReport(Sheet sheet, Map<String,Object> reportDataMap, int startRowNum, List<String> taxList, List<FulfillmentCenter>  kitchenScreen) {
		int rowNum = startRowNum;
		Set<String> dishTypes = (Set<String>)reportDataMap.get("dishTypes");
		List<Check> checks = (List<Check>)reportDataMap.get("data");
		List<PaymentType> paymentType = (List<PaymentType>)reportDataMap.get("paymentType");
		Collections.sort(checks, new Comparator<Check>() {
		    public int compare(Check v1,Check v2) {
		        return v1.getOpenTime().compareTo(v2.getOpenTime());
		    }
		});
		
		List<Customer> customerData= (List<Customer>)reportDataMap.get("customerData");
		//User user = (User)reportDataMap.get("user");
		Restaurant rest=(Restaurant)reportDataMap.get("user");
		String URL="https://www.cookedspecially.com/CookedSpecially/order/displayCheck?invoiceId=";
		DecimalFormat df = new DecimalFormat("###.##");
		int sno = 1;
		Double totalBill = 0.0;
		double totalBillWithTaxes = 0.0;
		double totalTax1 = 0.0;
		double totalTax2 = 0.0;
		double totalTax3 = 0.0;
		double deliveryChargesTotal=0.0;
		double cash=0.0;
		double credit=0.0;
		double paymentgatway=0.0;
		double room=0.0;
		double emc=0.0;
		double sub=0.0;
		double cod=0.0;
		double totalCreditAmount=0.0;
		double totalWithCreditAmount = 0.0;
		double discountAmountTotal = 0.0;
		double chargeAmountTotal = 0.0;
		double couponDiscountAmountTotal =0.0;
		float[] taxTotal = new float[50];
		int count=0;
		Map<String, Double> dishTypeTotalBillMap = new HashMap<String, Double>();
		//taxList.add("VAT");
		// Initialize
		for (String dishType : dishTypes) {
			dishTypeTotalBillMap.put(dishType, 0.0);
		}
		
		double[] pyCount =  new double[paymentType.size()];
		DateFormat formatter;
		formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone(rest.getTimeZone()));
		
		Workbook wb = new  HSSFWorkbook(); 
		
		CellStyle hlink_style = wb.createCellStyle(); 
		int cellNo = 0;
		for(Check check : checks) {
			//create the row data
			Row row = sheet.createRow(rowNum++);
			cellNo = 0;
			row.createCell(cellNo++).setCellValue(df.format(sno++));
			row.createCell(cellNo++).setCellValue(check.getCheckId());
			HSSFHyperlink link=new HSSFHyperlink(HSSFHyperlink.LINK_URL);
			link.setAddress(URL+check.getInvoiceId());			
			Cell cell = row.createCell(cellNo++);
			cell.setCellValue(check.getInvoiceId());
			row.createCell(cellNo++).setCellValue(check.getName());
			int id_check=0;
			for(Customer cust: customerData){
				if(check.getCustomerId().equals(cust.getCustomerId())){
					if(cust.getPhone()!=null){
					 row.createCell(cellNo++).setCellValue(cust.getPhone());
				}else{
					 row.createCell(cellNo++).setCellValue("NA");
				}
					if(cust.getEmail()!=null){
						row.createCell(cellNo++).setCellValue(cust.getEmail());
				     }else{
				    	row.createCell(cellNo++).setCellValue("NA");
				     }
					id_check++;
				}
			}
			if(id_check==0){
				row.createCell(cellNo++).setCellValue("NA");
			}
			cell.setHyperlink(link);
			if(check.getKitchenScreenId()!=0){
				for(FulfillmentCenter fc : kitchenScreen){
					if(check.getKitchenScreenId()==fc.getId()){
						row.createCell(cellNo++).setCellValue(fc.getName());
					}
				}
			}
			else {
				row.createCell(cellNo++).setCellValue("Default");
			}
            row.createCell(cellNo++).setCellValue(formatter.format(check.getOpenTime()));
            row.createCell(cellNo++).setCellValue(formatter.format(check.getDeliveryTime()));
			row.createCell(cellNo++).setCellValue(check.getDeliveryArea());
			
			if(check.getCheckType()!=null){
				    row.createCell(cellNo++).setCellValue(check.getCheckType().toString());
				}else{
					row.createCell(cellNo++).setCellValue("Delivery");
			}
			
			OrderSource ando = OrderSource.Android;
			Integer ANDROID= ando.ordinal();
			
			OrderSource ios = OrderSource.IOS;
			Integer IOS = ios.ordinal();
			
			OrderSource website = OrderSource.Website;
			Integer WEBSITE = website.ordinal();
			
			OrderSource pos = OrderSource.POS;
			Integer POS = pos.ordinal();
			
			OrderSource third_party = OrderSource.Third_Party;
			Integer THIRD_PARTY = third_party.ordinal();
			
			
			if(check.getOrderSource()!=null){
				if(ANDROID.toString().equals(check.getOrderSource()))
				 	row.createCell(cellNo++).setCellValue("Android");
				else if(IOS.toString().equals(check.getOrderSource()))
					 row.createCell(cellNo++).setCellValue("IOS");
				else if(POS.toString().equals(check.getOrderSource()))
		    	    row.createCell(cellNo++).setCellValue("POS");
				else if(WEBSITE.toString().equals(check.getOrderSource()))
		    	 	row.createCell(cellNo++).setCellValue("Website");
				else if(THIRD_PARTY.toString().equals(check.getOrderSource()))
					row.createCell(cellNo++).setCellValue("Third_Party");
				else 
		    	 	row.createCell(cellNo++).setCellValue(check.getOrderSource());
			}else{
					row.createCell(cellNo++).setCellValue("NA");
			}
			if((check.getStatus()!=Status.Cancel && check.getBill()>0) && (!"CANCELED".equalsIgnoreCase(check.getTransactionStatus()))){
				totalBill += (double)check.getBill();
				row.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(check.getBill())));
			}else{
				row.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(0)));
			}
			float chargeAmount =0.0f;
			float discAmount = 0.0f;
			float bill = check.getBill();
			
			if((check.getStatus()!=Status.Cancel && check.getBill()>0) && (!"CANCELED".equalsIgnoreCase(check.getTransactionStatus()))){
				if(check.getCoupon_Applied()!=null){
					double couponDiscountAmount=0;
					if(check.getCoupon_Applied().size()>0){
						for(Coupon coupon : check.getCoupon_Applied()){
							coupon.getFlatRules().getIsAbsoluteDiscount();
									if(!coupon.getFlatRules().getIsAbsoluteDiscount()){
										double dicountValue = (bill*coupon.getFlatRules().getDiscountValue())/100;
										couponDiscountAmountTotal+=dicountValue;
										couponDiscountAmount+=dicountValue;
									}else{
										couponDiscountAmountTotal+=coupon.getFlatRules().getDiscountValue();
									}
						}
					}
					bill-=couponDiscountAmount;
				    row.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(couponDiscountAmount)));
				}
				else {
					row.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(0)));
				}
				}else {
					row.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(0)));
				}
			
			if(check.getDiscount_Charge().size()>0){
				TaxType tax =  new TaxType();
				for(Order_DCList dc : check.getDiscount_Charge()){
					if(dc.getCategory()==AdditionalCategories.Charges){
						 chargeAmount += tax.getTaxCharge(bill,dc.getType(),dc.value);
						 bill+=chargeAmount;
					}
					else {
						discAmount += tax.getTaxCharge(bill,dc.getType(),dc.value);
						bill-=discAmount;
					}
				}
			}
			if((check.getStatus()!=Status.Cancel && check.getBill()>0) && (!"CANCELED".equalsIgnoreCase(check.getTransactionStatus()))){
				if(check.getDiscountAmount()>0){
					discountAmountTotal+=check.getDiscountAmount()+discAmount;
				    row.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(check.getDiscountAmount() + discAmount)));
				}
				else if(discAmount>0){
					discountAmountTotal+=discAmount;
				    row.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(discAmount)));
				}
				else {
					row.createCell(cellNo++).setCellValue("NA");
				}
			}else {
					row.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(0)));
			}
			
			JSONObject obj = null;
			double billWithTaxes =0;
			if(check.getTaxJsonObject()!=null){
				float sumTax = 0.0f;
				//taxList.add("VAT");
				
			for(String taxlists : taxList){
				try {
					obj = new JSONObject(check.getTaxJsonObject());
					if(check.getBill()!=0){
					if(obj.has(taxlists)){
						if(check.getBill()!=0 && check.getStatus()!=Status.Cancel && (!"CANCELED".equalsIgnoreCase(check.getTransactionStatus()))){
							row.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(obj.get(taxlists))));
							float s  =  Float.parseFloat(obj.get(taxlists).toString());
							sumTax+=s;
							taxTotal[count]+=s;
							count++;
						}
						else {
							row.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(0)));
						}
						}
						else{
							taxTotal[count]+=0;
							count++;
							row.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(0)));
						}
					}
					}
					catch (JSONException e) {
						e.printStackTrace();
						row.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(0)));
						continue;
				}	
			}
			count=0;
				if(check.getBill()!=0){
					if(check.getBill()!=0 && check.getStatus()!=Status.Cancel && (!"CANCELED".equalsIgnoreCase(check.getTransactionStatus()))){
					 row.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(check.getOutCircleDeliveryCharges())));
					 totalCreditAmount+=check.getCreditBalance();
					 if(check.getDiscountAmount()>0){
						 billWithTaxes = (((check.getBill()-check.getDiscountAmount())+sumTax+check.getOutCircleDeliveryCharges()));
					 }
					 else{
						 billWithTaxes = (check.getRoundOffTotal());
					 }
					}else {
						row.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(0)));
					}
					}
				else{
					row.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(0)));
				}
				}else{
			}
			
			if(check.getBill()!=0 && check.getStatus()!=Status.Cancel && (!"CANCELED".equalsIgnoreCase(check.getTransactionStatus()))){
					deliveryChargesTotal +=(double)check.getOutCircleDeliveryCharges();
				}
			else{
				deliveryChargesTotal +=0;
			}
			totalBillWithTaxes += billWithTaxes;
			if(check.getBill()!=0 && check.getStatus()!=Status.Cancel && (!"CANCELED".equalsIgnoreCase(check.getTransactionStatus()))){
				row.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(billWithTaxes)));
			}else{
				row.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(0)));
			}
			row.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(check.getCreditBalance())));
			row.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(billWithTaxes + check.getCreditBalance())));
			totalWithCreditAmount+=(billWithTaxes+check.getCreditBalance());

			Map<String, Double> dishTypeBillMap = getDishTypeBillMap(check);
			for (String dishType : dishTypes) {
				Double dishTypeBill = Double.valueOf((dishTypeBillMap.containsKey(dishType))?dishTypeBillMap.get(dishType):0);
				if(check.getStatus() != Status.Cancel){
					row.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(dishTypeBill)));
					dishTypeTotalBillMap.put(dishType, dishTypeTotalBillMap.get(dishType) + dishTypeBill);
				}else {
					row.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(0)));
				}
				
			}
			List<Order> orders = check.getOrders();
			int  checkId;
			int countOrder=0;
			for(Order order : orders){
 			checkId = order.getCheckId();
			if(checkId == check.getCheckId()){
					countOrder++;
			}
 			if (order.getStatus() == com.cookedspecially.enums.order.Status.CANCELLED || check.getStatus()== Status.Cancel) {
					continue;
			}
 			else if(countOrder>1){
				continue;
			}
 			int pyc=0;
 			 for(PaymentType py : paymentType){
 				 if(py!=null && order.getPaymentStatus()!=null){
 				 if(order.getPaymentStatus().equalsIgnoreCase(py.getName()) || py.getName().equalsIgnoreCase(check.getPaymentType())){
 					row.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(billWithTaxes +check.getCreditBalance())));
 					//cash+=billWithTaxes;
 					pyCount[pyc]+=(billWithTaxes+check.getCreditBalance());
 				 }else{
 				cellNo++;
 				 }
 				pyc++;
 				 }else{
 					row.createCell(cellNo++).setCellValue("");
 				 }
 			 }
 			row.createCell(cellNo++).setCellValue(order.getStatus().toString());
 			if(check.isFirstOrder()){
 				row.createCell(cellNo).setCellValue("NEW");
 			}else{
 				row.createCell(cellNo).setCellValue("OLD");
 			}
			//row.createCell(cellNo++).setCellValue(check.getPaymentDetail());
			}
			countOrder=0;
		}
		/// final line
		Row finalRow = sheet.createRow(rowNum++);
		cellNo = 0;
		finalRow.createCell(cellNo++).setCellValue("Total");
		finalRow.createCell(cellNo++).setCellValue("");
		finalRow.createCell(cellNo++).setCellValue("");
		finalRow.createCell(cellNo++).setCellValue("");
		finalRow.createCell(cellNo++).setCellValue("");
		finalRow.createCell(cellNo++).setCellValue("");
		finalRow.createCell(cellNo++).setCellValue("");
		finalRow.createCell(cellNo++).setCellValue("");
		finalRow.createCell(cellNo++).setCellValue("");
		finalRow.createCell(cellNo++).setCellValue("");
		finalRow.createCell(cellNo++).setCellValue("");
		finalRow.createCell(cellNo++).setCellValue("");
		finalRow.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(totalBill)));
		finalRow.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(couponDiscountAmountTotal)));
		finalRow.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(discountAmountTotal)));
		if(taxTotal!=null){
			int i=0;
		for(String tax : taxList) {
				finalRow.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(taxTotal[i])));
				i++;
		}
		}
		finalRow.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(deliveryChargesTotal)));
		finalRow.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(totalBillWithTaxes)));
		finalRow.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(totalCreditAmount)));
		finalRow.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(totalWithCreditAmount)));
		for (String dishType : dishTypes) {
			finalRow.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(dishTypeTotalBillMap.get(dishType))));
		}
		
		for (int i = 0; i < cellNo; i++) {
			sheet.autoSizeColumn(i);
		}
		for(double dd : pyCount){
			finalRow.createCell(cellNo++).setCellValue(Double.parseDouble(df.format(dd)));
		}
		logger.info("Daily Invoice report has been generated");
	}

	private void createDetailedInvoiceReport(Sheet sheet, Map<String,Object> reportDataMap, int startRowNum, List<String> taxList) {
		int rowNum = startRowNum;
		DecimalFormat df = new DecimalFormat("###.##");
		String URL="https://www.cookedspecially.com/CookedSpecially/order/displayCheck?invoiceId=";
		
		List<Check> checks = (List<Check>)reportDataMap.get("data");
		Restaurant rest=(Restaurant) reportDataMap.get("user");
		DateFormat formatter;
		formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone(rest.getTimeZone()));
		
		int cellNo = 0;
		for(Check check : checks) {
			Row row = sheet.createRow(rowNum++);
			cellNo = 0;

			HSSFHyperlink link=new HSSFHyperlink(HSSFHyperlink.LINK_URL);
			link.setAddress(URL+check.getInvoiceId());			
			double checkTotalTax = 0;
			Cell cell = row.createCell(cellNo++);
			cell.setCellValue(check.getInvoiceId());
			cell.setHyperlink(link);
			if(check.getPhone()!=null){
				row.createCell(cellNo++).setCellValue(check.getPhone());
				}
				else{
					row.createCell(cellNo++).setCellValue("");
			}
			if(check.getDeliveryTime()==null){
				row.createCell(cellNo++).setCellValue(formatter.format(check.getDeliveryTime()));
			}else{
				row.createCell(cellNo++).setCellValue(formatter.format(check.getCloseTime()));
			}
			//row.createCell(cellNo++).setCellValue(formatter.format(check.getCloseTime()));
			// "Delivery Address" , "Delivery Area"
			//row.createCell(cellNo++).setCellValue(check.getDeliveryAddress());
			//row.createCell(cellNo++).setCellValue(check.getDeliveryArea());

			//"Tax", "Total With taxes and Discount", "Sub Total without Tax and Discount", "Discount", 
			
			JSONObject obj = null;
			if(check.getTaxJsonObject()!=null){
				try {
					obj = new JSONObject(check.getTaxJsonObject());
					if(check.getBill()!=0){
					for(String taxlists : taxList){
					if(obj.has(taxlists)){
					float s  =  Float.parseFloat(obj.get(taxlists).toString());
					checkTotalTax+=s;
					}
					}
				}
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}}
			else{
				/*if(!StringUtility.isNullOrEmpty(check.getAdditionalChargesName1())) {
					checkTotalTax += (float)check.getAdditionalChargesValue1(); 
				}
				if(!StringUtility.isNullOrEmpty(check.getAdditionalChargesName2())) {
					checkTotalTax += (double)check.getAdditionalChargesValue2();
				}
				if(!StringUtility.isNullOrEmpty(check.getAdditionalChargesName3())) {
					checkTotalTax += (double)check.getAdditionalChargesValue3();
				}*/
			}
			float chargeAmount =0.0f;
			float discAmount = 0.0f;
			double discount = (double)(check.getDiscountAmount());
			float bill = check.getBill();
			if(check.getDiscount_Charge().size()>0){
				TaxType tax =  new TaxType();
				for(Order_DCList dc : check.getDiscount_Charge()){
					if(dc.getCategory()==AdditionalCategories.Charges){
						 chargeAmount += tax.getTaxCharge(bill,dc.getType(),dc.value);
						 bill+=chargeAmount;
					}
					else {
						discAmount += tax.getTaxCharge(bill,dc.getType(),dc.value);
						bill-=discAmount;
					}
				}
			}
			if(check.getDiscountAmount()>0){
			    discount =check.getDiscountAmount() + discAmount;
			}
			else if(discAmount>0){
				 discount = discAmount;
			}
			else if (discount == 0 && check.getDiscountPercent() > 0) {
				discount = check.getDiscountPercent() * check.getBill() / 100;
			}
			else {
				discount = 0;
			}
			/*if(check.getBill()!=0){
				row.createCell(cellNo++).setCellValue(df.format(checkTotalTax));
				row.createCell(cellNo++).setCellValue(df.format((double)(check.getBill()) + checkTotalTax - discount));
				row.createCell(cellNo++).setCellValue(check.getBill());			
				row.createCell(cellNo++).setCellValue(discount);
				}else{
					row.createCell(cellNo++).setCellValue(df.format(0));
					row.createCell(cellNo++).setCellValue(df.format(0));
					row.createCell(cellNo++).setCellValue(check.getBill());			
					row.createCell(cellNo++).setCellValue(df.format(discount));
				}*/
			
			// "Dish Name", "Total Dishes Cost", "Dish Quantity",
			HashMap<String, DishData> dishDataMap = new HashMap<String, DishData>();
			List<Order> orders = check.getOrders();

			for(Order order : orders) {
				if (order.getStatus() == com.cookedspecially.enums.order.Status.CANCELLED || check.getStatus()==Status.Cancel) {
					continue;
				}
				
				List<OrderDish> items = order.getOrderDishes();
				for(OrderDish item : items) {
					
					if(!dishDataMap.containsKey(item.getName())) {
						dishDataMap.put(item.getName(), new DishData());
					} 
					DishData dishData = dishDataMap.get(item.getName());
					dishData.count += item.getQuantity();
					dishData.price += item.getQuantity() * item.getPrice();
				}
				
			}
			
			if(dishDataMap.size() == 0) {
				row.createCell(cellNo++).setCellValue("Cancelled Check");
				row.createCell(cellNo++).setCellValue(0);
				row.createCell(cellNo++).setCellValue(0);
				
			} else {
				boolean firstEntry = true;
				for(Entry<String, DishData> dishDataMapEntry : dishDataMap.entrySet()) {
					if(!firstEntry) {
						row = sheet.createRow(rowNum++);
						cellNo = 0;
						row.createCell(cellNo++).setCellValue(check.getInvoiceId());
						row.createCell(cellNo++).setCellValue("");
						//row.createCell(cellNo++).setCellValue("");
						//row.createCell(cellNo++).setCellValue("");
						row.createCell(cellNo++).setCellValue("");
						//row.createCell(cellNo++).setCellValue(0);
						//row.createCell(cellNo++).setCellValue(0);
						//row.createCell(cellNo++).setCellValue(0);
						//row.createCell(cellNo++).setCellValue(0);
					}
					row.createCell(cellNo++).setCellValue(dishDataMapEntry.getKey());
					row.createCell(cellNo++).setCellValue(df.format(dishDataMapEntry.getValue().price));
					row.createCell(cellNo++).setCellValue(dishDataMapEntry.getValue().count);
					firstEntry = false;
				}	
			}
		}
				
		for (int i = 0; i < cellNo; i++) {
			sheet.autoSizeColumn(i);
		}
	}

	private void createCheckReport(Sheet sheet, Map<String,Object> reportDataMap, int startRowNum) {
		int rowNum = startRowNum;
		List<Check> checks = (List<Check>)reportDataMap.get("checks");
		for (Check check : checks) {
			//create the row data
			Row row = sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(check.getCheckId());
			row.createCell(1).setCellValue(check.getBill());
		}
	}
	
	private void createCustomerSummery(Sheet sheet, Map<String,Object> reportDataMap, int startRowNum) {
		int rowNum = startRowNum;
		List<Customer> customers = (List<Customer>)reportDataMap.get("data");
		HashMap<Integer,List<Check>> orderHist = (HashMap<Integer,List<Check>>)reportDataMap.get("custSummery");
		int cellNo = 0;		
		for(Customer customer : customers) {
			
			if(customer.getFirstName()==null && customer.getLastName()==null){
				continue;
			}
			if("".equalsIgnoreCase(customer.getFirstName()) && "".equalsIgnoreCase(customer.getLastName())){
				continue;
			}
			
			Row row = sheet.createRow(rowNum++);
			cellNo = 0;
			row.createCell(cellNo++).setCellValue(customer.getCustomerId());
			row.createCell(cellNo++).setCellValue(customer.getPhone());
			List<Check> checkList = orderHist.get(customer.getCustomerId());
			int count=0;
			float amount =0.0f;
			for(Check check : checkList){
				if(check.getBill()==0 || check.getStatus()==Status.Cancel ){
					continue;
				}
				amount+=check.getRoundOffTotal();
				if(count >200){
					row = sheet.createRow(rowNum++);
					cellNo = 3;
					row.createCell(cellNo++).setCellValue(check.getOpenTime().toLocaleString());
					count=0;
				}else{
				row.createCell(cellNo++).setCellValue(check.getOpenTime().toLocaleString());
				}
				count++;
			}
			row.createCell(cellNo++).setCellValue(amount);
		}
		for (int i = 0; i < cellNo; i++) {
			sheet.autoSizeColumn(i);
		}
	}
	
	private void createTransactionSummery(Sheet sheet, Map<String,Object> reportDataMap, int startRowNum,Workbook workbook) throws ParseException {
		int rowNum = startRowNum;
		Font defaultFont= workbook.createFont();
		defaultFont.setFontHeightInPoints((short)10);
		defaultFont.setFontName(HSSFFont.FONT_ARIAL);
		defaultFont.setColor(HSSFColor.BLACK.index);
		defaultFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		defaultFont.setItalic(true);
		
		CellStyle style=null;
	    style=workbook.createCellStyle();
	    style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
	    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
	    style.setAlignment(CellStyle.ALIGN_RIGHT);
	    style.setWrapText(true);
	    style.setFont(defaultFont);
	    
	    style.setBorderBottom((short)1);
	    style.setBorderLeft((short)1);
	    style.setBorderTop((short)1);
		style.setBorderRight((short)1);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		int cellNo=0;
		Row row = sheet.createRow(rowNum++);
		String pm = null;
		Cell cell = row.createCell(cellNo);
		Restaurant rest =  (Restaurant)reportDataMap.get("restaurant");
		List<FulfillmentCenter> ffc  = (List<FulfillmentCenter>) reportDataMap.get("kitchenScreens");
		List<String> transactionType =  (List<String>)reportDataMap.get("transactionType");
		List<User> listUser  = (List<User>)reportDataMap.get("listUser");
		SortedMap<Date,SortedMap<Integer,List<List<Transaction>>>> transactionList =  (SortedMap<Date, SortedMap<Integer, List<List<Transaction>>>>) reportDataMap.get("transactionList");
		 row = sheet.createRow(rowNum++);
		 pm = null;
		 cellNo=2;
		 cell = row.createCell(cellNo++);
		 cell.setCellValue("User Id");
		 cell.setCellStyle(style);
		 for(String tranType : transactionType){
			 	cell = row.createCell(cellNo++);
				cell.setCellValue(tranType);
				cell.setCellStyle(style);
		 }
		    cell = row.createCell(cellNo++);
			cell.setCellValue("Remarks");
			cell.setCellStyle(style);
		 for(FulfillmentCenter fc  : ffc){
			 row  = sheet.createRow(rowNum++);
			 cellNo=0;
			 cell = row.createCell(cellNo++);
			 cell.setCellValue(fc.getName());
			 cell.setCellStyle(style);
		 for(Date date :transactionList.keySet()){
			 SortedMap<Integer,List<List<Transaction>>> data1 = transactionList.get(date);
			 for(Integer key : data1.keySet()){
				 if(key==fc.getId()){
				 row  = sheet.createRow(rowNum++);
				 SimpleDateFormat sdfa = new SimpleDateFormat("MM-dd-yyyy");
			     sdfa.setTimeZone(TimeZone.getTimeZone(rest.getTimeZone()));
			     String dayDate = sdfa.format(date);
				 cellNo=1;
				 cell = row.createCell(cellNo++);
				 cell.setCellValue(dayDate);
				 cell.setCellStyle(style);
				 List<List<Transaction>> firstIter  = data1.get(key);
				 for(List<Transaction> secondIter  :firstIter){
					 List<Transaction> requiredTransactionList =  new ArrayList<Transaction>();
					 for(Transaction thirdIter : secondIter){
						 for(String st  : transactionType){
							 if(st.equalsIgnoreCase(thirdIter.getTransactionType())){
								 requiredTransactionList.add(thirdIter);
							 }
						 }
					 }
						 for(Transaction tran : requiredTransactionList){
						 row  = sheet.createRow(rowNum++);
						 cellNo=1;
						 
						 SimpleDateFormat sdfa1 = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a");
					     	sdfa.setTimeZone(TimeZone.getTimeZone("GMT"));
					     	String dayDate1 = sdfa1.format(tran.getTransactionTime());
						    
					     	DateFormat istFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a");
						    DateFormat gmtFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a");
						    TimeZone gmtTime = TimeZone.getTimeZone("GMT");
						    TimeZone istTime = TimeZone.getTimeZone(rest.getTimeZone());
						    istFormat.setTimeZone(gmtTime);
						    gmtFormat.setTimeZone(istTime);
						    
						    Date transactionDate  = istFormat.parse(dayDate1);
					     row.createCell(cellNo++).setCellValue(gmtFormat.format(transactionDate));
					     User transaUser=null;
					     for(User user :  listUser){
					    	 if(user.getUserId()==tran.getUserId()){
					    		 transaUser =user;
					    		 break;
					    	 }
					     }
					     if(transaUser!=null){
					    	 row.createCell(cellNo++).setCellValue(transaUser.getUserName());
					     }else {
					    	 row.createCell(cellNo++).setCellValue("");
					     }
					     boolean printRemark=false;
						 for(String st  : transactionType){
							 if(st.equalsIgnoreCase(tran.getTransactionType())){
								 row.createCell(cellNo++).setCellValue(tran.getTransactionAmount());
								 printRemark=true;
							 }else{
								 row.createCell(cellNo++).setCellValue("");
							 }
						 }
						 if(printRemark){
						 row.createCell(cellNo++).setCellValue(tran.getRemarks());
					 }
					}
					}
			 }
		 }
		 }
		 }
		 for (int i = 0; i < cellNo; i++) {
				sheet.autoSizeColumn(i);
			}
	}
}
