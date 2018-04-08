package com.winning.mars_consumer.monitor.presenter;

import com.winning.mars_consumer.monitor.Repository;
import com.winning.mars_consumer.monitor.presenter.base.BasePresenter;
import com.winning.mars_generator.core.modules.device.DeviceBean;

/**
 * Created by yuzhijun on 2018/4/8.
 */

public class DevicePresenter extends BasePresenter<DeviceBean> {
    @Override
    protected DeviceBean generateData() {
        return Repository.getInstance().getDeviceBean();
    }
}
