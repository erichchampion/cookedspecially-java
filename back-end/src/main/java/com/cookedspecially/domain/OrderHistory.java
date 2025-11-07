package com.cookedspecially.domain;

import java.io.Serializable;
import java.util.List;

public class OrderHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    
	public Integer  totalOrders=0;
	
	private String invoiceLinkPrefix;
	
	private List<Check> OrdersDetail;
	
	private List<CheckSummery> OrdersSummary;

	public String getInvoiceLinkPrefix() {
		return invoiceLinkPrefix;
	}

	public void setInvoiceLinkPrefix(String invoiceLinkPrefix) {
		this.invoiceLinkPrefix = invoiceLinkPrefix;
	}
	
	public List<CheckSummery> getOrdersSummary() {
		return OrdersSummary;
	}

	public void setOrdersSummary(List<CheckSummery> ordersSummary) {
		OrdersSummary = ordersSummary;
	}

	public List<Check> getOrdersDetail() {
		return OrdersDetail;
	}

	public void setOrdersDetail(List<Check> ordersDetail) {
		OrdersDetail = ordersDetail;
	}

	
	public Integer getTotalOrders() {
		return totalOrders;
	}

	public void setTotalOrders(Integer totalOrders) {
		this.totalOrders = totalOrders;
	}

}
