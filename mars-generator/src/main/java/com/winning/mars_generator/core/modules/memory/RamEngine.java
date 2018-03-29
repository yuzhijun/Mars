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

public class RamEngine implements Engine {
    private Generator<RamBean> mGenerator;
    private long mIntervalMillis;
    private Context mContext;
    private CompositeDisposable mCompositeDisposable;

    public RamEngine(Context context,Generator<RamBean> generator, long intervalMillis){
        this.mContext = context;
        this.mGenerator = generator;
        this.mIntervalMillis = intervalMillis;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void launch() {
        mCompositeDisposable.add(Observable.interval(mIntervalMillis, TimeUnit.MILLISECONDS).
                concatMap(new Function<Long, ObservableSource<RamBean>>() {
                    @Override
                    public ObservableSource<RamBean> apply(Long aLong) throws Exception {
                        return sample();
                    }
                }).subscribe(new Consumer<RamBean>() {
            @Override
            public void accept(RamBean ramBean) throws Exception {
                mGenerator.generate(ramBean);
            }
        }));
    }

    private ObservableSource<RamBean> sample() {
        return Observable.fromCallable(new Callable<RamBean>() {
            @Override
            public RamBean call() throws Exception {
                return MemoryUtil.getRamInfo(mContext);
            }
        });
    }

    @Override
    public void stop() {

    }
}
