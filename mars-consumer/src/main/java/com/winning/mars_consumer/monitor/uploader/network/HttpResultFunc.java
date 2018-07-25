package com.winning.mars_consumer.monitor.uploader.network;


import com.winning.mars_consumer.monitor.bean.HttpResult;

import io.reactivex.functions.Function;

/**
 * 处理服务器返回结果  通用返回json
 * Created by yuzhijun on 2017/6/27.
 */
public class HttpResultFunc<T> implements Function<HttpResult<T>, T> {

    @Override
    public T apply(HttpResult<T> tHttpResult) throws Exception {
        if (tHttpResult.getCode() != 1){
            throw new ResponseError(500, tHttpResult.getMessage());
        }
        return tHttpResult.getData();
    }
}
