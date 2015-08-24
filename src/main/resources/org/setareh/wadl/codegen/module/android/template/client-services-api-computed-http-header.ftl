[#ftl]
package ${packageName};

import java.net.URL;

/**
* Configuration class to simplify client calls.
*/
public interface ComputedHttpHeaderValue {

    String computeHeader(URL url, ApiInvoker.Method method, String payload);

}