[#ftl]
[#--template for the client-side enum _type.--]
// Generated by xsd compiler for typescript

module ${namespace} {

    export var ${enum.name} = {
    [#list enum.enumConstants as constant]
    ${constant.name}: "${constant.name}"[#if constant_has_next],[/#if]
    [/#list]

    }
}