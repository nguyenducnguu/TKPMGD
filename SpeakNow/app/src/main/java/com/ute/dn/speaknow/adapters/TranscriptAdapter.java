package com.ute.dn.speaknow.adapters;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ute.dn.speaknow.Interfaces.OnTranscriptItemClickListener;
import com.ute.dn.speaknow.Interfaces.OnTranscriptItemDoubleClickListener;
import com.ute.dn.speaknow.Interfaces.OnTranscriptItemLongClickListener;
import com.ute.dn.speaknow.R;
import com.ute.dn.speaknow.models.Transcript;

import java.util.ArrayList;
import java.util.List;

public class TranscriptAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Transcript> lstData;
    private RecyclerView rv;
    private Context context;
    private static OnTranscriptItemClickListener mOnItemClick;
    private static OnTranscriptItemDoubleClickListener mOnItemDoubleClick;
    private static OnTranscriptItemLongClickListener mOnItemLongClick;
    private int currentPosition = 0;
    private int oldPosition = 0;

    public void updateUI(int position){
        if(position == -1 || position == currentPosition) return;
        oldPosition = currentPosition;
        currentPosition = position;
        notifyItemChanged(oldPosition);
        notifyItemChanged(currentPosition);
        ((LinearLayoutManager) rv.getLayoutManager()).scrollToPositionWithOffset(currentPosition, 100);
        //rv.scrollToPosition(currentPosition);
    }

    public TranscriptAdapter(RecyclerView recyclerView, List<Transcript> lstData) {
        this.rv = recyclerView;
        this.lstData = lstData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_transcript, parent, false);
        viewHolder = new TranscriptViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TranscriptViewHolder vh = (TranscriptViewHolder) holder;
        Transcript transcript = lstData.get(position);
        if(transcript == null) return;
        vh.txt_id.setText(transcript.getId() + "");
        vh.txt_startAt.setText(transcript.getStrart() + "");

        int start = transcript.getStrart();
        int mm = 0, ss = 0, ms = 0;
        mm = start/(1000*60);
        ss = (start - mm*1000*60)/1000;
        ms = (start - mm*1000*60 - ss*1000)/10;
        String time = (mm > 9 ? mm + "" : "0" + mm) + ":" + (ss > 9 ? ss + "" : "0" + ss) + ":" + (ms > 9 ? ms + "" : "0" + ms);
        vh.txt_startDisplay.setText(time);
        vh.txt_endAt.setText((transcript.getStrart() + transcript.getDuration()) + "");
        vh.txt_transcript.setText(transcript.getTranscript());

        vh.txt_startDisplay.setTextColor(context.getResources().getColor(R.color.white));
        vh.txt_transcript.setTextColor(context.getResources().getColor(R.color.white));
        if(position == currentPosition){
            vh.txt_startDisplay.setTextColor(context.getResources().getColor(R.color.appcolor));
            vh.txt_transcript.setTextColor(context.getResources().getColor(R.color.appcolor));
        }
        else if (position == oldPosition){
            vh.txt_startDisplay.setTextColor(context.getResources().getColor(R.color.white));
            vh.txt_transcript.setTextColor(context.getResources().getColor(R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return lstData.size();
    }

    @Override
    public long getItemId(int position) {
        return lstData.get(position).getId();
    }

    public void setOnItemClickListener(OnTranscriptItemClickListener listener) {
        this.mOnItemClick = listener;
    }

    public void setOnItemDoubleClickListener(OnTranscriptItemDoubleClickListener listener) {
        this.mOnItemDoubleClick = listener;
    }

    public void setOnItemLongClickListener(OnTranscriptItemLongClickListener listener) {
        this.mOnItemLongClick = listener;
    }

    private int getPositionWithId(int id){
        for (int i = 0; i < lstData.size(); i++) {
            if(lstData.get(i).getId() == id) return i;
        }
        return -1;
    }

    public class TranscriptViewHolder extends RecyclerView.ViewHolder {
        TextView txt_id, txt_startAt, txt_startDisplay, txt_endAt, txt_transcript;
        LinearLayout ln_transcript_item;
        int count = 0;

        public TranscriptViewHolder(final View itemView) {
            super(itemView);
            ln_transcript_item = itemView.findViewById(R.id.ln_transcript_item);

            txt_id = itemView.findViewById(R.id.txt_id);
            txt_startAt = itemView.findViewById(R.id.txt_startAt);
            txt_startDisplay = itemView.findViewById(R.id.txt_startDisplay);
            txt_endAt = itemView.findViewById(R.id.txt_endAt);
            txt_transcript = itemView.findViewById(R.id.txt_transcript);

            ln_transcript_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    count++;
                    final int position = getPositionWithId(Integer.parseInt(txt_id.getText().toString()));
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(count == 1){ //Click
                                if (mOnItemClick != null)
                                    mOnItemClick.onItemClick(v, position, lstData.get(position));
                            }
                            else if (count == 2){ //Double click
                                if (mOnItemDoubleClick != null) {
                                    mOnItemDoubleClick.onItemDoubleClick(v, position, lstData.get(position));
                                }
                            }
                            count = 0;
                        }
                    }, 500);
                }
            });

            ln_transcript_item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getPositionWithId(Integer.parseInt(txt_id.getText().toString()));
                    if (mOnItemLongClick != null)
                        mOnItemLongClick.onItemLongClick(view, position, lstData.get(position));
                    return false;
                }
            });
        }
    }
}