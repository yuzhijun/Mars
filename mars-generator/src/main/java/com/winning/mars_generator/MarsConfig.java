package com.winning.mars_generator;

/**
 * Every module's configuration
 * Created by yuzhijun on 2018/3/27.
 */
public class MarsConfig {
    private static int debug;
    private static CPU cpu;
    private static Battery battery;
    private static Fps fps;
    private static Traffic traffic;
    private static Sm sm;
    private static Heap heap;
    private static Ram ram;
    private static Pss pss;

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

    public static class Traffic extends BaseConfig{
        //Sampling Cycle
        private long sampleMillis;

        public long getSampleMillis() {
            return sampleMillis;
        }

        public void setSampleMillis(long sampleMillis) {
            this.sampleMillis = sampleMillis;
        }
    }

    public static class Sm {
        private long longBlockThreshold;
        private long shortBlockThreshold;
        private long dumpInterval;

        public long getLongBlockThreshold() {
            return longBlockThreshold;
        }

        public void setLongBlockThreshold(long longBlockThreshold) {
            this.longBlockThreshold = longBlockThreshold;
        }

        public long getShortBlockThreshold() {
            return shortBlockThreshold;
        }

        public void setShortBlockThreshold(long shortBlockThreshold) {
            this.shortBlockThreshold = shortBlockThreshold;
        }

        public long getDumpInterval() {
            return dumpInterval;
        }

        public void setDumpInterval(long dumpInterval) {
            this.dumpInterval = dumpInterval;
        }
    }

    public static class Battery extends BaseConfig{}
    public static class Fps extends BaseConfig{}
    public static class Heap extends BaseConfig{}
    public static class Ram extends BaseConfig{}
    public static class Pss extends BaseConfig{}

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

    public static Battery getBattery() {
        return battery;
    }

    public static void setBattery(Battery battery) {
        MarsConfig.battery = battery;
    }

    public static Fps getFps() {
        return fps;
    }

    public static void setFps(Fps fps) {
        MarsConfig.fps = fps;
    }

    public static Traffic getTraffic() {
        return traffic;
    }

    public static void setTraffic(Traffic traffic) {
        MarsConfig.traffic = traffic;
    }

    public static Sm getSm() {
        return sm;
    }

    public static void setSm(Sm sm) {
        MarsConfig.sm = sm;
    }

    public static Heap getHeap() {
        return heap;
    }

    public static void setHeap(Heap heap) {
        MarsConfig.heap = heap;
    }

    public static Ram getRam() {
        return ram;
    }

    public static void setRam(Ram ram) {
        MarsConfig.ram = ram;
    }

    public static Pss getPss() {
        return pss;
    }

    public static void setPss(Pss pss) {
        MarsConfig.pss = pss;
    }
}
