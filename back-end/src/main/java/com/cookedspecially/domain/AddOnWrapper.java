package com.cookedspecially.domain;

import java.io.Serializable;
import java.util.List;

import com.cookedspecially.utility.ImageUtility;

public class AddOnWrapper implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer addOnId;

	private Integer restaurantId;
	
	private String name;
	
	private String description;
	
	private String shortDescription;
	
	private String rectangularImage;
	
	private String imageUrl;
	
	private String smallImageUrl;
	
	private List<AddOnDish_Size> addOnDishSize;
	
	private Float price;

	private Float displayPrice;
	
	private Integer limitQuantity;
	
	private List<AddOnNutrientInfo> nutritionalInfo;
	
	private String addOnType;
	
	private Boolean vegetarian;
	
	private Boolean alcoholic;

	private Boolean disabled;
	
	public static AddOnWrapper  getAddOnWrapper(AddOnDish addOn){
		AddOnWrapper addOnWrapper = new AddOnWrapper();
		
		addOnWrapper.setName(addOn.getName());
		addOnWrapper.setAddOnType(addOn.getDishType()!=null?addOn.getDishType().trim():addOn.getDishType());
		addOnWrapper.setAddOnId(addOn.getAddOnId());
		addOnWrapper.setRestaurantId(addOn.getRestaurantId());
		addOnWrapper.setDescription(addOn.getDescription()==null?"":addOn.getDescription().replaceAll("'", "&#39;"));
		addOnWrapper.setSmallImageUrl(ImageUtility.getSmallImageUrl(addOn.getImageUrl(), 200, 200));
		addOnWrapper.setRectangularImage(addOn.getRectangularImageUrl());
		addOnWrapper.setShortDescription(addOn.getShortDescription()==null?"":addOn.getShortDescription().replaceAll("'", "&#39;"));
		addOnWrapper.setAlcoholic(addOn.getAlcoholic());
		addOnWrapper.setAddOnDishSize(addOn.getAddOnDishSize());
		addOnWrapper.setDisplayPrice(addOn.getDisplayPrice());
		addOnWrapper.setDisabled(addOn.getDisabled());
		addOnWrapper.setLimitQuantity(addOn.getLimitQuantity());
		addOnWrapper.setNutritionalInfo(addOn.getNutritionalInfo());
		addOnWrapper.setPrice(addOn.getPrice());
		
		addOnWrapper.setVegetarian(addOn.getVegetarian());
		
		return addOnWrapper;
	}

	public List<AddOnDish_Size> getAddOnDishSize() {
		return addOnDishSize;
	}

	public void setAddOnDishSize(List<AddOnDish_Size> addOnDishSize) {
		this.addOnDishSize = addOnDishSize;
	}

	public Integer getRestaurantId() {
		return restaurantId;
	}


	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}


	public Integer getAddOnId() {
		return addOnId;
	}

	public void setAddOnId(Integer addOnId) {
		this.addOnId = addOnId;
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


	public String getRectangularImage() {
		return rectangularImage;
	}


	public void setRectangularImage(String rectangularImage) {
		this.rectangularImage = rectangularImage;
	}


	public String getImageUrl() {
		return imageUrl;
	}


	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}


	public String getSmallImageUrl() {
		return smallImageUrl;
	}


	public void setSmallImageUrl(String smallImageUrl) {
		this.smallImageUrl = smallImageUrl;
	}


	public Float getPrice() {
		return price;
	}


	public void setPrice(Float price) {
		this.price = price;
	}


	public String getAddOnType() {
		return addOnType;
	}


	public void setAddOnType(String addOnType) {
		this.addOnType = addOnType;
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


	public Boolean getDisabled() {
		return disabled;
	}


	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}


	public List<AddOnNutrientInfo> getNutritionalInfo() {
		return nutritionalInfo;
	}

	public void setNutritionalInfo(List<AddOnNutrientInfo> nutritionalInfo) {
		this.nutritionalInfo = nutritionalInfo;
	}

	public Float getDisplayPrice() {
		return displayPrice;
	}

	public void setDisplayPrice(Float displayPrice) {
		this.displayPrice = displayPrice;
	}
	
	public Integer getLimitQuantity() {
		return limitQuantity;
	}

	public void setLimitQuantity(Integer limitQuantity) {
		this.limitQuantity = limitQuantity;
	}


}
