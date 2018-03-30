package com.winning.mars_generator.core.modules.thread.deadlock;

import android.content.Context;

import com.winning.mars_generator.Mars;
import com.winning.mars_generator.core.Engine;
import com.winning.mars_generator.core.Generator;
import com.winning.mars_generator.core.modules.thread.ThreadDump;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yuzhijun on 2018/3/30.
 */

public class DeadLockEngine implements Engine {
    private Generator<List<Thread>> mGenerator;
    private long mIntervalMillis;
    private Context mContext;
    private CompositeDisposable mCompositeDisposable;

    public DeadLockEngine(Context context,Generator<List<Thread>> generator, long intervalMillis){
        this.mContext = context;
        this.mGenerator = generator;
        this.mIntervalMillis = intervalMillis;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void launch() {
        mCompositeDisposable.add(Mars.getInstance(mContext).getModule(ThreadDump.class).subject().sample(mIntervalMillis, TimeUnit.MILLISECONDS).map(new Function<List<Thread>, List<Thread>>() {
            @Override
            public List<Thread> apply(List<Thread> threadInfos) throws Exception {
                List<Thread> results = new ArrayList<>();
                for (Thread ti : threadInfos) {
                    if (!filter(ti)) {
                        continue;
                    }
                    if (Thread.State.BLOCKED.equals(ti.getState()) || Thread.State.WAITING.equals(ti.getState())) {
                        results.add(ti);
                    }
                }
                return results;
            }
        }).subscribeOn(Schedulers.newThread()).subscribe(new Consumer<List<Thread>>() {
            @Override
            public void accept(List<Thread> threads) throws Exception {
                List<Thread> deadLockThreads = detect(threads);
                if (deadLockThreads == null || deadLockThreads.isEmpty()) {
                    return;
                }
                mGenerator.generate(deadLockThreads);
            }
        }));
    }

    @Override
    public void stop() {
        mCompositeDisposable.dispose();
    }

    private long mLastDetectTime;
    private List<Thread> mLastThreadInfos;

    /**
     * detect
     * @param threadInfos
     * @return if one thread block and waiting at same stack trace，suspicious deadlock
     */
    public List<Thread> detect(List<Thread> threadInfos) {
        List<Thread> blockThreads = new ArrayList<>();
        if (mLastThreadInfos != null && !mLastThreadInfos.isEmpty()
                && mLastDetectTime > 0
                && threadInfos != null && !threadInfos.isEmpty()) {
            for (Thread lti : mLastThreadInfos) {
                for (Thread ti : threadInfos) {
                    if (isBlockAtOneLine(lti, ti)) {
                        blockThreads.add(ti);
                    }
                }
            }
        }
        mLastDetectTime = System.currentTimeMillis();
        mLastThreadInfos = threadInfos;
        return blockThreads;
    }

    /**
     * two thread at the same stack trace
     * @param lti
     * @param rti
     * @return
     */
    private boolean isBlockAtOneLine(Thread lti, Thread rti) {
        if (lti.getId() != rti.getId()) {
            return false;
        }
        if ((Thread.State.BLOCKED.equals(lti.getState()) && Thread.State.BLOCKED.equals(rti.getState()))
                || (Thread.State.WAITING.equals(lti.getState()) && Thread.State.WAITING.equals(rti.getState()))) {
            StackTraceElement[] lElements = lti.getStackTrace();
            StackTraceElement[] rElements = rti.getStackTrace();
            if (lElements != null && lElements.length > 0
                    && rElements != null && rElements.length > 0
                    && isArrayEqual(lElements, rElements)) {
                return true;
            }
        }
        return false;
    }

    private boolean isArrayEqual(Object[] l, Object[] r) {
        if (l.length != r.length) {
            return false;
        }
        final int size = l.length;
        for (int i = 0; i < size; i++) {
            if (!l[i].equals(r[i])) {
                return false;
            }
        }
        return true;
    }

    public boolean filter(Thread thread) {
        if (thread == null) {
            return false;
        }
        if ("LeakCanary-File-IO".equals(thread.getName())) {
            return false;
        }
        if (thread.getThreadGroup() == null) {
            return true;
        }
        if ("system".equals(thread.getThreadGroup().getName())) {
            return false;
        }
        return true;
    }
}
