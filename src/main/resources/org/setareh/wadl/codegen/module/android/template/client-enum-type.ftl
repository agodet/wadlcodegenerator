[#ftl]
[#--template for the client-side enum type.--]
// Generated by xsd compiler for android/java
// DO NOT CHANGE!
package ${enum.packageName};

[#if enum.docComment??]
/**
 * ${enum.docComment?chop_linebreak?replace("\n", "\n * ")?replace("\t", "")}
 */
[/#if]
public enum ${enum.name} {
  [#list enum.enumConstants as constant]

  [#if constant.docComment??]
    /**
     * ${constant.docComment?replace("\n", "\n   * ")?replace("\t", "")}
     */
  [/#if]
    ${constant.name}("${constant.value}")[#if constant_has_next],[#else];[/#if]
  
  [/#list]
  
    private final String value;
  
    ${enum.name}(String v) {
        value = v;
    }
    
    public String value() {
        return value;
    }

    public static ${enum.name} fromValue(String v) {
        if (v != null) {
            for (${enum.name} c: ${enum.name}.values()) {
                if (c.value.equals(v)) {
                    return c;
                }
            }
        }
        throw new IllegalArgumentException(v);
    }
}