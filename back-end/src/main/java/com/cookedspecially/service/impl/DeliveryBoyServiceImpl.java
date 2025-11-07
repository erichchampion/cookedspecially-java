package com.cookedspecially.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cookedspecially.dao.DeliveryBoyDAO;
import com.cookedspecially.domain.DeliveryBoy;
import com.cookedspecially.service.DeliveryBoyService;

/**
 * @author Rahul
 *
 */
@Service
public class DeliveryBoyServiceImpl implements DeliveryBoyService{

	@Autowired
	private DeliveryBoyDAO deliveryBoyDAO;
	
	@Override
	@Transactional
	public void addDeliveryBoy(DeliveryBoy deliveryBoy) {
		deliveryBoyDAO.addDeliveryBoy(deliveryBoy);
		// TODO Auto-generated method stub
		
	}

	@Override
	@Transactional
	public List<DeliveryBoy> listDeliveryBoys() {
		// TODO Auto-generated method stub
		return deliveryBoyDAO.listDeliveryBoy();
	}

	@Override
	@Transactional
	public void removeDeliveryBoy(Integer id) {
		// TODO Auto-generated method stub
		deliveryBoyDAO.removeDeliveryBoy(id);
		
	}

	@Override
	@Transactional
	public DeliveryBoy getDeliveryBoy(Integer id) {
		// TODO Auto-generated method stub
		return deliveryBoyDAO.getDeliveryBoy(id);
	}

	@Override
	@Transactional
	public List<DeliveryBoy> listDeliveryBoyByUser(Integer userId) {
		// TODO Auto-generated method stub
		return deliveryBoyDAO.listDeliveryBoyByUser(userId);
	}

}
