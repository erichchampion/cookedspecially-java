package com.cookedspecially.utility;
 

import javax.crypto.Mac;
 import javax.crypto.spec.SecretKeySpec;
 import java.io.BufferedReader;
 import java.io.DataOutputStream;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.io.StringReader;
 import java.net.ProtocolException;
 import java.net.URL;
 import java.util.HashMap;
 import java.util.Map;

 import javax.net.ssl.HttpsURLConnection;
 import javax.xml.parsers.DocumentBuilder;
 import javax.xml.parsers.DocumentBuilderFactory;
 import javax.xml.parsers.ParserConfigurationException;
 import javax.xml.xpath.XPath;
 import javax.xml.xpath.XPathExpressionException;
 import javax.xml.xpath.XPathFactory;

 import org.w3c.dom.Document;
 import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
public class Checksum {
public static String toHex(byte[] bytes) {
	StringBuilder buffer = new StringBuilder(bytes.length * 2);
	String str;
	for (Byte b : bytes) {
		str = Integer.toHexString(b);
		int len = str.length();
		if (len == 8)
		{
			buffer.append(str.substring(6));
		} else if (str.length() == 2){
			buffer.append(str);
		}else {
			buffer.append("0" + str);
		}
	}
	return buffer.toString();
}

public static String calculateChecksum(String secretKey, String allParamValue) throws Exception{
	byte[] dataToEncryptByte = allParamValue.getBytes(); 
	byte[] keyBytes = secretKey.getBytes();
	SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA256");
	Mac mac = Mac.getInstance("HmacSHA256");
	mac.init(secretKeySpec);
	byte[] checksumByte = mac.doFinal(dataToEncryptByte);
	String checksum = toHex(checksumByte);
	//String checksum = new String(checksumByte);
	return checksum;
}

public static boolean verifyChecksum(String secretKey, String allParamVauleExceptChecksum, String checksumReceived) throws Exception{
	byte[] dataToEncryptByte = allParamVauleExceptChecksum.getBytes();
	byte[] keyBytes = secretKey.getBytes();
	SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA256");
	Mac mac= Mac.getInstance("HmacSHA256");
	mac.init(secretKeySpec);
	byte[] checksumCalculatedByte = mac.doFinal(dataToEncryptByte);
	String checksumCalculated = toHex(checksumCalculatedByte);
	if(checksumReceived.equals(checksumCalculated)){
		return true;
	}else{
		return false;
	}
}

public static Map<String,String> verifyTransaction(String merchantid, String orderid, String amount, String secret_key, String url) throws IOException, ParserConfigurationException, XPathExpressionException, SAXException
{
	String[] returnArray = new String[6];
	
	String checksumString = "'"+merchantid+"''"+orderid+"'";
	
	String checksum = "";
	
	try {
		checksum = calculateChecksum(secret_key,checksumString);
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	URL obj = null;
	
	obj = new URL(url);
	
	HttpsURLConnection con = null;

	con = (HttpsURLConnection) obj.openConnection();
	
	try {
		con.setRequestMethod("POST");
	} catch (ProtocolException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

	String urlParameters = "mid=" + merchantid + "&orderid="+orderid+"&checksum="+checksum+"&ver=2";
	
	con.setDoOutput(true);
	DataOutputStream wr = null;
	try {
		wr = new DataOutputStream(con.getOutputStream());
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	wr.writeBytes(urlParameters);
	
	wr.flush();
	wr.close();

	int responseCode = con.getResponseCode();
	
	BufferedReader in = new BufferedReader(
			new InputStreamReader(con.getInputStream()));
	String inputLine;
	StringBuffer response = new StringBuffer();

	while ((inputLine = in.readLine()) != null) {
		response.append(inputLine);
	}
	
	in.close();
	
	String responsestring = new String(response);
	
	InputSource source = new InputSource(new StringReader(responsestring));

	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	DocumentBuilder db = dbf.newDocumentBuilder();
	Document document = db.parse(source);

	XPathFactory xpathFactory = XPathFactory.newInstance();
	XPath xpath = xpathFactory.newXPath();

	String receivedstatuscode = xpath.evaluate("/wallet/statuscode", document);
	String receivedstatusmessage = xpath.evaluate("/wallet/statusmessage", document);
	String receivedorderid = xpath.evaluate("/wallet/orderid", document);
	String receivedrefid = xpath.evaluate("/wallet/refid", document);
	String receivedamount = xpath.evaluate("/wallet/amount", document);
	String receivedordertype = xpath.evaluate("/wallet/ordertype", document);
	String receivedchecksum = xpath.evaluate("/wallet/checksum", document);
	
	String checksumString2 = "'" + receivedstatuscode + "''" + receivedorderid + "''" + receivedrefid + "''" + receivedamount + "''" + receivedstatusmessage + "''" + receivedordertype + "'";
	
	String checksum2 = "";
	
	try {
		checksum2 = calculateChecksum(secret_key,checksumString2);
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	Map<String,String> returnMap = new HashMap<String,String>();
	
	if ((checksum2.equals(receivedchecksum)) && (orderid.equals(receivedorderid)) && (Double.parseDouble(amount) == Double.parseDouble(receivedamount)))
	{
		returnMap.put("statuscode", receivedstatuscode);
		returnMap.put("orderid", receivedorderid);
		returnMap.put("refid", receivedrefid);
		returnMap.put("amount", receivedamount);
		returnMap.put("statusmessage", receivedstatusmessage);
		returnMap.put("ordertype", receivedordertype);
		returnMap.put("checksum", receivedchecksum);
		returnMap.put("flag", "true");
	}
	else
	{
		returnMap.put("flag", "false");
	}
	return returnMap;
}
}