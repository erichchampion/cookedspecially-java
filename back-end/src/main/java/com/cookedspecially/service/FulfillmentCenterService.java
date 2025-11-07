package com.cookedspecially.service;

import java.util.List;

import com.cookedspecially.domain.FulfillmentCenter;

public interface FulfillmentCenterService {
	
	public void addKitchenScreen(FulfillmentCenter kitchenScreen);
	public void removeKitchenScreen(int id);
	public List<FulfillmentCenter> getKitchenScreens(int restaurantId);
	public FulfillmentCenter getKitchenScreen(int id);
	
}
