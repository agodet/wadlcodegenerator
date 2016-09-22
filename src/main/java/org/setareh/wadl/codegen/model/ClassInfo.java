package org.setareh.wadl.codegen.model;

import com.sun.istack.NotNull;
import org.setareh.wadl.codegen.model.annotation.RootElementAnnotation;
import org.setareh.wadl.codegen.model.annotation.XmlTypeAnnotation;

import java.util.ArrayList;
import java.util.List;

/**
 * Class model for codegen
 *
 * @author bulldog
 */
public class ClassInfo implements Comparable<ClassInfo> {

    // package name of this class
    private String packageName;
    // simple name of this class
    private String name;
    // full name of this class
    private String fullName;
    // is this an abstract class
    private boolean isAbstract;

    // the super class this class extends
    private ClassInfo superClass;
    // doc comment of this class
    private String docComment;

    // is this a nest class?
    private boolean nestClass = false;

    private RootElementAnnotation rootElementAnnotation;

    private XmlTypeAnnotation xmlTypeAnnotation;

    // fields of this class
    private final List<FieldInfo> fields = new ArrayList<FieldInfo>();
    private final List<FieldInfo> superClassesFields = new ArrayList<FieldInfo>();


    /**
     * A list of fields this class contains
     *
     * @return a list of {@link FieldInfo} instances
     */
    public List<FieldInfo> getFields() {
        return fields;
    }

    public List<FieldInfo> getSuperClassesFields() {
        return superClassesFields;
    }

    public List<FieldInfo> getAllFields() {
        ArrayList<FieldInfo> allFields = new ArrayList<>();
        allFields.addAll(getSuperClassesFields());
        allFields.addAll(getFields());
        return allFields;
    }

    /**
     * the package name of this class
     *
     * @return package name
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * set the package name of this class
     *
     * @param packageName
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * get simple name of this class
     *
     * @return simple name
     */
    public String getName() {
        return name;
    }

    /**
     * set simple name of this class
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * get full name of this class
     *
     * @return full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * set full name of this class
     *
     * @param fullName, the full name of this class
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * abstract class or not
     *
     * @return abstract class or not
     */
    public boolean isAbstract() {
        return isAbstract;
    }

    /**
     * set abstract class or not
     *
     * @param isAbstract
     */
    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    /**
     * get super class type if this class extends any super class
     *
     * @return super class type, null if this class does not explicitly extend any super class
     */
    public ClassInfo getSuperClass() {
        return superClass;
    }

    /**
     * set super class type if this class extends any super class
     *
     * @param superClass
     */
    public void setSuperClass(ClassInfo superClass) {
        this.superClass = superClass;
    }

    /**
     * doc comment of this class, extracted from xsd annotation
     *
     * @return doc comment
     */
    public String getDocComment() {
        return docComment;
    }

    /**
     * set doc comment of this class
     *
     * @param docComment
     */
    public void setDocComment(String docComment) {
        this.docComment = docComment;
    }

    public RootElementAnnotation getRootElementAnnotation() {
        return rootElementAnnotation;
    }

    public void setRootElementAnnotation(RootElementAnnotation rootElementAnnotation) {
        this.rootElementAnnotation = rootElementAnnotation;
    }

    public XmlTypeAnnotation getXmlTypeAnnotation() {
        return xmlTypeAnnotation;
    }

    public void setXmlTypeAnnotation(XmlTypeAnnotation xmlTypeAnnotation) {
        this.xmlTypeAnnotation = xmlTypeAnnotation;
    }

    /**
     * Is this a nest class or not
     *
     * @return true if nest class, false otherwise
     */
    public boolean isNestClass() {
        return nestClass;
    }

    /**
     * Set is this a nest class or not
     *
     * @param nestClass
     */
    public void setNestClass(boolean nestClass) {
        this.nestClass = nestClass;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassInfo classInfo = (ClassInfo) o;

        if (fullName != null ? !fullName.equals(classInfo.fullName) : classInfo.fullName != null) return false;
        if (name != null ? !name.equals(classInfo.name) : classInfo.name != null) return false;
        if (packageName != null ? !packageName.equals(classInfo.packageName) : classInfo.packageName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = packageName != null ? packageName.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return fullName == null ? super.toString() : fullName;
    }

    @Override
    public int compareTo(ClassInfo o) {
        final int compare = packageName.compareTo(o.packageName);
        if (compare != 0) {
            return compare;
        }
        return fullName.compareTo(o.fullName);
    }
}
