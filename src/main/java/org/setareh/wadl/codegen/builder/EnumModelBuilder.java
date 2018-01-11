package org.setareh.wadl.codegen.builder;

import com.sun.tools.xjc.outline.EnumConstantOutline;
import com.sun.tools.xjc.outline.EnumOutline;
import com.sun.tools.xjc.outline.Outline;
import org.setareh.wadl.codegen.model.CGConfig;
import org.setareh.wadl.codegen.model.CGModel;
import org.setareh.wadl.codegen.model.EnumConstantInfo;
import org.setareh.wadl.codegen.model.EnumInfo;
import org.setareh.wadl.codegen.utils.StringUtil;

import java.util.Arrays;
import java.util.List;

public class EnumModelBuilder {
    
    public static void buildEnumModel(Outline outline, CGModel cgModel, CGConfig cgConfig) {

		List<String> persistedEnumsList = null;

		try
		{
			String[] persistedEnums = Common.generatePersistentData(cgConfig.persistantFilePath);
			persistedEnumsList = Arrays.asList(persistedEnums);
		} catch(Exception e) {
			e.printStackTrace();
		}


		for (EnumOutline eo : outline.getEnums()) {
			EnumInfo enumInfo = new EnumInfo();
			enumInfo.setPackageName(eo._package()._package().name());
			enumInfo.setName(eo.clazz.name());

			if (!StringUtil.isEmpty(enumInfo.getPackageName())) {
				enumInfo.setFullName(enumInfo.getPackageName() + "." + enumInfo.getName());
			} else {
				enumInfo.setFullName(enumInfo.getName());
			}

			if (persistedEnumsList != null && persistedEnumsList.contains(enumInfo.getName())) {
				enumInfo.setPersistentEnum(true);
			}

			// enum constant
			for (EnumConstantOutline eco : eo.constants) {
				EnumConstantInfo enumConstant = new EnumConstantInfo();
				// name of this enum constant
				enumConstant.setName(eco.target.getName());
				// value of this enum constant
				enumConstant.setValue(eco.target.getLexicalValue());
				// java doc on this enum constant
				enumConstant.setDocComment(eco.target.javadoc);

				// add this enum constant in the enum model
				enumInfo.getEnumConstants().add(enumConstant);
			}

			// xsd annotation as doc comment
			String docComment = ModelBuilder.getDocumentation(eo.target
					.getSchemaComponent());
			enumInfo.setDocComment(docComment);

			// add this enum in the codegen model
			cgModel.getEnums().add(enumInfo);
		}
    }
}
