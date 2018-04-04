package com.winning.mars_consumer.monitor.uploader.network;

import com.google.gson.JsonParseException;
import com.winning.mars_consumer.MarsConsumer;
import com.winning.mars_consumer.R;
import com.winning.mars_generator.Mars;
import com.winning.mars_generator.core.modules.network.Network;
import com.winning.mars_generator.core.modules.network.NetworkBean;
import com.winning.mars_generator.utils.BaseUtility;
import com.winning.mars_generator.utils.GsonSerializer;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

import static com.winning.mars_consumer.utils.Constants.HttpCode.HTTP_NETWORK_ERROR;
import static com.winning.mars_consumer.utils.Constants.HttpCode.HTTP_SERVER_ERROR;
import static com.winning.mars_consumer.utils.Constants.HttpCode.HTTP_UNAUTHORIZED;
import static com.winning.mars_consumer.utils.Constants.HttpCode.HTTP_UNKNOWN_ERROR;

public class ResponseErrorProxy implements InvocationHandler {
    public static final String TAG = ResponseErrorProxy.class.getSimpleName();

    private Object mProxyObject;
    private String url;

    public ResponseErrorProxy(Object proxyObject,String url) {
        mProxyObject = proxyObject;
        this.url = url;
    }

    @Override
    public Object invoke(Object proxy, final Method method, final Object[] args) {
            return Observable.just(null)
                    .flatMap(new Function<Object, ObservableSource<?>>() {
                        @Override
                        public ObservableSource<?> apply(Object o) throws Exception {
                            try {
                                final long startTime = System.currentTimeMillis();
                                Observable<?> observable = (Observable<?>) method.invoke(mProxyObject, args);
                                observable.map(new Function<Object, Object>() {
                                    @Override
                                    public Object apply(Object o)  {
                                        try {
                                            int respBodySizeByte = sizeOfObject(o);
                                            long endTime = System.currentTimeMillis();
                                            Mars.getInstance(MarsConsumer.mContext).getModule(Network.class).generate(new NetworkBean(startTime,endTime,respBodySizeByte,url));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        return o;
                                    }
                                });
                                return observable;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }).retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                        @Override
                        public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                            return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                                @Override
                                public Observable<?> apply(Throwable throwable) {
                                    ResponseError error = null;
                                    if (throwable instanceof ConnectTimeoutException
                                            || throwable instanceof SocketTimeoutException
                                            || throwable instanceof UnknownHostException
                                            || throwable instanceof ConnectException) {
                                        error = new ResponseError(HTTP_NETWORK_ERROR,
                                                BaseUtility.getString(MarsConsumer.mContext,R.string.toast_error_network));
                                    } else if (throwable instanceof retrofit2.HttpException) {
                                        retrofit2.HttpException exception = (retrofit2.HttpException) throwable;
                                        try {
                                            error = new GsonSerializer().deserialize(
                                                    exception.response().errorBody().string(), ResponseError.class);
                                        } catch (Exception e) {
                                            if (e instanceof JsonParseException) {
                                                error = new ResponseError(HTTP_SERVER_ERROR,
                                                        BaseUtility.getString(MarsConsumer.mContext,R.string.toast_error_server));
                                            } else {
                                                error = new ResponseError(HTTP_UNKNOWN_ERROR,
                                                        BaseUtility.getString(MarsConsumer.mContext,R.string.toast_error_unknown));
                                            }
                                        }
                                    } else if (throwable instanceof JsonParseException) {
                                        error = new ResponseError(HTTP_SERVER_ERROR,
                                                BaseUtility.getString(MarsConsumer.mContext,R.string.toast_error_server));
                                    } else {
                                        error = new ResponseError(HTTP_UNKNOWN_ERROR,
                                                BaseUtility.getString(MarsConsumer.mContext,R.string.toast_error_unknown));
                                    }

                                    if (error.getStatus() == HTTP_UNAUTHORIZED) {
                                        return refreshTokenWhenTokenInvalid();
                                    } else {
                                        return Observable.error(error);
                                    }
                                }
                            });
                        }
                    });
    }

    /**
     * calculate the size of object
     * @param o object to calculate
     * @return int size
     * */
    private  int sizeOfObject(Object o) throws IOException{
        if (null == o){
            return 0;
        }
        ByteArrayOutputStream buff = new ByteArrayOutputStream(4094);
        ObjectOutputStream outputStream = new ObjectOutputStream(buff);
        outputStream.writeObject(o);
        outputStream.flush();
        outputStream.close();
        return buff.size();
    }

    private Observable<?> refreshTokenWhenTokenInvalid() {
        synchronized (ResponseErrorProxy.class) {
            return Observable.error(new ResponseError(HTTP_SERVER_ERROR,
                    BaseUtility.getString(MarsConsumer.mContext,R.string.toast_error_server)));
        }
    }
}
