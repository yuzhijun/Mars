package com.winning.mars_consumer.monitor.presenter;

import com.winning.mars_consumer.monitor.Repository;
import com.winning.mars_consumer.monitor.presenter.base.BaseListPresenter;
import com.winning.mars_generator.core.modules.account.AccountBean;

import java.util.Collection;

/**
 * Created by yuzhijun on 2018/4/8.
 */

public class AccountPresenter extends BaseListPresenter<AccountBean> {

    @Override
    protected Collection<AccountBean> generateData() {
        return Repository.getInstance().getAccountBeans();
    }
}
