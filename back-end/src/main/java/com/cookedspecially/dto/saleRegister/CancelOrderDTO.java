package com.cookedspecially.dto.saleRegister;

import com.cookedspecially.enums.till.TillTransactionStatus;

/**
 * Created by Abhishek on 1/10/2017.
 */
public class CancelOrderDTO {
    public String paymentType;
    public int checkId;
    public TillTransactionStatus status = TillTransactionStatus.SUCCESS;
    public boolean createCreditTransaction = true;
}
