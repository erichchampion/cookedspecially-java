package com.cookedspecially.dto.credit;

import com.cookedspecially.enums.till.TransactionCategory;

public class AddCreditToCustomerAccountDTO {

	public int orgId;
	public int customerId;
	public float amount;
    public String invoiceId;
    public String remark;
	public TransactionCategory transactionType;
}
