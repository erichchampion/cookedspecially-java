/**
 * 
 */
package com.cookedspecially.dao;

import java.util.List;

import com.cookedspecially.domain.SeatingTable;

/**
 * @author shashank
 *
 */
public interface SeatingTableDAO {

	public void addSeatingTable(SeatingTable seatingTable);
	public List<SeatingTable> listSeatingTableByUser(Integer userId);
	public List<SeatingTable> listRestaurantSeatingTables(Integer restaurantId);
	public void removeSeatingTable(Integer id) throws Exception;
	public SeatingTable getSeatingTable(Integer id);

}
