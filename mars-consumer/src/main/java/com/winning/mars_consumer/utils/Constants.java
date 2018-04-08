package com.winning.mars_consumer.utils;

/**
 * Created by yuzhijun on 2018/4/3.
 */
public class Constants {
    public class HttpCode {
        public static final int HTTP_UNAUTHORIZED = 401;
        public static final int HTTP_SERVER_ERROR = 500;
        public static final int HTTP_NOT_HAVE_NETWORK = 600;
        public static final int HTTP_NETWORK_ERROR = 700;
        public static final int HTTP_UNKNOWN_ERROR = 800;
    }

    public class Mapper{
        public static final String ACCOUNT = "Account";
        public static final String BATTERY = "Battery";
        public static final String CPU = "Cpu";
        public static final String CRASH = "Crash";
        public static final String DEADLOCK = "Deadlock";
        public static final String DEVICE = "Device";
        public static final String FPS = "Fps";
        public static final String INFLATE = "Inflate";
        public static final String LEAK = "Leak";
        public static final String NETWORK = "Network";
        public static final String SM = "Sm";
        public static final String STARTUP = "Startup";
        public static final String TRAFFIC = "Traffic";
    }
}
