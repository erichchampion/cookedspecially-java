package com.cookedspecially.dao;

import java.util.List;

import com.cookedspecially.domain.FulfillmentCenter;

public interface FulfillmentCenterDAO {

	public void addKitchenScreen(FulfillmentCenter kitchenScreen);
	public void removeKitchenScreen(int restaurantId);
	public List<FulfillmentCenter> getKitchenScreens(int restaurantId);
	public FulfillmentCenter getKitchenScreen(int id);

}
