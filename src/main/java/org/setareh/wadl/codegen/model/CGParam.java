package org.setareh.wadl.codegen.model;

/**
 * @author: alexandre_godet
 * @since: MXXX
 */
public class CGParam {
    private String name;
    private ClassInfo classInfo;

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
}
