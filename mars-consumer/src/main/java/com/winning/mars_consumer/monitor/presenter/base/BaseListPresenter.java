package com.winning.mars_consumer.monitor.presenter.base;

import android.net.Uri;

import com.winning.mars_generator.utils.GsonSerializer;

import java.util.Collection;

/**
 * Created by yuzhijun on 2018/4/4.
 */

public abstract class BaseListPresenter<T> implements Presenter {
    GsonSerializer mGsonSerializer = new GsonSerializer();
    @Override
    public String process(Uri uri) throws Throwable {
        Collection<T> t = generateData();
        if (t == null) {
            return mGsonSerializer.serialize(new ResultWrapper("no data for " + getClass().getSimpleName()));
        }
        return mGsonSerializer.serialize(new ResultWrapper<>(t));
    }

    protected abstract Collection<T> generateData();
}
