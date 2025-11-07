/**
 * 
 */
package com.cookedspecially.service.impl;

import com.cookedspecially.config.CountryCodes;
import com.cookedspecially.dao.UserDAO;
import com.cookedspecially.domain.*;
import com.cookedspecially.service.*;
import com.cookedspecially.utility.CustomePasswordEncoder;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author Abhishek 
 *
 */
@Service
public class UserServiceImpl implements UserService {

	final static Logger logger = Logger.getLogger(UserService.class);
	private static String salt = CustomePasswordEncoder.getSalt();
	private static PasswordEncoder encoder = new ShaPasswordEncoder();
	@Autowired
	private UserDAO userDao;

	@Autowired
	private RestaurantService resturantService;

	@Autowired
	private FulfillmentCenterService fulfillmentCenterService;

	@Autowired
	private MicroKitchenScreenService microKitchenScreenService;

	@Autowired
	private RestaurantService restarantService;

	@Autowired
	private TaxTypeService taxTypeService;

	@Override
	@Transactional
	public void addUser(User user) {
		userDao.saveUser(user);
	}

	@Override
	@Transactional
	public User getUser(Integer userId) {
		return updateUserDetails(userDao.getUser(userId));
	}

	@Override
	@Transactional
	public User getUserByUsername(String username) {
		return updateUserDetails(userDao.getUserByUsername(username));
	}

	@Override
	@Transactional
	public boolean isValidUser(User user, String password) {
		if (password!=null && !password.isEmpty() && password.length()<36 && encoder.isPasswordValid(user.getPasswordHash(), password, salt))
			return true;
		else if(password!=null && !password.isEmpty() && password.length()==40 && (user.getPasswordHash().equals(password)) )
			return true;
		else	
			return false;
	}

	public String getHash(String password) {
		return encoder.encodePassword(password, salt);
	}


	@Override
	@Transactional
	public List<Role> getUserRole() {
		return userDao.getUserRole();
	}

	@Override
	@Transactional
	public List<User> listUserByOrg(Integer orgId) {
		List<User> userList = new ArrayList<>();
		userList.addAll(userDao.listUserByOrg(orgId));
		return userList;
	}

	@Override
	@Transactional
	public void removeUser(Integer userId) {
		userDao.removeUser(userId);
	}

	@Override
	@Transactional
	public List<User> listUserByRole(Integer restaurantId, int roleId) {
		List<User> list = new ArrayList<>();
		List<User> allUserList = listUserByRestaurant(restaurantId);
		for (User user : allUserList) {
			if (user.getRole().getId() == roleId) {
				list.add(user);
			}
		}
		return list;
	}

	@Override
	@Transactional
	public List<User> listUserByFulfillmentcenter(Integer fulfillmentcenterId) {
		return userDao.listUserByFulfillmentcenter(fulfillmentcenterId);
	}

	@Override
	@Transactional
	public List<User> listDeliveryBoy(List<Integer>  fulfillmentcenterId) {
		return userDao.listDeliveryBoy(fulfillmentcenterId);
	}

	@Override
	@Transactional
	public void updateUser(User user) {
		if(user.getPasswordHash().length()<30)
			user.setPasswordHash(getHash(user.getPasswordHash()));
		userDao.updateUser(user);
	}

	@Override
	@Transactional
	public List<User> listUserByRestaurant(Integer restaurantId) {
		return userDao.listUserByRestaurant(restaurantId);
	}

	@Override
	@Transactional
	public void updatePassword(User user) {
		userDao.updatePassword(user);		
	}

	@Override
	@Transactional
	public String getSalt() {
		return salt;
	}

	private User updateUserDetails(User user){
		if(user==null)
			return null;
		try{
			if(user.getRole().getRole().equals("admin") & (user.getOrgId()==null) & !user.getRestaurantId().isEmpty())
			{
				for(Integer restaurantId : user.getRestaurantId()){
					if(restarantService.getRestaurant(restaurantId).getParentRestaurantId()==null)
					{
						user.setOrgId(restaurantId);
						userDao.saveUser(user);
					}
				}
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return user;
	}

	@Override
	@Transactional
	public TreeMap<String, Object> getEmployeeDetails(Integer userId) {
		User user=null;
		TreeMap<String, Object>  employeeDetails = new TreeMap<String, Object>();
		if((user=userDao.getUser(userId))!=null)
		{
			employeeDetails.put("userId", userId);
			employeeDetails.put("name", user.getFirstName()+" "+user.getLastName());
			employeeDetails.put("role", user.getRole().getRole());
			employeeDetails.put("username",user.getUserName());
			employeeDetails.put("orgId", user.getOrgId());
			employeeDetails.put("orgName", restarantService.getRestaurant(user.getOrgId()).getRestaurantName());
			switch (user.getRole().getRole()) {
			case "admin":
			case "Call_Center_Associate" :
				employeeDetails.putAll(getOrganizationDetails(user.getOrgId()));
				break;
			case "restaurantManager":
				ArrayList<Object> restaurantList=new ArrayList<>();
				for (int restaurantId : user.getRestaurantId()) {
					restaurantList.add(getRestaurantDetails(restaurantId));
				}
				employeeDetails.put("restaurantList", restaurantList);
				break;
			case "fulfillmentCenterManager":
			case "deliveryManager":
			case "headChef" :
				List<Integer> ffcList=user.getKitchenId();
				TreeMap<Integer, Set<Integer>> rest = new TreeMap<Integer, Set<Integer>>();
				for(Integer ffc : ffcList){
					if(rest.isEmpty() || !rest.containsKey(fulfillmentCenterService.getKitchenScreen(ffc).getRestaurantId())){
						Set<Integer> ffcset = new HashSet<Integer>(Arrays.asList(ffc));
						rest.put(fulfillmentCenterService.getKitchenScreen(ffc).getRestaurantId(), ffcset);
					}
					else
					{
						Set<Integer> ffcList1=rest.get(fulfillmentCenterService.getKitchenScreen(ffc).getRestaurantId());
						ffcList1.add(ffc);
						rest.put(fulfillmentCenterService.getKitchenScreen(ffc).getRestaurantId(), ffcList1);
					}
				}
				ArrayList<Object> restaurantList1=new ArrayList<>();
				for (Entry<Integer, Set<Integer>> entry : rest.entrySet()) {
					TreeMap<String, Object> l=new TreeMap<String, Object>();
					Restaurant restI= restarantService.getRestaurant(entry.getKey());
					l.put("restaurantId", entry.getKey());
					l.put("restaurantName",restI.getRestaurantName());
					l.put("city", restI.getCity());
					l.put("taxList",taxTypeService.listTaxTypesByRestaurantId(entry.getKey()));
					CountryCodes cc   =  new CountryCodes();
					String[] locales = Locale.getISOCountries();
					for (String countryCode : locales) {
					    Locale obj = new Locale("", countryCode);
					  if(obj.getDisplayCountry(Locale.ENGLISH).equalsIgnoreCase(restI.getCountry())){
						l.put("countryCode",cc.country2phone.get(obj.getCountry()));
					  }
					 }
					List<TreeMap<String, Object>> fulfillmentcenterList=new ArrayList<>();
					for(Integer ffcId: entry.getValue()){
						fulfillmentcenterList.add(getFulfillmentcenterDetails(ffcId));  
					}
					l.put("fulfillmentCenterList", fulfillmentcenterList);
					restaurantList1.add(l);
				}
				employeeDetails.put("restaurantList", restaurantList1);
				break;	
			case "microKitchenManager":
				break;
			}
		}
		return employeeDetails;
	}

	@SuppressWarnings("deprecation")
	@Override
	@Transactional
	public org.springframework.security.core.userdetails.User loadUserByUsername(String username, String password) throws Exception{
		if(isValidUser(getUserByUsername(username), password)){
			User user=getUserByUsername(username);
			return (new org.springframework.security.core.userdetails.User(username, user.getPasswordHash(), true, true, true, true, Arrays.asList(new SimpleGrantedAuthority("ROLE_"+user.getRole().getRole()))));
		}
		else 
			throw new Exception("Invalid UserName and Password!");
	}
	@SuppressWarnings("deprecation")
	@Override
	@Transactional
	public String saleRegisterHandover(String username, String password) throws Exception{
		UserDetails userDetails=loadUserByUsername(username, password);
		Authentication userToken=new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(userToken);
		return "redirect:/deliveryDashboard.jsp";
	}
	private TreeMap<String, Object> getOrganizationDetails(int organizationId){
		TreeMap<String, Object>  resturantDetails = new TreeMap<String, Object>();
		Restaurant restaurant=restarantService.getRestaurant(organizationId);
		if(restaurant!=null)
		{
			resturantDetails.put("orgId", organizationId);
			resturantDetails.put("orgName", restaurant.getRestaurantName());
			List<TreeMap<String, Object>> restaurantList=new ArrayList<>();
			for (Restaurant restaurantObj : restarantService.listRestaurantByParentId(organizationId)) {
				restaurantList.add(getRestaurantDetails(restaurantObj.getRestaurantId()));
			}
			resturantDetails.put("restaurantList", restaurantList);
		}
		return resturantDetails;
	}
	private TreeMap<String, Object> getRestaurantDetails(int restaurantId){
		TreeMap<String, Object>  resturantDetails = new TreeMap<String, Object>();
		Restaurant restaurant=restarantService.getRestaurant(restaurantId);
		if(restaurant!=null)
		{
			resturantDetails.put("restaurantId", restaurantId);
			resturantDetails.put("restaurantName", restaurant.getRestaurantName());
			resturantDetails.put("city", restaurant.getCity());
			List<TaxType> taxList = taxTypeService.listTaxTypesByRestaurantId(restaurant.getRestaurantId());
			resturantDetails.put("taxList", taxList);
			resturantDetails.put("timeZone", restaurant.getTimeZone());
			CountryCodes cc   =  new CountryCodes();
			String[] locales = Locale.getISOCountries();
			for (String countryCode : locales) {
			    Locale obj = new Locale("", countryCode);
			  if(obj.getDisplayCountry(Locale.ENGLISH).equalsIgnoreCase(restaurant.getCountry())){
				  resturantDetails.put("countryCode",cc.country2phone.get(obj.getCountry()));
			  }
			 }
			List<TreeMap<String, Object>> fulfillmentcenterList=new ArrayList<>();
			for (FulfillmentCenter fulfillmentcenter : fulfillmentCenterService.getKitchenScreens(restaurantId)) {
				fulfillmentcenterList.add(getFulfillmentcenterDetails(fulfillmentcenter.getId()));
			}
			resturantDetails.put("fulfillmentCenterList", fulfillmentcenterList);
		}
		return resturantDetails;
	}
	private TreeMap<String, Object> getFulfillmentcenterDetails(int fulfillmentcenterId){
		TreeMap<String, Object>  fulfillmentcenterDetails = new TreeMap<String, Object>();
		FulfillmentCenter fulfillmentcenter=fulfillmentCenterService.getKitchenScreen(fulfillmentcenterId);
		if(fulfillmentcenter!=null)
		{
			fulfillmentcenterDetails.put("fulfillmentCenterId", fulfillmentcenterId);
			fulfillmentcenterDetails.put("fulfillmentCenterName", fulfillmentcenter.getName());
		}
		return fulfillmentcenterDetails;
	}
	@Override
	@Transactional
	public void createServiceUser(Restaurant restaurant) {
		User user=new User();
		List<Integer> restaurantId =new ArrayList<>();
		if(userDao.getUserByUsername(restaurant.getRestaurantName())==null)
		{
			String serviceUser=StringUtils.join(restaurant.getRestaurantName().split("\\s+"), "_");
			user.setUserName(serviceUser);
			user.setFirstName(restaurant.getBussinessName());
			user.setContact(restaurant.getBussinessPhoneNo());
			Role role=new Role();
			role.setId(2);
			user.setRole(role);
			user.setPasswordHash(getHash("password"));
			restaurantId.add(restaurant.getRestaurantId());
			user.setRestaurantId(restaurantId);
			user.setOrgId(restaurant.getParentRestaurantId());
			userDao.updateUser(user);
		}
	}
	public boolean hasRole(String role) {
		String rolePrefix = "ROLE_";
		SecurityContext context = SecurityContextHolder.getContext();
		if (context == null)
			return false;
		Authentication authentication = context.getAuthentication();
		if (authentication == null)
			return false;
		role = new StringBuilder().append(rolePrefix).append(role).toString();
		for (GrantedAuthority auth : authentication.getAuthorities()) {
			if (role.equalsIgnoreCase(auth.getAuthority()))
				return true;
		}
		return false;
	}
}