package com.ute.dn.speaknow.models;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Comparator;

public class SavedItem implements Serializable, Comparable<SavedItem> {
    private String videoId;
    private String transcript;
    private int startAt;
    private int endAt;
    private String notes;
    private long timeSaved;

    public SavedItem() {

    }

    public SavedItem(long timeSaved, String videoId, String transcript, int startAt, int endAt, String notes) {
        this.videoId = videoId;
        this.transcript = transcript;
        this.startAt = startAt;
        this.endAt = endAt;
        this.notes = notes;
        this.timeSaved = timeSaved;
    }

    public SavedItem(String videoId, String transcript, int startAt, int endAt, String notes) {
        this.videoId = videoId;
        this.transcript = transcript;
        this.startAt = startAt;
        this.endAt = endAt;
        this.notes = notes;
        this.timeSaved = System.currentTimeMillis();
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public int getStartAt() {
        return startAt;
    }

    public void setStartAt(int startAt) {
        this.startAt = startAt;
    }

    public int getEndAt() {
        return endAt;
    }

    public void setEndAt(int endAt) {
        this.endAt = endAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public long getTimeSaved() {
        return timeSaved;
    }

    public void setTimeSaved(long timeSaved) {
        this.timeSaved = timeSaved;
    }

    @Override
    public String toString()  {
        return this.transcript;
    }

    @Override
    public int compareTo(@NonNull SavedItem savedItem) {
        return (int)(savedItem.getTimeSaved() - this.timeSaved);
    }
}
