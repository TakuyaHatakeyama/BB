package com.example.tenma.wolkapp2;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SetData extends AppCompatActivity {

    SharedPreferences data;
    SharedPreferences.Editor dateEditor;

    Cursor c;

    public SetData(Context context, float steps) {

        Log.v("testt", "SQLにデータを格納します。");

        String sql = "";

        HosuukirokuTest hkData = new HosuukirokuTest( context );
        SQLiteDatabase db = hkData.getReadableDatabase();

        // 設定画面で決めたPreferencesのファイル「STATUS」より、身長と体重の読み込み
        data = context.getSharedPreferences("STATUS",MODE_PRIVATE);
        // PreferencesのdataEditorが操作できるようにする
        dateEditor = data.edit();

        // データ呼び出し。STATUSファイルのSintyouとTaizyuuをキーにして、値を取り出す
        int sintyouInt = data.getInt("Sintyou", 0);
        int taizyuuInt = data.getInt("Taizyuu", 0);

        // int型にキャスト
        final int hosuu = (int)steps;

        // カロリー計算
        int hohaba = 0;
        if(sintyouInt > 0) hohaba = sintyouInt - 100 ;
        double kyori = hohaba * hosuu / 100000.0 * 100;
        kyori = Math.round( kyori );
        double kekka = kyori / 100;
        double calorie =  kekka * taizyuuInt;

        // SQLに、その日の日付と歩数とカロリーを追加
        Calendar cal = Calendar.getInstance();
        SharedPreferences pref = context.getSharedPreferences("file", MODE_PRIVATE);
        if(pref.getBoolean("yesterday", false)) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String strDate = sdf.format(cal.getTime());
        int intDate = Integer.parseInt(strDate);

        Log.v("testt", "データを保存した日付：" + String.valueOf(intDate));

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
}
