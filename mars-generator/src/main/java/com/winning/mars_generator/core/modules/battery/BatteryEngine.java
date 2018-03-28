package com.winning.mars_generator.core.modules.battery;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.winning.mars_generator.core.Engine;
import com.winning.mars_generator.core.Generator;
import com.winning.mars_generator.exception.MarsInvalidDataException;
import com.winning.mars_generator.utils.LogUtil;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by yuzhijun on 2018/3/28.
 */

public class BatteryEngine implements Engine {
    private Generator<BatteryBean> mGenerator;
    private Context mContext;
    private long mIntervalMillis;
    private CompositeDisposable mCompositeDisposable;

    public BatteryEngine(Generator<BatteryBean> generator, Context context,long intervalMillis){
        this.mGenerator = generator;
        this.mContext = context;
        this.mIntervalMillis = intervalMillis;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void launch() {
        mCompositeDisposable.add(Observable.interval(mIntervalMillis, TimeUnit.MILLISECONDS).
                concatMap(new Function<Long, ObservableSource<BatteryBean>>() {
                    @Override
                    public ObservableSource<BatteryBean> apply(Long aLong) throws Exception {
                        return sample();
                    }
                }).subscribe(new Consumer<BatteryBean>() {
            @Override
            public void accept(BatteryBean batteryBean) throws Exception {
                mGenerator.generate(batteryBean);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LogUtil.e(String.valueOf(throwable));
            }
        }));
    }

    private ObservableSource<BatteryBean> sample() {
        return Observable.fromCallable(new Callable<BatteryBean>() {
            @Override
            public BatteryBean call() throws Exception {
                return getBatteryInfo(mContext);
            }
        });
    }

    private static final class BatteryIntentFilterHolder {
        private static final IntentFilter BATTERY_INTENT_FILTER = new IntentFilter();

        static {
            BATTERY_INTENT_FILTER.addAction(Intent.ACTION_BATTERY_CHANGED);
            BATTERY_INTENT_FILTER.addAction(Intent.ACTION_BATTERY_LOW);
            BATTERY_INTENT_FILTER.addAction(Intent.ACTION_BATTERY_OKAY);
        }
    }

    private BatteryBean getBatteryInfo(Context context) {
        try {
            Intent batteryInfoIntent = context.registerReceiver(null, BatteryIntentFilterHolder.BATTERY_INTENT_FILTER);
            if (batteryInfoIntent == null) {
                throw new MarsInvalidDataException("Can not registerReceiver for battery");
            }
            BatteryBean batteryInfo = new BatteryBean();
            batteryInfo.status = batteryInfoIntent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
            batteryInfo.health = batteryInfoIntent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN);
            batteryInfo.present = batteryInfoIntent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
            batteryInfo.level = batteryInfoIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            batteryInfo.scale = batteryInfoIntent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
            batteryInfo.plugged = batteryInfoIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
            batteryInfo.voltage = batteryInfoIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
            batteryInfo.temperature = batteryInfoIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
            batteryInfo.technology = batteryInfoIntent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
            return batteryInfo;
        } catch (Throwable e) {
            throw new MarsInvalidDataException(e);
        }
    }

    @Override
    public void stop() {
        mCompositeDisposable.dispose();
    }
}
