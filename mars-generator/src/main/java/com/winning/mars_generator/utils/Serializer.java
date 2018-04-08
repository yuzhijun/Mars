package com.winning.mars_generator.utils;

import com.google.gson.JsonElement;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

public interface Serializer {
    String serialize(Object o);

    <T> T deserialize(Reader reader, Class<T> clz);
    <T> T deserialize(String json,Class<T> clz);
    <T> T deserialize(String json, Type typeOfT);
    <T> T deserialize(JsonElement json, Class<T> classOfT);
    <T extends Object> List<T> getJsonList(String json, Class<T> clazz);
}
