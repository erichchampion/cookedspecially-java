/**
 * 
 */
package com.cookedspecially.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cookedspecially.enums.Status;

/**
 * @author sagarwal
 *
 */
public class MenuWrapper implements Serializable {
    private static final long serialVersionUID = 1L;

	private Integer menuId;
	
	private Integer restaurantId;
	
	private Integer userId;
	
	private String name;
	
	private String description;

	private Date modifiedTime;
	
	private Status status;
	
	private boolean posVisible;
	
	private String imageUrl;
	
	private boolean allDay;
	
	private String startTime;
	
	private String endTime;
	
	private List<SectionWrapper> sections;
	
	private  List<AddOnDish> addOns;

	public static MenuWrapper getMenuWrapper(Menu menu, List<StockManagement> stockDishes,String timeZone ) {
		MenuWrapper menuWrapper = new MenuWrapper();
		menuWrapper.setMenuId(menu.getMenuId());
		menuWrapper.setUserId(menu.getUserId());
		menuWrapper.setRestaurantId(menu.getRestaurantId());
		menuWrapper.setName(menu.getName());
		menuWrapper.setDescription(menu.getDescription().replaceAll("'", "&#39;"));
		menuWrapper.setModifiedTime(menu.getModifiedTime());
		menuWrapper.setStatus(menu.getStatus());
		menuWrapper.setImageUrl(menu.getImageUrl());
		menuWrapper.setPosVisible(menu.isPosVisible());
		menuWrapper.setAllDay(menu.isAllDay());
		menuWrapper.setStartTime(menu.getStartTime());
		menuWrapper.setEndTime(menu.getEndTime());
		List<Section> existingSections = menu.getSections();
		if (existingSections != null) {
			menuWrapper.sections = new ArrayList<SectionWrapper>();
			for (Section section : existingSections) {
				menuWrapper.sections.add(SectionWrapper.getSectionWrapper(section,stockDishes,timeZone));
			}
		}
		return menuWrapper;
	}
	
	public boolean isAllDay() {
		return allDay;
	}

	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}
	
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	public List<AddOnDish> getAddOns() {
		return addOns;
	}

	public void setAddOns(List<AddOnDish> addOns) {
		this.addOns = addOns;
	}
	
	public boolean isPosVisible() {
		return posVisible;
	}

	public void setPosVisible(boolean posVisible) {
		this.posVisible = posVisible;
	}
	
	public Integer getMenuId() {
		return menuId;
	}

	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}

	public Integer getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(Date modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public List<SectionWrapper> getSections() {
		return sections;
	}

	public void setSections(List<SectionWrapper> sections) {
		this.sections = sections;
	}

	
}
