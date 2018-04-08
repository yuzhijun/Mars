package com.winning.mars_consumer.monitor;

import com.winning.mars_consumer.MarsConsumer;
import com.winning.mars_consumer.monitor.storage.MarsPreference;
import com.winning.mars_generator.utils.GsonSerializer;

import java.util.List;

/**
 * used for sharePreference
 * Created by yuzhijun on 2018/4/8.
 */

public class LocalRepository {
    private MarsPreference mMarsPreference = new MarsPreference(MarsConsumer.mContext,"Mars");
    private GsonSerializer mGsonSerializer = new GsonSerializer();
    private static LocalRepository mInstance;
    private LocalRepository(){
    }

    public static LocalRepository getInstance(){
        if (null == mInstance){
            synchronized (LocalRepository.class){
                if (null == mInstance){
                    mInstance = new LocalRepository();
                }
            }
        }
        return mInstance;
    }

    public <T> void save2Local(String key,T object){
        String value = mGsonSerializer.serialize(object);
        mMarsPreference.setPrefString(key,value);
    }

    public <T> void saveCollection2Local(String key,T object){
        String value = mGsonSerializer.serialize(object);
        List<T> localList = (List<T>) getCollectionFromLocal(key,object.getClass());
        if (null != localList){
            List<T> valueList = (List<T>) mGsonSerializer.getJsonList(value,object.getClass());
            localList.addAll(valueList);
            value = mGsonSerializer.serialize(localList);
        }
        mMarsPreference.setPrefString(key,value);
    }

    public <T> T getFromLocal(String key,Class<T> tClass){
        String value = mMarsPreference.getPrefString(key,"");
        if (null == value || "".equalsIgnoreCase(value)){
            return null;
        }
        return mGsonSerializer.deserialize(value,tClass);
    }

    public <T> List<T> getCollectionFromLocal(String key,Class<T> tClass){
        String value = mMarsPreference.getPrefString(key,"");
        if (null == value || "".equalsIgnoreCase(value)){
            return null;
        }

        return mGsonSerializer.getJsonList(value,tClass);
    }
}
