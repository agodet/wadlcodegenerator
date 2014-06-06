[#ftl]
package ${packageName};

import ${utilityPackageName}.ApiException;
import ${utilityPackageName}.ApiInvoker;
import ${utilityPackageName}.ApiConfig;

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

public ${method.response.name} ${method.name} (${method.request.name} body) throws
[#list method.faults as fault]
        ${className}${fault.name}Exception,
[/#list]
        ApiException {

    final HashMap<Integer, Class<?>> faults = new HashMap<Integer, Class<?>>() {{
    [#list method.faultsMap?keys as key]
        put(${key}, ${method.faultsMap[key].name}.class);
    [/#list]
    }};

    try {
        return mApiInvoker.invoke(
                mConfig.getBaseUrl() + "${method.path}",
                ApiInvoker.Method.${method.type},
                body,
                ${method.response.name}.class,
                faults,
                mConfig.getUserAgent(),
                mConfig.isDebugLogEnabled(),
                mConfig.getLogin(),
                mConfig.getPassword(),
                mConfig.getExtraHeaders());
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
