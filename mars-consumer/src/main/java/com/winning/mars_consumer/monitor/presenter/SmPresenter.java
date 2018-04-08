package com.winning.mars_consumer.monitor.presenter;

import com.winning.mars_consumer.monitor.Repository;
import com.winning.mars_consumer.monitor.presenter.base.BaseListPresenter;
import com.winning.mars_generator.core.modules.sm.SmBean;

import java.util.Collection;

/**
 * Created by yuzhijun on 2018/4/8.
 */

public class SmPresenter extends BaseListPresenter<SmBean> {
    @Override
    protected Collection<SmBean> generateData() {
        return Repository.getInstance().getSmBeans();
    }
}
