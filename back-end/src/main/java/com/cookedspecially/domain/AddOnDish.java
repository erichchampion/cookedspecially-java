/**
 * 
 */
package com.cookedspecially.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.cookedspecially.enums.WeekDayFlags;
import com.cookedspecially.utility.ImageUtility;

/**
 * @author rahul 
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="ADDONDISHES")
public class AddOnDish implements Serializable {
    private static final long serialVersionUID = 1L;
    
	@Id
	@Column(name="ADDONID")
	@GeneratedValue
	private Integer addOnId;

	@Column(name="restaurantId")
	private Integer restaurantId;
	
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
	
	@Column(name="rectangularImageUrl")
	private String rectangularImageUrl;
	
	@Column(name="limitQuantity")
	private Integer limitQuantity;

	@OneToMany(fetch=FetchType.EAGER, orphanRemoval=true)
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.REMOVE})
	@JoinTable(name="ADDONDISH_NUTRIENTES", 
	                joinColumns={@JoinColumn(name="ADDONDISHID")}, 
	                inverseJoinColumns={@JoinColumn(name="ID")})
	private List<AddOnNutrientInfo> nutritionalInfo= new ArrayList<AddOnNutrientInfo>();
	
	@OneToMany(orphanRemoval=true)
	@LazyCollection(LazyCollectionOption.FALSE)
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.REMOVE})
	@JoinTable(name="ADDON_DISHSIZE", 
	                joinColumns={@JoinColumn(name="DISHID")}, 
	                inverseJoinColumns={@JoinColumn(name="ID")})
	private List<AddOnDish_Size> addOnDishSize= new ArrayList<AddOnDish_Size>();

	@Transient
	private List<AddOnDish_Size> DishSizeList= new ArrayList<AddOnDish_Size>();
	
	@Transient
	public List<AddOnDish_Size> getDishSizeList() {
		return DishSizeList;
	}

	@Transient
	public void setDishSizeList(List<AddOnDish_Size> dishSizeList) {
		DishSizeList = dishSizeList;
	}
	
	public List<AddOnDish_Size> getAddOnDishSize() {
		return addOnDishSize;
	}

	public void setAddOnDishSize(List<AddOnDish_Size> addOnDishSize) {
		this.addOnDishSize = addOnDishSize;
	}

	public Integer getLimitQuantity() {
		return limitQuantity;
	}

	public void setLimitQuantity(Integer limitQuantity) {
		this.limitQuantity = limitQuantity;
	}
	
	public List<AddOnNutrientInfo> getNutritionalInfo() {
		return nutritionalInfo;
	}

	public void setNutritionalInfo(List<AddOnNutrientInfo> nutritionalInfo) {
		this.nutritionalInfo = nutritionalInfo;
	}

	public String getRectangularImageUrl() {
		return rectangularImageUrl;
	}

	public void setRectangularImageUrl(String rectangularImageUrl) {
		this.rectangularImageUrl = rectangularImageUrl;
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

	public Integer getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}

	public String getDishType() {
		if(dishType!=null){
			return dishType.trim();
		}
		return dishType ;
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

	public boolean isDishActive() {
		Calendar cal = Calendar.getInstance();
		String dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
		/*HashSet<String> weekdayFlags = WeekDayFlags.getWeekDayFlags(getActiveDays());
		if (weekdayFlags.contains(dayOfWeek)) {
			return true;
		}*/
		return true;
	}
	
	public Float getDisplayPrice() {
		return displayPrice;
	}

	public void setDisplayPrice(Float displayPrice) {
		this.displayPrice = displayPrice;
	}

}
