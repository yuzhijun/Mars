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
import com.winning.mars_generator.utils.tree.TreeHelper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Stack;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * delegate inflater factory
 * Created by yuzhijun on 2018/3/28.
 */
public class InflateEngine implements Engine {
    private Generator<InflateBean> mGenerator;
    private Generator<UserBehaviorBean> mNodeGenerator;
    private Context mContext;
    private Application.ActivityLifecycleCallbacks mActivityLifecycleCallbacks;
    private WeakHashMap<Activity, InflaterDelegateFactory> mInflaterDelegateMap;
    private ConcurrentHashMap<AppCompatActivity,List<View>> mViewHashMap;
    private Stack<Activity> mHandlePathStack;
    private Stack<InflateBean> mInflateBeanStack;
    private final Handler mHandler;

    public InflateEngine(Generator<InflateBean> generator, Generator<UserBehaviorBean> nodeGenerator, Context context) {
        this.mGenerator = generator;
        this.mNodeGenerator = nodeGenerator;
        this.mContext = context;
        mViewHashMap = new ConcurrentHashMap<>();
        mHandler = new Handler(Looper.getMainLooper());
        mHandlePathStack = new Stack<>();
        mInflateBeanStack = new Stack<>();
    }

    @Override
    public void launch() {
        if (null == mActivityLifecycleCallbacks){
            mActivityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle bundle) {
                    addToTree(activity);
                    /**
                     *  in order to get all views of current activity
                     *  so that we can calculate the depth of inflater
                     * */
                    hookInflater(activity);
                    /**
                     * in order to get pageLoad time
                     * */
                    doInflaterTime(activity);
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
                    //mock user handler
                    popStackAndGenData(activity);
                    //record inflate info
                    calculateInflateDepthAndGenData(activity);
                }
            };
        }
        ((Application)mContext).registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
    }

    private void calculateInflateDepthAndGenData(Activity activity) {
        if (null != mInflateBeanStack && mInflateBeanStack.size() > 0){
            int index = -1;
            for (int i = mInflateBeanStack.size() -1;i >= 0;i --){
                InflateBean inflateBean = mInflateBeanStack.get(i);
                if (activity.getClass().getSimpleName().equalsIgnoreCase(inflateBean.getActivity())){
                    index = i;
                }
            }
            if (-1 != index){
                final long destroyTime = System.currentTimeMillis();
                InflateBean inflateBean = mInflateBeanStack.get(index);
                inflateBean.setStayTime(destroyTime - inflateBean.getStartTime());

                //calculate the depth of inflater
                if (null != mViewHashMap && null != mViewHashMap.get(activity)
                        && mViewHashMap.get(activity).size() > 0){
                    long depth = calculateDepthInflater(mViewHashMap.get(activity));
                    inflateBean.setInflateDepth(depth);
                }
                mGenerator.generate(inflateBean.clone());
                mInflateBeanStack.remove(index);
            }
        }
    }

    private void popStackAndGenData(Activity activity) {
        if (null != mHandlePathStack){
            if (mHandlePathStack.size() > 1){
                int index = mHandlePathStack.lastIndexOf(activity);
                if (index != -1){
                    mHandlePathStack.remove(index);
                }
            }else {
                if (null != TreeHelper.getTree() && TreeHelper.getTree().size() > 0){
                    mHandlePathStack.clear();
                    mNodeGenerator.generate(new UserBehaviorBean(TreeHelper.getFinalTree()));
                }
            }
        }
    }

    private void doInflaterTime(Activity activity) {
        InflateBean inflateBean = new InflateBean();
        long time = System.currentTimeMillis();
        inflateBean.setActivity(activity.getClass().getSimpleName());
        inflateBean.setStartTime(time);
        mInflateBeanStack.push(inflateBean);

        calculateInflaterTime(activity, () -> {
            long inflateTime = System.currentTimeMillis() - time;
            mInflateBeanStack.peek().setInflateTime(inflateTime);
        });
    }

    private void hookInflater(Activity activity) {
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
    }

    private void addToTree(Activity activity) {
        //mock user handler
        if (null != mHandlePathStack){
            Activity topActivity = mHandlePathStack.size() > 0 ? mHandlePathStack.peek() : null;
            mHandlePathStack.push(activity);
            TreeHelper.addNode(topActivity, activity);
        }
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
