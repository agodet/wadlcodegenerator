package org.setareh.wadl.codegen.module.swift;

import org.setareh.wadl.codegen.module.Qualifier;
import org.setareh.wadl.codegen.module.Wrapper;

/**
 * Objective-c primitive wrapper types
 * 
 * @author bulldog
 *
 */
public enum SwiftWrapper implements Wrapper {

    BOOL("Bool"),
    INT("Int"),
    FLOAT ("Float"),
    DOUBLE("Double"),

    NSNUMBER("NSNumber"),
    STRING("String"),
    DATE("NSDate");

    private final String type;

    SwiftWrapper(String type)
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
