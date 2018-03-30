package com.winning.mars_generator.core.modules.thread;

import com.winning.mars_generator.core.Engine;
import com.winning.mars_generator.core.Generator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by yuzhijun on 2018/3/30.
 */

public class ThreadEngine implements Engine {
    private Generator<List<Thread>> mGenerator;
    private long mIntervalMillis;
    private CompositeDisposable mCompositeDisposable;

    public ThreadEngine(Generator<List<Thread>> generator, long intervalMillis){
        this.mGenerator = generator;
        this.mIntervalMillis = intervalMillis;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void launch() {
        mCompositeDisposable.add(Observable.interval(mIntervalMillis, TimeUnit.MILLISECONDS).
                concatMap(new Function<Long, ObservableSource<List<Thread>>>() {
                    @Override
                    public ObservableSource<List<Thread>> apply(Long aLong) throws Exception {
                        return sample();
                    }
                }).subscribe(new Consumer<List<Thread>>() {
            @Override
            public void accept(List<Thread> threads) throws Exception {
                mGenerator.generate(threads);
            }
        }));
    }

    private ObservableSource<List<Thread>> sample() {
        return Observable.fromCallable(new Callable<List<Thread>>() {
            @Override
            public List<Thread> call() throws Exception {
                return dump();
            }
        });
    }

    public static List<Thread> dump() {
        ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
        if (rootGroup == null) {
            return new ArrayList<>();
        }
        ThreadGroup parentGroup;
        while ((parentGroup = rootGroup.getParent()) != null) {
            rootGroup = parentGroup;
        }
        Thread[] threads = new Thread[rootGroup.activeCount()];
        while (rootGroup.enumerate(threads, true) >= threads.length) {
            threads = new Thread[threads.length * 2];
        }
        List<Thread> threadList = new ArrayList<>();
        for (Thread thread : threads) {
            if (thread != null && !threadList.contains(thread) && filter(thread)) {
                threadList.add(thread);
            }
        }
        return threadList;
    }

    public static boolean filter(Thread thread) {
        if (thread == null) {
            return false;
        }
        if (thread.getThreadGroup() == null) {
            return true;
        }
        return !"system".equals(thread.getThreadGroup().getName());
    }

    @Override
    public void stop() {
        mCompositeDisposable.dispose();
    }
}
