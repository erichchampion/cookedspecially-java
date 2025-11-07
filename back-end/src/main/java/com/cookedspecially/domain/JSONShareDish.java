/**
 * 
 */
package com.cookedspecially.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author shashank
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JSONShareDish implements Serializable {
    private static final long serialVersionUID = 1L;
	
	private String fbAppId;
	private String ogType;
	private String ogUrl;
	private String address;
	private String locality;
	private String postalCode;
	private String country;
	private String longitude;
	private String latitude;
	private int restaurantId;
	private int dishId;
	
	public String getFbAppId() {
		return fbAppId;
	}
	public void setFbAppId(String fbAppId) {
		this.fbAppId = fbAppId;
	}
	public String getOgType() {
		return ogType;
	}
	public void setOgType(String ogType) {
		this.ogType = ogType;
	}
	public String getOgUrl() {
		return ogUrl;
	}
	public void setOgUrl(String ogUrl) {
		this.ogUrl = ogUrl;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLocality() {
		return locality;
	}
	public void setLocality(String locality) {
		this.locality = locality;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public Integer getRestaurantId() {
		return restaurantId;
	}
	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}
	public Integer getDishId() {
		return dishId;
	}
	public void setDishId(Integer dishId) {
		this.dishId = dishId;
	}
	
	
}
