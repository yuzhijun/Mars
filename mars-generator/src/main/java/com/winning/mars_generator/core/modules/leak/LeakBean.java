package com.winning.mars_generator.core.modules.leak;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.util.ArrayMap;

import com.winning.mars_generator.core.BaseBean;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by yuzhijun on 2018/3/30.
 */

public class LeakBean{
    private ArrayMap<String, Map<String, Object>> mLeakMemoryInfoArrayMap;

    private LeakBean() {
        mLeakMemoryInfoArrayMap = new ArrayMap<>();
    }

    private static class InstanceHolder {
        private static LeakBean sINSTANCE = new LeakBean();
    }

    public static LeakBean instance() {
        return InstanceHolder.sINSTANCE;
    }


    public synchronized void createOrUpdateIfExsist(String refKey, Map<String, Object> map) {
        Map<String, Object> curMap = mLeakMemoryInfoArrayMap.get(refKey);
        if (curMap == null) {
            curMap = new ArrayMap<>();
        }
        curMap.putAll(map);
        mLeakMemoryInfoArrayMap.put(refKey, curMap);
    }

    public synchronized LeakMemoryBean generateLeakMemoryInfo(String refKey) {
        LeakMemoryBean leakMemoryBean = new LeakMemoryBean(refKey);
        Map<String, Object> detailMap = mLeakMemoryInfoArrayMap.get(refKey);
        leakMemoryBean.referenceKey = refKey;
        Object leakTimeObj = detailMap.get(LeakMemoryBean.Fields.LEAK_TIME);
        leakMemoryBean.leakTime = leakTimeObj == null ? "" : String.valueOf(leakTimeObj);
        Object statusSummaryObj = detailMap.get(LeakMemoryBean.Fields.STATUS_SUMMARY);
        leakMemoryBean.statusSummary = statusSummaryObj == null ? "" : String.valueOf(statusSummaryObj);
        Object statusObj = detailMap.get(LeakMemoryBean.Fields.STATUS);
        leakMemoryBean.status = statusObj == null ? LeakMemoryBean.Status.STATUS_INVALID : (int) statusObj;
        Object leakObjName = detailMap.get(LeakMemoryBean.Fields.LEAK_OBJ_NAME);
        leakMemoryBean.leakObjectName = leakObjName == null ? "" : String.valueOf(leakObjName);
        Object pathToRoot = detailMap.get(LeakMemoryBean.Fields.PATH_TO_ROOT);
        leakMemoryBean.pathToGcRoot = pathToRoot == null ? new ArrayList<String>() : (List) pathToRoot;
        Object leakMemoryBytes = detailMap.get(LeakMemoryBean.Fields.LEAK_MEMORY_BYTES);
        leakMemoryBean.leakMemoryBytes = leakMemoryBytes == null ? 0L : (long) leakMemoryBytes;
        return leakMemoryBean;
    }

    public static class LeakMemoryBean extends BaseBean implements Serializable, Comparable<LeakMemoryBean> {
        public static final SimpleDateFormat DF = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
        @Retention(RetentionPolicy.SOURCE)
        @StringDef({Fields.REF_KEY, Fields.LEAK_TIME, Fields.STATUS_SUMMARY, Fields.STATUS, Fields.LEAK_OBJ_NAME, Fields.PATH_TO_ROOT, Fields.LEAK_MEMORY_BYTES})
        public @interface Fields {
            public static final String REF_KEY = "referenceKey";
            public static final String LEAK_TIME = "leakTime";
            public static final String STATUS_SUMMARY = "statusSummary";
            public static final String STATUS = "status";
            public static final String LEAK_OBJ_NAME = "leakObjectName";
            public static final String PATH_TO_ROOT = "pathToGcRoot";
            public static final String LEAK_MEMORY_BYTES = "leakMemoryBytes";
        }

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({Status.STATUS_INVALID, Status.STATUS_START, Status.STATUS_PROGRESS, Status.STATUS_RETRY, Status.STATUS_DONE})
        public @interface Status {
            public static final int STATUS_INVALID = -1;
            public static final int STATUS_START = 0;
            public static final int STATUS_PROGRESS = 1;
            public static final int STATUS_RETRY = 2;
            public static final int STATUS_DONE = 3;
        }

        public String referenceKey = "";
        public String leakTime = "";
        public String statusSummary = "";
        public @Status
        int status = Status.STATUS_INVALID;
        public String leakObjectName = "";
        public List pathToGcRoot = new ArrayList<>();
        public long leakMemoryBytes = 0L;

        public LeakMemoryBean(String referenceKey) {
            this.referenceKey = referenceKey;
        }

        @Override
        public int compareTo(@NonNull LeakMemoryBean o) {
            if (this == o) {
                return 0;
            }
            try {
                return DF.parse(this.leakTime).compareTo(DF.parse(o.leakTime));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }

        @Override
        public String toString() {
            return "LeakMemoryBean{" +
                    "referenceKey='" + referenceKey + '\'' +
                    ", leakTime='" + leakTime + '\'' +
                    ", statusSummary='" + statusSummary + '\'' +
                    ", status=" + status +
                    ", leakObjectName='" + leakObjectName + '\'' +
                    ", pathToGcRoot=" + pathToGcRoot +
                    ", leakMemoryBytes=" + leakMemoryBytes +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            LeakMemoryBean that = (LeakMemoryBean) o;
            return referenceKey.equals(that.referenceKey);
        }

        @Override
        public int hashCode() {
            return referenceKey.hashCode();
        }
    }
}
