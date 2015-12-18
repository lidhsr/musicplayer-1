package com.xiezhen.musicplayer.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xiezhen.musicplayer.R;
import com.xiezhen.musicplayer.entity.Mp3Info;
import com.xiezhen.musicplayer.utils.MediaUtils;

import java.util.ArrayList;

public class PlayActivity extends BaseActivity implements View.OnClickListener {
    private TextView textView1_title;
    private TextView textView1_end_time;
    private TextView textView1_start_time;
    private ImageView imageView1_next;
    private ImageView imageView2_play_pause;
    private ImageView imageView1_album;
    private ImageView imageView3_previous;
    private ImageView imageView1_play_mode;
    private SeekBar seekBar1;
    private ArrayList<Mp3Info> mp3infos;
    private static final int UPDATE_TIME = 0x1;
    private static MyHandler myHandler;
    private boolean isPause = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        initView();
        addListener();
        mp3infos = MediaUtils.getMp3Infos(this);
        bindPlayService();
//        change(position);
        myHandler = new MyHandler(this);
        isPause = getIntent().getBooleanExtra("isPause", false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindPlayService();
    }

    public void initView() {
        textView1_title = (TextView) findViewById(R.id.textView1_title);
        textView1_end_time = (TextView) findViewById(R.id.textView1_end_time);
        textView1_start_time = (TextView) findViewById(R.id.textView1_start_time);
        imageView1_next = (ImageView) findViewById(R.id.imageView1_next);
        imageView2_play_pause = (ImageView) findViewById(R.id.imageView2_play_pause);
        imageView1_album = (ImageView) findViewById(R.id.imageView1_album);
        imageView3_previous = (ImageView) findViewById(R.id.imageView3_previous);
        imageView1_play_mode = (ImageView) findViewById(R.id.imageView1_play_mode);
        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
    }

    private void addListener() {
        imageView1_next.setOnClickListener(this);
        imageView2_play_pause.setOnClickListener(this);
        imageView3_previous.setOnClickListener(this);
        imageView1_play_mode.setOnClickListener(this);

    }

    static class MyHandler extends Handler {
        private PlayActivity playActivity;


        public MyHandler(PlayActivity playActivity) {
            this.playActivity = playActivity;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (playActivity != null) {
                switch (msg.what) {
                    case UPDATE_TIME:
                        playActivity.textView1_start_time.setText(MediaUtils.formatTime(msg.arg1));
                }
            }

        }
    }

    @Override
    public void publish(int progress) {
//        textView1_start_time.setText(MediaUtils.formatTime(progress));
        Message msg = myHandler.obtainMessage(UPDATE_TIME);
        msg.arg1 = progress;
        myHandler.sendMessage(msg);
        seekBar1.setProgress(progress);
    }

    @Override
    public void change(int position) {
        Log.d("xiezhen","PlayActivity");
        if (this.playService.isPlaying()) {
            Mp3Info mp3Info = mp3infos.get(position);
            textView1_title.setText(mp3Info.getTitle());
            Bitmap albumBitmap = MediaUtils.getArtwork(this, mp3Info.getId(), mp3Info.getAlbumId(), true, true);
            imageView1_album.setImageBitmap(albumBitmap);
            textView1_end_time.setText(MediaUtils.formatTime(mp3Info.getDuration()));
            imageView2_play_pause.setImageResource(R.mipmap.pause);
            seekBar1.setProgress(0);
            seekBar1.setMax((int) mp3Info.getDuration());
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView2_play_pause:
                if (playService.isPlaying()) {
                    imageView2_play_pause.setImageResource(R.mipmap.play);
                    playService.pause();
                    isPause=true;
                } else {
                    if (isPause) {
                        imageView2_play_pause.setImageResource(R.mipmap.pause);
                        playService.start();
                    } else {
                        playService.play(0);
                    }
                    isPause=false;
                }
                break;
            default:
                break;
        }
    }
}