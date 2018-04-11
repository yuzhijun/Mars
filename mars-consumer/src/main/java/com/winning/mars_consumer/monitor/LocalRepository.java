package com.winning.mars_consumer.monitor;

import com.winning.mars_consumer.MarsConsumer;
import com.winning.mars_consumer.monitor.storage.MarsPreference;
import com.winning.mars_generator.utils.GsonSerializer;

import java.util.LinkedList;
import java.util.List;

/**
 * used for sharePreference
 * Created by yuzhijun on 2018/4/8.
 */

public class LocalRepository {
    private static final int MAX_CAPTITY = 150;
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
        LinkedList<T> localList = (LinkedList<T>) getCollectionFromLocal(key,object.getClass());
        if (null != localList){
            LinkedList<T> valueList = (LinkedList<T>) object;
            int size = valueList.size();
            if (localList.size() + size < MAX_CAPTITY){
                localList.addAll(valueList);
            }else{
                for (int i = 0;i < size;i++){
                    if (null != localList && localList.size() > 0){
                        localList.removeFirst();
                    }
                    localList.addLast(valueList.get(i));
                }
            }
            value = mGsonSerializer.serialize(localList);
        }
        mMarsPreference.setPrefString(key,value);
    }

    public <T> List<T> getCollectionFromLocal(String key,Class<T> tClass){
        String value = mMarsPreference.getPrefString(key,"");
        if (null == value || "".equalsIgnoreCase(value)){
            return null;
        }
        return mGsonSerializer.getJsonList(value,tClass);
    }

    public String getFromLocal(String key){
        String value = mMarsPreference.getPrefString(key,"");
        if (null == value || "".equalsIgnoreCase(value)){
            return null;
        }
        return value;
    }

    public void cleanLocal(String key){
        mMarsPreference.setPrefString(key,"");
    }
}
