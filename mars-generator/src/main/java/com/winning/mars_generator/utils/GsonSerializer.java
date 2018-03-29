package com.winning.mars_generator.utils;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;

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
}
