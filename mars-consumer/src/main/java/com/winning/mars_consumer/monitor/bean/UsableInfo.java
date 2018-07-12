package com.winning.mars_consumer.monitor.bean;

import java.util.Set;

public class UsableInfo {
    private String modelIMEI;
    private String app_key;
    private Set<String> accounts;

    public String getModelIMEI() {
        return modelIMEI;
    }

    public void setModelIMEI(String modelIMEI) {
        this.modelIMEI = modelIMEI;
    }

    public String getApp_key() {
        return app_key;
    }

    public void setApp_key(String app_key) {
        this.app_key = app_key;
    }

    public Set<String> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<String> accounts) {
        this.accounts = accounts;
    }
}
