package org.setareh.wadl.codegen.builder;

import org.setareh.wadl.codegen.generated.bo.*;
import org.setareh.wadl.codegen.model.*;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: alexandre_godet
 * @since: MXXX
 */
public class ServicesModelBuilder {

    public static void buildServicesModel(Application application, CGServices cgServices) {
        List<Resources> resourcesList = application.getResources();

        for (Resources resources : resourcesList) {
            List<Resource> resourceList = resources.getResource();
            for (Resource resource : resourceList) {
                cgServices.add(buildCGService(resource));
            }
        }

    }

    private static CGService buildCGService(Resource resource) {

        CGService cgService = new CGService();

        // TODO manage params
        if (resource.getParam() != null) {
            resource.getParam();
        }

        cgService.setName(createClassName(resource.getPath()));
        cgService.setPath(resource.getPath());

        List<Object> methodOrResourceList = resource.getMethodOrResource();

        if (methodOrResourceList != null && !methodOrResourceList.isEmpty()) {
            for (Object methodOrResource : methodOrResourceList) {
                if (methodOrResource instanceof Method) {
                    CGMethod cgMethod = createMethod((Method) methodOrResource, resource.getPath(), resource.getPath());
                    cgService.add(cgMethod);

                } else if (methodOrResource instanceof Resource) {
                    Resource includeResource = (Resource) methodOrResource;
                    List<Object> includeMethodOrResourceList = includeResource.getMethodOrResource();

                    // TODO manage params
                    if (includeResource.getParam() != null) {
                        List<Param> params = includeResource.getParam();
                    }

                    if (includeMethodOrResourceList != null && !includeMethodOrResourceList.isEmpty()) {
                        for (Object includeMethodOrResource : includeMethodOrResourceList) {
                            if (includeMethodOrResource instanceof Method) {
                                CGMethod cgMethod = createMethod((Method) includeMethodOrResource, includeResource.getPath(), resource.getPath() + includeResource.getPath());
                                cgService.add(cgMethod);

                            } else {
                                throw new RuntimeException("search for a method a this point");
                            }
                        }
                    }
                }
            }
        }

        return cgService;
    }

    private static CGMethod createMethod(Method method, String name, String path) {
        CGMethod cgMethod = new CGMethod();
        cgMethod.setType(method.getName());

        if (method.getRequest() != null && method.getRequest().getRepresentation() != null && !method.getRequest().getRepresentation().isEmpty()) {
            cgMethod.setRequest(createClassInfo(method.getRequest().getRepresentation().get(0).getElement()));
        }
        if (method.getRequest() != null) {
            cgMethod.setRequestParams(createParams(method.getRequest().getParam(), ParamStyle.QUERY));
            cgMethod.setTemplateParams(createParams(method.getRequest().getParam(), ParamStyle.TEMPLATE));
        }
        for (Response response : method.getResponse()) {
            if (isSuccessHttpCode(response)) {
                if (response.getRepresentation().isEmpty()) {
                    cgMethod.setResponse(null);
                } else {
                    cgMethod.setResponse(createClassInfo(response.getRepresentation().get(0).getElement()));
                }
            } else {
                cgMethod.addFault(response.getStatus().get(0), createClassInfo(response.getRepresentation().get(0).getElement()));
            }
        }
        cgMethod.setName(createMethodName(name));
        cgMethod.setPath(path);
        return cgMethod;
    }

    private static List<CGParam> createParams(List<Param> params, ParamStyle style) {
        List<CGParam> cgParams = new ArrayList<CGParam>();
        if (params != null) {
            for (Param param : params) {
                final ParamStyle paramStyle = param.getStyle() == null ? ParamStyle.QUERY : param.getStyle();
                if (paramStyle != style) {
                    continue;
                }
                final CGParam cgParam = new CGParam();
                cgParam.setName(param.getName());
                cgParam.setClassInfo(createClassInfo(param.getType()));
                cgParam.setStyle(paramStyle);
                cgParams.add(cgParam);
            }
        }

        return cgParams;
    }

    private static boolean isSuccessHttpCode(final Response response) {
        boolean isSuccessHttpCode = false;

        if (response.getStatus() == null || response.getStatus().isEmpty()) {
            isSuccessHttpCode = true;
        } else {
            if (response.getStatus().size() > 0 && (response.getStatus().get(0) / 100) == 2) {
                isSuccessHttpCode = true;
            }
        }
        return isSuccessHttpCode;
    }

    private static String createClassName(String path) {
        String className = path
                .replace("/", "")
                .replaceAll("\\{.*\\}", "");

        return Character.toUpperCase(className.charAt(0)) + className.substring(1);
    }

    private static String createMethodName(String path) {
        return path.replace("/", "")
                .replaceAll("\\{([A-Za-z0-9]*)\\}", "With$1")
                .replaceAll("\\.", "_");
    }

    private static ClassInfo createClassInfo(QName qname) {
        if (qname == null) {
            final ClassInfo classInfo = new ClassInfo();
            classInfo.setAbstract(false);
            classInfo.setName("InputStream");
            classInfo.setPackageName("java.io");
            return classInfo;
        }
        final ClassInfo classInfo = new ClassInfo();
        classInfo.setAbstract(false);
        classInfo.setPackageName(makePackage(qname));
        classInfo.setName(makeName(qname));
        return classInfo;
    }

    private static String makeName(QName qname) {
        String xmlClassName = qname.getLocalPart();
        return createClassName(xmlClassName);
    }

    private static String makePackage(QName qname) {
        String namespaceURI = qname.getNamespaceURI();
        String[] splitBySlash = namespaceURI.split("\\/");

        StringBuilder stringBuilder = new StringBuilder();

        for (String urlPart : splitBySlash) {
            if (!urlPart.startsWith("http") && !urlPart.isEmpty()) {
                if (urlPart.contains(".")) {
                    String[] splitByDot = urlPart.split("\\.");

                    for (int i = splitByDot.length - 1; i >= 0; i--) {
                        String dotPart = splitByDot[i];
                        if (!dotPart.startsWith("www")) {
                            stringBuilder.append(escapeChars(dotPart));
                            stringBuilder.append(".");
                        }
                    }
                } else {
                    stringBuilder.append(escapeChars(urlPart));
                    stringBuilder.append(".");
                }
            }
        }

        // delete last dot
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        return stringBuilder.toString();
    }

    private static String escapeChars(String string) {
        return string.replace('-', '_');
    }

}
