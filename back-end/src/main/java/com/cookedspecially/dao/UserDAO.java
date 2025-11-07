/**
 * 
 */
package com.cookedspecially.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.cookedspecially.domain.Role;
import com.cookedspecially.domain.User;

/**
 * @author sagarwal
 *
 */
public interface UserDAO {

	public void saveUser(User user);
	public User getUser(Integer userId);
	public User getUserByUsername(String username);
	public List<Role> getUserRole();
	
	public List<User> listUserByOrg(Integer OrgId);
	public List<User> listUserByRestaurant(Integer restaurantId);
	
	public List<User> listAllUserByFulfilmentCenter(Collection<Integer> ffcId);
	public List<User> listAllUserByMicroKitchen(Collection<Integer> mkId);

	public void removeUser(Integer userId);
	public List<User> listUserByFulfillmentcenter(Integer fulfillmentcenterId);
	public List<User> listDeliveryBoy(List<Integer>  fulfillmentcenterId);
	public void updateUser(User user);
	public void updatePassword(User user);
	
//	public List<User> listUserByRole(Integer fulFillmentCenterId, int roleId);
	
}
