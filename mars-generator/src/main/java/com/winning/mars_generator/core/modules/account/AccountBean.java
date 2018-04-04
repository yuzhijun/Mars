package com.winning.mars_generator.core.modules.account;

/**
 * Created by yuzhijun on 2018/4/2.
 */

public class AccountBean {
    private String name;
    private String pwd;

    public AccountBean(){

    }
    public AccountBean(String name, String pwd) {
        this.name = name;
        this.pwd = pwd;
    }

    @Override
    public String toString() {
        return  "AccountInfo{"+
                "name" + name + "\'" +
                ",password" + pwd + "\'" +
                "}";
    }
}
