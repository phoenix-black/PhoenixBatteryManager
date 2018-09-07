package com.blackphoenix.phoenixbatterymanager.listener;

/**
 * Created by Praba on 1/13/2018.
 */
public interface PxBatteryStateListener {
    void onBatteryLevelChanged(float batteryLevel);
    void onBatteryChargingStatusChanged(int chargeStatus, int chargingType);
}
