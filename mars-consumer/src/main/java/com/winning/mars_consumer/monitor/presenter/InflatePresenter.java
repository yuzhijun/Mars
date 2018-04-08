package com.winning.mars_consumer.monitor.presenter;

import com.winning.mars_consumer.monitor.Repository;
import com.winning.mars_consumer.monitor.presenter.base.BaseListPresenter;
import com.winning.mars_generator.core.modules.inflate.InflateBean;

import java.util.Collection;

/**
 * Created by yuzhijun on 2018/4/8.
 */

public class InflatePresenter extends BaseListPresenter<InflateBean> {
    @Override
    protected Collection<InflateBean> generateData() {
        return Repository.getInstance().getInflateBeans();
    }
}
