package com.winning.mars_consumer.monitor.presenter;

import com.winning.mars_consumer.monitor.Repository;
import com.winning.mars_consumer.monitor.presenter.base.BaseListPresenter;
import com.winning.mars_generator.core.modules.network.NetworkBean;

import java.util.Collection;

/**
 * Created by yuzhijun on 2018/4/8.
 */

public class NetworkPresenter extends BaseListPresenter<NetworkBean> {
    @Override
    protected Collection<NetworkBean> generateData() {
        return Repository.getInstance().getNetworkBeans();
    }
}
