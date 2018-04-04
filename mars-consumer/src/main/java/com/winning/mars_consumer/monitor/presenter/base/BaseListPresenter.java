package com.winning.mars_consumer.monitor.presenter.base;

import android.net.Uri;

import java.util.Collection;

/**
 * Created by yuzhijun on 2018/4/4.
 */

public abstract class BaseListPresenter<T> implements Presenter {
    @Override
    public byte[] process(Uri uri) throws Throwable {
        Collection<T> t = generateData();
        if (t == null) {
            return new ResultWrapper("no data for " + getClass().getSimpleName()).toBytes();
        }
        return new ResultWrapper<>(t).toBytes();
    }

    protected abstract Collection<T> generateData();
}
