package com.winning.mars_generator.core.modules.leak;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.util.ArrayMap;

import com.squareup.leakcanary.AnalysisResult;
import com.squareup.leakcanary.HeapDump;
import com.winning.mars_generator.utils.LogUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;


public class LeakOutputReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (OutputLeakService.OUTPUT_BOARDCAST_ACTION_START.equals(intent.getAction())) {
            onLeakDumpStart(intent);
        }  else if (OutputLeakService.OUTPUT_BOARDCAST_ACTION_RETRY.equals(intent.getAction())) {
            onLeakDumpRetry(intent);
        } else if (OutputLeakService.OUTPUT_BOARDCAST_ACTION_DONE.equals(intent.getAction())) {
            onLeakDumpDone(intent);
        }
    }

    private void onLeakDumpStart(Intent intent) {
        final String refrenceKey = intent.getStringExtra("refrenceKey");
        LogUtil.d("onLeakDumpStart:" + refrenceKey);
        Map<String, Object> map = new ArrayMap<>();
        map.put(LeakBean.LeakMemoryBean.Fields.LEAK_TIME, LeakBean.LeakMemoryBean.DF.format(new Date(System.currentTimeMillis())));
        map.put(LeakBean.LeakMemoryBean.Fields.STATUS_SUMMARY, "Leak detected");
        map.put(LeakBean.LeakMemoryBean.Fields.STATUS, LeakBean.LeakMemoryBean.Status.STATUS_START);
        LeakBean.instance().createOrUpdateIfExsist(refrenceKey, map);
        Leak.getInstance().generate(LeakBean.instance().generateLeakMemoryInfo(refrenceKey));
    }

    private void onLeakDumpProgress(Intent intent) {
        final String refrenceKey = intent.getStringExtra("refrenceKey");
        final String progress = intent.getStringExtra("progress");
        LogUtil.d("onLeakDumpProgress:" + refrenceKey + " , progress:" + progress);
        Map<String, Object> map = new ArrayMap<>();
        map.put(LeakBean.LeakMemoryBean.Fields.STATUS_SUMMARY, progress);
        map.put(LeakBean.LeakMemoryBean.Fields.STATUS, LeakBean.LeakMemoryBean.Status.STATUS_PROGRESS);
        LeakBean.instance().createOrUpdateIfExsist(refrenceKey, map);
        Leak.getInstance().generate(LeakBean.instance().generateLeakMemoryInfo(refrenceKey));
    }

    private void onLeakDumpRetry(Intent intent) {
        final String refrenceKey = intent.getStringExtra("refrenceKey");
        LogUtil.d("onLeakDumpRetry:" + refrenceKey);
        Map<String, Object> map = new ArrayMap<>();
        map.put(LeakBean.LeakMemoryBean.Fields.STATUS_SUMMARY, "Retry waiting");
        map.put(LeakBean.LeakMemoryBean.Fields.STATUS, LeakBean.LeakMemoryBean.Status.STATUS_RETRY);
        LeakBean.instance().createOrUpdateIfExsist(refrenceKey, map);
        Leak.getInstance().generate(LeakBean.instance().generateLeakMemoryInfo(refrenceKey));
    }

    private void onLeakDumpDone(Intent intent) {
        final String refrenceKey = intent.getStringExtra("refrenceKey");
        HeapDump heapDump = (HeapDump) intent.getSerializableExtra("heapDump");
        final AnalysisResult analysisResult = (AnalysisResult) intent.getSerializableExtra("result");
        String summary = intent.getStringExtra("summary");
        String leakInfo = intent.getStringExtra("leakInfo");
        LogUtil.d("onLeakDumpDone:" + summary);
        new LoadLeaks(Leak.getLeakDirectoryProvider(), heapDump.referenceKey, new LoadLeaks.OnLeakCallback() {
            @Override
            public void onLeak(List<String> list) {
                LogUtil.d("onLeakDumpDone:" + refrenceKey + " , leak:" + analysisResult.className);
                Map<String, Object> map = new ArrayMap<>();
                map.put(LeakBean.LeakMemoryBean.Fields.LEAK_OBJ_NAME, analysisResult.className + (analysisResult.excludedLeak ? "[Excluded]" : ""));
                map.put(LeakBean.LeakMemoryBean.Fields.PATH_TO_ROOT, list);
                //cost too mush time,so skip it, always is 0
                map.put(LeakBean.LeakMemoryBean.Fields.LEAK_MEMORY_BYTES, analysisResult.retainedHeapSize);
                map.put(LeakBean.LeakMemoryBean.Fields.STATUS, LeakBean.LeakMemoryBean.Status.STATUS_DONE);
                map.put(LeakBean.LeakMemoryBean.Fields.STATUS_SUMMARY, "done");
                LeakBean.instance().createOrUpdateIfExsist(refrenceKey, map);
                Leak.getInstance().generate(LeakBean.instance().generateLeakMemoryInfo(refrenceKey));
            }

            @Override
            public void onLeakNull(String s) {
                LogUtil.d("onLeakDumpDone:" + s);
                Map<String, Object> map = new ArrayMap<>();
                map.put(LeakBean.LeakMemoryBean.Fields.LEAK_OBJ_NAME, analysisResult.className + (analysisResult.excludedLeak ? "[Excluded]" : ""));
                //cost too mush time,so skip it, always is 0
                map.put(LeakBean.LeakMemoryBean.Fields.LEAK_MEMORY_BYTES, analysisResult.retainedHeapSize);
                map.put(LeakBean.LeakMemoryBean.Fields.STATUS, LeakBean.LeakMemoryBean.Status.STATUS_DONE);
                map.put(LeakBean.LeakMemoryBean.Fields.STATUS_SUMMARY, "leak null.");
                LeakBean.instance().createOrUpdateIfExsist(refrenceKey, map);
                Leak.getInstance().generate(LeakBean.instance().generateLeakMemoryInfo(refrenceKey));
            }
        }).load();
    }
}
