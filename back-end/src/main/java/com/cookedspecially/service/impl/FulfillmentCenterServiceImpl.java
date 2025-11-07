package com.cookedspecially.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cookedspecially.dao.FulfillmentCenterDAO;
import com.cookedspecially.domain.FulfillmentCenter;
import com.cookedspecially.service.FulfillmentCenterService;

@Service
public class FulfillmentCenterServiceImpl  implements FulfillmentCenterService{

	@Autowired
	FulfillmentCenterDAO kitchenServiceDao;
	
	@Override
	@Transactional
	public void addKitchenScreen(FulfillmentCenter kitchenScreen) {
		kitchenServiceDao.addKitchenScreen(kitchenScreen);	
	}

	@Override
	@Transactional
	public void removeKitchenScreen(int id) {
		// TODO Auto-generated method stub
		kitchenServiceDao.removeKitchenScreen(id);
	}

	@Override
	@Transactional
	public List<FulfillmentCenter> getKitchenScreens(int restaurantId) {
		return kitchenServiceDao.getKitchenScreens(restaurantId);
	}

	@Override
	@Transactional
	public FulfillmentCenter getKitchenScreen(int id) {
		// TODO Auto-generated method stub
		return kitchenServiceDao.getKitchenScreen(id);
	}

	
}
