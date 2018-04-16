package com.winning.mars_consumer.monitor.uploader.network;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import org.apache.http.conn.ConnectTimeoutException;
import org.reactivestreams.Publisher;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import retrofit2.HttpException;

import static com.winning.mars_consumer.utils.Constants.HttpCode.HTTP_NETWORK_ERROR;
import static com.winning.mars_consumer.utils.Constants.HttpCode.HTTP_SERVER_ERROR;
import static com.winning.mars_consumer.utils.Constants.HttpCode.HTTP_UNAUTHORIZED;
import static com.winning.mars_consumer.utils.Constants.HttpCode.HTTP_UNKNOWN_ERROR;

public class ResponseErrorProxy implements InvocationHandler {
    public static final String TAG = ResponseErrorProxy.class.getSimpleName();

    private Object mProxyObject;
    private String url;

    public ResponseErrorProxy(Object proxyObject, String url) {
        mProxyObject = proxyObject;
        this.url = url;
    }

    @Override
    public Object invoke(Object proxy, final Method method, final Object[] args) {
           return Flowable.just("")
                   .flatMap((Function<String, Flowable<?>>) s -> (Flowable<?>) method.invoke(mProxyObject, args))
                    .retryWhen(throwableFlowable -> throwableFlowable.flatMap((Function<Throwable, Publisher<?>>) throwable -> {
                        ResponseError error = null;
                        if (throwable instanceof ConnectTimeoutException
                                || throwable instanceof SocketTimeoutException
                                || throwable instanceof UnknownHostException
                                || throwable instanceof ConnectException) {
                            error = new ResponseError(HTTP_NETWORK_ERROR, "当前网络环境较差，请稍后重试!");
                        } else if (throwable instanceof HttpException) {
                            HttpException exception = (HttpException) throwable;
                            try {
                                error = new Gson().fromJson(exception.response().errorBody().string(), ResponseError.class);
                            } catch (Exception e) {
                                if (e instanceof JsonParseException) {
                                    error = new ResponseError(HTTP_SERVER_ERROR, "抱歉！服务器出错了!");
                                } else {
                                    error = new ResponseError(HTTP_UNKNOWN_ERROR, "抱歉！系统出现未知错误!");
                                }
                            }
                        } else if (throwable instanceof JsonParseException) {
                            error = new ResponseError(HTTP_SERVER_ERROR, "抱歉！服务器出错了!");
                        } else {
                            error = new ResponseError(HTTP_UNKNOWN_ERROR, "抱歉！系统出现未知错误!");
                        }

                        if (error.getStatus() == HTTP_UNAUTHORIZED) {
                            return refreshTokenWhenTokenInvalid();
                        } else {
                            return Flowable.error(error);
                        }
                    }));
    }

    private Flowable<?> refreshTokenWhenTokenInvalid() {
        synchronized (ResponseErrorProxy.class) {
            return Flowable.error(new ResponseError(HTTP_SERVER_ERROR, "抱歉！服务器出错了!"));
        }
    }
}
