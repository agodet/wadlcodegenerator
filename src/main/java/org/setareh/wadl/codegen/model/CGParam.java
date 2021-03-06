package org.setareh.wadl.codegen.model;

import org.setareh.wadl.codegen.generated.bo.ParamStyle;

/**
 * @author: alexandre_godet
 * @since: MXXX
 */
public class CGParam {
    private String name;
    private ClassInfo classInfo;
    private ParamStyle style = ParamStyle.QUERY;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setClassInfo(ClassInfo classInfo) {
        this.classInfo = classInfo;
    }

    public ClassInfo getClassInfo() {
        return classInfo;
    }

    public void setStyle(ParamStyle style) {
        if (style != null) {
            this.style = style;
        } else {
            this.style = ParamStyle.QUERY;
        }
    }
}
