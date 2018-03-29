package com.ute.dn.speaknow;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        overridePendingTransition(0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                    WelcomeActivity.this.finish();
                    WelcomeActivity.this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}
