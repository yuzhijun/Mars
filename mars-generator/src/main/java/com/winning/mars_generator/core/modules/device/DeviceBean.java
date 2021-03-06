package com.winning.mars_generator.core.modules.device;

import com.winning.mars_generator.core.BaseBean;

/**
 * Created by yuzhijun on 2018/3/28.
 */

public class DeviceBean extends BaseBean implements Cloneable{
    private String deviceBrand;
    private String deviceModel;
    private String deviceID;
    private String deviceSDK;
    private String productName;
    private String modelIP;
    private String networkOperator;
    private String simOperatorName;
    private String screenWidth;
    private String screenHeight;
    private long start_time;
    private String yxnc;
    private String cckj;
    private String czxt;

    public String getCzxt() {
        return czxt;
    }

    public void setCzxt(String czxt) {
        this.czxt = czxt;
    }

    public String getYxnc() {
        return yxnc;
    }

    public void setYxnc(String yxnc) {
        this.yxnc = yxnc;
    }

    public String getCckj() {
        return cckj;
    }

    public void setCckj(String cckj) {
        this.cckj = cckj;
    }

    public String getDeviceBrand() {
        return deviceBrand;
    }

    public void setDeviceBrand(String deviceBrand) {
        this.deviceBrand = deviceBrand;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getModelIP() {
        return modelIP;
    }

    public void setModelIP(String modelIP) {
        this.modelIP = modelIP;
    }

    public String getDeviceSDK() {
        return deviceSDK;
    }

    public void setDeviceSDK(String deviceSDK) {
        this.deviceSDK = deviceSDK;
    }

    public String getNetworkOperator() {
        return networkOperator;
    }

    public void setNetworkOperator(String networkOperator) {
        this.networkOperator = networkOperator;
    }

    public String getSimOperatorName() {
        return simOperatorName;
    }

    public void setSimOperatorName(String simOperatorName) {
        this.simOperatorName = simOperatorName;
    }

    public String getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(String screenWidth) {
        this.screenWidth = screenWidth;
    }

    public String getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(String screenHeight) {
        this.screenHeight = screenHeight;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    @Override
    public DeviceBean clone() {
        DeviceBean sc = null;
        try
        {
            sc = (DeviceBean) super.clone();
        } catch (CloneNotSupportedException e){
            e.printStackTrace();
        }
        return sc;
    }
}
