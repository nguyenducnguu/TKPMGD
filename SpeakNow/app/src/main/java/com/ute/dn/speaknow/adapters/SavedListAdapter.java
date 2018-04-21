package com.ute.dn.speaknow.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ute.dn.speaknow.Interfaces.OnSavedItemClickListener;
import com.ute.dn.speaknow.Interfaces.OnItemLongClickListener;
import com.ute.dn.speaknow.Interfaces.OnPracticeClickListener;
import com.ute.dn.speaknow.R;
import com.ute.dn.speaknow.databases.MyDatabaseHelper;
import com.ute.dn.speaknow.models.SavedItem;

import java.util.List;

public class SavedListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<SavedItem> lstData;
    private Context context;
    private OnSavedItemClickListener mOnItemClick;
    private OnItemLongClickListener mOnItemLongClick;
    private OnPracticeClickListener mOnPracticeClick;
    private static final int REQUEST_CODE = 1234;

    public SavedListAdapter(List<SavedItem> lstData) {
        this.lstData = lstData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_saved, parent, false);
        viewHolder = new TranscriptViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final TranscriptViewHolder vh = (TranscriptViewHolder) holder;
        final SavedItem savedItem = lstData.get(position);
        if (savedItem == null) return;

        vh.txt_videoId.setText(savedItem.getVideoId());
        vh.txt_startAt.setText(savedItem.getStartAt() + "");
        vh.txt_endAt.setText(savedItem.getEndAt() + "");
        vh.txt_notes.setText(savedItem.getNotes().length() == 0 ? "..." : savedItem.getNotes());
        vh.txt_transcript.setText(savedItem.getTranscript());

        vh.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setMessage("Are you sure you want to delete?")
                        .setTitle("Delete");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MyDatabaseHelper db = new MyDatabaseHelper(context);
                        if (db.deleteSavedItem(savedItem.getTimeSaved())) {
                            Toast.makeText(context, "Delete successfully!", Toast.LENGTH_SHORT).show();
                            lstData.remove(savedItem);
                            SavedListAdapter.this.notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, "Delete failed!", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return lstData.size();
    }

    public void setOnItemClickListener(OnSavedItemClickListener listener) {
        this.mOnItemClick = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        this.mOnItemLongClick = listener;
    }

    public void setOnPracticeClickListener(OnPracticeClickListener listener) {
        this.mOnPracticeClick = listener;
    }

    public class TranscriptViewHolder extends RecyclerView.ViewHolder {
        TextView txt_videoId, txt_startAt, txt_endAt, txt_notes, txt_transcript;
        LinearLayout ln_content;
        ImageView img_delete, img_practice;

        public TranscriptViewHolder(final View itemView) {
            super(itemView);
            ln_content = itemView.findViewById(R.id.ln_content);

            img_delete = itemView.findViewById(R.id.img_delete);
            img_practice = itemView.findViewById(R.id.img_practice);

            txt_videoId = itemView.findViewById(R.id.txt_videoId);
            txt_startAt = itemView.findViewById(R.id.txt_startAt);
            txt_endAt = itemView.findViewById(R.id.txt_endAt);
            txt_notes = itemView.findViewById(R.id.txt_notes);
            txt_transcript = itemView.findViewById(R.id.txt_transcript);

            ln_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClick != null)
                        mOnItemClick.onItemClick(itemView, getLayoutPosition(), lstData.get(getLayoutPosition()));
                }
            });

            ln_content.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (mOnItemLongClick != null)
                        mOnItemLongClick.onItemLongClick(itemView, getLayoutPosition());
                    return false;
                }
            });

            img_practice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnPracticeClick != null)
                        mOnPracticeClick.onPracticeClick(lstData.get(getLayoutPosition()));
                }
            });
        }
    }
}