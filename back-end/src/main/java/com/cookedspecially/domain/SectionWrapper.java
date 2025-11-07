/**
 * 
 */
package com.cookedspecially.domain;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonRawValue;


/**
 * @author shashank
 *
 */
public class SectionWrapper implements Serializable {
    private static final long serialVersionUID = 1L;
    
	private Integer sectionId;
	
	private Integer userId;
	
	private String name;
	
	private String shortName;

	private String description;

	private String header;
	
	private String footer;
	
	private Float price;
	
    private List<DishWrapper> items;
    
    public static long compareTo( java.util.Date date1, java.util.Date date2 ){
  	  return date1.getTime() - date2.getTime();
  	}
	
	public static SectionWrapper getSectionWrapper(Section section, List<StockManagement> stockDishes,String timeZone) {
		SectionWrapper sectionWrapper = new SectionWrapper();
		sectionWrapper.setSectionId(section.getSectionId());
		sectionWrapper.setUserId(section.getUserId());
		sectionWrapper.setName(section.getName());
		sectionWrapper.setShortName(section.getShortName());
		sectionWrapper.setDescription(section.getDescription().replaceAll("'", "&#39;"));
		sectionWrapper.setHeader((section.getHeader()!=null && section.getHeader()!=" ")?"["+section.getHeader()+"]":"["+"-"+"]");
		sectionWrapper.setFooter(section.getFooter());
		sectionWrapper.setPrice(section.getPrice());
		List<Dish> dishes = section.getDishes();
		String formats ="yyyy-MM-dd HH:mm:ss";
		java.util.Date actualTime =  new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(formats);
		TimeZone tz =  TimeZone.getTimeZone("Asia/kolkata");
		formatter.setTimeZone(tz);
		Date today =  new Date();
		String currentIst = formatter.format(today);
		try {
			actualTime = formatter.parse(currentIst);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date expireDate = new Date();
		if (dishes != null) {
			sectionWrapper.items = new ArrayList<DishWrapper>();
			for (Dish dish : dishes) {
				HashMap<Integer,Integer> map  =  new HashMap<Integer,Integer>();
				List<Entry> sm = new ArrayList<Entry>();
				if (dish != null && !dish.getDisabled() && dish.getDishSize().size()==0) {
					if (dish.isDishActive()) {
						if(stockDishes!=null){
							for(StockManagement sd:stockDishes){
								if(sd.getDishId()==dish.getDishId()){
									SimpleDateFormat formatterD = new SimpleDateFormat(formats);
									TimeZone tzD =  TimeZone.getTimeZone(timeZone);
									formatterD.setTimeZone(tzD);
									try {
										expireDate = formatterD.parse(sd.getExpireDate());
									} catch (ParseException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									Calendar cal = Calendar.getInstance();
								    cal.setTime(expireDate);
								    if(compareTo(actualTime,expireDate)<0){
								    	 map.put(sd.getFulfillmentCenterId(),(map.get(sd.getFulfillmentCenterId())!=null?map.get(sd.getFulfillmentCenterId()):0)+sd.getRemainingQuantity());
								}
								}
							}
							List<Entry> entryList = new ArrayList<Entry>(map.entrySet());
							sm.addAll(entryList);
						}
						dish.setAvailableStock(sm);
						sectionWrapper.items.add(DishWrapper.getDishWrapper(dish,timeZone));
					
					}
				}
				else if (dish != null && !dish.getDisabled() && dish.getDishSize().size()>0) {
					if (dish.isDishActive()) {
						for(Dish_Size dishSize : dish.getDishSize()){
							HashMap<Integer,Integer> mapDishSize  =  new HashMap<Integer,Integer>();
							List<Entry> smDishSize = new ArrayList<Entry>();
						if(stockDishes!=null){
							for(StockManagement sd:stockDishes){
								if(sd.getDishId()==Integer.parseInt(dish.getDishId()+""+dishSize.getDishSizeId())){
									SimpleDateFormat formatterD = new SimpleDateFormat(formats);
									TimeZone tzD =  TimeZone.getTimeZone(timeZone);
									formatterD.setTimeZone(tzD);
									try {
										expireDate = formatterD.parse(sd.getExpireDate());
									} catch (ParseException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									Calendar cal = Calendar.getInstance();
								    cal.setTime(expireDate);
								    if(compareTo(actualTime,expireDate)<0){
								    	mapDishSize.put(sd.getFulfillmentCenterId(),(mapDishSize.get(sd.getFulfillmentCenterId())!=null?mapDishSize.get(sd.getFulfillmentCenterId()):0)+sd.getRemainingQuantity());
								}
								}
							}
							List<Entry> entryList = new ArrayList<Entry>(mapDishSize.entrySet());
							smDishSize.addAll(entryList);
						}
						dishSize.setAvailableStock(smDishSize);
						} 
						dish.setAvailableStock(sm);
						sectionWrapper.items.add(DishWrapper.getDishWrapper(dish,timeZone));
					
					}
				}
				
			}
		}
		return sectionWrapper;
	}


	public String getShortName() {
		return shortName;
	}


	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Integer getSectionId() {
		return sectionId;
	}


	public void setSectionId(Integer sectionId) {
		this.sectionId = sectionId;
	}


	public Integer getUserId() {
		return userId;
	}


	public void setUserId(Integer userId) {
		this.userId = userId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}

	@JsonRawValue
	public String getHeader() {
		return header;
	}


	public void setHeader(String header) {
		this.header = header;
	}


	public String getFooter() {
		return footer;
	}


	public void setFooter(String footer) {
		this.footer = footer;
	}


	public Float getPrice() {
		return price;
	}


	public void setPrice(Float price) {
		this.price = price;
	}


	public List<DishWrapper> getItems() {
		return items;
	}


	public void setItems(List<DishWrapper> items) {
		this.items = items;
	}
	
}
