package org.setareh.wadl.codegen.module.typescript;

import org.setareh.wadl.codegen.module.Qualifier;
import org.setareh.wadl.codegen.module.Wrapper;

/**
 * Objective-c primitive wrapper types
 * 
 * @author bulldog
 *
 */
public enum TypeScriptWrapper implements Wrapper {

    DATE("Date"),
    FLOAT ("number"),
    NUMBER("number"),
    DOUBLE("number"),
    STRING("string"),
    OBJECT("any"),
    BOOL("boolean");

    private final String type;

    TypeScriptWrapper(String type)
    {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public Qualifier getQualifier() {
        return null;
    }

    public boolean isPointer() {
        return false;
    }
}
