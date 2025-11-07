package com.cookedspecially.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="EDIT_CHECKS")
public class EditChecks {

	@Id
	@Column(name="id")
	@GeneratedValue
	Integer id;
	
	@Column(name="CHECKJSON")
	String checkJson;
	
	@Column(name="CHECKID")
	Integer checkId;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="OPENTIME")
	private Date openTime;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCheckJson() {
		return checkJson;
	}

	public void setCheckJson(String checkJson) {
		this.checkJson = checkJson;
	}

	public Date getOpenTime() {
		return openTime;
	}

	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
	}
	
	public Integer getCheckId() {
		return checkId;
	}

	public void setCheckId(Integer checkId) {
		this.checkId = checkId;
	}
	
}
