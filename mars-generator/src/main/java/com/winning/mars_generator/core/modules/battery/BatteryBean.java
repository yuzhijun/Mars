package com.winning.mars_generator.core.modules.battery;

import android.os.BatteryManager;

import com.winning.mars_generator.core.BaseBean;

/**
 * Created by yuzhijun on 2018/3/28.
 */
public class BatteryBean extends BaseBean{
    public static final String SPILT = "\r\n";
    //charging or full or no charge or unknown
    public int status;
    // the health of battery
    public int health;
    //if provider battery or not
    public boolean present;
    // battery level
    public int level;
    // battery scale/capacity
    public int scale;
    // the way to charging
    public int plugged;
    // battery voltage
    public int voltage;
    // battery temperature
    public int temperature;
    // battery type,such as Li-ion
    public String technology;
    @Override
    public String toString() {
        return new StringBuilder().append("statusSummary: ").append(getDisplayStatus()).append(SPILT)
                .append("health: ").append(getDisplayHealth()).append(SPILT)
                .append("present: ").append(present).append(SPILT)
                .append("level: ").append(level).append(SPILT)
                .append("scale: ").append(scale).append(SPILT)
                .append("plugged: ").append(getDisplayPlugged()).append(SPILT)
                .append("voltage: ").append(voltage / 1000.0).append(SPILT)
                .append("temperature: ").append(temperature / 10.0).append(SPILT)
                .append("technology: ").append(technology)
                .toString();
    }

    public String getDisplayStatus() {
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                return "charging";
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                return "discharging";
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                return "not charging";
            case BatteryManager.BATTERY_STATUS_FULL:
                return "full";
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
            default:
                return "unknown";
        }
    }

    public String getDisplayHealth() {
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_GOOD:
                return "good";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                return "overheat";
            case BatteryManager.BATTERY_HEALTH_DEAD:
                return "dead";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                return "voltage";
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                return "unspecified failure";
            case BatteryManager.BATTERY_HEALTH_COLD:
                return "cold";
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
            default:
                return "unknown";
        }
    }

    public String getDisplayPlugged() {
        switch (plugged) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                return "ac";
            case BatteryManager.BATTERY_PLUGGED_USB:
                return "usb";
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                return "wireless";
            default:
                return "unknown";
        }
    }
}
