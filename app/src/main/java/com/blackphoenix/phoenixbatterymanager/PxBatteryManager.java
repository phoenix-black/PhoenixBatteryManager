package com.blackphoenix.phoenixbatterymanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.blackphoenix.phoenixbatterymanager.listener.PxBatteryListener;
import com.blackphoenix.phoenixbatterymanager.listener.PxBatteryStateListener;


/**
 * Created by praba on 12/18/2017.
 */

public class PxBatteryManager {

    public static class BatteryConfig {
        public static class STATE {
            final static int STATE_UNKNOWN = 0;
            final static int CHARGING = BatteryManager.BATTERY_STATUS_CHARGING;
            final static int CHARGE_FULL = BatteryManager.BATTERY_STATUS_FULL;
            final static int USB_CHARGING = BatteryManager.BATTERY_PLUGGED_USB;
            final static int AC_CHARGING = BatteryManager.BATTERY_PLUGGED_AC;
        }
    }

    private Context _context;
    private PxBatteryListener pxBatteryLevelListener;
    private PxBatteryStateListener pxBatteryStateListener;
    private float minimumBatteryThreshold = 0.25f;
    private static float MIN_BATTERY_THRESHOLD = 0.25f;
    boolean isBatteryReceiverEnabled = false;

    public PxBatteryManager(Context context){
        this(context,0.25f);
    }

    public PxBatteryManager(Context context, float batteryThreshold){
        // Battery
        _context = context;
        minimumBatteryThreshold = batteryThreshold;
    }


    public boolean setBatteryListener(PxBatteryListener batteryListener){
        if(batteryListener!= null) {
            pxBatteryLevelListener = batteryListener;
            return true;
        }
        return false;
    }

    public boolean setBatteryStateListener(PxBatteryStateListener batteryListener){
        if(batteryListener!= null) {
            pxBatteryStateListener = batteryListener;
            if(pxBatteryLevelListener == null) {
                IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                _context.registerReceiver(mBroadcastReceiver, iFilter);
            }

            return true;
        }
        return false;
    }

    public void onResume(){
        try {
            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            _context.registerReceiver(mBroadcastReceiver, iFilter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onPause(){
        try {
            _context.unregisterReceiver(mBroadcastReceiver);
        } catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            float percentage = level / (float) scale;

            if(pxBatteryLevelListener != null){
                pxBatteryLevelListener.onBatteryLevelChanged(percentage);
            }

            int chargeStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            int chargePlug =  intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            int chargePlu2g =  intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);

            if(pxBatteryStateListener !=null){
                pxBatteryStateListener.onBatteryLevelChanged(percentage);
                pxBatteryStateListener.onBatteryChargingStatusChanged(chargeStatus,chargePlug);
            }

        }
    };


    private BroadcastReceiver mPowerConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };


    private BroadcastReceiver mBatterySignificantLevelReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

       /*
        Check whether the current battery level is greater than or equal to minimum battery level
        @Parm: Context
        @Ret: boolean
     */

    public boolean isMinimumBatteryReady(Context context) throws PxBatteryException {
        return isBatteryReady(context,minimumBatteryThreshold);
    }

    /*
        Check whether the current battery level is greater than or equal to minimum battery level
        @Parm: Context, float (minimumBatteryPercent
        @Ret: boolean
     */

    public static boolean isBatteryReady(Context context, float batteryThreshold) throws PxBatteryException {
        return (getBatteryLevel(context)>= batteryThreshold);
    }


    /*
        Check whether the current battery level is greater than or equal to minimum battery level
        @Parm: Context
        @Ret: boolean
     */

    public static boolean isBatteryReady(Context context) throws PxBatteryException {
        return (getBatteryLevel(context)>= MIN_BATTERY_THRESHOLD);
    }



    /*
        Get Battery Level in Percentage as String Value
        @Parm: Context
        @Ret: String
     */

    public static String getBatteryLevelAsString(Context context) throws PxBatteryException {
        return "" + getBatteryLevel(context)*100;
    }

    /*
        Get Battery Level as Float Value
        @Parm: Context
        @Ret: Float
     */

    public static float getBatteryLevel(Context context) throws PxBatteryException {
        Intent batteryStatusIntent = getBatteryStatusIntent(context);
        int level = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        if(level == -1 || scale == -1 || scale == 0) {
            PxBatteryException exception = new PxBatteryException("Unable to get Battery Level");
            exception.initCause(new Throwable("Either Battery Level or Battery Scale is Invalid (level,scale):("+level+","+scale+")"));
            throw exception;
        }

        return level / (float)scale;
    }

    public static boolean isBatteryCharging(Context context) throws PxBatteryException {
        int status = getBatteryChargeStatus(context);
        return  (status == BatteryConfig.STATE.USB_CHARGING
                || status == BatteryConfig.STATE.AC_CHARGING);
    }

    public static int getBatteryChargeStatus(Context context) throws PxBatteryException {
        Intent batteryStatusIntent = getBatteryStatusIntent(context);
        int status = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        if (status == -1) {
            throw new PxBatteryException("No Charging Status form Battery Manager");
        }

        if(status == BatteryManager.BATTERY_STATUS_CHARGING){
            int chargeType = getBatteryChargeType(context);
            if(chargeType == BatteryConfig.STATE.AC_CHARGING || chargeType == BatteryConfig.STATE.USB_CHARGING){
                return chargeType;
            }
        }
        return status;
    }

    public static int getBatteryChargeType(Context context) throws PxBatteryException {
        Intent batteryStatusIntent = getBatteryStatusIntent(context);
        return batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
    }


    private static Intent getBatteryStatusIntent(Context context) {
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        return context.registerReceiver(null, iFilter);
    }



}
