package com.ute.dn.speaknow;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.ute.dn.speaknow.Interfaces.OnSavedItemClickListener;
import com.ute.dn.speaknow.Interfaces.OnItemLongClickListener;
import com.ute.dn.speaknow.Interfaces.OnPracticeClickListener;
import com.ute.dn.speaknow.adapters.SavedListAdapter;
import com.ute.dn.speaknow.common.DeveloperKey;
import com.ute.dn.speaknow.databases.MyDatabaseHelper;
import com.ute.dn.speaknow.models.SavedItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class SavedListActivity extends YouTubeBaseActivity implements View.OnClickListener, YouTubePlayer.OnInitializedListener{

    Dialog dialogPractice;
    ImageView img_back;
    EditText txt_keyword;
    TextView txt_status;
    RecyclerView rv_savedItem;
    List<SavedItem> lstData;
    SavedListAdapter mAdapter;
    YouTubePlayerView youTubePlayerView;
    YouTubePlayer mYoutubePlayer = null;
    Handler handler = null;
    Runnable runnable = null;
    String videoId = "";
    int startAt = 0;
    int endAt = 0;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    //Start video with duration
    //https://www.youtube.com/embed/[video_id]?start=[start_at_second]&end=[end_at_second]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_list);

        addView();
        addEvent();
        //loadDataDemo();
        loadData();
    }

    private void addView(){
        img_back = findViewById(R.id.img_back);
        txt_keyword = findViewById(R.id.txt_keyword);
        txt_status = findViewById(R.id.txt_status);
        rv_savedItem = findViewById(R.id.rv_savedItem);

        lstData = new ArrayList<>();
        mAdapter = new SavedListAdapter(lstData);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_savedItem.setLayoutManager(layoutManager);
        rv_savedItem.setHasFixedSize(true);
        rv_savedItem.setAdapter(mAdapter);
    }

    private void addEvent(){
        img_back.setOnClickListener(this);

        mAdapter.setOnItemClickListener(new OnSavedItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position, SavedItem savedItem) {
                InitYoutubePlayerView(savedItem);
                Toast.makeText(SavedListActivity.this, "setOnItemClickListener"
                        + "\nposition: " + position
                        + "\nvideoId: " + videoId
                        + "\nstartAt: " + startAt
                        + "\nendAt: " + endAt, Toast.LENGTH_SHORT).show();
            }
        });

        mAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, int position) {
                //Toast.makeText(SavedListActivity.this, "position: " + position, Toast.LENGTH_SHORT).show();
                showDialogEdit(lstData.get(position));
            }
        });

        mAdapter.setOnPracticeClickListener(new OnPracticeClickListener() {
            @Override
            public void onPracticeClick(SavedItem savedItem) {
                showDialogPractice(savedItem);
            }
        });
    }

    private void loadData(){
        MyDatabaseHelper db = new MyDatabaseHelper(this);
        lstData.addAll(db.getAllSavedItem());
        Collections.sort(lstData);
        if(lstData.size() > 0){
            rv_savedItem.setVisibility(View.VISIBLE);
            rv_savedItem.getAdapter().notifyDataSetChanged();
        }
        else {
            rv_savedItem.setVisibility(View.GONE);
            txt_status.setText("Empty!!!");
        }
    }

    private void loadDataDemo(){
        SavedItem item = new SavedItem("8irSFvoyLHQ", "Who is she?", 186800, -1, "");
        lstData.add(item);
        item = new SavedItem("8irSFvoyLHQ", "Can you jump?", 350300, -1, "test notes 0");
        lstData.add(item);
        item = new SavedItem("8irSFvoyLHQ", "Umm.... It's good. Thank you.", 561460, -1, "test notes 1");
        lstData.add(item);
        item = new SavedItem("8irSFvoyLHQ", "Sorry, I can't. It's cold and windy.", 807960, -1, "");
        lstData.add(item);
        item = new SavedItem("8irSFvoyLHQ", "Do you have an umbrella?", 934500, -1, "");
        lstData.add(item);
        item = new SavedItem("8irSFvoyLHQ", "Good morning, Sally. How are you?", 2113620, -1, "");
        lstData.add(item);
        item = new SavedItem("8irSFvoyLHQ", "Wow, I want to make cookies, too.", 2047280, -1, "");
        lstData.add(item);
        item = new SavedItem("8irSFvoyLHQ", "Excuse me. Where is the post office?", 1650600, -1, "");
        lstData.add(item);
        item = new SavedItem("8irSFvoyLHQ", "Where's the post office?", 1635600, -1, "");
        lstData.add(item);
        item = new SavedItem("8irSFvoyLHQ", "Where is my music book?", 1777620, -1, "");
        lstData.add(item);
        item = new SavedItem("8irSFvoyLHQ", "Good luck!", 2251440, -1, "test notes 2");
        lstData.add(item);

        rv_savedItem.getAdapter().notifyDataSetChanged();

        rv_savedItem.setVisibility(View.VISIBLE);
    }

    private void InitYoutubePlayerView(SavedItem savedItem) {
        releaseYoutubePlayer();
        videoId = savedItem.getVideoId();
        startAt = savedItem.getStartAt();
        endAt = savedItem.getEndAt();
        if(videoId.trim().length() == 0) return;
        youTubePlayerView = findViewById(R.id.youtube_view);
        youTubePlayerView.initialize(DeveloperKey.API_KEY, this);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        mYoutubePlayer = null;
        Toast.makeText(this, "Failured to Initialize!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        //add listeners to YouTubePlayer instance
        if (youTubePlayer == null) return;
        mYoutubePlayer = youTubePlayer;
        //Start buffering
        if (!wasRestored) {
            mYoutubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
            mYoutubePlayer.setShowFullscreenButton(false);
            mYoutubePlayer.loadVideo(videoId, startAt);
            mYoutubePlayer.play();
            Toast.makeText(SavedListActivity.this, "onInitializationSuccess"
                    + "\nvideoId: " + videoId
                    + "\nstartAt: " + startAt
                    + "\nendAt: " + endAt, Toast.LENGTH_SHORT).show();

            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    if(mYoutubePlayer.getCurrentTimeMillis() <= endAt) {
                        handler.postDelayed(this, 1000);
                    } else {
                        handler.removeCallbacks(this); //no longer required
                        mYoutubePlayer.pause(); //and Pause the video
                    }
                }
            };
            handler.postDelayed(runnable, 1000);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_back:
                finish();
                break;
        }
    }

    private void showDialogEdit(final SavedItem savedItem){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_save_edit_item);

        TextView txt_transcript = dialog.findViewById(R.id.txt_transcript);
        final EditText txt_notes = dialog.findViewById(R.id.txt_notes);

        txt_transcript.setText(savedItem.getTranscript());
        txt_notes.setText(savedItem.getNotes());

        Button btn_save = dialog.findViewById(R.id.btn_save);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(ViewVideoActivity.this, "id: " + savedItem.getTimeSaved(), Toast.LENGTH_SHORT).show();
                MyDatabaseHelper db = new MyDatabaseHelper(SavedListActivity.this);
                savedItem.setNotes(txt_notes.getText().toString().trim());
                if(db.updateSavedItem(savedItem)){
                    Toast.makeText(SavedListActivity.this, "Update successfully!", Toast.LENGTH_SHORT).show();
                    rv_savedItem.getAdapter().notifyDataSetChanged();
                    dialog.dismiss();
                }
                else {
                    Toast.makeText(SavedListActivity.this, "Update failed!", Toast.LENGTH_SHORT).show();
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

    private void showDialogPractice(final SavedItem savedItem){
        Toast.makeText(this, "Practice...\n"
                + "id: " + savedItem.getTimeSaved()
                + "\nvideoId: " + savedItem.getVideoId()
                + "\nstartAt: " + savedItem.getStartAt()
                + "\nendAt: " + savedItem.getEndAt()
                + "\ntranscript: " + savedItem.getTranscript()
                + "\nnotes: " + savedItem.getNotes(), Toast.LENGTH_SHORT).show();

        dialogPractice = new Dialog(this);
        dialogPractice.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogPractice.setCancelable(true);
        dialogPractice.setContentView(R.layout.dialog_practice);

        TextView txt_transcript = dialogPractice.findViewById(R.id.txt_transcript);
        final TextView txt_data = dialogPractice.findViewById(R.id.txt_data);
        ImageView img_speech = dialogPractice.findViewById(R.id.img_speech);

        txt_transcript.setText(savedItem.getTranscript());

        img_speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt_data.setText("Listening...");
                promptSpeechInput();
            }
        });

        txt_transcript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InitYoutubePlayerView(savedItem);
            }
        });

        dialogPractice.show();

        //Grab the window of the dialog, and change the width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialogPractice.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    TextView txt_data = dialogPractice.findViewById(R.id.txt_data);
                    TextView txt_transcript = dialogPractice.findViewById(R.id.txt_transcript);
                    txt_data.setText(result.get(0));
                    if(txt_data.getText().toString().trim().toUpperCase().equals(txt_transcript.getText().toString().trim().toUpperCase())){
                        txt_data.setBackground(getResources().getDrawable(R.drawable.edt_custom_boder_appcolor));
                    }
                    else {
                        txt_data.setBackground(getResources().getDrawable(R.drawable.edt_custom_boder_red));
                    }
                }
                break;
            }

        }
    }

    private void releaseYoutubePlayer(){
        if (mYoutubePlayer != null) {
            videoId = "";
            startAt = 0;
            endAt = 0;
            mYoutubePlayer.release();
        }
        mYoutubePlayer = null;

        if(handler != null) {
            handler.removeCallbacks(runnable);
        }
        handler = null;
        runnable = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseYoutubePlayer();
    }

    @Override
    public void onStop() {
        releaseYoutubePlayer();
        super.onStop();
    }
}
