package org.setareh.wadl.codegen.module.objectivec;

import org.setareh.wadl.codegen.module.Qualifier;

/**
 * @author: alexandre_godet
 * @since: MXXX
 */
public enum OCQualifier implements Qualifier{
    ASSIGN("assign"),
    READONLY("readonly"),
    STRONG("strong");

    private final String qualifierName;

    OCQualifier(String qualifierName) {
        this.qualifierName = qualifierName;
    };

    public String getQualifierName() {
        return qualifierName;
    }
}
