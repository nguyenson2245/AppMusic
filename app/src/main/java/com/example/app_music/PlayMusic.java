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
        if (mediaPlayer != null) {   // trinh phat đa phương tiện
            mediaPlayer.start();
  //        mediaPlayer.release(); //giải phóng tất cả các tài nguyên được liên kết với MediaPlayer đối tượng
            mediaPlayer.reset();  ////gọi để khôi phục đối tượng về trạng thái Chờ của nó
        }

        Intent intent  =getIntent();
        Bundle bundle = intent.getExtras();

        // đọc danh sach mảng cuộn dây bài hát
        mysongs = (ArrayList)bundle.getParcelableArrayList("songs");  // lấy danh sach mảng cấp dộ cá nhân và khóa là  các bai hát
        String name = intent.getStringExtra("songname");// tên bai hat
        position  = bundle.getInt("pos",0); // vị trí

        txttitle.setSelected(true);

        Uri uri = Uri.parse(mysongs.get(position).toString());
        songnames = mysongs.get(position).getName();
        txttitle.setText(songnames);
        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);// trả về null khi thất bại,
        mediaPlayer.start();
        btnplay.setBackgroundResource(R.drawable.pause);
        imageView.startAnimation(animation);

        // thanh seekBar cập nhật = luồng mới
        UpdateSeekbar = new Thread(){
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();   // tồng thời lượng = thời lượng của lớp lập trình phát
                int currentPosition = 0;                          // vị trí hiện tại

                while(currentPosition<totalDuration){
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);  // di chuyển thanh seekBar
                    } catch (InterruptedException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        seekBar.setMax(mediaPlayer.getDuration());  // vị tri tói đa
        UpdateSeekbar.start();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { // cảm ứng đến vị tri ng dùng clic
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });


        String endtime = createTime(mediaPlayer.getDuration());
        txtendtime.setText(endtime);
// ta sẽ cập nhat thanh tiến trinh của ta vs mỗi giây
        final Handler handler = new Handler();
        final int delay = 1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String curentTime = createTime(mediaPlayer.getCurrentPosition());// vtri htai
                txtstarttime.setText(curentTime);
                handler.postDelayed(this,delay);    // delay : độ trễ
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

        int min = duration/1000/60;  // thời lượng /1000/60
        int sec = duration/1000%60;//giây

        time = time + min + ":";
        if(sec<10){
            time += "0";
        }
        time += sec;
        return time;
    }

    public void startAnimation(View view,Float degree){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView,"rotation",0f,360f); // quay tròn ,từ 0 -> 360 độ
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