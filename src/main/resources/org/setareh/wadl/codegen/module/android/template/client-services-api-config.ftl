[#ftl]
package ${packageName};

import java.util.Map;
import javax.net.ssl.SSLSocketFactory;
import android.support.annotation.Nullable;

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
    Map<String, ComputedHttpHeaderValue> getComputedExtraHeaders();
    @Nullable SSLSocketFactory getSSLSocketFactory();

}