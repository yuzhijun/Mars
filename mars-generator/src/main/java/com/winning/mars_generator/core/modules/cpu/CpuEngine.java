package com.winning.mars_generator.core.modules.cpu;


import com.winning.mars_generator.core.Engine;
import com.winning.mars_generator.core.Generator;
import com.winning.mars_generator.exception.MarsInvalidDataException;
import com.winning.mars_generator.utils.BaseUtility;
import com.winning.mars_generator.utils.LogUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * cpu data sampling engine
 * Created by yuzhijun on 2018/3/27.
 */
public class CpuEngine implements Engine {
    private Generator<CpuBean> mGenerator;
    private long mIntervalMillis;
    private long mSampleMillis;
    private CompositeDisposable mCompositeDisposable;


    public CpuEngine(Generator<CpuBean> mGenerator,long intervalMillis,long sampleMillis){
        this.mGenerator = mGenerator;
        this.mIntervalMillis = intervalMillis;
        this.mSampleMillis = sampleMillis;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void launch() {
        mCompositeDisposable.add(Observable.interval(mIntervalMillis, TimeUnit.MILLISECONDS).
                concatMap(new Function<Long, ObservableSource<CpuBean>>() {
                    @Override
                    public ObservableSource<CpuBean> apply(Long aLong) throws Exception {
                        BaseUtility.ensureWorkThread("CpuEngine");
                        return sample();
                    }
                }).subscribe(new Consumer<CpuBean>() {
            @Override
            public void accept(CpuBean cpuBean) throws Exception {
                mGenerator.generate(cpuBean);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LogUtil.e(String.valueOf(throwable));
            }
        }));
    }

    @Override
    public void stop() {
        mCompositeDisposable.dispose();
    }

    private ObservableSource<CpuBean> sample() {
        final CpuSnapshot startSnapshot = CpuSnapshot.snapshot();
        return Observable.timer(mSampleMillis, TimeUnit.MILLISECONDS).map(aLong -> {
            CpuSnapshot endSnapshot = CpuSnapshot.snapshot();
            float totalTime = (endSnapshot.total - startSnapshot.total) * 1.0f;
            if (totalTime <= 0) {
                throw new MarsInvalidDataException("TotalTime must greater than 0");
            }
            long idleTime = endSnapshot.idle - startSnapshot.idle;
            double totalRatio = (totalTime - idleTime) / totalTime;
            double appRatio = (endSnapshot.app - startSnapshot.app) / totalTime;
            double userRatio = (endSnapshot.user - startSnapshot.user) / totalTime;
            double systemRatio = (endSnapshot.system - startSnapshot.system) / totalTime;
            double ioWaitRatio = (endSnapshot.ioWait - startSnapshot.ioWait) / totalTime;
            if (!isValidRatios(totalRatio, appRatio, userRatio, systemRatio, ioWaitRatio)) {
                throw new MarsInvalidDataException("Invalid ratio");
            }
            return new CpuBean(totalRatio, appRatio, userRatio, systemRatio, ioWaitRatio);
        }).retryWhen(throwableObservable -> throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
             int maxRetries = 3;
             long retryDelayMillis = mSampleMillis;
             int retryCount = 0;
            @Override
            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                if (++retryCount < maxRetries){
                    return Observable.timer(retryDelayMillis,TimeUnit.MILLISECONDS);
                }
                return Observable.error(throwable);
            }
        }));
    }

    private boolean isValidRatios(Double... ratios) {
        for (double ratio : ratios) {
            if (ratio < 0 || ratio > 1) {
                return false;
            }
        }
        return true;
    }
}
