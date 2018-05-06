package com.ute.dn.speaknow.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.ute.dn.speaknow.R;
import com.ute.dn.speaknow.ViewVideoOfflineActivity;
import com.ute.dn.speaknow.common.Const;

import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

//android:text="Avengers- Infinity War Trailer (2018).MP4"
//android:text="Avengers- Infinity War Trailer (2018).txt"
public class MainOfflineFragment extends Fragment implements View.OnClickListener {

    TextView txt_video_path, txt_transcript_path, txt_removeVideo, txt_removeTranscript;
    Button btn_ok;
    Context mContext;
    private static String VideoPath = "/storage/emulated/0/DCIM/Demo App/Avengers- Infinity War Trailer (2018)/Avengers- Infinity War Trailer (2018).MP4";
    private static String TranscriptPath = "/storage/emulated/0/DCIM/Demo App/Avengers- Infinity War Trailer (2018)/Avengers- Infinity War Trailer (2018).txt";

    public MainOfflineFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_offline, container, false);

        addView(view);
        addEvent();

        // Inflate the layout for this fragment
        return view;
    }

    private void addView(View view) {
        txt_video_path = view.findViewById(R.id.txt_video_path);
        txt_transcript_path = view.findViewById(R.id.txt_transcript_path);
        txt_removeVideo = view.findViewById(R.id.txt_removeVideo);
        txt_removeTranscript = view.findViewById(R.id.txt_removeTranscript);
        btn_ok = view.findViewById(R.id.btn_ok);
    }

    private void addEvent() {
        txt_video_path.setOnClickListener(this);
        txt_transcript_path.setOnClickListener(this);
        txt_removeVideo.setOnClickListener(this);
        txt_removeTranscript.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.txt_video_path:
                new MaterialFilePicker()
                        .withActivity((Activity) mContext)
                        .withFilter(Pattern.compile("^.*\\.(mp4|MP4)$"))
                        .withRequestCode(Const.PICKFILE_VIDEO_REQUEST_CODE)
                        .withHiddenFiles(true) // Show hidden files and folders
                        .withTitle("Choose video...")
                        .start();
                break;
            case R.id.txt_removeVideo:
                VideoPath = "";
                txt_video_path.setText("");
                break;
            case R.id.txt_transcript_path:
                new MaterialFilePicker()
                        .withActivity((Activity) mContext)
                        .withFilter(Pattern.compile(".*\\.txt$"))
                        .withRequestCode(Const.PICKFILE_TRANSCRIPT_REQUEST_CODE)
                        .withHiddenFiles(true) // Show hidden files and folders
                        .withTitle("Choose transcript...")
                        .start();
                break;
            case R.id.txt_removeTranscript:
                TranscriptPath = "";
                txt_transcript_path.setText("");
                break;
            case R.id.btn_ok:
                if (VideoPath.trim().equals("")) {
                    Toast.makeText(mContext, "Please choose video file!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TranscriptPath.trim().equals("")) {
                    Toast.makeText(mContext, "Please choose transcript file!", Toast.LENGTH_SHORT).show();
                    return;
                }

                intent = new Intent(getContext(), ViewVideoOfflineActivity.class);
                intent.putExtra("VideoPath", VideoPath);
                intent.putExtra("TranscriptPath", TranscriptPath);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Const.PICKFILE_VIDEO_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    VideoPath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                    Log.d("Path", "Video: " + VideoPath);
                    txt_video_path.setText(getFileName(VideoPath));
                }
                break;
            case Const.PICKFILE_TRANSCRIPT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    TranscriptPath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                    Log.d("Path", "Transcript: " + TranscriptPath);
                    txt_transcript_path.setText(getFileName(TranscriptPath));
                }
                break;
        }
    }

    private String getFileName(String path) {
        try {
            String[] arr = path.split("/");
            return arr[arr.length - 1];
        } catch (Exception e) {

        }
        return "";
    }
}
