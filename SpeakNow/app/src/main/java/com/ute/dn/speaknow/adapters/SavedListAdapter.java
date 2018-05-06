package com.ute.dn.speaknow.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ute.dn.speaknow.Interfaces.OnSavedItemClickListener;
import com.ute.dn.speaknow.Interfaces.OnSavedItemLongClickListener;
import com.ute.dn.speaknow.Interfaces.OnPracticeClickListener;
import com.ute.dn.speaknow.R;
import com.ute.dn.speaknow.databases.MyDatabaseHelper;
import com.ute.dn.speaknow.models.SavedItem;

import java.util.ArrayList;
import java.util.List;

public class SavedListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private ItemFilter mFilter = new ItemFilter();
    private List<SavedItem> lstData;
    private List<SavedItem> lstFilterData = new ArrayList<>();
    private Context context;
    private OnSavedItemClickListener mOnItemClick;
    private OnSavedItemLongClickListener mOnItemLongClick;
    private OnPracticeClickListener mOnPracticeClick;

    public SavedListAdapter(List<SavedItem> lstData) {
        this.lstData = lstData;
        lstFilterData.addAll(lstData);
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
        final SavedItem savedItem = lstFilterData.get(position);
        if (savedItem == null) return;

        vh.txt_timeSaved.setText(savedItem.getTimeSaved() + "");
        vh.txt_type.setText(savedItem.getType());
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
        return lstFilterData.size();
    }

    public void setOnItemClickListener(OnSavedItemClickListener listener) {
        this.mOnItemClick = listener;
    }

    public void setOnItemLongClickListener(OnSavedItemLongClickListener listener){
        this.mOnItemLongClick = listener;
    }

    public void setOnPracticeClickListener(OnPracticeClickListener listener) {
        this.mOnPracticeClick = listener;
    }

    public class TranscriptViewHolder extends RecyclerView.ViewHolder {
        TextView txt_timeSaved, txt_type, txt_videoId, txt_startAt, txt_endAt, txt_notes, txt_transcript;
        LinearLayout ln_content;
        ImageView img_delete, img_practice;

        public TranscriptViewHolder(final View itemView) {
            super(itemView);
            ln_content = itemView.findViewById(R.id.ln_content);

            img_delete = itemView.findViewById(R.id.img_delete);
            img_practice = itemView.findViewById(R.id.img_practice);

            txt_timeSaved = itemView.findViewById(R.id.txt_timeSaved);
            txt_type = itemView.findViewById(R.id.txt_type);
            txt_videoId = itemView.findViewById(R.id.txt_videoId);
            txt_startAt = itemView.findViewById(R.id.txt_startAt);
            txt_endAt = itemView.findViewById(R.id.txt_endAt);
            txt_notes = itemView.findViewById(R.id.txt_notes);
            txt_transcript = itemView.findViewById(R.id.txt_transcript);

            ln_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClick != null)
                        mOnItemClick.onItemClick(itemView, getLayoutPosition(), lstFilterData.get(getLayoutPosition()));
                }
            });

            ln_content.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (mOnItemLongClick != null)
                        mOnItemLongClick.onItemLongClick(itemView, getLayoutPosition(), lstFilterData.get(getLayoutPosition()));
                    return false;
                }
            });

            img_practice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnPracticeClick != null)
                        mOnPracticeClick.onPracticeClick(lstFilterData.get(getLayoutPosition()));
                }
            });
        }
    }

    public void filterData(String Query) {
        mFilter.filter(Query);
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().trim().toUpperCase();
            String[] filterArr = filterString.split(" ");
            lstFilterData.clear();

            FilterResults filterResults = new FilterResults();
            if (filterString.length() > 0) {
                for (int i = 0; i < lstData.size(); i++) {
                    SavedItem savedItem = lstData.get(i);
                    for (String str : filterArr) {
                        str = str.trim();
                        if (str.isEmpty() || str == "") continue;
                        if (savedItem.getTranscript().toUpperCase().contains(str)) {
                            lstFilterData.add(savedItem);
                            break;
                        }
                    }
                }
            } else {
                lstFilterData.addAll(lstData);
            }
            filterResults.values = lstFilterData;
            filterResults.count = lstFilterData.size();

            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notifyDataSetChanged();
        }
    }
}