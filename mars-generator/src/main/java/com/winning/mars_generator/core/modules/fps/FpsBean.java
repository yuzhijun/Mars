package com.winning.mars_generator.core.modules.fps;

/**
 * Created by yuzhijun on 2018/3/28.
 */
public class FpsBean {
    public int currentFps;
    public int systemFps;
    public long skipFrame;

    public int getCurrentFps() {
        return currentFps;
    }

    public void setCurrentFps(int currentFps) {
        this.currentFps = currentFps;
    }

    public int getSystemFps() {
        return systemFps;
    }

    public void setSystemFps(int systemFps) {
        this.systemFps = systemFps;
    }

    public long getSkipFrame() {
        return skipFrame;
    }

    public void setSkipFrame(long skipFrame) {
        this.skipFrame = skipFrame;
    }
}
