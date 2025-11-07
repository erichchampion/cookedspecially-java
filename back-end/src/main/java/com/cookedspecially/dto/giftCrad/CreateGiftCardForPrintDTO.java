package com.cookedspecially.dto.giftCrad;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by Abhishek on 5/27/2017.
 */
public class CreateGiftCardForPrintDTO {

    @NotNull
    @Min(0)
    public float amount;

    @Size(max = 100)
    public String category;

    @NotNull
    @Min(1)
    @Max(365)
    public int expireAfterDays;

    @NotNull
    @Min(1)
    public int noOfCard;
}
