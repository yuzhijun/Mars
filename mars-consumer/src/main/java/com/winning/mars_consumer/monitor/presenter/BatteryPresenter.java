package com.winning.mars_consumer.monitor.presenter;

import com.winning.mars_consumer.monitor.Repository;
import com.winning.mars_consumer.monitor.presenter.base.BasePresenter;
import com.winning.mars_generator.core.modules.battery.BatteryBean;

/**
 * Created by yuzhijun on 2018/4/4.
 */

public class BatteryPresenter extends BasePresenter<BatteryBean> {
    @Override
    protected BatteryBean generateData() {
        return Repository.getInstance().getBatteryBean();
    }
}
