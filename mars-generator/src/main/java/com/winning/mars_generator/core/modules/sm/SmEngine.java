package com.winning.mars_generator.core.modules.sm;

import android.content.Context;
import android.os.Looper;

import com.winning.mars_generator.core.Generator;
import com.winning.mars_generator.core.modules.cpu.CpuBean;
import com.winning.mars_generator.core.modules.memory.MemoryUtil;
import com.winning.mars_generator.core.modules.sm.blockCanary.BlockInterceptor;
import com.winning.mars_generator.core.modules.sm.blockCanary.CpuSampler;
import com.winning.mars_generator.core.modules.sm.blockCanary.HandlerThreadFactory;
import com.winning.mars_generator.core.modules.sm.blockCanary.LongBlockBean;
import com.winning.mars_generator.core.modules.sm.blockCanary.LooperMonitor;
import com.winning.mars_generator.core.modules.sm.blockCanary.MemoryBean;
import com.winning.mars_generator.core.modules.sm.blockCanary.StackSampler;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * Created by yuzhijun on 2018/3/29.
 */

public class SmEngine  {
    private Context mContext;
    private Generator<SmBean> mGenerator;
    private long mLongBlockThreshold;
    private long mShortBlockThreshold;
    private long mDumpInterval;
    private StackSampler mStackSampler;
    private CpuSampler mCpuSampler;
    private LooperMonitor mMonitor;
    private List<BlockInterceptor> mInterceptorChain = new LinkedList<>();

    public SmEngine(final Context context, Generator<SmBean> generator, long longBlockThreshold, long shortBlockThreshold, long dumpInterval){
        this.mContext = context;
        this.mGenerator = generator;
        this.mLongBlockThreshold = longBlockThreshold;
        this.mShortBlockThreshold = shortBlockThreshold;
        this.mDumpInterval = dumpInterval;

        this.mStackSampler = new StackSampler(Looper.getMainLooper().getThread(), dumpInterval);
        this.mCpuSampler = new CpuSampler(dumpInterval);
        this.mMonitor = new LooperMonitor(new LooperMonitor.BlockListener() {

            @Override
            public void onEventStart(long startTime) {
                startDump();
            }

            @Override
            public void onEventEnd(long endTime) {
                stopDump();
            }

            @Override
            public void onBlockEvent(final long blockTimeMillis, final long threadBlockTimeMillis, final boolean longBlock, final long eventStartTimeMilliis, final long eventEndTimeMillis, long longBlockThresholdMillis, long shortBlockThresholdMillis) {
                HandlerThreadFactory.getObtainDumpThreadHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (!longBlock) {//is short block
                            if (!mInterceptorChain.isEmpty()) {
                                for (BlockInterceptor interceptor : mInterceptorChain) {
                                    interceptor.onShortBlock(context, blockTimeMillis);
                                }
                            }
                            return;
                        }
                        //if long block, should get cpu info and stack trace info
                        final boolean cpuBusy = mCpuSampler.isCpuBusy(eventStartTimeMilliis, eventEndTimeMillis);
                        //这里短卡顿基本是dump不到数据的，因为dump延时一般都会比短卡顿时间久
                        final List<CpuBean> cpuInfos = mCpuSampler.getCpuRateInfo(eventStartTimeMilliis, eventEndTimeMillis);
                        final Map<Long, List<StackTraceElement>> threadStackEntries = mStackSampler.getThreadStackEntries(eventStartTimeMilliis, eventEndTimeMillis);
                        Observable.fromCallable(new Callable<MemoryBean>() {
                            @Override
                            public MemoryBean call() throws Exception {
                                return new MemoryBean(MemoryUtil.getAppHeapInfo(), MemoryUtil.getAppPssInfo(mContext), MemoryUtil.getRamInfo(mContext));
                            }
                        }).subscribe(new Consumer<MemoryBean>() {
                            @Override
                            public void accept(MemoryBean memoryInfo) throws Exception {
                                LongBlockBean blockBaseinfo = LongBlockBean.create(eventStartTimeMilliis, eventEndTimeMillis, threadBlockTimeMillis,
                                        blockTimeMillis, cpuBusy, cpuInfos, threadStackEntries, memoryInfo);
                                if (!mInterceptorChain.isEmpty()) {
                                    for (BlockInterceptor interceptor : mInterceptorChain) {
                                        interceptor.onLongBlock(context, blockBaseinfo);
                                    }
                                }
                            }
                        });
                    }
                });
            }
        }, longBlockThreshold, shortBlockThreshold);
    }

    public void install() {
        Looper.getMainLooper().setMessageLogging(mMonitor);
    }

    public void uninstall() {
        Looper.getMainLooper().setMessageLogging(null);
        stopDump();
    }

    private void startDump() {
        if (null != mStackSampler) {
            mStackSampler.start();
        }
        if (null != mCpuSampler) {
            mCpuSampler.start();
        }
    }

    private void stopDump() {
        if (null != mStackSampler) {
            mStackSampler.stop();
        }
        if (null != mCpuSampler) {
            mCpuSampler.stop();
        }
    }

    public void addBlockInterceptor(BlockInterceptor blockInterceptor) {
        mInterceptorChain.add(blockInterceptor);
    }

    public long getSampleDelay() {
        return (long) (this.mLongBlockThreshold * 0.8f);
    }
}
