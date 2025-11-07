package com.cookedspecially.domain;

import java.io.Serializable;
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

/**
 * @author Abhishek
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="OTP")
public class OTP implements Serializable {
    private static final long serialVersionUID = 1L;

	@Id
	@Column(name="mobileNumber")
	private String mobileNumber;
	
	@Column(name="otp")
	private int otp;
	
	@Column(name="organisationId")
	private int orgID;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="generatedTime")
	private Date generatedOn;

	
	public OTP(String mobileNumber, int otp, int orgId) {
		this.mobileNumber = mobileNumber;
		this.otp = otp;
		this.orgID=orgId;
		this.generatedOn=new Date();
	}

	public OTP() {
	}

	

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public Integer getOtp() {
		return otp;
	}

	public void setOtp(Integer otp) {
		this.otp = otp;
	}

	public Date getGeneratedOn() {
		return generatedOn;
	}

	public void setGeneratedOn(Date generatedOn) {
		this.generatedOn = generatedOn;
	}

	public int getOrgID() {
		return orgID;
	}

	public void setOrgID(int orgID) {
		this.orgID = orgID;
	}

	public void setOtp(int otp) {
		this.otp = otp;
	}

	

	
	
	
	
}
