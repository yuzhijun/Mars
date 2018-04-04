package com.winning.mars_consumer.monitor.presenter.base;

import android.net.Uri;

import com.winning.mars_generator.utils.GsonSerializer;

import java.io.UnsupportedEncodingException;

/**
 * Created by yuzhijun on 2018/4/4.
 */

public interface Presenter {
    byte[] process(Uri uri) throws Throwable;

    class ResultWrapper<T> {
        public static final int SUCCESS = 1;
        public static final int DEFAULT_FAIL = 0;
        public T data;
        public int code;
        public String message;

        public ResultWrapper(int code, String message, T data) {
            this.data = data;
            this.code = code;
            this.message = message;
        }

        public ResultWrapper(String message) {
            this.data = null;
            this.code = DEFAULT_FAIL;
            this.message = message;
        }

        public ResultWrapper(T data) {
            this.data = data;
            this.code = SUCCESS;
            this.message = "success";
        }

        public byte[] toBytes() throws UnsupportedEncodingException {
            return new GsonSerializer().serialize(this).getBytes("UTF-8");
        }
    }
}
