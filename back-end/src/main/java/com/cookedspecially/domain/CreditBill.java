package com.cookedspecially.domain;

import com.cookedspecially.enums.credit.CreditBillStatus;
import com.cookedspecially.utility.DateUtil;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author Abhishek
 *
 */
 
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="CREDIT_BILLS")
public class CreditBill implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
	@GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(name = "billId", unique = true)
	private String billId;
    
	@Column(name="amount")
	private float amount;
	
	@Column(name="date")
	private Timestamp date;
	
	@Column(name="customerId")
	private int customerId;

	@Enumerated(EnumType.STRING)
	@Column(name="status")
	private CreditBillStatus status;
	
	@Column(name="updatedDate")
	private Timestamp updatedOn;
	
	public CreditBill() {
		super();
	}

	public CreditBill(float amount, int customerId) {
		super();
		this.amount = amount;
		this.customerId = customerId;
		this.date=DateUtil.getCurrentTimestampInGMT();
		this.status=CreditBillStatus.NEW;
	}

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

	public String getBillId() {
		return billId;
	}

	public void setBillId(String billId) {
		this.billId = billId;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public CreditBillStatus getStatus() {
		return status;
	}

	public void setStatus(CreditBillStatus status) {
		this.status = status;
	}

	public Timestamp getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Timestamp updatedOn) {
		this.updatedOn = updatedOn;
	}

		
}
