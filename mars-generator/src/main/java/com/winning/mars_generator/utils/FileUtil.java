package com.winning.mars_generator.utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by yuzhijun on 2018/3/27.
 */
public class FileUtil {
    public static class FileException extends Exception {
        public FileException(String msg, Throwable e) {
            super(msg, e);
        }

        public FileException(String msg) {
            super(msg);
        }

        public FileException(Throwable e) {
            super(e);
        }
    }

    /**
     * Delete file
     * @param file
     * @throws IOException
     */
    public static void deleteIfExists(File file) throws FileException {
        if (file.exists() && !file.delete()) {
            throw new FileException("deleteIfExists failed");
        }
    }
}
