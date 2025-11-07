/**
 * 
 */
package com.cookedspecially.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cookedspecially.dao.SeatingTableDAO;
import com.cookedspecially.domain.SeatingTable;
import com.cookedspecially.service.SeatingTableService;

/**
 * @author shashank
 *
 */
@Service
public class SeatingTableServiceImpl implements SeatingTableService {

	@Autowired
	private SeatingTableDAO seatingTableDAO;
	
	@Override
	@Transactional
	public void addSeatingTable(SeatingTable seatingTable) {
		seatingTableDAO.addSeatingTable(seatingTable);
	}

	
	@Override
	@Transactional
	public List<SeatingTable> listSeatingTableByUser(Integer userId) {
		return seatingTableDAO.listSeatingTableByUser(userId);
	}

	
	@Override
	@Transactional
	public void removeSeatingTable(Integer id) throws Exception {
		seatingTableDAO.removeSeatingTable(id);

	}


	@Override
	@Transactional
	public SeatingTable getSeatingTable(Integer id) {
		return seatingTableDAO.getSeatingTable(id);
	}


	@Override
	@Transactional
	public List<SeatingTable> listRestaurantSeatingTables(Integer restaurantId) {
		return seatingTableDAO.listRestaurantSeatingTables(restaurantId);
	}

}
