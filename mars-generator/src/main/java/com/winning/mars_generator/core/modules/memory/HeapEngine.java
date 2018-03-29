package com.winning.mars_generator.core.modules.memory;

import com.winning.mars_generator.core.Engine;
import com.winning.mars_generator.core.Generator;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by yuzhijun on 2018/3/29.
 */
public class HeapEngine implements Engine {
    private Generator<HeapBean> mGenerator;
    private long mIntervalMillis;
    private CompositeDisposable mCompositeDisposable;

    public HeapEngine(Generator<HeapBean> generator,long intervalMillis){
        this.mGenerator = generator;
        this.mIntervalMillis = intervalMillis;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void launch() {
        mCompositeDisposable.add(Observable.interval(mIntervalMillis, TimeUnit.MILLISECONDS).
                concatMap(new Function<Long, ObservableSource<HeapBean>>() {
                    @Override
                    public ObservableSource<HeapBean> apply(Long aLong) throws Exception {
                        return sample();
                    }
                }).subscribe(new Consumer<HeapBean>() {
            @Override
            public void accept(HeapBean heapBean) throws Exception {
                mGenerator.generate(heapBean);
            }
        }));
    }

    private ObservableSource<HeapBean> sample() {
        return Observable.fromCallable(new Callable<HeapBean>() {
            @Override
            public HeapBean call() throws Exception {
                return MemoryUtil.getAppHeapInfo();
            }
        });
    }

    @Override
    public void stop() {
        mCompositeDisposable.dispose();
    }
}
