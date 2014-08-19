package org.setareh.wadl.codegen.module.ios;

import java.util.HashMap;
import java.util.Map;

/**
 * Objective-c primitive to wrapper type mapping
 * 
 * @author bulldog
 *
 */
public class TypeMapper {
	
	private static Map<String, OCWrapper> mapping;
	
	static {
		initMapping();
	}
	
	private static void initMapping() {
		mapping = new HashMap<String, OCWrapper>();
		
		mapping.put(Type.INTEGER, OCWrapper.NSINTEGER);
		mapping.put(Type.BOOL, OCWrapper.BOOL);
		mapping.put(Type.BYTE, OCWrapper.NSNUMBER);
		mapping.put(Type.CHAR, OCWrapper.NSSTRING);
		mapping.put(Type.SHORT, OCWrapper.NSNUMBER);
		mapping.put(Type.LONG, OCWrapper.LONG);
		mapping.put(Type.FLOAT, OCWrapper.FLOAT);
		mapping.put(Type.DOUBLE, OCWrapper.DOUBLE);
		mapping.put(Type.ENUM, OCWrapper.NSSTRING);
		mapping.put(Type.DATE, OCWrapper.NSDATE);
		mapping.put(Type.DURATION, OCWrapper.NSSTRING);
		mapping.put(Type.STRING, OCWrapper.NSSTRING);
		mapping.put(Type.DATA, OCWrapper.NSDATA);
		mapping.put(Type.QNAME, OCWrapper.NSSTRING);
		mapping.put(Type.ANYELEMENT, OCWrapper.ID);
	}
	
	/**
	 * Given an objective-c primitive type, return its wrapper type
	 */
	public static OCWrapper lookupWrapper(String primType) {
		return mapping.get(primType);
	}

}
