package com.winning.mars_consumer.monitor.presenter;

import com.winning.mars_consumer.monitor.Repository;
import com.winning.mars_consumer.monitor.presenter.base.BaseListPresenter;

import java.util.Collection;

/**
 * Created by yuzhijun on 2018/4/8.
 */

public class DeadLockPresenter extends BaseListPresenter<Thread> {
    @Override
    protected Collection<Thread> generateData() {
        return Repository.getInstance().getDeadLockThreads();
    }
}
