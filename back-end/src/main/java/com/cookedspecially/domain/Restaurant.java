/**
 * 
 */
package com.cookedspecially.domain;

import com.cookedspecially.config.TimeZoneConstants;
import com.cookedspecially.enums.Status;
import com.cookedspecially.enums.restaurant.ChargesType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author shashank
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="RESTAURANT")
public class Restaurant implements Serializable {
    private static final long serialVersionUID = 1L;
    
	@Id
	@Column(name="restaurantId")
	@GeneratedValue
	private Integer restaurantId;
	
	@Column(name="RESTAURANTNAME")
	private String restaurantName;
	
	@Column(name="parentRestaurantId")
	private Integer parentRestaurantId;
	
	@Column(name="businessPhoneNo")
	private String bussinessPhoneNo;
	
	@Column(name="phone")
	private String phone;
	
	@Column(name="businessName")
	private String bussinessName;
	
	@Column(name="BUSINESSPORTRAITIMAGEURL")
	private String businessPortraitImageUrl;
	
	@Column(name="BUSINESSLANDSCAPEIMAGEURL")
	private String businessLandscapeImageUrl;
	
	@Column(name="additionalChargesName1")
	private String additionalChargesName1;
	
	@Column(name="additionalChargesType1")
	private ChargesType additionalChargesType1;
	
	@Column(name="additionalChargesName2")
	private String additionalChargesName2;
	
	@Column(name="additionalChargesType2")
	private ChargesType additionalChargesType2;
	
	@Column(name="additionalChargesName3")
	private String additionalChargesName3;
	
	@Column(name="additionalChargesType3")
	private ChargesType additionalChargesType3;
	
	@Column(name="additionalChargesValue1")
	private float additionalChargesValue1 = 0.0f;
	
	@Column(name="additionalChargesValue2")
	private float additionalChargesValue2 = 0.0f;
	
	@Column(name="additionalChargesValue3")
	private float additionalChargesValue3 = 0.0f;
	
	@Column(name="ADDRESS1")
	private String address1;
	
	@Column(name="ADDRESS2")
	private String address2;
	
	@Column(name="STATE")
	private String state;
	
	@Column(name="CITY")
	private String city;
	
	@Column(name="COUNTRY")
	private String country;
	
	@Column(name="ZIP")
	private String zip;
	
	@Column(name="APPCACHEICONURL")
	private String appCacheIconUrl;
	
	@Column(name="BUTTONICONURL")
	private String buttonIconUrl;
	
	@Column(name="CURRENCY")
	private String currency;
	
	@Column(name="roundOffAmount")
	private boolean roundOffAmount=false;
	
/*	@Column(name="deliveryCharges")
	private float deliveryCharges = 0.0f;*/
	
	@Column(name="inCircleDeliveryCharges")
	private float inCircleDeliveryCharges = 0.0f;
	
	@Column(name="outCircleDeliveryCharges")
	private float outCircleDeliveryCharges = 0.0f;
	
	@Column(name="minInCircleDeliveyThreshold")
	private float minInCircleDeliveyThreshold = 0.0f;
	
	@Column(name="minOutCircleDeliveyThreshold")
	private float minOutCircleDeliveyThreshold = 0.0f;

	@Column(name="invoicePrefix")
	private String invoicePrefix;
	
	@Column(name="invoiceStartCounter")
	private long invoiceStartCounter;

	@Column(name="timeZone")
	private String timeZone = "Asia/Calcutta";
	
	@Column(name ="closedText")
	private String closedText;
	
	@Column(name ="openFlag")
	private String openFlag ="Always Open";
	
	@Column(name="sundayOpenTime")
	private String sundayOpenTime = "09:00";
	
	@Column(name="sundayCloseTime")
	private String sundayCloseTime = "17:00";
	
	@Column(name="mondayOpenTime")
	private String mondayOpenTime = "09:00";
	
	@Column(name="mondayCloseTime")
	private String mondayCloseTime = "17:00";
	
	@Column(name="tuesdayOpenTime")
	private String tuesdayOpenTime = "09:00";
	
	@Column(name="tuesdayCloseTime")
	private String tuesdayCloseTime = "17:00";
	
	@Column(name="wednesdayOpenTime")
	private String wednesdayOpenTime = "09:00";
	
	@Column(name="wednesdayCloseTime")
	private String wednesdayCloseTime = "17:00";
	
	@Column(name="thursdayOpenTime")
	private String thursdayOpenTime = "09:00";
	
	@Column(name="thursdayCloseTime")
	private String thursdayCloseTime = "17:00";
	
	@Column(name="fridayOpenTime")
	private String fridayOpenTime = "09:00";
	
	@Column(name="fridayCloseTime")
	private String fridayCloseTime = "17:00";
	
	@Column(name="saturdayOpenTime")
	private String saturdayOpenTime = "09:00";
	
	@Column(name="saturdayCloseTime")
	private String saturdayCloseTime = "17:00";

	@Column
	private String alertMail;
	
	@Column(name = "mailUsername")
	private String mailUsername;
	
	@Column(name = "mailPassword")
	private String mailPassword;
	
	@Column(name= "mailHost")
	private String mailHost;
	
	@Column(name= "mailPort")
	private Integer mailPort;
	
	@Column(name= "mailProtocol")
	private String mailProtocol;
	
	@Column(name="marketingImage")
	private String marketingImage;
	
	@Column(name="alterMarketingText")
	private String alterMarketingText;
	
	@Column(name="tinNo")
	private String tinNo;
	
	@Column(name="serviceTaxNo")
	private String serviceTaxNo;
	
	@Column(name="serviceTaxText")
	private String serviceTaxText;
	
	@Column(name="serviceTaxValue")
	private Float serviceTaxValue;
	
	@Column(name="hrefLink")
	private String referenceLink;

	@Column(name="STATUS")
	private Status status = Status.ACTIVE;
	
	@Column(name="closeImageLink")
	private String closeImageLink;
	
	@Column(name="deliveryManagerEdit")
	private boolean deliveryManagerEdit=false;
	
	@Column(name="headerImageUrl")
	private String headerImageUrl;
	
	@Column(name="websiteURL")
	private String websiteURL;
	
	@Column(name="enableCustCredit")
	private boolean enableCustCredit=false;

	@Column(name="defaultCreditType")
	private Integer defaultCreditType;


	@Transient
	private String timeZoneUnicode;
	
	@Transient
	private List<TaxType> taxList;
	
	@Transient
	private List<OrderSource> orderSource;
	
	@Transient
	private List<PaymentType> paymentType;
	
	@Transient
	private List<FulfillmentCenter> ffCenter;
	
	public boolean isEnableCustCredit() {
		return enableCustCredit;
	}
	public void setEnableCustCredit(boolean enableCustCredit) {
		this.enableCustCredit = enableCustCredit;
	}
	
	public Integer getDefaultCreditType() {
		return defaultCreditType;
	}
	public void setDefaultCreditType(Integer defaultCreditType) {
		this.defaultCreditType = defaultCreditType;
	}
	
	public String getHeaderImageUrl() {
		return headerImageUrl;
	}
	public void setHeaderImageUrl(String headerImageUrl) {
		this.headerImageUrl = headerImageUrl;
	}
	public boolean isDeliveryManagerEdit() {
		return deliveryManagerEdit;
	}
	public void setDeliveryManagerEdit(boolean deliveryManagerEdit) {
		this.deliveryManagerEdit = deliveryManagerEdit;
	}
	
	public List<FulfillmentCenter> getFfCenter() {
		return ffCenter;
	}
	public void setFfCenter(List<FulfillmentCenter> ffCenter) {
		this.ffCenter = ffCenter;
	}
	
	public String getAlertMail() {
		return alertMail;
	}
	public void setAlertMail(String alertMail) {
		this.alertMail = alertMail;
	}
	
	public List<PaymentType> getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(List<PaymentType> paymentType) {
		this.paymentType = paymentType;
	}
	public List<OrderSource> getOrderSource() {
		return orderSource;
	}
	public void setOrderSource(List<OrderSource> orderSource) {
		this.orderSource = orderSource;
	}
	public List<TaxType> getTaxList() {
		return taxList;
	}
	public void setTaxList(List<TaxType> taxList) {
		this.taxList = taxList;
	}
	public String getCloseImageLink() {
		return closeImageLink;
	}
	public void setCloseImageLink(String closeImageLink) {
		this.closeImageLink = closeImageLink;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public String getReferenceLink() {
		return referenceLink;
	}
	public void setReferenceLink(String referenceLink) {
		this.referenceLink = referenceLink;
	}
	public Integer getRestaurantId() {
		return restaurantId;
	}
	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}
	public String getRestaurantName() {
		return restaurantName;
	}
	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}
	public Integer getParentRestaurantId() {
		return parentRestaurantId;
	}
	public void setParentRestaurantId(Integer parentRestaurantId) {
		this.parentRestaurantId = parentRestaurantId;
	}
	public String getBussinessPhoneNo() {
		return bussinessPhoneNo;
	}
	public void setBussinessPhoneNo(String bussinessPhoneNo) {
		this.bussinessPhoneNo = bussinessPhoneNo;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getBussinessName() {
		return bussinessName;
	}
	public void setBussinessName(String bussinessName) {
		this.bussinessName = bussinessName;
	}
	public String getBusinessPortraitImageUrl() {
		return businessPortraitImageUrl;
	}
	public void setBusinessPortraitImageUrl(String businessPortraitImageUrl) {
		this.businessPortraitImageUrl = businessPortraitImageUrl;
	}
	public String getBusinessLandscapeImageUrl() {
		return businessLandscapeImageUrl;
	}
	public void setBusinessLandscapeImageUrl(String businessLandscapeImageUrl) {
		this.businessLandscapeImageUrl = businessLandscapeImageUrl;
	}
	public String getAdditionalChargesName1() {
		return additionalChargesName1;
	}
	public void setAdditionalChargesName1(String additionalChargesName1) {
		this.additionalChargesName1 = additionalChargesName1;
	}
	public ChargesType getAdditionalChargesType1() {
		return additionalChargesType1;
	}
	public void setAdditionalChargesType1(ChargesType additionalChargesType1) {
		this.additionalChargesType1 = additionalChargesType1;
	}
	public String getAdditionalChargesName2() {
		return additionalChargesName2;
	}
	public void setAdditionalChargesName2(String additionalChargesName2) {
		this.additionalChargesName2 = additionalChargesName2;
	}
	public ChargesType getAdditionalChargesType2() {
		return additionalChargesType2;
	}
	public void setAdditionalChargesType2(ChargesType additionalChargesType2) {
		this.additionalChargesType2 = additionalChargesType2;
	}
	public String getAdditionalChargesName3() {
		return additionalChargesName3;
	}
	public void setAdditionalChargesName3(String additionalChargesName3) {
		this.additionalChargesName3 = additionalChargesName3;
	}
	public ChargesType getAdditionalChargesType3() {
		return additionalChargesType3;
	}
	public void setAdditionalChargesType3(ChargesType additionalChargesType3) {
		this.additionalChargesType3 = additionalChargesType3;
	}
	public float getAdditionalChargesValue1() {
		return additionalChargesValue1;
	}
	public void setAdditionalChargesValue1(float additionalChargesValue1) {
		this.additionalChargesValue1 = additionalChargesValue1;
	}
	public float getAdditionalChargesValue2() {
		return additionalChargesValue2;
	}
	public void setAdditionalChargesValue2(float additionalChargesValue2) {
		this.additionalChargesValue2 = additionalChargesValue2;
	}
	public float getAdditionalChargesValue3() {
		return additionalChargesValue3;
	}
	public void setAdditionalChargesValue3(float additionalChargesValue3) {
		this.additionalChargesValue3 = additionalChargesValue3;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getAppCacheIconUrl() {
		return appCacheIconUrl;
	}
	public void setAppCacheIconUrl(String appCacheIconUrl) {
		this.appCacheIconUrl = appCacheIconUrl;
	}
	public String getButtonIconUrl() {
		return buttonIconUrl;
	}
	public void setButtonIconUrl(String buttonIconUrl) {
		this.buttonIconUrl = buttonIconUrl;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public float getInCircleDeliveryCharges() {
		return inCircleDeliveryCharges;
	}
	public void setInCircleDeliveryCharges(float inCircleDeliveryCharges) {
		this.inCircleDeliveryCharges = inCircleDeliveryCharges;
	}
	public float getOutCircleDeliveryCharges() {
		return outCircleDeliveryCharges;
	}
	public void setOutCircleDeliveryCharges(float outCircleDeliveryCharges) {
		this.outCircleDeliveryCharges = outCircleDeliveryCharges;
	}
	public float getMinInCircleDeliveyThreshold() {
		return minInCircleDeliveyThreshold;
	}
	public void setMinInCircleDeliveyThreshold(float minInCircleDeliveyThreshold) {
		this.minInCircleDeliveyThreshold = minInCircleDeliveyThreshold;
	}
	public float getMinOutCircleDeliveyThreshold() {
		return minOutCircleDeliveyThreshold;
	}
	public void setMinOutCircleDeliveyThreshold(float minOutCircleDeliveyThreshold) {
		this.minOutCircleDeliveyThreshold = minOutCircleDeliveyThreshold;
	}
	public String getInvoicePrefix() {
		return invoicePrefix;
	}
	public void setInvoicePrefix(String invoicePrefix) {
		this.invoicePrefix = invoicePrefix;
	}
	public long getInvoiceStartCounter() {
		return invoiceStartCounter;
	}
	public void setInvoiceStartCounter(long invoiceStartCounter) {
		this.invoiceStartCounter = invoiceStartCounter;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	public String getClosedText() {
		return closedText;
	}
	public void setClosedText(String closedText) {
		this.closedText = closedText;
	}
	public String getOpenFlag() {
		return openFlag;
	}
	public void setOpenFlag(String openFlag) {
		this.openFlag = openFlag;
	}
	public String getSundayOpenTime() {
		return sundayOpenTime;
	}
	public void setSundayOpenTime(String sundayOpenTime) {
		this.sundayOpenTime = sundayOpenTime;
	}
	public String getSundayCloseTime() {
		return sundayCloseTime;
	}
	public void setSundayCloseTime(String sundayCloseTime) {
		this.sundayCloseTime = sundayCloseTime;
	}
	public String getMondayOpenTime() {
		return mondayOpenTime;
	}
	public void setMondayOpenTime(String mondayOpenTime) {
		this.mondayOpenTime = mondayOpenTime;
	}
	public String getMondayCloseTime() {
		return mondayCloseTime;
	}
	public void setMondayCloseTime(String mondayCloseTime) {
		this.mondayCloseTime = mondayCloseTime;
	}
	public String getTuesdayOpenTime() {
		return tuesdayOpenTime;
	}
	public void setTuesdayOpenTime(String tuesdayOpenTime) {
		this.tuesdayOpenTime = tuesdayOpenTime;
	}
	public String getTuesdayCloseTime() {
		return tuesdayCloseTime;
	}
	public void setTuesdayCloseTime(String tuesdayCloseTime) {
		this.tuesdayCloseTime = tuesdayCloseTime;
	}
	public String getWednesdayOpenTime() {
		return wednesdayOpenTime;
	}
	public void setWednesdayOpenTime(String wednesdayOpenTime) {
		this.wednesdayOpenTime = wednesdayOpenTime;
	}
	public String getWednesdayCloseTime() {
		return wednesdayCloseTime;
	}
	public void setWednesdayCloseTime(String wednesdayCloseTime) {
		this.wednesdayCloseTime = wednesdayCloseTime;
	}
	public String getThursdayOpenTime() {
		return thursdayOpenTime;
	}
	public void setThursdayOpenTime(String thursdayOpenTime) {
		this.thursdayOpenTime = thursdayOpenTime;
	}

	public String getThursdayCloseTime() {
		return thursdayCloseTime;
	}
	public void setThursdayCloseTime(String thursdayCloseTime) {
		this.thursdayCloseTime = thursdayCloseTime;
	}
	public String getFridayOpenTime() {
		return fridayOpenTime;
	}
	public void setFridayOpenTime(String fridayOpenTime) {
		this.fridayOpenTime = fridayOpenTime;
	}
	public String getFridayCloseTime() {
		return fridayCloseTime;
	}
	public void setFridayCloseTime(String fridayCloseTime) {
		this.fridayCloseTime = fridayCloseTime;
	}
	public String getSaturdayOpenTime() {
		return saturdayOpenTime;
	}
	public void setSaturdayOpenTime(String saturdayOpenTime) {
		this.saturdayOpenTime = saturdayOpenTime;
	}
	public String getSaturdayCloseTime() {
		return saturdayCloseTime;
	}
	public void setSaturdayCloseTime(String saturdayCloseTime) {
		this.saturdayCloseTime = saturdayCloseTime;
	}
	public String getMailUsername() {
		return mailUsername;
	}
	public void setMailUsername(String mailUsername) {
		this.mailUsername = mailUsername;
	}
	public String getMailPassword() {
		return mailPassword;
	}
	public void setMailPassword(String mailPassword) {
		this.mailPassword = mailPassword;
	}
    public String getMailHost() {
		return mailHost;
	}
	public void setMailHost(String mailHost) {
		this.mailHost = mailHost;
	}
	public Integer getMailPort() {
		return mailPort;
	}
	public void setMailPort(Integer mailPort) {
		this.mailPort = mailPort;
	}
	public String getMailProtocol() {
		return mailProtocol;
	}
	public void setMailProtocol(String mailProtocol) {
		this.mailProtocol = mailProtocol;
	}
	public String getMarketingImage() {
		return marketingImage;
	}
	public void setMarketingImage(String marketingImage) {
		this.marketingImage = marketingImage;
	}
	public String getAlterMarketingText() {
		return alterMarketingText;
	}
	public void setAlterMarketingText(String alterMarketingText) {
		this.alterMarketingText = alterMarketingText;
	}

	public String getTinNo() {
		return tinNo;
	}
	public void setTinNo(String tinNo) {
		this.tinNo = tinNo;
	}

	public float getAdditionalCharge(float bill, ChargesType chargeType, float additionalChargeValue) {
		float retVal = 0;
		if (chargeType == ChargesType.ABSOLUTE) {
			retVal = additionalChargeValue;
		} else if (chargeType == ChargesType.PERCENTAGE) {
			retVal = bill * additionalChargeValue / 100;
		}
		return retVal;
	}
	
	public boolean isRoundOffAmount() {
		return roundOffAmount;
	}
	public void setRoundOffAmount(boolean roundOffAmount) {
		this.roundOffAmount = roundOffAmount;
	}
	
	public String getServiceTaxNo() {
		return serviceTaxNo;
	}
	
	public void setServiceTaxNo(String serviceTaxNo) {
		this.serviceTaxNo = serviceTaxNo;
	}
	public Float getServiceTaxValue() {
		return serviceTaxValue;
	}
	public void setServiceTaxValue(Float serviceTaxValue) {
		this.serviceTaxValue = serviceTaxValue;
	}
	
	public String getServiceTaxText() {
		return serviceTaxText;
	}
	public void setServiceTaxText(String serviceTaxText) {
		this.serviceTaxText = serviceTaxText;
	}
	
	public String getTimeZoneUnicode() {
		TimeZoneConstants tz = new TimeZoneConstants();
		 final Map<String,String> map = tz.currHexCode;
		timeZoneUnicode = map.get(getCurrency());
		return timeZoneUnicode;
	}
	public void setTimeZoneUnicode(String timeZoneUnicode) {
		this.timeZoneUnicode = timeZoneUnicode;
	}
	
	public String getWebsiteURL() {
		return websiteURL;
	}
	public void setWebsiteURL(String websiteURL) {
		this.websiteURL = websiteURL;
	}
}
