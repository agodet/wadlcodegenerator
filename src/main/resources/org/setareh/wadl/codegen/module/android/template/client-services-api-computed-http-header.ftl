[#ftl]
package ${packageName};

import java.net.URL;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.*;
/**
* Configuration class to simplify client calls.
*/
public interface ComputedHttpHeaderValue {

    String computeHeader(URL url, ApiInvoker.Method method, HttpRequest request);

}