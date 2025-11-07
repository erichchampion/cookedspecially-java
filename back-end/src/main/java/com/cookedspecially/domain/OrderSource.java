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

/**
 * @author Rahul
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="ORDER_SOURCE")
public class OrderSource implements Serializable {
    private static final long serialVersionUID = 1L;

	@Id
	@Column(name="id")
	@GeneratedValue
	private Integer sourceId;
	
	@Column(name="orgId")
	private Integer orgId;
	
	@Column(name="name")
	private String name;

	@Column(name="pointbooster")
	private float pointbooster;

	@Column(name="status")
	private String status ="ACTIVE";
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public float getPointbooster() {
		return pointbooster;
	}

	public void setPointbooster(float pointbooster) {
		this.pointbooster = pointbooster;
	}

	public Integer getSourceId() {
		return sourceId;
	}

	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	public Integer getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


}
