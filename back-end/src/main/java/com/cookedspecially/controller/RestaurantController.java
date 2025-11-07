/**
 * 
 */
package com.cookedspecially.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
//import org.thymeleaf.spring3.SpringTemplateEngine;
import java.util.Properties;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.xml.sax.SAXException;

import springfox.documentation.annotations.ApiIgnore;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentReq;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentRequestType;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentResponseType;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsReq;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsRequestType;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsResponseType;
import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutReq;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutRequestType;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutResponseType;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;
import urn.ebay.apis.eBLBaseComponents.CurrencyCodeType;
import urn.ebay.apis.eBLBaseComponents.DoExpressCheckoutPaymentRequestDetailsType;
import urn.ebay.apis.eBLBaseComponents.LandingPageType;
import urn.ebay.apis.eBLBaseComponents.PaymentActionCodeType;
import urn.ebay.apis.eBLBaseComponents.PaymentDetailsType;
import urn.ebay.apis.eBLBaseComponents.SetExpressCheckoutRequestDetailsType;
import urn.ebay.apis.eBLBaseComponents.SolutionTypeType;

import com.cookedspecially.config.CSConstants;
import com.cookedspecially.domain.Check;
import com.cookedspecially.domain.CheckDishResponse;
import com.cookedspecially.domain.CheckResponse;
import com.cookedspecially.domain.CreditTransactions;
import com.cookedspecially.domain.Customer;
import com.cookedspecially.domain.DeliveryArea;
import com.cookedspecially.domain.Discount_Charges;
import com.cookedspecially.domain.Dish;
import com.cookedspecially.domain.FulfillmentCenter;
import com.cookedspecially.domain.JsonAddOn;
import com.cookedspecially.domain.JsonDish;
import com.cookedspecially.domain.Menu;
import com.cookedspecially.domain.MicroKitchenScreen;
import com.cookedspecially.domain.Nutrientes;
import com.cookedspecially.domain.Order;
import com.cookedspecially.domain.OrderAddOn;
import com.cookedspecially.domain.OrderSource;
import com.cookedspecially.domain.PaymentGatwayDetail;
import com.cookedspecially.domain.PaymentType;
import com.cookedspecially.domain.Restaurant;
import com.cookedspecially.domain.RestaurantInfo;
import com.cookedspecially.domain.Section;
import com.cookedspecially.domain.TaxType;
import com.cookedspecially.domain.User;
import com.cookedspecially.dto.OrderStatusDTO;
import com.cookedspecially.dto.ResponseDTO;
import com.cookedspecially.dto.saleRegister.TillDTO;
import com.cookedspecially.enums.check.AdditionalCategories;
import com.cookedspecially.enums.check.PaymentMode;
import com.cookedspecially.enums.check.Status;
import com.cookedspecially.enums.credit.CreditTransactionStatus;
import com.cookedspecially.enums.restaurant.ChargesType;
import com.cookedspecially.service.AddOnDishTypeService;
import com.cookedspecially.service.CashRegisterService;
import com.cookedspecially.service.CheckService;
import com.cookedspecially.service.CustomerCreditService;
import com.cookedspecially.service.CustomerService;
import com.cookedspecially.service.DeliveryAreaService;
import com.cookedspecially.service.DishTypeService;
import com.cookedspecially.service.FulfillmentCenterService;
import com.cookedspecially.service.MenuService;
import com.cookedspecially.service.MicroKitchenScreenService;
import com.cookedspecially.service.OrderService;
import com.cookedspecially.service.RestaurantService;
import com.cookedspecially.service.StockManagementService;
import com.cookedspecially.service.TaxTypeService;
import com.cookedspecially.service.UserService;
import com.cookedspecially.service.ZomatoService;
import com.cookedspecially.utility.Checksum;
import com.cookedspecially.utility.MailerUtility;
import com.cookedspecially.utility.StringUtility;
import com.google.gson.Gson;
import com.paypal.core.rest.PayPalRESTException;
import com.paypal.exception.ClientActionRequiredException;
import com.paypal.exception.HttpErrorException;
import com.paypal.exception.InvalidCredentialException;
import com.paypal.exception.InvalidResponseDataException;
import com.paypal.exception.MissingCredentialException;
import com.paypal.exception.SSLConfigurationException;
import com.paypal.sdk.exceptions.OAuthException;
import com.paytm.merchant.CheckSumServiceHelper;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;
import com.stripe.model.ExternalAccount;
import com.stripe.model.ExternalAccountCollection;

/**
 * @author shashank, rahul, abhishek
 *
 */
@Controller
@Component
@Api(description="Restaurant REST API's")
@RequestMapping("/restaurant")
public class RestaurantController {

	final static Logger logger = Logger.getLogger(RestaurantController.class);
    private static int MAXFILESIZE = 5;
    @Autowired
	private RestaurantService restaurantService;
	@Autowired
	private TaxTypeService taxTypeService;
	@Autowired
	private DishTypeService dishTypeService;
	@Autowired
	private CashRegisterController crc;
	@Autowired
	private OrderController orderController;
	@Autowired
    private AddOnDishTypeService addOnDishTypeService;
   
	@Autowired
	private JavaMailSenderImpl mailSender;
	
	@Autowired
	private StockManagementService stockManagementService; 
	
	@Autowired
	private SpringTemplateEngine templateEngine;
	
	@Autowired
	private FulfillmentCenterService fulfillmentCenterService;
	
	@Autowired
	private MicroKitchenScreenService microKitchenScreenService;
	
	@Autowired 
	private UserService userService;
	
	@Autowired
	private CheckService checkService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CashRegisterService cashRegisterService;
	
	@Autowired
	private DeliveryAreaService deliveryAreaService;
	
	@Autowired
	private ZomatoService zomatoService;
	
	@Autowired
	private MenuService menuService;
	
	private HttpServletRequest REQUEST;
	
	@Autowired
	@Qualifier("customerCreditAutomatedBilling")
	private CustomerCreditService customerCreditService;

    public static long compareTo(java.util.Date date1, java.util.Date date2) {
        return date1.getTime() - date2.getTime();
    }

    @RequestMapping("/")
    @ApiIgnore
	public String listRestaurants(Map<String, Object> map, HttpServletRequest request) {
		map.put("restaurant", new Restaurant());
		map.put("restaurantList", restaurantService.listRestaurantById((Integer) request.getSession().getAttribute("restaurantId")));
		return "restaurant";
	}
	
	@RequestMapping(value="/edit", method=RequestMethod.GET)
	@ApiIgnore
	public String editUser(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) {
		Object userIdObj = request.getSession().getAttribute("restaurantId");
		if(userIdObj != null) {
			map.put("user", restaurantService.getRestaurant((Integer) userIdObj));
			map.put("chargeTypes", ChargesType.values());
			map.put("timeZones", CSConstants.timeZoneIds);
			map.put("openFlag", CSConstants.openFlag);
			map.put("statusTypes", com.cookedspecially.enums.Status.values());
			ArrayList<String> countryName = new ArrayList<String>();
			String[] locales = Locale.getISOCountries();
			for (String countryCode : locales) {
			    Locale obj = new Locale("", countryCode);
			    countryName.add(obj.getDisplayCountry(Locale.ENGLISH));
			 }
			map.put("countryList", countryName);
			return "editUser";
		}
		return "redirect:/";
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ApiIgnore
	public String updateUser(Map<String, Object> map, @ModelAttribute("user")
            Restaurant restaurant, BindingResult result, @RequestParam("files[0]") MultipartFile portraitImage, @RequestParam("files[1]") MultipartFile landscapeImage,
                             @RequestParam("files[2]") MultipartFile appCacheIcon, @RequestParam("files[3]") MultipartFile buttonIcon, @RequestParam("files[4]") MultipartFile marketingImage, @RequestParam("files[5]") MultipartFile closeImageLink, @RequestParam("files[6]") MultipartFile headerImageUrl,HttpServletRequest request) {
		FileOutputStream fos = null;
		ArrayList<MultipartFile> files = new ArrayList<MultipartFile>();
		files.add(portraitImage);
		files.add(landscapeImage);
		files.add(appCacheIcon);
		files.add(buttonIcon);
		files.add(marketingImage);
		files.add(closeImageLink);
		files.add(headerImageUrl);

		if (files != null && files.size() == 7) {
			String[] fileUrls = new String[7];
			int iter = 0;
			for (MultipartFile file : files) {
				String fileUrl = null;
				if (iter==0) {
					fileUrl = restaurant.getBusinessPortraitImageUrl();
				} else if (iter==1) {
					fileUrl = restaurant.getBusinessLandscapeImageUrl();
				} else if (iter == 2) {
					fileUrl = restaurant.getAppCacheIconUrl();
				}
				else if (iter == 4) {
					fileUrl = restaurant.getMarketingImage();
				}
				else if (iter == 5) {
					fileUrl = restaurant.getCloseImageLink();
				}
				else if (iter == 6) {
					fileUrl = restaurant.getHeaderImageUrl();
				}
				else {
					fileUrl = restaurant.getButtonIconUrl();
				}

				if (!file.isEmpty()) {
					if (file.getSize() > MAXFILESIZE*1000*1000) {
						String rejectValueName = null;
						if (iter == 0) {
							rejectValueName = "businessPortraitImageUrl";
						} else if (iter == 1) {
							rejectValueName = "businessLandscapeImageUrl";
						} else if (iter == 2) {
							rejectValueName = "appCacheIconUrl";
						}
						else if (iter == 4) {
							rejectValueName = "marketingImage";
						}
						else if (iter == 5) {
							rejectValueName = "closeImageLink";
						}
						else if (iter == 6) {
							rejectValueName = "headerImageUrl";
						}
						else {
							rejectValueName = "buttonIconUrl";
						}
						result.rejectValue(rejectValueName, "error.upload.sizeExceeded", "You cannot upload the file of more than " + MAXFILESIZE + " MB");
						map.put("user", restaurant);
						return "editUser";
					}
					try {
						byte[] bytes = file.getBytes();
						String fileDir = File.separator + "static" + File.separator + restaurant.getRestaurantId() + File.separator ;
						String filePrefix = null;
						if (iter == 0) {
							filePrefix = "portrait";
						} else if (iter == 1) {
							filePrefix = "landscape";
						} else if (iter == 2) {
							filePrefix = "appCache";
						}
						 else if (iter == 4) {
							filePrefix = "marketingImage";
						}
						 else if (iter == 5) {
							filePrefix = "closeImageLink";
						}
						 else if (iter == 6) {
								filePrefix = "headerImageUrl";
							}
						else {
							filePrefix = "button";
						}

						fileUrl = fileDir + filePrefix + "_" + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9_.]", "_");
						fileUrls[iter] = fileUrl;
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
						try {
							restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
						} catch (UnsupportedEncodingException
								| MessagingException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						logger.info("Exception mail sent");
					} finally {
						if (fos != null) {
							try {
								fos.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								try {
									restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
								} catch (UnsupportedEncodingException
										| MessagingException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								logger.info("Exception mail sent");
								e.printStackTrace();
							}
						}
					}
				}
				iter++;
			}

			for (iter = 0; iter < 7; iter++) {
				String existingImageUrl = null;
				if (iter==0) {
					existingImageUrl = restaurant.getBusinessPortraitImageUrl();
				} else if (iter == 1) {
					existingImageUrl = restaurant.getBusinessLandscapeImageUrl();
				} else if (iter == 2) {
					existingImageUrl = restaurant.getAppCacheIconUrl();
				}
				else if (iter == 4) {
					existingImageUrl = restaurant.getMarketingImage();
				}
				else if (iter == 5) {
					existingImageUrl = restaurant.getCloseImageLink();
				}
				else if (iter == 6) {
					existingImageUrl = restaurant.getHeaderImageUrl();
                } else {
					existingImageUrl = restaurant.getButtonIconUrl();
				}

				String fileUrl = fileUrls[iter];
				if(!StringUtility.isNullOrEmpty(fileUrl)) {
					if (!fileUrl.equals(existingImageUrl) && !StringUtility.isNullOrEmpty(existingImageUrl) && existingImageUrl.startsWith("/")) {
						File oldFile = new File("webapps" + existingImageUrl);
						if (oldFile.exists()) {
							oldFile.delete();
						}
					}
					if (iter == 0) {
						restaurant.setBusinessPortraitImageUrl(fileUrl);
					} else if (iter == 1) {
						restaurant.setBusinessLandscapeImageUrl(fileUrl);
					} else if (iter == 2) {
						restaurant.setAppCacheIconUrl(fileUrl);
                    } else if (iter == 4) {
						restaurant.setMarketingImage(fileUrl);
					}
					else if (iter == 5) {
						restaurant.setCloseImageLink(fileUrl);
					}
					else if (iter == 6) {
						restaurant.setHeaderImageUrl(fileUrl);
					}
					else {
						restaurant.setButtonIconUrl(fileUrl);
					}
				}
			}
		}

		Restaurant dbRestaurant= restaurantService.getRestaurant(restaurant.getRestaurantId());
		if(dbRestaurant != null){
			restaurant.setInvoicePrefix(dbRestaurant.getInvoicePrefix());
			restaurant.setInvoiceStartCounter(dbRestaurant.getInvoiceStartCounter());
			//dbRestaurant = null;
		}
		if("Closed".equalsIgnoreCase(dbRestaurant.getOpenFlag()) && ("Always Open".equalsIgnoreCase(restaurant.getOpenFlag())|| "Open During Normal Hours".equalsIgnoreCase(restaurant.getOpenFlag()))){
			ResponseDTO resp  = zomatoService.setZomatoRestaurantStatus(restaurant, request);
			logger.info("Opening restaurant : "+resp.result);
		}else if(("Always Open".equalsIgnoreCase(dbRestaurant.getOpenFlag()) || "Open During Normal Hours".equalsIgnoreCase(dbRestaurant.getOpenFlag())) && "Closed".equalsIgnoreCase(restaurant.getOpenFlag())){
			ResponseDTO resp = zomatoService.setZomatoRestaurantStatus(restaurant, request);
			logger.info("Closing restaurant : "+resp.result);
		}
        restaurantService.addRestaurant(restaurant);
        return "redirect:/manageRestaurant.jsp";
	}
	
	@RequestMapping(value="/redirectToPayTm", method=RequestMethod.GET)
	@ApiIgnore
	public String getCheckRedirectToPaytm(@RequestParam("restaurantId") String resturant,@RequestParam("checkId") String orderId, Map<String, String> map, HttpServletRequest request) throws IOException {
		String Website_name=null;
		String MID=null;
		String Merchant_Key=null;
		String Industry_type_ID=null;
		String Channel_ID=null;
		String redirectURL=null;
		CheckSumServiceHelper checkSumServiceHelper =CheckSumServiceHelper.getCheckSumServiceHelper();
		String checkSum=null;
		int id=Integer.parseInt(orderId);
		Check check=checkService.getCheck(id);
		Restaurant rest = restaurantService.getRestaurant(check.getRestaurantId());
		CheckResponse checkResponse = new CheckResponse(check,taxTypeService,null,rest);
		logger.info("Redirected to Payment Gateway DB check Id :"+id);
		double amount = Math.round(checkResponse.getRoundedOffTotal() + checkResponse.getCheckCreditBalance());
		Order order= orderService.getOrder(check.getOrderId());
		order.setPaymentStatus(PaymentMode.PAYTM_PENDING.toString());
		orderService.addOrder(order);
		//String am="10";
		TreeMap<String,String> parameters = new TreeMap<String,String>();

		Properties prop = new Properties();
		String propFileName = "paytmDetails.properties";

		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}
		 Website_name = prop.getProperty("Website_name");
		 MID = prop.getProperty("MID");

		 Merchant_Key = prop.getProperty("Merchant_Key");
		 Industry_type_ID = prop.getProperty("Industry_type_ID");
		 Channel_ID = prop.getProperty("Channel_ID");
		 redirectURL = prop.getProperty("redirectURL");
		parameters.put("REQUEST_TYPE","DEFAULT");
		parameters.put("MID", MID); // Merchant ID (MID) provided by Paytm
		parameters.put("ORDER_ID", orderId); // Merchant’s order id
		parameters.put("CUST_ID", check.getCustomerId().toString()); // Customer ID registered with merchant

		parameters.put("TXN_AMOUNT",Double.toString(amount));
		//parameters.put("TXN_AMOUNT",am.toString());
		parameters.put("CHANNEL_ID", Channel_ID);
		parameters.put("INDUSTRY_TYPE_ID",Industry_type_ID); //Provided by Paytm
		parameters.put("WEBSITE",Website_name); //Provided by Paytm
		try {
            checkSum = checkSumServiceHelper.genrateCheckSum(Merchant_Key, parameters);
        } catch (Exception e) {
			// TODO Auto-generated catch block
        	try {
				restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			logger.info("Exception mail sent");
			e.printStackTrace();
		}
		map.put("ORDER_ID",orderId);
		map.put("REQUEST_TYPE", "DEFAULT");
		map.put("MID", MID);
		map.put("CUST_ID",check.getCustomerId().toString());
		map.put("TXN_AMOUNT",Double.toString(amount));
		//map.put("TXN_AMOUNT",am.toString());
		map.put("CHANNEL_ID", Channel_ID);
		map.put("redirectURL",redirectURL);
		map.put("INDUSTRY_TYPE_ID",Industry_type_ID);
		map.put("WEBSITE",Website_name);
		map.put("CHECKSUMHASH",checkSum);

		return "redirectPayTm";
	}
	
	@RequestMapping(value="/getCardDetails", method=RequestMethod.GET)
	
	public @ResponseBody Map<String,Object> getCardDetails(HttpServletRequest request, HttpServletResponse response, @RequestParam String phone, @RequestParam String orgId) throws ParseException, JSONException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException, IOException {
		/*String phone  = request.getParameter("phone");
		String orgId = request.getParameter("orgId");*/
		String message;
		Map<String,Object> map  =  new HashMap<String, Object>();

		List<Customer> customerList   =  customerService.getCustomerByParams(null, null, phone, Integer.parseInt(orgId));

		if(customerList!=null){
			for(Customer customer :customerList){
				Properties prop = new Properties();
				String propFileName = "stripe_"+orgId+".properties";

				InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
				if (inputStream != null) {
					prop.load(inputStream);
				} else {
					//throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
					prop.put("secretKey", "sk_test_AW5wZRXDZq3wLPUI7khdd8Rm00RL3uT8Hu");
				}
				Stripe.apiKey   = prop.getProperty("secretKey");

				String stripeId = customer.getStripId();

				if(stripeId !=null){
					map.put("status","true");

					Map<String, Object> cardParams = new HashMap<String, Object>();
					cardParams.put("limit", 10);
					cardParams.put("object", "card");
					ExternalAccountCollection s =  com.stripe.model.Customer.retrieve(stripeId).getSources().all(cardParams);
					map.put("cards",s);
					map.put("status",true);
				}else {
					map.put("status",false);
					map.put("message","Customer don't have any saved information");
					return map;
		}

		return map;
	}
	}
		map.put("status",false);
		map.put("message","Customer doesn't exist");
		return map;
	}
	
	@RequestMapping(value="/payWithStripe", method=RequestMethod.POST)
	public String payWithStripe(@RequestParam("restaurantId") String resturantId,
			@RequestParam("checkId") String orderId, @RequestParam String cardId, @ApiParam(value="True or False", required=false) @RequestParam String saveDetails,  Map<String, Object> map,ModelMap model, HttpServletRequest request) throws IOException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, MessagingException {

       // String cardId = request.getParameter("cardId");
		//String saveDetails = request.getParameter("saveDetails");
		if(saveDetails==null){
			saveDetails="false";
		}
		int id=Integer.parseInt(orderId);
		Check check=checkService.getCheck(id);
		int restId =  Integer.parseInt(resturantId);
		Restaurant rest  =  restaurantService.getRestaurant(restId);
		Integer orgId = rest.getParentRestaurantId();

		// Get the credit card details submitted by the form
		String token = request.getParameter("stripeToken");
		Properties prop = new Properties();
		String propFileName = "stripe_"+orgId+".properties";

		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			//throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			prop.put("secretKey", "sk_test_AW5wZRXDZq3wLPUI7khdd8Rm00RL3uT8Hu");
		}
		 Stripe.apiKey   = prop.getProperty("secretKey");

		// Create the charge on Stripe's servers - this will charge the user's card
		try {
			/////////////////
			Charge charge = null;
			//	fetch customer
			Customer cust =  customerService.getCustomer(check.getCustomerId());
			com.stripe.model.Customer saveCustomer = null;
			if(cardId==null){
			if(cust.getStripId()==null && Boolean.parseBoolean(saveDetails)){
				// Create a Customer
				Map<String, Object> customerParams = new HashMap<String, Object>();
				 customerParams.put("source", token);
				 customerParams.put("description", "Example customer");
				 saveCustomer = com.stripe.model.Customer.create(customerParams);

				 List<ExternalAccount> ea=saveCustomer.getSources().getData();
				 String card="" ;
				 for(ExternalAccount ss : ea){
					 card = ss.getId();
				 }
				//Deducting amount from card
				double allTheMoney = check.getRoundOffTotal();
				long allCents = Math.round(allTheMoney * 100);
				Map<String, Object> chargeParams = new HashMap<String, Object>();
				  chargeParams.put("amount",allCents); // amount in cents, again
				  chargeParams.put("currency", rest.getCurrency());
				  chargeParams.put("customer", saveCustomer.getId()); // Previously stored, then retrievedother
				  chargeParams.put("card", card);
				  charge  = Charge.create(chargeParams);
			      logger.info("deduction from selected card : "+cardId);
				  cust.setStripId(saveCustomer.getId());
				  customerService.addCustomer(cust);
				  logger.info("Saving Card details");
				  //System
			}else if(cust.getStripId()!=null && Boolean.parseBoolean(saveDetails)){
				//Saving new  card details
				 com.stripe.model.Customer customerCard = com.stripe.model.Customer.retrieve(cust.getStripId());
				 Map<String, Object> params = new HashMap<String, Object>();
				 params.put("source", token);
				 ExternalAccount ea =  customerCard.getSources().create(params);


                //Deducting amount from card
				 double allTheMoney = check.getRoundOffTotal();
					long allCents = Math.round(allTheMoney * 100);
					Map<String, Object> chargeParams = new HashMap<String, Object>();
					  chargeParams.put("amount",allCents); // amount in cents, again
					  chargeParams.put("currency", rest.getCurrency());
					  chargeParams.put("customer", customerCard.getId()); // Previously stored, then retrievedother
					  chargeParams.put("card", ea.getId());
					  charge  = Charge.create(chargeParams);
				      logger.info("deduction from selected card : "+cardId);
			      logger.info("deduction from selected card : "+cardId);
				  logger.info("Saving Card details");

				 // charge = Charge.create(chargeParams);
			}else {
				double allTheMoney = check.getRoundOffTotal();
				long allCents = Math.round(allTheMoney * 100);
				Map<String, Object> chargeParams = new HashMap<String, Object>();
				  chargeParams.put("amount",allCents); // amount in cents, again
				  chargeParams.put("currency",rest.getCurrency());
				  chargeParams.put("source", token);
				  chargeParams.put("description",rest.getBussinessName()+" payment");

				Map<String, String> initialMetadata = new HashMap<String, String>();
				  initialMetadata.put("order_id",check.getCheckId().toString());
				  chargeParams.put("metadata", initialMetadata);
				  charge = Charge.create(chargeParams);
				  logger.info("Not saving any details");
			}
			}
			else if(cardId!=null && cust.getStripId()!=null){
				// Charging selected card
				double allTheMoney = check.getRoundOffTotal();
				long allCents = Math.round(allTheMoney * 100);
				Map<String, Object> otherChargeParams = new HashMap<String, Object>();
				 otherChargeParams.put("amount", allCents); // $15.00 this time
				 otherChargeParams.put("currency", rest.getCurrency());
				 otherChargeParams.put("customer", cust.getStripId()); // Previously stored, then retrievedother
				 otherChargeParams.put("card", cardId);
				 charge  = Charge.create(otherChargeParams);
				 logger.info("deduction from selected card : "+cardId);
			}

		  logger.info("paid info : "+charge.getPaid());
		  if(charge.getPaid()){
			  Customer customer  = customerService.getCustomer(check.getCustomerId());
			  Order order= orderService.getOrder(check.getOrderId());
			  	logger.info("Transaction Success");
				order.setPaymentStatus(PaymentMode.PG.toString());
				check.setStatus(Status.Paid);
				check.setPaymentDetail(charge.getDescription());
				check.setTransactionId(charge.getId());
				check.setTransactionStatus(charge.getStatus());
				checkService.addCheck(check);
				orderService.addOrder(order);
				Check ck  =  checkService.getCheck(check.getCheckId());
				populateDishes(ck,map, (Model)model,rest);
				model.addAttribute("paymentStatus","paid");
				orderController.emailCheckFromServer(request,check.getCheckId(),customer.getEmail(),"defaultemailbill",null,null,null,null,0);
				logger.info(charge.getStatus()+" email sent for check Id "+check.getCheckId()+"---"+charge.getDescription()+"----"+charge.getReceiptNumber()+"---"+charge.getId()+"--"+charge.getBalanceTransaction()+"--"+charge.getOutcome());
              return "custom/defaultbill";
          }else {
			  Customer customer  = customerService.getCustomer(check.getCustomerId());
			  Order order= orderService.getOrder(check.getOrderId());
			  	logger.info("Transaction failed");
				order.setPaymentStatus(charge.getStatus());
				order.setStatus(com.cookedspecially.enums.order.Status.CANCELLED);
				check.setStatus(Status.Cancel);
				check.setPaymentDetail(charge.getDescription());
				check.setTransactionId(charge.getId());
				check.setTransactionStatus(charge.getStatus());
				checkService.addCheck(check);
				orderService.addOrder(order);
				Check ck =  checkService.getCheck(check.getCheckId());
				populateDishes(ck,map, (Model)model,rest);
				model.addAttribute("RESPMSG",charge.getFailureMessage());
				orderController.emailCheckFromServer(request,check.getCheckId(),customer.getEmail(),"defaultemailbill",null,null,null,null,0);
				logger.info(charge.getStatus()+" email sent for check Id "+check.getCheckId()+"/"+check.getInvoiceId()+"---"+charge.getDescription()+"----"+charge.getReceiptNumber()+"---"+charge.getId()+"--"+charge.getBalanceTransaction()+"--"+charge.getOutcome());
				logger.info("email sent for check Id "+check.getCheckId()+"/"+check.getInvoiceId());
			  return "responsePG";
		  }
		}catch (CardException e) {
		  // The card has been declined
			restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
			logger.info("Exception mail sent");
		}
		return "responsePG";
	}
	
	private void populateDishes(Check check , Map<String,Object> map,Model model,Restaurant rest){
		if (check != null) {
			Restaurant restaurant= restaurantService.getRestaurant(check.getRestaurantId());

			model.addAttribute("CONTACT",restaurant.getBussinessPhoneNo());
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(restaurant.getTimeZone()));
			cal.setTime(check.getOpenTime());
			DateFormat formatter1;
			formatter1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			formatter1.setTimeZone(cal.getTimeZone());
			map.put("checkDate", formatter1.format(cal.getTime()));
		   CheckResponse checkResponse = new CheckResponse(check,taxTypeService,null,rest);
		    logger.info("setting json tax object"+checkResponse.getRoundedOffTotal());
			logger.info("setting Invoice rounndOffTotal."+checkResponse.getRoundedOffTotal());
			if(restaurant!=null){
			if(restaurant.isRoundOffAmount()){
				check.setRoundOffTotal(Math.round(checkResponse.getRoundedOffTotal()));
				checkResponse.setRoundedOffTotal(Math.round(checkResponse.getRoundedOffTotal()));
			}else{
		    	check.setRoundOffTotal(checkResponse.getRoundedOffTotal());
			}
			}
			map.put("checkRespone", checkResponse);
			map.put("restaurant", restaurant);
			if (check.getCustomerId() > 0) {
				Customer customer = customerService.getCustomer(check.getCustomerId());
				map.put("customer", customer);
			} else if (check.getTableId() > 0) {
				map.put("tableId", check.getTableId());
			}
			Map<String, JsonDish> itemsMap = new TreeMap<String, JsonDish>();
			List<CheckDishResponse> items = checkResponse.getItems();

			List<JsonAddOn> jsonAdd =  new ArrayList<JsonAddOn>();
			for (CheckDishResponse item : items) {
				if (itemsMap.containsKey(item.getName())) {
					JsonDish jsonDish = itemsMap.get(item.getName());
					jsonDish.setPrice(jsonDish.getPrice() + item.getPrice());
					jsonDish.setQuantity(jsonDish.getQuantity() + 1);
				}
				else {
					JsonDish jsonDish = new JsonDish();
					jsonDish.setQuantity(1);
					jsonDish.setName(item.getName());
					jsonDish.setId(item.getDishId());
					jsonDish.setPrice(item.getPrice());
					List<OrderAddOn> orderAddOn = item.getAddOnresponse();
					if(orderAddOn!=null){
					for( OrderAddOn oad : orderAddOn){
						JsonAddOn jsonAddOn = new JsonAddOn();
						jsonAddOn.setId(oad.getAddOnId());
						jsonAddOn.setDishId(item.getDishId());
						jsonAddOn.setName(oad.getName());
						jsonAddOn.setPrice(oad.getPrice());
						jsonAddOn.setQuantity(oad.getQuantity());
						jsonAdd.add(jsonAddOn);
						jsonDish.setAddOns(jsonAdd);
					}
				}
					itemsMap.put(item.getName(), jsonDish);
				}
			}
			map.put("itemsMap", itemsMap);
		}
	}
	
	@RequestMapping(value="/redirectToCitrus" , method=RequestMethod.GET)
	public String getCheckRedirectToCitrus(@RequestParam("restaurantId") String resturant,@RequestParam("checkId") String orderId, Map<String, String> map, HttpServletRequest request) throws IOException {

		String scheme = request.getScheme();
		String serverName = request.getServerName();
		int serverPort = request.getServerPort();
		String contextPath = request.getContextPath();  // includes leading forward slash
		String resultPath = scheme + "://" + serverName + ":" + serverPort + contextPath;

		int id=Integer.parseInt(orderId);
		Check check=checkService.getCheck(id);
		Restaurant rest = restaurantService.getRestaurant(check.getRestaurantId());
		CheckResponse checkResponse = new CheckResponse(check,taxTypeService,null,rest);
		logger.info("Redirected to Citrus Gateway DB check Id :"+id);
		double orderAmount =0;
		//Long orderAmount =(long) 1;
		Order order= orderService.getOrder(check.getOrderId());
		order.setPaymentStatus(PaymentMode.PG_PENDING.toString());
		orderService.addOrder(order);
		check.setStatus(Status.Pending);
		if(rest.isRoundOffAmount()){
			orderAmount= Math.round(checkResponse.getRoundedOffTotal() + checkResponse.getCheckCreditBalance());
			//check.setRoundOffTotal(orderAmount);
		}else {
			orderAmount= Math.round(checkResponse.getRoundedOffTotal()+checkResponse.getCheckCreditBalance());
		}
		Properties prop = new Properties();
		String propFileName = "citrusDetails.properties";
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}

        checkService.addCheck(check);
		 String vanityUrl  = prop.getProperty("vanityUrl");
		 String formPostUrl = "https://www.citruspay.com/"+vanityUrl;
		 String secret_key = prop.getProperty("secret_key");
		 String currency = prop.getProperty("currency");
		 String responseBack = prop.getProperty("responseBack");
		 String notifyUrl = prop.getProperty("notifyUrl");
		Customer customer = customerService.getCustomer(check.getCustomerId());
		String email  =  customer.getEmail();
		logger.info("Customer Email :"+email);
		logger.info("Vanity URL :"+vanityUrl);

		String merchantTxnId = orderId;

        logger.info(vanityUrl+": "+orderAmount+" : "+merchantTxnId+" : "+currency);
        String data=vanityUrl+orderAmount+merchantTxnId+currency;

        javax.crypto.Mac mac = null;
		try {
			mac = javax.crypto.Mac.getInstance("HmacSHA1");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			try {
				restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			logger.info("Exception mail sent");
			e.printStackTrace();
		}
         try {
			mac.init(new javax.crypto.spec.SecretKeySpec(secret_key.getBytes(), "HmacSHA1"));
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			try {
				restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			logger.info("Exception mail sent");
			e.printStackTrace();
		}
         byte[] hexBytes = new org.apache.commons.codec.binary.Hex().encode(mac.doFinal(data.getBytes()));
         String securitySignature = new String(hexBytes, "UTF-8");
         logger.info("security key "+securitySignature);
         map.put("formPostUrl",formPostUrl);
         map.put("merchantTxnId",merchantTxnId);
         map.put("orderAmount",Double.toString(orderAmount));
         map.put("securitySignature",securitySignature);
         map.put("responseBack",resultPath+""+responseBack);
         map.put("notifyUrl",resultPath+""+notifyUrl);
         map.put("currency",currency);
         map.put("email",email);

        return "redirectCitrus";
	}
	
	@RequestMapping(value="/redirectToMobiKwik" ,method=RequestMethod.GET)
	public String getCheckRedirectToMobiKwik(@RequestParam("restaurantId") String resturantId,@RequestParam("checkId") String orderId, Map<String, String> map, HttpServletRequest request) throws IOException {

        String scheme = request.getScheme();
		String serverName = request.getServerName();
		int serverPort = request.getServerPort();
		String contextPath = request.getContextPath();  // includes leading forward slash
		String resultPath = scheme + "://" + serverName + ":" + serverPort + contextPath;

        int id=Integer.parseInt(orderId);
		Check check=checkService.getCheck(id);
		Restaurant rest = restaurantService.getRestaurant(check.getRestaurantId());
		CheckResponse checkResponse = new CheckResponse(check,taxTypeService,null,rest);
		logger.info("Redirected to mobikwik Gateway DB check Id :"+id);
		Double orderAmount = 	(double) Math.round(check.getRoundOffTotal() + check.getCreditBalance());
		//Long orderAmount =(long) 1;
		Order order= orderService.getOrder(check.getOrderId());
		order.setPaymentStatus(PaymentMode.WALLET_PENDING.toString());
		orderService.addOrder(order);
		check.setStatus(Status.Pending);

        Properties prop = new Properties();
		String propFileName = "mobiKwikDetails.properties";
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

        if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}

        checkService.addCheck(check);
		 String vanityUrl  = prop.getProperty("vanityUrl");
		 String SecretKey = prop.getProperty("SecretKey");
		 String actionUrl = prop.getProperty("redirectUrl");
		 String returnUrl = prop.getProperty("returnUrl");
		 String merchantID = prop.getProperty("MerchantID");
		 String merchantName = prop.getProperty("merchantName");
		 Customer customer = customerService.getCustomer(check.getCustomerId());
		 String email  =  customer.getEmail();
		 logger.info("Customer Email :"+email);
		 String merchantTxnId = check.getCheckId().toString();
		 returnUrl = resultPath +""+returnUrl;
         String allParamValue = ("'" +customer.getPhone()+ "''"+orderAmount.toString()+"''" +merchantTxnId+ "''" +returnUrl + "''" + merchantID + "'").trim();
         logger.info("amount : "+orderAmount.toString());
         String checksum12=null;
		try {
			checksum12 = Checksum.calculateChecksum(SecretKey, allParamValue);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			logger.info("Exception mail sent");
		}

        map.put("orderId",merchantTxnId);
         map.put("amount",orderAmount.toString());
         map.put("checksum",checksum12);
         map.put("returnUrl",returnUrl);
         map.put("cell",customer.getPhone());
         map.put("mid",merchantID);
         map.put("actionUrl",actionUrl);
         map.put("merchantName",merchantName);

        return "redirectMobiKwik";
	}
	
	@RequestMapping(value="/responseByMobiKwik",method=RequestMethod.POST)
	@ApiIgnore
	public String getCheckResponseByMobiKwik(@ModelAttribute("SpringWeb")PaymentGatwayDetail pgDetails,  ModelMap model,Map<String, Object> map,HttpServletRequest request) throws Exception {
		logger.info("Into mobikwik response");
		Properties prop = new Properties();
		String propFileName = "mobiKwikDetails.properties";
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }
        //String MerchantName = prop.getProperty("MerchantName");
		String merchant_id = prop.getProperty("MerchantID");
		String secret_key = prop.getProperty("SecretKey");
		//String OutputURL=prop.getProperty("OutputURL");
		String checkStatus=prop.getProperty("checkStatus");

		String statuscode = request.getParameter("statuscode");
		String orderid = request.getParameter("orderid");
		String referenceid = request.getParameter("refid");
		String amount = request.getParameter("amount");
		String message = request.getParameter("statusmessage");
		String checksum = request.getParameter("checksum");
		String ordertype = request.getParameter("ordertype");

        String mid = request.getParameter("mid");
		int checkId = Integer.parseInt(orderid);
		Check check= checkService.getCheck(checkId);
		Restaurant rest = restaurantService.getRestaurant(check.getRestaurantId());
		Order order= orderService.getOrder(check.getOrderId());
		Double mTotal = Double.parseDouble(amount);
		boolean isChecksumValid = false;
		String allParamValue = ("'" +statuscode+ "''" +orderid+ "''" +mTotal.toString()+ "''" +message+ "''"+mid+"'"+referenceid).trim();
		Double roundOff=  check.getRoundOffTotal() + check.getCreditBalance();
		roundOff = Double.parseDouble(roundOff.toString());
		if(checksum != null){
			isChecksumValid = Checksum.verifyChecksum(secret_key, allParamValue, checksum);
		}
		Map<String,String> returnMap =null;
		 CheckResponse checkResponse=null;
		if(isChecksumValid || statuscode.equals("0")){
			if(statuscode.equals("0")){
				Double amountReceived = Double.parseDouble(amount);
				logger.info("status Code : "+statuscode+" orderid :"+orderid +" amount : "+mTotal.toString()+" roundoff :"+roundOff.toString()+" message "+message+" checksum "+isChecksumValid);
				logger.info(orderid.equalsIgnoreCase(check.getCheckId().toString()) +"- may b--"+amountReceived.toString().equalsIgnoreCase(roundOff.toString()));
				if(orderid.equalsIgnoreCase(check.getCheckId().toString()) && amountReceived.toString().equalsIgnoreCase(roundOff.toString()) ){
					returnMap  = Checksum.verifyTransaction(merchant_id, check.getCheckId().toString(),roundOff.toString(),secret_key,checkStatus);
					if (returnMap.get("flag").equals("true")|| statuscode.equals("0")) {
						check.setStatus(Status.Paid);
						order.setPaymentStatus(PaymentMode.MOBIKWIK_WALLET.toString());
						logger.info(message+" : "+check.getCheckId()+"/"+check.getInvoiceId());
						orderService.addOrder(order);
					}
					else{
                        logger.info("Transaction Canceled :" + message);
                        order.setPaymentStatus(PaymentMode.WALLET_PENDING.toString());
						check.setStatus(Status.Unpaid);
						order.setStatus(com.cookedspecially.enums.order.Status.CANCELLED);
						try {
							orderController.restoreStock(check,0);
						} catch (ParseException e) {
							e.printStackTrace();
							restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
							logger.info("Exception mail sent");
						}
						check.setBill(0);
						check.setRoundOffTotal(0);
						check.setCreditBalance(0);
						check.setOutCircleDeliveryCharges(0);
						check.setStatus(Status.Cancel);
						order.setStatus(com.cookedspecially.enums.order.Status.CANCELLED);
						logger.info("order status :"+order.getStatus());
						orderService.addOrder(order);
						logger.info("Check cancelled id :"+check.getCheckId()+"/"+check.getInvoiceId());
					}
				}
				else{
                    logger.info("Transaction Canceled :" + message);
                    order.setPaymentStatus(PaymentMode.WALLET_PENDING.toString());
					check.setStatus(Status.Unpaid);
					order.setStatus(com.cookedspecially.enums.order.Status.CANCELLED);
					try {
						orderController.restoreStock(check,0);
					} catch (ParseException e) {
						e.printStackTrace();
						restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
						logger.info("Exception mail sent");
					}
					check.setBill(0);
					check.setRoundOffTotal(0);
					check.setOutCircleDeliveryCharges(0);
					check.setStatus(Status.Cancel);
					order.setStatus(com.cookedspecially.enums.order.Status.CANCELLED);
					logger.info("order status :"+order.getStatus());
					orderService.addOrder(order);
					logger.info("Check cancelled id :"+check.getCheckId()+"/"+check.getInvoiceId());
				}
			}
			else{
				logger.info("Transaction Canceled "+message);
				check.setStatus(Status.Unpaid);
				order.setPaymentStatus(PaymentMode.WALLET_PENDING.toString());
				order.setStatus(com.cookedspecially.enums.order.Status.CANCELLED);
				try {
					orderController.restoreStock(check,0);
				} catch (ParseException e) {
					e.printStackTrace();
					restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
					logger.info("Exception mail sent");
				}
				check.setBill(0);
				check.setRoundOffTotal(0);
				check.setOutCircleDeliveryCharges(0);
				check.setStatus(Status.Cancel);
				order.setStatus(com.cookedspecially.enums.order.Status.CANCELLED);
				logger.info("order status :"+order.getStatus());
				orderService.addOrder(order);
                logger.info("Check cancelled id :" + check.getCheckId()+"/"+check.getInvoiceId());
            }
        }else{
			order.setPaymentStatus(PaymentMode.WALLET_PENDING.toString());
			check.setStatus(Status.Pending);
			orderService.addOrder(order);
			logger.info("Transaction status Pending");
		}
		Customer customer = new Customer();
		logger.info("response recieved from MobiKwik check Id :"+checkId+"/"+check.getInvoiceId());
		logger.info("PG transaction Id :" +referenceid);
		check.setTransactionId(referenceid);
		check.setTransactionStatus(message);
		check.setResponseCode(statuscode);
		checkService.addCheck(check);

        model.addAttribute("TXNID",referenceid);
        model.addAttribute("ORDERID", orderid);
        model.addAttribute("TXNAMOUNT",amount);
			model.addAttribute("RESPCODE",statuscode);
			model.addAttribute("RESPMSG",message);
			model.addAttribute("MOBIKWIK","Active");

        if (check != null) {
				Restaurant restaurant= restaurantService.getRestaurant(check.getRestaurantId());
				model.addAttribute("CONTACT",restaurant.getBussinessPhoneNo());
				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(restaurant.getTimeZone()));
				cal.setTime(check.getOpenTime());
				DateFormat formatter1;
				formatter1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				formatter1.setTimeZone(cal.getTimeZone());
				map.put("checkDate", formatter1.format(cal.getTime()));

            checkResponse = new CheckResponse(check,taxTypeService,null,rest);
			    logger.info("setting json tax object"+checkResponse.getRoundedOffTotal());
				logger.info("setting Invoice rounndOffTotal."+checkResponse.getRoundedOffTotal());
				if(restaurant!=null){
				if(restaurant.isRoundOffAmount()){
					check.setRoundOffTotal(Math.round(checkResponse.getRoundedOffTotal()));
					checkResponse.setRoundedOffTotal(Math.round(checkResponse.getRoundedOffTotal()));
				}else{
			    	check.setRoundOffTotal(checkResponse.getRoundedOffTotal());
				}
				}
				map.put("checkRespone", checkResponse);
				map.put("restaurant", restaurant);
				if (check.getCustomerId() > 0) {
					customer = customerService.getCustomer(check.getCustomerId());
					map.put("customer", customer);
				} else if (check.getTableId() > 0) {
					map.put("tableId", check.getTableId());
				}
				Map<String, JsonDish> itemsMap = new TreeMap<String, JsonDish>();
				List<CheckDishResponse> items = checkResponse.getItems();
				List<JsonAddOn> jsonAdd =  new ArrayList<JsonAddOn>();
				for (CheckDishResponse item : items) {
					if (itemsMap.containsKey(item.getName())) {
						JsonDish jsonDish = itemsMap.get(item.getName());
						jsonDish.setPrice(jsonDish.getPrice() + item.getPrice());
						jsonDish.setQuantity(jsonDish.getQuantity() + 1);
					}
					else {
						JsonDish jsonDish = new JsonDish();
						jsonDish.setQuantity(1);
						jsonDish.setName(item.getName());
						jsonDish.setId(item.getDishId());
						jsonDish.setPrice(item.getPrice());
						List<OrderAddOn> orderAddOn = item.getAddOnresponse();
						if(orderAddOn!=null){
						for( OrderAddOn oad : orderAddOn){
							JsonAddOn jsonAddOn = new JsonAddOn();
							jsonAddOn.setId(oad.getAddOnId());
							jsonAddOn.setDishId(item.getDishId());
							jsonAddOn.setName(oad.getName());
							jsonAddOn.setPrice(oad.getPrice());
							jsonAddOn.setQuantity(oad.getQuantity());
							jsonAdd.add(jsonAddOn);
							jsonDish.setAddOns(jsonAdd);
						}
					}
						itemsMap.put(item.getName(), jsonDish);
					}
				}
				map.put("itemsMap", itemsMap);
			}

        if(isChecksumValid || statuscode.equals("0")){
		        if(statuscode.equals("0")){
					Double amountReceived = Double.parseDouble(amount);
					if(orderid.equals(check.getCheckId().toString()) && amountReceived.equals(roundOff) ){
						if (returnMap.get("flag").equals("true") || statuscode.equals("0")) {
							logger.info("TXN SUCCESS");
			 				logger.info("check Id :"+checkId+"/"+check.getInvoiceId()+ " email :"+customer.getEmail());
			 				 order.setPaymentStatus(PaymentMode.MOBIKWIK_WALLET.toString());
							 orderService.addOrder(order);
							 if(checkResponse!=null)
							 checkResponse.setPaymentMode(PaymentMode.MOBIKWIK_WALLET.toString());
							 try{
								 orderController.emailCheckFromServer(request,checkId,customer.getEmail(),"saladdaysemailbill",null,null,null,null,0);
							     logger.info("email sent for check Id "+checkId+"/"+check.getInvoiceId());
							 }
							 catch(Exception e){
								 logger.info("Email sent fail");
								 e.printStackTrace();
								 restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
								 logger.info("Exception mail sent");
							 }
                            return "custom/saladdaysbill";
                        }else {
							//order.setPaymentStatus(PaymentMode.WALLET_PENDING.toString());
							//orderService.addOrder(order);
							logger.info("CANCELED Status  ");
							return "responsePG";
						}
						}else {
							//order.setPaymentStatus(PaymentMode.WALLET_PENDING.toString());
							//orderService.addOrder(order);
							logger.info("CANCELED Status  ");
							return "responsePG";
						}
		        }else {
		        	order.setPaymentStatus(PaymentMode.WALLET_PENDING.toString());
					orderService.addOrder(order);
					logger.info("CANCELED Status  ");
					return "responsePG";
		        }
		        }else{
		        	order.setPaymentStatus(PaymentMode.WALLET_PENDING.toString());
					orderService.addOrder(order);
					logger.info("CANCELED Status  ");
					return "responsePG";
		        }

    }

    @RequestMapping(value="/responseByPayTm",method=RequestMethod.POST)
    @ApiIgnore
	public String getCheckResponseByPaytm(@ModelAttribute("SpringWeb")PaymentGatwayDetail pgDetails,  ModelMap model,Map<String, Object> map,HttpServletRequest request) throws IOException, MessagingException {

        String Website_name=null;
		String MID=null;
		String Merchant_Key=null;
		String Industry_type_ID=null;
		String Channel_ID=null;
		String orderId = null;
		int checkId = -1;
		
		logger.info("Paytm POST response recieved:  orderId/checkId: "+request.getParameter("ORDERID") +": Status : "+request.getParameter("STATUS"));
		logger.info("Paytm JSON response recieved:  orderId/checKId: "+pgDetails.getORDERID() +": Status : "+pgDetails.getSTATUS());
		if(pgDetails.getORDERID()!=null){
			checkId = Integer.parseInt(pgDetails.getORDERID());
		}else {
			
			try{
				checkId = 	Integer.parseInt(request.getParameter("ORDERID"));
			}catch(Exception e){
				logger.info(checkId + "-Paytm-"+e.getMessage());
				//restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
				Gson json = new Gson();
				String jsonVal = json.toJson(pgDetails);
				logger.info("Recieved null payload:" +jsonVal);
				model.put("RESPMSG","Your payment is in Pending state");
				model.addAttribute("STATUS","PENDING");
				logger.info("PENDING state ");
				return "responsePayTm";
			}
		}
		Check check= checkService.getCheck(checkId);
		Restaurant rest = restaurantService.getRestaurant(check.getRestaurantId());
		model.addAttribute("CONTACT",rest.getBussinessPhoneNo());
		
        if(pgDetails.getSTATUS().equals("TXN_SUCCESS")){
        	logger.info("Transaction Success for checkId: "+checkId+"/"+check.getInvoiceId());
        	logger.info("Order Id from Check table: "+check.getOrders().get(0).getOrderId());
		if(check.getOrders().get(0)!=null){
			check.getOrders().get(0).setPaymentStatus(PaymentMode.PAYTM.toString());
		}
			model.addAttribute("paymentStatus","paid");
			check.setStatus(Status.Paid);
		}
		else if(pgDetails.getSTATUS().equals("TXN_FAILURE")){
            logger.info("Transaction Failed :  Status = "+pgDetails.getSTATUS() +" check Id "+checkId+"/"+check.getInvoiceId());
            logger.info("Order Id from Check table: "+check.getOrders().get(0).getOrderId()+": Response message: "+pgDetails.getRESPMSG());
            if(check.getOrders().get(0)!=null){
    			check.getOrders().get(0).setPaymentStatus(PaymentMode.PAYTM_PENDING.toString());
    			check.getOrders().get(0).setStatus(com.cookedspecially.enums.order.Status.CANCELLED);
    		}
		}
		else{
			logger.info("Unresolved Status:  Status = "+pgDetails.getSTATUS() +" checkId : "+checkId+"/"+check.getInvoiceId());
			logger.info("Order Id from Check table: "+check.getOrders().get(0).getOrderId() +": Response message: "+pgDetails.getRESPMSG());
			if(check.getOrders().get(0)!=null){
				check.getOrders().get(0).setPaymentStatus(PaymentMode.PAYTM_PENDING.toString());
			}
		}

        Customer customer = new Customer();
        TreeMap<String,String> parameters = new TreeMap<String,String>();
        Properties prop = new Properties();
		String propFileName = "paytmDetails.properties";

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

        if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}
		 Website_name = prop.getProperty("Website_name");
		 MID = prop.getProperty("MID");
		 Merchant_Key = prop.getProperty("Merchant_Key");
		 Industry_type_ID = prop.getProperty("Industry_type_ID");
		 Channel_ID = prop.getProperty("Channel_ID");

		check.setTransactionId(pgDetails.getTXNID());
		check.setTransactionStatus(pgDetails.getSTATUS());
        check.setResponseCode(pgDetails.getRESPCODE());
        checkService.addCheck(check);

		//String merchantKey = "CR6j5q@0u5PJ1fEu"; //Key provided by Paytm
		parameters.put("MID", MID); // Merchant ID (MID) provided by Paytm
		parameters.put("TXNID",pgDetails.getTXNID());
		parameters.put("ORDERID",pgDetails.getORDERID()); // Merchant’s order id
		parameters.put("BANKTXNID",pgDetails.getBANKTXNID());
		parameters.put("TXNAMOUNT",pgDetails.getTXNAMOUNT());
		parameters.put("CURRENCY",pgDetails.getCURRENCY());
		parameters.put("STATUS",pgDetails.getSTATUS());
		parameters.put("RESPCODE",pgDetails.getRESPCODE());
		parameters.put("RESPMSG",pgDetails.getRESPMSG());
		parameters.put("TXNDATE",pgDetails.getTXNDATE());
		parameters.put("GATEWAYNAME",pgDetails.getGATEWAYNAME());
		parameters.put("BANKNAME",pgDetails.getBANKNAME());
		parameters.put("PAYMENTMODE",pgDetails.getPAYMENTMODE());
		
        //Response Code , Response message, Transaction date, Payment MODE (credit,debit,netbanking) , Transaction Id,

		/*try {
			isValidChecksum = checkSumServiceHelper.verifycheckSum (Merchant_Key, parameters, paytmChecksum);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
			model.addAttribute("TXNID",pgDetails.getTXNID());
			model.addAttribute("ORDERID",pgDetails.getORDERID()); // Merchant’s order id
			model.addAttribute("BANKTXNID",pgDetails.getBANKTXNID());
			model.addAttribute("TXNAMOUNT",pgDetails.getTXNAMOUNT());
			model.addAttribute("CURRENCY",pgDetails.getCURRENCY());
			model.addAttribute("STATUS",pgDetails.getSTATUS());
			model.addAttribute("RESPCODE",pgDetails.getRESPCODE());
			model.addAttribute("RESPMSG",pgDetails.getRESPMSG());
			model.addAttribute("TXNDATE",pgDetails.getTXNDATE());
			model.addAttribute("GATEWAYNAME",pgDetails.getGATEWAYNAME());
			model.addAttribute("BANKNAME",pgDetails.getBANKNAME());
			model.addAttribute("PAYMENTMODE",pgDetails.getPAYMENTMODE());

        if (check != null) {
				model.addAttribute("CONTACT",rest.getBussinessPhoneNo());
				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
				cal.setTime(check.getOpenTime());

            DateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				formatter1.setTimeZone(cal.getTimeZone());
				map.put("checkDate", formatter1.format(cal.getTime()));

            CheckResponse checkResponse = new CheckResponse(check,taxTypeService,null,rest);
			    logger.info("setting json tax object: "+checkResponse.getRoundedOffTotal());
				logger.info("setting Invoice rounndOffTotal."+checkResponse.getRoundedOffTotal());
				if(rest!=null){
					if(rest.isRoundOffAmount()){
						check.setRoundOffTotal(Math.round(checkResponse.getRoundedOffTotal()));
						checkResponse.setRoundedOffTotal(Math.round(checkResponse.getRoundedOffTotal()));
					}else{
				    	check.setRoundOffTotal(checkResponse.getRoundedOffTotal());
					}
				}
				map.put("checkRespone", checkResponse);
				if (check.getCustomerId() > 0) {
					customer = customerService.getCustomer(check.getCustomerId());
					map.put("customer", customer);
				} else if (check.getTableId() > 0) {
					map.put("tableId", check.getTableId());
				}
				Map<String, JsonDish> itemsMap = new TreeMap<String, JsonDish>();
				List<CheckDishResponse> items = checkResponse.getItems();
				List<JsonAddOn> jsonAdd =  new ArrayList<JsonAddOn>();
				for (CheckDishResponse item : items) {
					if (itemsMap.containsKey(item.getName())) {
						JsonDish jsonDish = itemsMap.get(item.getName());
						jsonDish.setPrice(jsonDish.getPrice() + item.getPrice());
						jsonDish.setQuantity(jsonDish.getQuantity() + 1);
					}
					else {
						JsonDish jsonDish = new JsonDish();
						jsonDish.setQuantity(1);
						jsonDish.setName(item.getName());
						jsonDish.setId(item.getDishId());
						jsonDish.setPrice(item.getPrice());
						List<OrderAddOn> orderAddOn = item.getAddOnresponse();
						if(orderAddOn!=null){
						for( OrderAddOn oad : orderAddOn){
							JsonAddOn jsonAddOn = new JsonAddOn();
							jsonAddOn.setId(oad.getAddOnId());
							jsonAddOn.setDishId(item.getDishId());
							jsonAddOn.setName(oad.getName());
							jsonAddOn.setQuantity(oad.getQuantity());
							jsonAddOn.setPrice(oad.getPrice());
							jsonAdd.add(jsonAddOn);
							jsonDish.setAddOns(jsonAdd);
						}
					}
						itemsMap.put(item.getName(), jsonDish);
					}
				}
				map.put("itemsMap", itemsMap);
			}
			if(pgDetails.getSTATUS().equals("TXN_SUCCESS")){
				logger.info("email"+customer.getEmail());
				 try{
					 orderController.emailCheckFromServer(request,checkId,customer.getEmail(),"saladdaysemailbill",null,null,null,null,0);
				logger.info("email sent for check Id"+checkId+"/"+check.getInvoiceId());
				 }
				 catch(Exception e){
					 logger.info("Email sent fail");
					 e.printStackTrace();
					 restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
					 logger.info("Exception mail sent");
				 }
                return "custom/saladdaysbill";
            }
			else if(pgDetails.getSTATUS().equals("TXN_FAILURE")){

				try {
					orderController.restoreStock(check,0);
				} catch (ParseException e) {
					e.printStackTrace();
					restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
					logger.info("Exception mail sent");
				}
				check.setBill(0);
				check.setRoundOffTotal(0);
				CreditTransactions ct = customerCreditService.getLastPendingTransaction(check.getCustomerId());
				if(ct!=null){
					if(ct.getStatus()==CreditTransactionStatus.PENDING){
					try {
						customerCreditService.updateBillRecoveryTransaction("FAILED",check.getCustomerId(), check.getCreditBalance(),"CREDIT", "Setting status failed of existing pending transaction");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
						logger.info("Exception mail sent");
					}
				}
				}
				check.setCreditBalance(0);
				check.setOutCircleDeliveryCharges(0);
				check.setStatus(Status.Cancel);
				checkService.addCheck(check);
				logger.info("Check cancelled id :"+check.getCheckId()+"/"+check.getInvoiceId());
				model.addAttribute("STATUS","CANCELED");
                return "responsePG";
            }
			else{
					try{
						orderController.emailCheckFromServer(request, checkId,customer.getEmail(),"saladdaysemailbill",null,null,null,null,0);
						   logger.info("email sent for check Id"+checkId+"/"+check.getInvoiceId());
							}
							catch(Exception e){
								logger.info("Email sent fail");
								e.printStackTrace();
								restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
								logger.info("Exception mail sent");
							}
					model.put("RESPMSG","Your payment is in Pending state");
					model.addAttribute("STATUS","PENDING");
					logger.info("PENDING state ");
					return "responsePayTm";
				}
		}
	
	@RequestMapping(value="/responseByCitrus",method=RequestMethod.POST)
	@ApiIgnore
	public String getCheckResponseByCitrus(@ModelAttribute("SpringWeb")PaymentGatwayDetail pgDetails,  ModelMap model,Map<String, Object> map,HttpServletRequest request) throws IOException, MessagingException {

		Properties prop = new Properties();
		String propFileName = "citrusDetails.properties";

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

        if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }
        String vanityUrl  = prop.getProperty("vanityUrl");
		String secret_key = prop.getProperty("secret_key");
        String data="";
        String orderId=request.getParameter("TxId");
        String txnStatus=request.getParameter("TxStatus");
        String TxMsg  =  request.getParameter("TxMsg");
        String amount = request.getParameter("amount");
        logger.info("Amount paid at Citrus: "+amount +" checkId : "+orderId);
        String pgTxnId=request.getParameter("pgTxnNo");
        String issuerRefNo = request.getParameter("issuerRefNo");
        String authIdCode=request.getParameter("authIdCode");
        String pgRespCode=request.getParameter("pgRespCode");
        String zipCode=request.getParameter("addressZip");
        String resSignature=request.getParameter("signature");
        String transactionID = request.getParameter("transactionID");
        String paymentMode = request.getParameter("paymentMode");
		String TxGateway = request.getParameter("TxGateway");
		String couponCode  =  request.getParameter("couponCode");
		String txnDateTime = request.getParameter("txnDateTime");
        //Binding all required parameters in one string (i.e. data)
        if (orderId != null) {
            data += orderId;
        }
        if (txnStatus != null) {
            data += txnStatus;
        }
        if (amount != null) {
            data += amount;
        }
        if (pgTxnId != null) {
            data += pgTxnId;
        }
        if (issuerRefNo != null) {
            data += issuerRefNo;
        }
        if (authIdCode != null) {
            data += authIdCode;
        }
        if (pgRespCode != null) {
            data += pgRespCode;
        }
        if (zipCode != null) {
            data += zipCode;
        }

        javax.crypto.Mac mac = null;
		try {
			mac = javax.crypto.Mac.getInstance("HmacSHA1");
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			restaurantService.emailException(ExceptionUtils.getStackTrace(e1),request);
			logger.info("Exception mail sent");
		}
        try {
			mac.init(new javax.crypto.spec.SecretKeySpec(secret_key.getBytes(), "HmacSHA1"));
		} catch (InvalidKeyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			restaurantService.emailException(ExceptionUtils.getStackTrace(e1),request);
			logger.info("Exception mail sent");
		}
        byte[] hexBytes = new org.apache.commons.codec.binary.Hex().encode(mac.doFinal(data.getBytes()));
        String signature = new String(hexBytes, "UTF-8");

        boolean flag = true;
        if (resSignature != null && !resSignature.equalsIgnoreCase("")
            && !signature.equalsIgnoreCase(resSignature)) {
            flag = false;
        }
        if (flag) {
        	logger.info("Citrus Response Signature match for checkId :"+orderId);
        } else {
          logger.info("Citrus Response Signature and Our (Merchant) Signature Mis-Match");
     }
        String citrusStatus="";
        int checkId = Integer.parseInt(orderId);
		Check check= checkService.getCheck(checkId);
		String status =restaurantService.resolveCitrusResponseCode(pgRespCode);
		logger.info("PG transaction Id :" +pgTxnId);
		check.setTransactionId(pgTxnId);
		check.setTransactionStatus(txnStatus);
		check.setResponseCode(pgRespCode);
		checkService.addCheck(check);
		 logger.info(txnStatus+"  :status:  "+status +" :InvoiceId: "+check.getInvoiceId());
		 if("0".equalsIgnoreCase(pgRespCode) || "14".equalsIgnoreCase(pgRespCode)){
			    logger.info("Success :"+check.getCheckId());
			    if(check !=null){
			    	citrusStatus = "SUCCESS";
			    	check.setStatus(Status.Paid);
					check.getOrders().get(0).setPaymentStatus(PaymentMode.PG.toString());
			    	checkService.addCheck(check);
			    	logger.info("Check Success id :"+check.getCheckId());
			 }
		 }else if("3".equalsIgnoreCase(pgRespCode) || 
				  "2".equalsIgnoreCase(pgRespCode) ||
				  "5".equalsIgnoreCase(pgRespCode) || 
				  "7".equalsIgnoreCase(pgRespCode) ||
				  "4".equalsIgnoreCase(pgRespCode) ||
				  "1".equalsIgnoreCase(pgRespCode)){
			 if(check !=null){
				 citrusStatus = "CANCELED";
			     check.getOrders().get(0).setPaymentStatus(PaymentMode.PG_PENDING.toString());
			     check.setStatus(Status.Unpaid);
			     check = restaurantService.cancelInvoice(check);
			     checkService.addCheck(check);
			     logger.info("Check cancelled id :"+check.getCheckId());
			 }
		}else{
			 if(check !=null){
				 citrusStatus = "PG_PENDING";
			     check.setStatus(Status.Unpaid);
			     checkService.addCheck(check);
			     check.getOrders().get(0).setPaymentStatus(PaymentMode.PG_PENDING.toString());
			     checkService.addCheck(check);
			     logger.info("Check Pending :"+check.getCheckId());
			 }
		}
		 Customer customer = new Customer();
		logger.info("response recieved from Citrus check Id :"+checkId+"/"+check.getInvoiceId());
		
			model.addAttribute("TXNID",pgTxnId);
			model.addAttribute("ORDERID",orderId); // Merchantâ€™s order id
			model.addAttribute("BANKTXNID",authIdCode);
			model.addAttribute("TXNAMOUNT",amount);
			model.addAttribute("STATUS",txnStatus);
			model.addAttribute("RESPCODE",pgRespCode);
			model.addAttribute("RESPMSG",TxMsg);
			model.addAttribute("TXNDATE",txnDateTime);
			model.addAttribute("GATEWAYNAME",TxGateway);
			model.addAttribute("PAYMENTMODE",paymentMode);

        if (check != null) {
				Restaurant restaurant= restaurantService.getRestaurant(check.getRestaurantId());
                model.addAttribute("CONTACT",restaurant.getBussinessPhoneNo());
				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(restaurant.getTimeZone()));
				cal.setTime(check.getOpenTime());
				DateFormat formatter1;
				formatter1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				formatter1.setTimeZone(cal.getTimeZone());
				map.put("checkDate", formatter1.format(cal.getTime()));
				CheckResponse checkResponse = new CheckResponse(check,taxTypeService,null,restaurant);
				    logger.info("setting json tax object"+checkResponse.getRoundedOffTotal());
					logger.info("setting Invoice rounndOffTotal."+checkResponse.getRoundedOffTotal());
					if(restaurant!=null){
					if(restaurant.isRoundOffAmount()){
						check.setRoundOffTotal(Math.round(checkResponse.getRoundedOffTotal()));
						checkResponse.setRoundedOffTotal(Math.round(checkResponse.getRoundedOffTotal()));
					}else{
				    	check.setRoundOffTotal(checkResponse.getRoundedOffTotal());
					}
					}
					map.put("checkRespone", checkResponse);
					map.put("restaurant", restaurant);
					if (check.getCustomerId() > 0) {
						customer = customerService.getCustomer(check.getCustomerId());
						map.put("customer", customer);
					} else if (check.getTableId() > 0) {
						map.put("tableId", check.getTableId());
				}
				Map<String, JsonDish> itemsMap = new TreeMap<String, JsonDish>();
				List<CheckDishResponse> items = checkResponse.getItems();
				List<JsonAddOn> jsonAdd =  new ArrayList<JsonAddOn>();
				for (CheckDishResponse item : items) {
					if (itemsMap.containsKey(item.getName())) {
						JsonDish jsonDish = itemsMap.get(item.getName());
						jsonDish.setPrice(jsonDish.getPrice() + item.getPrice());
						jsonDish.setQuantity(jsonDish.getQuantity() + 1);
					}
					else {
						JsonDish jsonDish = new JsonDish();
						jsonDish.setQuantity(1);
						jsonDish.setName(item.getName());
						jsonDish.setId(item.getDishId());
						jsonDish.setPrice(item.getPrice());
						List<OrderAddOn> orderAddOn = item.getAddOnresponse();
						if(orderAddOn!=null){
						for( OrderAddOn oad : orderAddOn){
							JsonAddOn jsonAddOn = new JsonAddOn();
							jsonAddOn.setId(oad.getAddOnId());
							jsonAddOn.setDishId(item.getDishId());
							jsonAddOn.setName(oad.getName());
							jsonAddOn.setPrice(oad.getPrice());
							jsonAddOn.setQuantity(oad.getQuantity());
							jsonAdd.add(jsonAddOn);
							jsonDish.setAddOns(jsonAdd);
						}
					}
						itemsMap.put(item.getName(), jsonDish);
					}
				}
				map.put("itemsMap", itemsMap);
			}
			if(citrusStatus.equals("SUCCESS")){
				logger.info("TXN SUCCESS");
 				logger.info("check Id :"+checkId+ " email :"+customer.getEmail());
				 try{
					 orderController.emailCheckFromServer(request,checkId,customer.getEmail(),"saladdaysemailbill",null,null,null,null,0);
				logger.info("email sent for check Id "+checkId+"/"+check.getInvoiceId());
				 }
				 catch(Exception e){
					 logger.info("Email sent fail");
					 e.printStackTrace();
					 restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
					 logger.info("Exception mail sent");
				 }

                return "custom/saladdaysbill";
            }else if(citrusStatus.equals("CANCELED")){
					logger.info("CANCELED Status  ");
					return "responsePG";
				}
			else {
				try{
					orderController.emailCheckFromServer(request, checkId,customer.getEmail(),"saladdaysemailbill",null,null,null,null,0);
						}
						catch(Exception e){
							logger.info("Email sent fail");
							restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
							logger.info("Exception mail sent");
							e.printStackTrace();
						}
				logger.info("PENDING state ");
				return "responsePG";
			}
			}
	
	@RequestMapping(value="/citrusNotify",method=RequestMethod.POST)
	@ApiIgnore
	public void citrusNotify(HttpServletRequest request) throws IOException{
		Properties prop = new Properties();
		String propFileName = "citrusDetails.properties";
		logger.info("Inside Notify Api : server to server response");
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

        if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }
        String secret_key = prop.getProperty("secret_key");
        String data="";
        String orderId=request.getParameter("TxId");
        String txnStatus=request.getParameter("TxStatus");
        String amount = request.getParameter("amount");
        String pgTxnId=request.getParameter("pgTxnNo");
        String issuerRefNo = request.getParameter("issuerRefNo");
        String authIdCode=request.getParameter("authIdCode");
        String pgRespCode=request.getParameter("pgRespCode");
        String zipCode=request.getParameter("addressZip");
        String resSignature=request.getParameter("signature");

        //Binding all required parameters in one string (i.e. data)
        if (orderId != null) {
            data += orderId;
        }
        if (txnStatus != null) {
            data += txnStatus;
        }
        if (amount != null) {
            data += amount;
        }
        if (pgTxnId != null) {
            data += pgTxnId;
        }
        if (issuerRefNo != null) {
            data += issuerRefNo;
        }
        if (authIdCode != null) {
            data += authIdCode;
        }
        if (pgRespCode != null) {
            data += pgRespCode;
        }
        if (zipCode != null) {
            data += zipCode;
        }

        javax.crypto.Mac mac = null;
		try {
			mac = javax.crypto.Mac.getInstance("HmacSHA1");
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			try {
				restaurantService.emailException(ExceptionUtils.getStackTrace(e1),request);
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.info("Exception mail sent");
		}
        try {
			mac.init(new javax.crypto.spec.SecretKeySpec(secret_key.getBytes(), "HmacSHA1"));
		} catch (InvalidKeyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			try {
				restaurantService.emailException(ExceptionUtils.getStackTrace(e1),request);
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.info("Exception mail sent");
		}
        byte[] hexBytes = new org.apache.commons.codec.binary.Hex().encode(mac.doFinal(data.getBytes()));
        String signature = new String(hexBytes, "UTF-8");

        boolean flag = true;
        if (resSignature != null && !resSignature.equalsIgnoreCase("")
            && !signature.equalsIgnoreCase(resSignature)) {
            flag = false;
        }
        if (flag) {
        	logger.info("Citrus Response Signature match for checkId :"+orderId);
        } else {
          logger.info("Citrus Response Signature and Our (Merchant) Signature Mis-Match");
     }

        int checkId = Integer.parseInt(orderId);
		Check check= checkService.getCheck(checkId);
		String status =restaurantService.resolveCitrusResponseCode(pgRespCode);
		check.setTransactionId(pgTxnId);
		 logger.info("Citrus Notification API :" +status +" respCode: "+pgRespCode);
		 logger.info("Notification API Citrus: Order payment stauts before making any change:"+check.getOrders().get(0).getPaymentStatus());
		if(check.getOrders().get(0).getPaymentStatus().equalsIgnoreCase(PaymentMode.PG_PENDING.toString())){
		 if("0".equalsIgnoreCase(pgRespCode) || "14".equalsIgnoreCase(pgRespCode)){
			 if(orderId!=null){
			    logger.info("Success :"+check.getCheckId());
			    if(check !=null && !("SUCCESS".equalsIgnoreCase(check.getTransactionStatus()))){
			    	check.setTransactionStatus("SUCCESS");
			    	check.setResponseCode(pgRespCode);
			    	check.setStatus(Status.Paid);
					check.getOrders().get(0).setPaymentStatus(PaymentMode.PG.toString());
			    	checkService.addCheck(check);
			    }
			 }
		 }else if("3".equalsIgnoreCase(pgRespCode) || 
				  "2".equalsIgnoreCase(pgRespCode) ||
				  "5".equalsIgnoreCase(pgRespCode) || 
				  "7".equalsIgnoreCase(pgRespCode) ||
				  "4".equalsIgnoreCase(pgRespCode) ||
				  "1".equalsIgnoreCase(pgRespCode)){
			 if(check !=null){
				 check.setTransactionStatus(status);
			     check.setResponseCode(pgRespCode);
			     check.getOrders().get(0).setPaymentStatus(PaymentMode.PG_PENDING.toString());
			     check.setStatus(Status.Unpaid);
			     check = restaurantService.cancelInvoice(check);
			     checkService.addCheck(check);
			     logger.info("Check cancelled id :"+check.getCheckId());
			 }
		}else{
			 if(check !=null){
				 check.setTransactionStatus(txnStatus);
			     check.setResponseCode(pgRespCode);
			     check.setStatus(Status.Unpaid);
			     checkService.addCheck(check);
			     check.getOrders().get(0).setPaymentStatus(PaymentMode.PG_PENDING.toString());
			     checkService.addCheck(check);
			 }
		}
	}
		logger.info("PG transaction Id :" +pgTxnId);
    }

	@ApiOperation(value="To get current date and time using time zone ", notes="Pass time zone as paramater like IST, PST etc ")
    @RequestMapping(value = "/time.json", method = RequestMethod.GET)
	public @ResponseBody Map<String,Object> getTime(HttpServletRequest request, HttpServletResponse response, @RequestParam String tz) throws ParseException, JSONException {
		
		String zone =tz;
	    TimeZone  tZ =  TimeZone.getTimeZone(zone);
	    	Calendar cal = Calendar.getInstance(tZ,Locale.ENGLISH );
	        Date date = cal.getTime();
	        SimpleDateFormat sdfa = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
	        sdfa.setTimeZone(TimeZone.getTimeZone(zone));
	        String format = "dd-MM-yyyy hh:mm a";
	        SimpleDateFormat sdf = new SimpleDateFormat(format);
	        String formatdate = sdfa.format(date);
	        Date currentAdded = sdf.parse(formatdate);
	        String weekDay = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH);
	        long dif = currentAdded.getTime();
	        Date da = new Date(dif);
	        String dates=(da.getMonth()+1)+"-"+da.getDate()+"-"+cal.get(Calendar.YEAR);
	        Map<String,Object> json = new HashMap<String,Object>();
	        json.put("time",da.getHours()+":"+da.getMinutes());
	        json.put("date",dates);
	        json.put("day",weekDay);
	    	//String output = json.toString();
		return json;

    }
	
	@RequestMapping("/deliveryAreas")
	 @ApiIgnore
	public String listDeliveryAreas(Map<String, Object> map, HttpServletRequest request) {
		int count = 0;
		Map<Integer,Integer> savedCount =  new HashMap<Integer, Integer>();
		map.put("deliveryAreas", new DeliveryArea());
		map.put("deliveryAreaList", deliveryAreaService.listDeliveryAreasByResaurant((Integer) request.getSession().getAttribute("restaurantId")));
		map.put("kitchenScreenList", fulfillmentCenterService.getKitchenScreens((Integer) request.getSession().getAttribute("restaurantId")));
		List<Customer> cs  =  customerService.getCustomerByParams(0,null,null,(Integer) request.getSession().getAttribute("parentRestaurantId"));
		List<DeliveryArea> da = deliveryAreaService.listDeliveryAreasByResaurant((Integer) request.getSession().getAttribute("restaurantId"));
		for(DeliveryArea ad: da){
			for(Customer sc : cs){
			if(ad.getName().equalsIgnoreCase(sc.getDeliveryArea())){
				count ++;
			}
			}
			savedCount.put(ad.getId(),count);
			count=0;
		}
		map.put("minDeliveryTime",CSConstants.minDeliveryTime);
		map.put("deliveryTimeInterval", CSConstants.deliveryTimeInterval);
		map.put("userCount",savedCount);
		return "deliveryArea";
	}
	
	@RequestMapping("/edit/{restaurantId}")
	@ApiIgnore
	public String editRestaurant(Map<String, Object> map, HttpServletRequest request, @PathVariable("restaurantId") Integer restaurantId) {
		map.put("restaurant", restaurantService.getRestaurant(restaurantId));
		map.put("restaurantList", restaurantService.listRestaurantById((Integer) request.getSession().getAttribute("restaurantId")));
		map.put("statusTypes", com.cookedspecially.enums.Status.values());
		return "restaurant";
	}
	
//	@RequestMapping(value = "/add", method = RequestMethod.POST)
//	public String addRestaurant(@ModelAttribute("restaurant") Restaurant restaurant, BindingResult result) {
//		
//		restaurantService.addRestaurant(restaurant);
//		return "redirect:/restaurant/";
//	}
	@RequestMapping(value = "/addRestaurant", method = RequestMethod.POST)
	@ApiIgnore
	public String addRestaurant(HttpServletRequest request, @ModelAttribute("restaurant") Restaurant restaurant, BindingResult result) {
		
		Restaurant existingRest=restaurantService.getRestaurantByName(restaurant.getRestaurantName());
		if(existingRest!=null){
		   result.rejectValue("restaurantName", "restaurantName", "Restaurant Name All ready Exist! Try with diffrent Name.");
		   return "addRestaurant";
		}
		restaurantService.addRestaurant(restaurant);
		User user=userService.getUser((Integer) request.getSession().getAttribute("userId"));
		user.setOrgId(restaurant.getRestaurantId());
		userService.addUser(user);
		return "login";	
		
	}

	@RequestMapping("/delete/{restaurantId}")
	@ApiIgnore
	public String deleteRestaurant(@PathVariable("restaurantId") Integer restaurantId) {
		restaurantService.removeRestaurant(restaurantId);
		return "redirect:/restaurant/";
	}

	@RequestMapping(value = "/resources/APK", method = RequestMethod.GET)
	@ApiIgnore
	public void getFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String restaurantName = request.getParameter("restaurantName");
		if (StringUtility.isNullOrEmpty(restaurantName)) {
			Integer restId = (Integer) request.getSession().getAttribute("restaurantId");
			//User user = userService.getUser(userId);
			Restaurant rest=restaurantService.getRestaurant(restId);
			if (rest != null) {
				restaurantName = rest.getBussinessName();
			}
		}
		restaurantName = restaurantName.replaceAll("[^a-zA-Z0-9_]", ""); 
		response.setContentType("application/force-download");
		File f = new File("webapps" + File.separator + "static" + File.separator + "clients" + File.separator + "com"  + File.separator + "cookedspecially" + File.separator + restaurantName + File.separator + restaurantName + ".apk");
		response.setContentLength(new Long(f.length()).intValue());
        response.setHeader("Content-Disposition", "attachment; filename=" + restaurantName + ".apk");
        FileCopyUtils.copy(new FileInputStream(f), response.getOutputStream());
	}
	
	@ApiOperation(value="[*] Gets restaurant information by restaurant Id.And OrderSource and Payment Type list based on organization.")
	@RequestMapping(value="/getrestaurantinfo", method=RequestMethod.GET)
	public @ResponseBody RestaurantInfo getReataurantInfo(HttpServletRequest request, HttpServletResponse response, @RequestParam(required=false) String restaurantId) {
		String restaurantIdStr = restaurantId;
		Integer restId;
		if(restaurantIdStr != null)
			restId = Integer.parseInt(restaurantIdStr);
		else
			restId = (Integer)request.getSession().getAttribute("restaurantId");

		Restaurant  restaurant= restaurantService.getRestaurant(restId);
		List<TaxType> taxList = taxTypeService.listTaxTypesByRestaurantId(restId);
		List<OrderSource> orderSource =  restaurantService.listOrderSourcesByOrgId(restaurant.getParentRestaurantId());
		
		List<OrderSource> orderSourceList  = new ArrayList<OrderSource>();
		for(OrderSource os:orderSource){
			if(com.cookedspecially.enums.Status.INACTIVE.toString().equalsIgnoreCase(os.getStatus())){
				continue;
			}
			else {
				orderSourceList.add(os);
			}
		}
		
		List<PaymentType> filteredPaymentType =  new ArrayList<PaymentType>(); 
		List<PaymentType> paymentType = restaurantService.listPaymentTypeByOrgId(restaurant.getParentRestaurantId());
		for(PaymentType pt : paymentType){
			if("INACTIVE".equalsIgnoreCase(pt.getStatus())){
				continue;
			}
			filteredPaymentType.add(pt);
		}
		
		List<FulfillmentCenter> ffCenter  =  fulfillmentCenterService.getKitchenScreens(restaurant.getRestaurantId());
		//User user = null;
		Restaurant rest=restaurantService.getRestaurant(restId);
		rest.setTaxList(taxList);
		rest.setOrderSource(orderSourceList);
		rest.setPaymentType(filteredPaymentType );
		rest.setFfCenter(ffCenter);
		return new RestaurantInfo(rest);
	}
	
	@ApiOperation(value="[*] Get all delivery areas with time" , notes="Kindly display delivery time associated with delivery areas.")
	@RequestMapping(value="/getDeliveryAreas",method=RequestMethod.GET)
	public @ResponseBody ArrayList<DeliveryArea> getDeliveryAreas(HttpServletRequest request, HttpServletResponse response, @RequestParam(required=false) String restaurantId, @ApiParam(value="True or False", required=false)@RequestParam(required=false) String pos) throws ParseException, JSONException {
		//String restaurantId = request.getParameter("restaurantId");
		System.out.println(restaurantId);
		boolean posOnly = Boolean.parseBoolean(pos);
		Integer restId ; 
		if(restaurantId != null)
			restId = Integer.parseInt(restaurantId);
		else
			restId = (Integer)request.getSession().getAttribute("restaurantId");

		Restaurant rest=restaurantService.getRestaurant(restId);
		ArrayList<DeliveryArea> deliveryAreasList = new ArrayList<DeliveryArea>();
		
		if (rest != null) {
			if(posOnly){
			List<DeliveryArea> deliveryAreas = deliveryAreaService.listDeliveryAreasByResaurant(restId);
			for (DeliveryArea deliveryArea : deliveryAreas) {
				String todayTimeJson=null;
				String tomorrowTimeJson=null;
				if(deliveryArea.isTomorrowOnly()){
				 tomorrowTimeJson = getTime("Tomorrow",deliveryArea.getMinDeliveryTime(),deliveryArea.getDeliveryTimeInterval(),restId);	
				}
				else{
				todayTimeJson = getTime("Today",deliveryArea.getMinDeliveryTime(),deliveryArea.getDeliveryTimeInterval(),restId);
				tomorrowTimeJson = getTime("Tomorrow",deliveryArea.getMinDeliveryTime(),deliveryArea.getDeliveryTimeInterval(),restId);
				}

				deliveryArea.setTodayTimeJson(todayTimeJson );
				deliveryArea.setTomorrowTimeJson(tomorrowTimeJson);
				deliveryAreasList.add(deliveryArea);
			}
			}else{
				List<DeliveryArea> deliveryAreas = deliveryAreaService.listDeliveryAreasByResaurant(restId);
				for (DeliveryArea deliveryArea : deliveryAreas) {
					if(deliveryArea.isPosVisible()){
						continue;
					}
					String todayTimeJson=null;
					String tomorrowTimeJson=null;
					if(deliveryArea.isTomorrowOnly()){
					 tomorrowTimeJson = getTime("Tomorrow",deliveryArea.getMinDeliveryTime(),deliveryArea.getDeliveryTimeInterval(),restId);	
					}
					else{
					todayTimeJson = getTime("Today",deliveryArea.getMinDeliveryTime(),deliveryArea.getDeliveryTimeInterval(),restId);
					tomorrowTimeJson = getTime("Tomorrow",deliveryArea.getMinDeliveryTime(),deliveryArea.getDeliveryTimeInterval(),restId);
					}

					deliveryArea.setTodayTimeJson(todayTimeJson );
					deliveryArea.setTomorrowTimeJson(tomorrowTimeJson);
					deliveryAreasList.add(deliveryArea);
				}
			}
		}
		Collections.sort(deliveryAreasList, new Comparator<DeliveryArea>() {
		    public int compare(DeliveryArea v1,DeliveryArea  v2) {
		        return v1.getName().compareTo(v2.getName());
		    }
		});
		return deliveryAreasList;
	}
	
	private String getCalculatedDeliveryTime(String currentTime,int miDeliveryTime) throws ParseException{
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        Date d = df.parse(currentTime); 
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.MINUTE, miDeliveryTime);
        String newTime = df.format(cal.getTime());
		return twentyFourHrToTwelevHrConverter(newTime);
	}
	
	private String twentyFourHrToTwelevHrConverter(String time) throws ParseException{
		final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
	    final Date dateObj = sdf.parse(time);
	    String times = new SimpleDateFormat("K:mm a").format(dateObj);
	    return times;
	}
	
	public String getTime(String day,int miDeliveryTime ,int deliveryTimeInterval,int reaturantId) throws ParseException, JSONException {
	
		Restaurant restaurant = restaurantService.getRestaurant(reaturantId);
	    TimeZone  tz =  TimeZone.getTimeZone(restaurant.getTimeZone());
	    ArrayList<String> list = new ArrayList<String>();
	    if("Today".equalsIgnoreCase(day)){
	    	Calendar cal = Calendar.getInstance(tz,Locale.ENGLISH );
	    	Calendar calCompare = Calendar.getInstance(tz,Locale.ENGLISH );
	        int unroundedMinutes = cal.get(Calendar.MINUTE);
	        int mod = unroundedMinutes % deliveryTimeInterval;
	        cal.add(Calendar.MINUTE, miDeliveryTime);
	        //cal.add(Calendar.MINUTE,(15-mod));
	        cal.add(Calendar.MINUTE, mod < 8 ? -mod : (deliveryTimeInterval-mod)); 
	        Date date = cal.getTime();
	        Date compDate =calCompare.getTime();
	        SimpleDateFormat sdfa = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
	        sdfa.setTimeZone(TimeZone.getTimeZone(restaurant.getTimeZone()));
	        String formatdate = sdfa.format(date);
	        String weekDay = calCompare.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH);
	        String dayNightDiff="12:00 AM";
	        String compNightDate=sdfa.format(compDate);
	        String morning =null;
	        String nightM =null;
	        String evening = null;
	        String format = "dd-MM-yyyy hh:mm a";
	        
	        if(weekDay.equalsIgnoreCase("Sun")){
	         morning = getCalculatedDeliveryTime(restaurant.getSundayOpenTime(),miDeliveryTime-1);
	         nightM = getCalculatedDeliveryTime(restaurant.getSundayOpenTime(),miDeliveryTime);
	         evening = getCalculatedDeliveryTime(restaurant.getSundayCloseTime(),1);
	        }
	        else if(weekDay.equalsIgnoreCase("Mon")){
		         morning = getCalculatedDeliveryTime(restaurant.getMondayOpenTime(),miDeliveryTime-1);
		         nightM = getCalculatedDeliveryTime(restaurant.getMondayOpenTime(),miDeliveryTime);
		         evening = getCalculatedDeliveryTime(restaurant.getMondayCloseTime(),1);
		    }
	        else if(weekDay.equalsIgnoreCase("Tue")){
		         morning = getCalculatedDeliveryTime(restaurant.getTuesdayOpenTime(),miDeliveryTime-1);
		         nightM =getCalculatedDeliveryTime(restaurant.getTuesdayOpenTime(),miDeliveryTime);
		         evening =getCalculatedDeliveryTime(restaurant.getTuesdayCloseTime(),1);
		    }
	        else if(weekDay.equalsIgnoreCase("Wed")){
		         morning = getCalculatedDeliveryTime(restaurant.getWednesdayOpenTime(),miDeliveryTime-1);
		         nightM = getCalculatedDeliveryTime(restaurant.getWednesdayOpenTime(),miDeliveryTime);
		         evening = getCalculatedDeliveryTime(restaurant.getWednesdayCloseTime(),1);
		    }
	        else if(weekDay.equalsIgnoreCase("Thu")){
		         morning = getCalculatedDeliveryTime(restaurant.getThursdayOpenTime(),miDeliveryTime-1);
		         nightM = getCalculatedDeliveryTime(restaurant.getThursdayOpenTime(),miDeliveryTime);
		         evening = getCalculatedDeliveryTime(restaurant.getThursdayCloseTime(),1);
		    }
	        else if(weekDay.equalsIgnoreCase("Fri")){
		         morning = getCalculatedDeliveryTime(restaurant.getFridayOpenTime(),miDeliveryTime-1);
		         nightM = getCalculatedDeliveryTime(restaurant.getFridayOpenTime(),miDeliveryTime);
		         evening = getCalculatedDeliveryTime(restaurant.getFridayCloseTime(),1);
		    }
	        else if(weekDay.equalsIgnoreCase("Sat")){
		         morning = getCalculatedDeliveryTime(restaurant.getSaturdayOpenTime(),miDeliveryTime-1);
		         nightM = getCalculatedDeliveryTime(restaurant.getSaturdayOpenTime(),miDeliveryTime);
		         evening = getCalculatedDeliveryTime(restaurant.getSaturdayCloseTime(),1);
		    }
	        SimpleDateFormat sdf = new SimpleDateFormat(format);

	        Date currentAdded = sdf.parse(formatdate);
	        Date finalNightCompare =sdf.parse(compNightDate);
	        
	        String currentDate = currentAdded.getDate()+"-"+(currentAdded.getMonth()+1)+"-"+cal.get(Calendar.YEAR);
	        String serverDate =  (currentAdded.getMonth()+1)+"-"+currentAdded.getDate()+"-"+cal.get(Calendar.YEAR);
	        Date nightDT = sdf.parse(currentDate+" "+evening);
	        Date morningDT =sdf.parse(currentDate+" "+morning);
	        
	        Date nightMDT =sdf.parse(currentDate+" "+nightM);
	       
	        Date newDayObj=sdf.parse(currentDate+" "+dayNightDiff);
	        
	       long dif = currentAdded.getTime();
	       JSONObject json = new JSONObject();
	       if(currentAdded.after(morningDT)&&currentAdded.before(nightDT)){
	        while (dif < nightDT.getTime()) {
	        	Date slot = new Date(dif);
	            String time= ""+String.format("%02d",slot.getHours())+":"+String.format("%02d",slot.getMinutes());
	            list.add(time);
	            dif += deliveryTimeInterval*60000;
	        }
	        }
	       else if(currentAdded.before(nightMDT)&&finalNightCompare.after(newDayObj) ){
	    	   long diff = nightMDT.getTime();
		        while (diff < nightDT.getTime()) {
		            Date slot = new Date(diff);
		            String time= ""+String.format("%02d",slot.getHours())+":"+String.format("%02d",slot.getMinutes());
		            list.add(time);
		            diff += deliveryTimeInterval*60000;
		        }
	        }
	        Date da = new Date(dif);
	        String dates=(da.getMonth()+1)+"-"+da.getDate()+"-"+cal.get(Calendar.YEAR);
	        json.put("date",serverDate);
	        json.put("dateList",list);
	    	String output = json.toString();
		return output;
	    }
	   else if("Tomorrow".equalsIgnoreCase(day)){
		   Calendar startDate = Calendar.getInstance(tz,Locale.ENGLISH);
	    	startDate.setLenient(false);
	    	startDate.add(Calendar.DATE, 1);
	        Date date = startDate.getTime();
	        SimpleDateFormat sdfa = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
	        sdfa.setTimeZone(TimeZone.getTimeZone(restaurant.getTimeZone()));
	        String formatdate = sdfa.format(date);
	        String format = "dd-MM-yyyy hh:mm a";
	        String weekDay = startDate.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH);
	        SimpleDateFormat sdf = new SimpleDateFormat(format);
	        Date dateObj = sdf.parse(formatdate);
	        String date2 = dateObj.getDate()+"-"+(dateObj.getMonth()+1)+"-"+startDate.get(Calendar.YEAR);
	        String serverDate =  (dateObj.getMonth()+1)+"-"+dateObj.getDate()+"-"+startDate.get(Calendar.YEAR);

	        String morning=null;
	        String evening =null;
	        
	        if(weekDay.equalsIgnoreCase("Sun")){
		         morning = getCalculatedDeliveryTime(restaurant.getSundayOpenTime(),miDeliveryTime);
		         evening = getCalculatedDeliveryTime(restaurant.getSundayCloseTime(),1);
		        }
		        else if(weekDay.equalsIgnoreCase("Mon")){
			         morning = getCalculatedDeliveryTime(restaurant.getMondayOpenTime(),miDeliveryTime);
			         evening = getCalculatedDeliveryTime(restaurant.getMondayCloseTime(),1);
			    }
		        else if(weekDay.equalsIgnoreCase("Tue")){
			         morning = getCalculatedDeliveryTime(restaurant.getTuesdayOpenTime(),miDeliveryTime);
			         evening = getCalculatedDeliveryTime(restaurant.getTuesdayCloseTime(),1);
			    }
		        else if(weekDay.equalsIgnoreCase("Wed")){
			         morning = getCalculatedDeliveryTime(restaurant.getWednesdayOpenTime(),miDeliveryTime);
			         evening = getCalculatedDeliveryTime(restaurant.getWednesdayCloseTime(),1);
			    }
		        else if(weekDay.equalsIgnoreCase("Thu")){
			         morning = getCalculatedDeliveryTime(restaurant.getThursdayOpenTime(),miDeliveryTime);
			         evening = getCalculatedDeliveryTime(restaurant.getThursdayCloseTime(),1);
			    }
		        else if(weekDay.equalsIgnoreCase("Fri")){
			         morning = getCalculatedDeliveryTime(restaurant.getFridayOpenTime(),miDeliveryTime);
			         evening = getCalculatedDeliveryTime(restaurant.getFridayCloseTime(),1);
			    }
		        else if(weekDay.equalsIgnoreCase("Sat")){
			         morning = getCalculatedDeliveryTime(restaurant.getSaturdayOpenTime(),miDeliveryTime);
			         evening = getCalculatedDeliveryTime(restaurant.getSaturdayCloseTime(),1);
			    }

	        Date dateObj1 = sdf.parse(date2+" "+morning);
	        Date dateObj2 = sdf.parse(date2+" "+evening);
	        
	        long dif = dateObj1.getTime();
	        JSONObject json = new JSONObject();
	        while (dif < dateObj2.getTime()) {
	            Date slot = new Date(dif);
	            String time= ""+String.format("%02d",slot.getHours())+":"+String.format("%02d",slot.getMinutes());
	            list.add(time);
	            dif += deliveryTimeInterval*60000;
	        }
	        Date da = new Date(dif);
	        String dates=(da.getMonth()+1)+"-"+da.getDate()+"-"+startDate.get(Calendar.YEAR);
	        json.put("date",serverDate);
	        json.put("dateList",list);
	    	String output = json.toString();
		return output;
	    }
		return tz.toString();
	}
	
	@RequestMapping("/editDeliveryArea/{deliveryAreaId}")
	@ApiIgnore
	public String editDeliveryArea(Map<String, Object> map, HttpServletRequest request, @PathVariable("deliveryAreaId") Integer deliveryAreaId) {
		int count = 0;
		Map<Integer,Integer> savedCount =  new HashMap<Integer, Integer>();
		map.put("deliveryAreas", deliveryAreaService.getDeliveryArea(deliveryAreaId));
		map.put("deliveryAreaList", deliveryAreaService.listDeliveryAreasByResaurant((Integer) request.getSession().getAttribute("restaurantId")));
		map.put("kitchenScreenList", fulfillmentCenterService.getKitchenScreens((Integer) request.getSession().getAttribute("restaurantId")));
		List<Customer> cs  =  customerService.getCustomerByParams(0,null,null,(Integer) request.getSession().getAttribute("parentRestaurantId"));
		List<DeliveryArea> da = deliveryAreaService.listDeliveryAreasByResaurant((Integer) request.getSession().getAttribute("restaurantId"));
		for(DeliveryArea ad: da){
			for(Customer sc : cs){
			if(ad.getName().equalsIgnoreCase(sc.getDeliveryArea())){
				count ++;
			}
			}
			savedCount.put(ad.getId(),count);
			count=0;
		}
		map.put("userCount",savedCount);
		map.put("minDeliveryTime",CSConstants.minDeliveryTime);
		map.put("deliveryTimeInterval", CSConstants.deliveryTimeInterval);
		
		return "deliveryArea";
	}
	@RequestMapping(value = "/addDeliveryArea", method = RequestMethod.POST)
	@ApiIgnore
	public String addDeliveryArea(@ModelAttribute("deliveryArea")
	   DeliveryArea deliveryArea, BindingResult result, HttpServletRequest request) {
		deliveryAreaService.addDeliveryArea(deliveryArea);
		if("Zomato".equalsIgnoreCase(deliveryArea.getName())){
			updateIfExistInZomatoMenu(deliveryArea.getRestaurantId(),request);
		}
		return "redirect:/restaurant/deliveryAreas";
	}
	@RequestMapping("/deleteDeliveryArea/{deliveryAreaId}")
	@ApiIgnore
	public String deleteDeliveryArea(@PathVariable("deliveryAreaId") Integer deliveryAreaId) {
		deliveryAreaService.removeDeliveryArea(deliveryAreaId);
		return "redirect:/restaurant/deliveryAreas/";
	}
	

	@RequestMapping("/microKitchenScreens")
	@ApiIgnore
	public String listMicroKitchenScreen(Map<String, Object> map, HttpServletRequest request) {
		
		map.put("microKitchenScreen", new MicroKitchenScreen());
		ArrayList<FulfillmentCenter> kitchenScreenList  =  new ArrayList<FulfillmentCenter>();
		ArrayList<MicroKitchenScreen> microKitchenScreen = new ArrayList<MicroKitchenScreen>();
		kitchenScreenList  = (ArrayList<FulfillmentCenter>) fulfillmentCenterService.getKitchenScreens((Integer) request.getSession().getAttribute("restaurantId"));
		map.put("kitchenScreenList",kitchenScreenList);
		if(kitchenScreenList.size()>0){
			for(FulfillmentCenter  ks: kitchenScreenList){
				ArrayList<MicroKitchenScreen> mk = (ArrayList<MicroKitchenScreen>) microKitchenScreenService.getMicroKitchenScreensByKitchen(ks.getId());
			    microKitchenScreen.addAll(mk);
		}
		}
		map.put("microKitchenScreenList",microKitchenScreen);
	    //map.put("microKitchenScreenList", microKitchenScreenService.getMicroKitchenScreensByUser((Integer) request.getSession().getAttribute("userId")));
		//map.put("kitchenScreenList", kitchenScreenService.getKitchenScreens((Integer) request.getSession().getAttribute("userId")));
		return "microKitchenScreen";
	}
	@RequestMapping("/editMicroKitchenScreen/{microKitchenScreenId}")
	@ApiIgnore
	public String editMicroKitcheScreen(Map<String, Object> map, HttpServletRequest request, @PathVariable("microKitchenScreenId")
	Integer microKitchenScreenId) {
		map.put("microKitchenScreen", microKitchenScreenService.getMicroKitchenScreen(microKitchenScreenId));
		
		ArrayList<FulfillmentCenter> kitchenScreenList  =  new ArrayList<FulfillmentCenter>();
		ArrayList<MicroKitchenScreen> microKitchenScreen = new ArrayList<MicroKitchenScreen>();
		kitchenScreenList  = (ArrayList<FulfillmentCenter>) fulfillmentCenterService.getKitchenScreens((Integer) request.getSession().getAttribute("restaurantId"));
		map.put("kitchenScreenList",kitchenScreenList);
		if(kitchenScreenList.size()>0){
			for(FulfillmentCenter  ks: kitchenScreenList){
				ArrayList<MicroKitchenScreen> mk = (ArrayList<MicroKitchenScreen>) microKitchenScreenService.getMicroKitchenScreensByKitchen(ks.getId());
				microKitchenScreen.addAll(mk);
		}
		}
		map.put("microKitchenScreenList",microKitchenScreen);
		//map.put("microKitchenScreenList", microKitchenScreenService.getMicroKitchenScreensByUser((Integer) request.getSession().getAttribute("userId")));
		//map.put("kitchenScreenList", kitchenScreenService.getKitchenScreens((Integer) request.getSession().getAttribute("userId")));
		return "microKitchenScreen";
	}

	@RequestMapping(value = "/addMicroKitchenScreen", method = RequestMethod.POST)
	@ApiIgnore
	public String addMicroKitcheScreen(@ModelAttribute("microKitchenScreen")
	MicroKitchenScreen microKitchenScreen, BindingResult result) {
		microKitchenScreenService.addMicroKitchenScreen(microKitchenScreen);
		return "redirect:/restaurant/microKitchenScreens";
	}
	
	@RequestMapping("/deleteMicroKitchenScreen/{microKitchenScreenId}")
	@ApiIgnore
	public String deleteMicroKitchenScreen(@PathVariable("microKitchenScreenId")
	Integer microKitchenScreenId) {
		microKitchenScreenService.removeMicroKitchenScreen(microKitchenScreenId);
		return "redirect:/restaurant/microKitchenScreens/";
	}
	
	@RequestMapping("/kitchenScreens")
	@ApiIgnore
	public String listKitchenScreen(Map<String, Object> map, HttpServletRequest request) {
		ArrayList<FulfillmentCenter> kitchenScreenList  =  new ArrayList<FulfillmentCenter>();
		ArrayList<MicroKitchenScreen> microKitchenScreen = new ArrayList<MicroKitchenScreen>();
		kitchenScreenList  = (ArrayList<FulfillmentCenter>) fulfillmentCenterService.getKitchenScreens((Integer) request.getSession().getAttribute("restaurantId"));
		map.put("kitchenScreen", new FulfillmentCenter());
		map.put("kitchenScreenList",kitchenScreenList);
		//List<Integer> ffc=new ArrayList<>();
		//ffc.add(25);
		if(kitchenScreenList.size()>0){
			for(FulfillmentCenter  ks: kitchenScreenList){
				ArrayList<MicroKitchenScreen> mk = (ArrayList<MicroKitchenScreen>) microKitchenScreenService.getMicroKitchenScreensByKitchen(ks.getId());
				microKitchenScreen.addAll(mk);
		}
		}
		map.put("microScreen", microKitchenScreen);
		return "kitchenScreen";
	}

	@RequestMapping("/listFulfillmentCenters")
	@ApiIgnore
	public @ResponseBody List listFulfillmentCenters(HttpServletRequest request) {
		ArrayList<FulfillmentCenter> fulfillmentCenterList  =  new ArrayList<FulfillmentCenter>();
		fulfillmentCenterList  = (ArrayList<FulfillmentCenter>) fulfillmentCenterService.getKitchenScreens((Integer) request.getSession().getAttribute("restaurantId"));
		return fulfillmentCenterList;
	}

	@RequestMapping("/editKitchenScreen/{kitchenScreenId}")
	@ApiIgnore
	public String editKitcheScreen(Map<String, Object> map, HttpServletRequest request, @PathVariable("kitchenScreenId")
	Integer kitchenScreenId) {
		ArrayList<FulfillmentCenter> kitchenScreenList  =  new ArrayList<FulfillmentCenter>();
		ArrayList<MicroKitchenScreen> microKitchenScreen = new ArrayList<MicroKitchenScreen>();
		kitchenScreenList  = (ArrayList<FulfillmentCenter>) fulfillmentCenterService.getKitchenScreens((Integer) request.getSession().getAttribute("restaurantId"));
		map.put("kitchenScreen", fulfillmentCenterService.getKitchenScreen(kitchenScreenId));
		map.put("kitchenScreenList",kitchenScreenList);
		if(kitchenScreenList.size()>0){
			for(FulfillmentCenter  ks: kitchenScreenList){
				ArrayList<MicroKitchenScreen> mk = (ArrayList<MicroKitchenScreen>) microKitchenScreenService.getMicroKitchenScreensByKitchen(ks.getId());
				microKitchenScreen.addAll(mk);
		}
		}
		map.put("microScreen",microKitchenScreen);
		return "kitchenScreen";
	}
	
	@RequestMapping(value = "/addKitchenScreen", method = RequestMethod.POST)
	@ApiIgnore
	public String addKitcheScreen(@ModelAttribute("kitchenScreen")FulfillmentCenter kitchenScreen, BindingResult result,HttpServletRequest request) {
		fulfillmentCenterService.addKitchenScreen(kitchenScreen);
		TillDTO tillDto =  new TillDTO();
		tillDto.fulfillmentCenterId = kitchenScreen.getId();
		tillDto.balance=(float) 0.0;
		tillDto.tillName=kitchenScreen.getName()+"_SaleRegister";
		TreeMap<String, Object> response=(TreeMap<String, Object>) cashRegisterService.createNewTill(tillDto, (Integer) request.getSession().getAttribute("userId"));
		
		return "redirect:/restaurant/kitchenScreens";
	}
	
	@RequestMapping("/deleteKitchenScreen/{kitchenScreenId}")
	@ApiIgnore
	public String deleteKitchenScreen(@PathVariable("kitchenScreenId")
	Integer kitchenScreenId) {
		fulfillmentCenterService.removeKitchenScreen(kitchenScreenId);
		return "redirect:/restaurant/kitchenScreens/";
	}

	@RequestMapping(value = "/getRestaurantTaxInfo",method = RequestMethod.GET)
	public @ResponseBody List getRestaurantTaxInfo(HttpServletRequest request, @RequestParam(required=false) String restaurantId) {
		Integer restaurantID=Integer.parseInt(restaurantId);
		if(restaurantID==null || "undefined".equalsIgnoreCase(restaurantId)){
			restaurantID = (Integer)request.getSession().getAttribute("restaurantId");
		}
		return taxTypeService.listTaxTypesByRestaurantId(restaurantID);
	}
	

	@RequestMapping(value="/getDiscountOptions",method = RequestMethod.GET)
	public @ResponseBody List<Discount_Charges> getDiscountOptions(HttpServletRequest request, @RequestParam String restaurantId) throws UnsupportedEncodingException, MessagingException, ParseException{
		
		List<Discount_Charges> discount_Charges = null;
		String restaurantIdStr = restaurantId;
		Integer  restId =  Integer.parseInt(restaurantIdStr);
		if(restId !=null){
			discount_Charges  = restaurantService.listDiscountCharges(restId);
		}
		return discount_Charges;
	}
	
	@RequestMapping("/listDiscountCharges")
	@ApiIgnore
	public String listDiscountCharges(Map<String, Object> map, HttpServletRequest request) {
		map.put("dcType", new Discount_Charges());
		map.put("dcTypeList", restaurantService.listDiscountCharges((Integer) request.getSession().getAttribute("restaurantId")));
		map.put("chargeTypes", ChargesType.values());
		map.put("category", AdditionalCategories.values());
		return "manageDiscounts";
	}

	@RequestMapping("/editDC/{dcid}")
	@ApiIgnore
	public String editDC(Map<String, Object> map, HttpServletRequest request, @PathVariable("dcid")	Integer id) {
		
		Discount_Charges dcType = restaurantService.getDCById(id);
		map.put("dcType", dcType);
		map.put("chargeTypes", ChargesType.values());
		map.put("category", AdditionalCategories.values());
		map.put("dcTypeList", restaurantService.listDiscountCharges((Integer)request.getSession().getAttribute("restaurantId")));
		return "manageDiscounts";
	}
	
	@RequestMapping(value = "/addDC", method = RequestMethod.POST)
	@ApiIgnore
	public String addDC(@ModelAttribute("dc") Discount_Charges disount_Charges, BindingResult result) {
		restaurantService.addDC(disount_Charges);
		return "redirect:/restaurant/listDiscountCharges";
	}
	
	@RequestMapping(value="/deleteDC/{dcId}")
	@ApiIgnore
	public String deleteDC(@PathVariable Integer dcId){
		
		restaurantService.removeDC(dcId);
		return "redirect:/restaurant/listDiscountCharges";
	}
	
	
	@RequestMapping(value="/getNutrientOptions",method = RequestMethod.GET)
	public  List<Nutrientes> getNutrientOptions(HttpServletRequest request) throws UnsupportedEncodingException, MessagingException, ParseException{
		
		List<Nutrientes> nutrientes = null;
		Integer  restId =  (Integer) request.getSession().getAttribute("restaurantId");
		if(restId !=null){
			nutrientes  = restaurantService.getNutirentList(restId);
		}
		return nutrientes;
	}
	
	@RequestMapping("/listNutrientes")
	@ApiIgnore
	public String listNutirentes(Map<String, Object> map, HttpServletRequest request) {
		map.put("Nutrient", new Nutrientes());
		map.put("nutrientList", restaurantService.getNutirentList((Integer) request.getSession().getAttribute("restaurantId")));
		map.put("dishType", dishTypeService.listDishTypesByRestaurantId((Integer) request.getSession().getAttribute("restaurantId")));
		map.put("addOnDishType", addOnDishTypeService.listDishTypesByRestaurant((Integer) request.getSession().getAttribute("restaurantId")));
		return "manageNutrientes";
	}
 
	@RequestMapping("/editNutrientes/{id}")
	@ApiIgnore
	public String editNutrientes(Map<String, Object> map, HttpServletRequest request, @PathVariable("id")	Integer id) {
		
		Nutrientes nutrientes = restaurantService.getNutrientes(id);
		map.put("Nutrient",nutrientes);
		map.put("nutrientList", restaurantService.getNutirentList((Integer) request.getSession().getAttribute("restaurantId")));
		map.put("dishType", dishTypeService.listDishTypesByRestaurantId((Integer) request.getSession().getAttribute("restaurantId")));
		map.put("addOnDishType", addOnDishTypeService.listDishTypesByRestaurant((Integer) request.getSession().getAttribute("restaurantId")));
		return "manageNutrientes";
	}
	
	@RequestMapping(value = "/addNutrientes", method = RequestMethod.POST)
	@ApiIgnore
	public String addNutrientes(@ModelAttribute("nutrientes") Nutrientes nutrientes, BindingResult result) {
		restaurantService.addNutrientes(nutrientes);
		return "redirect:/restaurant/listNutrientes";
	}
	
	@RequestMapping(value="/deleteNutrientes/{id}")
	@ApiIgnore
	public String deleteNutrientes(@PathVariable Integer id){
		restaurantService.removeNutrientes(id);
		return "redirect:/restaurant/listNutrientes";
	}
	
	// citrus response to sync with mobile app
	
	@RequestMapping(value="/appResponseByCitrus",method=RequestMethod.POST)
	public void getAppCheckResponseByCitrus(@ModelAttribute("SpringWeb")PaymentGatwayDetail pgDetails,  ModelMap model,Map<String, Object> map,HttpServletRequest request) throws IOException, MessagingException {

		Properties prop = new Properties();
		String propFileName = "citrusDetails.properties";
		
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
 
		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		} 
		String vanityUrl  = prop.getProperty("vanityUrl");
		String secret_key = prop.getProperty("secret_key");
        String data="";
        String orderId=request.getParameter("TxId");
        String txnStatus=request.getParameter("TxStatus");
        String TxMsg  =  request.getParameter("TxMsg");
        String amount=request.getParameter("amount"); 
        String pgTxnId=request.getParameter("pgTxnNo");
        String issuerRefNo=request.getParameter("issuerRefNo"); 
        String authIdCode=request.getParameter("authIdCode");
        String pgRespCode=request.getParameter("pgRespCode");
        String zipCode=request.getParameter("addressZip");
        String resSignature=request.getParameter("signature");
        String transactionID = request.getParameter("transactionID");
        String paymentMode = request.getParameter("paymentMode");
		String TxGateway = request.getParameter("TxGateway");
		String couponCode  =  request.getParameter("couponCode");
		String txnDateTime = request.getParameter("txnDateTime");
        //Binding all required parameters in one string (i.e. data)
        if (orderId != null) {
            data += orderId;
        }
        if (txnStatus != null) {
            data += txnStatus;
        }
        if (amount != null) {
            data += amount;
        }
        if (pgTxnId != null) {
            data += pgTxnId;
        }
        if (issuerRefNo != null) {
            data += issuerRefNo;
        }
        if (authIdCode != null) {
            data += authIdCode;
        }
        if (pgRespCode != null) {
            data += pgRespCode;
        }
        if (zipCode != null) {
            data += zipCode;
        }
        
        javax.crypto.Mac mac = null;
		try {
			mac = javax.crypto.Mac.getInstance("HmacSHA1");
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			restaurantService.emailException(ExceptionUtils.getStackTrace(e1),request);
			logger.info("Exception mail sent");
		}
        try {
			mac.init(new javax.crypto.spec.SecretKeySpec(secret_key.getBytes(), "HmacSHA1"));
		} catch (InvalidKeyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			restaurantService.emailException(ExceptionUtils.getStackTrace(e1),request);
			logger.info("Exception mail sent");
		}
        byte[] hexBytes = new org.apache.commons.codec.binary.Hex().encode(mac.doFinal(data.getBytes()));
        String signature = new String(hexBytes, "UTF-8");

        boolean flag = true;
        if (resSignature !=null && !resSignature.equalsIgnoreCase("") 
            && !signature.equalsIgnoreCase(resSignature)) {
            flag = false;
        }
        if (flag) {
        	logger.info("Citrus Response Signature match for checkId :"+orderId);
     } else { 
          logger.info("Citrus Response Signature and Our (Merchant) Signature Mis-Match");
     }
        
		int checkId = Integer.parseInt(orderId);
		Check check= checkService.getCheck(checkId);
		Order order= orderService.getOrder(check.getOrderId());
		
		
		if(txnStatus.equals("SUCCESS")){
		logger.info("Transaction Success");
		order.setPaymentStatus(PaymentMode.PG.toString());
		orderService.addOrder(order);
		}
		else if(txnStatus.equals("FAILURE")){
			logger.info("Transaction Failed");	
			order.setPaymentStatus("PG_PENDING");
			orderService.addOrder(order);
		}
		else if(txnStatus.equals("CANCELED")){
			logger.info("Transaction Canceled");	
			order.setPaymentStatus("PG_PENDING");
			order.setStatus(com.cookedspecially.enums.order.Status.CANCELLED);
			check.setStatus(Status.Cancel);
			logger.info("order status :"+order.getStatus());
			orderService.addOrder(order);
			logger.info("Check cancelled id :"+check.getCheckId()+"/"+check.getInvoiceId());
		}
		else{
			order.setPaymentStatus("PG_PENDING");
			orderService.addOrder(order);
			check.setStatus(Status.Pending);
		}
		Customer customer = new Customer();
		logger.info("response recieved from Citrus check Id :"+checkId+"/"+check.getInvoiceId());
        TreeMap<String,String> parameters = new TreeMap<String,String>();

        if(txnStatus.equals("SUCCESS")){
			model.addAttribute("paymentStatus","paid");
			check.setStatus(Status.Paid);
		}
		logger.info("PG transaction Id :" +pgTxnId);
		check.setTransactionId(pgTxnId);
		check.setTransactionStatus(txnStatus);
		check.setResponseCode(pgRespCode);
		checkService.addCheck(check);
		
			model.addAttribute("TXNID",pgTxnId);
			model.addAttribute("ORDERID",orderId); // Merchantâ€™s order id
			model.addAttribute("BANKTXNID",authIdCode);
			model.addAttribute("TXNAMOUNT",amount);
			model.addAttribute("STATUS",txnStatus);
			model.addAttribute("RESPCODE",pgRespCode);
			model.addAttribute("RESPMSG",TxMsg);
			model.addAttribute("TXNDATE",txnDateTime);
			model.addAttribute("GATEWAYNAME",TxGateway);
			model.addAttribute("PAYMENTMODE",paymentMode);
	
			if (check != null) {
				Restaurant restaurant= restaurantService.getRestaurant(check.getRestaurantId());
				
				model.addAttribute("CONTACT",restaurant.getBussinessPhoneNo());
				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(restaurant.getTimeZone()));
				cal.setTime(check.getOpenTime());
				
				DateFormat formatter1;
				formatter1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				formatter1.setTimeZone(cal.getTimeZone());
				map.put("checkDate", formatter1.format(cal.getTime()));
				
			   CheckResponse checkResponse = new CheckResponse(check,taxTypeService,null,restaurant);
				map.put("checkRespone", checkResponse);
				if (check.getCustomerId() > 0) {
					customer = customerService.getCustomer(check.getCustomerId());
					map.put("customer", customer);
				} else if (check.getTableId() > 0) {
					map.put("tableId", check.getTableId());
				}
				Map<String, JsonDish> itemsMap = new TreeMap<String, JsonDish>();
				List<CheckDishResponse> items = checkResponse.getItems();
				List<JsonAddOn> jsonAdd =  new ArrayList<JsonAddOn>();
				for (CheckDishResponse item : items) {
					if (itemsMap.containsKey(item.getName())) {
						JsonDish jsonDish = itemsMap.get(item.getName());
						jsonDish.setPrice(jsonDish.getPrice() + item.getPrice());
						jsonDish.setQuantity(jsonDish.getQuantity() + 1);
					}
					else {
						JsonDish jsonDish = new JsonDish();
						jsonDish.setQuantity(1);
						jsonDish.setName(item.getName());
						jsonDish.setId(item.getDishId());
						jsonDish.setPrice(item.getPrice());
						List<OrderAddOn> orderAddOn = item.getAddOnresponse();
						if(orderAddOn!=null){
						for( OrderAddOn oad : orderAddOn){
							JsonAddOn jsonAddOn = new JsonAddOn();
							jsonAddOn.setId(oad.getAddOnId());
							jsonAddOn.setDishId(item.getDishId());
							jsonAddOn.setName(oad.getName());
							jsonAddOn.setPrice(oad.getPrice());
							jsonAddOn.setQuantity(oad.getQuantity());
							jsonAdd.add(jsonAddOn);
							jsonDish.setAddOns(jsonAdd);
						}
					}
						itemsMap.put(item.getName(), jsonDish);
					}
				}
				map.put("itemsMap", itemsMap);
			}
			if(txnStatus.equals("SUCCESS")){
				logger.info("TXN SUCCESS");
				order.setPaymentStatus(PaymentMode.PG.toString());
				orderService.addOrder(order);
 				logger.info("check Id :"+checkId+"/"+check.getInvoiceId()+ " email :"+customer.getEmail());
				 try{
					 orderController.emailCheckFromServer(request,checkId,customer.getEmail(),"saladdaysemailbill",null,null,null,null,0);
				logger.info("email sent for check Id "+checkId+"/"+check.getInvoiceId());
				 }
				 catch(Exception e){
					 logger.info("Email sent fail");
					 e.printStackTrace();
					 restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
					 logger.info("Exception mail sent");
				 }

			}
			else if(txnStatus.equals("FAILURE")){
				order.setPaymentStatus(PaymentMode.PG.toString());
				orderService.addOrder(order);
				try{
					orderController.emailCheckFromServer(request, checkId,customer.getEmail(),"saladdaysemailbill",null,null,null,null,0);
			   logger.info("email sent for check Id : "+checkId+"/"+check.getInvoiceId());
				}
				catch(Exception e){
					logger.info("Email sent fail");
					e.printStackTrace();
					restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
					logger.info("Exception mail sent");
				}
				
				logger.info("TXN Failure or Canceled");
			}
			else if(txnStatus.equals("CANCELED")){
					order.setPaymentStatus("PG_PENDING");
					orderService.addOrder(order);
					logger.info("CANCELED Status  ");
				}
			else {
				order.setPaymentStatus("PG_PENDING");
				orderService.addOrder(order);
				try{
					orderController.emailCheckFromServer(request, checkId,customer.getEmail(),"saladdaysemailbill",null,null,null,null,0);
					   logger.info("email sent for check Id: "+checkId+"/"+check.getInvoiceId());
						}
						catch(Exception e){
							logger.info("Email sent fail");
							e.printStackTrace();
							restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
							logger.info("Exception mail sent");
						}
				logger.info("PENDING state ");
			}
			}
	@RequestMapping(value="/removeImage",method=RequestMethod.GET )
	@ApiIgnore
	public String removeCloseImage(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response){
		
		String parameter = request.getParameter("parameter");
		Object userIdObj = request.getSession().getAttribute("restaurantId");
		if(userIdObj != null) {
			Restaurant rest = restaurantService.getRestaurant((Integer) userIdObj);
			if(parameter.equalsIgnoreCase("businessLandscapeImageUrl")){
				rest.setBusinessLandscapeImageUrl("");
			}else if(parameter.equalsIgnoreCase("businessPortraitImageUrl")){
				rest.setBusinessPortraitImageUrl("");
			}
			else if(parameter.equalsIgnoreCase("closeImageLink")){
				rest.setCloseImageLink("");
			}else if(parameter.equalsIgnoreCase("headerImageUrl")){
				rest.setHeaderImageUrl("");
			}
			else if(parameter.equalsIgnoreCase("marketingImage")){
				rest.setMarketingImage("");
			}
			restaurantService.addRestaurant(rest);
		}
		return "redirect:/restaurant/edit/";
	}
	

	@RequestMapping(value="/responseByMobileApp",method=RequestMethod.POST)
	public @ResponseBody Map<String,String> getCheckResponseByPaytm(ModelMap model,HttpServletRequest request, @RequestParam String checkId, @RequestParam String status, HttpServletResponse response) throws IOException {
		//String checkId= request.getParameter("checkId");
		//String status = request.getParameter("status");
		Map map = new HashMap();
		if(checkId!=null&& status!=null){
			Check check = checkService.getCheck(Integer.parseInt(checkId));
			if(check!=null){
			if(status.equalsIgnoreCase("success")){
				
				Order order  =  orderService.getOrder(check.getOrderId());
				if("PG_PENDING".equalsIgnoreCase(order.getPaymentStatus())){
					check.setStatus(Status.Paid);
					order.setPaymentStatus("PG");
					map.put("status","success");
					Customer customer = customerService.getCustomer(check.getCustomerId());
					checkService.addCheck(check);
					orderService.addOrder(order);
					try{ 
					orderController.emailCheckFromServer(request,check.getCheckId(),customer.getEmail(),"saladdaysemailbill",null,null,null,null,0);
					map.put("message","Email sent!");
					}catch(MessagingException e){
						e.printStackTrace();
						logger.info("sending check failed");
						map.put("message","Sending email failed");
						try {
							restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
						} catch (MessagingException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						logger.info("Exception mail sent");
					}
				}else {
					map.put("status","failed");
					map.put("message","Invalid request");
				}
				checkService.addCheck(check);
				orderService.addOrder(order);
				return map;
			}else if(status.equalsIgnoreCase("failure")){
				check.setStatus(Status.Cancel);
				Order order  =  orderService.getOrder(check.getOrderId());
				order.setStatus(com.cookedspecially.enums.order.Status.CANCELLED);
				checkService.addCheck(check);
				orderService.addOrder(order);
				map.put("status","success");
				map.put("message","Your order has been cancelled!");
				return map;
			}
		}
			map.put("status","failure");
			map.put("message","Invalid checkId");
			return map;
		}
		map.put("status","failure");
		map.put("message","Invalid request");
		return map;
	}
	    
	@RequestMapping(value="/redirectToPayPal", method=RequestMethod.GET)
	@ApiIgnore
	public void getCheckRedirectToPayPal(@RequestParam("checkId") String orderId, Map<String, String> map, HttpServletRequest request, HttpServletResponse resps) throws IOException, PayPalRESTException, SSLConfigurationException{
			int id=Integer.parseInt(orderId);
			Check check=checkService.getCheck(id);
			REQUEST = request;
			String scheme = request.getScheme();
			String serverName = request.getServerName();
			int serverPort = request.getServerPort();
			String contextPath = request.getContextPath();  // includes leading forward slash
			String resultPath = scheme + "://" + serverName + ":" + serverPort + contextPath;
			logger.info("Result path: " + resultPath);
			Restaurant rest = restaurantService.getRestaurant(check.getRestaurantId());
			//CheckResponse checkResponse = new CheckResponse(check,taxTypeService,null,rest);
			logger.info("Redirected to PayPal Gateway DB check Id :"+id);
			//double orderAmount = checkResponse.getRoundedOffTotal();
			//Long orderAmount =(long) 1;
			Order order= orderService.getOrder(check.getOrderId());
			order.setPaymentStatus(PaymentMode.PG_PENDING.toString());
			orderService.addOrder(order);
			check.setStatus(Status.Pending);
			String token = getECToken(check, rest,resultPath);
			Properties prop = new Properties();
			String propFileName = "paypal_"+rest.getParentRestaurantId()+".properties";
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
	 
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
			 checkService.addCheck(check);
			 String redirectURL = prop.getProperty("redirectURL");
	         resps.sendRedirect(redirectURL+""+token);
		}
		
		public String getECToken(Check check,Restaurant rest,String resultPath) throws SSLConfigurationException, IOException{
			PaymentDetailsType paymentDetails = new PaymentDetailsType();
			paymentDetails.setPaymentAction(PaymentActionCodeType.fromValue("Sale"));
			
			Double itemAmount = check.getRoundOffTotal();
			logger.info("item Amount : "+itemAmount);

			BasicAmountType orderTotal = new BasicAmountType();
			orderTotal.setCurrencyID(CurrencyCodeType.fromValue(rest.getCurrency()));
			orderTotal.setValue(itemAmount.toString()); 
			paymentDetails.setOrderTotal(orderTotal);
			paymentDetails.setInvoiceID(check.getInvoiceId());
			List<PaymentDetailsType> paymentDetailsList = new ArrayList<PaymentDetailsType>();
			paymentDetailsList.add(paymentDetails);
			
			Properties prop = new Properties();
			logger.info("orgId : "+rest.getParentRestaurantId());
			String propFileName = "paypal_"+rest.getParentRestaurantId()+".properties";
			
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
			String userName = prop.getProperty("UserName");
			String password = prop.getProperty("Password");
			String signature = prop.getProperty("Signature");
			String returnURL = prop.getProperty("returnURL");
			String cancelURL = prop.getProperty("cancelURL");
			
			SetExpressCheckoutRequestDetailsType setExpressCheckoutRequestDetails = new SetExpressCheckoutRequestDetailsType();
			setExpressCheckoutRequestDetails.setReturnURL(resultPath+""+returnURL+""+check.getCheckId());
			setExpressCheckoutRequestDetails.setCancelURL(resultPath+""+cancelURL+""+check.getCheckId());
			setExpressCheckoutRequestDetails.setSolutionType(SolutionTypeType.SOLE);
			setExpressCheckoutRequestDetails.setLandingPage(LandingPageType.BILLING);
			setExpressCheckoutRequestDetails.setPaymentDetails(paymentDetailsList);
			SetExpressCheckoutRequestType setExpressCheckoutRequest = new SetExpressCheckoutRequestType(setExpressCheckoutRequestDetails);
			setExpressCheckoutRequest.setVersion("104.0");

			SetExpressCheckoutReq setExpressCheckoutReq = new SetExpressCheckoutReq();
			setExpressCheckoutReq.setSetExpressCheckoutRequest(setExpressCheckoutRequest);

			Map<String, String> sdkConfig = new HashMap<String, String>();
			sdkConfig.put("mode", "live");
			sdkConfig.put("acct1.UserName",userName);
			sdkConfig.put("acct1.Password",password);
			sdkConfig.put("acct1.Signature",signature);
			PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(sdkConfig);
			try {
				SetExpressCheckoutResponseType setExpressCheckoutResponse = service.setExpressCheckout(setExpressCheckoutReq);
				if(setExpressCheckoutResponse!=null)
				return setExpressCheckoutResponse.getToken();
			} catch (InvalidCredentialException | HttpErrorException | InvalidResponseDataException
					| ClientActionRequiredException | MissingCredentialException
					| OAuthException | IOException | InterruptedException
					| ParserConfigurationException | SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					restaurantService.emailException(ExceptionUtils.getStackTrace(e),REQUEST);
				} catch (MessagingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				logger.info("Exception mail sent");
			}
			return null;
		}
		
		@RequestMapping(value="/responseByPaypal",method=RequestMethod.GET)
		public String getCheckResponseByPaypal(ModelMap model,Map<String, Object> map,HttpServletRequest request, @RequestParam String status, @RequestParam String checkId) throws IOException, MessagingException {

			//String status = request.getParameter("status");
			//String checkId = request.getParameter("checkId");
			Check check = null;
			Restaurant rest = null;
			if(checkId!=null){
				check =  checkService.getCheck(Integer.parseInt(checkId));
				rest =  restaurantService.getRestaurant(check.getRestaurantId());
			}
			// Retrieve PayerID form PayPal GET call
			String payerId = request.getParameter("PayerID");
			String token = request.getParameter("token");
			logger.info(token+ "--- "+payerId+"--reached--"+checkId+"---"+status);
			GetExpressCheckoutDetailsResponseType getExpressCheckoutDetailsResponse=null;
			DoExpressCheckoutPaymentResponseType doExpressCheckoutPaymentResponse=null;
			
			Properties prop = new Properties();
			logger.info("orgId : "+rest.getParentRestaurantId());
			String propFileName = "paypal_"+rest.getParentRestaurantId()+".properties";
			
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
			String userName = prop.getProperty("UserName");
			String password = prop.getProperty("Password");
			String signature = prop.getProperty("Signature");
			//String returnURL = prop.getProperty("returnURL");
			//String cancelURL = prop.getProperty("cancelURL");
			if(status!=null){
				if(status.equalsIgnoreCase("success")){
					GetExpressCheckoutDetailsRequestType getExpressCheckoutDetailsRequest = new GetExpressCheckoutDetailsRequestType(token);
					getExpressCheckoutDetailsRequest.setVersion("104.0");
			
					GetExpressCheckoutDetailsReq getExpressCheckoutDetailsReq = new GetExpressCheckoutDetailsReq();
					getExpressCheckoutDetailsReq.setGetExpressCheckoutDetailsRequest(getExpressCheckoutDetailsRequest);
			
					Map<String, String> sdkConfig = new HashMap<String, String>();
					sdkConfig.put("mode", "live");
					sdkConfig.put("acct1.UserName", userName);
					sdkConfig.put("acct1.Password", password);
					sdkConfig.put("acct1.Signature",signature);
					PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(sdkConfig);
					
					try {
						getExpressCheckoutDetailsResponse = service.getExpressCheckoutDetails(getExpressCheckoutDetailsReq);
						
					} catch (SSLConfigurationException | InvalidCredentialException
							| HttpErrorException | InvalidResponseDataException
							| ClientActionRequiredException | MissingCredentialException
							| OAuthException | InterruptedException
							| ParserConfigurationException | SAXException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
						restaurantService.emailException(ExceptionUtils.getStackTrace(e2),request);
						logger.info("Exception mail sent");
					}
				}else if("cancel".equalsIgnoreCase(status)){
					//getExpressCheckoutDetailsResponse
					return placePayPalOrder(model,map,status,request,check.getCheckId(),"CANCEL","cancelled by user",payerId);				
				}
				
				if(getExpressCheckoutDetailsResponse!=null){
					if("success".equalsIgnoreCase(getExpressCheckoutDetailsResponse.getAck().toString())){
						PaymentDetailsType paymentDetail = new PaymentDetailsType();
						paymentDetail.setNotifyURL("");
						BasicAmountType orderTotal = new BasicAmountType();
						Double itemAmount = check.getRoundOffTotal();
						orderTotal.setValue(itemAmount.toString());
						orderTotal.setCurrencyID(CurrencyCodeType.fromValue(rest.getCurrency()));
						paymentDetail.setOrderTotal(orderTotal);
						paymentDetail.setPaymentAction(PaymentActionCodeType.fromValue("Sale"));
						List<PaymentDetailsType> paymentDetails = new ArrayList<PaymentDetailsType>();
						paymentDetails.add(paymentDetail);
										
						DoExpressCheckoutPaymentRequestDetailsType doExpressCheckoutPaymentRequestDetails = new DoExpressCheckoutPaymentRequestDetailsType();
						doExpressCheckoutPaymentRequestDetails.setToken(token);
						doExpressCheckoutPaymentRequestDetails.setPayerID(payerId);
						doExpressCheckoutPaymentRequestDetails.setPaymentDetails(paymentDetails);
						doExpressCheckoutPaymentRequestDetails.setPaymentAction(PaymentActionCodeType.fromValue("Sale"));
						
						DoExpressCheckoutPaymentRequestType doExpressCheckoutPaymentRequest = new DoExpressCheckoutPaymentRequestType(doExpressCheckoutPaymentRequestDetails);
						doExpressCheckoutPaymentRequest.setVersion("104.0");

						DoExpressCheckoutPaymentReq doExpressCheckoutPaymentReq = new DoExpressCheckoutPaymentReq();
						doExpressCheckoutPaymentReq.setDoExpressCheckoutPaymentRequest(doExpressCheckoutPaymentRequest);

						Map<String, String> sdkConfig = new HashMap<String, String>();
						sdkConfig.put("mode", "live");
						sdkConfig.put("acct1.UserName", userName);
						sdkConfig.put("acct1.Password", password);
						sdkConfig.put("acct1.Signature",signature);
						PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(sdkConfig);
						try {
							 doExpressCheckoutPaymentResponse = service.doExpressCheckoutPayment(doExpressCheckoutPaymentReq);
						} catch (SSLConfigurationException
								| InvalidCredentialException | HttpErrorException
								| InvalidResponseDataException
								| ClientActionRequiredException
								| MissingCredentialException | OAuthException
								| InterruptedException
								| ParserConfigurationException | SAXException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
							logger.info("Exception mail sent");
						}
						
					}
					
					if(doExpressCheckoutPaymentResponse!=null){
						if("success".equalsIgnoreCase(doExpressCheckoutPaymentResponse.getAck().toString())){
							return placePayPalOrder(model,map,"success",request,check.getCheckId(),doExpressCheckoutPaymentResponse.getAck().toString(),doExpressCheckoutPaymentResponse.getDoExpressCheckoutPaymentResponseDetails().toString(),payerId);
						}else {
							return placePayPalOrder(model,map,"cancel",request,check.getCheckId(),doExpressCheckoutPaymentResponse.getAck().toString(),doExpressCheckoutPaymentResponse.getDoExpressCheckoutPaymentResponseDetails().toString(),payerId);
						}
					}
				}
				
			}
			return "";	
			// Order Id used for internal tracking purpose
			}
		
		public String placePayPalOrder(ModelMap model,Map<String, Object> map,String txnStatus , HttpServletRequest request,Integer checkId,String ack, String responseDetial,String payerId) throws IOException, MessagingException {
	        
			Check check= checkService.getCheck(checkId);
			Order order= orderService.getOrder(check.getOrderId());
			if(txnStatus.equalsIgnoreCase("SUCCESS")){
				logger.info("Transaction Success");
				order.setPaymentStatus(PaymentMode.PG.toString());
				orderService.addOrder(order);
			}
			else if(txnStatus.equalsIgnoreCase("FAILURE")){
				logger.info("Transaction Failed");	
				order.setPaymentStatus("PG_PENDING");
				orderService.addOrder(order);
			}
			else if(txnStatus.equalsIgnoreCase("cancel")){
				logger.info("Transaction Canceled");	
				order.setPaymentStatus("PG_PENDING");
				order.setStatus(com.cookedspecially.enums.order.Status.CANCELLED);
				try {
					orderController.restoreStock(check,0);
				} catch (ParseException e) {
					e.printStackTrace();
					restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
					logger.info("Exception mail sent");
				}
				check.setBill(0);
				check.setRoundOffTotal(0);
				check.setOutCircleDeliveryCharges(0);
				check.setStatus(Status.Cancel);
				order.setStatus(com.cookedspecially.enums.order.Status.CANCELLED);
				logger.info("order status :"+order.getStatus());
				orderService.addOrder(order);
				logger.info("Check cancelled id/InvoiceId :"+check.getCheckId()+"/"+check.getInvoiceId());
			}
			else{
				order.setPaymentStatus("PG_PENDING");
				orderService.addOrder(order);
			}
			Customer customer = new Customer();
			logger.info("response recieved from paypal check Id :"+checkId+"/"+check.getInvoiceId());
	        TreeMap<String,String> parameters = new TreeMap<String,String>();

	        if(txnStatus.equalsIgnoreCase("success")){
				model.addAttribute("paymentStatus","paid");
				check.setStatus(Status.Paid);
	        }else if(txnStatus.equalsIgnoreCase("cancel")){
				check.setStatus(Status.Unpaid);
	        }else if(txnStatus.equalsIgnoreCase("FAILURE")){
				check.setStatus(Status.Pending);
			}else {
				check.setStatus(Status.Pending);
			}
	    	//check.setStatus(Status.Pending);
			logger.info("PG transaction Id :" +payerId);
			check.setTransactionId(payerId);
			check.setTransactionStatus(txnStatus);
			check.setResponseCode(ack);
			checkService.addCheck(check);
			
				if (check != null) {
					Restaurant restaurant= restaurantService.getRestaurant(check.getRestaurantId());
					
					model.addAttribute("CONTACT",restaurant.getBussinessPhoneNo());
					Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(restaurant.getTimeZone()));
					cal.setTime(check.getOpenTime());
					DateFormat formatter1;
					formatter1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					formatter1.setTimeZone(cal.getTimeZone());
					map.put("checkDate", formatter1.format(cal.getTime()));
					//Restaurant restaurant = restaurantService.getRestaurant(check.getRestaurantId());
					Restaurant org =  restaurantService.getParentRestaurant(restaurant.getParentRestaurantId());
					restaurant.setWebsiteURL(org.getWebsiteURL());
				    CheckResponse checkResponse = new CheckResponse(check,taxTypeService,null,restaurant);
				    logger.info("setting json tax object"+checkResponse.getRoundedOffTotal());
					logger.info("setting Invoice rounndOffTotal."+checkResponse.getRoundedOffTotal());
					if(restaurant!=null){
					if(restaurant.isRoundOffAmount()){
						check.setRoundOffTotal(Math.round(checkResponse.getRoundedOffTotal()));
						checkResponse.setRoundedOffTotal(Math.round(checkResponse.getRoundedOffTotal()));
					}else{
				    	check.setRoundOffTotal(checkResponse.getRoundedOffTotal());
					}
					}
					map.put("checkRespone", checkResponse);
					map.put("restaurant", restaurant);
					if (check.getCustomerId() > 0) {
						customer = customerService.getCustomer(check.getCustomerId());
						map.put("customer", customer);
					} else if (check.getTableId() > 0) {
						map.put("tableId", check.getTableId());
					}
					Map<String, JsonDish> itemsMap = new TreeMap<String, JsonDish>();
					List<CheckDishResponse> items = checkResponse.getItems();
					List<JsonAddOn> jsonAdd =  new ArrayList<JsonAddOn>();
					for (CheckDishResponse item : items) {
						if (itemsMap.containsKey(item.getName())) {
							JsonDish jsonDish = itemsMap.get(item.getName());
							jsonDish.setPrice(jsonDish.getPrice() + item.getPrice());
							jsonDish.setQuantity(jsonDish.getQuantity() + 1);
						}
						else {
							JsonDish jsonDish = new JsonDish();
							jsonDish.setQuantity(1);
							jsonDish.setName(item.getName());
							jsonDish.setId(item.getDishId());
							jsonDish.setPrice(item.getPrice());
							List<OrderAddOn> orderAddOn = item.getAddOnresponse();
							if(orderAddOn!=null){
							for( OrderAddOn oad : orderAddOn){
								JsonAddOn jsonAddOn = new JsonAddOn();
								jsonAddOn.setId(oad.getAddOnId());
								jsonAddOn.setDishId(item.getDishId());
								jsonAddOn.setName(oad.getName());
								jsonAddOn.setPrice(oad.getPrice());
								jsonAddOn.setQuantity(oad.getQuantity());
								jsonAdd.add(jsonAddOn);
								jsonDish.setAddOns(jsonAdd);
							}
						}
							itemsMap.put(item.getName(), jsonDish);
						}
					}
					map.put("itemsMap", itemsMap);
				}
				if(txnStatus.equalsIgnoreCase("SUCCESS")){
					logger.info("TXN SUCCESS");
					order.setPaymentStatus(PaymentMode.PG.toString());
					orderService.addOrder(order);
	 				logger.info("check Id :"+checkId+ " email :"+customer.getEmail());
					 try{
						 orderController.emailCheckFromServer(request,checkId,customer.getEmail(),"defaultemailbill",null,null,null,null,0);
					logger.info("email sent for check Id "+checkId);
					 }
					 catch(Exception e){
						 logger.info("Email sent fail");
						 e.printStackTrace();
						 restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
						 logger.info("Exception mail sent");
					 }
  
					return "custom/defaultbill";	
				}
				else if(txnStatus.equalsIgnoreCase("FAILURE")){
					order.setPaymentStatus("PG_PENDING");
					orderService.addOrder(order);
					try{
						orderController.emailCheckFromServer(request, checkId,customer.getEmail(),"defaultemailbill",null,null,null,null,0);
				   logger.info("email sent for check Id"+checkId);
					}
					catch(Exception e){
						logger.info("Email sent fail");
						e.printStackTrace();
						restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
						logger.info("Exception mail sent");
					}
					
					logger.info("TXN Failure or Canceled");
					return "responsePG";	
				}
				else if(txnStatus.equalsIgnoreCase("cancel")){
						order.setPaymentStatus("PG_PENDING");
						orderService.addOrder(order);
						model.addAttribute("STATUS","CANCELED");
						/*try{
						//	   emailCheckFromServer(request, checkId,customer.getEmail(),"saladdaysemailbill");
							   logger.info("email sent for check Id"+checkId);
								}
								catch(Exception e){
									logger.info("Email sent fail");
									e.printStackTrace();
								}*/
						logger.info("CANCELED Status  ");
						return "responsePG";
					}
				else {
					order.setPaymentStatus("PG_PENDING");
					orderService.addOrder(order);
					try{
						orderController.emailCheckFromServer(request, checkId,customer.getEmail(),"defaultemailbill",null,null,null,null,0);
						   logger.info("email sent for check Id"+checkId);
							}
							catch(Exception e){
								logger.info("Email sent fail");
								e.printStackTrace();
								restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
								logger.info("Exception mail sent");
							}
					logger.info("PENDING state ");
					return "responsePG";
				}
		}
		
		@RequestMapping(value="/requestOrderStatus",method=RequestMethod.GET)
		public @ResponseBody OrderStatusDTO  checkPaytmOrderStatus(HttpServletRequest request, @RequestParam String checkId) throws IOException {
	        Integer orderId = Integer.parseInt(checkId);
	        Check check = checkService.getCheck(orderId);
	        OrderStatusDTO orderStatusDTO = null;
	        
	       if(PaymentMode.PG_PENDING.toString().equalsIgnoreCase(check.getOrders().get(0).getPaymentStatus())){
		        logger.info("Checking Citrus order status : "+check.getCheckId()+"/"+check.getInvoiceId());
	        	orderStatusDTO =  restaurantService.checkCitrusOrderStatus(orderId);
	        	System.out.println(orderStatusDTO.orderStatus +"---"+orderStatusDTO.responseMsz);
	        }else if(PaymentMode.PAYTM_PENDING.toString().equalsIgnoreCase(check.getOrders().get(0).getPaymentStatus())){
	        	
		        logger.info("Checking paytm order status : "+check.getCheckId()+"/"+check.getInvoiceId());
	        	orderStatusDTO =  restaurantService.checkPaytmOrderStatus(orderId);
	        }
	        
	       if(orderStatusDTO!=null){
		        if("TXN_SUCCESS".equalsIgnoreCase(orderStatusDTO.orderStatus) || "SUCCESS".equalsIgnoreCase(orderStatusDTO.orderStatus)){
		        	if(check!=null){
		        		Customer customer  =  customerService.getCustomer(check.getCustomerId());
		        		try {
							orderController.emailCheckFromServer(request,check.getCheckId(),customer.getEmail(),"saladdaysemailbill",null,null,null,null,0);
						} catch (MessagingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							try {
								restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
							} catch (MessagingException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							logger.info("Exception mail sent");
						}
		        	}
		        }
		        }else{
		        	orderStatusDTO =  new OrderStatusDTO();
		        	orderStatusDTO.orderStatus ="Invalid Status";
		        	orderStatusDTO.responseMsz="Order status is not PAYTM_PENDING/PG_PENDING. Current order status is :"+check.getOrders().get(0).getPaymentStatus();
		        }
		        
	        return orderStatusDTO;
	        
		}

		
	    @RequestMapping(value="/serverResponseByPayTm",method=RequestMethod.POST)
		public void getServerResponseByPaytm(@RequestBody PaymentGatwayDetail pgDetails,HttpServletRequest request) throws IOException, MessagingException {

	        String Website_name=null;
			String MID=null;
			String Merchant_Key=null;
			String Industry_type_ID=null;
			String Channel_ID=null;
			int checkId = Integer.parseInt(pgDetails.getORDERID());
			Check check= checkService.getCheck(checkId);
			Restaurant rest = restaurantService.getRestaurant(check.getRestaurantId());
			Order order= orderService.getOrder(check.getOrderId());
			logger.info("Checking server response for check/InvoiceId"+check.getCheckId()+"/"+check.getInvoiceId());
	        if(pgDetails.getSTATUS().equals("TXN_SUCCESS")){
				logger.info("Transaction Success");
				order.setPaymentStatus(PaymentMode.PAYTM.toString());
				orderService.addOrder(order);
			}
			else if(pgDetails.getSTATUS().equals("TXN_FAILURE")){
	            logger.info("Transaction Failed");
	            order.setPaymentStatus(PaymentMode.PAYTM_PENDING.toString());
				orderService.addOrder(order);
			}
			else{
				order.setPaymentStatus(PaymentMode.PAYTM_PENDING.toString());
				orderService.addOrder(order);
			}

	        Customer customer = new Customer();
			logger.info(pgDetails.getSTATUS()+" response recieved from PayTm check Id/InvoiceId :"+checkId+"/"+check.getInvoiceId());
	        //TreeMap<String,String> parameters = new TreeMap<String,String>();

	        Properties prop = new Properties();
			String propFileName = "paytmDetails.properties";

	        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

	        if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
			 Website_name = prop.getProperty("Website_name");
			 MID = prop.getProperty("MID");
			 Merchant_Key = prop.getProperty("Merchant_Key");
			 Industry_type_ID = prop.getProperty("Industry_type_ID");
			 Channel_ID = prop.getProperty("Channel_ID");

	        if(pgDetails.getSTATUS().equals("TXN_SUCCESS")){
				check.setStatus(Status.Paid);
			}
			check.setTransactionId(pgDetails.getTXNID());
			check.setTransactionStatus(pgDetails.getSTATUS());
	        check.setResponseCode(pgDetails.getRESPCODE());
	        checkService.addCheck(check);

			//String merchantKey = "CR6j5q@0u5PJ1fEu"; //Key provided by Paytm

	        //Response Code , Response message, Transaction date, Payment MODE (credit,debit,netbanking) , Transaction Id,

			/*try {
				isValidChecksum = checkSumServiceHelper.verifycheckSum (Merchant_Key, parameters, paytmChecksum);
			} catch (Exception e) {
				e.printStackTrace();
			}*/
		

	        if (check != null) {
					Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(rest.getTimeZone()));
					cal.setTime(check.getOpenTime());

	            DateFormat formatter1;
					formatter1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					formatter1.setTimeZone(cal.getTimeZone());

					CheckResponse checkResponse = new CheckResponse(check,taxTypeService,null,rest);
				   logger.info("setting json tax object"+checkResponse.getRoundedOffTotal());
					logger.info("setting Invoice rounndOffTotal."+checkResponse.getRoundedOffTotal());
					if(rest!=null){
					if(rest.isRoundOffAmount()){
						check.setRoundOffTotal(Math.round(checkResponse.getRoundedOffTotal()));
						checkResponse.setRoundedOffTotal(Math.round(checkResponse.getRoundedOffTotal()));
					}else{
				    	check.setRoundOffTotal(checkResponse.getRoundedOffTotal());
					}
					}
					if (check.getCustomerId() > 0) {
						customer = customerService.getCustomer(check.getCustomerId());
					} else if (check.getTableId() > 0) {
					}
				}
				if(pgDetails.getSTATUS().equals("TXN_SUCCESS")){
					logger.info("TXN SUCCESS");
					order.setPaymentStatus(PaymentMode.PAYTM.toString());
					orderService.addOrder(order);
					logger.info("request Object"+request);
					logger.info("check Id/ InvoiceId "+checkId+"/"+check.getInvoiceId()+ "email"+customer.getEmail());
					 try{
						 orderController.emailCheckFromServer(request,checkId,customer.getEmail(),"saladdaysemailbill",null,null,null,null,0);
					logger.info("email sent for check Id/invoiceID : "+checkId+"/"+check.getInvoiceId());
					 }
					 catch(Exception e){
						 logger.info("Email sent fail");
						 e.printStackTrace();
						 restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
						logger.info("Exception mail sent");
					 }
	            }
				else if(pgDetails.getSTATUS().equals("TXN_FAILURE")){

	                order.setPaymentStatus("PAYTM_PENDING");
					order.setStatus(com.cookedspecially.enums.order.Status.CANCELLED);
					try {
						orderController.restoreStock(check,0);
					} catch (ParseException e) {
						e.printStackTrace();
						restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
						logger.info("Exception mail sent");
					}
					check.setBill(0);
					check.setRoundOffTotal(0);
					CreditTransactions ct = customerCreditService.getLastPendingTransaction(check.getCustomerId());
					if(ct!=null){
						if(ct.getStatus()==CreditTransactionStatus.PENDING){
						try {
							customerCreditService.updateBillRecoveryTransaction("FAILED",check.getCustomerId(), check.getCreditBalance(),"CREDIT", "Setting status failed of existing pending transaction");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
							logger.info("Exception mail sent");
						}
					}
					}
					check.setCreditBalance(0);
					check.setOutCircleDeliveryCharges(0);
					check.setStatus(Status.Cancel);
					order.setStatus(com.cookedspecially.enums.order.Status.CANCELLED);
					logger.info("order status :"+order.getStatus());
					checkService.addCheck(check);
					orderService.addOrder(order);
					logger.info("Check cancelled id/InvoiceId :"+check.getCheckId()+"/"+check.getInvoiceId());
	            }
				else{
						order.setPaymentStatus(PaymentMode.PAYTM_PENDING.toString());
						orderService.addOrder(order);
						try{
							orderController.emailCheckFromServer(request, checkId,customer.getEmail(),"saladdaysemailbill",null,null,null,null,0);
							   logger.info("email sent for check Id/InvoiceId "+checkId+"/"+check.getInvoiceId());
								}
								catch(Exception e){
									logger.info("Email sent fail");
									e.printStackTrace();
									restaurantService.emailException(ExceptionUtils.getStackTrace(e),request);
									logger.info("Exception mail sent");
								}
						logger.info("PENDING state ");
					}
				}
	    
		void updateIfExistInZomatoMenu(Integer resId,HttpServletRequest request){
			List<Menu> menuList = menuService.listMenuByRestaurant(resId);
			for(Menu menu :menuList){
				if(!menu.isPosVisible() &&  menu.getZomatoStatus()==com.cookedspecially.enums.Status.ACTIVE){
					try {
						ResponseDTO response =zomatoService.updateZomatoMenu(menu,request);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	  		
		}
