package org.setareh.wadl.codegen.builder;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.model.*;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.xml.bind.api.impl.NameConverter;
import com.sun.xml.bind.v2.model.core.PropertyKind;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSTerm;
import org.setareh.wadl.codegen.model.*;
import org.setareh.wadl.codegen.model.annotation.AttributeAnnotation;
import org.setareh.wadl.codegen.model.annotation.ElementAnnotation;
import org.setareh.wadl.codegen.model.annotation.RootElementAnnotation;
import org.setareh.wadl.codegen.model.annotation.XmlTypeAnnotation;
import org.setareh.wadl.codegen.module.ClientModule;
import org.setareh.wadl.codegen.utils.ClassNameUtil;
import org.setareh.wadl.codegen.utils.IOUtils;
import org.setareh.wadl.codegen.utils.StringUtil;

import javax.xml.namespace.QName;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

public class ClassModelBuilder {

    public static void buildClassModel(Outline outline, CGModel cgModel, CGConfig cgConfig) {

        // build class full name to element qname mapping
        Map<String, QName> mapping = buildClass2ElementMapping(outline);

        String[] persistentFileArray = {};

        try
            { persistentFileArray = ClassModelBuilder.generatePersistentData(cgConfig); }
        catch(Exception e) { e.printStackTrace(); }

        for (ClassOutline co : outline.getClasses()) {
            ClassInfo classInfo = new ClassInfo();
            String packageName = ClassNameUtil.getPackageName(co.implClass.fullName());
            // for anonymous inner class, we need to change package name to lower case
            classInfo.setNestClass(isNestClass(co.implClass));

            classInfo.setPackageName(packageName);

            classInfo.setName(co.implClass.name());
            classInfo.setFullName((!StringUtil.isEmpty(classInfo.getPackageName()) ? packageName + "." : "") + classInfo.getName());

            classInfo.setXmlTypeAnnotation(getXmlTypeAnnotation(co)); // @XmlType(name="foo", targetNamespace="bar://baz")
            classInfo.setAbstract(co.implClass.isAbstract());
            setSuperClass(co, classInfo);
            classInfo.setRootElementAnnotation(getRootElementAnnotation(co, mapping, classInfo));
            classInfo.setDocComment(ModelBuilder.getDocumentation(co.target.getSchemaComponent()));

            if (Arrays.asList(persistentFileArray).contains(classInfo.getName())){
                classInfo.setPersistentClass(true);
            }

            addFields(cgConfig, co, classInfo);
            addSuperClassesFields(cgConfig, co, classInfo);

            // add this class in the code generation model
            cgModel.getClasses().add(classInfo);
        }
    }

    private static void addSuperClassesFields(CGConfig cgConfig, ClassOutline co, ClassInfo classInfo) {
        ClassOutline superClass = co.getSuperClass();
        while (superClass != null && !"Object".equals(superClass.implClass.name())) {
            classInfo.getSuperClassesFields().addAll(getFields(cgConfig, superClass));
            superClass = superClass.getSuperClass();
        }
    }

    private static void addFields(CGConfig cgConfig, ClassOutline co, ClassInfo classInfo) {
        classInfo.getFields().addAll(getFields(cgConfig, co));
    }

    private static List<FieldInfo> getFields(CGConfig cgConfig, ClassOutline co) {
        List<FieldInfo> fields = new ArrayList<>();
        for (FieldOutline fo : co.getDeclaredFields()) {

            FieldInfo fieldInfo = new FieldInfo();
            // field name
            ClientModule clientModule = cgConfig.module.getClientModule();
            fieldInfo.setName(clientModule.generateSafeName(fo.getPropertyInfo().getName(false)));
            fieldInfo.setInitialName(fo.getPropertyInfo().getName(false));
            fieldInfo.setRequired(isRequired(fo));

            JType rawType = fo.getRawType();
            TypeInfo typeInfo = buildTypeInfo(rawType);

            if (rawType.isArray()) {
                typeInfo.setArray(true);
                typeInfo.setElementType(buildTypeInfo(rawType.elementType())); // T of T[]
            }

            typeInfo.getTypeParameters().addAll(getTypeParameters(rawType));

            fieldInfo.setType(typeInfo);

            // schema kind
            CPropertyInfo cProp = fo.getPropertyInfo();
            fieldInfo.setPropertyKindElement(cProp.kind() == PropertyKind.ELEMENT);
            fieldInfo.setPropertyKindAttribute(cProp.kind() == PropertyKind.ATTRIBUTE);
            fieldInfo.setPropertyKindValue(cProp.kind() == PropertyKind.VALUE);
            fieldInfo.setPropertyKindAny(cProp.kind() == PropertyKind.REFERENCE);

            setAnnotation(co, fieldInfo, cProp);

            fieldInfo.getType().setCollection(cProp.isCollection());

            setDocComment(fo, fieldInfo);
            if (fieldInfo.isRequired() && fieldInfo.getType().isCollection()) {
                System.out.println("min occurs : 1 " + co.implClass.name() + "." + fieldInfo.getInitialName());
            }
            fields.add(fieldInfo);
        }
        return fields;
    }

    private static void setDocComment(FieldOutline fo, FieldInfo attrInfo) {
        XSComponent xsComp = fo.getPropertyInfo().getSchemaComponent();
        if (xsComp != null && xsComp instanceof XSParticle) {
            XSParticle xsParticle = (XSParticle) xsComp;
            XSTerm xsTerm = xsParticle.getTerm();
            XSElementDecl elemndecl = xsTerm.asElementDecl();
            if (elemndecl.getDefaultValue() != null) {
                attrInfo.setValue(elemndecl.getDefaultValue().value);
                attrInfo.setFixedValue(false);
            } else if (elemndecl.getFixedValue() != null) {
                attrInfo.setValue(elemndecl.getFixedValue().value);
                attrInfo.setFixedValue(true);
            }

            String attrDoc = ModelBuilder.getDocumentation(xsTerm);
            attrInfo.setDocComment(attrDoc);
        }
    }

    private static void setAnnotation(ClassOutline co, FieldInfo attrInfo, CPropertyInfo cProp) {
        if (cProp instanceof CElementPropertyInfo) {
            CElementPropertyInfo ep = (CElementPropertyInfo) cProp;
            List<CTypeRef> types = ep.getTypes();
            if (types.size() == 1) {
                CTypeRef t = types.get(0);
                attrInfo.setElementAnnotation(getElementAnnotation(co, cProp, t));
            }
        } else if (cProp instanceof CAttributePropertyInfo) {
            attrInfo.setAttributeAnnotation(getAttributeAnnotation(co, cProp));
        }
    }

    private static List<TypeInfo> getTypeParameters(JType rawType) {
        List<TypeInfo> typeParameters = new ArrayList<>();
        if (rawType instanceof JClass) { // has type parameters?
            JClass clzType = (JClass) rawType;
            for (JClass typeParamClass : clzType.getTypeParameters()) {
                typeParameters.add(buildTypeInfo(typeParamClass));
            }
        }
        return typeParameters;
    }

    private static boolean isRequired(FieldOutline fo) {
        boolean isRequired = false;
        if (fo.getPropertyInfo() instanceof CElementPropertyInfo) {
            CElementPropertyInfo cElementPropertyInfo = (CElementPropertyInfo) fo.getPropertyInfo();
            isRequired = cElementPropertyInfo.isRequired();
        }
        return isRequired;
    }

    private static TypeInfo buildTypeInfo(JType jType) {
        TypeInfo typeInfo = new TypeInfo();
        typeInfo.setName(jType.name());

        // for anonymous inner class, we need to change package name to lower case
        typeInfo.setNestClass(isNestClass(jType));
        typeInfo.setFullName(jType.fullName());
        typeInfo.setPrimitive(jType.isPrimitive());

        typeInfo.setEnum(isEnum(jType));
        return typeInfo;
    }

    private static void setSuperClass(ClassOutline co, ClassInfo classInfo) {
        // has super class?
        ClassOutline sco = co.getSuperClass();
        if (sco != null) {
            ClassInfo superClass = new ClassInfo();
            superClass.setName(sco.implClass.name());
            superClass.setFullName(sco.implClass.fullName());
            classInfo.setSuperClass(superClass);
        }
    }

    private static boolean isNestClass(JType type) {
        if (type instanceof JClass) {
            JClass clazz = (JClass)type;
            JClass out = clazz.outer();
            if (out == null) {
                return false;
            }
            if (out instanceof JClass) {
                return true;
            }
        }
        return false;
    }

    private static XmlTypeAnnotation getXmlTypeAnnotation(ClassOutline co) {
        XmlTypeAnnotation xmlTypeAnnotation = new XmlTypeAnnotation();

        // used to simplify the generated annotations
        String mostUsedNamespaceURI = co._package().getMostUsedNamespaceURI();

        QName typeName = co.target.getTypeName();
        if (typeName == null) {
            xmlTypeAnnotation.setName(""); // TODO, handle anonymous type
            xmlTypeAnnotation.setNamespace(mostUsedNamespaceURI);
        } else {
            xmlTypeAnnotation.setName(typeName.getLocalPart());
            final String typeNameURI = typeName.getNamespaceURI();
//            if(!typeNameURI.equals(mostUsedNamespaceURI)) // only generate if necessary
            xmlTypeAnnotation.setNamespace(typeNameURI);
        }

        return xmlTypeAnnotation;
    }

    private static RootElementAnnotation getRootElementAnnotation(ClassOutline co, Map<String, QName> mapping, ClassInfo classInfo) {
        // does this type map to a global element?
        QName elementName = null;
        if (co.target.isElement()) {
            elementName = co.target.getElementName();
        } else {// TODO, need to figure out a general way to handle element to class mapping
            elementName = mapping.get(classInfo.getFullName());
        }

        if (elementName != null) {
            RootElementAnnotation rootElementAnnotation = new RootElementAnnotation();
            rootElementAnnotation.setName(elementName.getLocalPart());
            rootElementAnnotation.setNamespace(elementName.getNamespaceURI());

            return rootElementAnnotation;
        } else {
            return null;
        }
    }

    private static AttributeAnnotation getAttributeAnnotation(ClassOutline parent, CPropertyInfo prop) {
        CAttributePropertyInfo ap = (CAttributePropertyInfo) prop;
        QName attName = ap.getXmlName();

        AttributeAnnotation attributeAnnotation = new AttributeAnnotation();

        final String generatedName = attName.getLocalPart();

        // Issue 570; always force generating name="" when do it when globalBindings underscoreBinding is set to non default value
        // generate name property?
        if(!generatedName.equals(ap.getName(false)) || (parent.parent().getModel().getNameConverter() != NameConverter.standard)) {
            attributeAnnotation.setName(generatedName);
        }

        return attributeAnnotation;
    }

    private static ElementAnnotation getElementAnnotation(ClassOutline parent, CPropertyInfo prop, CTypeRef ctype) {
        ElementAnnotation elementAnnotation = null;

        String propName = prop.getName(false);

        // generate the name property?
        String generatedName = ctype.getTagName().getLocalPart();
        if(!generatedName.equals(propName)) {
            elementAnnotation = new ElementAnnotation();
            elementAnnotation.setName(generatedName);
        }

        return elementAnnotation;
    }

    /**
     * check if a JType is an enum type
     *
     * @param jType
     * @return boolean
     */
    private static boolean isEnum(JType jType) {
        if (jType instanceof JDefinedClass) { // is enum?
            JDefinedClass jDefinedClass = (JDefinedClass) jType;
            ClassType classType = jDefinedClass.getClassType();
            if (classType == ClassType.ENUM) {
                return true;
            }
        }
        return false;
    }

    /**
     * Build class name to element name mapping
     *
     * @param outline, JAXB schema/code model
     * @return class name to element name map
     */
    private static Map<String, QName> buildClass2ElementMapping(Outline outline) {
        Map<String, QName> mapping = new HashMap<String, QName>();
        for(CElementInfo ei : outline.getModel().getAllElements()) {
            JType exposedType = ei.getContentInMemoryType().toType(outline,Aspect.EXPOSED);
            mapping.put(exposedType.fullName(), ei.getElementName());
        }
        return mapping;
    }


    public static String[] generatePersistentData(CGConfig cgConfig) throws URISyntaxException, IOException {

        String[] persistentClassArray = {};

        URI persistentFileUri = new URI(cgConfig.persistantFilePath);

        System.out.println("Reading persistentFile from URI : " + persistentFileUri);
        String persistentData = readPersistentFile(persistentFileUri.toString());

        persistentClassArray = persistentData.split("\\r?\\n");

        return persistentClassArray;
    }


    protected static String readPersistentFile(String persistentFileURI) {
        try {
            URL url = new URL(persistentFileURI);
            InputStream in = url.openStream();
            Reader reader = new InputStreamReader(in, "UTF-8");
            return IOUtils.toString(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
