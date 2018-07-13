package com.winning.mars_generator.core.modules.account;

import com.winning.mars_generator.core.BaseBean;

/**
 * Created by yuzhijun on 2018/4/2.
 */

public class AccountBean extends BaseBean{
    private String name;
    private String empno;

    public AccountBean(){

    }
    public AccountBean(String name, String empno) {
        this.name = name;
        this.empno = empno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmpno() {
        return empno;
    }

    public void setEmpno(String empno) {
        this.empno = empno;
    }

    @Override
    public String toString() {
        return  "AccountInfo{"+
                "name" + name + "\'" +
                ",emp_no" + empno + "\'" +
                "}";
    }
}
