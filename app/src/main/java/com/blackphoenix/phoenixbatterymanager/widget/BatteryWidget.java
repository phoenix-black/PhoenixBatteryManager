package com.blackphoenix.phoenixbatterymanager.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blackphoenix.phoenixbatterymanager.PxBatteryException;
import com.blackphoenix.phoenixbatterymanager.PxBatteryManager;
import com.blackphoenix.phoenixbatterymanager.R;
import com.blackphoenix.phoenixbatterymanager.listener.PxBatteryListener;

/**
 * Created by Praba on 4/22/2018.
 */

public class BatteryWidget extends RelativeLayout {

    ImageView percent10;
    ImageView percent20;
    ImageView percent30;
    ImageView percent40;
    ImageView percent50;
    ImageView percent60;
    ImageView percent70;
    ImageView percent80;
    ImageView percent90;
    ImageView percent100;
    ImageView batteryContainer;
    TextView batteryLabel;

    PxBatteryManager batteryManager;

    public BatteryWidget(Context context) {
        super(context);
    }

    public BatteryWidget(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.BatteryWidget, 0, 0);

        int batteryPercent = a.getInteger(R.styleable.BatteryWidget_battery_percent,0);


        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater!=null) {
            inflater.inflate(R.layout.view_battery_widget, this, true);

            percent10 = findViewById(R.id.percent10);
            percent20 = findViewById(R.id.percent20);
            percent30 = findViewById(R.id.percent30);
            percent40 = findViewById(R.id.percent40);
            percent50 = findViewById(R.id.percent50);
            percent60 = findViewById(R.id.percent60);
            percent70 = findViewById(R.id.percent70);
            percent80 = findViewById(R.id.percent80);
            percent90 = findViewById(R.id.percent90);
            percent100 = findViewById(R.id.percent100);
            batteryContainer = findViewById(R.id.battery_view);
            batteryLabel = findViewById(R.id.batterytext);
            //  initBatteryStatus(batteryPercent);

            Log.e("Battery Height ",""+getHeight());
            Log.e("Battery Width ",""+getWidth());

            if(getHeight()<60){
                batteryLabel.setTextSize(14);
            }

            batteryManager = new PxBatteryManager(context);

            try {
                initBatteryStatus((int)(PxBatteryManager.getBatteryLevel(context)*100));
            } catch (PxBatteryException e) {
                e.printStackTrace();
            }

            batteryManager.setBatteryListener(new PxBatteryListener() {
                @Override
                public void onBatteryLevelChanged(float v) {

                    initBatteryStatus((int)(v*100));

                }
            });



        }


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e("Battery Height ",""+h);
        Log.e("Battery Height Spec",""+MeasureSpec.getSize(h));
        Log.e("Battery Width ",""+w);

        if(h<60){
            batteryLabel.setTextSize(12);
        } else if(h>=60 && h<100){
            batteryLabel.setTextSize(18);
        } else {
            batteryLabel.setTextSize(24);
        }
    }



   /* @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(60,60);
    }*/

    private void initBatteryStatus(int batteryPercent){

        String batteryPercentString = ""+batteryPercent+"%";
        batteryLabel.setText(batteryPercentString);
        Log.e("Battery Percent ",""+batteryPercentString);
        if(batteryPercent <=20 && batteryPercent >0){
            batteryContainer.setImageResource(R.drawable.battery_container_red_512px);
        } else if(batteryPercent <=40 && batteryPercent >20) {
            batteryContainer.setImageResource(R.drawable.battery_container_orange_512px);
        } else {
            batteryContainer.setImageResource(R.drawable.battery_container_neon_512px);
        }

        percent100.setVisibility((batteryPercent <= 100 && batteryPercent > 90) ?
                View.VISIBLE : View.INVISIBLE);
        percent90.setVisibility((batteryPercent <= 90 && batteryPercent > 80) ?
                View.VISIBLE : View.INVISIBLE);
        percent80.setVisibility((batteryPercent <= 80 && batteryPercent > 70) ?
                View.VISIBLE : View.INVISIBLE);
        percent70.setVisibility((batteryPercent <= 70 && batteryPercent > 60) ?
                View.VISIBLE : View.INVISIBLE);
        percent60.setVisibility((batteryPercent <= 60 && batteryPercent > 50) ?
                View.VISIBLE : View.INVISIBLE);
        percent50.setVisibility((batteryPercent <= 50 && batteryPercent > 40) ?
                View.VISIBLE : View.INVISIBLE);
        percent40.setVisibility((batteryPercent <= 40 && batteryPercent > 30) ?
                View.VISIBLE : View.INVISIBLE);
        percent30.setVisibility((batteryPercent <= 30 && batteryPercent > 20) ?
                View.VISIBLE : View.INVISIBLE);
        percent20.setVisibility((batteryPercent <= 20 && batteryPercent > 10) ?
                View.VISIBLE : View.INVISIBLE);
        percent10.setVisibility((batteryPercent <= 10 && batteryPercent > 0) ?
                View.VISIBLE : View.INVISIBLE);
    }

   /* public BatteryWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BatteryWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }*/

    @Override
    protected void onDetachedFromWindow() {
        if(batteryManager!=null){
            batteryManager.onPause();
        }
        super.onDetachedFromWindow();
    }

}

   /* public BatteryWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BatteryWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }*/


