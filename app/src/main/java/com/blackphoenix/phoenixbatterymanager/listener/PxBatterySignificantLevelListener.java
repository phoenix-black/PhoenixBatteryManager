package com.blackphoenix.phoenixbatterymanager.listener;

/**
 * Created by Praba on 1/13/2018.
 */

public interface PxBatterySignificantLevelListener {
    void onBatterySignificantLevelChanged(boolean isLow, boolean isOkay);
}
