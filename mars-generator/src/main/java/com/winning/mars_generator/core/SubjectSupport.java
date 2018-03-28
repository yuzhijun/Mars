package com.winning.mars_generator.core;

import io.reactivex.Observable;

/**
 * Created by yuzhijun on 2018/3/27.
 */
public interface SubjectSupport<T> {
    Observable<T> subject();
}
