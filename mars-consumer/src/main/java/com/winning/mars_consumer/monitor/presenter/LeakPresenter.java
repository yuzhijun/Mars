package com.winning.mars_consumer.monitor.presenter;

import com.winning.mars_consumer.monitor.Repository;
import com.winning.mars_consumer.monitor.presenter.base.BaseListPresenter;
import com.winning.mars_generator.core.modules.leak.LeakBean;

import java.util.Collection;

/**
 * Created by yuzhijun on 2018/4/8.
 */

public class LeakPresenter extends BaseListPresenter<LeakBean.LeakMemoryBean> {
    @Override
    protected Collection<LeakBean.LeakMemoryBean> generateData() {
        return Repository.getInstance().getLeakMemoryBeans();
    }
}
