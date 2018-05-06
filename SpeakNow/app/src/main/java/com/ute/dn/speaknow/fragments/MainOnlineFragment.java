package com.ute.dn.speaknow.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.ute.dn.speaknow.R;
import com.ute.dn.speaknow.ViewVideoOnlineActivity;
import com.ute.dn.speaknow.common.Const;
import com.ute.dn.speaknow.common.YouTubeHelper;

import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

public class MainOnlineFragment extends Fragment implements View.OnClickListener{

    //http://youtu.be/8irSFvoyLHQ"
    //http://youtu.be/i1R4R84-EPA"

    EditText txt_url;
    TextView txt_removeUrl, txt_removeTranscript, txt_transcript_path;
    Button btn_ok;
    Context mContext;
    private static String VideoId = "";
    private static String TranscriptPath = "";

    public MainOnlineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_online, container, false);

        addView(view);
        addEvent();
        //TestGetId();

        return view;
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

    private void addView(View view){
        txt_url = view.findViewById(R.id.txt_url);
        txt_transcript_path = view.findViewById(R.id.txt_transcript_path);
        txt_removeUrl = view.findViewById(R.id.txt_removeUrl);
        txt_removeTranscript = view.findViewById(R.id.txt_removeTranscript);
        btn_ok = view.findViewById(R.id.btn_ok);
    }

    private void addEvent(){
        btn_ok.setOnClickListener(this);
        txt_transcript_path.setOnClickListener(this);
        txt_removeUrl.setOnClickListener(this);
        txt_removeTranscript.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.txt_removeUrl:
                txt_url.setText("");
                VideoId = "";
                break;
            case R.id.txt_transcript_path:
                new MaterialFilePicker()
                        .withActivity((Activity) mContext)
                        .withFilter(Pattern.compile(".*\\.txt$"))
                        .withRequestCode(Const.MAIN_ONLINE_PICKFILE_TRANSCRIPT_REQUEST_CODE)
                        .withHiddenFiles(true) // Show hidden files and folders
                        .withTitle("Choose transcript...")
                        .start();
                break;
            case R.id.txt_removeTranscript:
                txt_transcript_path.setText("");
                TranscriptPath = "";
                break;
            case R.id.btn_ok:
                VideoId = YouTubeHelper.getVideoId(String.valueOf(txt_url.getText()));
                if(VideoId.isEmpty() || VideoId.trim().length() == 0){
                    Toast.makeText(getContext(), "Not found video!!!", Toast.LENGTH_SHORT).show();
                }
                else {
                    intent = new Intent(getContext(), ViewVideoOnlineActivity.class);
                    intent.putExtra("VideoId", VideoId.trim());
                    intent.putExtra("TranscriptPath", TranscriptPath.trim());
                    startActivity(intent);
                }
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
            case Const.MAIN_ONLINE_PICKFILE_TRANSCRIPT_REQUEST_CODE:
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
