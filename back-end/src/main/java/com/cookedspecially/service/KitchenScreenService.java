package com.cookedspecially.service;

import java.util.List;

import com.cookedspecially.domain.KitchenScreen;

public interface KitchenScreenService {
	
	public void addKitchenScreen(KitchenScreen kitchenScreen);
	public void removeKitchenScreen(int id);
	public List<KitchenScreen> getKitchenScreens(int restaurantId);
	public KitchenScreen getKitchenScreen(int id);
	
}
