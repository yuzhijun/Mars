/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.winning.mars_generator.core.modules.leak;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.winning.mars_generator.R;
import com.winning.mars_generator.core.modules.leak.leakcanary.analyzer.AnalysisResult;
import com.winning.mars_generator.core.modules.leak.leakcanary.android.AbstractAnalysisResultService;
import com.winning.mars_generator.core.modules.leak.leakcanary.android.CanaryLog;
import com.winning.mars_generator.core.modules.leak.leakcanary.android.LeakCanary;
import com.winning.mars_generator.core.modules.leak.leakcanary.watcher.HeapDump;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.text.format.Formatter.formatShortFileSize;
import static com.winning.mars_generator.core.modules.leak.leakcanary.android.internal.LeakCanaryInternals.classSimpleName;

/**
 * output leak service
 */
public class OutputLeakService extends AbstractAnalysisResultService {
    public static final String OUTPUT_BOARDCAST_ACTION_START = "com.winning.leakcanary.output.start";
    public static final String OUTPUT_BOARDCAST_ACTION_RETRY = "com.winning.leakcanary.output.retry";
    public static final String OUTPUT_BOARDCAST_ACTION_DONE = "com.winning.leakcanary.output.done";

    @Override
    @WorkerThread
    protected final void onHeapAnalyzed(HeapDump heapDump, AnalysisResult result) {
        String leakInfo = LeakCanary.leakInfo(this, heapDump, result, true);
        boolean resultSaved;
        boolean shouldSaveResult = result.leakFound || result.failure != null;
        if (!shouldSaveResult) {
            CanaryLog.d("no leak");
            return;
        }
        heapDump = renameHeapdump(heapDump);
        resultSaved = saveResult(heapDump, result);
        if (!resultSaved) {
            CanaryLog.d("save leak info failed");
            return;
        }
        CanaryLog.d("Sampling");
        String summary;
        if (result.failure == null) {
            String size = formatShortFileSize(this, result.retainedHeapSize);
            String className = classSimpleName(result.className);
            if (result.excludedLeak) {
                summary = getString(R.string.leak_canary_leak_excluded, className, size);
            } else {
                summary = getString(R.string.leak_canary_class_has_leaked, className, size);
            }
        } else {
            summary = getString(R.string.leak_canary_analysis_failed);
        }
        CanaryLog.d("Sampling finished");
        sendOutputBroadcastDone(this, heapDump.referenceKey, heapDump, result, summary, leakInfo);
    }

    public static void sendOutputBroadcastStart(Context context, String refrenceKey) {
        Intent intent = new Intent(OUTPUT_BOARDCAST_ACTION_START);
        intent.putExtra("refrenceKey", refrenceKey);
        context.sendBroadcast(intent);
    }

    public static void sendOutputBroadcastRetry(Context context, String refrenceKey) {
        Intent intent = new Intent(OUTPUT_BOARDCAST_ACTION_RETRY);
        intent.putExtra("refrenceKey", refrenceKey);
        context.sendBroadcast(intent);
    }

    public static void sendOutputBroadcastDone(Context context, String refrenceKey, HeapDump heapDump, AnalysisResult result, String summary, String leakInfo) {
        Intent intent = new Intent(OUTPUT_BOARDCAST_ACTION_DONE);
        intent.putExtra("refrenceKey", refrenceKey);
        intent.putExtra("heapDump", heapDump);
        intent.putExtra("result", result);
        intent.putExtra("summary", summary);
        intent.putExtra("leakInfo", leakInfo);
        context.sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    private boolean saveResult(HeapDump heapDump, AnalysisResult result) {
        File resultFile = new File(heapDump.heapDumpFile.getParentFile(),
                heapDump.heapDumpFile.getName() + ".result");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(resultFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(heapDump);
            oos.writeObject(result);
            return true;
        } catch (IOException e) {
            CanaryLog.d(e, "Could not save leak analysis result to disk.");
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ignored) {
                }
            }
        }
        return false;
    }

    private HeapDump renameHeapdump(HeapDump heapDump) {
        String fileName =
                new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SSS'.hprof'", Locale.US).format(new Date());

        File newFile = new File(heapDump.heapDumpFile.getParent(), fileName);
        boolean renamed = heapDump.heapDumpFile.renameTo(newFile);
        if (!renamed) {
            CanaryLog.d("Could not rename heap dump file %s to %s", heapDump.heapDumpFile.getPath(),
                    newFile.getPath());
        } else {
            CanaryLog.d("rename,%s -> %s", heapDump.heapDumpFile.getAbsolutePath(), newFile.getAbsolutePath());
        }
        return new HeapDump(newFile, heapDump.referenceKey, heapDump.referenceName,
                heapDump.excludedRefs, heapDump.watchDurationMs, heapDump.gcDurationMs,
                heapDump.heapDumpDurationMs);
    }

}
