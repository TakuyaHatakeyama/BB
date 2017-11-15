package com.example.tenma.wolkapp2;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * 異なるアクティビティ間で、共有のBGMを流すためのアクティビティ継承クラス
 */
public class ActivityAddToBGMandSE extends AppCompatActivity {

    static protected MediaPlayer bgm;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        //MediaPlayerインスタンスの生成
        if (bgm == null) {
            // 再生したいBGMの指定
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.title_bgm);
            // mediaPlayerを適用するものの指定
            bgm = MediaPlayer.create(this, uri);
            // 音楽ファイル終了時にループするか
            bgm.setLooping(true);
        }
    }

    /**
     * このクラスを継承したアクティビティ間で、共通のBGMを（途中からでも）共有しつつ開始
     */
    protected void bgmStart() {
        if (!bgm.isPlaying()) {
            bgm.start();
        }
    }

    /**
     * このクラスを継承したアクティビティ間で、共通のBGMを停止
     */
    protected void bgmStop() {
        if (bgm.isPlaying()) {
            bgm.stop();
        }
    }

    /**
     * このクラスを継承したアクティビティ間で、共通のBGMを一時停止
     */
    protected void bgmPause() {
        if (bgm.isPlaying()) {
            bgm.pause();

        }
    }

    /**
     * このクラスを継承したアクティビティ間で、共通のBGMを解法
     */
    protected void bgmRelease() {
        if (bgm.isPlaying()) {
            // メモリの解放
            bgm.release();
            // 音楽プレーヤーを破棄
            bgm = null;
        }
    }
}

