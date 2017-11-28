package com.example.tenma.wolkapp2;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by Tenma on 2017/11/08.
 */

public class AppStatus extends ActivityAddToBGMandSE{

    private Spinner selectSpinner;
    private Spinner selectSpinner2;
    private SoundPool soundPool;
    private int soundId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        // android.R.Layout.simple_spinner_itemをR.layout.spinner_itemに変更
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.list, R.layout.spinner_item);
        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(this, R.array.list2, R.layout.spinner_item);

        // android.R.Layout.simple_spinner_dropdown_itemをR.layout.spinner_dropdown_itemに変更
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        selectSpinner = (Spinner) findViewById(R.id.spinner);
        selectSpinner.setAdapter(adapter);
        selectSpinner2 = (Spinner) findViewById(R.id.spinner2);
        selectSpinner2.setAdapter(adapter2);
        selectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // ここにスピナー内のアイテムを選択した際の処理を書く
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // スピナーでは使用しないようですが、ないといけないのでこのまま放置
            }
        });
        // Spinnerオブジェクトを取得
        Spinner spinner = (Spinner)findViewById(R.id.spinner);

        // 選択されているアイテムのIndexを取得
        int idx = spinner.getSelectedItemPosition();

        // 選択されているアイテムを取得
        String item = (String)spinner.getSelectedItem();


    }

    @Override
    // 画面が表示される度に実行
    protected void onResume() {
        super.onResume();

        bgmStart();
        // 予め音声データを読み込む
        soundPool = new SoundPool(50, AudioManager.STREAM_MUSIC, 0);
        soundId = soundPool.load(getApplicationContext(), R.raw.click2, 1);
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
