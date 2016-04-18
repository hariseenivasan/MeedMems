package sym.android;

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
    private static final String TABLE_METADATA = "metadata_SYM";
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATEDDATE = "createdDate";
    private static final String KEY_USERLIST = "userList";
    private static final String KEY_FILENAME = "fileName";
    private static final String KEY_GROUPNAME = "groupName";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_METADATA_TABLE = "CREATE TABLE " + TABLE_METADATA + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CREATEDDATE + " TEXT,"
                + KEY_USERLIST + " TEXT," + KEY_FILENAME + " TEXT," +KEY_GROUPNAME+" TEXT"+")";
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
        values.put(KEY_CREATEDDATE, metaData.getCreatedDate());
        values.put(KEY_USERLIST, metaData.getUserList());
        values.put(KEY_FILENAME, metaData.getFileName());
        values.put(KEY_GROUPNAME, metaData.getGroupName());

        // Inserting Row
        db.insert(TABLE_METADATA, null, values);
        db.close(); // Closing database connection
    }

    public MetaData getMetaData(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_METADATA, new String[] { KEY_ID,
                        KEY_CREATEDDATE, KEY_USERLIST, KEY_FILENAME, KEY_GROUPNAME }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        MetaData metaData = new MetaData(Long.parseLong(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
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
        contentValues.put(KEY_CREATEDDATE, metadata.getCreatedDate());
        contentValues.put(KEY_USERLIST, metadata.getUserList());
        contentValues.put(KEY_FILENAME, metadata.getFileName());
        contentValues.put(KEY_GROUPNAME, metadata.getGroupName());
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
                metaData.setCreatedDate(cursor.getString(1));
                metaData.setUserList(cursor.getString(2));
                metaData.setFileName(cursor.getString(3));
                metaData.setGroupName(cursor.getString(4));
                // Adding contact to list
                metaDataList.add(metaData);
            } while (cursor.moveToNext());
        }

        // return contact list
        return metaDataList;
    }
}
