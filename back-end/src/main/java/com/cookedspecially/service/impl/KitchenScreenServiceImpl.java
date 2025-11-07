package com.cookedspecially.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cookedspecially.dao.KitchenScreenDAO;
import com.cookedspecially.domain.KitchenScreen;
import com.cookedspecially.service.KitchenScreenService;

@Service
public class KitchenScreenServiceImpl  implements KitchenScreenService{

	@Autowired
	KitchenScreenDAO kitchenServiceDao;
	
	@Override
	@Transactional
	public void addKitchenScreen(KitchenScreen kitchenScreen) {
		// TODO Auto-generated method stub
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
	public List<KitchenScreen> getKitchenScreens(int restaurantId) {
		// TODO Auto-generated method stub
		return kitchenServiceDao.getKitchenScreens(restaurantId);
	}

	@Override
	@Transactional
	public KitchenScreen getKitchenScreen(int id) {
		// TODO Auto-generated method stub
		return kitchenServiceDao.getKitchenScreen(id);
	}

	
}
