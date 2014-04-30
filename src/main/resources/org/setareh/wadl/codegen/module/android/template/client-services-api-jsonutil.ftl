[#ftl]
package ${packageName};

import com.google.gson.*;

import java.io.Reader;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class JsonUtil {

/**
* Supposed to be thread-safe.
* See https://groups.google.com/forum/?fromgroups#!topic/google-gson/Vju1HuJJUIE
* <p/>
* For now, simple mapper, to be customized when needed through a static block.
*/
private static final Gson sGson;

static {
final GsonBuilder builder = new GsonBuilder();
builder.setDateFormat("yyyy-MM-dd'T'kk:mm:ssZ");
sGson = builder.create();
}

/**
* Read json string into object
*
* @param json   the json reader to be interpreted
* @param aClass the expected serial object output class
* @param <T>    the expected serial object's class (generic)
    * @return the instance of object that was read from json
    */
    public static <T> T readJson(final Reader json, final Class<T> aClass) throws JsonException {
        try {
        return sGson.fromJson(json, aClass);
        } catch (Exception e) {
        throw new JsonException(e);
        }
        }

        /**
        * Print object as JSON string. Log data to INFO stream when enabled.
        *
        * @param object the object to be serialized
        */
        public static String toJson(final Object object) throws JsonException {
        final String output;
        try {
        output = sGson.toJson(object);
        } catch (Exception e) {
        throw new JsonException(e);
        }
        return output;
        }

        /**
        * Serialization/Deserialization exception
        */
        public static class JsonException extends Exception {
        public JsonException(Throwable throwable) {
        super(throwable);
        }
        }

        }
