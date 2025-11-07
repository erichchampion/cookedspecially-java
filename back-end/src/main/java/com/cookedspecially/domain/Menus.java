/**
 * 
 */
package com.cookedspecially.domain;

import java.io.Serializable;
import java.util.List;

import com.cookedspecially.enums.Status;

/**
 * @author shashank
 *
 */
public class Menus implements Serializable {
    private static final long serialVersionUID = 1L;

	List<MenuWrapper> menus;
	List<AddOnWrapper> dishAddOn;
	
	Integer restaurantId;
	
	Status status;
	String portraitImageUrl;
	String landscapeImageUrl;
	String appCacheIconUrl;
	String buttonIconUrl;
	String currency;
	
	public List<AddOnWrapper> getDishAddOn() {
		return dishAddOn;
	}
	public void setDishAddOn(List<AddOnWrapper> dishAddOn) {
		this.dishAddOn = dishAddOn;
	}
	
	public List<MenuWrapper> getMenus() {
		return menus;
	}
	public void setMenus(List<MenuWrapper> menus) {
		this.menus = menus;
	}
	
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public Integer getRestaurantId() {
		return restaurantId;
	}
	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}
	public String getPortraitImageUrl() {
		return portraitImageUrl;
	}
	public void setPortraitImageUrl(String portraitImageUrl) {
		this.portraitImageUrl = portraitImageUrl;
	}
	public String getLandscapeImageUrl() {
		return landscapeImageUrl;
	}
	public void setLandscapeImageUrl(String landscapeImageUrl) {
		this.landscapeImageUrl = landscapeImageUrl;
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
}
