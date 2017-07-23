package com.demoapplication.starryskydemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.starryskyview)
    StarrySkyView starryskyview;
    private PlayMusicService.PlayMusicBinder binder;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (PlayMusicService.PlayMusicBinder) service;
            binder.startPlayMusic();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        starryskyview.startMoving();
        Intent intent = new Intent(this, PlayMusicService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);

        starryskyview.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        binder.startPlayMusic();
        unbindService(connection);
    }

    @Override
    public void onClick(View v) {
        if (binder != null)
            binder.playOrPauseMusic();
        starryskyview.pauseOrContinueMoving();
    }
}
