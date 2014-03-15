[#ftl]
package ${packageName};

import java.util.Map;

/**
* Configuration class to simplify client calls.
*/
public interface ApiConfig {
String getBaseUrl();
boolean isDebugLogEnabled();
String getUserAgent();
String getLogin();
String getPassword();
Map<String, String> getExtraHeaders();
}