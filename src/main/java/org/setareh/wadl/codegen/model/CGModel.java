package org.setareh.wadl.codegen.model;

import java.util.Collection;
import java.util.TreeSet;

/**
 * Data model for code generation
 *
 * @author bulldog
 */
public class CGModel {

    private final TreeSet<ClassInfo> classes = new TreeSet<>();

    private final TreeSet<EnumInfo> enums = new TreeSet<>();

    /**
     * Class model for codegen
     *
     * @return List<ClassInfo>
     */
    public Collection<ClassInfo> getClasses() {
        return classes;
    }

    /**
     * Enum model for codegen
     *
     * @return List<EnumInfo>
     */
    public Collection<EnumInfo> getEnums() {
        return enums;
    }

}
