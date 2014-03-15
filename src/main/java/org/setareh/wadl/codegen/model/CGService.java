package org.setareh.wadl.codegen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: alexandre_godet
 * @since: MXXX
 */
public class CGService {
    private String name;
    private String path;
    private List<CGMethod> methods = new ArrayList<CGMethod>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<CGMethod> getMethods() {
        return methods;
    }

    public void add(CGMethod method) {
        this.methods.add(method);
    }
}
