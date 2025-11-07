/**
 * 
 */
package com.cookedspecially.controller;

import io.swagger.annotations.Api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import springfox.documentation.annotations.ApiIgnore;

import com.cookedspecially.config.CSConstants;
import com.cookedspecially.domain.AddOnDish;
import com.cookedspecially.domain.AddOnWrapper;
import com.cookedspecially.domain.Dish;
import com.cookedspecially.domain.Menu;
import com.cookedspecially.domain.MenuWrapper;
import com.cookedspecially.domain.Menus;
import com.cookedspecially.domain.Restaurant;
import com.cookedspecially.domain.Section;
import com.cookedspecially.domain.StockManagement;
import com.cookedspecially.dto.ResponseDTO;
import com.cookedspecially.enums.Status;
import com.cookedspecially.service.AddOnDishService;
import com.cookedspecially.service.AddOnDishTypeService;
import com.cookedspecially.service.DishAddOnService;
import com.cookedspecially.service.DishService;
import com.cookedspecially.service.MenuService;
import com.cookedspecially.service.RestaurantService;
import com.cookedspecially.service.SectionService;
import com.cookedspecially.service.StockManagementService;
import com.cookedspecially.service.ZomatoService;
import com.cookedspecially.utility.ImageUtility;
import com.cookedspecially.utility.StringUtility;

/**
 * @author sagarwal, rahul
 * 
 */
@Controller
@RequestMapping("/menu") 
@Api(description="Menu REST API's")
public class MenuController {
	
	//final static Logger logger = Logger.getLogger(OrganizationController.class);
	
	@Autowired
	private MenuService menuService;
	
	@Autowired
	private DishService dishService;
	
	@Autowired
	private DishAddOnService dishAddOnService;
	@Autowired
	private  AddOnDishTypeService addOnDishTypeService;
	
	@Autowired
	private StockManagementService stockManagementService;
	
	@Autowired
	private SectionService sectionService;
	
	@Autowired
	private RestaurantService restService;
	
	@Autowired
	private AddOnDishService addOnDishService; 
	
	@Autowired
	private ZomatoService zomatoService;
	
	
	
	@RequestMapping("/")
	@ApiIgnore
	public String listMenus(Map<String, Object> map, HttpServletRequest request) {

		//map.put("menu", new Menu());
		List<Menu> menus = menuService.listMenuByRestaurant((Integer) request.getSession().getAttribute("restaurantId"));
		map.put("menuList", menus);
		//map.put("dishList", dishService.listDishByUser((Integer) request.getSession().getAttribute("userId")));
		return "listMenu";
	}

		
	@RequestMapping(value="/create", method = RequestMethod.GET)
	@ApiIgnore
	public String createMenu(Map<String, Object> map, HttpServletRequest request) {

		/*Here we're only creating menu not saving sections,items to it*/
		map.put("menu", new Menu());
		map.put("statusTypes", Status.values());
		map.put("dishList", dishService.listDishByResaurant((Integer) request.getSession().getAttribute("restaurantId")));
		return "newMenu";
	}
	@RequestMapping("/edit/{menuId}")
	@ApiIgnore
	public String editMenu(Map<String, Object> map, HttpServletRequest request, @PathVariable("menuId") Integer menuId) {

		/*Here we're populating menu to edit  */
		Menu menu = menuService.getMenu(menuId);
		map.put("menu", menu);
		List<Section> sec =  menu.getSections();
		map.put("statusTypes", Status.values());
		map.put("dishList", dishService.listDishByResaurant((Integer) request.getSession().getAttribute("restaurantId")));
		return "newMenu";
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiIgnore
	public String addMenu(@ModelAttribute("menu")
	Menu menu, BindingResult result, @RequestParam("dishIds") Integer[] dishIds , HttpServletRequest request) {
		
		ArrayList<Dish> dishes = new ArrayList<Dish>();
		if (dishIds != null && dishIds.length > 0) {
			List<Dish> dishesArr = dishService.getDishes(dishIds);
			HashMap<Integer, Dish> dishesMap = new HashMap<Integer, Dish>();
			
			if (dishesArr != null) {
				for (Dish dish: dishesArr) {
					dishesMap.put(dish.getDishId(), dish);
				}
			}
			for (Integer dishId : dishIds) {
				Dish dish = dishesMap.get(dishId);
				if (dish != null) {
					dishes.add(dish);
				}
			}
		}
		//menu.setDishes(dishes);
		menuService.addMenu(menu);

		return "redirect:/menu/";
	}
 
	@RequestMapping(value = "/addNew", method = RequestMethod.POST)
	@ApiIgnore
	public String addNewMenu(@ModelAttribute("menu")
	Menu menu, BindingResult result, @RequestParam("file") MultipartFile file , HttpServletRequest request) {
		FileOutputStream fos = null;
		String fileUrl = menu.getImageUrl();
		Menu oldMenu = null;
		Integer globalMenuId = menu.getMenuId();
		if(menu.getMenuId()!=null){
			oldMenu = menuService.getMenu(menu.getMenuId());
		}
		if (!file.isEmpty()) {
            try {
				byte[] bytes = file.getBytes();
				String fileDir = File.separator + "static" + File.separator + menu.getRestaurantId() + File.separator ;
				fileUrl = fileDir + menu.getMenuId() + "_menu_" + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9_.]", "_");
				File dir = new File("webapps" + fileDir);
				if (!dir.exists()) { 
					dir.mkdirs();
				}
				File outfile = new File("webapps" + fileUrl); 
				fos = new FileOutputStream(outfile);
				fos.write(bytes);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
            // store the bytes somewhere
           //return "uploadSuccess";
       } else {
           //return "uploadFailure";
       }
		if (!fileUrl.equals(menu.getImageUrl()) && menu.getImageUrl().startsWith("/")) {
			File oldFile = new File("webapps" + menu.getImageUrl());
			if (oldFile.exists()) {
				oldFile.delete();
			}
		}
		menu.setImageUrl(fileUrl);
		menu.setRestaurantId(menu.getRestaurantId());
		Set<String> dishIds = new HashSet<String>();
		List<Section> menuSections = menu.getSections();
		TreeMap<Integer, Section> sectionTree = new TreeMap<Integer, Section>();
		List<Integer> removedSections = new ArrayList<Integer>();
		if (menuSections != null && menuSections.size() > 0) {
			for (Section menuSection : menuSections) {
				if (menuSection.isValid() && !StringUtility.isNullOrEmpty(menuSection.getDishIds())) {
					sectionTree.put(menuSection.getPosition(), menuSection);
					String[] dishIdsStrArr = menuSection.getDishIds().split(CSConstants.COMMA);
					if (dishIdsStrArr != null) {
						dishIds.addAll(Arrays.asList(dishIdsStrArr));
					}
				}
				if (!menuSection.isValid() && menuSection.getSectionId() != null && menuSection.getSectionId() > 0) {
					removedSections.add(menuSection.getSectionId());
				}
			}
		}
		Integer[] dishIdsArr = new Integer[dishIds.size()];
		
		HashMap<Integer, Dish> dishMap = new HashMap<Integer, Dish>();
		int dishCounter = 0;
		for (String dishId: dishIds) {
			if(dishId.equalsIgnoreCase("")){
				continue;
			}
			dishIdsArr[dishCounter++] = Integer.parseInt(dishId);
		}
		
		if (dishIds.size() > 0) {
			List<Dish> dishes = dishService.getDishes(dishIdsArr);
			
			for (Dish dish : dishes) {
				dishMap.put(dish.getDishId(), dish);
			}	
		}
		
		ArrayList<Section> finalSections = new ArrayList<Section>();
		for (Integer key: sectionTree.keySet()) {
			finalSections.add(sectionTree.get(key));
		}
		//Iterator<Entry<Integer, Section>> sectionIterator = sectionTree.entrySet().iterator();
		//while(sectionIterator.hasNext()) {
		//	finalSections.add(sectionIterator.next().getValue());
		//}
		for (Section section : finalSections) {
			ArrayList<Dish> finalDishes = null;
			if (!StringUtility.isNullOrEmpty(section.getDishIds())) {
				String[] dishIdsStrArr = section.getDishIds().split(CSConstants.COMMA);
				if (dishIdsStrArr != null) {
					finalDishes = new ArrayList<Dish>();
					for (int i = 0; i < dishIdsStrArr.length; i++) {
						if(dishIdsStrArr[i].equalsIgnoreCase("")){
							continue;
						}
						finalDishes.add(dishMap.get(Integer.parseInt(dishIdsStrArr[i])));
					} 
				}
			}
			section.setDishes(finalDishes);
			//sectionService.addSection(section);
		}
		menu.setSections(finalSections);
		
		Integer menuId = menu.getMenuId();
		if (menuId != null && menuId > 0) {
			menu.setModifiedTime(new Date());
		} 
		if(((oldMenu!=null && oldMenu.getZomatoStatus()==null) || oldMenu==null || oldMenu.getZomatoStatus()==Status.INACTIVE) && menu.getZomatoStatus()==Status.ACTIVE){
			if(!menu.isPosVisible() && menu.getZomatoStatus()==Status.ACTIVE){
				try {
					// = menuService.getMenu(menu.getMenuId());
					menuService.addMenu(menu);
					Menu menuToSend = menuService.getMenuByMenuName(menu.getName(),menu.getRestaurantId());
					menuToSend.setZomatoStatus(Status.ACTIVE);
					ResponseDTO response = zomatoService.createZomatoMenu(menuToSend,request);
					if("success".equalsIgnoreCase(response.message)){
						restService.alertMail(response.message, "Menu Added status: "+response.result, request);
					}else{
						restService.alertMail(response.message, "Menu Added status: "+response.result, request);
					}
					} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			}else if(globalMenuId!=null && oldMenu!=null){
				if(!menu.isPosVisible() && ((oldMenu.getZomatoStatus()==Status.ACTIVE  && menu.getZomatoStatus()==Status.INACTIVE)|| menu.getZomatoStatus()==Status.ACTIVE)){
						try {
							ResponseDTO response = zomatoService.updateMenuWithSections(oldMenu, menu,request);
						if("success".equalsIgnoreCase(response.message)){
							restService.alertMail(response.message, "[Menu updated/deleted] status: "+response.result, request);
						}else{
							restService.alertMail(response.message, "[Menu updated/deleted] status: "+response.result, request);
						}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			}
		
		menuService.addMenu(menu);
		if (fileUrl != null && fileUrl.contains("null_")) {
			String newFileUrl = renameFileToHaveMenuId(fileUrl, menu.getMenuId());
			menu.setImageUrl(newFileUrl);
			menuService.addMenu(menu);
		}
		if (removedSections.size() > 0) {
			sectionService.removeSections(removedSections);
		}
		
		return "redirect:/menu/";
	}
	
	@RequestMapping("/delete/{menuId}")
	@ApiIgnore
	public String deleteMenu(@PathVariable("menuId")
	Integer menuId, HttpServletRequest request) {

		Menu menu = menuService.getMenu(menuId);
		if (menu != null) {
			String menuImageUrl = menu.getImageUrl();
			if (!StringUtility.isNullOrEmpty(menuImageUrl) && menuImageUrl.startsWith("/")) {
				File image = new File("webapps" + menuImageUrl);
				if (image.exists()) {
					image.delete();
				}
			}
			
			if(!menu.isPosVisible() && menu.getZomatoStatus()==Status.ACTIVE){
				try {
					menu.setZomatoStatus(Status.INACTIVE);
					ResponseDTO response = zomatoService.updateZomatoMenu(menu,request);
					if("success".equalsIgnoreCase(response.message)){
						restService.alertMail(response.message, "[Menu Deleted] status: "+response.result, request);
					}else{
						restService.alertMail(response.message, "[Menu Deleted] status: "+response.result, request);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			menuService.removeMenu(menuId);
			
		}
		
		return "redirect:/menu/";
	}
	
	@RequestMapping("/show/{menuId}")
	@ApiIgnore
	public String showMenu(Map<String, Object> map, @PathVariable("menuId") Integer menuId) {
		Menu menu = menuService.getMenu(menuId);
		map.put("menu", menu);
		return "showNewMenu";
	}
	
	@RequestMapping(value="/getjsonByMenuName", method = RequestMethod.GET)
	public @ResponseBody MenuWrapper showMenuJsonByMenuName(HttpServletRequest request,Map<String, Object> map) throws JSONException {
		
		String name = request.getParameter("name");
		
		Integer restId =  Integer.parseInt(request.getParameter("restaurantId"));
		Menu menu =  menuService.getMenuByMenuName(name.trim(), restId);
		Restaurant rest =  restService.getRestaurant(restId);
		MenuWrapper menuWrapper =null;
		if(menu!=null){
		List<AddOnDish> addOn = addOnDishService.listDishByRestaurant(menu.getRestaurantId());
		List<StockManagement> stockDishes = stockManagementService.getFromStockManagement(menu.getRestaurantId());
		menuWrapper = MenuWrapper.getMenuWrapper(menu,stockDishes,rest.getTimeZone());
		menuWrapper.setAddOns(addOn);
		}
		return menuWrapper ;
	}
	
	@RequestMapping(value="/getjson/{menuId}", method = RequestMethod.GET)
	public @ResponseBody MenuWrapper showMenuJson(Map<String, Object> map, @PathVariable("menuId") Integer menuId) {
		Menu menu =  menuService.getMenu(menuId);
		List<AddOnDish> addOn = addOnDishService.listDishByRestaurant(menu.getRestaurantId()); 
		List<StockManagement> stockDishes = stockManagementService.getFromStockManagement(menu.getRestaurantId());
		Restaurant rest =  restService.getRestaurant(menu.getRestaurantId());
		MenuWrapper menuWrapper = MenuWrapper.getMenuWrapper(menu,stockDishes,rest.getTimeZone());
		menuWrapper.setAddOns(addOn);
		return menuWrapper ;
	}
	@RequestMapping(value="/getallposmenusjson/{restaurantId}" , method = RequestMethod.GET)
	public @ResponseBody Menus getallPosMenusJson(Map<String, Object> map, @PathVariable("restaurantId") Integer restaurantId, HttpServletRequest request) {
		if(restaurantId == 0)
			restaurantId = (Integer)request.getSession().getAttribute("restaurantId");

		List<StockManagement> stockDishes = stockManagementService.getFromStockManagement(restaurantId);
		Menus menus = new Menus();
		menus.setStatus(Status.ACTIVE);
		menus.setRestaurantId(restaurantId);
		//Currently restaurant Id and userId are same.
		//User user = userService.getUser(restaurantId);
		Restaurant rest=restService.getRestaurant(restaurantId);
		if (rest != null) {
			menus.setPortraitImageUrl(rest.getBusinessPortraitImageUrl());
			menus.setLandscapeImageUrl(rest.getBusinessLandscapeImageUrl());
			menus.setAppCacheIconUrl(rest.getAppCacheIconUrl());
			menus.setButtonIconUrl(rest.getButtonIconUrl());
			menus.setCurrency(rest.getCurrency());
		}
		List<Menu> menuList = menuService.allPosMenus(restaurantId, Status.ACTIVE,true);
		List<MenuWrapper> menuWrappers = new ArrayList<MenuWrapper>();
		for (Menu menu : menuList) {
			menuWrappers.add(MenuWrapper.getMenuWrapper(menu,stockDishes,rest.getTimeZone()));
		}
		menus.setMenus(menuWrappers);
		List<AddOnWrapper> addOnD =  new ArrayList<AddOnWrapper>();
		List<AddOnDish> addOn= dishAddOnService.listDishAddOnByRestaurant(restaurantId);
		for(AddOnDish ad : addOn){
			if(!ad.getDisabled()){
				addOnD.add(AddOnWrapper.getAddOnWrapper(ad));
			}
			}
		
		menus.setDishAddOn(addOnD);
		return menus;
	}
		
	@RequestMapping(value="/getallmenusjson/{restaurantId}", method=RequestMethod.GET)
	public @ResponseBody Menus showAllMenusJson(Map<String, Object> map, @PathVariable("restaurantId") Integer restaurantId, HttpServletRequest request) {
		if(restaurantId == 0)
			restaurantId = (Integer)request.getSession().getAttribute("restaurantId");

		List<StockManagement> stockDishes = stockManagementService.getFromStockManagement(restaurantId);
		
		
		Menus menus = new Menus();
		menus.setStatus(Status.ACTIVE);
		menus.setRestaurantId(restaurantId);
		//Currently restaurant Id and userId are same.
		//User user = userService.getUser(restaurantId);
		Restaurant rest=restService.getRestaurant(restaurantId);
		if (rest != null) {
			menus.setPortraitImageUrl(rest.getBusinessPortraitImageUrl());
			menus.setLandscapeImageUrl(rest.getBusinessLandscapeImageUrl());
			menus.setAppCacheIconUrl(rest.getAppCacheIconUrl());
			menus.setButtonIconUrl(rest.getButtonIconUrl());
			menus.setCurrency(rest.getCurrency());
		}
		List<Menu> menuList = menuService.allMenusByStatus(restaurantId, Status.ACTIVE);
		List<MenuWrapper> menuWrappers = new ArrayList<MenuWrapper>();
		for (Menu menu : menuList) {
			menuWrappers.add(MenuWrapper.getMenuWrapper(menu,stockDishes,rest.getTimeZone()));
		}
		menus.setMenus(menuWrappers);
		
		List<AddOnWrapper> addOnD =  new ArrayList<AddOnWrapper>();
		List<AddOnDish> addOn= dishAddOnService.listDishAddOnByRestaurant(restaurantId);
		for(AddOnDish ad : addOn){
			if(!ad.getDisabled()){
				addOnD.add(AddOnWrapper.getAddOnWrapper(ad));
			}
		}
		menus.setDishAddOn(addOnD);
		
		return menus;
	}
	
	@RequestMapping(value="/getmenusjsonbyuname", method = RequestMethod.GET)
	public @ResponseBody Menus getMenusJsonByUsername(HttpServletRequest request, HttpServletResponse response , @RequestParam String username){
		//String username = request.getParameter("username");
		Menus menus = new Menus();
		if (StringUtility.isNullOrEmpty(username)) {
			return menus;
		}
		//User user = userService.getUserByUsername(username);
		Restaurant rest=restService.getRestaurant((Integer)request.getSession().getAttribute("restaurantId"));
		if (rest== null) {
			return menus;
		}
		List<StockManagement> stockDishes = stockManagementService.getFromStockManagement((Integer)request.getSession().getAttribute("restaurantId"));
		menus.setStatus(Status.ACTIVE);
		menus.setRestaurantId((Integer)request.getSession().getAttribute("restaurantId"));
		menus.setPortraitImageUrl(rest.getBusinessPortraitImageUrl());
		menus.setLandscapeImageUrl(rest.getBusinessLandscapeImageUrl());
		menus.setAppCacheIconUrl(rest.getAppCacheIconUrl());
		menus.setButtonIconUrl(rest.getButtonIconUrl());
		menus.setCurrency(rest.getCurrency());
		List<Menu> menuList = menuService.allMenusByStatus((Integer)request.getSession().getAttribute("restaurantId"), Status.ACTIVE);
		List<MenuWrapper> menuWrappers = new ArrayList<MenuWrapper>();
		for (Menu menu : menuList) {
			menuWrappers.add(MenuWrapper.getMenuWrapper(menu,stockDishes,rest.getTimeZone()));
		}
		menus.setMenus(menuWrappers);
		List<AddOnWrapper> addOnD =  new ArrayList<AddOnWrapper>();
		List<AddOnDish> addOn= dishAddOnService.listDishAddOnByRestaurant(rest.getRestaurantId());
		for(AddOnDish ad : addOn){
			addOnD.add(AddOnWrapper.getAddOnWrapper(ad));
		}
		
		menus.setDishAddOn(addOnD);
		return menus;
	}
	
	@RequestMapping(value = "/manifest/{restaurantId}.manifest", method = RequestMethod.GET)
	public void getManifestFile(HttpServletRequest request, HttpServletResponse response, @PathVariable("restaurantId") Integer restaurantId) throws IOException {
		//User user = userService.getUser(restaurantId);
		Restaurant rest=restService.getRestaurant(restaurantId);
		if (rest != null) {
			String businessName = rest.getBussinessName();
			businessName = businessName.replaceAll("[^a-zA-Z0-9_]", "");
			List<Menu> menus = menuService.allMenusByStatus(restaurantId, Status.ACTIVE);
			HashSet<String> fileNames = new HashSet<String>();
			if (!StringUtility.isNullOrEmpty(rest.getBusinessLandscapeImageUrl())) {
				fileNames.add(rest.getBusinessLandscapeImageUrl());
			}
			if (!StringUtility.isNullOrEmpty(rest.getBusinessPortraitImageUrl())) {
				fileNames.add(rest.getBusinessPortraitImageUrl());
			}
			if (!StringUtility.isNullOrEmpty(rest.getAppCacheIconUrl())) {
				fileNames.add(rest.getAppCacheIconUrl());
			}
			if (!StringUtility.isNullOrEmpty(rest.getButtonIconUrl())) {
				fileNames.add(rest.getButtonIconUrl());
			}
			
			for (Menu menu : menus) {
				List<Section> sections = menu.getSections();
				for (Section section: sections) {
					List<Dish> dishes = section.getDishes();
					for (Dish dish : dishes) {
						String imageUrl = dish.getImageUrl();
						if (!fileNames.contains(imageUrl)) {
							fileNames.add(imageUrl);
						}
						String smallImageUrl = ImageUtility.getSmallImageUrl(imageUrl, 200, 200);
						if (!fileNames.contains(smallImageUrl)) {
							fileNames.add(smallImageUrl);
						}
					}
				}
			}
			File fileDir = new File("webapps" + File.separator + "static" + File.separator + "clients" + File.separator + "com"  + File.separator + "cookedspecially" + File.separator + businessName);
			fileDir.mkdirs();
			File file = new File("webapps" + File.separator + "static" + File.separator + "clients" + File.separator + "com"  + File.separator + "cookedspecially" + File.separator + businessName + File.separator + businessName + ".manifest");
			BufferedWriter bw = null;
			BufferedReader br = null;
			try {
				// if file doesnt exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}
	 
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				bw = new BufferedWriter(fw);
				File staticFile = new File("webapps" + File.separator + "static" + File.separator + "resources" + File.separator + "staticFile");
				FileReader fr = new FileReader(staticFile);
				br = new BufferedReader(fr);
				String line;
				while((line = br.readLine()) != null) {
					bw.write(line + "\n");
				}
				
				Iterator<String> iterFileName = fileNames.iterator();
				while(iterFileName.hasNext()) {
					bw.write(iterFileName.next() + "\n");
				}
				bw.write("\n");
				bw.write("NETWORK:\n");
				bw.write("/CookedSpecially/menu/getallmenusjson/" + restaurantId + "\n*\n\n");
				bw.write("FALLBACK:\n");
				
				bw.flush();
				
			} catch(IOException excep) {
				excep.printStackTrace();
			} finally {
				if (br != null) {
					br.close();
				}
				if (bw != null) {
					bw.close();
				}
			}
		    response.setContentType("text/cache-manifest");
		    response.setCharacterEncoding("UTF-8");
		    response.setHeader("Pragma","No-cache");     
		    response.setHeader("Cache-Control","no-cache");     
		    response.setDateHeader("Expires", 0);   
			response.setContentLength(new Long(file.length()).intValue());
	        FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
		}
	}
	
	private String renameFileToHaveMenuId(String fileUrl, Integer menuId) {
		File oldFile = new File("webapps" + fileUrl);
		String newFileUrl = fileUrl.replace("null_", menuId + "_");
		File newFile = new File("webapps" + newFileUrl);
		oldFile.renameTo(newFile);
		return newFileUrl;
	}
	boolean updateIfExistInZomatoMenu(Menu oldMenu, Menu newMenu,Dish dish,HttpServletRequest request){
		List<Menu> menuList = menuService.allMenusByStatus(dish.getRestaurantId(),Status.ACTIVE);
		boolean dishExist=false;
		for(Menu menu :menuList){
			if(!menu.isPosVisible() &&  menu.getZomatoStatus()==Status.ACTIVE){
				for(Section section : menu.getSections()){
					for(Dish dishData :  section.getDishes()){
						if(dishData.getDishId() == dish.getDishId()){
							dishExist=true;
						}
					}
				}
				if(dishExist){
					try {
						zomatoService.updateZomatoMenu(menu,request);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return dishExist;
	}
}
