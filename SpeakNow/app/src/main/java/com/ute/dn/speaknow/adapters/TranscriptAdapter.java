package com.ute.dn.speaknow.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ute.dn.speaknow.Interfaces.OnSavedItemClickListener;
import com.ute.dn.speaknow.Interfaces.OnItemLongClickListener;
import com.ute.dn.speaknow.Interfaces.OnTranscriptItemClickListener;
import com.ute.dn.speaknow.R;
import com.ute.dn.speaknow.models.Transcript;

import java.util.List;

public class TranscriptAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Transcript> lstData;
    private int currentPosition = 0;
    private Context context;
    private static OnTranscriptItemClickListener mOnItemClick;
    private static OnItemLongClickListener mOnItemLongClick;

    public TranscriptAdapter(List<Transcript> lstData) {
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
        final TranscriptViewHolder vh = (TranscriptViewHolder) holder;
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

        //SpannableString str = new SpannableString(transcript.getTranscript());
        //str.setSpan(new BackgroundColorSpan(Color.RED), 0, 11, 0);
        vh.txt_transcript.setText(transcript.getTranscript());
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

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mOnItemLongClick = listener;
    }

    public class TranscriptViewHolder extends RecyclerView.ViewHolder {
        TextView txt_id, txt_startAt, txt_startDisplay, txt_endAt, txt_transcript;
        LinearLayout ln_transcript_item;

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
                public void onClick(View v) {
                    if (mOnItemClick != null)
                        mOnItemClick.onItemClick(itemView, getLayoutPosition(), lstData.get(getLayoutPosition()));
                }
            });
            ln_transcript_item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (mOnItemLongClick != null)
                        mOnItemLongClick.onItemLongClick(itemView, getLayoutPosition());
                    return false;
                }
            });
        }
    }
}