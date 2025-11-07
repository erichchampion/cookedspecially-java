package com.cookedspecially.dto.credit;

import com.cookedspecially.domain.CreditBill;

import java.util.List;

/**
 * Created by Abhishek on 11/20/2016.
 */
public class AllCreditStatements {
    public List<CreditBill> creditBills;
    public CreditStatementDTO latestStatement;
}
