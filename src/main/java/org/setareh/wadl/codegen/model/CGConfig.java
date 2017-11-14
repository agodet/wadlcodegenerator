package org.setareh.wadl.codegen.model;

import org.setareh.wadl.codegen.module.ModuleName;

/**
 * Config for code generation
 * 
 * @author bulldog
 *
 */
public class CGConfig {
	
	public String targetDir;

    public String packageName;

    public String prefix;

    public String wadlPath;

    public ModuleName module;

    public String persistantFilePath;

    public static CGConfig createCGConfigFromArgs(String[] args) {
        CGConfig cgConfig = new CGConfig();
        try{
            cgConfig.wadlPath = args[0];
            cgConfig.packageName = args[1];
            cgConfig.prefix = args[1];
            cgConfig.targetDir = args[2];
            cgConfig.module = ModuleName.valueOf(args[3]);
            cgConfig.persistantFilePath = args[4];
        }
        catch (Exception e)
        {
            throw new RuntimeException("error during getting args parameters :\n" +
                    "wadlPath\n" +
                    "package or prefix name\n" +
                    "target directory", e);
        }

        return cgConfig;
    }
}
