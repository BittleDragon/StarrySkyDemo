package com.demoapplication.starryskydemo;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

/**
 * 播放音乐服务
 * Created by raoxuting on 2017/7/4.
 */

public class PlayMusicService extends Service {

    private MediaPlayer mediaPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        return new PlayMusicBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mediaPlayer == null)
            mediaPlayer = new MediaPlayer();
    }

    private void playMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            Log.e("播放音乐", "执行了");
            //重置播放器
            mediaPlayer.reset();
            //绑定资源
//            mediaPlayer = MediaPlayer.create(this, R.raw.yiruma_river_flows_in_you);
            try {
                mediaPlayer.setDataSource(this, Uri.parse
                        ("android.resource://"+getPackageName()+"/"+R.raw.yiruma_river_flows_in_you));
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //开始播放
            mediaPlayer.start();
            //是否循环
            mediaPlayer.setLooping(true);
        }
    }

    public class PlayMusicBinder extends Binder {

        public void startPlayMusic() {
            playMusic();
        }

        public void playOrPauseMusic() {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }else {
                    mediaPlayer.start();
                }
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("service的ondestroy", "调用了");
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}
