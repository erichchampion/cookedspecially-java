/**
 * 
 */
package com.cookedspecially.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
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
import com.cookedspecially.domain.AddOnDish_Size;
import com.cookedspecially.domain.AddOnNutrientInfo;
import com.cookedspecially.domain.Dish;
import com.cookedspecially.domain.DishAddOn;
import com.cookedspecially.domain.Dish_Size;
import com.cookedspecially.domain.JSONShareDish;
import com.cookedspecially.domain.NutrientInfo;
import com.cookedspecially.domain.Nutrientes;
import com.cookedspecially.domain.Restaurant;
import com.cookedspecially.domain.User;
import com.cookedspecially.enums.WeekDayFlags;
import com.cookedspecially.service.AddOnDishService;
import com.cookedspecially.service.AddOnDishTypeService;
import com.cookedspecially.service.DishTypeService;
import com.cookedspecially.service.RestaurantService;
import com.cookedspecially.service.UserService;
import com.cookedspecially.utility.ImageUtility;
import com.cookedspecially.utility.StringUtility;

/**
 * @author rahul, abhishek
 *
 */
@Controller
@ApiIgnore
@RequestMapping("/addOnDish")
public class AddOnDishController {

	@Autowired
	private AddOnDishService addOnDishService;
	@Autowired	
	private AddOnDishTypeService addOnDishTypeService;
	@Autowired
	private UserService userService;
	@Autowired
	private DishTypeService dishTypeService;
	@Autowired
	private RestaurantService restService;
	
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
	public String listDishes(Map<String, Object> map, HttpServletRequest request) {

		AddOnDish dish = new AddOnDish();
		//dish.setActiveDays(WeekDayFlags.getWeekDayVal(ALLWEEKDAYS));
		//dish.setHappyHourDays(WeekDayFlags.getWeekDayVal(ALLWEEKDAYS));
		
		map.put("dish", dish);
		//Integer userId = (Integer) request.getSession().getAttribute("userId");
		Integer restId=(Integer)request.getSession().getAttribute("restaurantId");
		map.put("dishList", addOnDishService.listDishByRestaurant(restId));
		
		ArrayList<Nutrientes> nutrients =  (ArrayList<Nutrientes>) restService.getNutirentList(restId);
		
		map.put("nutrients",nutrients);
		map.put("dishSizeList", dishTypeService.listDishSizeByRestaurantId(restId));
		map.put("hours", HOURS);
		map.put("mins", MINS);
		map.put("currTime", new Date().toLocaleString());
		map.put("weekdayFlags", WeekDayFlags.values());
		map.put("dishTypes", addOnDishTypeService.listDishTypesByRestaurant(restId));
		//map.put("categoryList", categoryService.listCategoryByUser((Integer) request.getSession().getAttribute("userId")));
		return "addOnDish";
	}
	@RequestMapping("/edit/{dishId}")
	public String editDish(Map<String, Object> map, HttpServletRequest request, @PathVariable("dishId") Integer dishId) {
		AddOnDish dish = addOnDishService.getDish(dishId);
		map.put("dish", dish);
		//Integer userId = (Integer) request.getSession().getAttribute("userId");
		Integer restId=(Integer)request.getSession().getAttribute("restaurantId");
		map.put("dishList", addOnDishService.listDishByRestaurant(restId));
		ArrayList<Nutrientes> nutrients =  (ArrayList<Nutrientes>) restService.getNutirentList(dish.getRestaurantId());
		map.put("nutrients",nutrients);
		map.put("dishSizeList", dishTypeService.listDishSizeByRestaurantId(restId));
		map.put("hours", HOURS);
		map.put("mins", MINS);
		/*map.put("weekdayFlags", WeekDayFlags.values());*/
		map.put("currTime", new Date().toLocaleString());
		//map.put("categoryList", categoryService.listCategoryByUser((Integer) request.getSession().getAttribute("userId")));
		map.put("dishTypes", addOnDishTypeService.listDishTypesByRestaurant(restId));
		return "addOnDish";
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addDish(Map<String, Object> map, @ModelAttribute("dish")
	AddOnDish dish, BindingResult result, @RequestParam("file") MultipartFile squareImage, @RequestParam("file[1]") MultipartFile rectangularImage, HttpServletRequest request) throws Exception {
		FileOutputStream fos = null;
		//String fileUrl = dish.getImageUrl();
		String outFileUrl = null;
		String defalutOutFileUrl = null;
		String imageUrls= null;
		ArrayList<MultipartFile> files = new ArrayList<MultipartFile>();
		files.add(squareImage);
		files.add(rectangularImage);
		addOnDishService.addDish(dish);
		List<AddOnNutrientInfo> nInfo =  new ArrayList<AddOnNutrientInfo>();
		List<AddOnDish_Size> dSize= new ArrayList<AddOnDish_Size>();
		for(AddOnDish_Size dishSize : dish.getDishSizeList()){
			if(dishSize.getAddOnDishId()==null || dishSize.getAddOnDishId().equals("")){
			dishSize.setAddOnDishId(dish.getAddOnId());
			}
			if(dishSize.getDishSizeId()!=null){
			dSize.add(dishSize);
		}
		}
		dish.setAddOnDishSize(dSize);
		String[] nutrientNames = request.getParameterValues("nutrientName");
		String[] instructions= request.getParameterValues("instructions");
		HashMap<String,Double> dc =  new LinkedHashMap<String, Double>();
		if(nutrientNames!=null){
		for(int i=0;i<nutrientNames.length;i++){
		dc.put(nutrientNames[i],Double.parseDouble(instructions[i].equalsIgnoreCase("")?"0":instructions[i]));
		}
		Iterator it  = dc.entrySet().iterator();
		while(it.hasNext()){
			AddOnNutrientInfo nf  =    new AddOnNutrientInfo();
			 Map.Entry pair = (Map.Entry)it.next();
		     nf.setName(pair.getKey().toString());
		     Nutrientes nutrientes =  restService.getByNutrientesByNameType(nf.getName(),dish.getDishType(),dish.getRestaurantId());
		     nf.setValue(Double.parseDouble(pair.getValue().toString()));
		     nf.setDishId(dish.getAddOnId());
		     nf.setNutrientId(nutrientes.getId());
		     nInfo.add(nf);
		}
		dish.setNutritionalInfo(nInfo);
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
						map.put("dishList", addOnDishService.listDishByRestaurant(dish.getRestaurantId()));
						return "dish";
					}
		            try {
						byte[] bytes = file.getBytes();
						String fileDir = File.separator + "static" + File.separator + dish.getRestaurantId() + File.separator ;
						
						if (iter == 0)
							fileUrl = fileDir + dish.getAddOnId() + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9_.]", "_");
						 else if (iter == 1)
							 fileUrl = fileDir + "Rect_"+dish.getAddOnId() + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9_.]", "_");
						
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
						String newFileUrl = renameFileToHaveDishId(fileUrl, dish.getAddOnId());
						if (iter == 0) 
							dish.setImageUrl(newFileUrl);
						else if (iter == 1) 
							dish.setRectangularImageUrl(newFileUrl);
						addOnDishService.addDish(dish);
						String smallFileOldUrl = ImageUtility.getSmallImageUrl(fileUrl, 200, 200);
						renameFileToHaveDishId(smallFileOldUrl, dish.getAddOnId());
					}
				iter++;
		}
		
			
			Integer dishId = dish.getAddOnId();  
			/*dish.setActiveDays(WeekDayFlags.getWeekDayVal(dish.getDishActiveDays()));
			dish.setHappyHourDays(WeekDayFlags.getWeekDayVal(dish.getHappyHourActiveDays()));*/
			addOnDishService.addDish(dish);
			if (dishId != null && dishId > 0) {
				addOnDishService.updateMenuModificationTime(dishId);
			} 
		}
		return "redirect:/addOnDish/";
	}

	
	@RequestMapping("/delete/{dishId}")
	public String deleteDish(Map<String, Object> map, HttpServletRequest request, @PathVariable("dishId") Integer dishId) {

		try {
			AddOnDish dish = addOnDishService.getDish(dishId);
			if (dish != null) {
				String dishImageUrl = dish.getImageUrl();
				addOnDishService.removeDish(dishId);
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
			map.put("errorMsg", "Sorry, this AddOn is associated with some id and could not be deleted");
		} catch (Exception e) {
			map.put("errorMsg", "Sorry, something went wrong and we could not delete this AddOn.");
		}
		return listDishes(map, request);
	}
	
	@RequestMapping("/resizeDishes/{userId}")
	public String resizeDishes(Map<String, Object> map, HttpServletRequest request, @PathVariable("userId") Integer userId) {
		Integer adminUserId = (Integer) request.getSession().getAttribute("userId");
		User adminUser = userService.getUser(adminUserId);
		Integer restId=(Integer) request.getSession().getAttribute("restaurantId");
		//if ("shashankagarwal1706@gmail.com".equals(adminUser.getUsername())) {
		if ("admin".equals(adminUser.getRole().getRole()) || "restaurantManager".equals(adminUser.getRole().getRole())) {
			List<AddOnDish> dishes = addOnDishService.listDishByRestaurant(restId);
			for (AddOnDish dish : dishes) {
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
		return "redirect:/addOnDish/";
	}
	
	@RequestMapping(value = "/shareDish.json", method= RequestMethod.POST, consumes = "application/json")
	public String shareDishJSON(@RequestBody JSONShareDish shareDish, Model model, HttpServletRequest request) {
		model.addAttribute("shareDishJson", shareDish);
		AddOnDish dish = addOnDishService.getDish(shareDish.getDishId());
		model.addAttribute("dish", dish);
		//User user = userService.getUser(shareDish.getRestaurantId());
		Restaurant restaurant=restService.getRestaurant(shareDish.getRestaurantId());
		String address = restaurant.getAddress1();
		if(StringUtility.isNullOrEmpty(restaurant.getAddress2())) {
			address += restaurant.getAddress2();
		}
		model.addAttribute("address", address);
		model.addAttribute("city", restaurant.getCity());
		model.addAttribute("zip", restaurant.getZip());
		return "shareDish";
	}
	
	@RequestMapping(value = "/shareDish.htm")
	public String shareDish(Model model, HttpServletRequest request) {
		String restIdString = request.getParameter("restaurantId");
		String dishIdStr = request.getParameter("dishId");
		Integer restaurantId = Integer.parseInt(restIdString);
		Integer dishId = Integer.parseInt(dishIdStr);
		AddOnDish dish = addOnDishService.getDish(dishId);
		model.addAttribute("dish", dish);
		//User user = userService.getUser(restaurantId);
		Restaurant restaurant=restService.getRestaurant(restaurantId);
		String address = restaurant.getAddress1();
		if(StringUtility.isNullOrEmpty(restaurant.getAddress2())) {
			address += restaurant.getAddress2();
		}
		model.addAttribute("address", address);
		model.addAttribute("city",restaurant.getCity());
		model.addAttribute("zip", restaurant.getZip());
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
}
