[#ftl]
package ${packageName};

import android.util.Base64;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Generated API invoker.
 */
public class ApiInvoker {

    public static final String REST_API_LOGGER = "REST API LOG";

    public static enum Method {
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
                               final Map<String, ComputedHttpHeaderValue> extraComputedHeaders)
            throws ApiException, ApiFunctionalError {

        HttpURLConnection connection = null;

        try {

            final URL url = new URL(path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", userAgent);

            if (login != null) {
                String userpass = login + ":" + password;
                String basicAuth = "Basic " + new String(Base64.encode(userpass.getBytes(), Base64.DEFAULT));
                connection.setRequestProperty("Authorization", basicAuth);
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
            } else {
                requestAsString = null;
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
                return (T) content;
            }

            Reader reader = null;
            try {
                int responseFactor = responseCode / 100;
                switch (responseFactor) {
                    case 2:
                        // Normal responses : 200, 201, ... 299
                        final InputStream content = connection.getInputStream();
                        if (content == null) {
                            return null;
                        }
                        reader = enableLogging ? new LogInputStreamReader(content) : new InputStreamReader(content);
                        return JsonUtil.readJson(reader, responseClass);
                    default:
                        // Other responses
                        final InputStream errorContent = connection.getInputStream();
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
                if (connection != null) {
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
}