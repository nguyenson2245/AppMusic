package com.example.app_to_do_list___;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_Intro extends AppCompatActivity {

    private TextView startBtn;
    private ImageView img_star1,img_star2,img_star3,img_star4,img_star5,img_star6,img_star7;
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        AnhXa();

        animation = AnimationUtils.loadAnimation(this, R.anim.disc_rotate);


       StartAnimation();

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Activity_Intro.this,MainActivity.class));
            }
        });
    }

    private void StartAnimation() {
        img_star1.startAnimation(animation);
        img_star2.startAnimation(animation);
        img_star3.startAnimation(animation);
        img_star4.startAnimation(animation);
        img_star5.startAnimation(animation);
        img_star6.startAnimation(animation);
        img_star7.startAnimation(animation);
    }

    private void AnhXa() {
        startBtn = findViewById(R.id.btnstart);
        img_star1 = findViewById(R.id.star1);
        img_star2= findViewById(R.id.star2);
        img_star3= findViewById(R.id.star3);
        img_star4 = findViewById(R.id.star4);
        img_star5 = findViewById(R.id.star5);
        img_star6 = findViewById(R.id.star6);
        img_star7 = findViewById(R.id.star7);
    }
}