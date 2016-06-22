[#ftl]
package ${packageName};

import android.text.TextUtils;
import android.util.Base64;

import com.sncf.fusion.api.client.ApiException;
import com.sncf.fusion.api.client.ComputedHttpHeaderValue;
import com.sncf.fusion.api.client.JsonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import java.util.zip.GZIPInputStream;

/**
 * Generated API invoker.
 */
public class ApiInvoker {

    public static final String REST_API_LOGGER = "REST API LOG";

    public enum Method {
        GET, POST, PUT, DELETE, HEAD, OPTIONS, TRACE, CONNECT
    }

    public static final class ApiFunctionalError extends Exception {

        public final Object nestedError;
        public final int errorCode;

        public ApiFunctionalError(int errorCode, Object nestedError) {
            this.nestedError = nestedError;
            this.errorCode = errorCode;
        }
    }


    public static <T> T invoke(final String path,
                               final com.sncf.fusion.api.client.ApiInvoker.Method method,
                               final Object userRequest,
                               final Class<T> responseClass,
                               final Map<Integer, Class<?>> faultClasses,
                               final String userAgent,
                               final boolean enableLogging,
                               final String login,
                               final String password,
                               final Map<String, String> extraHeaders,
                               final Map<String, ComputedHttpHeaderValue> extraComputedHeaders,
                               final SSLSocketFactory sslSocketFactory)
            throws ApiException, com.sncf.fusion.api.client.ApiInvoker.ApiFunctionalError {

        HttpURLConnection connection = null;
        boolean closeConnectionWhenFinished = true;
        try {

            final URL url = new URL(path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setDoInput(true);

            if (!TextUtils.isEmpty(login)) {
                String userpass = login + ":" + password;
                String basicAuth = "Basic " + new String(Base64.encode(userpass.getBytes(), Base64.DEFAULT));
                connection.setRequestProperty("Authorization", basicAuth);
            }

            if (sslSocketFactory != null && connection instanceof HttpsURLConnection) {
                ((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
            }

            connection.setRequestMethod(method.name());

            if (extraHeaders != null) {
                for (Map.Entry<String, String> entry : extraHeaders.entrySet()) {
                    connection.addRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            connection.addRequestProperty("Content-Type", "application/json");
            connection.addRequestProperty("Accept-Encoding", "gzip");

            // Invoke

            final String requestAsString;
            if (userRequest != null) {
                requestAsString = JsonUtil.toJson(userRequest);
                if (enableLogging) {
                    android.util.Log.d(REST_API_LOGGER, "Called " + url + " with json :\n" + requestAsString);
                }
                connection.setDoOutput(true);

            } else {
                requestAsString = null;
                connection.setDoOutput(false);
            }

            if (extraComputedHeaders != null) {
                for (Map.Entry<String, ComputedHttpHeaderValue> entry : extraComputedHeaders.entrySet()) {
                    connection.addRequestProperty(entry.getKey(), entry.getValue().computeHeader(url, method, requestAsString));
                }
            }

            // Prepare json request
            if (requestAsString != null) {
                final OutputStream os = connection.getOutputStream();
                os.write(requestAsString.getBytes("UTF-8"));
                os.flush();
                os.close();
            }

            final int responseCode = connection.getResponseCode();

            android.util.Log.d(REST_API_LOGGER, "Server answered with response " + responseCode);

            // Manage simple InputStream fetches.
            if (responseClass.equals(InputStream.class) && responseCode / 100 == 2) {
                InputStream content = getInputStream(connection);
                closeConnectionWhenFinished = false;
                return (T) new com.sncf.fusion.api.client.ApiInvoker.ConnectionClosingInputStream(content, connection);
            }

            Reader reader = null;
            try {
                int responseFactor = responseCode / 100;
                switch (responseFactor) {
                    case 2:
                        // Normal responses : 200, 201, ... 299
                        InputStream content = getInputStream(connection);
                        if (content == null) {
                            return null;
                        }

                        reader = enableLogging ? new com.sncf.fusion.api.client.ApiInvoker.LogInputStreamReader(content) : new InputStreamReader(content);
                        return JsonUtil.readJson(reader, responseClass);
                    default:
                        // Other responses
                        InputStream errorContent = getErrorInputStream(connection);

                        final Class<?> faultClass = faultClasses.get(responseCode);
                        if (errorContent == null || faultClass == null) {
                            throw new ApiException("Error " + responseCode);
                        }
                        reader = enableLogging ? new com.sncf.fusion.api.client.ApiInvoker.LogInputStreamReader(errorContent) : new InputStreamReader(errorContent);

                        final Object nestedError = JsonUtil.readJson(reader, faultClass);
                        throw new com.sncf.fusion.api.client.ApiInvoker.ApiFunctionalError(responseCode, nestedError);
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
        } catch (JsonUtil.JsonException e) {
            throw new ApiException(e);
        } catch (IOException e) {
            android.util.Log.e(REST_API_LOGGER, "Network error", e);
            throw new ApiException(ApiException.NETWORK_ERROR, e.getMessage());
        } finally {
            try {
                if (connection != null && closeConnectionWhenFinished) {
                    connection.disconnect();
                }
            } catch (final Exception e) {
                android.util.Log.e(REST_API_LOGGER, "Error while closing client", e);
            }
        }
    }

    private static InputStream getErrorInputStream(HttpURLConnection connection) throws IOException {
        return getGzipInputStream(connection.getErrorStream(), connection);
    }

    private static InputStream getInputStream(HttpURLConnection connection) throws IOException {
        return getGzipInputStream(connection.getInputStream(), connection);
    }

    private static InputStream getGzipInputStream(InputStream content, HttpURLConnection connection) throws IOException {
        if (content == null) {
            return null;
        }

        String contentEncoding = connection.getContentEncoding();
        if (contentEncoding != null && content != null && "gzip".equalsIgnoreCase(contentEncoding)) {
            content = new GZIPInputStream(content);
        }
        return content;
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
            if (read == -1) {
                android.util.Log.d(REST_API_LOGGER, builder.toString());
            } else {
                builder.append((char) read);
            }
            return read;
        }

        @Override
        public int read(char[] cbuf, int offset, int length) throws IOException {
            int read = super.read(cbuf, offset, length);
            if (read == -1) {
                android.util.Log.d(REST_API_LOGGER, builder.toString());
            } else {
                for (int i = offset; i < offset + read; i++) {
                    builder.append(cbuf[i]);
                }
            }
            return read;
        }

    }

    /**
     * An InputStream that will disconnect the wrapping connection when closed itself.
     */
    private static class ConnectionClosingInputStream extends InputStream {

        private final InputStream content;
        private final HttpURLConnection connection;

        public ConnectionClosingInputStream(InputStream content, HttpURLConnection connection) {
            this.content = content;
            this.connection = connection;
        }

        @Override
        public int read() throws IOException {
            return content.read();
        }

        @Override
        public int read(byte[] b) throws IOException {
            return content.read(b);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return content.read(b, off, len);
        }

        @Override
        public long skip(long n) throws IOException {
            return content.skip(n);
        }

        @Override
        public int available() throws IOException {
            return content.available();
        }

        @Override
        public void close() throws IOException {
            content.close();
            connection.disconnect();
        }

        @Override
        public void mark(int readlimit) {
            content.mark(readlimit);
        }

        @Override
        public void reset() throws IOException {
            content.reset();
        }

        @Override
        public boolean markSupported() {
            return content.markSupported();
        }
    }
}