package org.setareh.wadl.codegen.model.annotation;

import org.setareh.wadl.codegen.model.Annotatable;
import org.setareh.wadl.codegen.utils.StringUtil;

public class ElementAnnotation implements Annotatable {
	
	private String name = "";
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean isParameterProvided() {
		return !StringUtil.isEmpty(name);
	}

	
	public String toString() {
		String value = "";
		if (!StringUtil.isEmpty(name)) {
			value += "name = \"" + name + "\"";;
		}
		value = "Element(" + value + ")";
		return value;
	}
	
}
