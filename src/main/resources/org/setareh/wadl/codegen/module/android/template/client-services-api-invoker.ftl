[#ftl]
package ${packageName};

import android.text.TextUtils;
import android.util.Base64;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

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
                               final Method method,
                               final Object userRequest,
                               final Class<T> responseClass,
                               final Map<Integer, Class<?>> faultClasses,
                               final String userAgent,
                               final boolean enableLogging,
                               final String login,
                               final String password,
                               final Map<String, String> extraHeaders,
                               final Map<String, ComputedHttpHeaderValue> extraComputedHeaders,
                               final ResponseProcessor preProcessor,
                               final SSLSocketFactory sslSocketFactory,
                               final int connectTimeout,
                               final int readTimeout)
            throws ApiException, ApiFunctionalError {

        HttpURLConnection connection = null;
        boolean closeConnectionWhenFinished = true;
        try {

            final URL url = new URL(path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
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
                InputStream content = connection.getInputStream();
                closeConnectionWhenFinished = false;
                return (T) new ConnectionClosingInputStream(content, connection);
            }

            Reader reader = null;
            try {
                int responseFactor = responseCode / 100;
                switch (responseFactor) {
                    case 2:
                        // Normal responses : 200, 201, ... 299
                        InputStream content = connection.getInputStream();
                        if (preProcessor != null) {
                            if (enableLogging) {
                                android.util.Log.v(REST_API_LOGGER, "Pre-processing data with " + preProcessor);
                            }
                            content = preProcessor.preProcess(responseCode, content);
                        }
                        if (content == null) {
                            return null;
                        }
                        reader = enableLogging ? new LogInputStreamReader(content) : new InputStreamReader(content);
                        return JsonUtil.readJson(reader, responseClass);
                    default:
                        // Other responses
                        InputStream errorContent = connection.getErrorStream();
                        if (preProcessor != null) {
                            if (enableLogging) {
                                android.util.Log.v(REST_API_LOGGER, "Pre-processing error data with " + preProcessor);
                            }
                            errorContent = preProcessor.preProcess(responseCode, errorContent);
                        }

                        final Class<?> faultClass = faultClasses.get(responseCode);
                        if (errorContent == null || faultClass == null) {
                            throw new ApiException("Error " + responseCode);
                        }
                        reader = enableLogging ? new LogInputStreamReader(errorContent) : new InputStreamReader(errorContent);

                        final Object nestedError = JsonUtil.readJson(reader, faultClass);
                        throw new ApiFunctionalError(responseCode, nestedError);
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

    /**
     * A processor that pre-processes response before API invoker processes it.
     */
    public interface ResponseProcessor {
        /**
         * Pre-process data before Invoker does.
         * @param responseCode the response code that was given by the server
         * @param inputStream the input stream (nullable) that was answered by server
         * @return the pre-processed input stream (error or success stream, depending on response type).
         */
        InputStream preProcess(int responseCode, InputStream inputStream);
    }
}