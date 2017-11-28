package com.example.tenma.wolkapp2;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;

public class AppTitle extends ActivityAddToBGMandSE {
    MediaPlayer bgm;
    private SoundPool soundPool;
    private int soundId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);


    }

    @Override
    // 画面が表示される度に実行
    protected void onResume() {
        super.onResume();

        // 予め音声データを読み込む
        soundPool = new SoundPool(50, AudioManager.STREAM_MUSIC, 0);
        soundId = soundPool.load(getApplicationContext(), R.raw.click2, 1);

        bgmStart();
    }

    @Override
    protected void onPause() {
        super.onPause();

        bgmPause();
    }

    public void button1(View view) {
        //ボタンの音
        soundPool.play(soundId, 1f, 1f, 0, 0, 1);    //音の大きさは0fから1fで調整できる
        //インテントの作成
        Intent intent = new Intent(AppTitle.this, AppMain.class);

        //遷移先の画面を起動
        startActivity(intent);
    }

    public void button2(View view) {
        //ボタンの音
        soundPool.play(soundId, 1f, 1f, 0, 0, 1);    //音の大きさは0fから1fで調整できる
        //インテントの作成
        Intent intent = new Intent(AppTitle.this, AppLog.class);
        //遷移先の画面を起動
        startActivity(intent);
    }

    public void button3(View view) {
        //ボタンの音
        soundPool.play(soundId, 1f, 1f, 0, 0, 1);    //音の大きさは0fから1fで調整できる
        //インテントの作成
        Intent intent = new Intent(AppTitle.this, AppStatus.class);
        //遷移先の画面を起動
        startActivity(intent);

    }
}