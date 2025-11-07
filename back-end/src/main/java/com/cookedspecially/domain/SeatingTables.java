/**
 * 
 */
package com.cookedspecially.domain;

import java.io.Serializable;
import java.util.List;
/**
 * @author shashank
 *
 */
public class SeatingTables implements Serializable {
    private static final long serialVersionUID = 1L;
    
	List<SeatingTable> tables;

	public List<SeatingTable> getTables() {
		return tables;
	}
	public void setTables(List<SeatingTable> tables) {
		this.tables = tables;
	}	
}