[#ftl]
package ${packageName};

import android.net.http.AndroidHttpClient;
import android.util.Log;

import ${packageName}.ApiException;
import ${packageName}.JsonUtil;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.util.Map;

/**
* Generated API invoker.
*
*/
public class ApiInvoker {

public static final String REST_API_LOGGER = "REST API LOG";

public static enum Method {
GET, POST, PUT, DELETE, HEAD, OPTIONS, TRACE, CONNECT
}


public static <T> T invoke(final String path,
    final Method method,
    final Object userRequest,
    final Class<T> responseClass,
        final String userAgent,
        final boolean enableLogging,
        final String login,
        final String password,
        final Map<String, String> extraHeaders)
        throws ApiException {
        AndroidHttpClient client = null;
        try {
        final URL url = new URL(path);

        client = AndroidHttpClient.newInstance(userAgent);
        if (enableLogging) {
        client.enableCurlLogging(REST_API_LOGGER, Log.VERBOSE);
        }

        HttpContext credContext = new BasicHttpContext();
        if (login != null) {
        AuthScope scope = new AuthScope(url.getHost(), url.getPort());
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(login, password);
        CredentialsProvider cp = new BasicCredentialsProvider();
        cp.setCredentials(scope, creds);
        credContext.setAttribute(ClientContext.CREDS_PROVIDER, cp);
        }

        // Prepare json request
        final StringEntity outputEntity;
        if (userRequest != null) {
        final String requestAsString = JsonUtil.toJson(userRequest);
        outputEntity = new StringEntity(requestAsString);
        if(enableLogging) {
        android.util.Log.d(REST_API_LOGGER, "Called " + url + " with json :\n" + requestAsString);
        }
        } else {
        outputEntity = null;
        }

        final HttpRequestBase httpRequest;
        final URI uri = url.toURI();
        switch (method) {
        case POST:
        final HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(outputEntity);
        httpRequest = httpPost;
        break;
        case GET:
        httpRequest = new HttpGet(uri);
        break;
        case PUT:
        final HttpPut httpPut = new HttpPut(uri);
        httpPut.setEntity(outputEntity);
        httpRequest = httpPut;
        break;
        case OPTIONS:
        httpRequest = new HttpOptions(uri);
        break;
        case DELETE:
        httpRequest = new HttpDelete(uri);
        break;
        case HEAD:
        case CONNECT:
        case TRACE:
        default:
        throw new RuntimeException("Unsupported method : " + method);
        }

        if (extraHeaders != null) {
        for (Map.Entry<String, String> entry : extraHeaders.entrySet()) {
        httpRequest.addHeader(entry.getKey(), entry.getValue());
        }
        }
        httpRequest.addHeader("Content-Type", "application/json");

        // Invoke
        final HttpHost host = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
        final HttpResponse httpResponse = client.execute(host, httpRequest, credContext);

        final int responseCode = httpResponse.getStatusLine().getStatusCode();

        android.util.Log.d(REST_API_LOGGER, "Server answered with response " + responseCode);

        Reader reader = null;
        try {
        switch (responseCode) {
        case 200:// OK with data
        if(enableLogging) {
            reader = new LogInputStreamReader(httpResponse.getEntity().getContent());
        } else {
            reader = new InputStreamReader(httpResponse.getEntity().getContent());
        }
        return JsonUtil.readJson(reader, responseClass);
        case 400:// KO with data
        if(enableLogging) {
            reader = new LogInputStreamReader(httpResponse.getEntity().getContent());
        } else {
            reader = new InputStreamReader(httpResponse.getEntity().getContent());
        }
        return JsonUtil.readJson(reader, responseClass);
        case 204:// OK, no data
        return null;
        case 404:// KO, no data
        default:
        throw new ApiException("Error " + responseCode);
        }
        } finally {
        if (reader != null) {
        try {
        reader.close();
        } catch (Exception e) {
        android.util.Log.e(REST_API_LOGGER, "Error while closing reader", e);
        }
        }
        }
        } catch (final Exception e) {
        throw new ApiException("Error while calling " + path + " with method " + method, e);
        } finally {
        try {
        if(client != null) {
        client.close();
        }
        } catch (final Exception e) {
        android.util.Log.e(REST_API_LOGGER, "Error while closing client", e);
        }
        }
        }

            public static final class LogInputStreamReader extends InputStreamReader {

                private final StringBuilder builder;

                public LogInputStreamReader(InputStream in) {
                    super(in);
                    builder = new StringBuilder(1024);
                }

                @Override
                public int read() throws IOException {
                    int read = super.read();
                    if(read == -1) {
                        android.util.Log.d(REST_API_LOGGER, builder.toString());
                    } else {
                        builder.append((char) read);
                    }
                    return read;
                }

                @Override
                public int read(char[] cbuf, int offset, int length) throws IOException {
                    int read = super.read(cbuf, offset, length);
                    if(read == -1) {
                        android.util.Log.d(REST_API_LOGGER, builder.toString());
                    } else {
                        for (int i = offset; i < offset + read; i++) {
                            builder.append(cbuf[i]);
                        }
                    }
                    return read;
                }

            }
        }