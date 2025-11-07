package com.cookedspecially.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import com.cookedspecially.utility.DateUtil;

@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="CREDIT_BILL_HANDLER")
public class CreditBillHandler implements Serializable {
	private static final long serialVersionUID = 1L;
    
    @Id
	@GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(name = "id", unique = true)
	private String id;
    
    @Column(name="deliveryBoyId")
	private int deliveryBoyId;
    
    @Column(name="userId")
   	private int userId;
    
    @Column(name="creditBillId")
   	private String creditBillId;
    
    @Column(name="time")
   	private Timestamp time;
    
    

	public CreditBillHandler(int deliveryBoyId, int userId, String creditBillId) {
		super();
		this.deliveryBoyId = deliveryBoyId;
		this.userId = userId;
		this.creditBillId = creditBillId;
		this.time=DateUtil.getCurrentTimestampInGMT();
	}
	

	public CreditBillHandler() {
		super();
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getDeliveryBoyId() {
		return deliveryBoyId;
	}

	public void setDeliveryBoyId(int deliveryBoyId) {
		this.deliveryBoyId = deliveryBoyId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getCreditBillId() {
		return creditBillId;
	}

	public void setCreditBillId(String creditBillId) {
		this.creditBillId = creditBillId;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}
    
    
    
}
