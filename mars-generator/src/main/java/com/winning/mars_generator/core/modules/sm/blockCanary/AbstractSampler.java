package com.winning.mars_generator.core.modules.sm.blockCanary;

import com.winning.mars_generator.core.modules.sm.Sm;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * sampling every interval
 */
public abstract class AbstractSampler {
    private static final int DEFAULT_SAMPLE_INTERVAL = 300;
    protected AtomicBoolean mShouldSample = new AtomicBoolean(false);
    //sample every interval
    protected long mSampleInterval;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doSample();

            if (mShouldSample.get()) {
                HandlerThreadFactory.getDoDumpThreadHandler()
                        .postDelayed(mRunnable, mSampleInterval);
            }
        }
    };

    public AbstractSampler(long sampleInterval) {
        if (0 == sampleInterval) {
            sampleInterval = DEFAULT_SAMPLE_INTERVAL;
        }
        mSampleInterval = sampleInterval;
    }

    public void start() {
        if (mShouldSample.get()) {
            return;
        }
        mShouldSample.set(true);

        HandlerThreadFactory.getDoDumpThreadHandler().removeCallbacks(mRunnable);
        HandlerThreadFactory.getDoDumpThreadHandler().postDelayed(mRunnable,
                Sm.getSmEngine().getSampleDelay());
    }

    public void stop() {
        if (!mShouldSample.get()) {
            return;
        }
        mShouldSample.set(false);
        HandlerThreadFactory.getDoDumpThreadHandler().removeCallbacks(mRunnable);
    }

    abstract void doSample();

    public void setSampleInterval(long sampleInterval) {
        mSampleInterval = sampleInterval;
    }
}
