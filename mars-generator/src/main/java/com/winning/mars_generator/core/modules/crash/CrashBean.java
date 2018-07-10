package com.winning.mars_generator.core.modules.crash;

import com.winning.mars_generator.core.BaseBean;
import com.winning.mars_generator.utils.BaseUtility;

import java.util.List;

/**
 * Created by yuzhijun on 2018/3/29.
 */

public class CrashBean extends BaseBean{
    public static CrashBean INVALID = new CrashBean();
    public long timestampMillis;
    public String threadName;
    public String threadState;
    public String threadGroupName;
    public boolean threadIsDaemon;
    public boolean threadIsAlive;
    public boolean threadIsInterrupted;
    public String throwableMessage;
    public List<String> throwableStacktrace;

    public CrashBean() {
    }

    public CrashBean(long timestampMillis, Thread thread, Throwable throwable) {
        this.timestampMillis = timestampMillis;
        this.threadName = thread.getName();
        this.threadState = String.valueOf(thread.getState());
        if (thread.getThreadGroup() != null) {
            this.threadGroupName = String.valueOf(thread.getThreadGroup().getName());
        }
        this.threadIsDaemon = thread.isDaemon();
        this.threadIsAlive = thread.isAlive();
        this.threadIsInterrupted = thread.isInterrupted();
        this.throwableMessage = throwable.getLocalizedMessage();
        this.throwableStacktrace = BaseUtility.getStack(throwable.getStackTrace());
    }

    @Override
    public String toString() {
        return "CrashInfo{" +
                "timestampMillis=" + timestampMillis +
                ", threadName='" + threadName + '\'' +
                ", threadState='" + threadState + '\'' +
                ", threadGroupName='" + threadGroupName + '\'' +
                ", threadIsDaemon=" + threadIsDaemon +
                ", threadIsAlive=" + threadIsAlive +
                ", threadIsInterrupted=" + threadIsInterrupted +
                ", throwableMessage='" + throwableMessage + '\'' +
                ", throwableStacktrace=" + throwableStacktrace +
                '}';
    }
}
