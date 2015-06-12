package org.setareh.wadl.codegen.module;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Date;
import java.util.Currency;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import com.sun.xml.xsom.impl.util.Uri;

/**
 * Java to Objective-c primitive type mapper
 * 
 * @author bulldog
 *
 */
public class Java2TypeMapper {
	
	/**
	 * java to objc primitive type mapping
	 */
	private static Map<String, String> primitiveMap;
	
	/**
	 * java.lang to objc type mapping
	 */
	private static Map<String, String> languageMap;
	
	/**
	 * java.util to objc type mapping
	 */
	private static Map<String, String> utilityMap;
	
	/**
	 * javax.xml to objc type mapping
	 */
	private static Map<String, String> xmlMap;
	
	/**
	 * java.net to objc type mapping
	 */
	private static Map<String, String> urlMap;
	
	/**
	 * java.math to objc type mapping
	 */
	private static Map<String, String> mathMap;
	
	/**
	 * initialize java to objc primitive type mapping
	 */
	private static void initPrimitiveMap() {
		primitiveMap = new HashMap<String, String>();
		
		primitiveMap.put(int.class.getName(), Type.INTEGER);
		primitiveMap.put(boolean.class.getName(), Type.BOOL);
		primitiveMap.put(long.class.getName(), Type.LONG);
		primitiveMap.put(double.class.getName(), Type.DOUBLE);
		primitiveMap.put(float.class.getName(), Type.FLOAT);
		primitiveMap.put(short.class.getName(), Type.SHORT);
		primitiveMap.put(byte.class.getName(), Type.BYTE);
		primitiveMap.put(char.class.getName(), Type.CHAR); //string or int?
		primitiveMap.put("byte[]", Type.DATA);
	}

	/**
	 * initialize java.lang to objc type mapping
	 */
	private static void initLanguageMap() {
		languageMap = new HashMap<String, String>();
		
		languageMap.put(Boolean.class.getName(), Type.BOOL);
		languageMap.put(Integer.class.getName(), Type.INTEGER);
		languageMap.put(Long.class.getName(), Type.LONG);
		languageMap.put(Double.class.getName(), Type.DOUBLE);
		languageMap.put(Float.class.getName(), Type.FLOAT);
		languageMap.put(Short.class.getName(), Type.SHORT);
		languageMap.put(Byte.class.getName(), Type.BYTE);
		languageMap.put(Character.class.getName(), Type.CHAR);
		languageMap.put(String.class.getName(), Type.STRING);
	}
	
	
	/**
	 * initialize java.util to objc type mapping
	 */
	private static void initUtilityMap() {
		utilityMap = new HashMap<String, String>();
		
		utilityMap.put(Date.class.getName(), Type.DATE);
		utilityMap.put(Locale.class.getName(), Type.STRING);
		utilityMap.put(Currency.class.getName(), Type.STRING);
		utilityMap.put(GregorianCalendar.class.getName(), Type.DATE);
		utilityMap.put(TimeZone.class.getName(), Type.STRING);
        utilityMap.put(InputStream.class.getName(), Type.DATA);
	}
	
	
	/**
	 * initialize javax.xml to objc type mapping
	 */
	private static void initXMLMap() {
		xmlMap = new HashMap<String, String>();
		
		xmlMap.put(XMLGregorianCalendar.class.getName(), Type.DATE);
		xmlMap.put(Duration.class.getName(), Type.DURATION);
		xmlMap.put(QName.class.getName(), Type.QNAME);
		xmlMap.put(Object.class.getName(), Type.ANYELEMENT);
	}
	
	/**
	 * initialize java.net to objc type mapping
	 */
	private static void initUrlMap() {
		urlMap = new HashMap<String, String>();
		
		urlMap.put(URL.class.getName(), Type.STRING);
		urlMap.put(Uri.class.getName(), Type.STRING);
	}
	
	private static void initMathMap() {
		mathMap = new HashMap<String, String>();
		
		mathMap.put(BigDecimal.class.getName(), Type.DOUBLE);
		mathMap.put(BigInteger.class.getName(), Type.LONG);
	}
	
	static {
		initPrimitiveMap();
		initLanguageMap();
		initUtilityMap();
		initXMLMap();
		initUrlMap();
		initMathMap();
	}
	
	
	/**
	 * Given a java primitive type, find its mapped ios type.
	 * 
	 * @param javaType
	 * @return
	 */
	public static String lookupType(String javaType) {
		if (languageMap.containsKey(javaType)) {
			return languageMap.get(javaType);			
		}
		
		if (xmlMap.containsKey(javaType)) {
			return xmlMap.get(javaType);
		}
		
		if (utilityMap.containsKey(javaType)) {
			return utilityMap.get(javaType);
		}
		
		if (primitiveMap.containsKey(javaType)) {
			return primitiveMap.get(javaType);
		}
		
		if (urlMap.containsKey(javaType)) {
			return urlMap.get(javaType);
		}
		
		if (mathMap.containsKey(javaType)) {
			return mathMap.get(javaType);
		}
		
		return null;
	}
}
