package com.winning.mars_consumer.monitor.presenter;

import com.winning.mars_consumer.monitor.Repository;
import com.winning.mars_consumer.monitor.presenter.base.BaseListPresenter;
import com.winning.mars_generator.core.modules.crash.CrashBean;

import java.util.Collection;

/**
 * Created by yuzhijun on 2018/4/4.
 */

public class CrashPresenter extends BaseListPresenter<CrashBean> {
    @Override
    protected Collection<CrashBean> generateData() {
        return Repository.getInstance().getCrashBeans();
    }
}
