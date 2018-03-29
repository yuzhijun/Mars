package com.winning.mars_generator.core.modules.fps;

import android.content.Context;
import android.view.Choreographer;
import android.view.Display;
import android.view.WindowManager;

import com.winning.mars_generator.core.Engine;
import com.winning.mars_generator.core.Generator;
import com.winning.mars_generator.utils.BaseUtility;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by yuzhijun on 2018/3/28.
 */
public class FpsEngine implements Engine {
    private Context mContext;
    private Generator<FpsBean> mGenerator;
    private long mIntervalMillis;
    private CompositeDisposable mCompositeDisposable;
    private long mFrameIntervalNanos;
    private FpsBean mFpsBean;

    public FpsEngine(Context context, Generator<FpsBean> generator,long intervalMillis){
        this.mContext = context;
        this.mGenerator = generator;
        this.mIntervalMillis = intervalMillis;
        mCompositeDisposable = new CompositeDisposable();
        mFrameIntervalNanos = (long)(1000000000 / getRefreshRate(mContext));
    }

    @Override
    public void launch() {
        if (null == mFpsBean){
            mFpsBean = new FpsBean();
        }
        mCompositeDisposable.add(Observable.interval(mIntervalMillis, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).
                        concatMap(new Function<Long, ObservableSource<FpsBean>>() {
                            @Override
                            public ObservableSource<FpsBean> apply(Long aLong) throws Exception {
                                return sample();
                            }
                        }).subscribe(new Consumer<FpsBean>() {
                    @Override
                    public void accept(FpsBean fpsBean) throws Exception {
                        mGenerator.generate(fpsBean);
                    }
                })
        );
    }

    private ObservableSource<FpsBean> sample() {
        return Observable.create(new ObservableOnSubscribe<FpsBean>() {
            @Override
            public void subscribe(final ObservableEmitter<FpsBean> e) throws Exception {
                BaseUtility.ensureWorkThread("fpsEngine");
                final float systemRate = getRefreshRate(mContext);
                final Choreographer choreographer = Choreographer.getInstance();
                choreographer.postFrameCallback(new Choreographer.FrameCallback() {
                    @Override
                    public void doFrame(long frameTimeNanos) {
                        final long startTimeNanos = frameTimeNanos;
                        choreographer.postFrameCallback(new Choreographer.FrameCallback() {
                            @Override
                            public void doFrame(long frameTimeNanos) {
                                long frameInterval = frameTimeNanos - startTimeNanos;
                                long fps = 1000000000 / frameInterval;
                                if (fps > mFrameIntervalNanos){
                                    final long skippedFrames = fps / mFrameIntervalNanos;
                                    mFpsBean.setSkipFrame(skippedFrames);
                                }
                                mFpsBean.setCurrentFps((int) Math.min(fps,systemRate));
                                mFpsBean.setSystemFps((int) systemRate);
                                e.onNext(mFpsBean);
                                e.onComplete();
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void stop() {
        mCompositeDisposable.dispose();
    }

    private static float getRefreshRate(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getRefreshRate();
    }
}
