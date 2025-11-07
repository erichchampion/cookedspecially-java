/**
 * 
 */
package com.cookedspecially.domain;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cookedspecially.controller.RestaurantController;
import com.cookedspecially.dto.DishCouponCalcDTO;
import com.cookedspecially.enums.check.AdditionalCategories;
import com.cookedspecially.enums.check.CheckType;
import com.cookedspecially.enums.check.PaymentMode;
import com.cookedspecially.enums.check.Status;
import com.cookedspecially.enums.restaurant.ChargesType;
import com.cookedspecially.service.TaxTypeService;

/**
 * @author shashank, rahul
 *
 */
public class CheckResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    
	DecimalFormat df = new DecimalFormat("#.##");
	final static Logger logger = Logger.getLogger(RestaurantController.class);
	private Integer id;
	private Integer tableId;
	private Integer customerId;
	private Integer guests;
	private Status status;
	private CheckType checkType = CheckType.Delivery;
	private float amount;
	private float amountAfterDiscountCharges;
	private float amountAfterDiscount;
	private String name;
	private double total;
	private double roundedOffTotal;
	private String deliveryTime;
	private String deliveryArea;
	private float outCircleDeliveryCharges;
	private String deliverAddress;
	private String invoiceId;
	private float discountPercent;
	private float discountAmount;
	private Integer orderId;
    private	String deliveryDateTime;
    private String paymentMode;
    private List<TaxType> taxTypes;
	private List<CheckDishResponse> items;
	private List<Order_DCList> dc_List;
	private SortedMap<String,Float> taxDetails = new TreeMap<String,Float>();
	private SortedMap<String,Float> taxPool =  new TreeMap<String,Float>();
	private SortedMap<String,Float> discountDetails = new TreeMap<String,Float>();
	private SortedMap<String,Float>  chargeDetails  = new TreeMap<String, Float>();
//	private SortedMap<String,Double> couponApplied =  new TreeMap<String,Double>();
	private Map<Integer,DishCouponCalcDTO> couponCal = new HashMap<Integer,DishCouponCalcDTO>();
	private double totalCouponAmount;
	private ChargesType specialType;
	private float specialValue;
	private String[] taxNames;
	private Float[] taxValue ;
	private float[] enterVal;
	private String deliveryInst;
	private double amountAfterCharge;
	private double amountSaved;
	private float waiveOffCharges;
	private int discountflag;
	private String phone;
	private float additionalCharge3;
	private float additionalCharge2;
	private float additionalCharge1;
	private String additionalChargeName3;
	private String additionalChargeName2;
	private String additionalChargeName1;
	private String orderSource;
	private float checkCreditBalance;
	private float lastInvoiceAmount;
	private List<Coupon> couponAppliedList;
	private boolean toShowOldVat_ServiceTax = false;
	
	@Autowired
	public CheckResponse(Check check, TaxTypeService taxTypeService, Float waiveOffCharge, Restaurant rest) {
		taxNames = new String[50];
		taxValue = new Float[50];
		enterVal = new float[50];
		if(waiveOffCharge!=null){
			this.waiveOffCharges = waiveOffCharge;
			this.outCircleDeliveryCharges = waiveOffCharge;
		}
		else {
			this.setOutCircleDeliveryCharges(check.getOutCircleDeliveryCharges());
		}
		this.couponAppliedList = check.getCoupon_Applied();
		this.checkCreditBalance=check.getCreditBalance();
		this.deliveryInst = check.getDeliveryInst();
		this.taxTypes =	taxTypeService.listTaxTypesByRestaurantId(check.getRestaurantId());
		this.id = check.getCheckId();
		this.tableId = check.getTableId();
		this.customerId = check.getCustomerId();
		this.guests = check.getGuests();
		this.status = check.getStatus();
		this.orderSource = check.getOrderSource();
		if(check.getCheckType()!=null){
			this.checkType = check.getCheckType();
		}
		this.amount = check.getBill();
		this.discountAmount = check.getDiscountAmount();
		this.discountPercent = check.getDiscountPercent();
		this.amountAfterDiscount = this.amount - this.discountAmount;
		this.phone=check.getPhone();
		String format ="yyyy-MM-dd HH:mm";
		SimpleDateFormat sdf =  new SimpleDateFormat(format);
		sdf.setTimeZone(TimeZone.getTimeZone(rest.getTimeZone()));
		if(check.getDeliveryTime()!=null){
			this.deliveryDateTime=sdf.format(check.getDeliveryTime());
		}
		this.deliveryArea = check.getDeliveryArea();
		this.deliverAddress = check.getDeliveryAddress();
		this.name = check.getName();
		this.dc_List =  check.getDiscount_Charge();
		this.invoiceId = check.getInvoiceId();
		this.orderId=check.getOrderId();
		this.items = new ArrayList<CheckDishResponse>();
		this.additionalCharge1 = check.getAdditionalChargesValue1();
		this.additionalCharge2 = check.getAdditionalChargesValue2();
		this.additionalCharge3 = check.getAdditionalChargesValue3();
		this.additionalChargeName1 = check.getAdditionalChargesName1();
		this.additionalChargeName2 = check.getAdditionalChargesName2();
		this.additionalChargeName3 = check.getAdditionalChargesName3();
		this.lastInvoiceAmount = check.getLastInvoiceAmount();
		List<OrderDish> orderDishes;
		List<Order> orders = check.getOrders();
		int count=0;
		for(int i =0;i<taxValue.length;i++){
			this.taxValue[i] =0.0f;
		}
		for(TaxType tt : taxTypes){
			taxDetails.put(tt.getName(),0.0f);
		}
		
		float finalValue=0.0f;
		int counter=0;
		
		double chargeAmount =0.0f;
		double  discAmount = 0.0f;
		double billValue = check.getBill();
		this.amountAfterDiscountCharges =check.getBill();
		for(Order_DCList dc : check.getDiscount_Charge()){
			if(dc.getName()==null){
				dc.setName("Discount");
			}
			discountDetails.put(dc.getName(),0.0f);
		}
		boolean countDeliveryAreaTAX=true;
		if (orders != null){
			for(Order order : orders) {
				if (order.getStatus() == com.cookedspecially.enums.order.Status.CANCELLED) {
					continue;
				}
				orderDishes= order.getOrderDishes();

				if (orderDishes != null) {
					for (OrderDish orderDish : orderDishes) {
						for ( int i = 0;  i < orderDish.getQuantity(); i++) {
							this.items.add(new CheckDishResponse(orderDish));
						}
						double dishPrice = orderDish.getPrice();
						if(check.getCoupon_Applied().size()>0){
							dishPrice = getAmountAfterCouponDiscount(orderDish,check.getCoupon_Applied(),order.getBill());
						}
						double orderD =dishPrice*orderDish.getQuantity();
						if(check.getDiscount_Charge().size()>0){
							TaxType tax =  new TaxType();
							for(Order_DCList dc : check.getDiscount_Charge()){
								if(dc.getCategory()==AdditionalCategories.Charges){
									 chargeAmount = tax.getTaxCharge(billValue,dc.getType(),dc.value);
									 billValue +=chargeAmount;
									 this.amountAfterCharge = billValue;
									 this.total+=chargeAmount;
									 chargeDetails.put(dc.getName(),(float)((float)Math.round(chargeAmount *100.0) / 100.0));
								}else {
									if(dc.getType()==ChargesType.ABSOLUTE){
										float val = (dc.value/check.getBill())*100;
										discAmount = tax.getTaxCharge(orderD,ChargesType.PERCENTAGE,val);
										
									}else{
									    discAmount = tax.getTaxCharge(orderD,dc.getType(),dc.value);
									  }
									  orderD = orderD-discAmount;
									  this.amountAfterDiscountCharges -=discAmount;
									  this.total = this.amountAfterDiscountCharges;
									  discountDetails.put(dc.getName(),discountDetails.get(dc.getName())+((float) ((float)Math.round(discAmount *100.0) / 100.0)));
								}
							}
						}
						
						finalValue=0;
						for(TaxType tT : taxTypes){
							String taxName=null;
							if(tT.getDishType().equalsIgnoreCase("Default")){
								for(TaxType compareTax : taxTypes){
									if(orderDish.getDishType().equalsIgnoreCase(compareTax.getDishType()))	{
										if(compareTax.getOverridden()!=null){
											if(tT.getTaxTypeId()==compareTax.getOverridden()){
												counter++;
												specialType=compareTax.getChargeType();
												specialValue=compareTax.getTaxValue();
												taxName=compareTax.getName();
												count++;
										}
									}
								}
								else{
									continue;
								}
								}
								if(counter!=1){
									float addOnPriceSum = 0.0f;	
									if(!(orderDish.getDishType().equalsIgnoreCase(tT.getDishType()))){
										this.taxNames[count]=tT.getName();
										if(orderDish.getOrderAddOn().size()>0){
											for(OrderAddOn addOn :orderDish.getOrderAddOn()){
												addOnPriceSum+=addOn.getPrice()*addOn.getQuantity();
											}
										}
										if(countDeliveryAreaTAX){
											orderD = orderD+check.getOutCircleDeliveryCharges();
											countDeliveryAreaTAX=false;
										}
										enterVal[count]  += (tT.getTaxCharge(orderD+addOnPriceSum,tT.getChargeType(),tT.getTaxValue()));
										this.taxValue[count] += enterVal[count];
										count++;
										
							}
						}
						else if(counter==1){
							float taxOnDelivery=0.0f;
							if(countDeliveryAreaTAX){
								countDeliveryAreaTAX=false;
								this.taxNames[count]=tT.getName();
								enterVal[count] +=  (float) tT.getTaxCharge(check.getOutCircleDeliveryCharges(),tT.getChargeType(),tT.getTaxValue());
								this.taxValue[count] += enterVal[count];
								count++;
							}
							this.taxNames[count]=taxName;
							enterVal[count]   += (tT.getTaxCharge(orderD,specialType,specialValue));
							this.taxValue[count] += enterVal[count];
							count++;
						}
						}
						counter=0;
						}
						//count=0;
					}
					
				}
				for(int i =0;i<taxNames.length;i++){
					if(taxValue[i]!=null && taxNames[i]!=null){
						if(taxValue[i]!=0.00f){
						taxDetails.put(taxNames[i],taxDetails.get(taxNames[i])+taxValue[i]);
						}
					    finalValue+=taxValue[i];
					}
					else if(taxValue[i]==null){
						continue;
					}
				}
				Iterator<Map.Entry<String,Float>> iter = taxDetails.entrySet().iterator();
				while (iter.hasNext()) {
				    Map.Entry<String,Float> entry = iter.next();
				    if(entry.getValue()==0.0f){
				        iter.remove();
				    }
				}
				
				String transactionStatus = check.getTransactionStatus();
				if(transactionStatus!=null){
					if(check.getTransactionStatus().equalsIgnoreCase("TXN_SUCCESS")|| check.getTransactionStatus().equalsIgnoreCase("SUCCESS")){
						if(order.getPaymentStatus()!=null){
							this.paymentMode = order.getPaymentStatus();
						}else{
							this.paymentMode = "PG";
						}
					}else if(check.getTransactionStatus().equalsIgnoreCase("CANCELED")){
						this.paymentMode = "CANCELED";
					}
					else if(order.getPaymentStatus()!=null){
						this.paymentMode = order.getPaymentStatus();
					}else if(order.getPaymentStatus()!=null) {
						this.paymentMode = order.getPaymentStatus();
					}else{
						this.paymentMode = "PG";
					}
				}
				else if(order.getPaymentStatus().equalsIgnoreCase(PaymentMode.PG.toString())){
					this.paymentMode = "PG";
				}
				else if(order.getPaymentStatus().equalsIgnoreCase(PaymentMode.PG_PENDING.toString())){
					this.paymentMode = "PG_PENDING";
				}
				else if(order.getPaymentStatus().equalsIgnoreCase(PaymentMode.SUBSCRIPTION.toString())){
					this.paymentMode = "Subscription";
				}else if(order.getPaymentStatus()!=null) {
					this.paymentMode = order.getPaymentStatus();
				}
				else {
					this.paymentMode = "COD";
				}
				
 			}
			
		}
		if(this.outCircleDeliveryCharges!=0){
			this.amount+=check.getOutCircleDeliveryCharges();
			this.total = finalValue+ this.outCircleDeliveryCharges +this.amountAfterDiscountCharges - this.discountAmount - waiveOffCharges - totalCouponAmount;
		    finalValue=0;
		}
		else{
			this.total = finalValue +this.amountAfterDiscountCharges - this.discountAmount - totalCouponAmount;
			finalValue=0;
		}
		this.amount -= (float)totalCouponAmount;
		this.roundedOffTotal=round(this.total,2);
		this.amountAfterDiscountCharges = (float)(Math.round(this.amountAfterDiscountCharges *100.0) / 100.0);
		this.amountSaved =round((check.getBill() - this.amountAfterDiscountCharges) + waiveOffCharges,2); 
		/*if(lastInvoiceAmount>0 && this.amountAfterDiscountCharges>0){
			if(Math.abs(lastInvoiceAmount-(this.roundedOffTotal+this.checkCreditBalance))>0){
					this.amountSaved=Math.round(round(lastInvoiceAmount - this.roundedOffTotal,2) + totalCouponAmount);
				}
			}
			else */
		if(totalCouponAmount>0 || this.amountSaved>0){
					this.amountSaved += Math.round(totalCouponAmount);
		}
	}

	public double getAmountAfterCouponDiscount(OrderDish orderDish,List<Coupon> couponList,double orderAmount){
		double dishAmountACA =orderDish.getPrice();
		DishCouponCalcDTO dishCoupCalc = new DishCouponCalcDTO();
		for(Coupon coupon: couponList){
			if(coupon.getFlatRules().getIsAbsoluteDiscount()){
				double percantageAmount= (coupon.getFlatRules().getDiscountValue()/orderAmount)*100;
				double couponAmt = ((dishAmountACA*percantageAmount)/100);
				dishCoupCalc.calcAmount = couponAmt*orderDish.getQuantity();
				dishCoupCalc.couponName = coupon.getCouponName();
				couponCal.put(orderDish.getDishId(),dishCoupCalc);
				totalCouponAmount+= couponAmt*orderDish.getQuantity();
				dishAmountACA -= couponAmt;
				 
			}else{
				double couponAmt=((dishAmountACA*coupon.getFlatRules().getDiscountValue())/100 ); 
					dishCoupCalc.calcAmount = couponAmt*orderDish.getQuantity();
					dishCoupCalc.couponName = coupon.getCouponName();
					couponCal.put(orderDish.getDishId(),dishCoupCalc);
					totalCouponAmount+= couponAmt*orderDish.getQuantity();
				
				 dishAmountACA -=couponAmt;
			}
		}
		return dishAmountACA;
	}
	
	public String[] getTaxNames() {
		return taxNames;
	}

	public Float[] getTaxValue() {
		return taxValue;
	}
	
	public void setTaxValue(Float[] taxValue) {
		this.taxValue = taxValue;
	}
	
	public void setTaxNames(String[] taxNames) {
		this.taxNames = taxNames;
	}

	public String getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
	public String getDeliveryDateTime() {
		return deliveryDateTime;
	}
	public void setDeliveryDateTime(String deliveryDateTime) {
		this.deliveryDateTime = deliveryDateTime;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getTableId() {
		return tableId;
	}
	public void setTableId(Integer tableId) {
		this.tableId = tableId;
	}
	public Integer getGuests() {
		return guests;
	}
	public void setGuests(Integer guests) {
		this.guests = guests;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	public String getAdditionalChargeName1() {
		return additionalChargeName1;
	}
	public void setAdditionalChargeName1(String additionalChargeName1) {
		this.additionalChargeName1 = additionalChargeName1;
	}
	public String getAdditionalChargeName2() {
		return additionalChargeName2;
	}
	public void setAdditionalChargeName2(String additionalChargeName2) {
		this.additionalChargeName2 = additionalChargeName2;
	}
	public String getAdditionalChargeName3() {
		return additionalChargeName3;
	}
	public void setAdditionalChargeName3(String additionalChargeName3) {
		this.additionalChargeName3 = additionalChargeName3;
	}
	public float getAdditionalCharge1() {
		return additionalCharge1;
	}
	public void setAdditionalCharge1(float additionalCharge1) {
		this.additionalCharge1 = additionalCharge1;
	}
	public float getAdditionalCharge2() {
		return additionalCharge2;
	}
	public void setAdditionalCharge2(float additionalCharge2) {
		this.additionalCharge2 = additionalCharge2;
	}
	public float getAdditionalCharge3() {
		return additionalCharge3;
	}
	public void setAdditionalCharge3(float additionalCharge3) {
		this.additionalCharge3 = additionalCharge3;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public List<CheckDishResponse> getItems() {
		return items;
	}
	public void setItems(List<CheckDishResponse> items) {
		this.items = items;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public CheckType getCheckType() {
		return checkType; 
	}

	public void setCheckType(CheckType checkType) {
		this.checkType = checkType;
	}

	public String getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(String deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public String getDeliveryArea() {
		// Erich: A hack, since many reports have been hardcoded to only work with a non-null value
		return deliveryArea != null ? deliveryArea : "N/A";
	}

	public void setDeliveryArea(String deliveryArea) {
		this.deliveryArea = deliveryArea;
	}

	public String getDeliverAddress() {
		return deliverAddress;
	}

	public void setDeliverAddress(String deliverAddress) {
		this.deliverAddress = deliverAddress;
	}

	public double getRoundedOffTotal() {
		return roundedOffTotal;
	}

	public void setRoundedOffTotal(double roundedOffTotal) {
		this.roundedOffTotal = roundedOffTotal;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public float getDiscountPercent() {
		return discountPercent;
	}

	public void setDiscountPercent(float discountPercent) {
		this.discountPercent = discountPercent;
	}

	public float getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(float discountAmount) {
		this.discountAmount = discountAmount;
	}

	public float getAmountAfterDiscount() {
		return amountAfterDiscount;
	}

	public void setAmountAfterDiscount(float amountAfterDiscount) {
		this.amountAfterDiscount = amountAfterDiscount;
	}

	public float getOutCircleDeliveryCharges() {
		return outCircleDeliveryCharges;
	}

	public void setOutCircleDeliveryCharges(float outCircleDeliveryCharges) {
		this.outCircleDeliveryCharges = outCircleDeliveryCharges;
	}
	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public String getDeliveryInst() {
		return deliveryInst;
	}
	
	public void setDeliveryInst(String deliveryInst) {
		this.deliveryInst = deliveryInst;
	}
	
	public List<Order_DCList> getDc_List() {
		return dc_List;
	}

	public void setDc_List(List<Order_DCList> dc_List) {
		this.dc_List = dc_List;
	}

	public float getAmountAfterDiscountCharges() {
		return amountAfterDiscountCharges;
	}


	public void setAmountAfterDiscountCharges(float amountAfterDiscountCharges) {
		this.amountAfterDiscountCharges = amountAfterDiscountCharges;
	}
	
	public double  getAmountAfterCharge() {
		return amountAfterCharge;
	}

	public void setAmountAfterCharge(float amountAfterCharge) {
		this.amountAfterCharge = amountAfterCharge;
	}
	
	public double getAmountSaved() {
		return amountSaved;
	}

	public void setAmountSaved(double amountSaved) {
		this.amountSaved = amountSaved;
	}
	
	public float getWaiveOffCharges() {
		return waiveOffCharges;
	}

	public void setWaiveOffCharges(float waiveOffCharges) {
		this.waiveOffCharges = waiveOffCharges;
	}

	public int getDiscountflag() {
		return discountflag;
	}

	public void setDiscountflag(int discountflag) {
		this.discountflag = discountflag;
	}
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getOrderSource() {
		return orderSource;
	}

	public void setOrderSource(String orderSource) {
		this.orderSource = orderSource;
	}
	public SortedMap<String, Float> getTaxDetails() {
		return taxDetails;
	}


	public void setTaxDetails(SortedMap<String, Float> taxDetails) {
		this.taxDetails = taxDetails;
	}


	public SortedMap<String, Float> getTaxPool() {
		return taxPool;
	}


	public void setTaxPool(SortedMap<String, Float> taxPool) {
		this.taxPool = taxPool;
	}


	public SortedMap<String, Float> getDiscountDetails() {
		return discountDetails;
	}
	
	public float getDiscountedPrice(float price,float discount){
		price = (price*discount)/100;
		return price;
	}

	public void setDiscountDetails(SortedMap<String, Float> discountDetails) {
		this.discountDetails = discountDetails;
	}

	public SortedMap<String, Float> getChargeDetails() {
		return chargeDetails;
	}


	public void setChargeDetails(SortedMap<String, Float> chargeDetails) {
		this.chargeDetails = chargeDetails;
	}

	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
	public float getCheckCreditBalance() {
		return checkCreditBalance;
	}


	public void setCheckCreditBalance(float checkCreditBalance) {
		this.checkCreditBalance = checkCreditBalance;
	}
	
	public float getLastInvoiceAmount() {
		return lastInvoiceAmount;
	}

	public void setLastInvoiceAmount(float lastInvoiceAmount) {
		this.lastInvoiceAmount = lastInvoiceAmount;
	}
	
	public List<Coupon> getCouponAppliedList() {
		return couponAppliedList;
	}

	public void setCouponAppliedList(List<Coupon> couponAppliedList) {
		this.couponAppliedList = couponAppliedList;
	}
	
	public Map<Integer, DishCouponCalcDTO> getCouponCal() {
		return couponCal;
	}

	public void setCouponCal(Map<Integer, DishCouponCalcDTO> couponCal) {
		this.couponCal = couponCal;
	}
	public boolean isToShowOldVat_ServiceTax() {
		return toShowOldVat_ServiceTax;
	}

	public void setToShowOldVat_ServiceTax(boolean toShowOldVat_ServiceTax) {
		this.toShowOldVat_ServiceTax = toShowOldVat_ServiceTax;
	}


}