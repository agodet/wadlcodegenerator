package org.setareh.wadl.codegen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: alexandre_godet
 * @since: MXXX
 */
public class CGServices {
    private List<CGService> services = new ArrayList<CGService>();

    public List<CGService> getServices() {
        return services;
    }

    public void add(CGService service) {
        this.services.add(service);
    }
}
