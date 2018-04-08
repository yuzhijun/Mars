package com.winning.mars_consumer.monitor.presenter.base;

import android.net.Uri;

import com.winning.mars_generator.utils.GsonSerializer;

/**
 * Created by yuzhijun on 2018/4/4.
 */
public abstract class BasePresenter<T> implements Presenter {
    GsonSerializer mGsonSerializer = new GsonSerializer();
    @Override
    public String process(Uri uri) throws Throwable {
        T t = generateData();
        if (t == null) {
            return mGsonSerializer.serialize(new ResultWrapper("no data for " + getClass().getSimpleName()));
        }
        return mGsonSerializer.serialize(new ResultWrapper<>(t));
    }

    protected abstract T generateData();
}
