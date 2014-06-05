[#ftl]
package ${packageName};

import ${utilityPackageName}.ApiException;
import ${utilityPackageName}.ApiInvoker;
import ${utilityPackageName}.ApiConfig;
import ${utilityPackageName}.Result;

import java.util.Map;

[#list imports as import]
import ${import};
[/#list]
import java.util.*;

public class ${className}Api {

private final ApiConfig mConfig;
private final ApiInvoker mApiInvoker;

public ${className}Api(final ApiConfig config) {
mConfig = config;
mApiInvoker = new ApiInvoker();
}

[#list methods as method]

public Result<${method.response.name}, [#if method.fault??]${method.fault.name}[#else]Void[/#if]> ${method.name} (${method.request.name} body) throws ApiException {
    return mApiInvoker.invoke(
        mConfig.getBaseUrl() + "${method.path}",
        ApiInvoker.Method.${method.type},
        body,
        ${method.response.name}.class,
        [#if method.fault??]${method.fault.name}.class[#else]null[/#if],
        mConfig.getUserAgent(),
        mConfig.isDebugLogEnabled(),
        mConfig.getLogin(),
        mConfig.getPassword(),
        mConfig.getExtraHeaders());
    }

[/#list]
}
