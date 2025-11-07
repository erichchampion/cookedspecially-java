package com.cookedspecially.dao;

import java.util.List;

import com.cookedspecially.domain.KitchenScreen;

public interface KitchenScreenDAO {

	public void addKitchenScreen(KitchenScreen kitchenScreen);
	public void removeKitchenScreen(int restaurantId);
	public List<KitchenScreen> getKitchenScreens(int restaurantId);
	public KitchenScreen getKitchenScreen(int id);

}
