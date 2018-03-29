package com.ute.dn.speaknow;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SavedListActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView img_back, img_demo2, img_demo3;
    LinearLayout ln_demo1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_list);

        addView();
        addEvent();
    }

    private void addView(){
        img_back = (ImageView) findViewById(R.id.img_back);
        img_demo2 = (ImageView) findViewById(R.id.img_demo2);
        img_demo3 = (ImageView) findViewById(R.id.img_demo3);
        ln_demo1 = (LinearLayout) findViewById(R.id.ln_demo1);
    }

    private void addEvent(){
        img_back.setOnClickListener(this);
        img_demo2.setOnClickListener(this);
        img_demo3.setOnClickListener(this);
        ln_demo1.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.img_demo2:
                Toast.makeText(this, "delete..", Toast.LENGTH_SHORT).show();
                break;
            case R.id.img_demo3:
                intent = new Intent(this, PracticeActivity.class);
                startActivity(intent);
                break;
            case R.id.ln_demo1:
                intent = new Intent(this, ViewVideoActivity.class);
                startActivity(intent);
                break;
        }
    }
}
