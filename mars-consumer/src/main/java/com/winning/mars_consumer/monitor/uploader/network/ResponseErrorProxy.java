package com.winning.mars_consumer.monitor.uploader.network;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.winning.mars_consumer.MarsConsumer;
import com.winning.mars_generator.Mars;
import com.winning.mars_generator.core.modules.network.Network;
import com.winning.mars_generator.core.modules.network.NetworkBean;

import org.apache.http.conn.ConnectTimeoutException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import retrofit2.HttpException;
import retrofit2.http.GET;
import retrofit2.http.POST;

import static com.winning.mars_consumer.utils.Constants.HttpCode.HTTP_NETWORK_ERROR;
import static com.winning.mars_consumer.utils.Constants.HttpCode.HTTP_SERVER_ERROR;
import static com.winning.mars_consumer.utils.Constants.HttpCode.HTTP_UNAUTHORIZED;
import static com.winning.mars_consumer.utils.Constants.HttpCode.HTTP_UNKNOWN_ERROR;

public class ResponseErrorProxy implements InvocationHandler {
    public static final String TAG = ResponseErrorProxy.class.getSimpleName();
    private Gson mGson = new Gson();
    private Object mProxyObject;
    private String url;
    private long startTime;

    public ResponseErrorProxy(Object proxyObject, String url) {
        mProxyObject = proxyObject;
        this.url = url;
    }

    @Override
    public Object invoke(Object proxy, final Method method, final Object[] args) {
           return Flowable.just("")
                   .flatMap((Function<String, Flowable<?>>) s -> {
                       startTime = System.currentTimeMillis();
                       Flowable<?> flowable =  (Flowable<?>) method.invoke(mProxyObject, args);
                       return flowable.map((Function<Object, Object>) o -> {
                                try {
                                    generateNetworkData(method, args, startTime, o, "");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return o;
                       });
                   })
                    .retryWhen(throwableFlowable -> throwableFlowable.flatMap((Function<Throwable, Flowable<?>>) throwable -> {
                        ResponseError error = null;
                        generateNetworkData(method, args, startTime,null, throwable.getMessage());
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

    private void generateNetworkData(Method method, Object[] args, long startTime, Object o, String error) {
        GET getAnnotation = method.getAnnotation(GET.class);
        POST postAnnotation = method.getAnnotation(POST.class);
        String subUrl = null != getAnnotation ? getAnnotation.value() : null != postAnnotation ? postAnnotation.value() : "";
        int respBodySizeByte = null != o ?  mGson.toJson(o).length() : 0;
        long endTime = System.currentTimeMillis();
        Mars.getInstance(MarsConsumer.mContext).getModule(Network.class).generate(new NetworkBean(startTime,endTime,respBodySizeByte,url+subUrl,args , error));
    }

    private Flowable<?> refreshTokenWhenTokenInvalid() {
        synchronized (ResponseErrorProxy.class) {
            return Flowable.error(new ResponseError(HTTP_SERVER_ERROR, "抱歉！服务器出错了!"));
        }
    }
}
