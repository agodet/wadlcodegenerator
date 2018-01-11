package org.setareh.wadl.codegen.module.objectivec;

import freemarker.template.SimpleHash;
import org.setareh.wadl.codegen.model.*;
import org.setareh.wadl.codegen.module.*;

import java.net.URL;
import java.util.*;

public class ObjectiveCClientModule extends AbstractClientModule {

    private static final String SOURCE_FOLDER = "";

    // references to templates
    private URL clientClassIntTemplate;
    private URL clientClassImplementationTemplate;
    private URL enumDeclarationTemplate;
    private URL enumDefinitionTemplate;
    private URL clientDateImplTemplate;
    private URL clientDateIntTemplate;
    private URL clientTimeZoneDateIntTemplate;
    private URL clientTimeZoneDateImplTemplate;
    private URL clientFileImplTemplate;
    private URL clientFileIntTemplate;
    private URL clientObjectImplTemplate;
    private URL clientObjectIntTemplate;
    private URL clientRLMObjectImplTemplate;
    private URL clientRLMObjectIntTemplate;
    private URL clientServicesApiClientImplTemplate;
    private URL clientServicesApiClientIntTemplate;
    private URL clientServicesApiServiceImplTemplate;
    private URL clientServicesApiServiceIntTemplate;
    private URL parentEnumDeclarationTemplate;
    private URL parentEnumDefinitionTemplate;
    private URL parentRLMEnumIntTemplate;
    private URL parentRLMEnumImplTemplate;
    private URL inputstreamInterfaceTemplate;
    private URL inputstreamImplementationTemplate;
    private URL clientServicesRegistryManagerIntTemplate;
    private URL clientServicesRegistryManagerImplTemplate;
    private URL clientClassConstantsTemplate;

    @Override
    public ModuleName getName() {
        return ModuleName.OBJECTIVEC;
    }

    @Override
    public void init() throws ModuleException {
        //info("ObjectiveCClientModule loading templates ...");
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
        parentRLMEnumIntTemplate = this.getTemplateURL("client-rlmenum-parent-interface.ftl");
        parentRLMEnumImplTemplate = this.getTemplateURL("client-rlmenum-parent-implementation.ftl");
        clientDateImplTemplate = this.getTemplateURL("client-date-implementation.ftl");
        clientDateIntTemplate = this.getTemplateURL("client-date-interface.ftl");
        clientTimeZoneDateIntTemplate = this.getTemplateURL("client-timezone-date-interface.ftl");
        clientTimeZoneDateImplTemplate = this.getTemplateURL("client-timezone-date-implementation.ftl");
        clientFileImplTemplate = this.getTemplateURL("client-file-implementation.ftl");
        clientFileIntTemplate = this.getTemplateURL("client-file-interface.ftl");
        clientObjectImplTemplate = this.getTemplateURL("client-object-implementation.ftl");
        clientObjectIntTemplate = this.getTemplateURL("client-object-interface.ftl");
        clientRLMObjectImplTemplate = this.getTemplateURL("client-rlmobject-implementation.ftl");
        clientRLMObjectIntTemplate = this.getTemplateURL("client-rlmobject-interface.ftl");
        clientServicesApiClientImplTemplate = this.getTemplateURL("client-services-api-client-implementation.ftl");
        clientServicesApiClientIntTemplate = this.getTemplateURL("client-services-api-client-interface.ftl");
        clientServicesApiServiceImplTemplate = this.getTemplateURL("client-services-api-service-implementation.ftl");
        clientServicesApiServiceIntTemplate = this.getTemplateURL("client-services-api-service-interface.ftl");
        clientServicesRegistryManagerIntTemplate = this.getTemplateURL("client-services-registry-manager-interface.ftl");
        clientServicesRegistryManagerImplTemplate = this.getTemplateURL("client-services-registry-manager-implementation.ftl");
        inputstreamInterfaceTemplate = this.getTemplateURL("inputstream-interface.ftl");
        inputstreamImplementationTemplate = this.getTemplateURL("inputstream-implementation.ftl");
        clientClassConstantsTemplate = this.getTemplateURL("client-constants-interface.ftl");
    }

    @Override
    public Set<FileInfo> generate(CGModel cgModel, CGConfig config)
            throws ModuleException {
        // freemarker datamodel
        SimpleHash fmModel = this.getFreemarkerModel();

        // container for target codes
        Set<FileInfo> targetFileSet = new HashSet<FileInfo>();

        //info("Generating the client classes...");

        if (config.prefix == null) {
            //warn("No prefix is provided, it's recommended to add prefix to avoid possible conflict");
        }

        final String projectPrefix = config.prefix;
        addPrefixTypeForClassInfo(cgModel, projectPrefix);
        addPrefixTypeForEnumInfo(cgModel, projectPrefix);

        fmModel.put("projectPrefix", projectPrefix);

        // generate classes
        //info("Generating classes ...");
        for (ClassInfo classInfo : cgModel.getClasses()) {
            super.convertFieldsType(classInfo);
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
        //info("Generating enums ...");
        for (EnumInfo enumInfo : cgModel.getEnums()) {
            fmModel.put("enum", enumInfo);

            FileInfo enumDec = this.generateFile(enumDeclarationTemplate, fmModel, projectPrefix + enumInfo.getName(), "h", "Classes", SOURCE_FOLDER);
            targetFileSet.add(enumDec);
            FileInfo enumDef = this.generateFile(enumDefinitionTemplate, fmModel, projectPrefix + enumInfo.getName(), "m", "Classes", SOURCE_FOLDER);
            targetFileSet.add(enumDef);
        }

        FileInfo clientServiceRegistryManagerImpl = this.generateFile(clientServicesRegistryManagerImplTemplate, fmModel, projectPrefix + "RegistryManager", "m", "Classes", SOURCE_FOLDER);
        targetFileSet.add(clientServiceRegistryManagerImpl);
        FileInfo clientServiceRegistryManagerInt = this.generateFile(clientServicesRegistryManagerIntTemplate, fmModel, projectPrefix + "RegistryManager", "h", "Classes", SOURCE_FOLDER);
        targetFileSet.add(clientServiceRegistryManagerInt);

        FileInfo clientDateImpl = this.generateFile(clientDateImplTemplate, fmModel, projectPrefix + "DateFormatterUtils", "m", "Classes", SOURCE_FOLDER);
        targetFileSet.add(clientDateImpl);
        FileInfo clientDateInt = this.generateFile(clientDateIntTemplate, fmModel, projectPrefix + "DateFormatterUtils", "h", "Classes", SOURCE_FOLDER);
        targetFileSet.add(clientDateInt);

        FileInfo clientTimeZoneDateImpl = this.generateFile(clientTimeZoneDateImplTemplate, fmModel, projectPrefix + "TimeZoneDate", "m", "Classes", SOURCE_FOLDER);
        targetFileSet.add(clientTimeZoneDateImpl);
        FileInfo clientTimeZoneDateInt = this.generateFile(clientTimeZoneDateIntTemplate, fmModel, projectPrefix + "TimeZoneDate", "h", "Classes", SOURCE_FOLDER);
        targetFileSet.add(clientTimeZoneDateInt);

        FileInfo clientFileImpl = this.generateFile(clientFileImplTemplate, fmModel, projectPrefix + "File", "m", "Classes", SOURCE_FOLDER);
        targetFileSet.add(clientFileImpl);
        FileInfo clientFileInt = this.generateFile(clientFileIntTemplate, fmModel, projectPrefix + "File", "h", "Classes", SOURCE_FOLDER);
        targetFileSet.add(clientFileInt);

        FileInfo clientObjectImpl = this.generateFile(clientObjectImplTemplate, fmModel, projectPrefix + "Object", "m", "Classes", SOURCE_FOLDER);
        targetFileSet.add(clientObjectImpl);
        FileInfo clientObjectInt = this.generateFile(clientObjectIntTemplate, fmModel, projectPrefix + "Object", "h", "Classes", SOURCE_FOLDER);
        targetFileSet.add(clientObjectInt);

        FileInfo clientRLMObjectImpl = this.generateFile(clientRLMObjectImplTemplate, fmModel, projectPrefix + "RLMObject", "m", "Classes", SOURCE_FOLDER);
        targetFileSet.add(clientRLMObjectImpl);
        FileInfo clientRLMObjectInt = this.generateFile(clientRLMObjectIntTemplate, fmModel, projectPrefix + "RLMObject", "h", "Classes", SOURCE_FOLDER);
        targetFileSet.add(clientRLMObjectInt);

        FileInfo parentEnumImpl = this.generateFile(parentEnumDeclarationTemplate, fmModel, projectPrefix + "Enum", "m", "Classes", SOURCE_FOLDER);
        targetFileSet.add(parentEnumImpl);
        FileInfo parentEnumInt = this.generateFile(parentEnumDefinitionTemplate, fmModel, projectPrefix + "Enum", "h", "Classes", SOURCE_FOLDER);
        targetFileSet.add(parentEnumInt);

        FileInfo parentRLMEnumInt = this.generateFile(parentRLMEnumIntTemplate, fmModel, projectPrefix + "RLMEnum", "h", "Classes", SOURCE_FOLDER);
        targetFileSet.add(parentRLMEnumInt);
        FileInfo parentRLMEnumImpl = this.generateFile(parentRLMEnumImplTemplate, fmModel, projectPrefix + "RLMEnum", "m", "Classes", SOURCE_FOLDER);
        targetFileSet.add(parentRLMEnumImpl);

        final FileInfo inputStreamImplem = this.generateFile(inputstreamImplementationTemplate, fmModel, projectPrefix + "InputStream", "m", "Classes", SOURCE_FOLDER);
        targetFileSet.add(inputStreamImplem);

        final FileInfo inputStreamInterface = this.generateFile(inputstreamInterfaceTemplate, fmModel, projectPrefix + "InputStream", "h", "Classes", SOURCE_FOLDER);
        targetFileSet.add(inputStreamInterface);

        final FileInfo constantsInterface = this.generateFile(clientClassConstantsTemplate, fmModel, projectPrefix + "Constants", "h", "Classes", SOURCE_FOLDER);
        targetFileSet.add(constantsInterface);

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
            if (method.getFaults() != null){
                for(ClassInfo classInfo : method.getFaults()){
                    importList.add(prefix + classInfo.getName() + ".h");
                }
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
                ClassInfo superClass = classInfo.getSuperClass();
                this.prefixType(superClass, prefix);
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

    private void prefixType(ClassInfo superClass, String prefix) {
        if (superClass == null) return; // be cautious
        // for ios primitives, do not prefix
        if (Java2TypeMapper.lookupType(superClass.getFullName()) != null) {
            return;
        }
        String name = superClass.getName();
        superClass.setName(name);
        superClass.setFullName(name); // remove package for ios
    }

    private Set<String> getSuperClassImports(ClassInfo clazz, String prefix) {
        Set<String> imports = new HashSet<String>();

        // extends super class?
        if (clazz.getSuperClass() != null) {
            ClassInfo superClass = clazz.getSuperClass();
            imports.add(prefix + superClass.getFullName());
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
        reservedWord.add("hash");
        return reservedWord;
    }

    @Override
    protected Map<String, Wrapper> getWrappers() {
        Map<String, Wrapper> wrappers = new HashMap<String, Wrapper>();

        wrappers.put(Type.INTEGER, OCWrapper.NSNUMBER);
        wrappers.put(Type.BOOL, OCWrapper.NSNUMBER);
        wrappers.put(Type.BYTE, OCWrapper.NSNUMBER);
        wrappers.put(Type.CHAR, OCWrapper.NSSTRING);
        wrappers.put(Type.SHORT, OCWrapper.NSNUMBER);
        wrappers.put(Type.LONG, OCWrapper.NSNUMBER);
        wrappers.put(Type.FLOAT, OCWrapper.NSNUMBER);
        wrappers.put(Type.DOUBLE, OCWrapper.NSNUMBER);
        wrappers.put(Type.ENUM, OCWrapper.NSSTRING);
        wrappers.put(Type.DATE, OCWrapper.TIMEZONEDATE);
        wrappers.put(Type.DURATION, OCWrapper.NSSTRING);
        wrappers.put(Type.STRING, OCWrapper.NSSTRING);
        wrappers.put(Type.DATA, OCWrapper.NSDATA);
        wrappers.put(Type.QNAME, OCWrapper.NSSTRING);
        wrappers.put(Type.ENUM, OCWrapper.ENUM);
        wrappers.put(Type.OBJECT, OCWrapper.OBJECT);
        return wrappers;
    }


}
