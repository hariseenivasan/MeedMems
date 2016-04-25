package sym.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
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
    public static final String KEY_ID = "id";
    public static final String KEY_CREATEDDATE = "createdDate";
    public static final String KEY_USERLIST = "userList";
    public static final String KEY_FILENAME = "fileName";
    public static final String KEY_SIZE = "size";
    public static final String KEY_GROUPNAME = "groupName";

    public static final int DB_OP_UPDATE = 0x1;
    public static final int DB_OP_DELETE = 0x2;
    public static final int DB_OP_ADD = 0x3;
    public static final int DB_OP_SYNC = 0x4;


    public static ContentValues contentFromMetadata(MetaData metaData) throws JSONException {
     return contentFromMetadata(metaData,false);
    }
    public static ContentValues contentFromMetadata(MetaData metaData,boolean nullId) throws JSONException {
        ContentValues values =  new ContentValues();

        values.put(KEY_ID, nullId?null: metaData.getId());
        values.put(KEY_CREATEDDATE, metaData.getCreatedDate());
        values.put(KEY_USERLIST,new JSONArray(metaData.getUserList()).toString());
        values.put(KEY_FILENAME, new JSONArray(metaData.getFileNameList()).toString());
        values.put(KEY_SIZE, new JSONArray(metaData.getSizeList()).toString());
        values.put(KEY_GROUPNAME, metaData.getGroupName());
        return values;
    }
    public static String JSONStringFromMetaData(MetaData metaData) throws JSONException {
        JSONObject json =  new JSONObject();
        json.put(KEY_ID, metaData.getId());
        json.put(KEY_CREATEDDATE, metaData.getCreatedDate());
        json.put(KEY_USERLIST,metaData.getUserList());
        json.put(KEY_FILENAME, metaData.getFileNameList());
        json.put(KEY_SIZE, metaData.getSizeList());
        json.put(KEY_GROUPNAME, metaData.getGroupName());

        return json.toString();
    }

    public static MetaData MetaDataFromJSONString(String JSONString) throws JSONException{
        JSONObject json = new JSONObject(JSONString);

        MetaData mData = new MetaData(json.getLong(KEY_ID),json.getString(KEY_CREATEDDATE),
                JSONObjectStringtoList(json.getString(KEY_USERLIST)), JSONObjectStringtoList(json.getString(KEY_FILENAME)),
                JSONObjectStringtoList(json.getString(KEY_SIZE)),json.getString(KEY_GROUPNAME));
        return mData;
    }

    public static ArrayList<String> JSONObjectStringtoList(String jsonObjectString) throws JSONException {
        JSONObject json = new JSONObject(jsonObjectString);
        return jsonArraytoList(json.optJSONArray(KEY_USERLIST));
    }
    public static ArrayList<String> jsonArraytoList(JSONArray jArray) throws JSONException {
        ArrayList<String> ao  = new ArrayList<String>();
        for(int i=0;i<jArray.length();i++)
            ao.add(jArray.get(i).toString());
        return ao;
    }

    public static String ArrayListToJSONArrayString(ArrayList<String> arrayList,String jsonString) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(jsonString, new JSONArray(arrayList));
        return json.toString();
    }
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_METADATA_TABLE = "CREATE TABLE " + TABLE_METADATA + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_CREATEDDATE + " TEXT,"
                + KEY_USERLIST + " TEXT," + KEY_FILENAME + " TEXT," + KEY_SIZE + " TEXT," +KEY_GROUPNAME+" TEXT UNIQUE"+")";
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


    public void addMetaData  (MetaData metaData) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values =contentFromMetadata(metaData,true);

        // Inserting Row
        db.insert(TABLE_METADATA, null, values);
        db.close(); // Closing database connection
    }

    public MetaData getMetaData(long id) throws JSONException {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_METADATA, new String[]{KEY_ID,
                        KEY_CREATEDDATE, KEY_USERLIST, KEY_FILENAME, KEY_SIZE, KEY_GROUPNAME}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();


        MetaData metaData = new MetaData(Long.parseLong(cursor.getString(0)),cursor.getString(1),
                JSONObjectStringtoList (cursor.getString(2)), JSONObjectStringtoList(cursor.getString(3)),
                JSONObjectStringtoList (cursor.getString(4)), cursor.getString(5));
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

    public int updateMetaData (MetaData metadata) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = contentFromMetadata(metadata,true);
        //updating row
        return db.update(TABLE_METADATA, contentValues, KEY_GROUPNAME + " = ?",
                new String[] { String.valueOf(metadata.getGroupName()) });
    }

    public void deleteMetaData (MetaData metaData)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_METADATA, KEY_GROUPNAME + " = ?",
                new String[] { String.valueOf(metaData.getGroupName()) });
        db.close();
    }

    public List<MetaData> getAllMetaData() throws JSONException {
        List<MetaData> metaDataList = new ArrayList<MetaData>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_METADATA;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                    MetaData metaData = new MetaData(Long.parseLong(cursor.getString(0)),cursor.getString(1),
                            jsonArraytoList(new JSONArray(cursor.getString(2))), jsonArraytoList(new JSONArray(cursor.getString(3))),
                            jsonArraytoList(new JSONArray(cursor.getString(4))), cursor.getString(5));

                // Adding contact to list
                metaDataList.add(metaData);
            } while (cursor.moveToNext());
        }

        // return contact list
        return metaDataList;
    }
    public List<String> getKeyMetaData(String key) throws JSONException {
        List<String> resultSetArray = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT  "+key+" FROM " + TABLE_METADATA;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                // Adding contact to list
                resultSetArray.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // return contact list
        return resultSetArray;
    }
}
