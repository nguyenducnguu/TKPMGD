package com.ute.dn.speaknow;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.ute.dn.speaknow.Interfaces.OnTranscriptItemClickListener;
import com.ute.dn.speaknow.Interfaces.OnTranscriptItemDoubleClickListener;
import com.ute.dn.speaknow.Interfaces.OnTranscriptItemLongClickListener;
import com.ute.dn.speaknow.adapters.TranscriptAdapter;
import com.ute.dn.speaknow.common.DeveloperKey;
import com.ute.dn.speaknow.common.GetTitleTask;
import com.ute.dn.speaknow.common.GetTranscriptTask;
import com.ute.dn.speaknow.common.LocalTranscript;
import com.ute.dn.speaknow.databases.MyDatabaseHelper;
import com.ute.dn.speaknow.models.SavedItem;
import com.ute.dn.speaknow.models.Transcript;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ViewVideoOnlineActivity extends YouTubeBaseActivity implements View.OnClickListener, YouTubePlayer.OnInitializedListener {

    EditText txt_transcript, txt_notes;
    TextView txt_startDisplay, txt_endDisplay;
    Button btn_save;

    LinearLayout ln_rv_transcript, ln_createTranscript, ln_statusTranscript;
    ImageView img_back, img_savedlist;
    TextView txt_title;

    YouTubePlayerView youTubePlayerView;
    YouTubePlayer mYoutubePlayer = null;

    RecyclerView rv_transcripts;
    List<Transcript> lstData;
    TranscriptAdapter mAdapter;

    GetTranscriptTask mGetTranscriptTask;
    GetTitleTask mGetTitleTask;

    int currentTime = 0;
    Handler handler = new Handler();
    Runnable run;

    public static String TranscriptPath = "";
    public static String VIDEO_ID = "";
    public static String VIDEO_URL = "";
    public static String EMBEDED_URL = "";
    public static String TRANSCRIPT_URL = "";

    private static final int RECOVERY_DIALOG_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_video_online);

        addView();
        addEvent();

        if (!isDeviceOnline()) {
            Toast.makeText(this, "Offline!!!", Toast.LENGTH_LONG).show();
            return;
        }

        //Receice URL
        ReceiveData();

        //Get title
        loadTitle();

        //Get transcript
        loadTranscript();

        //Initializing YouTube Player View
        InitYoutubePlayerView();

        //Update UI with current time
        run = new Runnable() {
            @Override
            public void run() {
                if(mAdapter != null && mYoutubePlayer != null){
                    mAdapter.updateUI(getPositionWithCurrentTime(mYoutubePlayer.getCurrentTimeMillis()));
                }
                handler.postDelayed(this, 100);
            }
        };
        handler.postDelayed(run, 100);
    }



    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void addView() {
        txt_transcript = findViewById(R.id.txt_transcript);
        txt_notes = findViewById(R.id.txt_notes);
        txt_startDisplay = findViewById(R.id.txt_startDisplay);
        txt_endDisplay = findViewById(R.id.txt_endDisplay);
        btn_save = findViewById(R.id.btn_save);

        img_back = findViewById(R.id.img_back);
        img_savedlist = findViewById(R.id.img_savedlist);
        txt_title = findViewById(R.id.txt_title);
        rv_transcripts = findViewById(R.id.rv_transcripts);
        ln_rv_transcript = findViewById(R.id.ln_rv_transcript);
        ln_statusTranscript = findViewById(R.id.ln_statusTranscript);
        ln_createTranscript = findViewById(R.id.ln_createTranscript);
    }

    private void addEvent() {
        img_back.setOnClickListener(this);
        img_savedlist.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        txt_startDisplay.setOnClickListener(this);
        txt_endDisplay.setOnClickListener(this);
    }

    private void ReceiveData() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getStringExtra("VideoId") != null && !intent.getStringExtra("VideoId").isEmpty()) {
                VIDEO_ID = intent.getStringExtra("VideoId");
                VIDEO_URL = "https://www.youtube.com/watch?v=" + VIDEO_ID;
                EMBEDED_URL = "http://www.youtube.com/oembed?url=" + VIDEO_URL + "&format=json";
                TRANSCRIPT_URL = "https://www.youtube.com/api/timedtext?v=" + VIDEO_ID + "&lang=en&fmt=srv3";
            }
            if (intent.getStringExtra("TranscriptPath") != null && !intent.getStringExtra("TranscriptPath").isEmpty()) {
                TranscriptPath = intent.getStringExtra("TranscriptPath");
            }
        }
    }

    private void loadTitle() {
        mGetTitleTask = new GetTitleTask(txt_title);
        mGetTitleTask.execute(EMBEDED_URL);
    }

    private void loadTranscript() {
        lstData = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_transcripts.setLayoutManager(layoutManager);
        rv_transcripts.setHasFixedSize(true);
        mAdapter = new TranscriptAdapter(rv_transcripts, lstData);
        rv_transcripts.setAdapter(mAdapter);

        //Load transcript from local
        if(TranscriptPath.trim().length() > 0){
            lstData.addAll(LocalTranscript.readData(TranscriptPath));

            if(lstData.size() > 0) {
                ln_createTranscript.setVisibility(View.GONE);
                ln_rv_transcript.setVisibility(View.VISIBLE);
                ln_statusTranscript.setVisibility(View.GONE);
                rv_transcripts.getAdapter().notifyDataSetChanged();
            }
            else {
                ln_createTranscript.setVisibility(View.VISIBLE);
                ln_rv_transcript.setVisibility(View.GONE);
                ln_statusTranscript.setVisibility(View.GONE);
            }
        }
        //Auto load transcript
        else {
            mGetTranscriptTask = new GetTranscriptTask();
            mGetTranscriptTask.execute(TRANSCRIPT_URL);
            mGetTranscriptTask.setOnGetTranscriptTask(new GetTranscriptTask.OnGetTranscriptListener() {
                @Override
                public void onPreExecute() {
                    ln_createTranscript.setVisibility(View.GONE);
                    ln_rv_transcript.setVisibility(View.GONE);
                    ln_statusTranscript.setVisibility(View.VISIBLE);
                }

                @Override
                public void onResult(List<Transcript> lst) {
                    lstData.addAll(lst);
                    if(lstData.size() > 0) {
                        ln_createTranscript.setVisibility(View.GONE);
                        ln_rv_transcript.setVisibility(View.VISIBLE);
                        ln_statusTranscript.setVisibility(View.GONE);
                        rv_transcripts.getAdapter().notifyDataSetChanged();
                    }
                    else {
                        ln_createTranscript.setVisibility(View.VISIBLE);
                        ln_rv_transcript.setVisibility(View.GONE);
                        ln_statusTranscript.setVisibility(View.GONE);
                    }
                }
            });
        }

        mAdapter.setOnItemClickListener(new OnTranscriptItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position, Transcript transcript) {
                if (mYoutubePlayer != null) {
                    mYoutubePlayer.seekToMillis(transcript.getStrart());
                } else {
                    Toast.makeText(ViewVideoOnlineActivity.this, "mYoutubePlayer = null", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mAdapter.setOnItemDoubleClickListener(new OnTranscriptItemDoubleClickListener() {
            @Override
            public void onItemDoubleClick(View itemView, int position, Transcript transcript) {
                SavedItem savedItem = new SavedItem(SavedItem.ONLINE, VIDEO_ID, transcript.getTranscript(),
                        transcript.getStrart(), transcript.getStrart() + transcript.getDuration(),"");
                MyDatabaseHelper db = new MyDatabaseHelper(ViewVideoOnlineActivity.this);
                if (db.addSavedItem(savedItem)) {
                    Toast.makeText(ViewVideoOnlineActivity.this, "Save successfully!\n" + transcript.getTranscript(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ViewVideoOnlineActivity.this, "Save failed!\n" + transcript.getTranscript(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mAdapter.setOnItemLongClickListener(new OnTranscriptItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, int position, Transcript transcript) {
                SavedItem savedItem = new SavedItem(SavedItem.ONLINE, VIDEO_ID, transcript.getTranscript(),
                        transcript.getStrart(), transcript.getStrart() + transcript.getDuration(),"");
                showDialogSave(savedItem);
            }
        });
    }

    private int getPositionWithCurrentTime(int currentTime){
        for(int i = 0; i < lstData.size(); i++){
            if (lstData.get(i).getStrart() > currentTime){
                return --i;
            }
        }
        return lstData.size() - 1;
    }

    private void InitYoutubePlayerView() {
        youTubePlayerView = findViewById(R.id.youtube_view);
        youTubePlayerView.initialize(DeveloperKey.API_KEY, this);
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorReason) {
        mYoutubePlayer = null;
        Toast.makeText(this, "Failured to Initialize!", Toast.LENGTH_LONG).show();
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(getString(R.string.error_player), errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(DeveloperKey.API_KEY, this);
        }
    }

    protected Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        //add listeners to YouTubePlayer instance
        if (youTubePlayer == null) return;
        mYoutubePlayer = youTubePlayer;
        //Start buffering
        if (!wasRestored) {
            mYoutubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
            mYoutubePlayer.setShowFullscreenButton(false);
            mYoutubePlayer.loadVideo(VIDEO_ID, currentTime);
            mYoutubePlayer.play();
        }
    }

    private void showDialogSave(final SavedItem savedItem) {
        if(mYoutubePlayer != null && mYoutubePlayer.isPlaying()) mYoutubePlayer.pause();
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
                    Toast.makeText(getApplicationContext(), "Please enter transcript!!!", Toast.LENGTH_SHORT).show();
                    txt_transcript.setText("");
                    txt_transcript.requestFocus();
                    return;
                }
                //Toast.makeText(ViewVideoOnlineActivity.this, "id: " + savedItem.getTimeSaved(), Toast.LENGTH_SHORT).show();
                MyDatabaseHelper db = new MyDatabaseHelper(ViewVideoOnlineActivity.this);
                savedItem.setTranscript(txt_transcript.getText().toString().trim());
                savedItem.setNotes(txt_notes.getText().toString().trim());
                if (db.addSavedItem(savedItem)) {
                    Toast.makeText(ViewVideoOnlineActivity.this, "Save successfully!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(ViewVideoOnlineActivity.this, "Save failed!", Toast.LENGTH_SHORT).show();
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
        Intent intent;
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_savedlist:
                intent = new Intent(this, SavedListActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_save:
                if(txt_transcript.getText().toString().trim().length() == 0){
                    Toast.makeText(this, "Please enter the sentence!", Toast.LENGTH_SHORT).show();
                    txt_transcript.requestFocus();
                    return;
                }
                try {
                    String[] s = txt_startDisplay.getText().toString().split(":");
                    String[] e = txt_endDisplay.getText().toString().split(":");

                    int msStart = Integer.parseInt(s[2]) + Integer.parseInt(s[1]) * 1000 + Integer.parseInt(s[0]) * 60 * 1000;
                    int msEnd = Integer.parseInt(e[2]) + Integer.parseInt(e[1]) * 1000 + Integer.parseInt(e[0]) * 60 * 1000;

                    if(msStart >= msEnd){
                        Toast.makeText(this, "'Start at' must be smaller than 'End at'", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SavedItem savedItem = new SavedItem(SavedItem.ONLINE, VIDEO_ID, txt_transcript.getText().toString(), msStart, msEnd, txt_notes.getText().toString());
                    MyDatabaseHelper db = new MyDatabaseHelper(this);
                    if (db.addSavedItem(savedItem)) {
                        txt_transcript.setText("");
                        txt_notes.setText("");
                        txt_startDisplay.setText("00:00:00");
                        txt_startDisplay.setText("00:00:00");
                        Toast.makeText(this, "Save successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Save faild!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.txt_startDisplay:
                if (mYoutubePlayer != null) {
                    int curMilTime = mYoutubePlayer.getCurrentTimeMillis();
                    int mm = curMilTime / (1000 * 60);
                    int ss = (curMilTime - mm * 1000 * 60) / 1000;
                    int ms = (curMilTime - mm * 1000 * 60 - ss * 1000) / 10;

                    txt_startDisplay.setText((mm > 9 ? mm + "" : ("0" + mm))
                            + ":" + (ss > 9 ? ss + "" : ("0" + ss))
                            + ":" + (ms > 9 ? ms + "" : ("0" + ms)));
                } else {
                    Toast.makeText(ViewVideoOnlineActivity.this, "mYoutubePlayer = null", Toast.LENGTH_SHORT);
                }
                break;
            case R.id.txt_endDisplay:
                if (mYoutubePlayer != null) {
                    int curMilTime = mYoutubePlayer.getCurrentTimeMillis();
                    int mm = curMilTime / (1000 * 60);
                    int ss = (curMilTime - mm * 1000 * 60) / 1000;
                    int ms = (curMilTime - mm * 1000 * 60 - ss * 1000) / 10;

                    txt_endDisplay.setText((mm > 9 ? mm + "" : ("0" + mm))
                            + ":" + (ss > 9 ? ss + "" : ("0" + ss))
                            + ":" + (ms > 9 ? ms + "" : ("0" + ms)));
                } else {
                    Toast.makeText(ViewVideoOnlineActivity.this, "mYoutubePlayer = null", Toast.LENGTH_SHORT);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mYoutubePlayer == null) {
            InitYoutubePlayerView();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mYoutubePlayer != null) {
            currentTime = mYoutubePlayer.getCurrentTimeMillis();
            mYoutubePlayer.release();
        }
        mYoutubePlayer = null;
    }

    @Override
    public void onStop() {
    /* release ut when go to other fragment or back pressed */
        if (mYoutubePlayer != null) {
            mYoutubePlayer.release();
        }
        mYoutubePlayer = null;
        handler.removeCallbacks(run);
        run = null;
        TranscriptPath = "";
        VIDEO_ID = "";
        VIDEO_URL = "";
        EMBEDED_URL = "";
        TRANSCRIPT_URL = "";
        super.onStop();
    }
}
