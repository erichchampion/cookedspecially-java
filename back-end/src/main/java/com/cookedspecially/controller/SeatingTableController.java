/**
 * 
 */
package com.cookedspecially.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import springfox.documentation.annotations.ApiIgnore;

import com.cookedspecially.domain.SeatingTable;
import com.cookedspecially.domain.SeatingTables;
import com.cookedspecially.domain.User;
import com.cookedspecially.enums.table.Status;
import com.cookedspecially.service.SeatingTableService;
import com.cookedspecially.service.UserService;

/**
 * @author shashank
 *
 */
@Controller
@RequestMapping("/seatingTable")
@ApiIgnore
public class SeatingTableController {

	@Autowired
	private SeatingTableService seatingTableService;
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("/")
	@ApiIgnore
	public String listSeatingTable(Map<String, Object> map, HttpServletRequest request) {

		map.put("seatingTable", new SeatingTable());
		map.put("seatingTableList", seatingTableService.listSeatingTableByUser((Integer) request.getSession().getAttribute("restaurantId")));
		map.put("statusTypes", Status.values());
		return "seatingTable";
	}

	@RequestMapping("/edit/{seatingTableId}")
	@ApiIgnore
	public String editSeatingTable(Map<String, Object> map, HttpServletRequest request, @PathVariable("seatingTableId") Integer seatingTableId) {

		map.put("seatingTable", seatingTableService.getSeatingTable(seatingTableId));
		map.put("seatingTableList", seatingTableService.listSeatingTableByUser((Integer) request.getSession().getAttribute("restaurantId")));
		map.put("statusTypes", Status.values());
		return "seatingTable";
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiIgnore
	public String addSeatingTable(Map<String, Object> map, @ModelAttribute("seatingTable") SeatingTable seatingTable ) {
		seatingTable.setRestaurantId(seatingTable.getUserId());
		seatingTableService.addSeatingTable(seatingTable);
		return "redirect:/seatingTable/";
	}

	@RequestMapping(value = "/setStatus")
	public @ResponseBody SeatingTable setSeatingTableStatus(HttpServletRequest request, HttpServletResponse response, @RequestParam String tableId, @RequestParam String status) {
		Integer tableID = Integer.parseInt(tableId);
		//String statusStr = request.getParameter("status");
		Status statuS = Status.valueOf(Status.class, status);
		SeatingTable table = seatingTableService.getSeatingTable(tableID);
		table.setStatus(statuS);
		seatingTableService.addSeatingTable(table);
		return table;
	}
	
	@RequestMapping("/delete/{seatingTableId}")
	@ApiIgnore
	public String deleteSeatingTable(Map<String, Object> map, HttpServletRequest request, @PathVariable("seatingTableId") Integer seatingTableId) {

		try {
			seatingTableService.removeSeatingTable(seatingTableId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listSeatingTable(map, request);
	}
	
	@RequestMapping("/gettablesjsonbyuname")
	public @ResponseBody SeatingTables getTablesJsonByUsername(HttpServletRequest request, HttpServletResponse response, @RequestParam String username){
		//String username = request.getParameter("username");
		SeatingTables tables = new SeatingTables();
		
		User user = userService.getUserByUsername(username);
		if (user == null) {
			return tables;
		}
		List<SeatingTable> seatingTables = seatingTableService.listSeatingTableByUser(user.getUserId());
		
		tables.setTables(seatingTables);
		return tables;
	}

	@RequestMapping(value = "/getRestaurantTables.json", method = RequestMethod.GET)
	public @ResponseBody SeatingTables getRestaurantTablesJson(HttpServletRequest request, HttpServletResponse response,@RequestParam String restaurantId){
		Integer restaurantID = Integer.parseInt(restaurantId);
		SeatingTables tables = new SeatingTables();
		
		List<SeatingTable> seatingTables = seatingTableService.listRestaurantSeatingTables(restaurantID);
		
		tables.setTables(seatingTables);
		return tables;
	}

}
