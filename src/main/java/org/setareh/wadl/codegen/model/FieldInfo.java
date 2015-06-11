package org.setareh.wadl.codegen.model;

import org.setareh.wadl.codegen.model.annotation.AttributeAnnotation;
import org.setareh.wadl.codegen.model.annotation.ElementAnnotation;

/**
 * Field model for codegen
 * 
 * @author bulldog
 *
 */
public class FieldInfo {

    // name of this field
    private String name;

    private String initialName;

    // type of this field
    private TypeInfo type;

    // doc comments
    private String docComment;

    private ElementAnnotation elementAnnotation;

    private AttributeAnnotation attributeAnnotation;
    private String value;
    private boolean fixedValue = false;
    private boolean propertyKindElement;
    private boolean propertyKindAttribute;
    private boolean propertyKindValue;
    private boolean propertyKindAny;
    private boolean required;

    /**
     * doc comment of this field
     *
     * @return doc comment
     */
    public String getDocComment() {
        return docComment;
    }

    /**
     * set doc comment of this field
     *
     * @param docComment
     */
    public void setDocComment(String docComment) {
        this.docComment = docComment;
    }

    /**
     * simple name of this field
     *
     * @return simple name
     */
    public String getName() {
        return name;
    }

    /**
     * set simple name of this field
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * type of this field
     *
     * @return {@link org.setareh.wadl.codegen.model.TypeInfo} instance
     */
    public TypeInfo getType() {
        return type;
    }

    /**
     * set type of this field
     *
     * @param type
     */
    public void setType(TypeInfo type) {
        this.type = type;
    }

    public ElementAnnotation getElementAnnotation() {
        return elementAnnotation;
    }

    public void setElementAnnotation(ElementAnnotation elementAnnotation) {
        this.elementAnnotation = elementAnnotation;
    }

    public AttributeAnnotation getAttributeAnnotation() {
        return attributeAnnotation;
    }

    public void setAttributeAnnotation(AttributeAnnotation attributeAnnotation) {
        this.attributeAnnotation = attributeAnnotation;
    }

    public void setInitialName(String initialName) {
        this.initialName = initialName;
    }

    public String getInitialName() {
        return initialName;
    }

    public boolean isModifiedName() {
        return this.initialName != this.name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setFixedValue(boolean fixed) {
        this.fixedValue = fixed;
    }

    public boolean isFixedValue() {
        return fixedValue;
    }

    public void setPropertyKindElement(boolean propertyKindElement) {
        this.propertyKindElement = propertyKindElement;
    }

    public boolean isPropertyKindElement() {
        return propertyKindElement;
    }

    public void setPropertyKindAttribute(boolean propertyKindAttribute) {
        this.propertyKindAttribute = propertyKindAttribute;
    }

    public boolean isPropertyKindAttribute() {
        return propertyKindAttribute;
    }

    public void setPropertyKindValue(boolean propertyKindValue) {
        this.propertyKindValue = propertyKindValue;
    }

    public boolean isPropertyKindValue() {
        return propertyKindValue;
    }

    public void setPropertyKindAny(boolean propertyKindAny) {
        this.propertyKindAny = propertyKindAny;
    }

    public boolean isPropertyKindAny() {
        return propertyKindAny;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
