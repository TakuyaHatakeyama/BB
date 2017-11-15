package com.example.tenma.wolkapp2;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;

public class AppTitle extends ActivityAddToBGMandSE {
    MediaPlayer bgm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);


    }

    @Override
    // 画面が表示される度に実行
    protected void onResume() {
        super.onResume();

        bgmStart();
    }

    @Override
    protected void onPause() {
        super.onPause();

        bgmPause();
    }

    public void button1(View view) {
        //インテントの作成
        Intent intent = new Intent(AppTitle.this, AppMain.class);
        //遷移先の画面を起動
        startActivity(intent);
    }

    public void button2(View view) {
        //インテントの作成
        Intent intent = new Intent(AppTitle.this, AppLog.class);
        //遷移先の画面を起動
        startActivity(intent);
    }

    public void button3(View view) {
        //インテントの作成
        Intent intent = new Intent(AppTitle.this, AppStatus.class);
        //遷移先の画面を起動
        startActivity(intent);

    }
}