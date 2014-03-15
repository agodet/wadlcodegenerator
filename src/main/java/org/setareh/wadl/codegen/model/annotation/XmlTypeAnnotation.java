package org.setareh.wadl.codegen.model.annotation;

import org.setareh.wadl.codegen.model.Annotatable;
import org.setareh.wadl.codegen.utils.StringUtil;

public class XmlTypeAnnotation implements Annotatable {

	private String name = "";
	private String namespace = "";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	@Override
	public boolean isParameterProvided() {
		return !StringUtil.isEmpty(name) || !StringUtil.isEmpty(namespace);
	}
	
    /**
     * Returns a String representation of this <code>RootElement</code> annotation.
     *
     * @return a string representation.
     */
	public String toString() {
		String value = "";
		if (!StringUtil.isEmpty(name)) {
			value += "name = \"" + name + "\"";;
		}
		if (!StringUtil.isEmpty(namespace)) {
			if (value.length() != 0) {
				value += ", ";
			}
			value += "namespace = \"" + namespace + "\"";;
		}
		value = "XmlType(" + value + ")";
		return value;
	}


}
