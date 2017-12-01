/*package com.example.tenma.wolkapp2;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.util.Locale;


/**
 * Created by Tenma on 2017/11/22.
 */

/*正直ぐちゃぐちゃで意味わかんないことになってます
public class FontCalc extends AppCompatActivity{
    //数字のフォント変えるところ
    int i,j,k,l,m;
    int a;
    //contextに必要？らしい
    private static FontCalc instance = null;
    //配列に入れた画像が入ったimageView2
    ImageView imageView2;


    //contextに必要？らしい
    public static FontCalc getInstance() {
        return instance;
    }

    public void start() {
        //context取得に必要？らしい
        instance = this;
        //context取得に必要？らしい
        Context context = FontCalc.getInstance().getApplicationContext();
        //画像のidを配列に格納
        TypedArray typedArray = context.getResources().obtainTypedArray(R.array.prefecture);
        int[] drawableIds = new int[typedArray.length()];
        for (int i = 0; i < typedArray.length(); i++) {
            // drawableのresource idを取得
            drawableIds[i] = typedArray.getResourceId(i, 0);
        }
        //recycle()は絶対必要らしい
        typedArray.recycle();
        imageView2 = (ImageView) findViewById(R.id.test);
        imageView2.setImageResource(drawableIds[i]);
    }
    public void calc() {
        //一万の位の取り出し
        i = (int) steps / 10000;
        //余剰の計算
        a = (int) steps % 10000;

        //千の位の取り出し
        j = a / 1000;
        //余剰の計算
        a = a % 1000;


        //百の位の取り出し
        k = a / 100;
        //余剰の計算
        a = a % 100;


        //十の位の取り出し
        l = a / 10;
        //余剰の計算
        a = a % 10;

        //一の位の取り出し
        m = a / 1;
        if(i == 0){

        }

    }
}
*/