package com.ute.dn.speaknow.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ute.dn.speaknow.common.Const;
import com.ute.dn.speaknow.models.SavedItem;

import java.util.ArrayList;
import java.util.List;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    public MyDatabaseHelper(Context context)  {
        super(context, Const.DATABASE_NAME, null, Const.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("MyDatabaseHelper", "MyDatabaseHelper.onCreate ... ");
        // Script to create table.
        String script = "CREATE TABLE " + Const.TABLE_NAME + "("
                + Const.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," + Const.COLUMN_NAME_TYPE + " TEXT,"
                + Const.COLUMN_NAME_VIDEOID + " TEXT,"
                + Const.COLUMN_NAME_TRANSCRIPT + " TEXT," + Const.COLUMN_NAME_STARTAT + " INTEGER,"
                + Const.COLUMN_NAME_ENDAT + " INTEGER," + Const.COLUMN_NAME_NOTES + " TEXT)";
        // Execute script.
        db.execSQL(script);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("MyDatabaseHelper", "MyDatabaseHelper.onUpgrade ... ");
        // Drop table
        db.execSQL("DROP TABLE IF EXISTS " + Const.TABLE_NAME);
        // Recreate
        onCreate(db);
    }

    public SavedItem getSavedItem(int id) {
        Log.i("MyDatabaseHelper", "MyDatabaseHelper.get ... " + id);

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Const.TABLE_NAME, new String[] { Const.COLUMN_NAME_ID, Const.COLUMN_NAME_TYPE,
                        Const.COLUMN_NAME_VIDEOID, Const.COLUMN_NAME_TRANSCRIPT, Const.COLUMN_NAME_STARTAT,
                        Const.COLUMN_NAME_ENDAT, Const.COLUMN_NAME_NOTES },
                Const.COLUMN_NAME_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        SavedItem savedItem = new SavedItem(Long.parseLong(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3),
                Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5)),
                cursor.getString(6));
        return savedItem;
    }

    public List<SavedItem> getAllSavedItem() {
        Log.i("MyDatabaseHelper", "MyDatabaseHelper.getAll ... " );

        List<SavedItem> savedList = new ArrayList<SavedItem>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Const.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                SavedItem savedItem = new SavedItem(Long.parseLong(cursor.getString(0)),
                        cursor.getString(1), cursor.getString(2), cursor.getString(3),
                        Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5)),
                        cursor.getString(6));
                savedList.add(savedItem);
            } while (cursor.moveToNext());
        }
        return savedList;
    }

    public int getSavedItemCount() {
        Log.i("MyDatabaseHelper", "MyDatabaseHelper.getSavedItemCount ... " );

        String countQuery = "SELECT  * FROM " + Const.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public boolean addSavedItem(SavedItem savedItem) {
        Log.i("MyDatabaseHelper", "MyDatabaseHelper.addSavedItem ... " + savedItem.getTranscript());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Const.COLUMN_NAME_ID, savedItem.getTimeSaved());
        values.put(Const.COLUMN_NAME_TYPE, savedItem.getType());
        values.put(Const.COLUMN_NAME_VIDEOID, savedItem.getVideoId());
        values.put(Const.COLUMN_NAME_TRANSCRIPT, savedItem.getTranscript());
        values.put(Const.COLUMN_NAME_STARTAT, savedItem.getStartAt());
        values.put(Const.COLUMN_NAME_ENDAT, savedItem.getEndAt());
        values.put(Const.COLUMN_NAME_NOTES, savedItem.getNotes());

        long success = db.insert(Const.TABLE_NAME, null, values);

        db.close();

        return success == -1 ? false : true;
    }

    public boolean updateSavedItem(SavedItem savedItem) {
        Log.i("MyDatabaseHelper", "MyDatabaseHelper.updateSavedItem ... "  + savedItem.getTranscript());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Const.COLUMN_NAME_TYPE, savedItem.getType());
        values.put(Const.COLUMN_NAME_VIDEOID, savedItem.getVideoId());
        values.put(Const.COLUMN_NAME_TRANSCRIPT, savedItem.getTranscript());
        values.put(Const.COLUMN_NAME_STARTAT, savedItem.getStartAt());
        values.put(Const.COLUMN_NAME_ENDAT, savedItem.getEndAt());
        values.put(Const.COLUMN_NAME_NOTES, savedItem.getNotes());

        int success = db.update(Const.TABLE_NAME, values, Const.COLUMN_NAME_ID + " = ?",
                                new String[]{String.valueOf(savedItem.getTimeSaved())});

        return success == 0 ? false : true;
    }

    public boolean deleteSavedItem(long id) {
        Log.i("MyDatabaseHelper", "MyDatabaseHelper.deleteSavedItem ... " + id);

        SQLiteDatabase db = this.getWritableDatabase();
        int success = db.delete(Const.TABLE_NAME, Const.COLUMN_NAME_ID + " = ?",
                                new String[] { String.valueOf(id) });
        db.close();

        return success == 0 ? false : true;
    }
}