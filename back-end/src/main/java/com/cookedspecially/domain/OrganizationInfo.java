/**
 * 
 */
package com.cookedspecially.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import com.cookedspecially.config.CountryCodes;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Rahul
 *
 */
public class OrganizationInfo implements Serializable {
    private static final long serialVersionUID = 1L;
	private Integer restaurantId;
	private Integer parentRestaurantId;
	private String businessName;
	private String address1;
	private String address2;
	private String city;
	private String country;
	private String zip;
	private String businessPortraitImageUrl;
	private String businessLandscapeImageUrl;
	private String appCacheIconUrl;
	private String buttonIconUrl;
	private String currency;
	private float inCircleDeliveryCharges=0.0f;
	private float outCircleDeliveryCharges=0.0f;
	private float minInCircleDeliveyThreshold = 0.0f;
	private float minOutCircleDeliveyThreshold = 0.0f;
	private String timeZone;
	private String sundayOpenTime = "09:00";
	private String sundayCloseTime = "17:00";
	private String mondayOpenTime = "09:00";
	private String mondayCloseTime = "17:00";
	private String tuesdayOpenTime = "09:00";
	private String tuesdayCloseTime = "17:00";
	private String wednesdayOpenTime = "09:00";
	private String wednesdayCloseTime = "17:00";
	private String thursdayOpenTime = "09:00";
	private String thursdayCloseTime = "17:00";
	private String fridayOpenTime = "09:00";
	private String fridayCloseTime = "17:00";
	private String saturdayOpenTime = "09:00";
	private String saturdayCloseTime = "17:00";
	private String mailUsername ;
	private String marketingImage;
	private String alterMarketingText;
	private String referenceLink;
	private String restaurantStatus;
	private String closeImageLink;
	private String restaurantCloseText;
	private List<TaxType> taxList;
	private List<OrderSource> orderSource;
	private List<PaymentType> paymentType;
	private List<RestaurantInfo> restaurants;
	private String timeZoneUnicode;
	private String countryCode;
	private String websiteURL;
	private List<SocialConnector> socialConnectors;
	
	public List<SocialConnector> getSocialConnectors() {
		return socialConnectors;
	}
	public void setSocialConnectors(List<SocialConnector> socialConnectors) {
		this.socialConnectors = socialConnectors;
	}
	public String getWebsiteURL() {
		return websiteURL;
	}
	public void setWebsiteURL(String websiteURL) {
		this.websiteURL = websiteURL;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getTimeZoneUnicode() {
		return timeZoneUnicode;
	}
	public void setTimeZoneUnicode(String timeZoneUnicode) {
		this.timeZoneUnicode = timeZoneUnicode;
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
	public String getRestaurantStatus() {
		return restaurantStatus;
	}
	public void setRestaurantStatus(String restaurantStatus) {
		this.restaurantStatus = restaurantStatus;
	}
	public String getCloseImageLink() {
		return closeImageLink;
	}
	public void setCloseImageLink(String closeImageLink) {
		this.closeImageLink = closeImageLink;
	}
	public String getRestaurantCloseText() {
		return restaurantCloseText;
	}
	public void setRestaurantCloseText(String restaurantCloseText) {
		this.restaurantCloseText = restaurantCloseText;
	}

	public List<RestaurantInfo> getRestaurants() {
		return restaurants;
	}
	public void setRestaurants(List<RestaurantInfo> restaurants) {
		this.restaurants = restaurants;
	}
	public String getAlterMarketingText() {
		return alterMarketingText;
	}
	public void setAlterMarketingText(String alterMarketingText) {
		this.alterMarketingText = alterMarketingText;
	}
	public String getReferenceLink() {
		return referenceLink;
	}
	public void setReferenceLink(String referenceLink) {
		this.referenceLink = referenceLink;
	}
	public String getMarketingImage() {
		return marketingImage;
	}
	public void setMarketingImage(String marketingImage) {
		this.marketingImage = marketingImage;
	}

	public void setMailHost(String mailHost) {
		this.mailHost = mailHost;
	}
	@JsonIgnore
	public Integer getMailPort() {
		return mailPort;
	}

	public void setMailPort(Integer mailPort) {
		this.mailPort = mailPort;
	}
	
	private String mailPassword;
	private String mailHost;
	private Integer mailPort;
	public OrganizationInfo(Restaurant rest) {
		if (rest != null) {
			this.restaurantId = rest.getRestaurantId();
			this.parentRestaurantId = rest.getParentRestaurantId();
			this.businessName = rest.getBussinessName();
			this.address1 = rest.getAddress1();
			this.address2 = rest.getAddress2();
			this.city = rest.getCity();
			this.country = rest.getCountry();
			this.zip = rest.getZip();
			this.businessPortraitImageUrl = rest.getBusinessPortraitImageUrl();
			this.businessLandscapeImageUrl = rest.getBusinessLandscapeImageUrl();
			this.appCacheIconUrl = rest.getAppCacheIconUrl();
			this.buttonIconUrl = rest.getButtonIconUrl();
			this.currency = rest.getCurrency();
			this.inCircleDeliveryCharges=rest.getInCircleDeliveryCharges();
			this.outCircleDeliveryCharges=rest.getOutCircleDeliveryCharges();
			this.minInCircleDeliveyThreshold = rest.getMinInCircleDeliveyThreshold();
			this.minOutCircleDeliveyThreshold = rest.getMinOutCircleDeliveyThreshold();
			this.timeZone = rest.getTimeZone();
			this.sundayOpenTime = rest.getSundayOpenTime();
			this.sundayCloseTime = rest.getSundayCloseTime();
			this.mondayOpenTime = rest.getMondayOpenTime();
			this.mondayCloseTime = rest.getMondayCloseTime();
			this.tuesdayOpenTime = rest.getTuesdayOpenTime();
			this.tuesdayCloseTime = rest.getTuesdayCloseTime();
			this.wednesdayOpenTime = rest.getWednesdayOpenTime();
			this.wednesdayCloseTime = rest.getWednesdayCloseTime();
			this.thursdayOpenTime = rest.getThursdayOpenTime();
			this.thursdayCloseTime = rest.getThursdayCloseTime();
			this.fridayOpenTime = rest.getFridayOpenTime();
			this.fridayCloseTime = rest.getFridayCloseTime();
			this.saturdayOpenTime = rest.getSaturdayOpenTime();
			this.saturdayCloseTime = rest.getSaturdayCloseTime();
			this.mailUsername=rest.getMailUsername();
			this.mailPassword=rest.getMailPassword();
			this.mailHost=rest.getMailHost();
			this.mailPort=rest.getMailPort();
			this.marketingImage=rest.getMarketingImage();
			this.referenceLink=rest.getReferenceLink();
			this.alterMarketingText=rest.getAlterMarketingText();
			this.restaurantStatus = rest.getOpenFlag();
			this.closeImageLink = rest.getCloseImageLink();
			this.restaurantCloseText = rest.getClosedText();
			this.timeZoneUnicode = rest.getTimeZoneUnicode();
			this.websiteURL = rest.getWebsiteURL();
			CountryCodes cc   =  new CountryCodes();
			String[] locales = Locale.getISOCountries();
			for (String countryCode : locales) {
			    Locale obj = new Locale("", countryCode);
			  if(obj.getDisplayCountry(Locale.ENGLISH).equalsIgnoreCase(rest.getCountry())){
				this.countryCode =   cc.country2phone.get(obj.getCountry());
			  }
			 }
		}
	}
	
	public Integer getRestaurantId() {
		return restaurantId;
	}
	public void setRestaurantId(Integer restId) {
		this.restaurantId = restId;
	}
	public String getBusinessName() {
		return businessName;
	}
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
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

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
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

	public Integer getParentRestaurantId() {
		return this.parentRestaurantId;
	}

	public void setParentRestaurantId(Integer parentUserId) {
		this.parentRestaurantId = parentUserId;
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
	@JsonIgnore
	public String getMailUsername() {
		return mailUsername;
	}

	public void setMailUsername(String mailUsername) {
		this.mailUsername = mailUsername;
	}
	@JsonIgnore
	public String getMailPassword() {
		return mailPassword;
	}

	public void setMailPassword(String mailPassword) {
		this.mailPassword = mailPassword;
	}
	
	@JsonIgnore
	public String getMailHost() {
		return mailHost;
	}

	
}
