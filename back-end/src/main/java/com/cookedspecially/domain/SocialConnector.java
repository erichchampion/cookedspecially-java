package com.cookedspecially.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.cookedspecially.enums.Status;

@Entity
@Table(name="SOCIALCONNECTORS")
public class SocialConnector {
	
	@Id
	@Column(name="id")
	@GeneratedValue
	private Integer id;

	@Column(name="name")
	private String name;
	
	@Column(name="appId")
	private String appId;
	
	@Column(name="status")
	private Status status;
	
	@Column(name="organizationId")
	private Integer organizationId;

	public Integer getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Integer organizationId) {
		this.organizationId = organizationId;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
}
