package com.ute.dn.speaknow;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class PracticeActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView img_back, img_savedlist, img_feedback, img_speech, img_listen;
    int temp = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        addView();
        addEvent();

        if(temp == -1){
            img_feedback.setVisibility(View.GONE);
            temp = R.drawable.ic_successfull;
        }
    }

    private void addView(){
        img_back = (ImageView) findViewById(R.id.img_back);
        img_savedlist = (ImageView) findViewById(R.id.img_savedlist);
        img_feedback = (ImageView) findViewById(R.id.img_feedback);
        img_speech = (ImageView) findViewById(R.id.img_speech);
        img_listen = (ImageView) findViewById(R.id.img_listen);
    }

    private void addEvent(){
        img_back.setOnClickListener(this);
        img_savedlist.setOnClickListener(this);
        img_speech.setOnClickListener(this);
        img_listen.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.img_savedlist:
                intent = new Intent(this, SavedListActivity.class);
                startActivity(intent);
                break;
            case R.id.img_speech:
                Toast.makeText(this, "Speak now..", Toast.LENGTH_SHORT).show();
                img_feedback.setVisibility(View.VISIBLE);
                switch(temp) {
                    case R.drawable.ic_successfull:
                        img_feedback.setImageDrawable(getResources().getDrawable(R.drawable.ic_fail));
                        temp = R.drawable.ic_fail;
                        break;
                    case R.drawable.ic_fail:
                        img_feedback.setImageDrawable(getResources().getDrawable(R.drawable.ic_successfull));
                        temp = R.drawable.ic_successfull;
                        break;
                }
                break;
            case R.id.img_listen:
                Toast.makeText(this, "Listen..", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
