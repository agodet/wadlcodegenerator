[#ftl]
package ${packageName};

import ${utilityPackageName}.ApiException;
import ${utilityPackageName}.ApiInvoker;
import ${utilityPackageName}.ApiConfig;
import ${utilityPackageName}.JsonUtil;

[#list imports as import]
import ${import};
[/#list]
import java.util.*;
import java.io.*;
import java.net.URLEncoder;

public class ${className}Api {

private final ApiConfig mConfig;

public ${className}Api(final ApiConfig config) {
    mConfig = config;
}

[#list faults as fault]
    /**
     * GENERATED
     */
    public static class ${className}${fault.name}Exception extends Exception {

        public final ${fault.name} nestedError;

        public ${className}${fault.name}Exception(${fault.name} nestedError) {
            super("This stacktrace should not appear in logs : this exception is a generated wrapper that wraps a functional exception, and its nested ${fault.name} should be analyzed for more details.");
            this.nestedError = nestedError;
        }
    }

[/#list]

[#list methods as method]

public ${method.response.name} ${method.name} (
    [#if method.templateParams??]
        [#list method.templateParams as param]
        final ${param.classInfo.name} ${param.name?uncap_first}[#if param_has_next || method.request?? || method.requestParams?has_content],[/#if]
        [/#list]
    [/#if]
    [#if method.requestParams??]
        [#list method.requestParams as param]
        final ${param.classInfo.name} ${param.name?uncap_first}[#if param_has_next || method.request??],[/#if]
        [/#list]
    [/#if]
    [#if method.request??]${method.request.name} body[/#if]

) throws
[#list method.faults as fault]
        ${className}${fault.name}Exception,
[/#list]
        ApiException {
    final HashMap<Integer, Class<?>> faults = new HashMap<Integer, Class<?>>() {{
    [#list method.faultsMap?keys as key]
        put(${key}, ${method.faultsMap[key].name}.class);
    [/#list]
    }};

    [#assign hasRequestParams = method.requestParams?? && method.requestParams?has_content/]
    [#assign hasTemplateParams = method.templateParams?? && method.templateParams?has_content/]

    [#-- Ajout des requestParams --]
    [#if hasRequestParams || hasTemplateParams]
    /* Stringify parameters */
    [#list method.requestParams + method.templateParams as param]
    final String ${param.name?uncap_first}AsStr = ${param.name?uncap_first} == null ? "" :
        [#switch param.classInfo.name?lower_case]
            [#case 'string'] ${param.name?uncap_first}[#break]
            [#case 'int']
            [#case 'integer'] Integer.toString(${param.name?uncap_first})[#break]
            [#case 'long'] Long.toString(${param.name?uncap_first})[#break]
            [#case 'date'] JsonUtil.formatDate(${param.name?uncap_first})[#break]
            [#case 'float']
            [#case 'double']String.format(java.util.Locale.ENGLISH, "%.8f", ${param.name?uncap_first})[#break]
            [#default]${param.name?uncap_first}.toString()
        [/#switch];
    [/#list]
    [/#if]

    [#if hasRequestParams]
    /* Build queryString parameters */
    final String extraParamsFormat = "?[#list method.requestParams as param]${param.name}=%${param_index + 1}$s[#if param_has_next]&[/#if][/#list]";


    final String extraParams;
    try{
        extraParams = [#compress]
        [#if hasRequestParams]
        String.format(extraParamsFormat,[#list method.requestParams as param]
        URLEncoder.encode(${param.name?uncap_first}AsStr, "UTF-8")[#if param_has_next],[/#if]
        [/#list])
        [#else]extraParamsFormat[/#if]
        [/#compress];

    } catch (Exception e) {
        throw new RuntimeException(e);
    }
    [/#if]
    [#-- Fin des requestParams --]

    final String basePath = "${method.path}"[#compress]
    [#if method.templateParams??]
        [#list method.templateParams as param]
        .replace("{${param.name}}", ${param.name?uncap_first}AsStr)
        [/#list]
    [/#if]
    [/#compress];

    try {
        return ApiInvoker.invoke(
                mConfig.getBaseUrl() + basePath[#if hasRequestParams] + extraParams[/#if],
                ApiInvoker.Method.${method.type},
                [#if method.request??]body[#else]null[/#if],
                ${method.response.name}.class,
                faults,
                mConfig.getUserAgent(),
                mConfig.isDebugLogEnabled(),
                mConfig.getLogin(),
                mConfig.getPassword(),
                mConfig.getExtraHeaders(),
                mConfig.getComputedExtraHeaders(),
                mConfig.getSSLSocketFactory(),
                mConfig.getConnectTimeout(),
                mConfig.getReadTimeout());
    } catch (ApiInvoker.ApiFunctionalError apiFunctionalError) {
            switch(apiFunctionalError.errorCode) {
            [#list method.faultsMap?keys as key]
                case ${key}:
                    throw new ${className}${method.faultsMap[key].name}Exception((${method.faultsMap[key].name})apiFunctionalError.nestedError);
            [/#list]
                default:
                    throw new ApiException(apiFunctionalError.errorCode, apiFunctionalError);
            }
    }
}

[/#list]
}
