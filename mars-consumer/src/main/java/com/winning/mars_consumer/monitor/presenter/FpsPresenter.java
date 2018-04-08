package com.winning.mars_consumer.monitor.presenter;

import com.winning.mars_consumer.monitor.Repository;
import com.winning.mars_consumer.monitor.presenter.base.BaseListPresenter;
import com.winning.mars_generator.core.modules.fps.FpsBean;

import java.util.Collection;

/**
 * Created by yuzhijun on 2018/4/8.
 */

public class FpsPresenter extends BaseListPresenter<FpsBean> {
    @Override
    protected Collection<FpsBean> generateData() {
        return Repository.getInstance().getFpsBeans();
    }
}
