package com.ute.dn.speaknow.Interfaces;


import android.view.View;
import com.ute.dn.speaknow.models.Transcript;

public interface OnTranscriptItemClickListener {
    void onItemClick(View itemView, int position, Transcript transcript);
}
