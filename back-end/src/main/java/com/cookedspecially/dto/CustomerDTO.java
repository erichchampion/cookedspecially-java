package com.cookedspecially.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Ankit on 10/25/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerDTO {

    public Integer id;
    public String name;
    public String phone;
    public String email;
    public String address;
    public String deliveryArea;
    public String city;
}