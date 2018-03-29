package com.winning.mars_generator.utils;

import java.io.Reader;

public interface Serializer {
    String serialize(Object o);

    <T> T deserialize(Reader reader, Class<T> clz);
}
