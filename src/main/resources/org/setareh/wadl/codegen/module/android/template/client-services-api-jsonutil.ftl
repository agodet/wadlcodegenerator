[#ftl]
package ${packageName};

import com.google.gson.*;
import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.Reader;
import java.lang.reflect.Type;

public final class JsonUtil {

    /**
     * Supposed to be thread-safe.
     * See https://groups.google.com/forum/?fromgroups#!topic/google-gson/Vju1HuJJUIE
     * <p/>
     * For now, simple mapper, to be customized when needed through a static block.
     */
    private static final Gson sGson;

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    static {
        final GsonBuilder builder = new GsonBuilder()
                .setDateFormat(DATE_FORMAT)
                .registerTypeAdapter(DateTime.class, new DateTimeAdapter());
        sGson = builder.create();
    }


    public static class DateTimeAdapter implements JsonDeserializer<DateTime>, JsonSerializer<DateTime> {

        @Override
        public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return new DateTime(json.getAsJsonPrimitive().getAsString());
        }

        @Override
        public JsonElement serialize(DateTime dateTime, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(dateTime.toString());
        }
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
     * Helper method to format a date with the appropriate format.
     */
    public static String formatDate(Date departureDate) {
        return new SimpleDateFormat(DATE_FORMAT).format(departureDate);
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
