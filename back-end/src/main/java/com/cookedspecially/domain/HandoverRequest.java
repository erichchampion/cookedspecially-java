package com.cookedspecially.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import com.cookedspecially.utility.DateUtil;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TILL_HANDOVER")
public class HandoverRequest implements Serializable {
    private static final long serialVersionUID = 1L;

	public enum HANDOVER_TYPE {DELETE,CLOSE,OPEN }; 
	
	@Id
	@Column(name = "handoverId")
	@GeneratedValue
	private int requestId;
		
	@Column(name="USERID")
	private Integer userId;
	
	
	@Column(name="TIME")
	private Timestamp time;
	
	@Column(name="TILLID")
	private String tillId;
	
	@Column(name="BALANCE")
	private Float balance;
	
	@Column(name="REMARKS")
	private String remarks;
	
	@Column(name="STATUS")
	private String status;

	public HandoverRequest() {
		super();
	}

	public HandoverRequest(Integer userid, String tillid, String remark, String c_status, Float balance) {
		super();
		this.userId = userid;
		this.tillId = tillid;
		this.remarks = remark;
		this.status = c_status;
		this.time=DateUtil.getCurrentTimestampInGMT();
		this.balance=balance;
	}

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public String getTillId() {
		return tillId;
	}

	public void setTillId(String tillId) {
		this.tillId = tillId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Float getBalance() {
		return balance;
	}

	public void setBalance(Float balance) {
		this.balance = balance;
	}

	
	
}
