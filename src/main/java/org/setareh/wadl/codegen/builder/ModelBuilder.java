package org.setareh.wadl.codegen.builder;

import org.setareh.wadl.codegen.model.*;
import com.sun.tools.xjc.ErrorReceiver;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BindInfo;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSComponent;
import org.setareh.wadl.codegen.generated.bo.Application;

/**
 * A model builder which can build a intermediate code generation model from
 * jaxb model.
 * 
 * <p>
 * intermediate code generation model could be considered the core of the
 * code-generator architecture, it contains a set of classes that make it easy
 * to manipulate the information coming from the jaxb model to generate
 * target code.
 * 
 * @author bulldog
 *
 */
public class ModelBuilder {
	
	
	public static CGModel buildCodeGenModel(Outline outline, ErrorReceiver errorReceiver, CGConfig cgConfig) {
		
		CGModel cgModel = new CGModel();
		
		if (errorReceiver != null)
			errorReceiver.debug("Building class model ...");
		// build class/bean model
		ClassModelBuilder.buildClassModel(outline, cgModel, cgConfig, errorReceiver);
		
		if (errorReceiver != null)
			errorReceiver.debug("Building enum model ...");
		// build enum model
		EnumModelBuilder.buildEnumModel(outline, cgModel, cgConfig, errorReceiver);
		
		return cgModel;
	}

    public static CGServices buildServicesGenModel(Application application) {

        CGServices cgServices = new CGServices();

        ServicesModelBuilder.buildServicesModel(application, cgServices);

        return cgServices;
    }

    /**
	 * Helper to get xsdoc from schema component
	 * 
	 * @param xsComp
	 *            ,XSComponent
	 * @return doc string
	 */
	public static String getDocumentation(XSComponent xsComp) {
		if (xsComp == null)
			return null;
		XSAnnotation xsa = xsComp.getAnnotation();
		if (xsa != null && xsa.getAnnotation() != null) {
			String docComment = ((BindInfo) xsa.getAnnotation())
					.getDocumentation();
			return docComment;
		}
		return null;
	}
}
