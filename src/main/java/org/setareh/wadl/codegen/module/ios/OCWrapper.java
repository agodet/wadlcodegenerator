package org.setareh.wadl.codegen.module.ios;

import org.setareh.wadl.codegen.module.Wrapper;

/**
 * Objective-c primitive wrapper types
 * 
 * @author bulldog
 *
 */
public enum OCWrapper implements Wrapper {

    BOOL("BOOL",OCQualifier.ASSIGN,false),
    NSINTEGER("NSInteger", OCQualifier.ASSIGN, false),
    FLOAT ("float", OCQualifier.ASSIGN, false),
    DOUBLE("double", OCQualifier.ASSIGN, false),
    LONG("long", OCQualifier.ASSIGN, false),

    NSNUMBER("NSNumber", OCQualifier.STRONG, true),
    NSSTRING("NSString", OCQualifier.STRONG, true),
    NSDATE("NSDate", OCQualifier.STRONG, true),
    NSDATA("NSData", OCQualifier.STRONG, true),
    NSINPUTSTREAM("NSInputStream", OCQualifier.STRONG, true),
	
    ID("id", OCQualifier.ASSIGN, false),

    ENUM("enum", OCQualifier.STRONG, true),

    OBJECT("object", OCQualifier.STRONG, true);

    private final String type;
    private final OCQualifier qualifier;
    private final boolean pointer;

    OCWrapper(String type, OCQualifier qualifier, boolean pointer)
    {

        this.type = type;
        this.qualifier = qualifier;
        this.pointer = pointer;
    }

    public String getType() {
        return type;
    }

    public OCQualifier getQualifier() {
        return qualifier;
    }

    public boolean isPointer() {
        return pointer;
    }
}
