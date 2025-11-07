package com.cookedspecially.dto.giftCrad;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Abhishek on 5/25/2017.
 */
public class GiftCardDTO {

    public String giftCardId;

    @NotNull
    @Min(1)
    public int expiryDayCount;

    public float amount = 0;

    @NotNull
    @Min(3)
    public String category;

    public Date createdOn;

    public String status;
    public String formattedGiftCardId;
}
