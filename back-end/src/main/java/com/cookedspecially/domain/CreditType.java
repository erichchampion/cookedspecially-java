package com.cookedspecially.domain;

import com.cookedspecially.enums.credit.BilligCycle;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.io.Serializable;


/**
 * @author Abhishek
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="CREDIT_TYPE")
public class CreditType implements Serializable {
    private static final long serialVersionUID = 1L;

	@Id
	@Column(name="id")
	@GeneratedValue
	private int id;
	
	@Size(min=3, max=50)
	@Column(name="type_name")
	private String name;
	
	@Min(1)
	@Column(name="organisationId")
	private int orgId;
	
	@Min(1)
	@Column(name="maxLimit")
	private float maxLimit;

	@Enumerated(EnumType.STRING)
	@Column(name="billingCycle")
	private BilligCycle billingCycle=BilligCycle.ONE_OFF;


	@Column(name = "banner_title")
	private String banner;


	public CreditType() {
		super();
	}

	public CreditType(String name, int orgId) {
		this.name=name;
		this.orgId=orgId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOrgId() {
		return orgId;
	}

	public void setOrgId(int orgId) {
		this.orgId = orgId;
	}

	public float getMaxLimit() {
		return maxLimit;
	}

	public void setMaxLimit(float maxLimit) {
		this.maxLimit = maxLimit;
	}
	
	
	public BilligCycle getBillingCycle() {
		return billingCycle;
	}

	public void setBillingCycle(BilligCycle billingCycle) {
		this.billingCycle = billingCycle;
	}

	public String getBanner() {
		return banner;
	}

	public void setBanner(String banner) {
		this.banner = banner;
	}

	public String to_string(){
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getName()+"{");
		sb.append("id="+this.id);
		sb.append(" name="+this.name);
		sb.append(" orgId="+this.orgId);
		sb.append(" maxLimit="+this.maxLimit);
		sb.append(" billingCycle="+this.billingCycle.toString());
		sb.append(" banner=" + this.banner.toString());
		return sb.toString();
	}

}
