package com.winning.mars_generator.core.modules.crash;

import android.content.Context;
import android.os.Environment;

import com.winning.mars_generator.utils.GsonSerializer;
import com.winning.mars_generator.utils.IoUtil;
import com.winning.mars_generator.utils.Serializer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by yuzhijun on 2018/3/29.
 */
public class DefaultCrashHelper implements ICrashHelper {
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);

    private Context mContext;
    private Serializer mSerializer;

    public DefaultCrashHelper(Context context, Serializer serializer) {
        mContext = context.getApplicationContext();
        mSerializer = serializer;
    }

    public DefaultCrashHelper(Context context) {
        mContext = context.getApplicationContext();
        mSerializer = new GsonSerializer();
    }

    @Override
    public synchronized void storeCrash(CrashBean crashInfo) throws IOException {
        File file = getStoreFile(mContext, getStoreFileName());
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
            fileWriter.write(mSerializer.serialize(crashInfo));
        } catch (IOException e) {
        } finally {
            IoUtil.closeSilently(fileWriter);
        }
    }

    @Override
    public synchronized List<CrashBean> restoreCrash() throws IOException {
        File[] crashFiles = makeSureCrashDir(mContext).listFiles(mCrashFilenameFilter);
        List<CrashBean> crashInfos = new ArrayList<>();
        for (File crashFile : crashFiles) {
            FileReader reader = null;
            try {
                reader = new FileReader(crashFile);
                crashInfos.add(mSerializer.deserialize(reader, CrashBean.class));
            } catch (IOException e) {
            } finally {
                IoUtil.closeSilently(reader);
            }
        }
        return crashInfos;
    }

    public synchronized void clearCrash() throws IOException {
        File[] crashFiles = makeSureCrashDir(mContext).listFiles(mCrashFilenameFilter);
        for (File crashFile : crashFiles) {
            boolean deleteResult = crashFile.delete();
        }
    }

    private static final String SUFFIX = ".crash";

    private FilenameFilter mCrashFilenameFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String filename) {
            return filename.endsWith(SUFFIX);
        }
    };

    private static String getStoreFileName() {
        return FORMATTER.format(new Date(System.currentTimeMillis())) + SUFFIX;
    }

    private static File getStoreFile(Context context, String fileName) throws IOException {
        File file = new File(makeSureCrashDir(context), fileName);
        if (file.exists() && !file.delete()) {
            throw new IOException(file.getAbsolutePath() + " already exist and delete failed");
        }
        return file;
    }

    private static File makeSureCrashDir(Context context) throws IOException {
        File crashDir;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            crashDir = new File(context.getExternalCacheDir(), "MarsCrash");
        } else {
            crashDir = new File(context.getCacheDir(), "MarsCrash");
        }
        if (!crashDir.exists() && !crashDir.mkdirs()) {
            throw new IOException("can not get crash directory");
        }
        return crashDir;
    }
}
