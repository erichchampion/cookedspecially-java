package com.cookedspecially.domain;

import java.io.Serializable;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cookedspecially.enums.check.AdditionalCategories;
import com.cookedspecially.enums.restaurant.ChargesType;

/**
 * @author rahul
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="ORDER_DCLIST")
public class Order_DCList implements Serializable {
    private static final long serialVersionUID = 1L;

	@Id
	@Column(name="id")
	@GeneratedValue
	private long id;
	
	@Column(name="dcId")
	private long dcId;
	
	@Column(name="name")
	private String name;
	
	@Column(name="category")
	public AdditionalCategories category;

	@Column(name="type")
	public ChargesType type;
	
	@Column(name="value")
	public float value;
	
	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public long getDcId() {
		return dcId;
	}

	public void setDcId(long dcId) {
		this.dcId = dcId;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AdditionalCategories getCategory() {
		return category;
	}

	public void setCategory(AdditionalCategories category) {
		this.category = category;
	}

	public ChargesType getType() {
		return type;
	}

	public void setType(ChargesType type) {
		this.type = type;
	}

}
