package com.winning.mars_consumer.monitor.uploader.network;

import com.winning.mars_consumer.MarsConsumer;
import com.winning.mars_generator.Mars;
import com.winning.mars_generator.core.modules.network.Network;
import com.winning.mars_generator.core.modules.network.NetworkBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;


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
                                long startTime = System.currentTimeMillis();
                                Observable<?> observable = (Observable<?>) method.invoke(mProxyObject, args);
                                long endTime = System.currentTimeMillis();
                                Mars.getInstance(MarsConsumer.mContext).getModule(Network.class).generate(new NetworkBean(startTime,endTime,0,url));
                                return observable;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    });
    }
}
