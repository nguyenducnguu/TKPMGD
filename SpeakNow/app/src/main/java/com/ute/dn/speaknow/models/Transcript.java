package com.ute.dn.speaknow.models;

public class Transcript {
    private int id;
    private String transcript;
    private int strart;
    private int duration;

    public Transcript() {

    }

    public Transcript(int id, String transcript, int strart, int duration) {
        this.id = id;
        this.transcript = transcript;
        this.strart = strart;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public int getStrart() {
        return strart;
    }

    public void setStrart(int strart) {
        this.strart = strart;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
