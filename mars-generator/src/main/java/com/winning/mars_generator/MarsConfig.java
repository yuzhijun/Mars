package com.winning.mars_generator;

/**
 * Every module's configuration
 * Created by yuzhijun on 2018/3/27.
 */
public class MarsConfig {
    private static int debug;
    private static CPU cpu;


    public static class BaseConfig{
        //Obtaining Cycle
        private long intervalMillis;

        public long getIntervalMillis() {
            return intervalMillis;
        }

        public void setIntervalMillis(long intervalMillis) {
            this.intervalMillis = intervalMillis;
        }
    }

    public static class CPU extends BaseConfig{
        //Sampling Cycle
        private long sampleMillis;

        public long getSampleMillis() {
            return sampleMillis;
        }

        public void setSampleMillis(long sampleMillis) {
            this.sampleMillis = sampleMillis;
        }
    }

    public static int getDebug() {
        return debug;
    }

    public static void setDebug(int debug) {
        MarsConfig.debug = debug;
    }

    public static CPU getCpu() {
        return cpu;
    }

    public static void setCpu(CPU cpu) {
        MarsConfig.cpu = cpu;
    }
}
