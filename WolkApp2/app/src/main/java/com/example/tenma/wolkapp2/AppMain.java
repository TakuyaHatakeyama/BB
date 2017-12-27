package com.example.tenma.wolkapp2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.R.attr.data;


public class AppMain extends AppCompatActivity implements View.OnClickListener{

    boolean nowMessageDisp;
    SharedPreferences.Editor editor2;
    MediaPlayer bgm;

    SharedPreferences data;
    SharedPreferences.Editor dateEditor;

    Cursor c;

    public void back(View view) {
        //ボタンの音
        soundPool.play(soundId, 1f, 1f, 0, 0, 1);    //音の大きさは0fから1fで調整できる

        //インテントの作成
        Intent intent = new Intent(this, AppTitle.class);
        //遷移先の画面を起動
        startActivity(intent);

        //Activityの終了
        finishAndRemoveTask();
    }

    private ImageButton start,stop;

    private TextView mStepCounterText;
    private SensorManager mSensorManager;
    private Sensor mStepCounterSensor;

    //gif
    ImageView imageView;
    GlideDrawableImageViewTarget target;

    //前回の不必要歩数
    private float beforedust;   // 歩数がない → センサーの値
    private float beforestopfirst;  // 前回の値がない場合 → -1

    private SoundPool soundPool;

    //Android終了時・起動時の処理
    public final static class mReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            SharedPreferences pref = context.getSharedPreferences("file", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            String action = intent.getAction();
            if(action.equals("android.intent.action.ACTION_SHUTDOWN")) {
                Log.v("testt", "-----SHUTDOWN-----");


//                // ----------------------------------------
//
//                // データを入れる処理
//                editor.putBoolean("yesterday", true);
//                editor.apply();
//
//                //必要ないセンサの累積歩数を入れる（起動時は必ず）
//                float dust = pref.getFloat("runningSensor", 0);
//                //不要歩数の上書き（前回終了時に歩数stepsがある場合）
//                if(beforedust != 0) {
//                    dust = beforedust;
//                }
//                Log.v("testt", "前回の不必要歩数の総和[beforedust]" + beforedust);
//
//                //起動時、ストップボタンを押している状態にする
//                stopfirst = se.values[0];
//                //ストップボタンを押し、加算された状態でアプリ終了した場合
//                if(beforestopfirst > -1) {
//                    //前回、ストップボタンを押した時のセンサの値
//                    stopfirst = beforestopfirst;
//                    Log.v("testt", "※※※ストップを押して終了※※※");
//                    Log.v("testt", "ストップを押した時[beforestopfirst]：" + beforestopfirst);
//
//                    stopsteps = se.values[0] - stopfirst;
//                    dust += stopsteps;
//
//                    //初期化(この処理をしないとスタートボタンを押した時に重複処理になる)
//                    stopfirst = se.values[0];
//
//                    Log.v("testt", "stop中に増えた歩数[stopsteps]：" + stopsteps);
//                    Log.v("testt", "stopstepsを足す[dust(起動時)]：" + dust);
//                    Log.v("testt", "※※※※※※");
//
//                }
//
//                //最初に表示したい歩数の計算
//                steps = se.values[0] - dust;
//
//                //データの記録
//                SetData sd = new SetData(context.getApplicationContext(), steps);
//
//
//                // ----------------------------------------


                Log.v("testt", "runningstartflag " + pref.getBoolean("runningstartflag", false));

                //スタートボタンが押された状態でandroid再起動()
                if(pref.getBoolean("runningstartflag", false)) {

                    Log.v("testt", "takuyaaaaaaaaaaaaaaa");

                    Log.v("testt", "卍卍卍卍卍beforestopfirst(センサ)" + pref.getFloat("runningSensor", 0));

                    Log.v("testt", "スタートが押された状態でシャットダウン");


                    editor.putFloat("beforestopfirst", pref.getFloat("runningSensor", 0));
                    editor.apply();

                    editor.putBoolean("shutdown", true);
                    editor.apply();

                }
                Log.v("testt", "[センサ]" + pref.getFloat("runningSensor", 0));
                Log.v("testt", "[beforestopfirst]" + pref.getFloat("beforestopfirst", -1));
                Log.v("testt", "-----SHUTDOWN-----");

                onstopflag = true;

            }
            if(action.equals("android.intent.action.BOOT_COMPLETED")) {
                Log.v("testt", "BOOT_COMPLETED");
                Toast.makeText(context, "-----BOOT_COMPLETED-----", Toast.LENGTH_SHORT).show();

                if(pref.getBoolean("shutdown", false)) {

                    Log.v("testt", "& shutdownのPreferenceが成功");

                    //shutdownの初期化
                    editor.putBoolean("shutdown", false);
                    editor.apply();

                    //アプリ起動時に動作させるif分に入る
                    editor.putBoolean("bootcompleted", true);
                    editor.apply();

                }
            }
            if(action.equals("android.intent.action.DATE_CHANGED")) {
                Log.v("testt", "DATE_CHANGED DATE_CHANGED DATE_CHANGED");

                // データを入れる処理
                editor.putBoolean("yesterday", true);
                editor.apply();



                // dust更新

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //歩数のフォント変える
        TextView text1 = (TextView)findViewById(R.id.pedometer);
        text1.setTypeface(Typeface.createFromAsset(getAssets(), "chibit.ttf"));
        TextView text2 = (TextView)findViewById(R.id.walk);
        text2.setTypeface(Typeface.createFromAsset(getAssets(), "GD-DOTFONT-DQ-TTF_008.ttf"));
        TextView text3 = (TextView)findViewById(R.id.syouhi);
        text3.setTypeface(Typeface.createFromAsset(getAssets(), "GD-DOTFONT-DQ-TTF_008.ttf"));
        TextView text4 = (TextView)findViewById(R.id.end);
        text4.setTypeface(Typeface.createFromAsset(getAssets(), "GD-DOTFONT-DQ-TTF_008.ttf"));
        TextView text5 = (TextView)findViewById(R.id.value);
        text5.setTypeface(Typeface.createFromAsset(getAssets(), "chibit.ttf"));

        //メイン画面で表示するカロリーのフォントの色と透明度
        TextView tv = (TextView) findViewById(R.id.value);
        tv.setText("0");
        TextView tv2 = (TextView) findViewById(R.id.syouhi);
        TextView tv3 = (TextView) findViewById(R.id.end);

//        //フォントの色
//        tv.setTextColor(Color.WHITE);
//        tv2.setTextColor(Color.WHITE);
//        tv3.setTextColor(Color.WHITE);

        //スタートボタン
        start = (ImageButton) findViewById(R.id.IBstart);
        start.setOnClickListener(this);

        //ストップボタン
        stop = (ImageButton) findViewById(R.id.IBstop);
        stop.setOnClickListener(this);

        //歩数表示
        mStepCounterText = (TextView) findViewById(R.id.pedometer);

        //プリファレンスのインスタンス取得
        //前回不必要歩数の取得（歩数stepsがある場合）
        SharedPreferences pref = getSharedPreferences("file", MODE_PRIVATE);
        beforedust = pref.getFloat("beforedust", 0);
        //ストップの間に加算された歩数があり、終了した場合
        beforestopfirst = pref.getFloat("beforestopfirst", -1);

        //android起動時の処理
        if(pref.getBoolean("bootcompleted", false)) {

            Log.v("testt", "Android起動！！！！！！！！！！！！！！！");

            //Serviceを起動
            Intent intent = new Intent(getApplication(), NotificationService.class);
            startService(intent);

            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("bootcompleted", false);
            editor.apply();
        }
        nowMessageDisp = false;

        //キャラクターの初期表示（歩いているか止まっているか）
        //止まっている状態（デフォルト）
        imageView = (ImageView) findViewById(R.id.gifView);
        //target = new GlideDrawableImageViewTarget(imageView);
        Glide.with(this).load(R.raw.main_gif_stop2).into(imageView);
        //歩いている状態
        if(pref.getBoolean("runningstartflag", false)) {

            imageView = (ImageView) findViewById(R.id.gifView);
            //target = new GlideDrawableImageViewTarget(imageView);
            Glide.with(this).load(R.raw.main_gif_walk).into(imageView);
        }

    }

    protected void onResume() {
        super.onResume();

        //リソースファイルから再生
        bgm = MediaPlayer.create(this, R.raw.main_b);
        bgm.start();
        bgm.setLooping(true);

        // 予め音声データを読み込む
        soundPool = new SoundPool(50, AudioManager.STREAM_MUSIC, 0);
        soundId = soundPool.load(getApplicationContext(), R.raw.click2, 1);

        //吹き出しの透明化
        findViewById(R.id.hukidasi).setVisibility(View.INVISIBLE);
        findViewById(R.id.value).setVisibility(View.INVISIBLE);
        findViewById(R.id.syouhi).setVisibility(View.INVISIBLE);
        findViewById(R.id.end).setVisibility(View.INVISIBLE);

        //KITKAT以上かつTYPE_STEP_COUNTERが有効ならtrue
        boolean isTarget = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER);

        if (isTarget) {
            //TYPE_STEP_COUNTERが有効な場合の処理
            Log.d("hasStepCounter", "STEP-COUNTER is available!!!");
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mStepCounterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

            setStepCounterListener();
        } else {
            //TYPE_STEP_COUNTERが無効な場合の処理
            Log.d("hasStepCounter", "STEP-COUNTER is NOT available.");
            mStepCounterText.setText("計測不可端末");
            mStepCounterText.setTypeface(null);
            mStepCounterText.setTextSize(20);
            mStepCounterText.setTextColor(Color.RED);

            TextView walk = (TextView)findViewById(R.id.walk);
            walk.setTypeface(null);
            walk.setText("");

        }

    }

    static boolean onstopflag = false;

    protected  void  onStart() {
        super.onStart();

        if(onstopflag) {
            onstopflag = false;
        }
    }

    protected void onStop() {
        super.onStop();

        bgm.pause();
        bgm.release();
        bgm = null;

        SharedPreferences pref = getSharedPreferences("file", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        //onDestroy()時にonStop()が呼び出されてしまうのでflag処理
        if(!onstopflag) {

            Log.v("testt", "-----onStop()が呼ばれました-----");
            Log.v("testt", "[steps]" + steps);
            Log.v("testt", "[stopfirst]" + stopfirst);

            //歩数がある場合の保存
            if(steps > 0) {
                beforedust = dust;
            }else {
                beforedust = pref.getFloat("runningSensor", 0);
            }
            //増やした数を再び保存
            pref = getSharedPreferences("file", MODE_PRIVATE);
            editor = pref.edit();
            editor.putFloat("beforedust", beforedust);
            editor.apply();
            Log.v("testt", "[beforedust]" + beforedust);

            //ストップの間に加算された歩数があり、終了した場合
            if(stopflag) {
                Log.v("testt", "※ストップが押されてる状態でonStop()");
                beforestopfirst = stopfirst;
            }else {
                beforestopfirst = -1;
            }
            //増やした数を再び保存
            editor = pref.edit();
            editor.putFloat("beforestopfirst", beforestopfirst);
            editor.apply();

            Log.v("testt", "[beforestopfirst]" + beforestopfirst);
            Log.v("testt", "[センサ]" + se.values[0]);
            Log.v("testt", "-----onStop()が呼ばれました[終了]-----");

            onstopflag = true;
        }
    }

//    //呼び出されないことがあるので使用しない
//    @Override
//    protected void onDestroy() {
//       super.onDestroy();
//
//    }

    private void setStepCounterListener() {
        if (mStepCounterSensor != null) {
            //ここでセンサーリスナーを登録する
            mSensorManager.registerListener(mStepCountListener, mStepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    private SensorEvent se;
    private int soundId;

    //スタート・ストップ・リセットの状態
    private boolean startflag = false;
    private boolean stopflag = false;

    //現在の歩数
    private float steps = 0;

    //起動時を表す（1度だけ使用）
    private int first = 0;

    //アプリ起動以前に記録された歩数、および不必要歩数の総和
    private float dust = 0;
    //ストップが押された時の[センサの値]
    private float stopfirst = 0;
    //ストップが押されている間の歩数（不必要歩数）
    //(スタートが押された瞬間の[センサの値]) - stopdust で求める
    private float stopsteps = 0;



    private final SensorEventListener mStepCountListener = new SensorEventListener() {

        //センサーから歩数を取得し、表示するメソッド
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            se = sensorEvent;

            SharedPreferences pref = getSharedPreferences("file", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putFloat("runningSensor", se.values[0]);
            editor.apply();

            Log.v("testt", "[センサ]：" + se.values[0]);

            //アプリ起動直後の処理
            //[0歩]もしくは、[前回の累積歩数]を表示
            if(first == 0) {
                firstprocessing();
                mStepCounterText.setText(String.format(Locale.US, "%d", (int)steps));

            }
            //スタートボタンが押されている時
            else if(startflag) {

                //歩数表示を増加させる
                //wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww
                steps = se.values[0] - dust;
                mStepCounterText.setText(String.format(Locale.US, "%d", (int)steps));
                //wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww

                //dustをNotificationServiceに渡す
                pref = getSharedPreferences("file", MODE_PRIVATE);
                editor = pref.edit();
                editor.putFloat("runningdust", dust);
                editor.apply();

//                //データの記録
//                SetData sd = new SetData(getApplicationContext(), steps);
            }
            //ストップボタンが押されている時
            else if(stopflag) {

                //歩数表示は変化させず維持する（ストップが押される直前の歩数を表示し続けるだけ）

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }

    };

    public void firstprocessing() {

        //必要ないセンサの累積歩数を入れる（起動時は必ず）
        dust = se.values[0];
        //不要歩数の上書き（前回終了時に歩数stepsがある場合）
        if(beforedust != 0) {
            dust = beforedust;
        }
        Log.v("testt", "前回の不必要歩数の総和[beforedust]" + beforedust);

        //起動時、ストップボタンを押している状態にする
        stopfirst = se.values[0];
        //ストップボタンを押し、加算された状態でアプリ終了した場合
        if(beforestopfirst > -1) {
            //前回、ストップボタンを押した時のセンサの値
            stopfirst = beforestopfirst;
            Log.v("testt", "※※※ストップを押して終了※※※");
            Log.v("testt", "ストップを押した時[beforestopfirst]：" + beforestopfirst);

            stopsteps = se.values[0] - stopfirst;
            dust += stopsteps;

            //初期化(この処理をしないとスタートボタンを押した時に重複処理になる)
            stopfirst = se.values[0];

            Log.v("testt", "stop中に増えた歩数[stopsteps]：" + stopsteps);
            Log.v("testt", "stopstepsを足す[dust(起動時)]：" + dust);
            Log.v("testt", "※※※※※※");

        }

        //最初に表示したい歩数の計算
        steps = se.values[0] - dust;

        //状態の初期化（ストップを押している状態）
        startflag = false;
        stopflag = true;

        //（起動2回目以降）スタートが押された状態で終了
        SharedPreferences pref = getSharedPreferences("file", MODE_PRIVATE);
        if(pref.getBoolean("runningstartflag", false)) {
            startflag = true;
            stopflag = false;

            teststart = (ImageButton) findViewById(R.id.IBstart);
            teststart.setImageResource(R.drawable.main_kanban);
            teststart = (ImageButton) findViewById(R.id.IBstop);
            teststart.setImageResource(R.drawable.main_stop);
        }

        //初回起動時の処理のため、以降このif文に入らないようにする
        first++;
    }

    ImageButton teststart;

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            //スタートボタン
            case R.id.IBstart:
                //ストップが押されたときに押せる
                if(stopflag) {
                    //ボタンの音
                    soundPool.play(soundId, 1f, 1f, 0, 0, 1);    //音の大きさは0fから1fで調整できる
                    //Toast.makeText(this, "スタート！", Toast.LENGTH_SHORT).show();

                    //スタート・ストップボタンの画像変更
                    teststart = (ImageButton) findViewById(R.id.IBstart);
                    teststart.setImageResource(R.drawable.main_kanban);
                    teststart = (ImageButton) findViewById(R.id.IBstop);
                    teststart.setImageResource(R.drawable.main_stop);

                    //gif
                    imageView = (ImageView) findViewById(R.id.gifView);
                    //target = new GlideDrawableImageViewTarget(imageView);
                    Glide.with(this).load(R.raw.main_gif_walk).into(imageView);

                    Log.v("testt", "-----スタートボタンが押されました-----");
                    Log.v("testt", "いらない歩数[dust(変化前)]" + dust);

                    //歩数計算
                    stopsteps = se.values[0] - stopfirst;
                    dust += stopsteps;  //不必要歩数

                    Log.v("testt", "stop中に増えた歩数[stopsteps]" + stopsteps + " = [センサ]" + se.values[0] + " - [stopfirst]" + stopfirst);
                    Log.v("testt", "いらない歩数[dust(変化後)]" + dust + " = [dust(変化前)]" + (dust - stopsteps) + " + [stopsteps]" + stopsteps);
                    Log.v("testt", "歩数[steps]" + steps + " = [センサ]" + se.values[0] + " - [dust(変化後)]" + dust);
                    Log.v("testt", "~~~スタートボタンが押されました[終了]~~~");

                    //状態変更
                    startflag = true;
                    stopflag = false;

                    //startflagをNotificationServiceに渡す
                    SharedPreferences pref = getSharedPreferences("file", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("runningstartflag", startflag);
                    editor.apply();

                    //Serviceを起動
                    Intent intent = new Intent(getApplication(), NotificationService.class);
                    startService(intent);

                }
                break;

            //ストップボタン
            case R.id.IBstop:

                if(startflag) {
                    //ボタンの音
                    soundPool.play(soundId, 1f, 1f, 0, 0, 1);    //音の大きさは0fから1fで調整できる

                    //スタート・ストップ・リセットボタンの画像変更
                    teststart = (ImageButton) findViewById(R.id.IBstart);
                    teststart.setImageResource(R.drawable.main_start);
                    teststart = (ImageButton) findViewById(R.id.IBstop);
                    teststart.setImageResource(R.drawable.main_kanban);

                    //gif
                    imageView = (ImageView) findViewById(R.id.gifView);
                    //target = new GlideDrawableImageViewTarget(imageView);
                    Glide.with(this).load(R.raw.main_gif_stop2).into(imageView);

                    //歩数計算
                    stopfirst = se.values[0];

                    //状態変更
                    startflag = false;
                    stopflag = true;

                    //startflagをNotificationServiceに渡す
                    SharedPreferences pref = getSharedPreferences("file", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("runningstartflag", startflag);
                    editor.apply();

                    //Serviceを停止
                    Intent intent = new Intent(getApplication(), NotificationService.class);
                    stopService(intent);

                    //ストップボタンを押してる際の通知（非常駐）
                    sendNotification();

                }
                break;

        }
    }

    public void serif(View view) {

        // 作成したDataクラスに読み取り専用でアクセス
        HosuukirokuTest hkData = new HosuukirokuTest( getApplicationContext() );
        SQLiteDatabase db = hkData.getReadableDatabase();

        // セリフが出てなければ表示する


        //日付の取得
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String strDate = sdf.format(cal.getTime());
        int kakunoubanngou = Integer.parseInt(strDate);

        // SELECT（取得したい列） FROM（対象テーブル）WHERE（条件）※変数を使う場合「 + 変数」文字列結合
        String sql = "SELECT hizuke , hosuu , karori FROM hosuukirokuTable WHERE hizuke=" + kakunoubanngou;
        if( !nowMessageDisp ){
            //表示
            findViewById(R.id.hukidasi).setVisibility(View.VISIBLE);
            findViewById(R.id.value).setVisibility(View.VISIBLE);
            findViewById(R.id.syouhi).setVisibility(View.VISIBLE);
            findViewById(R.id.end).setVisibility(View.VISIBLE);
            nowMessageDisp = true;
            // SQL文を実行してデータを取得
            try {
                c = db.rawQuery(sql, null);
                c.moveToFirst();
                String karoriVal = c.getString(c.getColumnIndex("karori"));
                BigDecimal bd = new BigDecimal(karoriVal);
                BigDecimal bd1 = bd.setScale(0, BigDecimal.ROUND_HALF_UP);  //小数第１位

                TextView value = (TextView) findViewById(R.id.value);
                value.setText(String.valueOf(bd1.intValue()) + "kcal");

                TextView end = (TextView) findViewById(R.id.end);
                end.setText("だじょ～");

            } finally {
                // クローズ処理
                c.close();
                db.close();
            }


        }
        else{
            //非表示
            findViewById(R.id.hukidasi).setVisibility(View.INVISIBLE);
            findViewById(R.id.value).setVisibility(View.INVISIBLE);
            findViewById(R.id.syouhi).setVisibility(View.INVISIBLE);
            findViewById(R.id.end).setVisibility(View.INVISIBLE);
            nowMessageDisp = false;
        }
    }

    private void sendNotification() {

        Intent notificationIntent = new Intent(this, AppTitle.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,notificationIntent,0);

        Notification.Builder builder = new Notification.Builder(this);

        NotificationManager manager= (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        builder.setSmallIcon(R.mipmap.icon);

        builder.setContentTitle("すとっぷぼたんがおされてるよ");
        builder.setContentText("きょうのほすう　→　" + (int)steps);

        builder.setDefaults(Notification.PRIORITY_DEFAULT);
        builder.setContentIntent(contentIntent);

        manager.notify(1,builder.build());

    }

}
