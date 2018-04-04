package com.winning.mars_consumer.monitor.presenter;

import com.winning.mars_consumer.monitor.Repository;
import com.winning.mars_consumer.monitor.presenter.base.BaseListPresenter;
import com.winning.mars_generator.core.modules.cpu.CpuBean;

import java.util.Collection;

/**
 * Created by yuzhijun on 2018/4/4.
 */

public class CpuPresenter extends BaseListPresenter<CpuBean> {
    @Override
    protected Collection<CpuBean> generateData() {
        return Repository.getInstance().getCpuBeans();
    }
}
