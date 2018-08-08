package com.winning.mars_generator.core;

public class BaseBean {
    public String appKey;
    public String modelIMEI;
    public int app_type;

    public int getApp_type() {
        return app_type;
    }

    public void setApp_type(int app_type) {
        this.app_type = app_type;
    }

    public String getModelIMEI() {
        return modelIMEI;
    }

    public void setModelIMEI(String modelIMEI) {
        this.modelIMEI = modelIMEI;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
}
