package com.ute.dn.speaknow;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.ute.dn.speaknow.Interfaces.OnTranscriptItemClickListener;
import com.ute.dn.speaknow.Interfaces.OnTranscriptItemDoubleClickListener;
import com.ute.dn.speaknow.Interfaces.OnTranscriptItemLongClickListener;
import com.ute.dn.speaknow.adapters.TranscriptAdapter;
import com.ute.dn.speaknow.common.LocalTranscript;
import com.ute.dn.speaknow.databases.MyDatabaseHelper;
import com.ute.dn.speaknow.models.SavedItem;
import com.ute.dn.speaknow.models.Transcript;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViewVideoOfflineActivity extends AppCompatActivity implements View.OnClickListener{

    TextView txt_title;
    ImageView img_back, img_savedlist;
    LinearLayout ln_rv_transcript, ln_createTranscript, ln_statusTranscript;
    RecyclerView rv_transcripts;
    List<Transcript> lstData;
    TranscriptAdapter mAdapter;
    VideoView video_view;
    MediaController mediaController;
    private static String VideoPath = "";
    private static String TranscriptPath = "";
    int currentTime = 0;
    Handler handler = new Handler();
    Runnable run;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_video_offline);

        ReceiveData();

        addView();
        addEvent();

        loadTranscriptFromLocal();

        //Update UI with current time
        run = new Runnable() {
            @Override
            public void run() {
                if(mAdapter != null && video_view != null){
                    mAdapter.updateUI(getPositionWithCurrentTime(video_view.getCurrentPosition()));
                }
                handler.postDelayed(this, 100);
            }
        };
        handler.postDelayed(run, 100);
    }

    private int getPositionWithCurrentTime(int currentTime){
        for(int i = 0; i < lstData.size(); i++){
            if (lstData.get(i).getStrart() > currentTime){
                return --i;
            }
        }
        return lstData.size() - 1;
    }

    private void ReceiveData() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getStringExtra("VideoPath") != null && !intent.getStringExtra("VideoPath").isEmpty()) {
                VideoPath = intent.getStringExtra("VideoPath");
            }
            if (intent.getStringExtra("TranscriptPath") != null && !intent.getStringExtra("TranscriptPath").isEmpty()) {
                TranscriptPath = intent.getStringExtra("TranscriptPath");
            }
        }
    }

    private String getFileName(String path){
        try {
            String[] arr = path.split("/");
            return arr[arr.length - 1];
        }
        catch (Exception e) {

        }
        return "";
    }

    private void addView(){
        txt_title = findViewById(R.id.txt_title);
        img_back = findViewById(R.id.img_back);
        img_savedlist = findViewById(R.id.img_savedlist);
        video_view = findViewById(R.id.video_view);
        ln_rv_transcript = findViewById(R.id.ln_rv_transcript);
        ln_createTranscript = findViewById(R.id.ln_createTranscript);
        ln_statusTranscript = findViewById(R.id.ln_statusTranscript);
        rv_transcripts = findViewById(R.id.rv_transcripts);

        txt_title.setText(getFileName(VideoPath));

        mediaController = new MediaController(this);
        mediaController.setAnchorView(video_view);
        video_view.setMediaController(mediaController);
        video_view.setKeepScreenOn(true);
        video_view.setVideoPath(VideoPath);
        video_view.seekTo(currentTime);
        video_view.start();
        video_view.requestFocus();
    }

    private void addEvent(){
        img_back.setOnClickListener(this);
        img_savedlist.setOnClickListener(this);
    }

    private void loadTranscriptFromLocal(){
        lstData = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_transcripts.setLayoutManager(layoutManager);
        rv_transcripts.setHasFixedSize(true);
        mAdapter = new TranscriptAdapter(rv_transcripts, lstData);
        rv_transcripts.setAdapter(mAdapter);

        lstData.addAll(LocalTranscript.readData(TranscriptPath));

        if(lstData.size() > 0){
            ln_statusTranscript.setVisibility(View.GONE);
            ln_createTranscript.setVisibility(View.GONE);
            ln_rv_transcript.setVisibility(View.VISIBLE);
            rv_transcripts.getAdapter().notifyDataSetChanged();
        }
        else {
            ln_statusTranscript.setVisibility(View.GONE);
            ln_createTranscript.setVisibility(View.VISIBLE);
            ln_rv_transcript.setVisibility(View.GONE);
        }

        mAdapter.setOnItemClickListener(new OnTranscriptItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position, Transcript transcript) {
                if (video_view != null) {
                    video_view.seekTo(transcript.getStrart());
                } else {
                    Toast.makeText(ViewVideoOfflineActivity.this, "video_view = null", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mAdapter.setOnItemDoubleClickListener(new OnTranscriptItemDoubleClickListener() {
            @Override
            public void onItemDoubleClick(View itemView, int position, Transcript transcript) {
                SavedItem savedItem = new SavedItem(SavedItem.OFFLINE, VideoPath, transcript.getTranscript(),
                        transcript.getStrart(), transcript.getStrart() + transcript.getDuration(),"");
                MyDatabaseHelper db = new MyDatabaseHelper(ViewVideoOfflineActivity.this);
                if (db.addSavedItem(savedItem)) {
                    Toast.makeText(ViewVideoOfflineActivity.this, "Save successfully!\n" + transcript.getTranscript(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ViewVideoOfflineActivity.this, "Save failed!\n" + transcript.getTranscript(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mAdapter.setOnItemLongClickListener(new OnTranscriptItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, int position, Transcript transcript) {
                SavedItem savedItem = new SavedItem(SavedItem.OFFLINE, VideoPath, transcript.getTranscript(),
                        transcript.getStrart(), transcript.getStrart() + transcript.getDuration(),"");
                showDialogSave(savedItem);
            }
        });
    }

    private void showDialogSave(final SavedItem savedItem) {
        if(video_view != null && video_view.isPlaying()) video_view.pause();

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_save_edit_item);

        final EditText txt_transcript = dialog.findViewById(R.id.txt_transcript);
        final EditText txt_notes = dialog.findViewById(R.id.txt_notes);

        txt_transcript.setText(savedItem.getTranscript());
        txt_notes.setText(savedItem.getNotes());

        Button btn_save = dialog.findViewById(R.id.btn_save);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(video_view != null) video_view.start();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txt_transcript.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter transcript!!!", Toast.LENGTH_SHORT).show();
                    txt_transcript.setText("");
                    txt_transcript.requestFocus();
                    return;
                }
                //Toast.makeText(ViewVideoOnlineActivity.this, "id: " + savedItem.getTimeSaved(), Toast.LENGTH_SHORT).show();
                MyDatabaseHelper db = new MyDatabaseHelper(ViewVideoOfflineActivity.this);
                savedItem.setTranscript(txt_transcript.getText().toString().trim());
                savedItem.setNotes(txt_notes.getText().toString().trim());
                if (db.addSavedItem(savedItem)) {
                    Toast.makeText(ViewVideoOfflineActivity.this, "Save successfully!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    if(video_view != null) video_view.start();
                } else {
                    Toast.makeText(ViewVideoOfflineActivity.this, "Save failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();

        //Grab the window of the dialog, and change the width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.img_savedlist:
                Intent intent = new Intent(this, SavedListActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(video_view != null){
            video_view.seekTo(currentTime);
            video_view.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (video_view != null) {
            currentTime = video_view.getCurrentPosition();
        }
    }
}
