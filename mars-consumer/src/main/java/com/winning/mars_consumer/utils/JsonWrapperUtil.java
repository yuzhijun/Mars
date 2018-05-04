package com.winning.mars_consumer.utils;

import com.winning.mars_generator.utils.GsonSerializer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by yuzhijun on 2018/4/10.
 */

public class JsonWrapperUtil {
    private static GsonSerializer mGsonSerializer = new GsonSerializer();

    public static JSONObject toJsonObject(String json){
        if (null != json && !"".equalsIgnoreCase(json)){
            try {
                JSONObject jsonObject = new JSONObject(json);
                return jsonObject;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static <T> JSONObject objectToJsonObject(T object){
        if (null != object){
            try {
                JSONObject jsonObject = new JSONObject(mGsonSerializer.serialize(object));
                return jsonObject;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static <T> JSONArray toJsonArray(String json,Class<T> tClass){
        if (null != json && !" ".equalsIgnoreCase(json)){
            try {
                List<JSONObject> jsonObjects = new ArrayList<>();
                List<T> list = mGsonSerializer.getJsonList(json,tClass);
                for (T item : list){
                    JSONObject jsonObject = new JSONObject(mGsonSerializer.serialize(item));
                    jsonObjects.add(jsonObject);
                }
                JSONArray jsonArray = new JSONArray(jsonObjects);
                return jsonArray;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static <T> JSONArray listToJsonArray(Collection<T> collection){
        if (null != collection && collection.size() > 0){
            try{
                List<JSONObject> jsonObjects = new ArrayList<>();
                for (T item : collection){
                    JSONObject jsonObject = new JSONObject(mGsonSerializer.serialize(item));
                    jsonObjects.add(jsonObject);
                }
                JSONArray jsonArray = new JSONArray(jsonObjects);
                return jsonArray;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }
}
