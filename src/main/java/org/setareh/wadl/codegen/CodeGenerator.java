package org.setareh.wadl.codegen;

import org.setareh.wadl.codegen.model.CGConfig;
import org.setareh.wadl.codegen.model.CGModel;
import org.setareh.wadl.codegen.model.CGServices;
import org.setareh.wadl.codegen.model.FileInfo;
import org.setareh.wadl.codegen.module.ClientModule;
import org.setareh.wadl.codegen.module.ModuleException;
import org.setareh.wadl.codegen.writer.FileCodeWriter;
import org.setareh.wadl.codegen.writer.ICodeWriter;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * @author: alexandre_godet
 * @since: MXXX
 */
public class CodeGenerator {

    public static void generateModel(CGModel cgModel, CGConfig cgConfig){
        ClientModule clientModule = cgConfig.module.getClientModule();
        try {
            clientModule.init();
        } catch (ModuleException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        Set<FileInfo> files = null;
        try {
            files = clientModule.generate(cgModel, cgConfig);
        } catch (ModuleException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        // then print them out
        try {
            File sourceDir = new File(cgConfig.targetDir);
            if(!sourceDir.exists())
            {
                sourceDir.mkdirs();
            }
            ICodeWriter cw = new FileCodeWriter(sourceDir, false);
            CodeBuilder.build(files, cw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateServices(CGServices cgServices, CGConfig cgConfig) {

        ClientModule clientModule = cgConfig.module.getClientModule();
        try {
            clientModule.init();
        } catch (ModuleException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        Set<FileInfo> files = null;
        try {
            files = clientModule.generate(cgServices, cgConfig);
        } catch (ModuleException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        // then print them out
        try {
            File sourceDir = new File(cgConfig.targetDir);
            if(!sourceDir.exists())
            {
                sourceDir.mkdirs();
            }
            ICodeWriter cw = new FileCodeWriter(sourceDir, false);
            CodeBuilder.build(files, cw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateProjectModel(CGConfig cgConfig) {
        ClientModule clientModule = cgConfig.module.getClientModule();
        try {
            clientModule.init();
        } catch (ModuleException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        Set<FileInfo> files = null;
        try {
            files = clientModule.generateProjectModel(cgConfig);
        } catch (ModuleException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        // then print them out
        try {
            ICodeWriter cw = new FileCodeWriter(new File(cgConfig.targetDir), false);
            CodeBuilder.build(files, cw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
