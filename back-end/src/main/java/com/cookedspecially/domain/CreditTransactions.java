package com.cookedspecially.domain;

import com.cookedspecially.enums.credit.CreditTransactionStatus;
import com.cookedspecially.enums.till.TransactionCategory;
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
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="CREDIT_TRANSACTION")
public class CreditTransactions implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(name = "id", unique = true)
	private String id;
	
	@Enumerated(EnumType.STRING)
	@Column(name="type")
	private TransactionCategory type;
	
	@Column(name="date")
	private Timestamp date;
	
	@Column(name="customerId")
	private int customerId;
	
	@Enumerated(EnumType.STRING)
	@Column(name="status")
	private CreditTransactionStatus status;
	
	@Column(name="remark")
	private String remark;

	@Column(name = "invoiceId")
	private String invoiceId;

	@Column(name="amount")
	private float amount;
	
	public CreditTransactions() {
		super();
	}
	
	 public CreditTransactions(TransactionCategory transactionType, int customerId,String remark, float amount) {
			super();
			this.type = transactionType;
			this.customerId = customerId;
			this.status = CreditTransactionStatus.SUCCESS;
			this.remark = remark;
			this.amount=amount;
			this.date=DateUtil.getCurrentTimestampInGMT();
		}
	 
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public TransactionCategory getType() {
		return type;
	}

	public void setType(TransactionCategory type) {
		this.type = type;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}
	public CreditTransactionStatus getStatus() {
		return status;
	}

	public void setStatus(CreditTransactionStatus status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}
	
	
}
