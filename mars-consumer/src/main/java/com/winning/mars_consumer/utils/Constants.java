package com.winning.mars_consumer.utils;

/**
 * Created by yuzhijun on 2018/4/3.
 */
public class Constants {
//    public static final String BASE_URL = "http://172.16.72.253:9090";
public static final String BASE_URL = "http://203.110.176.174:8085";
    public static final  String UPDATE_URL = "http://203.110.176.174:8085/app/getAppVersion";
    public static final String SOCKET_URL = "http://203.110.176.174:8084";
//    public static final String SOCKET_URL = "http://172.16.72.253:9092";
    public static final int DEBUG_UPLOAD_RATE = 2000;//two seconds
    public static final int RELEASE_UPLOAD_RATE = 60000;//one minute

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
        public static final String DEVICE_HANDLER = "DEVICE_HANDLER";
        public static final String APP_HANDLER = "APP_HANDLER";
        public static final String ACCOUNT_HANDLER = "ACCOUNT_HANDLER";
        public static final String BASE_INFO = "BASE_INFO";
        public static final String USER_BEHAVIOR = "UserBehavior";
    }
}
