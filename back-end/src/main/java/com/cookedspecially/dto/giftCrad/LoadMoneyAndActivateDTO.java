package com.cookedspecially.dto.giftCrad;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by Abhishek on 6/5/2017.
 */
public class LoadMoneyAndActivateDTO {

    @NotNull
    @Size(max = 16, min = 16)
    public String giftCardId;

    @NotNull
    @Min(10)
    public float amount;

    @NotNull
    @Size(min = 5)
    public String invoiceId;

    public Integer customerId;
    
    public String message;
}
