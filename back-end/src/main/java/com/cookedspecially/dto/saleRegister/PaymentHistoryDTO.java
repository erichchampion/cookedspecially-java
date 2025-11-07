package com.cookedspecially.dto.saleRegister;

import java.util.ArrayList;
import java.util.List;

public class PaymentHistoryDTO {
    public List<PaymentCategoryDTO> saleSummary = new ArrayList<PaymentCategoryDTO>();
    public List<PaymentCategoryDTO> creditSummary = new ArrayList<PaymentCategoryDTO>();
}
