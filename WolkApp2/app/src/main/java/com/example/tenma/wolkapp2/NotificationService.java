package com.example.tenma.wolkapp2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageButton;

import java.util.Locale;

public class NotificationService extends Service {

    private float steps;
    private SensorEvent se;
    private SensorManager mSensorManager;
    private Sensor mStepCounterSensor;

    public void onCreate() {
        super.onCreate();

        Log.v("testt", "-----Notification onCreate()-----");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        setStepCounterListener();

        return super.onStartCommand(intent, flags, startId);
//        return START_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();

        Log.v("testt", "-----NotificationService onDestroy()-----");

    }

    //使わない
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    private void setStepCounterListener() {
        if (mStepCounterSensor != null) {
            //ここでセンサーリスナーを登録する
            mSensorManager.registerListener(mStepCountListener, mStepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    private final SensorEventListener mStepCountListener = new SensorEventListener() {

        //センサーから歩数を取得し、表示するメソッド
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            se = sensorEvent;

            SharedPreferences pref = getSharedPreferences("file", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putFloat("runningSensor", se.values[0]);
            editor.apply();

            Log.v("testt", "[(通知)センサ]：" + se.values[0]);

            //スタートボタンが押されている時
            if(pref.getBoolean("runningstartflag", false)) {

                //歩数表示を増加させる
                //wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww
                steps = se.values[0] - pref.getFloat("runningdust", -1);
                Log.v("testt", "[(通知)runningdust]：" + pref.getFloat("runningdust", -1));
                //wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww

                //スタートを押してる際の通知（常駐）
                sendNotification();

                //データの記録
                SetData sd = new SetData(getApplicationContext(), steps);

            }
            //ストップボタンが押されている時
            else {

                //歩数表示は変化させず維持する（ストップが押される直前の歩数を表示し続けるだけ）

            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }

    };

    private void sendNotification() {

        Intent notificationIntent = new Intent(this, AppTitle.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,notificationIntent,0);

        Notification.Builder builder = new Notification.Builder(this);

        NotificationManager manager= (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        builder.setSmallIcon(R.mipmap.icon);

        builder.setContentTitle("ほすうをけいそくちゅう");
        builder.setContentText("きょうのほすう　→　" + (int)steps);

        builder.setDefaults(Notification.PRIORITY_DEFAULT);
        builder.setContentIntent(contentIntent);

        manager.notify(1,builder.build());

    }
}