package com.winning.mars_consumer.monitor.presenter;

import com.winning.mars_consumer.monitor.Repository;
import com.winning.mars_consumer.monitor.presenter.base.BasePresenter;
import com.winning.mars_generator.core.modules.startup.StartupBean;

/**
 * Created by yuzhijun on 2018/4/8.
 */

public class StartupPresenter extends BasePresenter<StartupBean> {
    @Override
    protected StartupBean generateData() {
        return Repository.getInstance().getStartupBean();
    }
}
