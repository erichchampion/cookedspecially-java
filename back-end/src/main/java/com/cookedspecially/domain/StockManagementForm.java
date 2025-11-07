package com.cookedspecially.domain;

import java.io.Serializable;
import java.util.List;

public class StockManagementForm implements Serializable {
    private static final long serialVersionUID = 1L;
	
	private List<StockManagement> manageStock;

	public List<StockManagement> getManageStock() {
		return manageStock;
	}

	public void setManageStock(List<StockManagement> manageStock) {
		this.manageStock = manageStock;
	}

}
