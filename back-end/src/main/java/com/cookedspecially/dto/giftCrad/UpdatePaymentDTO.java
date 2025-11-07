package com.cookedspecially.dto.giftCrad;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by Abhishek on 6/3/2017.
 */
public class UpdatePaymentDTO {

    @NotNull
    @Size(min = 3)
    public String paymentStatus;
    @NotNull
    @Size(min = 3)
    public String paymentMode;
    @NotNull
    @Size(min = 16, max = 16)
    public String giftCardId;
}
