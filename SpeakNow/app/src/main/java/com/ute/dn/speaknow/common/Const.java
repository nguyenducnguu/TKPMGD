package com.ute.dn.speaknow.common;

public class Const {
    public static final String DATABASE_NAME = "speaknow";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "SavedItem";
    public static final String COLUMN_NAME_ID = "timeSaved";
    public static final String COLUMN_NAME_TYPE = "type";
    public static final String COLUMN_NAME_VIDEOID = "videoId";
    public static final String COLUMN_NAME_TRANSCRIPT = "transcript";
    public static final String COLUMN_NAME_STARTAT = "startAt";
    public static final String COLUMN_NAME_ENDAT = "endAt";
    public static final String COLUMN_NAME_NOTES = "notes";

    public static final int PICKFILE_VIDEO_REQUEST_CODE = 1;
    public static final int PICKFILE_TRANSCRIPT_REQUEST_CODE = 2;
    public static final int MAIN_ONLINE_PICKFILE_TRANSCRIPT_REQUEST_CODE = 3;
}
