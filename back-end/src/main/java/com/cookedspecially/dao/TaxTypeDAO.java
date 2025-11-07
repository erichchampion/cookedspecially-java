/**
 * 
 */
package com.cookedspecially.dao;

import java.util.List;

import com.cookedspecially.domain.DishType;
import com.cookedspecially.domain.TaxType;

/**
 * @author shashank
 *
 */
public interface TaxTypeDAO {
	public void addTaxType(TaxType dishType);
	public List<TaxType> listTaxTypes();
	public List<TaxType> listTaxTypesByRestaurantId(Integer restaurantId);
	public List<TaxType> listAllActiveInactiveTaxesByRestaurantId(Integer restaurantId);
	public void removeTaxType(Integer id);
	public TaxType getTaxType(Integer id);
	public TaxType getTaxTypeByName(String name, Integer restId);
}
