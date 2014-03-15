[#ftl]
package ${packageName};

import ${utilityPackageName}.ApiException;
import ${utilityPackageName}.ApiInvoker;
import ${utilityPackageName}.ApiConfig;

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

public ${method.response.name} ${method.name} (${method.request.name} body) throws ApiException {
    return mApiInvoker.invoke(
        mConfig.getBaseUrl() + "${method.path}",
        ApiInvoker.Method.${method.type},
        body,${method.response.name}.class ,
        mConfig.getUserAgent(),
        mConfig.isDebugLogEnabled(),
        mConfig.getLogin(),
        mConfig.getPassword(),
        mConfig.getExtraHeaders());
    }

[/#list]
}
