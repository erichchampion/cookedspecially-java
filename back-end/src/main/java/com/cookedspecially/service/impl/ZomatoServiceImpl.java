package com.cookedspecially.service.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cookedspecially.dao.CheckDAO;
import com.cookedspecially.domain.Check;
import com.cookedspecially.domain.DeliveryArea;
import com.cookedspecially.domain.Dish;
import com.cookedspecially.domain.Menu;
import com.cookedspecially.domain.Order;
import com.cookedspecially.domain.Restaurant;
import com.cookedspecially.domain.Section;
import com.cookedspecially.domain.TaxType;
import com.cookedspecially.domain.User;
import com.cookedspecially.dto.CustomerDTO;
import com.cookedspecially.dto.OrderDTO;
import com.cookedspecially.dto.OrderDishDTO;
import com.cookedspecially.dto.PlaceOrderDTO;
import com.cookedspecially.dto.ResponseDTO;
import com.cookedspecially.dto.zomato.common.InactivateTaxes;
import com.cookedspecially.dto.zomato.common.ItemCharges;
import com.cookedspecially.dto.zomato.common.ItemTaxes;
import com.cookedspecially.dto.zomato.common.Items;
import com.cookedspecially.dto.zomato.common.OrderAdditionalCharges;
import com.cookedspecially.dto.zomato.common.Taxes;
import com.cookedspecially.dto.zomato.common.ZomatoRestaurantStatus;
import com.cookedspecially.dto.zomato.menu.AddUpdateMenu;
import com.cookedspecially.dto.zomato.menu.Categories;
import com.cookedspecially.dto.zomato.order.ZomatoOrderBody;
import com.cookedspecially.dto.zomato.order.ZomatoOrderConfirmDTO;
import com.cookedspecially.dto.zomato.order.ZomatoOrderDeliveredDTO;
import com.cookedspecially.dto.zomato.order.ZomatoOrderPickedUpDTO;
import com.cookedspecially.dto.zomato.order.ZomatoOrderRejectDTO;
import com.cookedspecially.enums.Status;
import com.cookedspecially.enums.check.CheckType;
import com.cookedspecially.enums.restaurant.ChargesType;
import com.cookedspecially.service.DeliveryAreaService;
import com.cookedspecially.service.RestaurantService;
import com.cookedspecially.service.TaxTypeService;
import com.cookedspecially.service.UserService;
import com.cookedspecially.service.ZomatoService;
import com.google.gson.Gson;

@Service
public class ZomatoServiceImpl implements ZomatoService {

	final String Zomato = "Zomato";
	final static Logger logger = Logger.getLogger(ZomatoServiceImpl.class);
	@Autowired
	private TaxTypeService taxTypeService;

	@Autowired
	private RestaurantService restaurantService;

	@Autowired
	private UserService userService;

	@Autowired
	private DeliveryAreaService deliveryAreaService;

	@Autowired
	private CheckDAO checkDAO;

	List<TaxType> taxTypeList;

	final static String order_Type = CheckType.Delivery.toString();

	@Override
	@Transactional
	public ResponseDTO createZomatoMenu(Menu menu, HttpServletRequest request)
			throws IOException {
		return executeMenu(menu, "add_menu", request);
	}

	@Override
	@Transactional
	public ResponseDTO updateZomatoMenu(Menu menu, HttpServletRequest request)
			throws IOException {
		return executeMenu(menu, "update_menu", request);
	}

	@Override
	@Transactional
	public ResponseDTO inactivateTax(TaxType taxType, HttpServletRequest request)
			throws IOException {
		ResponseDTO response = new ResponseDTO();
		InactivateTaxes taxesList = new InactivateTaxes();
		taxesList.taxes = new ArrayList<Taxes>();
		Taxes itemTaxes = new Taxes();
		itemTaxes.tax_id = taxType.getTaxTypeId();
		itemTaxes.tax_is_active = 0;
		itemTaxes.tax_name = taxType.getName();
		itemTaxes.tax_value = taxType.getTaxValue();
		if (taxType.getChargeType() == ChargesType.PERCENTAGE) {
			itemTaxes.tax_type = ChargesType.PERCENTAGE.toString();
		} else {
			itemTaxes.tax_type = "FIXED";
		}
		taxesList.taxes.add(itemTaxes);

		Properties prop = readPropertieFile(taxType.getRestaurantId());
		String access_key = prop.getProperty("access_key");
		String actionUrl;

		actionUrl = prop.getProperty("updateMenu");

		try {
			response = callURL(access_key, actionUrl, taxesList);
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO Auto-generated method stub
		return response;
	}

	@Override
	@Transactional
	public ResponseDTO updateMenuWithSections(Menu oldMenu, Menu newMenu,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		oldMenu.setStatus(newMenu.getStatus());
		oldMenu.setZomatoStatus(newMenu.getZomatoStatus());
		oldMenu.setName(newMenu.getName());
		oldMenu.setDescription(newMenu.getDescription());
		oldMenu.setImageUrl(newMenu.getImageUrl());
		ArrayList<Section> sectionsToRemove = new ArrayList<Section>();
		Map<Integer, List<Dish>> dishesToRemove = new HashMap<>();
		// Here we're removing deleted dishes, sections from the menu
		for (Section oldMenuSection : oldMenu.getSections()) {
			boolean sectionVerified = false;
			List<Dish> disabledDishList = new ArrayList<Dish>();
			for (Section newMenuSection : newMenu.getSections()) {
				if (oldMenuSection.getSectionId().equals(
						newMenuSection.getSectionId())) {
					sectionVerified = true;
					for (Dish oldMenuDish : oldMenuSection.getDishes()) {
						boolean dishVerified = false;
						for (Dish newMenuDish : newMenuSection.getDishes()) {
							if (oldMenuDish.getDishId().equals(
									newMenuDish.getDishId())) {
								dishVerified = true;
							}
						}
						if (!dishVerified) {
							oldMenuDish.setDisabled(true);
							disabledDishList.add(oldMenuDish);
						}
					}
				}
			}
			if (!sectionVerified) {
				oldMenuSection.setValid(false);
				sectionsToRemove.add(oldMenuSection);
			}

			if (disabledDishList.size() > 0) {
				dishesToRemove.put(oldMenuSection.getSectionId(),
						disabledDishList);
			}
		}
		// Here it end's
		// Here we are adding new items and sections to menu
		Integer prirorityIndex = 0;  
		ArrayList<Section> sectionList = new ArrayList<Section>();
		for (Section newMenuSection : newMenu.getSections()) {
			prirorityIndex++;
			boolean sectionVerified = false;
			for (Section oldMenuSection : oldMenu.getSections()) {
				if (oldMenuSection.getSectionId().equals(newMenuSection.getSectionId())) {
					sectionVerified = true;
					ArrayList<Dish> dishList = new ArrayList<Dish>();
					for (Dish newMenuDish : newMenuSection.getDishes()) {
						boolean dishVerified = false;
						for (Dish oldMenuDish : oldMenuSection.getDishes()) {
							if (oldMenuDish.getDishId().equals(newMenuDish.getDishId())) {
								dishVerified = true;
								dishList.add(newMenuDish);
							}
						}
						if (!dishVerified) {
							dishList.add(newMenuDish);
						}
					}
					if (dishesToRemove.get(oldMenuSection.getSectionId()) != null)
						dishList.addAll(dishesToRemove.get(oldMenuSection.getSectionId()));
						oldMenuSection.setDishes(dishList);
						sectionList.add(oldMenuSection);
				}
			}
			if (!sectionVerified) {
				sectionList.add(newMenuSection);
			}
		}
		if (sectionList.size() > 0) {
			if (sectionsToRemove.size() > 0) {
				sectionList.addAll(sectionsToRemove);
			}
			oldMenu.setSections(sectionList);

		}

		ResponseDTO response = null;
		try {
			response = updateZomatoMenu(oldMenu, request);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public ResponseDTO zomatoOrderConfirm(Check check, Integer deliveryTime,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		ZomatoOrderConfirmDTO orderConfirm = new ZomatoOrderConfirmDTO();
		orderConfirm.order_id = check.getZomatoOrderId().toString(); // Zomato
																		// orderId
		orderConfirm.external_order_id = check.getCheckId().toString(); // check
																		// Id
																		// from
																		// our
																		// database
		orderConfirm.delivery_time = deliveryTime;
		ResponseDTO response = null;
		Properties prop = null;
		try {
			prop = readPropertieFile(check.getRestaurantId());
			String confirmOrder = prop.getProperty("confirmOrder");
			String auth_Key = prop.getProperty("access_key");

			response = callURL(auth_Key, confirmOrder, orderConfirm);
			return response;
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;

	}

	@Override
	public ResponseDTO zomatoOrderReject(Check check, HttpServletRequest request) {
		// TODO Auto-generated method stub
		ZomatoOrderRejectDTO rejectOrder = new ZomatoOrderRejectDTO();
		rejectOrder.order_id = check.getZomatoOrderId().toString();
		/*
		 * Id Message 1 Items out of stock. 2 No delivery boys available. 3
		 * Nearing closing time. 4 Out of Subzone/Area integer 5 Kitchen is Full
		 */
		rejectOrder.rejection_message_id = 1;
		rejectOrder.vendor_rejection_message = "Order rejected because of discrepancy in gross amount or item is out of stock";
		Properties prop = null;
		try {
			prop = readPropertieFile(check.getRestaurantId());
			String rejectOrderAPI = prop.getProperty("rejectOrder");
			String auth_Key = prop.getProperty("access_key");
			ResponseDTO response = null;
			response = callURL(auth_Key, rejectOrderAPI, rejectOrder);
			return response;
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ResponseDTO zomatoOrderOutForDelivery(Check check,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		ZomatoOrderPickedUpDTO outForDeliery = new ZomatoOrderPickedUpDTO();
		outForDeliery.order_id = check.getZomatoOrderId().toString(); // zoamto
																		// order
																		// Id

		Order order = check.getOrders().get(0);
		if (order.getDeliveryAgent() != null) {
			if (Character.isDigit(order.getDeliveryAgent().charAt(0))) {
				Integer agentId = Integer.parseInt(order.getDeliveryAgent());
				User deliveryBoy = userService.getUser(agentId);
				if (deliveryBoy != null) {
					outForDeliery.rider_name = deliveryBoy.getFirstName() + " "
							+ deliveryBoy.getLastName();
					outForDeliery.rider_phone_number = deliveryBoy.getContact()
							.toString();
				}
			} else {
				outForDeliery.rider_name = order.getDeliveryAgent();
			}
		}

		Properties prop = null;
		try {
			prop = readPropertieFile(check.getRestaurantId());
			String pickedUpAPI = prop.getProperty("pickedUp");
			String auth_Key = prop.getProperty("access_key");
			ResponseDTO response = null;
			response = callURL(auth_Key, pickedUpAPI, outForDeliery);
			return response;
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ResponseDTO zomatoOrderUpdateDeliveryBoyInfo(Check check,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		ZomatoOrderPickedUpDTO updateDeliverBoy = new ZomatoOrderPickedUpDTO();
		updateDeliverBoy.order_id = check.getOrderId().toString();
		updateDeliverBoy.rider_name = "";
		updateDeliverBoy.rider_phone_number = "";
		Properties prop = null;
		String resultPath = null;
		try {
			prop = readPropertieFile(check.getRestaurantId());
			String updateDeliverBoyAPI = prop.getProperty("updateDeliverBoy");
			String auth_Key = null;
			if (request != null) {
				String serverName = request.getServerName();
				resultPath = serverName;
			}
			if ("www.cookedspecially.com".equalsIgnoreCase(resultPath)) {
				auth_Key = prop.getProperty("access_key");
			} else {
				auth_Key = prop.getProperty("TestAccess_key");
			}
			ResponseDTO response = null;
			response = callURL(auth_Key, updateDeliverBoyAPI, updateDeliverBoy);
			return response;
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ResponseDTO zomatOrderDelivered(Check check,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		ZomatoOrderDeliveredDTO orderDelivered = new ZomatoOrderDeliveredDTO();
		orderDelivered.order_id = check.getZomatoOrderId().toString();
		Properties prop = null;
		String resultPath = null;
		try {
			prop = readPropertieFile(check.getRestaurantId());
			String orderDeliveredAPI = prop.getProperty("orderDelivered");
			String auth_Key = null;
			if (request != null) {
				String serverName = request.getServerName();
				resultPath = serverName;
			}
			if ("www.cookedspecially.com".equalsIgnoreCase(resultPath)) {
				auth_Key = prop.getProperty("access_key");
			} else {
				auth_Key = prop.getProperty("TestAccess_key");
			}

			ResponseDTO response = null;
			response = callURL(auth_Key, orderDeliveredAPI, orderDelivered);
			logger.info("Zomato order delivered : " + response.result);
			return response;
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	@Transactional
	public PlaceOrderDTO convertZomatoJsonToPlaceOrderDTO(
			ZomatoOrderBody orderDTO) {
		PlaceOrderDTO placeOrderDTO = new PlaceOrderDTO();
		// TODO Auto-generated method stub
		placeOrderDTO.customer = new CustomerDTO();
		if (orderDTO.customer_details != null) {
			placeOrderDTO.order = new OrderDTO();

			if (orderDTO.customer_details.address_instructions!=null && orderDTO.customer_details.address_instructions.length() > 0) {
				if(orderDTO.customer_details.address_instructions.length()>300){
					placeOrderDTO.order.instructions = orderDTO.customer_details.address_instructions.substring(0, Math.min(
									orderDTO.customer_details.address_instructions
											.length(), 299));
				}else{
					placeOrderDTO.order.instructions = orderDTO.customer_details.address_instructions;
				}
			}

			if ("ONLINE".equalsIgnoreCase(orderDTO.payment_mode)) {
				placeOrderDTO.order.paymentMethod = "PG";
			} else if ("CASH".equalsIgnoreCase(orderDTO.payment_mode)) {
				placeOrderDTO.order.paymentMethod = "COD";
			} else {
				placeOrderDTO.order.paymentMethod = orderDTO.payment_mode;
			}
			placeOrderDTO.order.orderSource = "Zomato";
			Date date = new Date(
					Integer.parseInt(orderDTO.order_date_time) * 1000L);

			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss z");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
			String formattedDate = sdf.format(date);

			placeOrderDTO.order.deliveryDateTime = formattedDate;
			placeOrderDTO.order.orderType = this.order_Type;
			boolean setCookingInst = true;
			placeOrderDTO.order.items = new ArrayList<OrderDishDTO>();
			for (Items item : orderDTO.order_items) {
				OrderDishDTO dish = new OrderDishDTO();
				dish.itemId = item.item_id;
				dish.quantity = item.item_quantity;
				if (orderDTO.customer_details.order_instructions != null) {
					if (setCookingInst) {

						dish.instructions = orderDTO.customer_details.order_instructions;
						setCookingInst = false;
					}
				}

				placeOrderDTO.order.items.add(dish);
			}

			/* adding customer details */
			placeOrderDTO.customer.address = orderDTO.customer_details.delivery_area
					+ ", " + orderDTO.customer_details.address;
			placeOrderDTO.customer.city = orderDTO.customer_details.City;
			placeOrderDTO.customer.name = orderDTO.customer_details.name;
			// placeOrderDTO.customer.email=orderDTO.customer_details.email;
			placeOrderDTO.customer.phone = orderDTO.customer_details.phone_number
					.toString();
			if (orderDTO.order_additional_charges != null
					&& orderDTO.order_additional_charges.size() > 0) {
				if (orderDTO.order_additional_charges.get(0).charge_value > 0) {
					DeliveryArea area = deliveryAreaService
							.getDeliveryArea(orderDTO.order_additional_charges
									.get(0).charge_id);
					if (area != null) {
						placeOrderDTO.customer.deliveryArea = area.getName();
						placeOrderDTO.order.deliveryArea = area.getName();
					} else {
						placeOrderDTO.customer.deliveryArea = Zomato;
						placeOrderDTO.order.deliveryArea = Zomato;
					}
				}
			}
			// placeOrderDTO.customer.deliveryArea="tier1";

			/* need to define mechanism */
			/*
			 * for(ItemCharges deliveryCharges :
			 * orderDTO.order_additional_charges){
			 * placeOrderDTO.customer.deliveryArea="";
			 * //placeOrderDTO.order.deliveryCharges
			 * =Integer.parseInt(deliveryCharges.charge_value.toString()); }
			 */

			// placeOrderDTO.order.deliveryCharges = 42; //for testing only
			placeOrderDTO.order.zomatoGrossAmount = orderDTO.gross_amount;

		}
		return placeOrderDTO;
	}

	ResponseDTO executeMenu(Menu menu, String menuAction,
			HttpServletRequest request) throws IOException {
		ResponseDTO response = new ResponseDTO();
		Properties prop = readPropertieFile(menu.getRestaurantId());

		String auth_Key = null;
		String resultPath = null;
		if (request != null) {
			String serverName = request.getServerName();
			resultPath = serverName;
		}
		logger.info(resultPath);
		if ("www.cookedspecially.com".equalsIgnoreCase(resultPath)) {
			auth_Key = prop.getProperty("access_key");
		} else {
			auth_Key = prop.getProperty("TestAccess_key");
		}

		String actionUrl;
		if ("add_menu".equalsIgnoreCase(menuAction)) {
			actionUrl = prop.getProperty("addMenu");
		} else {
			actionUrl = prop.getProperty("updateMenu");
		}
		AddUpdateMenu zomatoMenu = new AddUpdateMenu();
		if (menu != null) {
			zomatoMenu.menu = new com.cookedspecially.dto.zomato.menu.Menu();
			zomatoMenu.menu.taxes = new ArrayList<Taxes>();
			zomatoMenu.menu.categories = new ArrayList<Categories>();
			zomatoMenu.outlet_id = menu.getRestaurantId();
			zomatoMenu.menu.taxes = setMenuTaxes(menu.getRestaurantId());
			zomatoMenu.menu.charges = setCharges(menu.getRestaurantId(),
					zomatoMenu);
			zomatoMenu.menu.categories = setCategories(menu);

			try {
				logger.info(actionUrl + " :existing menu pushed: "
						+ response.message);
				response = callURL(auth_Key, actionUrl, zomatoMenu);
				if ("422".equalsIgnoreCase(response.message)) {
					/* For empty menu push */
					if ("add_menu".equalsIgnoreCase(menuAction)) {
						AddUpdateMenu zomatoMenuTest = new AddUpdateMenu();
						zomatoMenuTest.menu = new com.cookedspecially.dto.zomato.menu.Menu();
						zomatoMenuTest.menu.taxes = new ArrayList<Taxes>();
						zomatoMenuTest.menu.categories = new ArrayList<Categories>();
						zomatoMenuTest.outlet_id = menu.getRestaurantId();
						zomatoMenuTest.menu.taxes = setMenuTaxes(menu
								.getRestaurantId());
						zomatoMenuTest.menu.charges = setCharges(
								menu.getRestaurantId(), zomatoMenu);
						zomatoMenu.menu.categories = setTestCategories(menu);

						logger.info(actionUrl + ": Empty menu pushed");
						response = callURL(auth_Key, actionUrl, zomatoMenu);
						logger.info(actionUrl + ": Empty menu pushed response."
								+ response.message);

						zomatoMenu.menu.categories = setCategories(menu);

						logger.info(actionUrl + " :Full menu update pushed");
						actionUrl = prop.getProperty("updateMenu");
						response = callURL(auth_Key, actionUrl, zomatoMenu);

						logger.info(actionUrl + " :Full menu pushed response: "
								+ response.message);
					}
					if ("422".equalsIgnoreCase(response.message)) {
						logger.info("2nd: " + response.message);
						actionUrl = prop.getProperty("updateMenu");
						callURL(auth_Key, actionUrl, zomatoMenu);
					}
				}
			} catch (IOException | JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return response;
	}

	List<Taxes> setMenuTaxes(Integer restId) {
		List<Taxes> itemTaxesList = new ArrayList<Taxes>();
		this.taxTypeList = taxTypeService.listTaxTypesByRestaurantId(restId);
		for (TaxType taxType : taxTypeList) {
			if ("Default".equalsIgnoreCase(taxType.getDishType())) {
				Taxes itemTaxes = new Taxes();
				itemTaxes.tax_id = taxType.getTaxTypeId();
				itemTaxes.tax_is_active = 1;
				itemTaxes.tax_name = taxType.getName();
				itemTaxes.tax_value = taxType.getTaxValue();
				if (taxType.getChargeType() == ChargesType.PERCENTAGE) {
					itemTaxes.tax_type = ChargesType.PERCENTAGE.toString();
				} else {
					itemTaxes.tax_type = "FIXED";
				}
				itemTaxesList.add(itemTaxes);
			}
		}
		return itemTaxesList;
	}

	List<ItemTaxes> setItemTaxes(String dishType) {
		ItemTaxes itemTax = new ItemTaxes();
		List<ItemTaxes> itemTaxList = new ArrayList<ItemTaxes>();
		itemTax.order_type = order_Type.toUpperCase();
		itemTax.taxes = new ArrayList<Integer>();
		for (TaxType taxType : taxTypeList) {
			if ("Default".equalsIgnoreCase(taxType.getDishType())) {
				for (TaxType taxTypeAgain : taxTypeList) {
					if (!"Default".equalsIgnoreCase(taxTypeAgain.getDishType())) {
						if (taxTypeAgain.getOverridden() == taxType
								.getTaxTypeId()
								&& dishType.equalsIgnoreCase(taxType
										.getDishType())) {
							taxType = taxTypeAgain;
						}
					}
				}
				Taxes itemTaxes = new Taxes();
				itemTaxes.tax_id = taxType.getTaxTypeId();
				itemTaxes.tax_is_active = 1;
				itemTaxes.tax_name = taxType.getName();
				itemTaxes.tax_value = taxType.getTaxValue();
				if (taxType.getChargeType() == ChargesType.PERCENTAGE) {
					itemTaxes.tax_type = ChargesType.PERCENTAGE.toString();
				} else {
					itemTaxes.tax_type = "FIXED";
				}
				itemTax.taxes.add(itemTaxes.tax_id);
			}
		}
		itemTaxList.add(itemTax);
		return itemTaxList;
	}

	List<Categories> setTestCategories(Menu menu) {
		List<Categories> categoriesList = new ArrayList<Categories>();
		List<Section> sectionList = menu.getSections();
		Integer prioritizeCategoriesIndex=0;
		for (Section section : sectionList) {
			prioritizeCategoriesIndex++;
			Categories categories = new Categories();
			if (menu.getZomatoStatus() == Status.ACTIVE) {
				categories.category_is_active = 1;
			} else {
				categories.category_is_active = 0;
			}
			categories.category_name = section.getShortName();
			categories.category_id = section.getSectionId(); // *
			categories.category_image_url = "";
			categories.category_description = section.getDescription();
			categories.category_order=prioritizeCategoriesIndex;
			categories.items = new ArrayList<Items>();
			Integer prioritizeDishesIndex =0;
			for (Dish dish : section.getDishes()) {
				prioritizeDishesIndex++;
				if (dish != null) {
					Items item = new Items();
					item.item_id = dish.getDishId(); // *
					item.item_name = dish.getName(); // *
					item.item_order=prioritizeDishesIndex;
					if (dish.getPrice() == null) {
						item.item_final_price = 0.0f; // *
						item.item_unit_price = 0.0f; // *
					} else {
						item.item_final_price = dish.getPrice(); // *
						item.item_unit_price = dish.getPrice(); // *
					}
					item.item_image_url = "www.cookedspecially.com"
							+ dish.getImageUrl();
					if (dish.getDisabled()) {
						item.item_is_active = 0;
						continue;
					} else {
						item.item_is_active = 1;
					}
					item.item_tags = new ArrayList<Integer>();
					if (dish.getVegetarian()) {
						item.item_tags.add(1);
					} else if (!dish.getVegetarian()) {
						item.item_tags.add(2);
					}
					if (dish.isRecommended()) {
						item.item_is_recommended = 1;
					} else {
						item.item_is_recommended = 0;
					}
					item.item_short_description = StringEscapeUtils
							.escapeHtml4(Jsoup
									.parse(dish.getShortDescription()).text());
					item.item_taxes = setItemTaxes(dish.getDishType());
					categories.items.add(item);
				}
			}
			categoriesList.add(categories);
		}
		return categoriesList;
	}

	List<Categories> setCategories(Menu menu) {
		List<Categories> categoriesList = new ArrayList<Categories>();
		List<Section> sectionList = menu.getSections();
		Integer prioritizeCategoriesIndex=0;
		for (Section section : sectionList) {
			prioritizeCategoriesIndex++;
			Categories categories = new Categories();
			if (menu.getZomatoStatus() == Status.ACTIVE) {
				categories.category_is_active = 1;
			} else {
				categories.category_is_active = 0;
			}
			categories.category_name = section.getShortName();
			categories.category_id = section.getSectionId(); // *
			categories.category_image_url = "";
			categories.category_description = section.getDescription();
			categories.category_order=prioritizeCategoriesIndex;
			categories.items = new ArrayList<Items>();
			Integer prioritizeDishesIndex =0;
			for (Dish dish : section.getDishes()) {
				if (dish != null) {
					prioritizeDishesIndex++;
					Items item = new Items();
					item.item_id = dish.getDishId(); // *
					item.item_name = dish.getName(); // *
					item.item_order=prioritizeDishesIndex;
					if (dish.getPrice() == null) {
						item.item_final_price = 0.0f; // *
						item.item_unit_price = 0.0f; // *
					} else {
						item.item_final_price = dish.getPrice(); // *
						item.item_unit_price = dish.getPrice(); // *
					}
					item.item_image_url = "https://www.cookedspecially.com"
							+ dish.getImageUrl();
					if (dish.getDisabled()) {
						item.item_is_active = 0;
					} else {
						item.item_is_active = 1;
					}
					item.item_tags = new ArrayList<Integer>();
					if (dish.getVegetarian()) {
						item.item_tags.add(1);
					} else if (!dish.getVegetarian()) {
						item.item_tags.add(2);
					}
					if (dish.isRecommended()) {
						item.item_is_recommended = 1;
					} else {
						item.item_is_recommended = 0;
					}
					item.item_short_description = StringEscapeUtils
							.escapeHtml4(Jsoup
									.parse(dish.getShortDescription()).text());
					item.item_taxes = setItemTaxes(dish.getDishType());
					categories.items.add(item);
				}
			}
			categoriesList.add(categories);
		}
		return categoriesList;
	}

	/*
	 * List<Categories> setCategories(Menu menu) {
	 * 
	 * List<Categories> categoriesList = new ArrayList<Categories>();
	 * List<Section> sectionList = menu.getSections(); Categories categories =
	 * new Categories(); categories.category_id = menu.getMenuId(); // *
	 * categories.category_image_url = menu.getImageUrl(); if
	 * (menu.getZomatoStatus() == Status.ACTIVE) { categories.category_is_active
	 * = 1; } else { categories.category_is_active = 0; }
	 * categories.category_name = menu.getName(); // *
	 * categories.category_description = menu.getDescription();
	 * categories.has_subcategory = 1; categories.subcategories = new
	 * ArrayList<SubCategories>(); for (Section section : sectionList) {
	 * SubCategories subCategories = new SubCategories();
	 * subCategories.subcategory_id = section.getSectionId(); // *
	 * subCategories.subcategory_image_url = ""; if (section.isValid()) {
	 * subCategories.subcategory_is_active = 1; } else {
	 * subCategories.subcategory_is_active = 0; } subCategories.subcategory_name
	 * = section.getName(); // * subCategories.subcategory_description =
	 * section.getDescription(); subCategories.items = new ArrayList<Items>();
	 * for (Dish dish : section.getDishes()) { if (dish != null) { Items item =
	 * new Items(); item.item_id = dish.getDishId(); // * item.item_name =
	 * dish.getName(); // * item.item_final_price = dish.getPrice(); // *
	 * item.item_unit_price = dish.getPrice(); // * item.item_image_url =
	 * "www.cookedspecially.com" + dish.getImageUrl(); if (dish.getDisabled()) {
	 * item.item_is_active = 0; } else { item.item_is_active = 1; }
	 * if(dish.getVegetarian()){ item.item_tags.add(1); }else
	 * if(!dish.getVegetarian()){ item.item_tags.add(2); } //
	 * item.item_long_description = dish.getDescription(); // item
	 * .item_quantity=1; item.item_short_description = StringEscapeUtils
	 * .escapeHtml4(Jsoup .parse(dish.getShortDescription()).text());
	 * item.item_taxes = setItemTaxes(dish.getDishType());
	 * 
	 * subCategories.items.add(item); } }
	 * categories.subcategories.add(subCategories); }
	 * categoriesList.add(categories); return categoriesList; }
	 */

	public static ResponseDTO callURL(String auth_Key, String myURL, Object data)
			throws ClientProtocolException, IOException, JSONException {
		ResponseDTO respDTO = new ResponseDTO();
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(myURL);
		Gson gson = new Gson();
		post.setEntity(new StringEntity(gson.toJson(data), "UTF8"));
		post.setHeader("x-zomato-api-key", auth_Key);
		post.setHeader("Content-type", "application/json");
		logger.info(auth_Key + " key used and Call URL : " + myURL);
		logger.info(gson.toJson(data));
		// StringEntity params = new StringEntity(gson.toJson(data));

		// post.setEntity(params);
		HttpResponse response = client.execute(post);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		JSONObject o = new JSONObject(result.toString());
		logger.info(o);

		respDTO.result = (String) o.get("status");
		respDTO.message = (String) o.get("message");
		if ((Integer) o.get("code") == 422) {
			respDTO.message = ((Integer) o.get("code")).toString();
		}

		return respDTO;
	}

	List<ItemCharges> setCharges(Integer restId, AddUpdateMenu zomatoMenu) {
		List<ItemCharges> item = new ArrayList<ItemCharges>();
		List<DeliveryArea> deliveryAreaList = deliveryAreaService
				.listDeliveryAreasByResaurant(restId);
		for (DeliveryArea deliveryArea : deliveryAreaList) {
			if ("Zomato".equalsIgnoreCase(deliveryArea.getName())) {
				ItemCharges itemCharges = new ItemCharges();
				itemCharges.charge_id = deliveryArea.getId();
				itemCharges.charge_always_applicable = 1;
				itemCharges.has_tier_wise_values = 0;
				itemCharges.charge_applicable_below_order_amount = deliveryArea
						.getMinDeliveryThreshold();
				itemCharges.charge_is_active = 1;
				itemCharges.applicable_on = "ORDER";
				itemCharges.charge_name = "Delivery Charge";
				itemCharges.charge_type = "FIXED";
				List<TaxType> taxList = taxTypeService
						.listTaxTypesByRestaurantId(restId);
				float taxAmount = 0;
				if (taxList.size() > 0) {
					for (TaxType tax : taxList) {
						if ("default".equalsIgnoreCase(tax.getDishType())) {
							if (ChargesType.PERCENTAGE == tax.getChargeType()) {
								taxAmount = (deliveryArea.getDeliveryCharges() * tax
										.getTaxValue()) / 100;
							} else {
								taxAmount = deliveryArea.getDeliveryCharges()
										+ tax.getTaxValue();
							}
						}
					}
					itemCharges.charge_value = deliveryArea
							.getDeliveryCharges() + taxAmount;
				}
				item.add(itemCharges);
				zomatoMenu.menu.order_additional_charges = setOrderAdditionalCharges(deliveryArea);
			}
		}

		return item;
	}

	List<OrderAdditionalCharges> setOrderAdditionalCharges(DeliveryArea area) {
		List<OrderAdditionalCharges> orderACList = new ArrayList<OrderAdditionalCharges>();
		OrderAdditionalCharges orderAC = new OrderAdditionalCharges();
		orderAC.order_type = "DELIVERY";
		orderAC.charges = new ArrayList<Integer>();
		orderAC.charges.add(area.getId());
		orderACList.add(orderAC);
		return orderACList;
	}

	Properties readPropertieFile(Integer restaurantId) throws IOException {
		Restaurant rest = restaurantService.getRestaurant(restaurantId);
		String propFileName = "zomato_" + rest.getParentRestaurantId()
				+ ".properties";
		InputStream inputStream = getClass().getClassLoader()
				.getResourceAsStream(propFileName);
		Properties prop = new Properties();
		if (inputStream != null) {
			prop.load(inputStream);
			return prop;
		} else {
			throw new FileNotFoundException("property file '" + propFileName
					+ "' not found in the classpath");
		}
	}

	@Override
	@Transactional
	public Check getZomatoOrderById(Integer orderId) {
		// TODO Auto-generated method stub
		return checkDAO.getZomatoOrderById(orderId);
	}

	@Override
	@Transactional
	public ResponseDTO setZomatoRestaurantStatus(Restaurant restaurant_Org,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		Properties prop = null;
		String resultPath = null;
		ResponseDTO response = null;
		String orgStatus = null;
		List<Restaurant> restaurantList = new ArrayList<Restaurant>();
		if (restaurant_Org.getParentRestaurantId() == null) {
			orgStatus = restaurant_Org.getOpenFlag();
			restaurantList
					.addAll(restaurantService
							.listRestaurantByParentId(restaurant_Org
									.getRestaurantId()));
		} else {
			restaurantList.add(restaurant_Org);
		}
		for (Restaurant restaurant : restaurantList) {
			ZomatoRestaurantStatus zomatoRest = new ZomatoRestaurantStatus();
			if ("Closed".equalsIgnoreCase(restaurant.getOpenFlag())
					|| "Closed".equalsIgnoreCase(orgStatus)) {
				zomatoRest.outlet_delivery_status = 0;
				zomatoRest.outlet_delivery_status_update_reason = "Closed";
				zomatoRest.outlet_id = restaurant.getRestaurantId();
			} else if ("Always Open".equalsIgnoreCase(restaurant.getOpenFlag())
					|| "Open During Normal Hours".equalsIgnoreCase(restaurant
							.getOpenFlag())
					|| "Open During Normal Hours".equalsIgnoreCase(orgStatus)
					|| "Always Open".equalsIgnoreCase(orgStatus)) {
				zomatoRest.outlet_delivery_status = 1;
				zomatoRest.outlet_delivery_status_update_reason = "Opening";
				zomatoRest.outlet_id = restaurant.getRestaurantId();
			}
			try {
				prop = readPropertieFile(restaurant.getRestaurantId());
				String restaurantStatusAPI = prop
						.getProperty("setRestaurantStatus");
				String auth_Key = null;
				if (request != null) {
					String serverName = request.getServerName();
					resultPath = serverName;
				}
				if ("www.cookedspecially.com".equalsIgnoreCase(resultPath)) {
					auth_Key = prop.getProperty("access_key");
				} else {
					auth_Key = prop.getProperty("TestAccess_key");
				}
				response = callURL(auth_Key, restaurantStatusAPI, zomatoRest);
				logger.info(zomatoRest.outlet_delivery_status_update_reason
						+ " Zomato restaurant status updated : "
						+ response.result);

			} catch (IOException | JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return response;
	}

}
