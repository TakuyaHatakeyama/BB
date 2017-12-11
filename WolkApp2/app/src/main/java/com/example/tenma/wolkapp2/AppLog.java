package com.example.tenma.wolkapp2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

public class AppLog extends ActivityAddToBGMandSE{
    private SoundPool soundPool;
    private int soundId;

    CalendarView calendarView;
    TextView dateDisplay;
    Cursor c;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);



    }

    @Override
    // 画面が表示される度に実行
    protected void onResume() {
        super.onResume();

        bgmStart();

        // 予め音声データを読み込む
        soundPool = new SoundPool(50, AudioManager.STREAM_MUSIC, 0);
        soundId = soundPool.load(getApplicationContext(), R.raw.click2, 1);

        calendarView = (CalendarView) findViewById(R.id.calendarView);

        dateDisplay = (TextView) findViewById(R.id.date_display);
        dateDisplay.setText("日付をタップしてください");




        // ～～～～～～～～～～～～～カレンダー～～～～～～～～～～～～～～～～～～～～～～～～～～
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {

                // ～～～～～～～データベース～～～～～～～～

                // 作成したDataクラスに読み取り専用でアクセス
                HosuukirokuTest hkData = new HosuukirokuTest( getApplicationContext() );
                SQLiteDatabase db = hkData.getReadableDatabase();

                // タップされた年月日
                int kakunoubanngou = (i*10000) + ((i1 + 1) * 100) + i2;

                // SELECT（取得したい列） FROM（対象テーブル）WHERE（条件）※変数を使う場合「 + 変数」文字列結合
                String sql = "SELECT hizuke , hosuu , karori FROM hosuukirokuTable WHERE hizuke=" + kakunoubanngou;

                Log.v("testt","日付:" + String.valueOf( kakunoubanngou ) );

                // SQL文を実行してデータを取得
                try {
                    c = db.rawQuery(sql, null);
                    c.moveToFirst();

                    // データベースから取ってきただけのデータを、使えるように変数へセット
                    String hizukeVal = c.getString(c.getColumnIndex("hizuke"));
                    String hosuuVal = c.getString(c.getColumnIndex("hosuu"));
                    String karoriVal = c.getString(c.getColumnIndex("karori"));

                    dateDisplay.setText((i1 + 1) + " 月 " + i2 + "日の歩数は" + hosuuVal + "歩です。\n\n" + "カロリーは" + karoriVal + "㌔カロリーです");

                } catch ( Exception e){
                    dateDisplay.setText((i1 + 1) + " 月 " + i2 + "日のデータがありません。");
                } finally {
                    // クローズ処理
                    c.close();
                    db.close();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        bgmPause();
    }

    public void back(View view) {
        //ボタンの音
        soundPool.play(soundId, 1f, 1f, 0, 0, 1);    //音の大きさは0fから1fで調整できる
        //インテントの作成
        Intent intent = new Intent(this, AppTitle.class);
        //遷移先の画面を起動
        startActivity(intent);
    }

}