/**
 * 
 */
package com.cookedspecially.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.cookedspecially.enums.WeekDayFlags;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonRawValue;

/**
 * @author sagarwal, rahul
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="DISHES")
public class Dish  implements Serializable {
    private static final long serialVersionUID = 1L;

	@Id
	@Column(name="DISHID")
	@GeneratedValue
	private Integer dishId;
	
	@Column(name="ADDON") 
	private String addOn;
	
	@Column(name="RESTAURANTID")
	private Integer restaurantId;
	
	@Column(name="manageStock")
	private boolean manageStock;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="SHORTDESCRIPTION")
	private String shortDescription;
	
	@Column(name="IMAGEURL")
	private String imageUrl;
	
	@Column(name="PRICE") 
	private Float price;
	
    @Column(name="DISPLAYPRICE")
    private Float displayPrice;
    
	@Column(name="DISHTYPE")
	private String dishType;
	
	@Column(name="VEGETARIAN")
	private boolean vegetarian;
	
	@Column(name="ALCOHOLIC")
	private boolean alcoholic;
	
	@Column(name="DISABLED")
	private boolean disabled;
	
	@Column(name="ACTIVEDAYS")
	private int activeDays;
	
	@Column(name="HAPPYHOURENABLED")
	private boolean happyHourEnabled;
	
	@Column(name="HAPPYHOURDAYS")
	private int happyHourDays;
	
	@Column(name="HAPPYHOURSTARTHOUR")
	private int happyHourStartHour;
	
	@Column(name="HAPPYHOURSTARTMIN")
	private int happyHourStartMin;
	
	@Column(name="HAPPYHOURENDHOUR")
	private int happyHourEndHour;
	
	@Column(name="HAPPYHOURENDMIN")
	private int happyHourEndMin;
	
	@Column(name="HAPPYHOURPRICE")
	private Float happyHourPrice;
	
	@Column(name="microScreen")
	private int microScreen; 
	
	@Column(name="rectangularImageUrl")
	private String rectangularImageUrl;
	

	@Column(name="customizeLimits")
	private String customizeLimits;
	
	@Column(name="recommended")
	private boolean recommended; 
	
	@OneToMany(fetch=FetchType.EAGER, orphanRemoval=true)
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.REMOVE})
	@JoinTable(name="DISH_NUTRIENTES", 
	                joinColumns={@JoinColumn(name="DISHID")}, 
	                inverseJoinColumns={@JoinColumn(name="ID")})
	private List<NutrientInfo> nutrientInfo = new ArrayList<NutrientInfo>();

	@OneToMany(orphanRemoval=true)
	@LazyCollection(LazyCollectionOption.FALSE)
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.REMOVE})
	@JoinTable(name="DISH_DISHSIZE", 
	                joinColumns={@JoinColumn(name="DISHID")}, 
	                inverseJoinColumns={@JoinColumn(name="ID")})
	private List<Dish_Size> dishSize= new ArrayList<Dish_Size>();

	@Transient
	private boolean[] dishActiveDays = new boolean[7];
	
	@Transient
	private boolean[] happyHourActiveDays = new boolean[7];
	
	@Transient
	private List<Entry> availableStock;

	@Transient
	private List<Dish_Size> DishSizeList= new ArrayList<Dish_Size>();
	
	@Transient
	public List<Dish_Size> getDishSizeList() {
		return DishSizeList;
	}

	@Transient
	public void setDishSizeList(List<Dish_Size> dishSizeList) {
		DishSizeList = dishSizeList;
	}

	public List<Dish_Size> getDishSize() {
		return dishSize;
	}

	public void setDishSize(List<Dish_Size> dishSize) {
		this.dishSize = dishSize;
	}
	
	public List<Entry> getAvailableStock() {
		return availableStock;
	}

	public void setAvailableStock(List<Entry> availableStock) {
		this.availableStock = availableStock;
	}

	public String getRectangularImageUrl() {
		return rectangularImageUrl;
	}

	public void setRectangularImageUrl(String rectangularImageUrl) {
		this.rectangularImageUrl = rectangularImageUrl;
	}
	
	public List<NutrientInfo> getNutrientInfo() {
		return nutrientInfo;
	}

	public void setNutrientInfo(List<NutrientInfo> nutrientInfo) {
		this.nutrientInfo = nutrientInfo;
	}
	
	public boolean isManageStock() {
		return manageStock;
	}

	public void setManageStock(boolean manageStock) {
		this.manageStock = manageStock;
	}

	
	public int getMicroScreen() {
		return microScreen;
	}

	public void setMicroScreen(int microScreen) {
		this.microScreen = microScreen;
	}
	
	public Integer getDishId() {
		return dishId;
	}

	public void setDishId(Integer dishId) {
		this.dishId = dishId;
	}

	public Integer getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
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

	/*public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}*/

	public String getDishType() {
		return dishType;
	}

	public void setDishType(String dishType) {
		this.dishType = dishType;
	}

	public boolean getVegetarian() {
		return vegetarian;
	}

	public void setVegetarian(boolean vegetarian) {
		this.vegetarian = vegetarian;
	}

	public boolean getAlcoholic() {
		return alcoholic;
	}

	public void setAlcoholic(boolean alcoholic) {
		this.alcoholic = alcoholic;
	}

	public boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public int getActiveDays() {
		return activeDays;
	}

	public void setActiveDays(int activeDays) {
		this.activeDays = activeDays;
	}

	public boolean getHappyHourEnabled() {
		return happyHourEnabled;
	}

	public void setHappyHourEnabled(boolean happyHourEnabled) {
		this.happyHourEnabled = happyHourEnabled;
	}

	public int getHappyHourDays() {
		return happyHourDays;
	}

	public void setHappyHourDays(int happyHourDays) {
		this.happyHourDays = happyHourDays;
	}

	public int getHappyHourStartHour() {
		return happyHourStartHour;
	}

	public void setHappyHourStartHour(int happyHourStartHour) {
		this.happyHourStartHour = happyHourStartHour;
	}

	public int getHappyHourStartMin() {
		return happyHourStartMin;
	}

	public void setHappyHourStartMin(int happyHourStartMin) {
		this.happyHourStartMin = happyHourStartMin;
	}

	public int getHappyHourEndHour() {
		return happyHourEndHour;
	}

	public void setHappyHourEndHour(int happyHourEndHour) {
		this.happyHourEndHour = happyHourEndHour;
	}

	public int getHappyHourEndMin() {
		return happyHourEndMin;
	}

	public void setHappyHourEndMin(int happyHourEndMin) {
		this.happyHourEndMin = happyHourEndMin;
	}

	public Float getHappyHourPrice() {
		return happyHourPrice;
	}

	public void setHappyHourPrice(Float happyHourPrice) {
		this.happyHourPrice = happyHourPrice;
	}

	public boolean[] getDishActiveDays() {
		return dishActiveDays;
	}

	public void setDishActiveDays(boolean[] dishActiveDays) {
		this.dishActiveDays = dishActiveDays;
	}

	public boolean[] getHappyHourActiveDays() {
		return happyHourActiveDays;
	}

	public void setHappyHourActiveDays(boolean[] happyHourActiveDays) {
		this.happyHourActiveDays = happyHourActiveDays;
	}
	
	public Float getDisplayPrice() {
			return displayPrice;
	}

	public void setDisplayPrice(Float displayPrice) {
		this.displayPrice = displayPrice;
	}
	
	public String getAddOn() {
		return addOn;

	}
	public void setAddOn(String addOn) {
		this.addOn = addOn;
	}
	
	public boolean isDishActive() {
		Calendar cal = Calendar.getInstance();
		String dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
		HashSet<String> weekdayFlags = WeekDayFlags.getWeekDayFlags(getActiveDays());
		if (weekdayFlags.contains(dayOfWeek)) {
			return true;
		}
		return false;
	}

	public Float getPriceByHappyHour(String timeZone) {
		if (getHappyHourEnabled()) {
		    TimeZone  tz =  TimeZone.getTimeZone(timeZone);
		    Calendar cal = Calendar.getInstance(tz,Locale.ENGLISH );
			String dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
			HashSet<String> weekdayFlags = WeekDayFlags.getWeekDayFlags(getHappyHourDays());
			if (weekdayFlags.contains(dayOfWeek)) {
				int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
				int minOfHour = cal.get(Calendar.MINUTE);
				//(hourOfDay < getHappyHourEndHour() || (hourOfDay == getHappyHourEndHour() && minOfHour < getHappyHourEndMin()))) ) {
				if ((hourOfDay >= getHappyHourStartHour() && minOfHour >=getHappyHourStartMin()) && hourOfDay<=getHappyHourEndHour() && minOfHour<=getHappyHourEndMin()){
					return getHappyHourPrice();
				}else if((hourOfDay >= getHappyHourStartHour() && minOfHour >=getHappyHourStartMin()) && hourOfDay<getHappyHourEndHour()){
					return getHappyHourPrice();
				}else if((hourOfDay > getHappyHourStartHour() && hourOfDay==getHappyHourEndHour())  && minOfHour<=getHappyHourEndMin()){
					return getHappyHourPrice();
				}else{
					return getPrice();
				}
			}
		}
		return getPrice();
	}
	
	public String getCustomizeLimits() {
		return customizeLimits;
	}
	
	public void setCustomizeLimits(String customizeLimits) {
		this.customizeLimits = customizeLimits;
	}
	
	public boolean isRecommended() {
		return recommended;
	}

	public void setRecommended(boolean recommended) {
		this.recommended = recommended;
	}
}
