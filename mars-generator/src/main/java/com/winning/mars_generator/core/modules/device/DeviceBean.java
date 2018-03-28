package com.winning.mars_generator.core.modules.device;

/**
 * Created by yuzhijun on 2018/3/28.
 */

public class DeviceBean {
    private String deviceBrand;
    private String deviceModel;
    private String deviceID;
    private String deviceSDK;
    private String productName;
    private String modelIMEI;
    private String modelIP;
    private String networkOperator;
    private String simOperatorName;

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

    public String getModelIMEI() {
        return modelIMEI;
    }

    public void setModelIMEI(String modelIMEI) {
        this.modelIMEI = modelIMEI;
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
}
