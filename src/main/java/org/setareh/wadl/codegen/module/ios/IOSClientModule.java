package org.setareh.wadl.codegen.module.ios;

import freemarker.template.SimpleHash;
import org.setareh.wadl.codegen.model.*;
import org.setareh.wadl.codegen.module.AbstractClientModule;
import org.setareh.wadl.codegen.module.ModuleException;
import org.setareh.wadl.codegen.module.ModuleName;
import org.setareh.wadl.codegen.module.Wrapper;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IOSClientModule extends AbstractClientModule {

    private static final String SOURCE_FOLDER = "";

    // references to templates
    private URL clientClassIntTemplate;
    private URL clientClassImplementationTemplate;
    private URL enumDeclarationTemplate;
    private URL enumDefinitionTemplate;
    private URL clientDateImplTemplate;
    private URL clientDateIntTemplate;
    private URL clientFileImplTemplate;
    private URL clientFileIntTemplate;
    private URL clientObjectImplTemplate;
    private URL clientObjectIntTemplate;
    private URL clientServicesApiClientImplTemplate;
    private URL clientServicesApiClientIntTemplate;
    private URL clientServicesApiServiceImplTemplate;
    private URL clientServicesApiServiceIntTemplate;
    private URL parentEnumDeclarationTemplate;
    private URL parentEnumDefinitionTemplate;
    private URL inputstreamInterfaceTemplate;
    private URL inputstreamImplementationTemplate;

    @Override
    public ModuleName getName() {
        return ModuleName.IOS;
    }

    @Override
    public void init() throws ModuleException {
        info("IOSClientModule loading templates ...");
        loadTemplates();
    }

    private void loadTemplates() throws ModuleException {
        //load template
        clientClassIntTemplate = this.getTemplateURL("client-class-interface.ftl");
        clientClassImplementationTemplate = this.getTemplateURL("client-class-implementation.ftl");
        enumDeclarationTemplate = this.getTemplateURL("client-enum-declaration.ftl");
        enumDefinitionTemplate = this.getTemplateURL("client-enum-definition.ftl");
        parentEnumDeclarationTemplate = this.getTemplateURL("client-enum-parent-implementation.ftl");
        parentEnumDefinitionTemplate = this.getTemplateURL("client-enum-parent-interface.ftl");
        clientDateImplTemplate = this.getTemplateURL("client-date-implementation.ftl");
        clientDateIntTemplate = this.getTemplateURL("client-date-interface.ftl");
        clientFileImplTemplate = this.getTemplateURL("client-file-implementation.ftl");
        clientFileIntTemplate = this.getTemplateURL("client-file-interface.ftl");
        clientObjectImplTemplate = this.getTemplateURL("client-object-implementation.ftl");
        clientObjectIntTemplate = this.getTemplateURL("client-object-interface.ftl");
        clientServicesApiClientImplTemplate = this.getTemplateURL("client-services-api-client-implementation.ftl");
        clientServicesApiClientIntTemplate = this.getTemplateURL("client-services-api-client-interface.ftl");
        clientServicesApiServiceImplTemplate = this.getTemplateURL("client-services-api-service-implementation.ftl");
        clientServicesApiServiceIntTemplate = this.getTemplateURL("client-services-api-service-interface.ftl");
        inputstreamInterfaceTemplate = this.getTemplateURL("inputstream-interface.ftl");
        inputstreamImplementationTemplate = this.getTemplateURL("inputstream-implementation.ftl");
    }

    @Override
    public Set<FileInfo> generate(CGModel cgModel, CGConfig config)
            throws ModuleException {
        // freemarker datamodel
        SimpleHash fmModel = this.getFreemarkerModel();

        // container for target codes
        Set<FileInfo> targetFileSet = new HashSet<FileInfo>();

        info("Generating the client classes...");

        if (config.prefix == null) {
            warn("No prefix is provided, it's recommended to add prefix to avoid possible conflict");
        }

        final String projectPrefix = config.prefix;
        addPrefixTypeForClassInfo(cgModel, projectPrefix);
        addPrefixTypeForEnumInfo(cgModel, projectPrefix);

        fmModel.put("projectPrefix", projectPrefix);

        // generate classes
        info("Generating classes ...");
        for (ClassInfo classInfo : cgModel.getClasses()) {
            this.convertFieldsType(classInfo);
            this.convertFieldsValue(classInfo);
            fmModel.put("superClassImports", this.getSuperClassImports(classInfo, projectPrefix));
            fmModel.put("fieldClassImports", this.getFieldImports(classInfo, projectPrefix));
            fmModel.put("clazz", classInfo);

            FileInfo classIntf = this.generateFile(clientClassIntTemplate, fmModel, projectPrefix + classInfo.getName(), "h", "Classes", SOURCE_FOLDER);
            targetFileSet.add(classIntf);
            FileInfo classImpl = this.generateFile(clientClassImplementationTemplate, fmModel, projectPrefix + classInfo.getName(), "m", "Classes", SOURCE_FOLDER);
            targetFileSet.add(classImpl);
        }

        // generate enums
        info("Generating enums ...");
        for (EnumInfo enumInfo : cgModel.getEnums()) {
            fmModel.put("enum", enumInfo);

            FileInfo enumDec = this.generateFile(enumDeclarationTemplate, fmModel, projectPrefix + enumInfo.getName(), "h", "Classes", SOURCE_FOLDER);
            targetFileSet.add(enumDec);
            FileInfo enumDef = this.generateFile(enumDefinitionTemplate, fmModel, projectPrefix + enumInfo.getName(), "m", "Classes", SOURCE_FOLDER);
            targetFileSet.add(enumDef);
        }

        FileInfo clientDateImpl = this.generateFile(clientDateImplTemplate, fmModel, projectPrefix + "DateFormatterUtils", "m", "Classes", SOURCE_FOLDER);
        targetFileSet.add(clientDateImpl);
        FileInfo clientDateInt = this.generateFile(clientDateIntTemplate, fmModel, projectPrefix + "DateFormatterUtils", "h", "Classes", SOURCE_FOLDER);
        targetFileSet.add(clientDateInt);

        FileInfo clientFileImpl = this.generateFile(clientFileImplTemplate, fmModel, projectPrefix + "File", "m", "Classes", SOURCE_FOLDER);
        targetFileSet.add(clientFileImpl);
        FileInfo clientFileInt = this.generateFile(clientFileIntTemplate, fmModel, projectPrefix + "File", "h", "Classes", SOURCE_FOLDER);
        targetFileSet.add(clientFileInt);

        FileInfo clientObjectImpl = this.generateFile(clientObjectImplTemplate, fmModel, projectPrefix + "Object", "m", "Classes", SOURCE_FOLDER);
        targetFileSet.add(clientObjectImpl);
        FileInfo clientObjectInt = this.generateFile(clientObjectIntTemplate, fmModel, projectPrefix + "Object", "h", "Classes", SOURCE_FOLDER);
        targetFileSet.add(clientObjectInt);

        FileInfo parentEnumImpl = this.generateFile(parentEnumDeclarationTemplate, fmModel, projectPrefix + "Enum", "m", "Classes", SOURCE_FOLDER);
        targetFileSet.add(parentEnumImpl);
        FileInfo parentEnumInt = this.generateFile(parentEnumDefinitionTemplate, fmModel, projectPrefix + "Enum", "h", "Classes", SOURCE_FOLDER);
        targetFileSet.add(parentEnumInt);

        final FileInfo inputStreamImplem = this.generateFile(inputstreamImplementationTemplate, fmModel, projectPrefix + "InputStream", "m", "Classes", SOURCE_FOLDER);
        targetFileSet.add(inputStreamImplem);

        final FileInfo inputStreamInterface = this.generateFile(inputstreamInterfaceTemplate, fmModel, projectPrefix + "InputStream", "h", "Classes", SOURCE_FOLDER);
        targetFileSet.add(inputStreamInterface);

        return targetFileSet;
    }

    @Override
    public Set<FileInfo> generate(CGServices cgServices, CGConfig cgConfig) throws ModuleException {
        // freemarker datamodel
        SimpleHash fmModel = this.getFreemarkerModel();
        final String projectPrefix = cgConfig.prefix;

        fmModel.put("projectPrefix", projectPrefix);

        // container for target codes
        Set<FileInfo> targetFileSet = new HashSet<>();

        FileInfo clientObjectImpl = this.generateFile(clientServicesApiClientImplTemplate, fmModel, projectPrefix + "ApiClient", "m", "Services", SOURCE_FOLDER);
        targetFileSet.add(clientObjectImpl);
        FileInfo clientObjectInt = this.generateFile(clientServicesApiClientIntTemplate, fmModel, projectPrefix + "ApiClient", "h", "Services", SOURCE_FOLDER);
        targetFileSet.add(clientObjectInt);

        for (CGService cgService : cgServices.getServices()) {
            final List<CGMethod> methods = dissociateMethodsWithSameName(cgService.getMethods());
            fmModel.put("imports", getImports(methods, projectPrefix));
            fmModel.put("className", projectPrefix + cgService.getName());
            fmModel.put("methods", methods);
            FileInfo clientServicesApiServiceImpl = this.generateFile(clientServicesApiServiceImplTemplate, fmModel, projectPrefix + cgService.getName() + "Api", "m", "Services", SOURCE_FOLDER);
            targetFileSet.add(clientServicesApiServiceImpl);
            FileInfo clientServicesApiServiceInt = this.generateFile(clientServicesApiServiceIntTemplate, fmModel, projectPrefix + cgService.getName() + "Api", "h", "Services", SOURCE_FOLDER);
            targetFileSet.add(clientServicesApiServiceInt);
        }

        return targetFileSet;
    }

    /**
     * ObjC does not support overriding method names. We'll have to dissociate methods with their Type
     * (GET, POST, PUT, DELETE, ...)
     */
    private List<CGMethod> dissociateMethodsWithSameName(List<CGMethod> methods) {
        final HashSet<String> duplicateNames = new HashSet<>();
        final HashSet<String> names = new HashSet<>();
        for (CGMethod method : methods) {
            final String name = method.getName();
            if (!names.add(name)) {
                duplicateNames.add(name);
            }
        }
        final ArrayList<CGMethod> returnMethods = new ArrayList<CGMethod>();
        for (CGMethod method : methods) {
            if (duplicateNames.contains(method.getName())) {
                final CGMethod clone = method.clone();
                clone.setName(method.getName() + method.getType());
                returnMethods.add(clone);
            } else {
                returnMethods.add(method);
            }
        }
        return returnMethods;
    }

    @Override
    public Set<FileInfo> generateProjectModel(CGConfig cgConfig) throws ModuleException {
        // DO NOTHING
        return new HashSet<FileInfo>();
    }

    private Set<String> getImports(List<CGMethod> methods, String prefix) {
        Set<String> importList = new HashSet<String>();
        for (CGMethod method : methods) {
            if (method.getRequest() != null) {
                importList.add(prefix + method.getRequest().getName() + ".h");
            }
            if (method.getResponse() != null) {
                importList.add(prefix + method.getResponse().getName() + ".h");
            }
        }

        return importList;
    }

    private void addPrefixTypeForEnumInfo(CGModel model, String prefix) {
        // add prefix on enum
        for (EnumInfo enumInfo : model.getEnums()) {
            enumInfo.setName(enumInfo.getName());
            String newFullName = enumInfo.getPackageName() + "." + enumInfo.getName();
            enumInfo.setFullName(newFullName);
        }
    }

    // add prefix to avoid possible conflict
    private void addPrefixTypeForClassInfo(CGModel model, String prefix) {
        // add prefix on class
        for (ClassInfo classInfo : model.getClasses()) {
            String newFullName = classInfo.getPackageName() + "." + classInfo.getName();
            classInfo.setFullName(newFullName);
        }

        for (ClassInfo classInfo : model.getClasses()) {
            // update super class reference
            if (classInfo.getSuperClass() != null) {
                TypeInfo superType = classInfo.getSuperClass();
                this.prefixType(superType, prefix);
            }

            // update field reference
            for (FieldInfo field : classInfo.getFields()) {
                TypeInfo fieldType = field.getType();
                this.prefixType(fieldType, prefix);

                if (fieldType.isArray()) {
                    this.prefixType(fieldType.getElementType(), prefix);
                }

                // convert type parameters
                for (TypeInfo paraType : fieldType.getTypeParameters()) {
                    this.prefixType(paraType, prefix);
                }
            }
        }
    }


    // add prefix in the type full name
    private void prefixType(TypeInfo type, String prefix) {
        if (type == null) return; // be cautious
        // for ios primitives, do not prefix
        if (Java2TypeMapper.lookupType(type.getFullName()) != null) {
            return;
        }
        String name = type.getName();
        type.setName(name);
        type.setFullName(name); // remove package for ios
    }

    private Set<String> getSuperClassImports(ClassInfo clazz, String prefix) {
        Set<String> imports = new HashSet<String>();

        // extends super class?
        if (clazz.getSuperClass() != null) {
            TypeInfo superClassType = clazz.getSuperClass();
            imports.add(prefix + superClassType.getFullName());
        }

        return imports;
    }

    private Set<String> getFieldImports(ClassInfo clazz, String projectPrefix) {
        Set<String> imports = new HashSet<String>();

        for (FieldInfo field : clazz.getFields()) {
            TypeInfo fieldType = field.getType();
            if (fieldType.isArray()) {
                TypeInfo elementType = fieldType.getElementType();
                if (elementType != null && (!elementType.isPrimitive() || elementType.isEnum())) {
                    imports.add(projectPrefix + elementType.getFullName());
                }
            } else {
                if ((!fieldType.isPrimitive() && !fieldType.isCollection()) || fieldType.isEnum()) {
                    imports.add(projectPrefix + fieldType.getFullName());
                }
            }
            // import type parameters
            for (TypeInfo paraType : fieldType.getTypeParameters()) { // object type
                if ((!paraType.isPrimitive() && !field.isPropertyKindAny()) || paraType.isEnum()) {
                    imports.add(projectPrefix + paraType.getFullName());
                }
            }
        }

        return imports;
    }

    private void convertFieldsValue(ClassInfo clazz)
    {
        for (FieldInfo field : clazz.getFields()) {
            if(field.getValue() != null) {
                if (OCWrapper.BOOL.equals(field.getType().getWrapper())) {
                    if (Boolean.parseBoolean(field.getValue())) {
                        field.setValue("YES");
                    } else {
                        field.setValue("NO");
                    }
                } else if (OCWrapper.NSSTRING.equals(field.getType().getWrapper())) {
                    field.setValue("@\"" + field.getValue() + "\"");
                }
            }
        }
    }

    private void convertFieldsType(ClassInfo clazz) {
        for (FieldInfo field : clazz.getFields()) {
            TypeInfo fieldType = field.getType();
            convertType(fieldType);

            // TODO should element type of array be converted?
//			if (fieldType.isArray()) { 
//				convertType(fieldType.getElementType());
//			}
            // convert type parameters
            for (TypeInfo paraType : fieldType.getTypeParameters()) {
                convertType(paraType);
            }
        }
    }

    /**
     * Check and covert a type
     *
     * @param type
     */
    private void convertType(TypeInfo type) {
        if (type == null) return; // be cautious
        String primitiveType = Java2TypeMapper.lookupType(type.getFullName());
        if (primitiveType != null) {// ios primitive type
            final Wrapper wrapper = TypeMapper.lookupWrapper(primitiveType);
            type.setFullName(wrapper.getType());
            type.setName(primitiveType); // ios primitive
            type.setPrimitive(true);
            type.setWrapper(wrapper);
        } else if (type.isEnum()) {
            type.setName(Type.ENUM); // ios enum type
            type.setPrimitive(true); // treat enum as primitive type
            type.setWrapper(OCWrapper.ENUM);
        } else {
            type.setName(Type.OBJECT);
            type.setPrimitive(false);
            type.setWrapper(OCWrapper.OBJECT);
        }
    }

    @Override
    protected URL getTemplateURL(String template) throws ModuleException {
        URL url = IOSClientModule.class.getResource("template/" + template);
        if (url == null) {
            throw new ModuleException("Fail to load required template file : "
                    + template);
        }
        debug("IOSClientModule get template : " + url.toString());
        return url;
    }

    @Override
    protected Set<String> getReservedWords() {
        Set<String> reservedWord = new HashSet<String>();
        reservedWord.add("void");
        reservedWord.add("char");
        reservedWord.add("short");
        reservedWord.add("int");
        reservedWord.add("long");
        reservedWord.add("float");
        reservedWord.add("double");
        reservedWord.add("signed");
        reservedWord.add("unsigned");
        reservedWord.add("id");
        reservedWord.add("description");
        reservedWord.add("const");
        reservedWord.add("volatile");
        reservedWord.add("in");
        reservedWord.add("out");
        reservedWord.add("inout");
        reservedWord.add("bycopy");
        reservedWord.add("byref");
        reservedWord.add("oneway");
        reservedWord.add("self");
        reservedWord.add("super");
        return reservedWord;
    }


}
