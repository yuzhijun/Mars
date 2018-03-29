package com.winning.mars_generator.core.modules.memory;

import android.content.Context;

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

public class PssEngine implements Engine {
    private Generator<PssBean> mGenerator;
    private long mIntervalMillis;
    private Context mContext;
    private CompositeDisposable mCompositeDisposable;

    public PssEngine(Context context, Generator<PssBean> generator, long intervalMillis){
        this.mContext = context;
        this.mGenerator = generator;
        this.mIntervalMillis = intervalMillis;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void launch() {
        mCompositeDisposable.add(Observable.interval(mIntervalMillis, TimeUnit.MILLISECONDS).
                concatMap(new Function<Long, ObservableSource<PssBean>>() {
                    @Override
                    public ObservableSource<PssBean> apply(Long aLong) throws Exception {
                        return sample();
                    }
                }).subscribe(new Consumer<PssBean>() {
            @Override
            public void accept(PssBean pssBean) throws Exception {
                mGenerator.generate(pssBean);
            }
        }));
    }

    private ObservableSource<PssBean> sample() {
        return Observable.fromCallable(new Callable<PssBean>() {
            @Override
            public PssBean call() throws Exception {
                return MemoryUtil.getAppPssInfo(mContext);
            }
        });
    }

    @Override
    public void stop() {
        mCompositeDisposable.dispose();
    }
}
