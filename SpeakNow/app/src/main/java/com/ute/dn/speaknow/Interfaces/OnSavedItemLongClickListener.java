package com.ute.dn.speaknow.Interfaces;

import android.view.View;

import com.ute.dn.speaknow.models.SavedItem;

public interface OnSavedItemLongClickListener {
    void onItemLongClick(View itemView, int position, SavedItem savedItem);
}
