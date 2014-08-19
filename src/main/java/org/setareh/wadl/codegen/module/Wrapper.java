package org.setareh.wadl.codegen.module;

import org.setareh.wadl.codegen.module.ios.OCQualifier;

/**
 * @author: alexandre_godet
 * @since: MXXX
 */
public interface Wrapper {

    public String getType();

    public Qualifier getQualifier();

    public boolean isPointer();
}
