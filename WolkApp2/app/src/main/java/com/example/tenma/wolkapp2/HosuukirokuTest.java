package com.example.tenma.wolkapp2;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HosuukirokuTest extends SQLiteOpenHelper {
    public HosuukirokuTest(Context context) {
        // データベース生成
        // (context , 【データベース名】, null , バージョン);
        super(context, "HOSUUKIROKU.db", null, 1);
    }
    /**
     * onCreateメソッドはデータベースを「初めて」使用する時に実行される処理
     * テーブルの作成や初期データの投入を行う
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // テーブルの作成
        db.execSQL("CREATE TABLE hosuukirokuTable" +
                "(" +
                // 日付格納の列　主キー（重複不可）に指定
                " _id INTEGER PRIMARY KEY AUTOINCREMENT,hizuke INTEGER , hosuu INTEGER , karori INTEGER)");

        // 初期データ投入
        //(INSERT INTO 【テーブル名】(列名...) values(【追加列のデータ型に対応した「''」or「」】【他データ投入時の区切り「,」】) ););
    }
    /*
     * onUpgradeメソッド
     * onUpgrade()メソッドはデータベースをバージョンアップした時に呼ばれる
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // とりあえず今回は空でOK
    }
}