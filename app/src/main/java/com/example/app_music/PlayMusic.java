package com.example.app_music;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayMusic extends AppCompatActivity {

    Button btnplay, btnrewind, btnprevious, btnforward, btnnext;
    TextView txttitle, txtstarttime, txtendtime;
    SeekBar seekBar;
    ImageView imageView,img_Back;

    public static final String EXTRA_NAME = "song_name";
    static MediaPlayer mediaPlayer;
    int position;
    String songnames;
    ArrayList<File> mysongs;

    Thread UpdateSeekbar;
    Animation animation;

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if(item.getItemId()== android.R.id.home){
//            onBackPressed();
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
////    @Override
////    protected void onDestroy() {
////        if()
////        super.onDestroy();
////    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        AnhXa();
        animation = AnimationUtils.loadAnimation(this,R.anim.disc_rotate);

//        getSupportActionBar().setTitle("Now Playing");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        barVisualizer = findViewById(R.id.blast);
        if (mediaPlayer != null) {   // trinh phat ??a ph????ng ti???n
            mediaPlayer.start();
  //        mediaPlayer.release(); //gi???i ph??ng t???t c??? c??c t??i nguy??n ???????c li??n k???t v???i MediaPlayer ?????i t?????ng
            mediaPlayer.reset();  ////g???i ????? kh??i ph???c ?????i t?????ng v??? tr???ng th??i Ch??? c???a n??
        }

        Intent intent  =getIntent();
        Bundle bundle = intent.getExtras();

        // ?????c danh sach m???ng cu???n d??y b??i h??t
        mysongs = (ArrayList)bundle.getParcelableArrayList("songs");  // l???y danh sach m???ng c???p d??? c?? nh??n v?? kh??a l??  c??c bai h??t
        String name = intent.getStringExtra("songname");// t??n bai hat
        position  = bundle.getInt("pos",0); // v??? tr??

        txttitle.setSelected(true);

        Uri uri = Uri.parse(mysongs.get(position).toString());
        songnames = mysongs.get(position).getName();
        txttitle.setText(songnames);
        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);// tr??? v??? null khi th???t b???i,
        mediaPlayer.start();
        btnplay.setBackgroundResource(R.drawable.pause);
        imageView.startAnimation(animation);

        // thanh seekBar c???p nh???t = lu???ng m???i
        UpdateSeekbar = new Thread(){
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();   // t???ng th???i l?????ng = th???i l?????ng c???a l???p l???p tr??nh ph??t
                int currentPosition = 0;                          // v??? tr?? hi???n t???i

                while(currentPosition<totalDuration){
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);  // di chuy???n thanh seekBar
                    } catch (InterruptedException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        seekBar.setMax(mediaPlayer.getDuration());  // v??? tri t??i ??a
        UpdateSeekbar.start();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { // c???m ???ng ?????n v??? tri ng d??ng clic
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });


        String endtime = createTime(mediaPlayer.getDuration());
        txtendtime.setText(endtime);
// ta s??? c???p nhat thanh ti???n trinh c???a ta vs m???i gi??y
        final Handler handler = new Handler();
        final int delay = 1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String curentTime = createTime(mediaPlayer.getCurrentPosition());// vtri htai
                txtstarttime.setText(curentTime);
                handler.postDelayed(this,delay);    // delay : ????? tr???
            }
        },delay);
//
        btnforward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000);
                }
            }
        });

        btnrewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-5000);
                }
            }
        });

//        int autoSesstion = mediaPlayer.getAudioSessionId();
//        if(autoSesstion != -1){
//            barVisualizer.setAudioSessionId(autoSesstion);
//        }

        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    btnplay.setBackgroundResource(R.drawable.play);
                    mediaPlayer.pause();
                    imageView.clearAnimation();
                }else{
                    btnplay.setBackgroundResource(R.drawable.pause);
                    mediaPlayer.start();

                    TranslateAnimation translateAnimation = new TranslateAnimation(-25,25,-25,25);
                    translateAnimation.setInterpolator(new AccelerateInterpolator());
                    translateAnimation.setDuration(600);
                    translateAnimation.setFillAfter(true);
                    translateAnimation.setFillEnabled(true);
                    translateAnimation.setRepeatMode(Animation.REVERSE);
                    translateAnimation.setRepeatCount(2);
                    imageView.startAnimation(translateAnimation);
                    imageView.startAnimation(animation);
                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnnext.performClick();
            }
        });

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position+1)%mysongs.size());
                Uri uri  = Uri.parse(mysongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                songnames = mysongs.get(position).getName();
                txttitle.setText(songnames);
                txtendtime.setText(createTime(mediaPlayer.getDuration()));
                mediaPlayer.start();
                btnplay.setBackgroundResource(R.drawable.pause);
                startAnimation(imageView,360f);
            }
        });

        btnprevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position-1)<0) ? (mysongs.size()-1):position-1;
                Uri uri = Uri.parse(mysongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                songnames = mysongs.get(position).getName();
                txttitle.setText(songnames);
                txtendtime.setText(createTime(mediaPlayer.getDuration()));
                mediaPlayer.start();
                btnplay.setBackgroundResource(R.drawable.pause);
                startAnimation(imageView,-360f);
            }
        });

        img_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_back = new Intent(PlayMusic.this,MainActivity.class);
                startActivity(intent_back);
            }
        });

    }
    public String createTime(int duration){
        String time = "";

        int min = duration/1000/60;  // th???i l?????ng /1000/60
        int sec = duration/1000%60;//gi??y

        time = time + min + ":";
        if(sec<10){
            time += "0";
        }
        time += sec;
        return time;
    }

    public void startAnimation(View view,Float degree){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView,"rotation",0f,360f); // quay tr??n ,t??? 0 -> 360 ?????
        objectAnimator.setDuration(1000);
        AnimatorSet animatorSet  =new AnimatorSet();
        animatorSet.playTogether(objectAnimator);
        animatorSet.start();
    }


    private void AnhXa() {
        btnplay = findViewById(R.id.btnplay);
        btnforward = findViewById(R.id.btnforward);
        btnnext = findViewById(R.id.btnnext);
        btnprevious = findViewById(R.id.btnprvious);
        btnrewind = findViewById(R.id.btnrewind);
        txttitle = findViewById(R.id.txtsong);
        seekBar = findViewById(R.id.seekbar);
        txtstarttime = findViewById(R.id.starttime);
        txtendtime = findViewById(R.id.endttime);
        imageView = findViewById(R.id.imageviewplay);
        img_Back = findViewById(R.id.back_home);

    }
}