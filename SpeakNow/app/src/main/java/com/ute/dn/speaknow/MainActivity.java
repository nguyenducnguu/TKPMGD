package com.ute.dn.speaknow;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView img_savedlist;
    EditText txt_keyword;
    Button btn_ok;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addView();
        addEvent();
    }

    private void addView(){
        img_savedlist = (ImageView) findViewById(R.id.img_savedlist);
        txt_keyword = (EditText) findViewById(R.id.txt_keyword);
        btn_ok = (Button) findViewById(R.id.btn_ok);
    }

    private void addEvent(){
        img_savedlist.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.img_savedlist:
                intent = new Intent(this, SavedListActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_ok:
                intent = new Intent(this, ViewVideoActivity.class);
                startActivity(intent);
                break;
        }
    }
}
