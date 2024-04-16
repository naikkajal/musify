package com.android.musify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class playSong extends AppCompatActivity {
    static boolean repeatFlag = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    public String updateLabel(int duration) {
        String timeLable = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;

        timeLable += min + ":";

        if (sec < 10)
            timeLable += "0";
        timeLable+= sec;

        return timeLable;
    }

    TextView textView,textView2;
    ImageView previous, play, next, rotate, repeat;
    Animation animation;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String content;
    int position;
    SeekBar seekBar;
    Thread updateSeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Musify);
        setContentView(R.layout.activity_play_song);

        textView = findViewById(R.id.textView);
        previous = findViewById(R.id.previous);
        play = findViewById(R.id.play);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
        textView2 = findViewById(R.id.textView2);
        rotate = findViewById(R.id.imageView3);
        repeat = findViewById(R.id.imageView4);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList)bundle.getParcelableArrayList("songList");
        content = intent.getStringExtra("currentSong");
        textView.setText(content);
        textView.setSelected(true);
        position = intent.getIntExtra("position", 0);


        final Uri[] uri = {Uri.parse(songs.get(position).toString())};
        mediaPlayer = MediaPlayer.create(this, uri[0]);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_animation);
        rotate.startAnimation(animation);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textView2.setText(updateLabel(seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        updateSeek = new Thread() {
            @Override
            public void run() {
                int currentPosition = 0;
                try {
                    while(currentPosition < mediaPlayer.getDuration()) {
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        sleep(1000);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        updateSeek.start();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else {
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (!repeatFlag) {
                    if (position != 0) {
                        position = position - 1;
                    }
                    else {
                        position = songs.size() - 1;
                    }
                }

                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());
                content = songs.get(position).getName().toString();
                textView.setText(content);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (!repeatFlag) {
                    if (position != songs.size() - 1) {
                        position = position + 1;
                    }
                    else {
                        position = 0;
                    }
                }

                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());
                content = songs.get(position).getName().toString();
                textView.setText(content);
            }
        });


        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (repeatFlag) {
                    repeat.setAlpha(0.5f);
                    repeatFlag = false;
                }
                else {
                    repeat.setAlpha(1.0f);
                    repeatFlag = true;
                }
            }
        });

    }

}