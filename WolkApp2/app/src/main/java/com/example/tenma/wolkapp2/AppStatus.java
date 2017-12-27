package com.example.tenma.wolkapp2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class AppStatus extends ActivityAddToBGMandSE{
    int sintyouInt;
    int taizyuuInt;
    String seibetuString;
    ArrayAdapter sintyouAdapter;
    ArrayAdapter taizyuuAdapter;
    ArrayAdapter seibetuAdapter;
    private Spinner sintyouSpinner;
    private Spinner taizyuuSpinner;
    private Spinner seibetuSpinner;
    private SoundPool soundPool;
    private int soundId;
    SharedPreferences data;
    SharedPreferences.Editor editor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*歩数のフォント変える
        TextView text1 = (TextView)findViewById(R.id.seibetu);
        text1.setTypeface(Typeface.createFromAsset(getAssets(), "GD-DOTFONT-DQ-TTF_008.ttf"));
        */


        setContentView(R.layout.activity_status);
        //保存
        data = getSharedPreferences("STATUS",MODE_PRIVATE);
        editor = data.edit();

        // ArrayAdapterを生成。layoutにはspinner_item,中身はそれぞれlist,list2使用
        sintyouAdapter = ArrayAdapter.createFromResource(this, R.array.list, R.layout.spinner_item);
        taizyuuAdapter = ArrayAdapter.createFromResource(this, R.array.list2, R.layout.spinner_item);
        seibetuAdapter = ArrayAdapter.createFromResource(this, R.array.list3, R.layout.spinner_item);

        // グラフィックに「spinner_dropdown_item」使用
        sintyouAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        taizyuuAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        seibetuAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        //身長
        sintyouSpinner = (Spinner) findViewById(R.id.spinner);
        sintyouSpinner.setAdapter(sintyouAdapter);

        //体重
        taizyuuSpinner = (Spinner) findViewById(R.id.spinner2);
        taizyuuSpinner.setAdapter(taizyuuAdapter);

        //性別
        seibetuSpinner = (Spinner) findViewById(R.id.spinner3);
        seibetuSpinner.setAdapter(seibetuAdapter);

        // 身長のドロップダウンリストから選んだ場合に、その値を保持。
        sintyouSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner) parent;
                String item = (String) spinner.getSelectedItem();
                sintyouInt = Integer.parseInt(item);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // スピナーでは使用しないようですが、ないといけないので放置
            }
        });

        // 体重のドロップダウンリストから選んだ場合に、その値を保持。
        taizyuuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner2 = (Spinner) parent;
                String item2 = (String) spinner2.getSelectedItem();
                taizyuuInt = Integer.parseInt(item2);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 性別のドロップダウンリストから選んだ場合に、その値を保持。
        seibetuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView parent, View view, int position, long id) {
                Spinner spinner3 = (Spinner) parent;
                String item3 = (String) spinner3.getSelectedItem();
                seibetuString = (item3);
            }
            @Override
            public void onNothingSelected(AdapterView parent) {
            }
        });
    }

    @Override
    // 画面が表示される度に実行
    protected void onResume() {
        super.onResume();

        bgmStart();

        // 予め音声データを読み込む
        soundPool = new SoundPool(50, AudioManager.STREAM_MUSIC, 0);
        soundId = soundPool.load(getApplicationContext(), R.raw.click2, 1);

        // SharedPreferencesよりデータを読み込む
        int sintyouInt =data.getInt("Sintyou", 0);
        int taizyuuInt = data.getInt("Taizyuu",0);
        String seibetuString = data.getString("seibetu",null);

        // 身長のデータが存在する場合
        if( sintyouInt != 0) {
            sintyouSpinner = (Spinner) findViewById(R.id.spinner);
            sintyouAdapter = (ArrayAdapter) sintyouSpinner.getAdapter();
            int spinnerPosition = sintyouAdapter.getPosition(String.valueOf(sintyouInt));
            sintyouSpinner.setSelection(spinnerPosition);
        }

        // 体重のデータが存在する場合
        if( taizyuuInt != 0 ) {
            taizyuuSpinner = (Spinner) findViewById(R.id.spinner2);
            taizyuuAdapter = (ArrayAdapter) taizyuuSpinner.getAdapter();
            int spinnerPosition2 = taizyuuAdapter.getPosition(String.valueOf(taizyuuInt));
            taizyuuSpinner.setSelection(spinnerPosition2);
        }

        // 性別のデータが存在する場合
        if( seibetuString != null ) {
            seibetuSpinner = (Spinner) findViewById(R.id.spinner3);
            seibetuAdapter = (ArrayAdapter) seibetuSpinner.getAdapter();
            int spinnerPosition3 = seibetuAdapter.getPosition(String.valueOf(seibetuString));
            seibetuSpinner.setSelection(spinnerPosition3);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // putInt("キー値" , value )
        editor.putInt("Sintyou",sintyouInt);
        editor.putInt("Taizyuu",taizyuuInt);
        editor.putString("seibetu",seibetuString);
        editor.commit();
        bgmPause();


    }

    public void back(View view) {
        //ボタンの音
        soundPool.play(soundId, 1f, 1f, 0, 0, 1);    //音の大きさは0fから1fで調整できる

        // putInt("キー値" , value )
        editor.putInt("Sintyou",sintyouInt);
        editor.putInt("Taizyuu",taizyuuInt);
        editor.putString("seibetu",seibetuString);
        editor.commit();

        //インテントの作成
        Intent intent = new Intent(this, AppTitle.class);
        //遷移先の画面を起動
        startActivity(intent);
    }
}