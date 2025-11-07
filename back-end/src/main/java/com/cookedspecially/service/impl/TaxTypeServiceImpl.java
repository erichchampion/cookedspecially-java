/**
 * 
 */
package com.cookedspecially.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cookedspecially.dao.DishTypeDAO;
import com.cookedspecially.dao.TaxTypeDAO;
import com.cookedspecially.domain.DishType;
import com.cookedspecially.domain.TaxType;
import com.cookedspecially.service.DishTypeService;
import com.cookedspecially.service.TaxTypeService;

/**
 * @author rahul
 *
 */
@Service
public class TaxTypeServiceImpl implements TaxTypeService {

	@Autowired
	TaxTypeDAO taxTypeDAO;
	
	@Override
	@Transactional
	public void addTaxType(TaxType taxType) {
		taxTypeDAO.addTaxType(taxType);
	}

	@Override
	@Transactional
	public List<TaxType> listTaxTypes() {
		return taxTypeDAO.listTaxTypes();
	}

	@Override
	@Transactional
	public List<TaxType> listTaxTypesByRestaurantId(Integer restaurantId) {
		return taxTypeDAO.listTaxTypesByRestaurantId(restaurantId);
	}

	@Override
	@Transactional
	public void removeTaxType(Integer id) {
		taxTypeDAO.removeTaxType(id);
	}

	@Override
	@Transactional
	public TaxType getTaxType(Integer id) {
		return taxTypeDAO.getTaxType(id);
	}

	@Override
	@Transactional
	public TaxType getTaxTypeByName(String name, Integer restId) {
		// TODO Auto-generated method stub
		return taxTypeDAO.getTaxTypeByName(name, restId);
	}

	@Override
	@Transactional
	public List<TaxType> listAllActiveInactiveTaxesByRestaurantId(
			Integer restaurantId) {
		// TODO Auto-generated method stub
		return taxTypeDAO.listAllActiveInactiveTaxesByRestaurantId(restaurantId);
	}

}
