package org.setareh.wadl.codegen.module;

import java.util.Set;

import org.setareh.wadl.codegen.model.CGConfig;
import org.setareh.wadl.codegen.model.CGModel;
import org.setareh.wadl.codegen.model.CGServices;
import org.setareh.wadl.codegen.model.FileInfo;


/**
 * Interface for a client module.  A client module for a specific platform 
 * implements logic for code generation.
 *
 * @author bulldog
 */
public interface ClientModule {

	/**
	 * Get the name of the client module
	 * 
	 * @return The name of the client module
	 */
	public ModuleName getName();
	
	/**
	 * Initialize the module
	 * 
	 */
	public void init() throws ModuleException;
	
	
	/**
	 * Generate target code according to platform specific logic
	 * 
	 * @param context, code generation model
	 * @param config, config for code generation
	 * @return a set of generated file model
	 * @throws ModuleException
	 */
    /**
     *
     * @param cgModel
     * @param config
     * @return
     * @throws ModuleException
     */
	public Set<FileInfo> generate(CGModel cgModel, CGConfig config) throws ModuleException;

    /**
     *
     * Generate target services according to platform specific logic
     *
     * @param cgServices
     * @param cgConfig
     * @return
     */
    public Set<FileInfo> generate(CGServices cgServices, CGConfig cgConfig) throws ModuleException;

    public Set<FileInfo> generateProjectModel(CGConfig cgConfig) throws ModuleException;

    public String generateSafeName(String name);
}
