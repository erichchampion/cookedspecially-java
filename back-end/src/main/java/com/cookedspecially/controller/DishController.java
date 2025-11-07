/**
 * 
 */
package com.cookedspecially.controller;

import io.swagger.annotations.Api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import springfox.documentation.annotations.ApiIgnore;

import com.cookedspecially.domain.AddOnDish;
import com.cookedspecially.domain.Dish;
import com.cookedspecially.domain.DishAddOn;
import com.cookedspecially.domain.Dish_Size;
import com.cookedspecially.domain.FulfillmentCenter;
import com.cookedspecially.domain.JSONShareDish;
import com.cookedspecially.domain.Menu;
import com.cookedspecially.domain.NutrientInfo;
import com.cookedspecially.domain.Nutrientes;
import com.cookedspecially.domain.Restaurant;
import com.cookedspecially.domain.Section;
import com.cookedspecially.domain.StockManagement;
import com.cookedspecially.domain.StockManagementForm;
import com.cookedspecially.domain.User;
import com.cookedspecially.dto.ResponseDTO;
import com.cookedspecially.enums.Status;
import com.cookedspecially.enums.WeekDayFlags;
import com.cookedspecially.service.AddOnDishService;
import com.cookedspecially.service.DishAddOnService;
import com.cookedspecially.service.DishService;
import com.cookedspecially.service.DishTypeService;
import com.cookedspecially.service.FulfillmentCenterService;
import com.cookedspecially.service.MenuService;
import com.cookedspecially.service.MicroKitchenScreenService;
import com.cookedspecially.service.RestaurantService;
import com.cookedspecially.service.StockManagementService;
import com.cookedspecially.service.UserService;
import com.cookedspecially.service.ZomatoService;
import com.cookedspecially.utility.ImageUtility;
import com.cookedspecially.utility.MailerUtility;
import com.cookedspecially.utility.StringUtility;
/**
 * @author sagarwal
 *
 */
@Controller
@RequestMapping("/dish")
@Api(description="Dish REST API's")
public class DishController {

	@Autowired
	private DishService dishService;
	@Autowired
	private DishTypeService dishTypeService;
	
	@Autowired
	private AddOnDishService  addOnDishService;
	
	@Autowired
	private DishAddOnService dishAddOnService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RestaurantService restService;
	
	@Autowired
	private FulfillmentCenterService fulfillmentCenterService;
	
	@Autowired
	private MicroKitchenScreenService microKitchenScreenService;
	
	@Autowired
	private StockManagementService stockManagementService;
	
	@Autowired
	private ZomatoService zomatoService;
	
	@Autowired
	private MenuService menuService;
	
	private static int MAXFILESIZE=5; //in MB
	//@Autowired
	//private CategoryService categoryService;
	
	private static int HOURS[] = new int[24];
	private static int MINS[] = new int[4];
	private static boolean ALLWEEKDAYS[] = new boolean[7];
	static {
		for (int i = 0; i < 24; i++) {
			HOURS[i] = i;
		}
		for (int i = 0; i < 4; i++) {
			MINS[i] = i*15;
		}
		for (int i = 0; i < 7; i++) {
			ALLWEEKDAYS[i] = true;
		}
	}
	
	@RequestMapping("/")
	@ApiIgnore
	public String listDishes(Map<String, Object> map, HttpServletRequest request) {

		Dish dish = new Dish();
		List<DishAddOn> dishAddOn =  new ArrayList<DishAddOn>();
		
		//dish.setActiveDays(WeekDayFlags.getWeekDayVal(ALLWEEKDAYS));
		//dish.setHappyHourDays(WeekDayFlags.getWeekDayVal(ALLWEEKDAYS));
		dish.setDishActiveDays(ALLWEEKDAYS);
		dish.setHappyHourActiveDays(ALLWEEKDAYS);
		
		map.put("dish", dish);
		Integer restId = (Integer) request.getSession().getAttribute("restaurantId");
		ArrayList<AddOnDish> addOn = (ArrayList<AddOnDish>) addOnDishService.listDishByRestaurant(restId);
		ArrayList<Nutrientes> nutrients =  (ArrayList<Nutrientes>) restService.getNutirentList(restId);
		map.put("nutrients",nutrients);
		map.put("dishList", dishService.listDishByResaurant(restId));
		
		
		map.put("dishSizeList", dishTypeService.listDishSizeByRestaurantId(restId));
		map.put("addOn", addOn);
		map.put("dishAddOn",dishAddOnService.listDishAddOn(restId));
		map.put("hours", HOURS);
		map.put("mins", MINS);
		map.put("currTime", new Date().toLocaleString());
		map.put("weekdayFlags", WeekDayFlags.values());
		map.put("dishTypes", dishTypeService.listDishTypesByRestaurantId(restId));
		map.put("microScreenList",microKitchenScreenService.getMicroKitchenScreensByUser(restId));
		return "dish";
	}
	@RequestMapping("/edit/{dishId}")
	@ApiIgnore
	public String editDish(Map<String, Object> map, HttpServletRequest request, @PathVariable("dishId") Integer dishId) {
		Dish dish = dishService.getDish(dishId);
		
		dish.setDishActiveDays(WeekDayFlags.getWeekDayFlagsArr(dish.getActiveDays()));
		dish.setHappyHourActiveDays(WeekDayFlags.getWeekDayFlagsArr(dish.getHappyHourDays()));
		ArrayList<AddOnDish> addOn = (ArrayList<AddOnDish>) addOnDishService.listDishByRestaurant(dish.getRestaurantId());
		ArrayList<Nutrientes> nutrients =  (ArrayList<Nutrientes>) restService.getNutirentList(dish.getRestaurantId());
		map.put("nutrients",nutrients);
		map.put("addOn", addOn);
		map.put("dish", dish);
		//Integer userId = (Integer) request.getSession().getAttribute("userId");
		Integer restId = (Integer) request.getSession().getAttribute("restaurantId");
		map.put("dishList", dishService.listDishByResaurant(restId));
		map.put("dishSizeList", dishTypeService.listDishSizeByRestaurantId(restId));
		map.put("hours", HOURS);
		map.put("addOnList",dishAddOnService.listDishAddOnByDish(dishId));
		map.put("dishAddOn",dishAddOnService.listDishAddOn(restId));
		map.put("mins", MINS);
		map.put("weekdayFlags", WeekDayFlags.values());
		map.put("currTime", new Date().toLocaleString());
		map.put("microScreenList",microKitchenScreenService.getMicroKitchenScreensByUser((Integer) request.getSession().getAttribute("restaurantId")));
		//map.put("categoryList", categoryService.listCategoryByUser((Integer) request.getSession().getAttribute("userId")));
		map.put("dishTypes", dishTypeService.listDishTypesByRestaurantId(restId));
		return "dish";
	} 
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiIgnore
	public String addDish(Map<String, Object> map, @ModelAttribute("dish")
	Dish dish, BindingResult result, @RequestParam("file") MultipartFile squareImage, @RequestParam("file[1]") MultipartFile rectangularImage, HttpServletRequest request) throws Exception {
		FileOutputStream fos = null;
		//String fileUrl = dish.getImageUrl();
		String outFileUrl = null;
		String imageUrls= null;
		Dish lastSavedDish;
		if(dish.getDishId()!=null){
			lastSavedDish = dishService.getDish(dish.getDishId());
			dish.setRestaurantId(lastSavedDish.getRestaurantId());
			
		}
		dishService.addDish(dish);	
		ArrayList<MultipartFile> files = new ArrayList<MultipartFile>();
		List<Dish_Size> dSize= new ArrayList<Dish_Size>();
		files.add(squareImage);
		files.add(rectangularImage);
		int id = dish.getDishId();
		for(Dish_Size dishSize : dish.getDishSizeList()){
			if(dishSize.getDishId()==null || dishSize.getDishId().equals("") ){
			dishSize.setDishId(dish.getDishId());
			}
			if(dishSize.getDishSizeId()!=null){
			dSize.add(dishSize);
			}
		}
		dish.setDishSize(dSize);
		List<NutrientInfo> nInfo =  new ArrayList<NutrientInfo>();

		String[] nutrientNames = request.getParameterValues("nutrientName");
		String[] instructions= request.getParameterValues("instructions");
		HashMap<String,String> dc =  new LinkedHashMap<String, String>();
		if(nutrientNames!=null){
		for(int i=0;i<nutrientNames.length;i++){
		dc.put(nutrientNames[i],instructions[i]);
		}
		Iterator it  = dc.entrySet().iterator();
		while(it.hasNext()){
			NutrientInfo nf  =    new NutrientInfo();
			 Map.Entry pair = (Map.Entry)it.next();
		     nf.setName(pair.getKey().toString());
		     Nutrientes nutrientes =  restService.getByNutrientesByNameType(nf.getName(),dish.getDishType(),dish.getRestaurantId());
		     nf.setValue(Double.parseDouble(pair.getValue().equals("")?"0":pair.getValue().toString()));
		     nf.setDishId(dish.getDishId());
		     nf.setNutrientId(nutrientes.getId());
		     nInfo.add(nf);
		}
		dish.setNutrientInfo(nInfo);
		}
		ArrayList<AddOnDish> addOnList = new ArrayList<AddOnDish>();
		if(dish!=null){
			dishAddOnService.removeDishAddOn(id);
		}
		String addOn = dish.getAddOn();
		if(addOn!=null){
		List<String> addOnLists = Arrays.asList(addOn.split(","));
		for(String val :addOnLists){
			AddOnDish data=addOnDishService.getDish(Integer.parseInt(val));
			DishAddOn dishAdd =  new DishAddOn();
			
			dishAdd.setDishId(id);
			dishAdd.setAddOnId(Integer.parseInt(val));
			dishAdd.setAlcoholic(data.getAlcoholic());
			dishAdd.setDescription(data.getDescription());
			dishAdd.setDisabled(data.getDisabled());
			dishAdd.setDishType(data.getDishType());
			dishAdd.setDisplayPrice(data.getDisplayPrice());
			dishAdd.setImageUrl(data.getImageUrl());
			dishAdd.setName(data.getName());
			dishAdd.setPrice(data.getPrice());
			dishAdd.setShortDescription(data.getShortDescription());
			dishAdd.setVegetarian(data.getVegetarian());
			dishAdd.setAlcoholic(data.getAlcoholic());
			dishAdd.setRestaurantId(data.getRestaurantId());
			dishAddOnService.addDish(dishAdd);
		}
		}
		if(dish.getDishId()!=null){
			updateIfExistInZomatoMenu(dish,request);
		}
		
		if (files != null && files.size() == 2) {
			String[] fileUrls = new String[2];
			int iter = 0;
			for (MultipartFile file : files) {
				
				String fileUrl = null;
				if (iter==0) {
					fileUrl = dish.getImageUrl();
				} else if (iter==1) {
					fileUrl = dish.getRectangularImageUrl();
				}
				
				if (!file.isEmpty()) {
					if (file.getSize() > MAXFILESIZE*1000*1000) {
						result.rejectValue("imageUrl", "error.upload.sizeExceeded", "You cannot upload the file of more than " + MAXFILESIZE + " MB");
						map.put("dish", dish);
						map.put("dishList", dishService.listDishByResaurant(dish.getRestaurantId()));
						return "dish";
					}
		            try {
						byte[] bytes = file.getBytes();
						String fileDir = File.separator + "static" + File.separator + dish.getRestaurantId() + File.separator ;
						
						if (iter == 0)
							fileUrl = fileDir + dish.getDishId() + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9_.]", "_");
						 else if (iter == 1)
							 fileUrl = fileDir + "Rect_"+dish.getDishId() + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9_.]", "_");
						
						File dir = new File("webapps" + fileDir);
						if (!dir.exists()) { 
							dir.mkdirs();
						}
						outFileUrl = "webapps" + fileUrl;
						File outfile = new File(outFileUrl); 
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
		            if (iter !=1){
		            	if (!StringUtility.isNullOrEmpty(outFileUrl))
		            	ImageUtility.resizeImage(outFileUrl, ImageUtility.getSmallImageUrl(outFileUrl, 200, 200), ImageUtility.getFileFormat(outFileUrl)/*"jpg"*/, 200, 200);
		            }
		            // store the bytes somewhere
		           //return "uploadSuccess";
		       } else {
		           //return "uploadFailure";
		       }
				
				if (iter == 0)
					imageUrls  = dish.getImageUrl();
				 else if (iter == 1)
					imageUrls   = "Rect_"+dish.getRectangularImageUrl();
			
				if (!fileUrl.equals(imageUrls) && imageUrls.startsWith("/")) {
					File oldFile = new File("webapps" + dish.getImageUrl());
					if (oldFile.exists()) {
						oldFile.delete();
					}
					File oldSmallFile = new File("webapps" + ImageUtility.getSmallImageUrl(dish.getImageUrl(), 200, 200) );
					if (oldSmallFile.exists()) {
						oldSmallFile.delete();
					}
				}
				if (iter == 0)
					dish.setImageUrl(fileUrl);
				else if (iter == 1) 
					dish.setRectangularImageUrl(fileUrl);
					if (fileUrl.contains("null_")) {
						String newFileUrl = renameFileToHaveDishId(fileUrl, dish.getDishId());
						if (iter == 0) 
							dish.setImageUrl(newFileUrl);
						else if (iter == 1) 
							dish.setRectangularImageUrl(newFileUrl);
						dishService.addDish(dish);
						String smallFileOldUrl = ImageUtility.getSmallImageUrl(fileUrl, 200, 200);
						renameFileToHaveDishId(smallFileOldUrl, dish.getDishId());
					}
				iter++;
		}
		
			
			Integer dishId = dish.getDishId();  
			dish.setActiveDays(WeekDayFlags.getWeekDayVal(dish.getDishActiveDays()));
			dish.setHappyHourDays(WeekDayFlags.getWeekDayVal(dish.getHappyHourActiveDays()));
			dishService.addDish(dish);
			if (dishId != null && dishId > 0) {
				dishService.updateMenuModificationTime(dishId);
			} 
		}
		return "redirect:/dish/";
	}


	@RequestMapping("/delete/{dishId}")
	@ApiIgnore
	public String deleteDish(Map<String, Object> map, HttpServletRequest request, @PathVariable("dishId") Integer dishId) {

		try {
			Dish dish = dishService.getDish(dishId);
			if (dish != null) {
				String dishImageUrl = dish.getImageUrl();
				if(inactiveIfExistInZomatoMenu(dish,request)){
					inactiveIfExistInZomatoMenu(dish,request);
				}
				dishAddOnService.removeDishAddOn(dishId);
				dishService.removeDish(dishId);
				if (!StringUtility.isNullOrEmpty(dishImageUrl) && dishImageUrl.startsWith("/")) {
					File image = new File("webapps" + dishImageUrl);
					if (image.exists()) {
						image.delete();
					}
					File smallImage = new File("webapps" + ImageUtility.getSmallImageUrl(dishImageUrl, 200, 200));
					if (smallImage.exists()) {
						smallImage.delete();
					}
				}
			}
		} catch (DataIntegrityViolationException exp) {
			map.put("errorMsg", "Sorry, this dish is associated with some id and could not be deleted");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("errorMsg", "Sorry, something went wrong and we could not delete this dish.");
		}
		return listDishes(map, request);
	}
	
	@RequestMapping("/resizeDishes/{userId}")
	@ApiIgnore
	public String resizeDishes(Map<String, Object> map, HttpServletRequest request, @PathVariable("userId") Integer userId) {
		Integer adminUserId = (Integer) request.getSession().getAttribute("userId");
		User adminUser = userService.getUser(adminUserId);
		if ("admin".equals(adminUser.getRole().getRole()) || "restaurantManager".equals(adminUser.getRole().getRole())) {
			
			List<Dish> dishes = dishService.listDishByResaurant((Integer)request.getSession().getAttribute("restaurantId"));
			for (Dish dish : dishes) {
				String dishImageUrl = dish.getImageUrl();
				if (!StringUtility.isNullOrEmpty(dishImageUrl)) {
					String localUrl = "webapps" + dishImageUrl;
					File dishImage = new File(localUrl);
					if (dishImage.exists()) {
						ImageUtility.resizeImage(localUrl, ImageUtility.getSmallImageUrl(localUrl, 200, 200), ImageUtility.getFileFormat(localUrl)/*"jpg"*/, 200, 200);
					}
				}
			}
		}
		return "redirect:/dish/";
	}
	
	@RequestMapping(value = "/shareDish.json", method= RequestMethod.POST, consumes = "application/json")
	public String shareDishJSON(@RequestBody JSONShareDish shareDish, Model model, HttpServletRequest request) {
		model.addAttribute("shareDishJson", shareDish);
		Dish dish = dishService.getDish(shareDish.getDishId());
		model.addAttribute("dish", dish);
	    Restaurant rest = restService.getRestaurant(shareDish.getRestaurantId());
		String address = rest.getAddress1();
		if(StringUtility.isNullOrEmpty(rest.getAddress2())) {
			address += rest.getAddress2();
		}
		model.addAttribute("address", address);
		model.addAttribute("city", rest.getCity());
		model.addAttribute("zip", rest.getZip());
		return "shareDish";
	}
	
	@RequestMapping(value = "/shareDish.htm")
	public String shareDish(Model model, HttpServletRequest request,@RequestParam String restaurantId, @RequestParam String dishId) {
		//String restIdString = request.getParameter("restaurantId");
		//String dishIdStr = request.getParameter("dishId");
		Integer restaurantID = Integer.parseInt(restaurantId);
		Integer dishID = Integer.parseInt(dishId);
		Dish dish = dishService.getDish(dishID);
		model.addAttribute("dish", dish);
		//User user = userService.getUser(restaurantId);
		Restaurant rest=restService.getRestaurant(restaurantID);
		String address = rest.getAddress1();
		if(StringUtility.isNullOrEmpty(rest.getAddress2())) {
			address += rest.getAddress2();
		}
		model.addAttribute("address", address);
		model.addAttribute("city", rest.getCity());
		model.addAttribute("zip", rest.getZip());
		return "shareDish";
	}
	
	@RequestMapping("/shareDishTest")
	public String shareDishTest(Model model, HttpServletRequest request) {
		return "shareDishTest";
	}
	private String renameFileToHaveDishId(String fileUrl, Integer dishId) {
		File oldFile = new File("webapps" + fileUrl);
		String newFileUrl = fileUrl.replace("null_", dishId + "_");
		File newFile = new File("webapps" + newFileUrl);
		oldFile.renameTo(newFile);
		return newFileUrl;
	}
	
	@RequestMapping("/addStockDish")
	@ApiIgnore
	public String addStockDish(@ModelAttribute("stockDish")  StockManagement stockManagement,Map<String, Object> map, HttpServletRequest request){
		
		int restId= (Integer) request.getSession().getAttribute("restaurantId");
		int currentFulfillmentId= stockManagement.getFulfillmentCenterId();
		boolean duplicateValue=true;
		int dishId =stockManagement.getDishId();
		if(stockManagement.getId()>0)
			stockManagement.setAddQuantity(stockManagement.getRemainingQuantity()+stockManagement.getAddQuantity());
		if(stockManagement.getRemoveQuantity()>0)
			stockManagement.setAddQuantity(stockManagement.getRemainingQuantity()-stockManagement.getRemoveQuantity());
		stockManagement.setRemainingQuantity(stockManagement.getAddQuantity());
		List<StockManagement> stockList =  stockManagementService.getFromStockManagement(restId);
		if(stockManagement.getId()==0){
		for(StockManagement sm : stockList){
		if(sm.getDishId()==dishId && sm.getFulfillmentCenterId()==currentFulfillmentId && sm.getExpireDate().equalsIgnoreCase(stockManagement.getExpireDate())){
			map.put("errorMsg", "Duplicate entry");
			 duplicateValue=false;
		}
		}}
		if(duplicateValue){
			stockManagementService.addStockDish(stockManagement);
		}
		return "redirect:/dish/stockedDishes";
	}
	
	@ModelAttribute("pojoForm")
	@ApiIgnore
	public StockManagementForm populatePojos() {
	    // Don't forget to initialize the pojos list or else it won't work
		StockManagementForm pojoForm = new StockManagementForm();
	    List<StockManagement> pojos = new ArrayList<StockManagement>();
	    for(int i=0; i<1; i++) {
	        pojos.add(new StockManagement());
	    }
	    pojoForm.setManageStock(pojos);
	    return pojoForm;
	}
	
	@RequestMapping("/addStock")
	@ApiIgnore
	public String saveForm(@ModelAttribute("pojoForm") StockManagementForm smForm, HttpServletRequest request) {
	    
		
		for(StockManagement stockManagement : smForm.getManageStock()) {
			if(stockManagement.getRemoveQuantity()>0){
			StockManagement  sm = stockManagementService.getStockedDish(stockManagement.getId());	
			if(sm.getId()==stockManagement.getId()){
				Restaurant rest =  restService.getRestaurant(sm.getRestaurantId());
				FulfillmentCenter ffc = fulfillmentCenterService.getKitchenScreen(stockManagement.getFulfillmentCenterId());
				String username = (String) request.getSession().getAttribute("username");
				String role = (String)request.getSession().getAttribute("role");
				String message = "User Name : "+username+"("+role+") has remove the "+stockManagement.getRemoveQuantity()+" Items from Dish "+stockManagement.getDishName()+". Stock Id : "+ stockManagement.getId();
				try{
				emailNotification(sm.getRestaurantId(),rest.getAlertMail(), stockManagement.getDishName(),ffc.getName(), message, request);
			}
				catch(Exception e){
					e.getStackTrace();
				}
			}
			}
	    	
			if(stockManagement.getId()>0)
				stockManagement.setAddQuantity(stockManagement.getRemainingQuantity()+stockManagement.getAddQuantity());
			if(stockManagement.getRemoveQuantity()>0)
				stockManagement.setAddQuantity(stockManagement.getRemainingQuantity()-stockManagement.getRemoveQuantity());
			stockManagement.setRemainingQuantity(stockManagement.getAddQuantity());
	    	if(stockManagement.getExpireDate().equalsIgnoreCase("")){
	    	continue;
	    	}
	    	else{
	       stockManagementService.addStockDish(stockManagement);
	    }
	    }
	    return "redirect:/dish/stockedDishes";
	}
	
	
	@RequestMapping("/stockedDishes")
	@ApiIgnore
	public String getSockedDishes(Map<String, Object> map, HttpServletRequest request){
		int restId= (Integer) request.getSession().getAttribute("restaurantId");
		List<Dish> stockes = stockManagementService.getStockedDishes(restId); 
		Set<Dish> stockedDishes = new HashSet<Dish>(stockes);
		map.put("stockDish", new StockManagement());
		List<FulfillmentCenter> ffcList = fulfillmentCenterService.getKitchenScreens(restId);
		List<StockManagement> listOfStockedDishes = stockManagementService.getFromStockManagement(restId);
		
		if(listOfStockedDishes!=null){
		Collections.sort(listOfStockedDishes, new Comparator<StockManagement>() {
			public int compare(StockManagement v1,StockManagement  v2) {
		        return "null".equalsIgnoreCase(v1.getExpireDate())?'0':v1.getExpireDate().compareTo((String)("null".equalsIgnoreCase(v2.getExpireDate())?'0':v2.getExpireDate()));
		    }
		});
		}
		
		map.put("stockedDishes", stockedDishes);
		map.put("fulfillmentCenter", ffcList);
		map.put("listOfStockedDishes", listOfStockedDishes);
		return "stockManagement";
	}
	
	@RequestMapping("/deleteStockedDish/{stockDishId}")
	@ApiIgnore
	public String removeStockDish(@ModelAttribute("stockDish") @PathVariable("stockDishId") Integer id, StockManagement stockManagement){
		stockManagementService.removeStockDish(id);
		return "redirect:/dish/stockedDishes";
	}
	
	@RequestMapping("/editStockedDish/{stockDishId}")
	@ApiIgnore
	public String editStockDish(Map<String, Object> map,@ModelAttribute("stockDish")  @PathVariable("stockDishId") Integer id,HttpServletRequest request){
		int restId= (Integer) request.getSession().getAttribute("restaurantId");
		List<Dish> stockes = stockManagementService.getStockedDishes(restId); 
		Set<Dish> stockedDishes = new HashSet<Dish>(stockes); 
		List<FulfillmentCenter> ffcList = fulfillmentCenterService.getKitchenScreens(restId);
		List<StockManagement> listOfStockedDishes = stockManagementService.getFromStockManagement(restId);
		
		if(listOfStockedDishes!=null){
			Collections.sort(listOfStockedDishes, new Comparator<StockManagement>() {
			    public int compare(StockManagement v1,StockManagement  v2) {
			        return v1.getExpireDate()==null?'0':v1.getExpireDate().compareTo(v2.getExpireDate());
			    }
			});
			}
		
		map.put("stockDish", stockManagementService.getStockedDish(id));
		map.put("stockedDishes", stockedDishes);
		map.put("fulfillmentCenter", ffcList);
		map.put("listOfStockedDishes", listOfStockedDishes);
		return "stockManagement";
	}
	
	
	public String emailNotification(Integer restaurantId,String emailAddr,String itemName,String FulfillmentCenterName,String message,HttpServletRequest request){
		
		if (restaurantId>0) {
			Restaurant rest=restService.getRestaurant(restaurantId);
			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append("<html><body>");
			String username="";
			String password="";
			if (rest != null) {
				strBuilder.append("Restaurant Name : " + rest.getBussinessName() + "<br />");
				username = rest.getMailUsername();
				password = rest.getMailPassword();
			}
			strBuilder.append("<p style='align:center;'<b><i>Attention Required !</i></b></p><br />");
			strBuilder.append("<table><tr><td colspan='2'><hr></td></tr>");
			strBuilder.append("<br>");
			strBuilder.append("<tr>");
			strBuilder.append("<td><b>Fulfillment Center :</b></td>");
			strBuilder.append("<td>" + FulfillmentCenterName+ "</td>&nbsp;&nbsp;");
			strBuilder.append("</tr><tr>");
			strBuilder.append("<td><b>Dish :</b></td>");
			strBuilder.append("<td>" +itemName + "</td>&nbsp;&nbsp;");
			strBuilder.append("</tr><tr>");
			strBuilder.append("</tr>");
			strBuilder.append("<br>");
			strBuilder.append("<tr><td colspan='2'><hr></td></tr>");
			strBuilder.append("<br>");
			strBuilder.append("<tr>");
			strBuilder.append("<td colspan='3'>");
			strBuilder.append("<B align='center'>"+message+"</B>");
			strBuilder.append("</td>");
			strBuilder.append("</tr>");
			strBuilder.append("</table>");
			strBuilder.append("<br/>");
			strBuilder.append("<table>");
			strBuilder.append("<tr>");
			strBuilder.append("<td>");
			strBuilder.append("<tr>");
			strBuilder.append("<td>");
			strBuilder.append("</td>");
			strBuilder.append("</tr>");
			strBuilder.append("</table>");
			strBuilder.append("</body></html>");
			MailerUtility.sendHTMLMail(emailAddr, "Stock Notification", strBuilder.toString(),username,password);
		} else {
			return "Error: No check found";
		}
		
		return "Email Sent Successfully";
	}
	
	boolean updateIfExistInZomatoMenu(Dish dish,HttpServletRequest request){
		List<Menu> menuList = menuService.listMenuByRestaurant(dish.getRestaurantId());
		boolean dishExist=false;
		for(Menu menu :menuList){
			dishExist=false;
			if(!menu.isPosVisible() &&  menu.getZomatoStatus()==Status.ACTIVE){
				for(Section section : menu.getSections()){
					for(Dish dishData :  section.getDishes()){
						if(dishData.getDishId().equals(dish.getDishId())){
							dishExist=true;
						}
					}
				}
				if(dishExist){
					try {
						ResponseDTO response =zomatoService.updateZomatoMenu(menu,request);
						try {
							restService.alertMail(response.message,"updated dish "+dish.getName()+" to zomato menu. Status :"+response.result, request);
						} catch (MessagingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return dishExist;
	}
	
	boolean inactiveIfExistInZomatoMenu(Dish dish,HttpServletRequest request){
		List<Menu> menuList = menuService.allMenusByStatus(dish.getRestaurantId(),Status.ACTIVE);
		boolean dishExist=false;
		for(Menu menu :menuList){
			dishExist=false;
			if(!menu.isPosVisible() && menu.getZomatoStatus()==Status.ACTIVE){
				for(Section section : menu.getSections()){
					for(Dish dishData :  section.getDishes()){
						if(dishData!=null){
							if(dishData.getDishId().equals(dish.getDishId())){
								dishExist=true;
								dishData.setDisabled(true);
							}
						}
					}
				}
				if(dishExist){
					try {
						ResponseDTO response = zomatoService.updateZomatoMenu(menu,request);
						try {
							restService.alertMail(response.message,"Deleted/Inactived dish "+dish.getName()+" from zomato menu. Status :"+response.result, request);
						} catch (MessagingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return dishExist;
	}
	
}
