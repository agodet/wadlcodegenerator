package org.setareh.wadl.codegen.module.coffeescript;


import freemarker.template.SimpleHash;
import org.setareh.wadl.codegen.model.*;
import org.setareh.wadl.codegen.module.AbstractClientModule;
import org.setareh.wadl.codegen.module.ModuleException;
import org.setareh.wadl.codegen.module.ModuleName;
import org.setareh.wadl.codegen.utils.ClassNameUtil;

import java.net.URL;
import java.util.*;


/**
 * Created by brice-coquereau on 27/04/15.
 */
public class CoffeeScriptClientModule extends AbstractClientModule {
    @Override
    public ModuleName getName() {
        return ModuleName.COFFEESCRIPT;
    }

    private static final String TEMPLATE_FOLDER = "/org/setareh/wadl/codegen/module/coffeescript.template";
    private static final String SOURCE_FOLDER = "";

    private URL classTemplate;

    @Override
    public void init() throws ModuleException {
        info("CoffeeScriptClientModule loading ...");
        classTemplate = getTemplateURL("coffee-schemas-template.ftl");
        info("CoffeeScriptClientModule loaded.");
    }

    @Override
    public Set<FileInfo> generate(CGModel cgModel, CGConfig config) throws ModuleException {
        Set<FileInfo> targetFileSet = new HashSet<>();

        SimpleHash fmModel = this.getFreemarkerModel();
        fmModel.put("config", config);

        Map<String, String> enumConstants = new HashMap<>();
        for (EnumInfo enums : cgModel.getEnums()) {
            enumConstants.put(enums.getName(), joinEnumConstants(enums.getEnumConstants()));
        }
        fmModel.put("enumConstants", enumConstants);


        for (ClassInfo classInfo : cgModel.getClasses()) {
            fmModel.put("clazz", classInfo);
        }

        fmModel.put("classes", cgModel.getClasses());

        String relativePath = ClassNameUtil.packageNameToPath("com.vsc_technologies");
        FileInfo classFile = this.generateFile(classTemplate, fmModel, "schemas", "coffee", relativePath, SOURCE_FOLDER);
        targetFileSet.add(classFile);
        return targetFileSet;
    }

    private String joinEnumConstants(List<EnumConstantInfo> enumConstants) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < enumConstants.size(); i++) {
            EnumConstantInfo enumConstantInfo = enumConstants.get(i);
            sb.append(enumConstantInfo.getName());
            if (i != enumConstants.size() - 1) {
                sb.append("|");
            }
        }
        return sb.toString();
    }

    @Override
    public Set<FileInfo> generate(CGServices cgServices, CGConfig cgConfig) throws ModuleException {
        Set<FileInfo> targetFileSet = new HashSet<>();
//        System.out.println("Services");
//        for(CGService service : cgServices.getServices()) {
//            System.out.println("Service: "+service.getName()+" / Path="+service.getPath()+" / Methods"+service.getMethods());
//        }
        return targetFileSet;
    }

    @Override
    public Set<FileInfo> generateProjectModel(CGConfig cgConfig) throws ModuleException {
        Set<FileInfo> targetFileSet = new HashSet<FileInfo>();
        System.out.println("ProjectModel");
        return targetFileSet;
    }

    @Override
    protected URL getTemplateURL(String template) throws ModuleException {
        URL url = CoffeeScriptClientModule.class.getResource(TEMPLATE_FOLDER + "/" + template);
        if (url == null) {
            throw new ModuleException("Fail to load required template file : "
                    + template);
        }
        debug("CoffeeScriptClientModule get template : " + url.toString());
        return url;
    }

    @Override
    public String generateSafeName(String name) {
        return null;
    }

    @Override
    protected Set<String> getReservedWords() {
        Set<String> reservedWords = new HashSet<String>();
        reservedWords.add("break");
        reservedWords.add("case");
        reservedWords.add("catch");
        reservedWords.add("continue");
        reservedWords.add("debugger");
        reservedWords.add("default");
        reservedWords.add("delete");
        reservedWords.add("do");
        reservedWords.add("else");
        reservedWords.add("finally");
        reservedWords.add("for");
        reservedWords.add("function");
        reservedWords.add("if");
        reservedWords.add("in");
        reservedWords.add("instanceof");
        reservedWords.add("new");
        reservedWords.add("return");
        reservedWords.add("switch");
        reservedWords.add("this");
        reservedWords.add("throw");
        reservedWords.add("try");
        reservedWords.add("typeof");
        reservedWords.add("var");
        reservedWords.add("void");
        reservedWords.add("while");
        reservedWords.add("with");
        reservedWords.add("class");
        reservedWords.add("enum");
        reservedWords.add("export");
        reservedWords.add("extends");
        reservedWords.add("import");
        reservedWords.add("super");
        reservedWords.add("implements");
        reservedWords.add("interface");
        reservedWords.add("let");
        reservedWords.add("package");
        reservedWords.add("private");
        reservedWords.add("protected");
        reservedWords.add("public");
        reservedWords.add("static");
        reservedWords.add("yield");
        return reservedWords;
    }
}
