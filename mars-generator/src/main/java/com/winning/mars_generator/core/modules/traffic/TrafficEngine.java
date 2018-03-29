package com.winning.mars_generator.core.modules.traffic;

import android.content.Context;

import com.winning.mars_generator.core.Engine;
import com.winning.mars_generator.core.Generator;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by yuzhijun on 2018/3/29.
 */
public class TrafficEngine implements Engine {
    private Context mContext;
    private Generator<TrafficBean> mGenerator;
    private long mIntervalMillis;
    private long mSampleMillis;
    private CompositeDisposable mCompositeDisposable;

    public TrafficEngine(Context context, Generator<TrafficBean> generator,long intervalMillis,long sampleMillis){
        this.mContext = context;
        this.mGenerator = generator;
        this.mIntervalMillis = intervalMillis;
        this.mSampleMillis = sampleMillis;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void launch() {
        mCompositeDisposable.add(Observable.interval(mIntervalMillis, TimeUnit.MILLISECONDS).
                concatMap(new Function<Long, ObservableSource<TrafficBean>>() {
                    @Override
                    public ObservableSource<TrafficBean> apply(Long aLong) throws Exception {
                        return sample();
                    }
                }).subscribe(new Consumer<TrafficBean>() {
            @Override
            public void accept(TrafficBean trafficBean) throws Exception {
                mGenerator.generate(trafficBean);
            }
        }));
    }

    private ObservableSource<TrafficBean> sample() {
        final TrafficSnapshot start = TrafficSnapshot.snapshot();
        return Observable.timer(mSampleMillis, TimeUnit.MILLISECONDS).map(new Function<Long, TrafficBean>() {
            @Override
            public TrafficBean apply(Long aLong) throws Exception {
                TrafficSnapshot endTrafficSnapshot = TrafficSnapshot.snapshot();
                TrafficBean trafficBean = new TrafficBean();
                trafficBean.rxTotalRate = (endTrafficSnapshot.rxTotalKB - start.rxTotalKB) * 1000 / mSampleMillis;
                trafficBean.txTotalRate = (endTrafficSnapshot.txTotalKB - start.txTotalKB) * 1000 / mSampleMillis;
                trafficBean.rxUidRate = (endTrafficSnapshot.rxUidKB - start.rxUidKB) * 1000 / mSampleMillis;
                trafficBean.txUidRate = (endTrafficSnapshot.txUidKB - start.txUidKB) * 1000 / mSampleMillis;
                return trafficBean;
            }
        });
    }

    @Override
    public void stop() {
        mCompositeDisposable.dispose();
    }
}
