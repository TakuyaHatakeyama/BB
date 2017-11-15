package com.example.tenma.wolkapp2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AppLog extends ActivityAddToBGMandSE {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
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

    public void back3(View view) {
        //インテントの作成
        Intent intent = new Intent(this, AppTitle.class);
        //遷移先の画面を起動
        startActivity(intent);
    }

}