/**
 * 
 */
package com.cookedspecially.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import com.cookedspecially.utility.ImageUtility;
import com.fasterxml.jackson.annotation.JsonRawValue;


/**
 * @author sagarwal
 *
 */
public class DishWrapper implements Serializable {
    private static final long serialVersionUID = 1L;
	
	private Integer itemId;
	
	private Integer restaurantId;
	
	private Integer userId;
	
	private String name;
	
	private String description;
	
	private String shortDescription;
	
	private String rectangularImage;
	
	private String imageUrl;
	
	private String smallImageUrl;
	
	private List<Dish_Size> dishSize;
	
	private Float price;

	private Float displayPrice;
	
	private List<NutrientInfo> nutritionalInfo;
	
	private boolean manageStock;

	private List<Entry> remainingStock;
	
	private String itemType;
	
	private Boolean vegetarian;
	
	private Boolean alcoholic;

	private Boolean disabled;
	
	private String customizeLimits;
	
	private List<String> addOn; 
	
	public List<Dish_Size> getDishSize() {
		return dishSize;
	}

	public void setDishSize(List<Dish_Size> dishSize) {
		this.dishSize = dishSize;
	}
	
	public List<String> getAddOn() {
		return addOn;
	}

	public void setAddOn(String addOn) {
		List<String> addOnLists =  new ArrayList<String>();
		if(addOn!=null){
		if(!"0".equalsIgnoreCase(addOn)){
		 addOnLists = Arrays.asList(addOn.split(","));
		}
		 this.addOn=addOnLists;
	}else{
		this.addOn=addOnLists;
	}
	}

	public static DishWrapper getDishWrapper(Dish dish,String timeZone) throws NullPointerException {
		DishWrapper dishWrapper = new DishWrapper();
		dishWrapper.setItemId(dish.getDishId());
		dishWrapper.setRestaurantId(dish.getRestaurantId());
		dishWrapper.setUserId(dish.getRestaurantId());
		dishWrapper.setName(dish.getName());
		dishWrapper.setDescription(dish.getDescription()==null?"":dish.getDescription().replaceAll("'", "&#39;"));
		dishWrapper.setShortDescription(dish.getShortDescription()==null?"":dish.getShortDescription().replaceAll("'", "&#39;"));
		dishWrapper.setImageUrl(dish.getImageUrl());
		dishWrapper.setSmallImageUrl(ImageUtility.getSmallImageUrl(dish.getImageUrl(), 200, 200));
		dishWrapper.setRectangularImage(dish.getRectangularImageUrl());
		dishWrapper.setPrice(dish.getPriceByHappyHour(timeZone));
		dishWrapper.setDishSize(dish.getDishSize());
		dishWrapper.setManageStock(dish.isManageStock());
		dishWrapper.setRemainingStock(dish.getAvailableStock());
		dishWrapper.setItemType(dish.getDishType());
		dishWrapper.setVegetarian(dish.getVegetarian());
		dishWrapper.setAlcoholic(dish.getAlcoholic());
		dishWrapper.setDisabled(dish.getDisabled());
		dishWrapper.setDisplayPrice(dish.getDisplayPrice());
	    dishWrapper.setAddOn(dish.getAddOn());
	    dishWrapper.setNutritionalInfo(dish.getNutrientInfo());
	    dishWrapper.setCustomizeLimits(dish.getCustomizeLimits());	    
		return dishWrapper;
	}
	
	public List<Entry> getRemainingStock() {
		return remainingStock;
	}

	public void setRemainingStock(List<Entry> remainingStock) {
		this.remainingStock = remainingStock;
	}
	
	public boolean isManageStock() {
		
		return manageStock;
	}

	public void setManageStock(boolean manageStock) {
		
		this.manageStock = manageStock;
	}

	public List<NutrientInfo> getNutritionalInfo() {
		
		return nutritionalInfo;
	}

	public void setNutritionalInfo(List<NutrientInfo> nutritionalInfo) {
	
		this.nutritionalInfo = nutritionalInfo;
	}

	public Float getDisplayPrice() {
		
		return displayPrice;
	}

	public void setDisplayPrice(Float displayPrice) {
		
		this.displayPrice = displayPrice;
	}

	public String getRectangularImage() {
		
		return rectangularImage;
	}

	public void setRectangularImage(String rectangularImage) {
		this.rectangularImage = rectangularImage;
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

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public Boolean getVegetarian() {
		return vegetarian;
	}

	public void setVegetarian(Boolean vegetarian) {
		this.vegetarian = vegetarian;
	}

	public Boolean getAlcoholic() {
		return alcoholic;
	}

	public void setAlcoholic(Boolean alcoholic) {
		this.alcoholic = alcoholic;
	}


	public String getSmallImageUrl() {
		return smallImageUrl;
	}


	public void setSmallImageUrl(String smallImageUrl) {
		this.smallImageUrl = smallImageUrl;
	}


	public Boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}
	
	@JsonRawValue
	public String getCustomizeLimits() {
		if("".equals(customizeLimits)){
			customizeLimits=null;
		}
		return customizeLimits;
	}

	public void setCustomizeLimits(String customizeLimits) {
		this.customizeLimits = customizeLimits;
	}

}
