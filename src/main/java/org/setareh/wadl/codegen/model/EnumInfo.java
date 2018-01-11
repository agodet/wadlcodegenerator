package org.setareh.wadl.codegen.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Enum model for codegen
 *
 * @author bulldog
 */
public class EnumInfo implements Serializable, Comparable<EnumInfo> {

    private static final long serialVersionUID = -683805101371156468L;

    // package name of this enum type
    private String pkgName;
    // simple name of this enum type
    private String name;
    // full name of this enum type
    private String fullName;
    // doc comment of this enum type
    private String docComment;
    // a list of enum constant this enum contains
    private final List<EnumConstantInfo> enumConstants = new ArrayList<EnumConstantInfo>();

    // is it persisted (inherit from SURLMEnum is iOS)
    private boolean persistentEnum = false;

    /**
     * the package name of this enum type
     *
     * @return package name
     */
    public String getPackageName() {
        return pkgName;
    }

    /**
     * set the package name of this enum type
     *
     * @param pkgName
     */
    public void setPackageName(String pkgName) {
        this.pkgName = pkgName;
    }

    /**
     * get the simple name of this enum type
     *
     * @return the simple name of this enum
     */
    public String getName() {
        return name;
    }

    /**
     * set the simple name of this enum type
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * the enum constants this enum contains
     *
     * @return a list of enum constants
     */
    public List<EnumConstantInfo> getEnumConstants() {
        return enumConstants;
    }

    /**
     * the doc comment of this enum type
     *
     * @return doc comment
     */
    public String getDocComment() {
        return docComment;
    }

    /**
     * set the doc comment of this enum type
     *
     * @param docComment
     */
    public void setDocComment(String docComment) {
        this.docComment = docComment;
    }

    /**
     * Get full name of this enum
     *
     * @return full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * set full name of this enum
     *
     * @param fullName
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Is this a persistent enum or not
     *
     * @return true if persistent enum, false otherwise
     */
    public boolean isPersistentEnum() {
        return persistentEnum;
    }

    /**
     * Set is this a persistent class or not
     *
     * @param persistentEnum
     */
    public void setPersistentEnum(boolean persistentEnum) {
        this.persistentEnum = persistentEnum;
    }

    @Override
    public int compareTo(EnumInfo o) {
        final int compare = pkgName.compareTo(o.pkgName);
        if (compare != 0) {
            return compare;
        }
        return fullName.compareTo(o.fullName);
    }
}

