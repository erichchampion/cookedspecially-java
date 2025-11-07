package com.cookedspecially.utility;

import com.cookedspecially.service.impl.GiftCardServiceImpl;
import com.google.gson.Gson;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Abhishek on 5/27/2017.
 */
public class DtoUtility {

    final static Logger logger = Logger.getLogger(GiftCardServiceImpl.class);

    static Gson gson = new Gson();

    public static Object convertToDto(Object classToBeConvertedObj, Class convertedToDTOClass) {
        return gson.fromJson(gson.toJson(classToBeConvertedObj), convertedToDTOClass);
    }

    public static String convertToString(Object classToBeConvertedObj) {
        return gson.toJson(classToBeConvertedObj);
    }

    public static void copyEntity(Object org, Object dest) {
        if (dest != null && org != null) {
            try {
                BeanUtils.copyProperties(dest, org);
            } catch (IllegalAccessException e) {
                logger.error(e.getStackTrace());
            } catch (InvocationTargetException e) {
                logger.error(e.getStackTrace());
            }
        }

    }


}
