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
    SharedPreferences kiroku;
    SharedPreferences.Editor kirokuEditor;

    Cursor c;


    public void back(View view) {
        //ボタンの音
        soundPool.play(soundId, 1f, 1f, 0, 0, 1);    //音の大きさは0fから1fで調整できる
        //インテントの作成
        Intent intent = new Intent(this, AppTitle.class);
        //遷移先の画面を起動
        startActivity(intent);

        //データの記録
        setStepsData();

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
    float beforedust;
    static float beforestopfirst;

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
                //Toast.makeText(context, "-----SHUTDOWN-----", Toast.LENGTH_SHORT).show();

                if(pref.getFloat("beforestopfirst", -1) == -1) {

                    Log.v("testt", "スタートが押された状態でシャットダウン");

                    editor.putFloat("beforestopfirst", se.values[0]);
                    editor.apply();

                    editor.putBoolean("shutdown", true);
                    editor.apply();

                }
                Log.v("testt", "[センサ]" + se.values[0]);
                Log.v("testt", "[beforestopfirst]" + pref.getFloat("beforestopfirst", -1));
                Log.v("testt", "-----SHUTDOWN-----");

            }
            if(action.equals("android.intent.action.BOOT_COMPLETED")) {
                Log.v("testt", "BOOT_COMPLETED");
                //Toast.makeText(context, "-----BOOT_COMPLETED-----", Toast.LENGTH_SHORT).show();

                if(pref.getBoolean("shutdown", false)) {

                    Log.v("testt", "& shutdownのPreferenceが成功");

                    editor.putBoolean("bootcompleted", true);
                    editor.apply();

                    editor.putBoolean("shutdown", false);
                    editor.apply();

                }
            }
            if(action.equals("android.intent.action.DATE_CHANGED")) {
                //stepsの値が0より大きい時
                if(se.values[0] - pref.getFloat("beforedust", 0) > 0) {

                    //日付の取得
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    String strDate = sdf.format(cal.getTime());
                    int intDate = Integer.parseInt(strDate);

                    //SQLに日付(yyyymmdd)と歩数を入れる


                }

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

        //メイン画面で表示するカロリーのフォントの色と透明度
        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setText("0");

        //フォントの色
        tv.setTextColor(Color.WHITE);

        //ジャイロセンサー起動　歩数計測スタート
        start = (ImageButton) findViewById(R.id.IBstart);
        start.setOnClickListener(this);

        //ジャイロセンサー停止　歩数計測ストップ
        stop = (ImageButton) findViewById(R.id.IBstop);
        stop.setOnClickListener(this);

        //歩数
        mStepCounterText = (TextView) findViewById(R.id.pedometer);

        //プリファレンスのインスタンス取得
        //前回不必要歩数の取得（歩数stepsがある場合）
        SharedPreferences pref = getSharedPreferences("file", MODE_PRIVATE);
        beforedust = pref.getFloat("beforedust", -1);
        //ストップの間に加算された歩数があり、終了した場合
        beforestopfirst = pref.getFloat("beforestopfirst", -1);

        if(pref.getBoolean("bootcompleted", false)) {

            Log.v("testt", "Android起動！！！！！！！！！！！！！！！");

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
        if(pref.getBoolean("beforestartbutton", false)) {

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
            mStepCounterText.setText("STEP-COUNTER is NOT available.");
        }

        // 予め音声データを読み込む
        soundPool = new SoundPool(50, AudioManager.STREAM_MUSIC, 0);
        soundId = soundPool.load(getApplicationContext(), R.raw.click2, 1);

        findViewById(R.id.imageView8).setVisibility(View.INVISIBLE);
        findViewById(R.id.textView).setVisibility(View.INVISIBLE);
    }

    boolean onstopflag = false;

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

        //スタートボタンが押された状態で終了した
        SharedPreferences pref = getSharedPreferences("file", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("beforestartbutton", true);
        editor.apply();

        //onDestroy()時にonStop()が呼び出されてしまうのでflag処理
        if(!onstopflag) {

            Log.v("testt", "-----onStop()が呼ばれました-----");
            Log.v("testt", "[steps]" + steps);
            Log.v("testt", "[stopfirst]" + stopfirst);

            //歩数がある場合の保存
            if(steps > 0) {
                beforedust = dust;
            }else {
                beforedust = -1;
            }
            //増やした数を再び保存
            pref = getSharedPreferences("file", MODE_PRIVATE);
            editor = pref.edit();
            editor.putFloat("beforedust", beforedust);
            editor.apply();
            Log.v("testt", "[beforedust]" + beforedust);

            //ストップの間に加算された歩数があり、終了した場合
            if(stopflag) {
                Log.v("testt", "※ストップが押されてる状態でOnstop()");
                beforestopfirst = stopfirst;

                //スタートボタンが押されていない状態で終了した
                pref = getSharedPreferences("file", MODE_PRIVATE);
                editor = pref.edit();
                editor.putBoolean("beforestartbutton", false);
                editor.apply();

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

        //データの記録
        setStepsData();
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

    private static SensorEvent se;
    private int soundId;

    //スタート・ストップ・リセットの状態
    boolean startflag = false;
    boolean stopflag = false;
    boolean resetflag = false;

    //現在の歩数
    private float steps = 0;

    //起動時を表す（1度だけ使用）
    int first = 0;

    //アプリ起動以前に記録された歩数、および不必要歩数の総和
    float dust = 0;
    //ストップが押された時の[センサの値]
    float stopfirst = 0;
    //ストップが押されている間の歩数（不必要歩数）
    //(スタートが押された瞬間の[センサの値]) - stopdust で求める
    float stopsteps = 0;



    private final SensorEventListener mStepCountListener = new SensorEventListener() {

        //センサーから歩数を取得し、表示するメソッド
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            se = sensorEvent;

            Log.v("testt", "[センサ]：" + se.values[0]);

            //アプリ起動直後の処理
            //[0歩]もしくは、[前回の累積歩数]を表示
            if(first == 0) {
                //必要ないセンサの累積歩数を入れる（起動時は必ず）
                dust = se.values[0];
                //不要歩数の上書き（前回終了時に歩数stepsがある場合）
                if(beforedust > 0) {
                    dust = beforedust;
                }
                Log.v("testt", "前回の不必要歩数の総和[beforedust]" + beforedust);

                //起動時、ストップボタンを押している状態にする
                stopfirst = se.values[0];
                //ストップボタンを押し、加算された状態でアプリ終了した場合
                if(beforestopfirst > 0) {
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

                mStepCounterText.setText(String.format(Locale.US, "%d", (int)steps));

                //状態の初期化（ストップを押している状態）
                startflag = false;
                stopflag = true;
                resetflag = true;

                SharedPreferences pref = getSharedPreferences("file", MODE_PRIVATE);
                //（起動2回目以降）スタートが押された状態で終了
                if(pref.getBoolean("beforestartbutton", false)) {
                    startflag = true;
                    stopflag = false;
                    resetflag = true;

                    teststart = (ImageButton) findViewById(R.id.IBstart);
                    teststart.setImageResource(R.drawable.start2);
                    teststart = (ImageButton) findViewById(R.id.IBstop);
                    teststart.setImageResource(R.drawable.stop1);
                }

                //初回起動時の処理のため、以降このif文に入らないようにする
                first++;
            }
            //スタートボタンが押されている時
            else if(startflag) {

                //歩数表示を増加させる
                //wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww
                steps = se.values[0] - dust;
                mStepCounterText.setText(String.format(Locale.US, "%d", (int)steps));
                //wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww

                sendNotification();

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
                    teststart.setImageResource(R.drawable.start2);
                    teststart = (ImageButton) findViewById(R.id.IBstop);
                    teststart.setImageResource(R.drawable.stop1);

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

                }
                break;

            //ストップボタン
            case R.id.IBstop:

                if(startflag) {
                    //ボタンの音
                    soundPool.play(soundId, 1f, 1f, 0, 0, 1);    //音の大きさは0fから1fで調整できる
                    //Toast.makeText(this, "ストップ！", Toast.LENGTH_SHORT).show();

                    //スタート・ストップ・リセットボタンの画像変更
                    teststart = (ImageButton) findViewById(R.id.IBstart);
                    teststart.setImageResource(R.drawable.start1);
                    teststart = (ImageButton) findViewById(R.id.IBstop);
                    teststart.setImageResource(R.drawable.stop2);

                    //gif
                    imageView = (ImageView) findViewById(R.id.gifView);
                    //target = new GlideDrawableImageViewTarget(imageView);
                    Glide.with(this).load(R.raw.main_gif_stop2).into(imageView);

                    //歩数計算
                    stopfirst = se.values[0];

                    //状態変更
                    startflag = false;
                    stopflag = true;

                }
                break;

        }
    }

    public void setStepsData() {

        Log.v("testt", "SQLにデータを格納します。");

        String sql = "";

        HosuukirokuTest hkData = new HosuukirokuTest( getApplicationContext() );
        SQLiteDatabase db = hkData.getReadableDatabase();

        // 設定画面で決めたPreferencesのファイル「STATUS」より、身長と体重の読み込み
        data = getSharedPreferences("STATUS",MODE_PRIVATE);
        // PreferencesのdataEditorが操作できるようにする
        dateEditor = data.edit();

        // データ呼び出し。STATUSファイルのSintyouとTaizyuuをキーにして、値を取り出す
        int sintyouInt = data.getInt("Sintyou", 0);
        int taizyuuInt = data.getInt("Taizyuu", 0);

        // int型にキャスト
        final int hosuu = (int)steps;

        // カロリー計算
        int hohaba = sintyouInt - 100 ;
        double kyori = hohaba * hosuu / 100000.0 * 100;
        kyori = Math.round( kyori );
        double kekka = kyori / 100;
        double calorie =  kekka * taizyuuInt;

        // SQLに、その日の日付と歩数とカロリーを追加
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String strDate = sdf.format(cal.getTime());
        int intDate = Integer.parseInt(strDate);

        // Exceptionを吐くかで、データの有無をチェック(あまり推奨されないやり方。機能実現優先。誰か変えて)
        try {
            String dateDualChk = "SELECT hizuke FROM hosuukirokuTable WHERE hizuke=" + intDate;

            // 今日の日付のデータが無ければ、Exceptionを出力してcatchに飛ぶ
            c = db.rawQuery(dateDualChk, null);
            c.moveToFirst();

            String hizukeVal = c.getString(c.getColumnIndex("hizuke"));

            if( hizukeVal.equals( String.valueOf( intDate ) )) {
                sql = "UPDATE hosuukirokuTable SET hosuu = " + hosuu + " , karori = " + calorie + " WHERE hizuke=" + intDate;

                c = db.rawQuery(sql, null);
                c.moveToFirst();
            }
        } catch ( Exception e){
            sql = "INSERT INTO hosuukirokuTable( hizuke , hosuu , karori )values(" + intDate + " ," + hosuu + "," + calorie +")";

            c = db.rawQuery(sql, null);
            c.moveToFirst();
        }
        finally {
            // クローズ処理
            c.close();
            db.close();
        }
    }

    public void serif(View view){


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
            findViewById(R.id.imageView8).setVisibility(View.VISIBLE);
            findViewById(R.id.textView).setVisibility(View.VISIBLE);
            nowMessageDisp = true;
            // SQL文を実行してデータを取得
            try {
                c = db.rawQuery(sql, null);
                c.moveToFirst();
                String karoriVal = c.getString(c.getColumnIndex("karori"));
                TextView tv = (TextView) findViewById(R.id.textView);
                tv.setText(karoriVal);
            } finally {
                // クローズ処理
                c.close();
                db.close();
            }


        }
        else{
            findViewById(R.id.imageView8).setVisibility(View.INVISIBLE);
            findViewById(R.id.textView).setVisibility(View.INVISIBLE);
            nowMessageDisp = false;
        }
    }

    private void sendNotification() {

        Intent notificationIntent = new Intent(this, AppMain.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,notificationIntent,0);

        Notification.Builder builder = new Notification.Builder(this);

        NotificationManager manager= (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        builder.setSmallIcon(R.mipmap.aikon);
        builder.setContentTitle("");

        SharedPreferences pref = getSharedPreferences("file", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        builder.setContentText("歩数" + steps);
        builder.setDefaults(Notification.PRIORITY_DEFAULT);
        builder.setContentIntent(contentIntent);
//        manager.flags = Notification.FLAG_ONGOING_EVENT; // 常駐

        manager.notify(1,builder.build());

    }
}
