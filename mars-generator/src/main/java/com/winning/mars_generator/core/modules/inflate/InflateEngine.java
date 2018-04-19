package com.winning.mars_generator.core.modules.inflate;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.winning.mars_generator.core.Engine;
import com.winning.mars_generator.core.Generator;

import java.lang.reflect.Field;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * delegate inflater factory
 * Created by yuzhijun on 2018/3/28.
 */
public class InflateEngine implements Engine {
    private Generator<InflateBean> mGenerator;
    private Context mContext;
    private Application.ActivityLifecycleCallbacks mActivityLifecycleCallbacks;
    private WeakHashMap<Activity, InflaterDelegateFactory> mInflaterDelegateMap;
    private ConcurrentHashMap<AppCompatActivity,List<View>> mViewHashMap;
    private InflateBean mInflateBean;
    private final Handler mHandler;
    private long time;

    public InflateEngine(Generator<InflateBean> generator, Context context) {
        this.mGenerator = generator;
        this.mContext = context;
        mViewHashMap = new ConcurrentHashMap<>();
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void launch() {
        if (null == mInflateBean){
            mInflateBean = new InflateBean();
        }
        if (null == mActivityLifecycleCallbacks){
            mActivityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle bundle) {
                    /**
                     *  in order to get all views of current activity
                     *  so that we can calculate the depth of inflater
                     * */
                    if (activity instanceof AppCompatActivity){
                        LayoutInflater layoutInflater = activity.getLayoutInflater();
                        try {
                            Field field = LayoutInflater.class.getDeclaredField("mFactorySet");
                            field.setAccessible(true);
                            field.setBoolean(layoutInflater, false);
                            LayoutInflaterCompat.setFactory(activity.getLayoutInflater(), getInflaterDelegate(InflateEngine.this,(AppCompatActivity) activity));
                        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    /**
                     * in order to get pageLoad time
                     * */
                   time = System.currentTimeMillis();
                    calculateInflaterTime(activity, new OnViewInflated() {
                        @Override
                        public void didInflated() {
                            long inflateTime = System.currentTimeMillis() - time;
                            mInflateBean.setInflateTime(inflateTime);
                        }
                    });
                }
                @Override
                public void onActivityStarted(Activity activity) {

                }

                @Override
                public void onActivityResumed(Activity activity) {

                }

                @Override
                public void onActivityPaused(Activity activity) {

                }

                @Override
                public void onActivityStopped(Activity activity) {

                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    final long destroyTime = System.currentTimeMillis();
                    mInflateBean.setStayTime(destroyTime - time);
                    //calculate the depth of inflater
                    if (null != mViewHashMap && null != mViewHashMap.get(activity)
                            && mViewHashMap.get(activity).size() > 0){
                        long depth = calculateDepthInflater(mViewHashMap.get(activity));
                        mInflateBean.setInflateDepth(depth);
                        mInflateBean.setActivity(activity.getClass().getSimpleName());
                    }
                    mGenerator.generate(mInflateBean);
                }
            };
        }
        ((Application)mContext).registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
    }

    @Override
    public void stop() {
        ((Application)mContext).unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
        mActivityLifecycleCallbacks = null;
        mInflaterDelegateMap = null;
    }

    private InflaterDelegateFactory getInflaterDelegate(InflateEngine engine,AppCompatActivity activity) {
        if (mInflaterDelegateMap == null) {
            mInflaterDelegateMap = new WeakHashMap<>();
        }

        InflaterDelegateFactory mInflaterDelegate = mInflaterDelegateMap.get(activity);
        if (mInflaterDelegate == null) {
            mInflaterDelegate = InflaterDelegateFactory.create(engine,activity);
        }
        mInflaterDelegateMap.put(activity, mInflaterDelegate);
        return mInflaterDelegate;
    }

    public ConcurrentHashMap<AppCompatActivity, List<View>> getViewHashMap() {
        return mViewHashMap;
    }

    /**
     * calculate max inflater depth
     * @param views all views
     * @Return long maxDepth
     * */
    private long calculateDepthInflater(List<View> views){
        long maxDepth = 0;
        long viewDepth;
        for (View view : views){
            viewDepth = 0;
            do {
                if (view instanceof CoordinatorLayout){
                    if (viewDepth > maxDepth){
                        maxDepth = viewDepth;
                    }
                   break;
                }else if (view instanceof FrameLayout){
                    if (view.getId() == android.R.id.content){
                        if (viewDepth > maxDepth){
                            maxDepth = viewDepth;
                        }
                        break;
                    }else{
                        break;
                    }
                }

                if (view != null) {
                    final ViewParent parent = view.getParent();
                    view = parent instanceof View ? (View) parent : null;
                }
                viewDepth ++;
            } while (view != null);
        }
        return maxDepth;
    }

    public interface OnViewInflated {
        void didInflated();
    }
    /**
     * calculate time
     * @param activity
     * @param onViewInflated
     * */
    private void calculateInflaterTime(final Activity activity, final OnViewInflated onViewInflated) {
        activity.getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (onViewInflated != null) {
                            onViewInflated.didInflated();
                        }
                    }
                });
            }
        });
    }
}
