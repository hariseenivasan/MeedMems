package com.example.bhavya.myapplication_synced;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 14-04-2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ShareYourMoments_DB";

    // Contacts table name
    private static final String TABLE_METADATA = "t_metadata";
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_IMAGECRC = "imageCRC";
    private static final String KEY_USERLIST = "userList";
    private static final String KEY_OWNER = "owner";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_METADATA_TABLE = "CREATE TABLE " + TABLE_METADATA + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_IMAGECRC + " INTEGER,"
                + KEY_USERLIST + " TEXT," + KEY_OWNER + " TEXT" +")";
        db.execSQL(CREATE_METADATA_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_METADATA);

        // Create tables again
        onCreate(db);
    }

    public void addMetaData  (MetaData metaData)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, metaData.getId());
        values.put(KEY_IMAGECRC, metaData.getImageCRC());
        values.put(KEY_USERLIST, metaData.getUserList());
        values.put(KEY_OWNER, metaData.getOwner());

        // Inserting Row
        db.insert(TABLE_METADATA, null, values);
        db.close(); // Closing database connection
    }

    public MetaData getMetaData(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_METADATA, new String[] { KEY_ID,
                        KEY_IMAGECRC, KEY_USERLIST, KEY_OWNER }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        MetaData metaData = new MetaData(Long.parseLong(cursor.getString(0)),
                Long.parseLong(cursor.getString(1)), cursor.getString(2), cursor.getString(3));
        // return contact
        return metaData;
    }

    /*public Cursor getData(long id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM "+ TABLE_METADATA+ " where id="+id+"", null );
        return res;
    }*/

    public long getMaxId(){
        String maxIdQuery = "SELECT MAX(id) FROM " + TABLE_METADATA;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(maxIdQuery, null);

        try {
            cursor.moveToFirst();
            return cursor.getLong(0);
        } finally {
            cursor.close();
        }

    }

    public int numberOfRows(){
        String countQuery = "SELECT  * FROM " + TABLE_METADATA;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        // return count
        return cursor.getCount();
    }

    public int updateContact (MetaData metadata)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ID, metadata.getId());
        contentValues.put(KEY_IMAGECRC, metadata.getImageCRC());
        contentValues.put(KEY_USERLIST, metadata.getUserList());
        contentValues.put(KEY_OWNER, metadata.getOwner());
        //updating row
        return db.update(TABLE_METADATA, contentValues, KEY_ID + " = ?",
                new String[] { String.valueOf(metadata.getId()) });
    }

    public void deleteContact (MetaData metaData)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_METADATA, KEY_ID + " = ?",
                new String[] { String.valueOf(metaData.getId()) });
        db.close();
    }

    public List<MetaData> getAllMetaData()
    {
        List<MetaData> metaDataList = new ArrayList<MetaData>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_METADATA;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MetaData metaData = new MetaData();
                metaData.setId(Long.parseLong(cursor.getString(0)));
                metaData.setImageCRC(Long.parseLong(cursor.getString(1)));
                metaData.setUserList(cursor.getString(2));
                metaData.setOwner(cursor.getString(3));
                // Adding contact to list
                metaDataList.add(metaData);
            } while (cursor.moveToNext());
        }

        // return contact list
        return metaDataList;
    }
}
