/**
 * 
 */
package com.cookedspecially.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cookedspecially.enums.Status;
import com.cookedspecially.enums.restaurant.ChargesType;

/**
 * @author Rahul	
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TAXTYPES")
public class TaxType implements Serializable {
    private static final long serialVersionUID = 1L;
    
	@Id
	@Column(name="id")
	@GeneratedValue
	private Integer taxTypeId;
	
	@Column(name="restaurantId")
	private Integer restaurantId;
	
	@Column(name="name")
	private String name;

	@Column(name="chargeType")
	private ChargesType chargeType;

	@Column(name="taxValue")
	private Float taxValue;
	
	@Column(name = "dishType")
	private String dishType;
	
	@Column(name="overridden")
	private Integer overridden;
	
	@Column(name="STATUS")
	private Status status = Status.ACTIVE;
	

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Integer getOverridden() {
		return overridden;
	}

	public void setOverridden(Integer overridden) {
		this.overridden = overridden;
	}

	public ChargesType getChargeType() {
		return chargeType;
	}

	public void setChargeType(ChargesType chargeType) {
		this.chargeType = chargeType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getTaxTypeId() {
		return taxTypeId;
	}

	public void setTaxTypeId(Integer taxTypeId) {
		this.taxTypeId = taxTypeId;
	}

	public Float getTaxValue() {
	
		return taxValue;
	}
	
	public double getTaxCharge(double bill, ChargesType chargeType, float additionalChargeValue) {
		double retVal = 0;
		if (chargeType == ChargesType.ABSOLUTE) {
			retVal = additionalChargeValue;
		} else if (chargeType == ChargesType.PERCENTAGE) {
			retVal = bill * additionalChargeValue / 100;
		}
		
		return retVal;
	}

	public void setTaxValue(Float taxValue) {
		this.taxValue = taxValue;
	}

	public String getDishType() {
		return dishType;
	}

	public void setDishType(String dishType) {
		this.dishType = dishType;
	}

	public Integer getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}
}