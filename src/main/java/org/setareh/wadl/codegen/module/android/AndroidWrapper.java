package org.setareh.wadl.codegen.module.android;

import org.setareh.wadl.codegen.module.Qualifier;
import org.setareh.wadl.codegen.module.Wrapper;

/**
 * Objective-c primitive wrapper types
 * 
 * @author bulldog
 *
 */
public enum AndroidWrapper implements Wrapper {

    DATE("java.util.Date"),
    DATETIME("org.joda.time.DateTime");

    private final String type;

    AndroidWrapper(String type)
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
