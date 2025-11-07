package com.cookedspecially.dto.saleRegister;

import javax.validation.constraints.Min;

public class ApplyDiscount {
 public int checkId;
 public String remarks;
 
 @Min(1)
 public float discountedAmount;
}
