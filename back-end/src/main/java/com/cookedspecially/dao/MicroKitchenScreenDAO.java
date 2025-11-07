package com.cookedspecially.dao;

import java.util.List;

import com.cookedspecially.domain.FulfillmentCenter;
import com.cookedspecially.domain.MicroKitchenScreen;

public interface MicroKitchenScreenDAO {

	public void addMicroKitchenScreen(MicroKitchenScreen kitchenScreen);
	public void removeMicroKitchenScreen(int id);
	public List<MicroKitchenScreen> getMicroKitchenScreensByUser(int restaurantId);
	public List<MicroKitchenScreen> getMicroKitchenScreensByKitchen(int restaurantId);
	public MicroKitchenScreen getMicroKitchenScreen(int microKitchenId);
	

}
