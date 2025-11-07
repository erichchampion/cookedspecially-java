package com.cookedspecially.service;

import java.util.List;

import com.cookedspecially.domain.FulfillmentCenter;
import com.cookedspecially.domain.MicroKitchenScreen;

public interface MicroKitchenScreenService {
	
	public void addMicroKitchenScreen(MicroKitchenScreen kitchenScreen);
	public void removeMicroKitchenScreen(int id);
	public List<MicroKitchenScreen> getMicroKitchenScreensByUser(int restaurantId);
	public List<MicroKitchenScreen> getMicroKitchenScreensByKitchen(int fulfillmentCenterId);
	public MicroKitchenScreen getMicroKitchenScreen(int microKitchenId);
	
}
