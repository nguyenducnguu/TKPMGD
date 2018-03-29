package com.ute.dn.speaknow;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewVideoActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView img_back, img_savedlist, img_save, img_reload;
    Button btn_practice;
    TextView txt_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_video);

        addView();
        addEvent();
    }

    private void addView() {
        img_back = (ImageView) findViewById(R.id.img_back);
        img_savedlist = (ImageView) findViewById(R.id.img_savedlist);
        img_save = (ImageView) findViewById(R.id.img_save);
        img_reload = (ImageView) findViewById(R.id.img_reload);
        btn_practice = (Button) findViewById(R.id.btn_practice);
        txt_title = (TextView) findViewById(R.id.txt_title);
    }

    private void addEvent() {
        img_back.setOnClickListener(this);
        img_savedlist.setOnClickListener(this);
        img_save.setOnClickListener(this);
        img_reload.setOnClickListener(this);
        btn_practice.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_savedlist:
                intent = new Intent(this, SavedListActivity.class);
                startActivity(intent);
                break;
            case R.id.img_save:
                Toast.makeText(this, "Save sentence", Toast.LENGTH_SHORT).show();
                break;
            case R.id.img_reload:
                Toast.makeText(this, "Reload", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_practice:
                intent = new Intent(this, PracticeActivity.class);
                startActivity(intent);
                break;
        }
    }
}
