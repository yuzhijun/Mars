package com.winning.mars_generator.core.modules.memory;

/**
 * Created by yuzhijun on 2018/3/29.
 */
public class PssBean {
    public int totalPssKb;
    public int dalvikPssKb;
    public int nativePssKb;
    public int otherPssKb;

    @Override
    public String toString() {
        return "PssInfo{" +
                "totalPss=" + totalPssKb +
                ", dalvikPss=" + dalvikPssKb +
                ", nativePss=" + nativePssKb +
                ", otherPss=" + otherPssKb +
                '}';
    }
}
