package org.setareh.wadl.codegen.model;

import java.util.*;

import static java.util.Collections.emptyList;

/**
 * @author: alexandre_godet
 * @since: MXXX
 */
public class CGMethod implements Cloneable {
    private ClassInfo request;
    private ClassInfo response;
    private String name;
    private String type;
    private String path;
    private HashMap<String, ClassInfo> faults = new HashMap<>();
    private List<CGParam> requestParams = emptyList();
    private List<CGParam> templateParams = emptyList();

    public ClassInfo getRequest() {
        return request;
    }

    public void setRequest(ClassInfo request) {
        this.request = request;
    }

    public ClassInfo getResponse() {
        return response;
    }

    public void setResponse(ClassInfo response) {
        this.response = response;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void addFault(long errorCode, ClassInfo fault) {
        this.faults.put(String.valueOf(errorCode), fault);
    }

    public Map<String, ClassInfo> getFaultsMap() {
        return faults;
    }
    public Collection<ClassInfo> getFaults() {
        return faults.values();
    }

    @Override
    public CGMethod clone() {
        final CGMethod cgMethod = new CGMethod();
        cgMethod.request = this.request;
        cgMethod.response = this.response;
        cgMethod.name = this.name;
        cgMethod.type = this.type;
        cgMethod.path = this.path;
        cgMethod.faults.putAll(this.faults);
        return cgMethod;
    }

    public void setRequestParams(List<CGParam> requestParams) {
        this.requestParams = requestParams;
    }

    public List<CGParam> getRequestParams() {
        return requestParams;
    }

    public void setTemplateParams(List<CGParam> templateParams) {
        this.templateParams = templateParams;
    }

    public List<CGParam> getTemplateParams() {
        return templateParams;
    }
}
