package io.blockchainetl.common.utils;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.lang.reflect.Field;

public class JsonUtils {

    public static JsonNode parseJson(String msg) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parseJson(String s, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(s, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parseJson(String s, TypeReference<T> typeReference) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(s, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T mergeObjects(T common, T override) throws IllegalAccessException, InstantiationException {
        Class<?> clazz = common.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Object returnValue = clazz.newInstance();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value1 = field.get(common);
            Object value2 = field.get(override);
            Object value = (value1 != null) ? value1 : value2;
            field.set(returnValue, value);
        }
        return (T) returnValue;
    }
}
