package com.winning.mars_generator.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by yuzhijun on 2018/03/28.
 */
public class IoUtil {
    public static final int DEFAULT_BUFFER_SIZE = 32768;

    private IoUtil() {
    }

    public static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException var2) {
            }
        }
    }
}
