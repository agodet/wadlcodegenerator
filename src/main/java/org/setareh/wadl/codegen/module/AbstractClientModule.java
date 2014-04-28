package org.setareh.wadl.codegen.module;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Set;

import org.setareh.wadl.codegen.model.TypeInfo;
import org.setareh.wadl.codegen.module.ios.Java2TypeMapper;
import org.setareh.wadl.codegen.module.ios.Type;
import org.setareh.wadl.codegen.module.ios.TypeMapper;
import org.xml.sax.helpers.LocatorImpl;

import org.setareh.wadl.codegen.model.FileInfo;
import com.sun.tools.xjc.ErrorReceiver;

import freemarker.template.SimpleHash;

/**
 * Common client module
 * 
 * @author bulldog
 * 
 */
public abstract class AbstractClientModule implements ClientModule {
	
	private ErrorReceiver errorReceiver;
    private Set<String> reservedWordCache;

	/**
	 * Get freemarker datamodel
	 * 
	 * @return a new SimpleHash instance
	 */
	protected SimpleHash getFreemarkerModel() {
		return new SimpleHash();
	}
	
	/**
	 * Set ErrorReceiver instance to be used for error reporting
	 */
	public void setErrorReceiver(ErrorReceiver errorReceiver) {
		this.errorReceiver = errorReceiver;
	}
	
	/**
	 * Get a template URL for the template of the given name.
	 * 
	 * @param template
	 *            The specified template.
	 * @return The URL to the specified template.
	 * @throws ModuleException
	 */
	abstract protected URL getTemplateURL(String template) throws ModuleException;
	

	/**
	 * Generate files according to specific template, datamodel and file
	 * information,
	 * 
	 * No cache for this interface
	 * 
	 *
     * @param template
     *            , template URL
     * @param fmModel
     *            , freemarker datamodel
     * @param fileName
     *            , file name
     * @param suffix
     *            , file suffix
     * @param relativePath
     *            , file relative path(such as a\b\c)
     * @param sourceFolder
     * @return FileInfo instance
	 * @throws ModuleException
	 */
	protected FileInfo generateFile(URL template, Object fmModel,
                                    String fileName, String suffix, String relativePath, String sourceFolder)
			throws ModuleException {
		byte[] context = processTemplate(template, fmModel);
		FileInfo fileInfo = new FileInfo();
		fileInfo.setName(fileName);
		fileInfo.setPath(sourceFolder + "/" + relativePath);
		fileInfo.setSuffix(suffix);
		fileInfo.setContent(context);
		return fileInfo;
	}

	/**
	 * Process given template and datamodel
	 * 
	 * @param templateURL
	 *            , template URL
	 * @param model
	 *            , freemarker datamodel
	 * @return byte array
	 * @throws ModuleException
	 */
	protected byte[] processTemplate(URL templateURL, Object model)
			throws ModuleException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			FreemarkerProcessor.getInstance().processTemplate(templateURL, model, baos);
			return baos.toByteArray();
		} catch (ModuleException e) {
			throw new ModuleException("Fail to process template "
					+ templateURL, e);
		}
	}
	
	/**
	 * information level report
	 * 
	 * @param msg
	 */
	protected void info(String msg) {
		if (this.errorReceiver != null) {
			this.errorReceiver.debug(msg);
		}
	}
	
	/**
	 * error level report
	 * 
	 * @param msg
	 */
	protected void error(String msg) {
		if (this.errorReceiver != null) {
			LocatorImpl locator = new LocatorImpl();
			locator.setLineNumber(-1);
			locator.setSystemId("module : " + this.getName().toString());
			this.errorReceiver.error(locator, msg);
		}
	}
	
	protected void warn(String msg) {
		if (this.errorReceiver != null) {
			LocatorImpl locator = new LocatorImpl();
			locator.setLineNumber(-1);
			locator.setSystemId("module : " + this.getName().toString());
			this.errorReceiver.warning(locator, msg);
		}
	}
	
	/**
	 * error level report
	 * 
	 * @param msg
	 */
	protected void debug(String msg) {
		if (this.errorReceiver != null) {
			this.errorReceiver.debug(msg);
		}
	}

    @Override
    public String generateSafeName(String name) {
        if(reservedWordCache == null)
        {
            reservedWordCache = getReservedWords();
        }

        if(reservedWordCache.contains(name))
        {
            return name + "_rw";
        }

        return name;
    }

    protected abstract Set<String> getReservedWords();

}
