package com.winning.mars_consumer.monitor.presenter;

import com.winning.mars_consumer.monitor.Repository;
import com.winning.mars_consumer.monitor.presenter.base.BaseListPresenter;
import com.winning.mars_generator.core.modules.traffic.TrafficBean;

import java.util.Collection;

/**
 * Created by yuzhijun on 2018/4/8.
 */

public class TrafficPresenter extends BaseListPresenter<TrafficBean> {
    @Override
    protected Collection<TrafficBean> generateData() {
        return Repository.getInstance().getTrafficBeans();
    }
}
