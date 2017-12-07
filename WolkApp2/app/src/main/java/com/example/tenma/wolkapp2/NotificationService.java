package com.example.tenma.wolkapp2;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by takuya-PC on 2017/12/06.
 */

public class NotificationService extends Service {

    public void onCreate() {
        super.onCreate();


    }

    public int onStartCommand(Intent intent, int flags, int startId) {



        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();

        Log.v("testt", "Service onDestroy()");

    }

    //使わない
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
