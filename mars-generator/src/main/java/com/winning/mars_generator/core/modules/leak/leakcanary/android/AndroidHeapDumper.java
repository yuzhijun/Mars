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
package com.winning.mars_generator.core.modules.leak.leakcanary.android;

import android.content.Context;
import android.os.Debug;

import com.winning.mars_generator.core.modules.leak.OutputLeakService;
import com.winning.mars_generator.core.modules.leak.leakcanary.watcher.HeapDumper;
import com.winning.mars_generator.utils.LogUtil;

import java.io.File;

public final class AndroidHeapDumper implements HeapDumper {

    final Context context;
    private final LeakDirectoryProvider leakDirectoryProvider;

    public AndroidHeapDumper(Context context, LeakDirectoryProvider leakDirectoryProvider) {
        this.leakDirectoryProvider = leakDirectoryProvider;
        this.context = context.getApplicationContext();
    }


    @SuppressWarnings("ReferenceEquality") // Explicitly checking for named null.
    @Override
    public File dumpHeap(String referenceKey,String referenceName) {
        OutputLeakService.sendOutputBroadcastStart(context, referenceKey);
        File heapDumpFile = leakDirectoryProvider.newHeapDumpFile();

        if (heapDumpFile == RETRY_LATER) {
            LogUtil.d("create dump file failed，RETRY_LATER");
            OutputLeakService.sendOutputBroadcastRetry(context, referenceKey);
            return RETRY_LATER;
        }
        LogUtil.d("create new dump file：" + heapDumpFile.getAbsolutePath());

        try {
            CanaryLog.d("write dump info");
            Debug.dumpHprofData(heapDumpFile.getAbsolutePath());
            CanaryLog.d("dump info finished");
            return heapDumpFile;
        } catch (Exception e) {
            CanaryLog.d(e, "Could not dump heap");
            // Abort heap dump
            OutputLeakService.sendOutputBroadcastRetry(context, referenceKey);
            return RETRY_LATER;
        }
    }
}
