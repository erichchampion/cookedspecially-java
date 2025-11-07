package com.cookedspecially.utility;


import org.apache.log4j.Logger;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Abhishek on 5/28/2017.
 */
public class GiftCardIdGenerator implements IdentifierGenerator {

    final static Logger logger = Logger.getLogger(GiftCardIdGenerator.class);

    public static String formateGiftCardId(String giftCard) {
        return giftCard.replaceAll("(.{4})(?!$)", "$1-");
    }

    @Override
    public Serializable generate(SessionImplementor sessionImplementor, Object obj) {
        return generateRandomIndex();
    }

    private String generateRandomIndex() {
    	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		String random = String.valueOf(System.nanoTime());
		String padding = String.valueOf(String.format("%016d", cal.get(Calendar.DAY_OF_MONTH)));
		String cardId=null;
		if(random.length()< 16)
			cardId=padding.substring(padding.length()-(16-random.length()), padding.length()) + "" + random;
		else if(random.length()> 16) 
			cardId = random.substring(random.length()-16, random.length());
		else
			cardId=random;
		logger.info("generated Gift Card="+formateGiftCardId(cardId));
		return cardId;
    }
}
