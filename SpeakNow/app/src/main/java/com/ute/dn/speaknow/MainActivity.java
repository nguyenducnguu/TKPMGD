package com.ute.dn.speaknow;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ute.dn.speaknow.common.Authentication;
import com.ute.dn.speaknow.common.YouTubeHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //http://youtu.be/8irSFvoyLHQ"
    //http://youtu.be/i1R4R84-EPA"

    ImageView img_savedlist;
    EditText txt_url;
    Button btn_ok;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addView();
        addEvent();
        //TestGetId();
    }


    private void TestGetId(){
        Log.d("VIDEOID", "1: '" + YouTubeHelper.getVideoId("youtube.com/v/8d_a1j1MPe0") + "'");
        Log.d("VIDEOID", "2: '" + YouTubeHelper.getVideoId("youtube.com/vi/8d_a1j1MPe0") + "'");
        Log.d("VIDEOID", "3: '" + YouTubeHelper.getVideoId("youtube.com/?v=8d_a1j1MPe0") + "'");
        Log.d("VIDEOID", "4: '" + YouTubeHelper.getVideoId("youtube.com/?vi=8d_a1j1MPe0") + "'");
        Log.d("VIDEOID", "5: '" + YouTubeHelper.getVideoId("youtube.com/watch?v=8d_a1j1MPe0") + "'");
        Log.d("VIDEOID", "6: '" + YouTubeHelper.getVideoId("youtube.com/watch?vi=8d_a1j1MPe0") + "'");
        Log.d("VIDEOID", "7: '" + YouTubeHelper.getVideoId("youtu.be/8d_a1j1MPe0") + "'");
        Log.d("VIDEOID", "8: '" + YouTubeHelper.getVideoId("youtube.com/embed/8d_a1j1MPe0") + "'");
        Log.d("VIDEOID", "9: '" + YouTubeHelper.getVideoId("youtube.com/embed/8d_a1j1MPe0") + "'");
        Log.d("VIDEOID", "10: '" + YouTubeHelper.getVideoId("www.youtube.com/v/8d_a1j1MPe0") + "'");
        Log.d("VIDEOID", "11: '" + YouTubeHelper.getVideoId("http://www.youtube.com/v/8d_a1j1MPe0") + "'");
        Log.d("VIDEOID", "12: '" + YouTubeHelper.getVideoId("https://www.youtube.com/v/8d_a1j1MPe0") + "'");
        Log.d("VIDEOID", "13: '" + YouTubeHelper.getVideoId("youtube.com/watch?v=8d_a1j1MPe0&wtv=wtv") + "'");
        Log.d("VIDEOID", "14: '" + YouTubeHelper.getVideoId("http://www.youtube.com/watch?dev=inprogress&v=8d_a1j1MPe0&feature=related") + "'");
        Log.d("VIDEOID", "15: '" + YouTubeHelper.getVideoId("https://m.youtube.com/watch?v=8d_a1j1MPe0") + "'");
    }

    private void addView(){
        img_savedlist = findViewById(R.id.img_savedlist);
        txt_url = findViewById(R.id.txt_url);
        btn_ok = findViewById(R.id.btn_ok);
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
                String videoId = YouTubeHelper.getVideoId(String.valueOf(txt_url.getText()));
                if(videoId.isEmpty() || videoId.trim().length() == 0){
                    Toast.makeText(this, "Not found video!!!", Toast.LENGTH_SHORT).show();
                }
                else {
                    intent = new Intent(this, ViewVideoActivity.class);
                    intent.putExtra("videoId", videoId.trim());
                    startActivity(intent);
                }
                break;
        }
    }
}
