package com.ute.dn.speaknow;


import android.Manifest;
import android.app.Dialog;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.ute.dn.speaknow.Interfaces.OnSavedItemLongClickListener;
import com.ute.dn.speaknow.Interfaces.OnPracticeClickListener;
import com.ute.dn.speaknow.Interfaces.OnSavedItemClickListener;
import com.ute.dn.speaknow.adapters.SavedListAdapter;
import com.ute.dn.speaknow.common.DeveloperKey;
import com.ute.dn.speaknow.databases.MyDatabaseHelper;
import com.ute.dn.speaknow.models.SavedItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class SavedListActivity extends AppCompatActivity implements View.OnClickListener, YouTubePlayer.OnInitializedListener{

    Dialog dialogPractice;
    ImageView img_back;
    EditText txt_keyword;
    TextView txt_status;
    RecyclerView rv_savedItem;
    FrameLayout frame_fragment;
    VideoView video_view;

    List<SavedItem> lstData;
    SavedListAdapter mAdapter;
    YouTubePlayerSupportFragment mYoutubePlayerFragment;
    YouTubePlayer mYoutubePlayer = null;
    Handler handler = null;
    Runnable runnable = null;

    MediaController mediaController;

    String videoId = "";
    int startAt = 0;
    int endAt = 0;
    //Start video with duration
    //https://www.youtube.com/embed/[video_id]?start=[start_at_second]&end=[end_at_second]

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
        frame_fragment = findViewById(R.id.frame_fragment);
        video_view = findViewById(R.id.video_view);

        lstData = new ArrayList<>();

        mAdapter = new SavedListAdapter(lstData);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
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
                if(savedItem.getType().equals(SavedItem.ONLINE)){
                    InitYoutubePlayerView(savedItem);
                }
                else if(savedItem.getType().equals(SavedItem.OFFLINE)){
                    InitVideoView(savedItem);
                    //Toast.makeText(SavedListActivity.this, "Type: ofline", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mAdapter.setOnItemLongClickListener(new OnSavedItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, int position, SavedItem savedItem) {
                showDialogEdit(savedItem);
            }
        });

        mAdapter.setOnPracticeClickListener(new OnPracticeClickListener() {
            @Override
            public void onPracticeClick(SavedItem savedItem) {
                showDialogPractice(savedItem);
            }
        });

        txt_keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                mAdapter.filterData(txt_keyword.getText().toString());
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
            mAdapter.filterData(txt_keyword.getText().toString());
        }
        else {
            rv_savedItem.setVisibility(View.GONE);
            txt_status.setText("Empty!!!");
        }
    }

    private void loadDataDemo(){
        SavedItem item = new SavedItem(SavedItem.ONLINE, "8irSFvoyLHQ", "Who is she?", 186800, -1, "");
        lstData.add(item);
        item = new SavedItem(SavedItem.ONLINE, "8irSFvoyLHQ", "Can you jump?", 350300, -1, "test notes 0");
        lstData.add(item);
        item = new SavedItem(SavedItem.ONLINE, "8irSFvoyLHQ", "Umm.... It's good. Thank you.", 561460, -1, "test notes 1");
        lstData.add(item);
        item = new SavedItem(SavedItem.ONLINE, "8irSFvoyLHQ", "Sorry, I can't. It's cold and windy.", 807960, -1, "");
        lstData.add(item);
        item = new SavedItem(SavedItem.ONLINE, "8irSFvoyLHQ", "Do you have an umbrella?", 934500, -1, "");
        lstData.add(item);
        item = new SavedItem(SavedItem.ONLINE, "8irSFvoyLHQ", "Good morning, Sally. How are you?", 2113620, -1, "");
        lstData.add(item);
        item = new SavedItem(SavedItem.ONLINE, "8irSFvoyLHQ", "Wow, I want to make cookies, too.", 2047280, -1, "");
        lstData.add(item);
        item = new SavedItem(SavedItem.ONLINE, "8irSFvoyLHQ", "Excuse me. Where is the post office?", 1650600, -1, "");
        lstData.add(item);
        item = new SavedItem(SavedItem.ONLINE, "8irSFvoyLHQ", "Where's the post office?", 1635600, -1, "");
        lstData.add(item);
        item = new SavedItem(SavedItem.ONLINE, "8irSFvoyLHQ", "Where is my music book?", 1777620, -1, "");
        lstData.add(item);
        item = new SavedItem(SavedItem.ONLINE, "8irSFvoyLHQ", "Good luck!", 2251440, -1, "test notes 2");
        lstData.add(item);

        rv_savedItem.getAdapter().notifyDataSetChanged();

        rv_savedItem.setVisibility(View.VISIBLE);
    }

    private void InitVideoView(SavedItem savedItem) {
        releaseYoutubePlayer();
        releaseMediaController();
        video_view.setVisibility(View.VISIBLE);
        videoId = savedItem.getVideoId();
        startAt = savedItem.getStartAt();
        endAt = savedItem.getEndAt();
        if(videoId.trim().length() == 0) return;

        mediaController = new MediaController(this);
        mediaController.setAnchorView(video_view);
        video_view.setMediaController(mediaController);
        video_view.setKeepScreenOn(true);
        video_view.setVideoPath(savedItem.getVideoId());
        video_view.seekTo(savedItem.getStartAt());
        video_view.start();
        video_view.requestFocus();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if(video_view.getCurrentPosition() <= endAt) {
                    if(video_view.getCurrentPosition() == video_view.getDuration()){
                        handler.removeCallbacks(this); //no longer required
                        video_view.pause(); //and Pause the video
                    }
                    handler.postDelayed(this, 1000);
                } else {
                    handler.removeCallbacks(this); //no longer required
                    video_view.pause(); //and Pause the video
                }
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    private void InitYoutubePlayerView(SavedItem savedItem) {
        releaseMediaController();
        releaseYoutubePlayer();
        frame_fragment.setVisibility(View.VISIBLE);
        videoId = savedItem.getVideoId();
        startAt = savedItem.getStartAt();
        endAt = savedItem.getEndAt();
        if(videoId.trim().length() == 0) return;
        mYoutubePlayerFragment = new YouTubePlayerSupportFragment();
        mYoutubePlayerFragment.initialize(DeveloperKey.API_KEY, this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_fragment, mYoutubePlayerFragment);
        fragmentTransaction.commit();
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

            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    if(mYoutubePlayer.getCurrentTimeMillis() <= endAt) {
                        if(mYoutubePlayer.getCurrentTimeMillis() == mYoutubePlayer.getDurationMillis()){
                            handler.removeCallbacks(this); //no longer required
                            mYoutubePlayer.pause(); //and Pause the video
                        }
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
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txt_transcript.getText().toString().trim().equals("")) {
                    Toast.makeText(SavedListActivity.this, "Please enter transcript!!!", Toast.LENGTH_SHORT).show();
                    txt_transcript.setText("");
                    txt_transcript.requestFocus();
                    return;
                }
                MyDatabaseHelper db = new MyDatabaseHelper(SavedListActivity.this);
                savedItem.setTranscript(txt_transcript.getText().toString().trim());
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

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }

    private void showDialogPractice(final SavedItem savedItem){
        final SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());
        checkPermission();

        dialogPractice = new Dialog(this);
        dialogPractice.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogPractice.setCancelable(true);
        dialogPractice.setContentView(R.layout.dialog_practice);

        final TextView txt_transcript = dialogPractice.findViewById(R.id.txt_transcript);
        final TextView txt_data = dialogPractice.findViewById(R.id.txt_data);
        ImageButton btn_speech = dialogPractice.findViewById(R.id.btn_speech);

        txt_transcript.setText(savedItem.getTranscript());

        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onResults(Bundle bundle) {
                //getting all the matches
                ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                //displaying the first match
                if (matches != null)
                    txt_data.setText(matches.get(0));
                //Check result here
                if (removeAllNonWordCharacters(txt_data.getText().toString()).equals(removeAllNonWordCharacters(txt_transcript.getText().toString()))){
                    txt_data.setBackground(getResources().getDrawable(R.drawable.edt_custom_boder_appcolor));
                }
                else {
                    txt_data.setBackground(getResources().getDrawable(R.drawable.edt_custom_boder_red));
                }
            }

            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }
            @Override
            public void onBeginningOfSpeech() {

            }
            @Override
            public void onRmsChanged(float v) {

            }
            @Override
            public void onBufferReceived(byte[] bytes) {

            }
            @Override
            public void onEndOfSpeech() {

            }
            @Override
            public void onError(int i) {

            }
            @Override
            public void onPartialResults(Bundle bundle) {

            }
            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        btn_speech.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mSpeechRecognizer.stopListening();
                        //when the user removed the finger
                        txt_data.setHint("You will see input here...");
                        break;

                    case MotionEvent.ACTION_DOWN:
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        //finger is on the button
                        txt_data.setText("");
                        txt_data.setHint("Listening...");
                        break;
                }
                return false;
            }
        });

        txt_transcript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(savedItem.getType().equals(SavedItem.ONLINE)){
                    InitYoutubePlayerView(savedItem);
                }
                else if(savedItem.getType().equals(SavedItem.OFFLINE)){
                    InitVideoView(savedItem);
                }
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

    private String removeAllNonWordCharacters(String str){
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(str.toLowerCase());

        for(int i = 0; i < sb.length(); i++){
            if(alphabet.indexOf(sb.charAt(i)) == -1){
                sb.deleteCharAt(i);
                i--;
            }
        }
        return sb.toString();
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
        frame_fragment.setVisibility(View.GONE);
    }

    private void releaseMediaController(){
        if(mediaController != null){
            videoId = "";
            startAt = 0;
            endAt = 0;
            mediaController = null;
            video_view.stopPlayback();
        }

        if(handler != null) {
            handler.removeCallbacks(runnable);
        }
        handler = null;
        runnable = null;

        video_view.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseYoutubePlayer();
    }

    @Override
    public void onStop() {
        releaseYoutubePlayer();
        super.onStop();
    }
}
