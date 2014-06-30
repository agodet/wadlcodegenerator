[#ftl]
package ${packageName};

import ${utilityPackageName}.ApiException;
import ${utilityPackageName}.ApiInvoker;
import ${utilityPackageName}.ApiConfig;

[#list imports as import]
import ${import};
[/#list]
import java.util.*;
import java.io.*;
import java.net.URLEncoder;

public class ${className}Api {

private final ApiConfig mConfig;
private final ApiInvoker mApiInvoker;

public ${className}Api(final ApiConfig config) {
    mConfig = config;
    mApiInvoker = new ApiInvoker();
}

[#list faults as fault]
    /**
     * GENERATED
     */
    public static class ${className}${fault.name}Exception extends Exception {

        public final ${fault.name} nestedError;

        public ${className}${fault.name}Exception(${fault.name} nestedError) {
            this.nestedError = nestedError;
        }
    }

[/#list]

[#list methods as method]

public ${method.response.name} ${method.name} (
    [#if method.requestParams??]
        [#list method.requestParams as param]
        final String ${param.name?uncap_first}[#if param_has_next || method.request??],[/#if]
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

    [#assign hasParams = method.requestParams?? && method.requestParams?has_content/]

    [#-- Ajout des requestParams --]
    [#if hasParams]
    /* Add extra parameters */
    final String extraParamsFormat = "?[#list method.requestParams as param]${param.name}=%${param_index + 1}$s[#if param_has_next]&[/#if][/#list]";

    final String extraParams;
    try{
        extraParams = [#if hasParams]String.format(extraParamsFormat, [#list method.requestParams as param]URLEncoder.encode(${param.name?uncap_first}, "UTF-8")[#if param_has_next],[/#if][/#list])[#else]extraParamsFormat[/#if];

    } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);// Will never happen. For compilation.
    }
    [/#if]
    [#-- Fin des requestParams --]

    try {
        return mApiInvoker.invoke(
                mConfig.getBaseUrl() + "${method.path}"[#if hasParams] + extraParams[/#if],
                ApiInvoker.Method.${method.type},
                [#if method.request??]body[#else]null[/#if],
                ${method.response.name}.class,
                faults,
                mConfig.getUserAgent(),
                mConfig.isDebugLogEnabled(),
                mConfig.getLogin(),
                mConfig.getPassword(),
                mConfig.getExtraHeaders(),
                mConfig.getComputedExtraHeaders());
    } catch (ApiInvoker.ApiFunctionalError apiFunctionalError) {
            switch(apiFunctionalError.errorCode) {
            [#list method.faultsMap?keys as key]
                case ${key}:
                    throw new ${className}${method.faultsMap[key].name}Exception((${method.faultsMap[key].name})apiFunctionalError.nestedError);
            [/#list]
                default:
                    throw new ApiException(apiFunctionalError.nestedError.toString(), apiFunctionalError);
            }
    }
}

[/#list]
}
