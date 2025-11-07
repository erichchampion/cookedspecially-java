package com.cookedspecially.dto;

import java.util.List;
import java.util.Map;

public class OrganizationDTO {
    public Integer organizationId;
    public String organizationName;
    public List<RestaurantDTO> restaurants;
    public Map properties;
}

class RestaurantDTO{
    public Integer restaurantId;
    public String restaurantName;
    public List<FulfillmentCenterDTO> fulfillmentCenters;
    public Map properties;
}

class FulfillmentCenterDTO{
    public Integer fulfillmentCenterId;
    public String fulfillmentCenterName;
    public Map properties;
}