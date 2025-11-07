/**
 * 
 */
package com.cookedspecially.service;

import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import com.cookedspecially.domain.Restaurant;
import com.cookedspecially.domain.Role;
import com.cookedspecially.domain.User;

/**
 * @author abhishek
 *
 */
public interface UserService {

	public void addUser(User user);
	public User getUser(Integer userId);
	public User getUserByUsername(String username);
	public void updateUser(User user);
	
	public List<Role> getUserRole();
	public boolean isValidUser(User user, String password);
	
	public String getHash(String password);
	
	public List<User> listUserByRestaurant(Integer restaurantId);
	public List<User> listUserByOrg(Integer resturantId);
	public List<User> listUserByFulfillmentcenter(Integer fulfillmentcenterId);
	public List<User> listDeliveryBoy(List<Integer>  fulfillmentcenterId);
	public void removeUser(Integer userId);
	public List<User> listUserByRole(Integer fulFillmentCenterId, int roleId);
	public void updatePassword(User user);
	String getSalt();
	public TreeMap<String, Object> getEmployeeDetails(Integer userId);
	void createServiceUser(Restaurant restaurant);
	public org.springframework.security.core.userdetails.User loadUserByUsername(String username, String password) throws Exception;
	public String saleRegisterHandover(String username, String password) throws Exception;
    public boolean hasRole(String role);

}
