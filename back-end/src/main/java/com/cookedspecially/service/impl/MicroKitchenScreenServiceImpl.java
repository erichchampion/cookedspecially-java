package com.cookedspecially.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cookedspecially.dao.FulfillmentCenterDAO;
import com.cookedspecially.dao.MicroKitchenScreenDAO;
import com.cookedspecially.domain.FulfillmentCenter;
import com.cookedspecially.domain.MicroKitchenScreen;
import com.cookedspecially.service.FulfillmentCenterService;
import com.cookedspecially.service.MicroKitchenScreenService;

@Service
public class MicroKitchenScreenServiceImpl  implements MicroKitchenScreenService{

	@Autowired
	MicroKitchenScreenDAO  microKitchenScreenDAO;
	
	@Override
	@Transactional
	public void addMicroKitchenScreen(MicroKitchenScreen kitchenScreen) {
		// TODO Auto-generated method stub
		microKitchenScreenDAO.addMicroKitchenScreen(kitchenScreen);
	}

	@Override
	@Transactional
	public void removeMicroKitchenScreen(int id) {
		// TODO Auto-generated method stub
		microKitchenScreenDAO.removeMicroKitchenScreen(id);
	}

	@Override 
	@Transactional
	public List<MicroKitchenScreen> getMicroKitchenScreensByUser(int restaurantId) {
		// TODO Auto-generated method stub
		return microKitchenScreenDAO.getMicroKitchenScreensByUser(restaurantId);
	}

	@Override
	@Transactional
	public List<MicroKitchenScreen> getMicroKitchenScreensByKitchen(int fulfillmentCenterId) {
		// TODO Auto-generated method stub
		return microKitchenScreenDAO.getMicroKitchenScreensByKitchen(fulfillmentCenterId);
	}

	@Override
	@Transactional
	public MicroKitchenScreen getMicroKitchenScreen(int microKitchenId) {
		// TODO Auto-generated method stub
		return microKitchenScreenDAO.getMicroKitchenScreen(microKitchenId);
	}

}
