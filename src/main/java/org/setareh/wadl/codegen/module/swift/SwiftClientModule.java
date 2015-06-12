package org.setareh.wadl.codegen.module.swift;

import freemarker.template.SimpleHash;
import org.setareh.wadl.codegen.model.*;
import org.setareh.wadl.codegen.module.*;
import org.setareh.wadl.codegen.module.objectivec.OCWrapper;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author: alexandre_godet
 * @since: MXXX
 */
public class SwiftClientModule extends AbstractClientModule {

    private static final String SOURCE_FOLDER = "";
    private URL enumTemplate;
    private URL classTemplate;

    @Override
    public ModuleName getName() {
        return ModuleName.SWIFT;
    }

    @Override
    public void init() throws ModuleException {
        //info("SwiftClientModule loading templates ...");
        loadTemplates();
    }

    private void loadTemplates() throws ModuleException {
        //load template
        enumTemplate = getTemplateURL("client-enum-type.ftl");
        classTemplate = getTemplateURL("client-class-type.ftl");
    }

    @Override
    public Set<FileInfo> generate(CGModel cgModel, CGConfig config) throws ModuleException {
        // freemarker datamodel
        SimpleHash fmModel = this.getFreemarkerModel();

        // container for target codes
        Set<FileInfo> targetFileSet = new HashSet<FileInfo>();

        final String projectPrefix = config.prefix;
        addPrefixTypeForClassInfo(cgModel, projectPrefix);
        addPrefixTypeForEnumInfo(cgModel, projectPrefix);

        fmModel.put("projectPrefix", projectPrefix);

        //info("Generating the client classes...");

        fmModel.put("config", config);

        // generate classes
        //info("Generating classes ...");
        for (ClassInfo classInfo : cgModel.getClasses()) {
            super.convertFieldsType(classInfo);
            fmModel.put("clazz", classInfo);
            FileInfo classFile = this.generateFile(classTemplate, fmModel, projectPrefix + classInfo.getName(), "swift", "Classes", SOURCE_FOLDER);
            targetFileSet.add(classFile);
        }

        // generate enums
        //info("Generating enums ...");
        for (EnumInfo enumInfo : cgModel.getEnums()) {
            fmModel.put("enum", enumInfo);
            FileInfo classFile = this.generateFile(enumTemplate, fmModel, projectPrefix + enumInfo.getName(), "swift", "Classes", SOURCE_FOLDER);
            targetFileSet.add(classFile);
        }

        return targetFileSet;
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

    @Override
    public Set<FileInfo> generate(CGServices cgServices, CGConfig cgConfig) throws ModuleException {
        return new HashSet<FileInfo>();
    }

    @Override
    public Set<FileInfo> generateProjectModel(CGConfig cgConfig) throws ModuleException {
        return new HashSet<FileInfo>();
    }

    @Override
    protected Set<String> getReservedWords() {
        Set<String> reservedWord = new HashSet<String>();
        reservedWord.add("class");
        reservedWord.add("deinit");
        reservedWord.add("enum");
        reservedWord.add("extension");
        reservedWord.add("func");
        reservedWord.add("import");
        reservedWord.add("init");
        reservedWord.add("let");
        reservedWord.add("protocol");
        reservedWord.add("static");
        reservedWord.add("struct");
        reservedWord.add("subscript");
        reservedWord.add("typealias");
        reservedWord.add("var");
        reservedWord.add("break");
        reservedWord.add("case");
        reservedWord.add("continue");
        reservedWord.add("default");
        reservedWord.add("do");
        reservedWord.add("else");
        reservedWord.add("fallthrough");
        reservedWord.add("if");
        reservedWord.add("in");
        reservedWord.add("for");
        reservedWord.add("return");
        reservedWord.add("switch");
        reservedWord.add("where");
        reservedWord.add("while");
        reservedWord.add("as");
        reservedWord.add("dynamicType");
        reservedWord.add("is");
        reservedWord.add("new");
        reservedWord.add("super");
        reservedWord.add("self");
        reservedWord.add("Self");
        reservedWord.add("Type");
        reservedWord.add("__COLUMN__");
        reservedWord.add("__FILE__");
        reservedWord.add("__FUNCTION__");
        reservedWord.add("and");
        reservedWord.add("__LINE__");
        reservedWord.add("associativity");
        reservedWord.add("didSet");
        reservedWord.add("get");
        reservedWord.add("infix");
        reservedWord.add("inout");
        reservedWord.add("left");
        reservedWord.add("mutating");
        reservedWord.add("none");
        reservedWord.add("nonmutating");
        reservedWord.add("operator");
        reservedWord.add("override");
        reservedWord.add("postfix");
        reservedWord.add("precedence");
        reservedWord.add("prefix");
        reservedWord.add("right");
        reservedWord.add("set");
        reservedWord.add("unowned");
        reservedWord.add("weak");
        reservedWord.add("willSet");
        reservedWord.add("_description");
        return reservedWord;
    }

    @Override
    protected Map<String, Wrapper> getWrappers() {
        Map<String, Wrapper> wrappers = new HashMap<String, Wrapper>();

        wrappers.put(Type.INTEGER, SwiftWrapper.INT);
        wrappers.put(Type.BOOL, SwiftWrapper.BOOL);
        wrappers.put(Type.BYTE, OCWrapper.NSNUMBER);
        wrappers.put(Type.CHAR, SwiftWrapper.STRING);
        wrappers.put(Type.SHORT, OCWrapper.NSNUMBER);
        wrappers.put(Type.LONG, SwiftWrapper.INT);
        wrappers.put(Type.FLOAT, SwiftWrapper.FLOAT);
        wrappers.put(Type.DOUBLE, SwiftWrapper.DOUBLE);
        wrappers.put(Type.ENUM, SwiftWrapper.STRING);
        wrappers.put(Type.DATE, SwiftWrapper.DATE);
        wrappers.put(Type.DURATION, SwiftWrapper.STRING);
        wrappers.put(Type.STRING, SwiftWrapper.STRING);
        wrappers.put(Type.DATA, OCWrapper.NSDATA);
        wrappers.put(Type.QNAME, OCWrapper.NSSTRING);
        wrappers.put(Type.ANYELEMENT, OCWrapper.ID);

        return wrappers;
    }
}
