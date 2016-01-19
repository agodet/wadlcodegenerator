package org.setareh.wadl.codegen.module.typescript;

import freemarker.template.SimpleHash;
import org.setareh.wadl.codegen.model.*;
import org.setareh.wadl.codegen.module.*;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author: alexandre_godet
 * @since: MXXX
 */
public class TypeScriptModule extends AbstractClientModule {

    private URL classTemplate;
    private URL enumTemplate;
    private final String EXTENSION = "ts";

    @Override
    protected Set<String> getReservedWords() {
        Set<String> reservedWord = new HashSet<String>();
        reservedWord.add("break");
        reservedWord.add("case");
        reservedWord.add("catch");
        reservedWord.add("class");
        reservedWord.add("const");
        reservedWord.add("continue");
        reservedWord.add("debugger");
        reservedWord.add("default");
        reservedWord.add("delete");
        reservedWord.add("do");
        reservedWord.add("else");
        reservedWord.add("enum");
        reservedWord.add("export");
        reservedWord.add("extends");
        reservedWord.add("finally");
        reservedWord.add("for");
        reservedWord.add("function");
        reservedWord.add("if");
        reservedWord.add("import");
        reservedWord.add("in");
        reservedWord.add("instanceof");
        reservedWord.add("null");
        reservedWord.add("return");
        reservedWord.add("new");
        reservedWord.add("switch");
        reservedWord.add("this");
        reservedWord.add("throw");
        reservedWord.add("true");
        reservedWord.add("try");
        reservedWord.add("super");
        reservedWord.add("typeof");
        reservedWord.add("var");
        reservedWord.add("void");
        reservedWord.add("while");
        reservedWord.add("with");
        return reservedWord;
    }

    @Override
    protected Map<String, Wrapper> getWrappers() {
        Map<String, Wrapper> wrappers = new HashMap<String, Wrapper>();
        wrappers.put(Type.BOOL, TypeScriptWrapper.BOOL);
        wrappers.put(Type.CHAR, TypeScriptWrapper.STRING);
        wrappers.put(Type.INTEGER, TypeScriptWrapper.NUMBER);
        wrappers.put(Type.LONG, TypeScriptWrapper.NUMBER);
        wrappers.put(Type.FLOAT, TypeScriptWrapper.FLOAT);
        wrappers.put(Type.DOUBLE, TypeScriptWrapper.DOUBLE);
        wrappers.put(Type.ENUM, TypeScriptWrapper.STRING);
        wrappers.put(Type.DATE, TypeScriptWrapper.DATE);
        wrappers.put(Type.DURATION, TypeScriptWrapper.STRING);
        wrappers.put(Type.STRING, TypeScriptWrapper.STRING);
        wrappers.put(Type.OBJECT, TypeScriptWrapper.OBJECT);
        return wrappers;
    }

    @Override
    public ModuleName getName() {
        return ModuleName.TYPESCRIPT;
    }

    @Override
    public void init() throws ModuleException {
        loadTemplates();
    }

    private void loadTemplates() throws ModuleException {
        classTemplate = getTemplateURL("client-class-type.ftl");
        enumTemplate = getTemplateURL("client-enum-type.ftl");
    }

    @Override
    public Set<FileInfo> generate(CGModel cgModel, CGConfig config) throws ModuleException {
        // freemarker datamodel
        SimpleHash fmModel = this.getFreemarkerModel();

        // container for target codes
        Set<FileInfo> targetFileSet = new HashSet<FileInfo>();

        fmModel.put("namespace", config.packageName);

        // generate classes
        //info("Generating classes ...");
        for (ClassInfo classInfo : cgModel.getClasses()) {
            this.convertFieldsType(classInfo);
            fmModel.put("clazz", classInfo);
            FileInfo classFile = this.generateFile(classTemplate, fmModel, classInfo.getName(), EXTENSION, "", "");
            targetFileSet.add(classFile);
        }

        // generate enums
        for (EnumInfo enumInfo : cgModel.getEnums()) {
            fmModel.put("enum", enumInfo);
            FileInfo classFile = this.generateFile(enumTemplate, fmModel, enumInfo.getName(), EXTENSION, "", "");
            targetFileSet.add(classFile);
        }

        return targetFileSet;
    }

    @Override
    public Set<FileInfo> generate(CGServices cgServices, CGConfig cgConfig) throws ModuleException {
        Set<FileInfo> targetFileSet = new HashSet<>();
        return targetFileSet;
    }

    @Override
    public Set<FileInfo> generateProjectModel(CGConfig cgConfig) throws ModuleException {
        return new HashSet<FileInfo>();
    }

    protected void convertType(TypeInfo type) {
        if (type == null) return; // be cautious

        if (type.isEnum()) {
            type.setName(Type.STRING); // ios enum type
            type.setPrimitive(true); // treat enum as primitive type
        }

        Wrapper wrapper = null;

                String primitiveType = Java2TypeMapper.lookupType(type.getFullName());
        if (primitiveType != null) {
            if(wrappersCache == null)
            {
                wrappersCache = getWrappers();
            }
            wrapper = wrappersCache.get(primitiveType);
        } else {
            wrapper = wrappersCache.get(type.getName());
        }

        if(wrapper != null)
        {
            type.setFullName(wrapper.getType());
            type.setWrapper(wrapper);
            type.setName(wrapper.getType());
            type.setPrimitive(true);
        }
    }
}
