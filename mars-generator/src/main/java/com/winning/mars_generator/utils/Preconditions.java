package com.winning.mars_generator.utils;

public class Preconditions {
    public static <T> T checkNotNull(T instance) {
        if (instance == null) {
            throw new NullPointerException("must not be null");
        } else {
            return instance;
        }
    }
}
