package org.setareh.wadl.codegen.module.ios;

import org.setareh.wadl.codegen.model.*;
import org.setareh.wadl.codegen.module.AbstractClientModule;
import org.setareh.wadl.codegen.module.ModuleException;
import org.setareh.wadl.codegen.module.ModuleName;
import org.setareh.wadl.codegen.utils.ClassNameUtil;
import freemarker.template.SimpleHash;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IOSClientModule extends AbstractClientModule {

    private static final String SOURCE_FOLDER = "";
    private final static  String GENERATED_PREFIX = "Generated";

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
    private URL clientSubClassImplTemplate;
    private URL clientSubClassIntTemplate;
    private URL parentEnumDeclarationTemplate;
    private URL parentEnumDefinitionTemplate;

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
        clientSubClassImplTemplate = this.getTemplateURL("client-subclass-implementation.ftl");
        clientSubClassIntTemplate = this.getTemplateURL("client-subclass-interface.ftl");
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
	}

	@Override
	public Set<FileInfo> generate(CGModel cgModel, CGConfig config)
			throws ModuleException {
		// freemarker datamodel
		SimpleHash fmModel = this.getFreemarkerModel();
		
		// container for target codes
		Set<FileInfo> targetFileSet = new HashSet<FileInfo>();
		
		info("Generating the Pico client classes...");
		
		if (config.prefix == null) {
			warn("No prefix is provided, it's recommended to add prefix to avoid possible conflict");
		}

        final String projectPrefix = config.prefix;
		addPrefixTypeForClassInfo(cgModel, projectPrefix);
        addPrefixTypeForEnumInfo(cgModel, GENERATED_PREFIX);

        fmModel.put("generatedPrefix",GENERATED_PREFIX);
        fmModel.put("projectPrefix",projectPrefix);

		// generate classes
		info("Generating classes ...");
		for(ClassInfo classInfo : cgModel.getClasses()) {
			this.convertFieldsType(classInfo);
			fmModel.put("superClassImports", this.getSuperClassImports(classInfo, GENERATED_PREFIX));
			fmModel.put("fieldClassImports", this.getFieldImports(classInfo, projectPrefix, GENERATED_PREFIX));
			fmModel.put("clazz", classInfo);

			FileInfo classIntf = this.generateFile(clientClassIntTemplate, fmModel, GENERATED_PREFIX + classInfo.getName(), "h", "Generated", SOURCE_FOLDER);
			targetFileSet.add(classIntf);
			FileInfo classImpl = this.generateFile(clientClassImplementationTemplate, fmModel, GENERATED_PREFIX + classInfo.getName(), "m", "Generated", SOURCE_FOLDER);
			targetFileSet.add(classImpl);
            FileInfo clientSubClassInt = this.generateFile(clientSubClassIntTemplate, fmModel, projectPrefix + classInfo.getName(), "h", "Classes", SOURCE_FOLDER);
            targetFileSet.add(clientSubClassInt);
            FileInfo clientSubClassImpl = this.generateFile(clientSubClassImplTemplate, fmModel, projectPrefix + classInfo.getName(), "m", "Classes", SOURCE_FOLDER);
            targetFileSet.add(clientSubClassImpl);
		}
		
		// generate enums
		info("Generating enums ...");
		for(EnumInfo enumInfo : cgModel.getEnums()) {
			fmModel.put("enum", enumInfo);

			FileInfo enumDec = this.generateFile(enumDeclarationTemplate, fmModel, GENERATED_PREFIX + enumInfo.getName(), "h", "Generated", SOURCE_FOLDER);
			targetFileSet.add(enumDec);
			FileInfo enumDef = this.generateFile(enumDefinitionTemplate, fmModel, GENERATED_PREFIX + enumInfo.getName(), "m", "Generated", SOURCE_FOLDER);
			targetFileSet.add(enumDef);
		}

        FileInfo clientDateImpl = this.generateFile(clientDateImplTemplate, fmModel, GENERATED_PREFIX + "DateFormatterUtils", "m", "Generated", SOURCE_FOLDER);
        targetFileSet.add(clientDateImpl);
        FileInfo clientDateInt = this.generateFile(clientDateIntTemplate, fmModel, GENERATED_PREFIX + "DateFormatterUtils", "h", "Generated", SOURCE_FOLDER);
        targetFileSet.add(clientDateInt);

        FileInfo clientFileImpl = this.generateFile(clientFileImplTemplate, fmModel, GENERATED_PREFIX + "File", "m", "Generated", SOURCE_FOLDER);
        targetFileSet.add(clientFileImpl);
        FileInfo clientFileInt = this.generateFile(clientFileIntTemplate, fmModel, GENERATED_PREFIX + "File", "h", "Generated", SOURCE_FOLDER);
        targetFileSet.add(clientFileInt);

        FileInfo clientObjectImpl = this.generateFile(clientObjectImplTemplate, fmModel, GENERATED_PREFIX + "Object", "m", "Generated", SOURCE_FOLDER);
        targetFileSet.add(clientObjectImpl);
        FileInfo clientObjectInt = this.generateFile(clientObjectIntTemplate, fmModel, GENERATED_PREFIX + "Object", "h", "Generated", SOURCE_FOLDER);
        targetFileSet.add(clientObjectInt);

        FileInfo parentEnumImpl = this.generateFile(parentEnumDeclarationTemplate, fmModel, GENERATED_PREFIX + "Enum", "m", "Generated", SOURCE_FOLDER);
        targetFileSet.add(parentEnumImpl);
        FileInfo parentEnumInt = this.generateFile(parentEnumDefinitionTemplate, fmModel, GENERATED_PREFIX + "Enum", "h", "Generated", SOURCE_FOLDER);
        targetFileSet.add(parentEnumInt);

		return targetFileSet;
	}

    @Override
    public Set<FileInfo> generate(CGServices cgServices, CGConfig cgConfig) throws ModuleException {
        // freemarker datamodel
        SimpleHash fmModel = this.getFreemarkerModel();
        final String projectPrefix = cgConfig.prefix;

        fmModel.put("generatedPrefix",GENERATED_PREFIX);
        fmModel.put("projectPrefix",projectPrefix);

        // container for target codes
        Set<FileInfo> targetFileSet = new HashSet<FileInfo>();

        FileInfo clientObjectImpl = this.generateFile(clientServicesApiClientImplTemplate, fmModel, GENERATED_PREFIX + "ApiClient", "m", "Services", SOURCE_FOLDER);
        targetFileSet.add(clientObjectImpl);
        FileInfo clientObjectInt = this.generateFile(clientServicesApiClientIntTemplate, fmModel, GENERATED_PREFIX + "ApiClient", "h", "Services", SOURCE_FOLDER);
        targetFileSet.add(clientObjectInt);

        for(CGService cgService : cgServices.getServices())
        {
            fmModel.put("imports", getImports(cgService.getMethods(), projectPrefix));
            fmModel.put("className", GENERATED_PREFIX + cgService.getName());
            fmModel.put("methods", cgService.getMethods());
            FileInfo clientServicesApiServiceImpl = this.generateFile(clientServicesApiServiceImplTemplate, fmModel, GENERATED_PREFIX + cgService.getName() + "Api", "m", "Services", SOURCE_FOLDER);
            targetFileSet.add(clientServicesApiServiceImpl);
            FileInfo clientServicesApiServiceInt = this.generateFile(clientServicesApiServiceIntTemplate, fmModel, GENERATED_PREFIX + cgService.getName() + "Api", "h", "Services", SOURCE_FOLDER);
            targetFileSet.add(clientServicesApiServiceInt);
        }

        return targetFileSet;
    }

    @Override
    public Set<FileInfo> generateProjectModel(CGConfig cgConfig) throws ModuleException {
        // DO NOTHING
        return new HashSet<FileInfo>();
    }

    private Set<String> getImports(List<CGMethod> methods, String prefix) {
        Set<String> importList = new HashSet<String>();
        for(CGMethod method : methods)
        {
            importList.add(prefix + method.getRequest().getName() + ".h");
            importList.add(prefix + method.getResponse().getName() + ".h");
        }

        return importList;
    }

    private void addPrefixTypeForEnumInfo(CGModel model, String prefix)
    {
        // add prefix on enum
        for(EnumInfo enumInfo : model.getEnums()) {
            enumInfo.setName(enumInfo.getName());
            String newFullName = enumInfo.getPackageName() + "." + enumInfo.getName();
            enumInfo.setFullName(newFullName);
        }
    }

    // add prefix to avoid possible conflict
	private void addPrefixTypeForClassInfo(CGModel model, String prefix) {
		// add prefix on class
		for(ClassInfo classInfo : model.getClasses()) {
			String newFullName = classInfo.getPackageName() + "." + classInfo.getName();
			classInfo.setFullName(newFullName);
        }
		
		for(ClassInfo classInfo : model.getClasses()) {
			// update super class reference
			if (classInfo.getSuperClass() != null) {
				TypeInfo superType = classInfo.getSuperClass();
				this.prefixType(superType, prefix);
			}
			
			// update field reference
			for(FieldInfo field : classInfo.getFields()) {
				TypeInfo fieldType = field.getType();
				this.prefixType(fieldType, prefix);
				
				if (fieldType.isArray()) {
					this.prefixType(fieldType.getElementType(), prefix);
				}
				
				// convert type parameters
				for(TypeInfo paraType : fieldType.getTypeParameters()) {
					this.prefixType(paraType, prefix);
				}
			}
		}
	}
	
	
	// add prefix in the type full name
	private void prefixType(TypeInfo type, String prefix) {
		if (type == null) return; // be cautious
		// for ios primitives, do not prefix
		if (Java2PicoTypeMapper.lookupPicoType(type.getFullName()) != null) {
			return;
		}
		String name = type.getName();
		type.setName(name);
		type.setFullName(name); // remove package for ios
	}
	
	private Set<String> getSuperClassImports(ClassInfo clazz, String generatedPrefix) {
		Set<String> imports = new HashSet<String>();
		
		// extends super class?
		if (clazz.getSuperClass() != null) {
			TypeInfo superClassType = clazz.getSuperClass();
			imports.add(generatedPrefix + superClassType.getFullName());
		}
		
		return imports;
	}
	
	private Set<String> getFieldImports(ClassInfo clazz, String projectPrefix, String generatedPrefix) {
		Set<String> imports = new HashSet<String>();
		
		for(FieldInfo field : clazz.getFields()) {
			TypeInfo fieldType = field.getType();
			if (fieldType.isArray()) {
				TypeInfo elementType = fieldType.getElementType();
                if (elementType != null && elementType.isEnum()) {
                    imports.add(generatedPrefix + elementType.getFullName());
                }
                else if(elementType != null && !elementType.isPrimitive()) {
					imports.add(projectPrefix + elementType.getFullName());
				}
			} else {
                if(fieldType.isEnum())
                {
                    imports.add(generatedPrefix + fieldType.getFullName());
                }
                else if (!fieldType.isPrimitive() && !fieldType.isCollection()) {
					imports.add(projectPrefix + fieldType.getFullName());
				}
			}
			// import type parameters
			for(TypeInfo paraType : fieldType.getTypeParameters()) { // object type
				if(paraType.isEnum())
                {
                    imports.add(generatedPrefix + paraType.getFullName());
                }
                else if (!paraType.isPrimitive() && !field.isAny()) {
					imports.add(projectPrefix + paraType.getFullName());
				}
			}
		}
		
		return imports;
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
			for(TypeInfo paraType : fieldType.getTypeParameters()) {
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
		String picoPrimitiveType = Java2PicoTypeMapper.lookupPicoType(type.getFullName());
		if (picoPrimitiveType != null)  {// ios primitive type
			type.setFullName(PicoTypeMapper.lookupWrapper(picoPrimitiveType));
			type.setName(picoPrimitiveType); // ios primitive
			type.setPrimitive(true);
		} else if (type.isEnum()) {
			type.setName(PicoType.ENUM); // ios enum type
			type.setPrimitive(true); // treat enum as primitive type
		} else {
			type.setName(PicoType.OBJECT);
			type.setPrimitive(false);
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
