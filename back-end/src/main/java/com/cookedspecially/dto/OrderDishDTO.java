package com.cookedspecially.dto;

import java.util.List;

import com.cookedspecially.domain.JsonAddOn;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Ankit on 8/27/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDishDTO {

    public String name;
    public String type;
    public Integer quantity;
    public Float price;
    public String instructions;
    public List<JsonAddOn> addOns;
    public Integer dishSizeId;
    public Integer itemId;
    public String smallImageUrl;
    public String itemType;
    public String dishSizeName; //to show on kitchen screens

}