package com.winning.mars_generator.utils;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GsonSerializer implements Serializer {
    private Gson mGson;

    public GsonSerializer() {
        mGson = new GsonBuilder().create();
    }

    @Override
    public String serialize(Object o) {
        return mGson.toJson(o);
    }

    @Override
    public <T> T deserialize(Reader reader, Class<T> clz) {
        return mGson.fromJson(reader, clz);
    }

    public <T> T deserialize(String json,Class<T> clz){
        return mGson.fromJson(json,clz);
    }

    public <T> T deserialize(String json, Type typeOfT){
        return mGson.fromJson(json,typeOfT);
    }

    public <T> T deserialize(JsonElement json, Class<T> classOfT){
        return mGson.fromJson(json,classOfT);
    }

    public  <T extends Object> List<T> getJsonList(String json, Class<T> clazz) {
        try {
            if (null != json && !"".equalsIgnoreCase(json)) {
                List<T> resutList = new LinkedList<>();
                java.lang.reflect.Type type = new TypeToken<ArrayList<JsonObject>>() {}.getType();
                ArrayList<JsonObject> jsonObjs = deserialize(json, type);
                for (JsonObject jsonObj : jsonObjs) {
                    resutList.add(deserialize(jsonObj, clazz));
                }
                return resutList;
            }else{
                return null;
            }

        } catch (Exception e) {
            return null;
        }
    }
}
