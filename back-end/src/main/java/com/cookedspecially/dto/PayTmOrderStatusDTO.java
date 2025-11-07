package com.cookedspecially.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat
public class PayTmOrderStatusDTO implements Serializable {

	public String checkId;
	public String orderStatus;
	public String orderAmount;
	public String responseMsz;
}
