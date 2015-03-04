package org.setareh.wadl.codegen.module;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.setareh.wadl.codegen.model.ClassInfo;
import org.setareh.wadl.codegen.model.FieldInfo;
import org.setareh.wadl.codegen.model.TypeInfo;
import org.setareh.wadl.codegen.module.objectivec.OCWrapper;

import org.setareh.wadl.codegen.model.FileInfo;

import freemarker.template.SimpleHash;

/**
 * Common client module
 * 
 * @author bulldog
 * 
 */
public abstract class AbstractClientModule implements ClientModule {

    private Set<String> reservedWordCache;
    protected Map<String, Wrapper> wrappersCache;

	/**
	 * Get freemarker datamodel
	 * 
	 * @return a new SimpleHash instance
	 */
	protected SimpleHash getFreemarkerModel() {
		return new SimpleHash();
	}
	

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

    protected URL getTemplateURL(String template) throws ModuleException {
        URL url = this.getClass().getResource("template/" + template);
        if (url == null) {
            throw new ModuleException("Fail to load required template file : "
                    + template);
        }
        //debug(getClass().getName() + "get template : " + url.toString());
        return url;
    }

    protected void convertFieldsType(ClassInfo clazz) {
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
     * Check and convert a type
     *
     * @param type
     */
    private void convertType(TypeInfo type) {
        if (type == null) return; // be cautious
        String primitiveType = Java2TypeMapper.lookupType(type.getFullName());
        if (primitiveType != null) {// ios primitive type
            if(wrappersCache == null)
            {
                wrappersCache = getWrappers();
            }
            final Wrapper wrapper = wrappersCache.get(primitiveType);
            if(wrapper != null)
            {
                type.setFullName(wrapper.getType());
                type.setWrapper(wrapper);
                type.setName(primitiveType); // ios primitive
                type.setPrimitive(true);
            }
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

    protected abstract Set<String> getReservedWords();

    protected abstract Map<String, Wrapper> getWrappers();

}
