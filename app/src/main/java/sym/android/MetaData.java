package sym.android;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by Aishwarya on 4/16/2016.
 */
public class MetaData {
    private long id;
    private String createdDate;
    private ArrayList<String> userList;
    private ArrayList<String> fileNameList;
    private ArrayList<String> sizeList;
    private String groupName;
    private String repoPath;


    public MetaData(String groupName){
        this.setGroupName( groupName);
    }

    public MetaData(long id, String createdDate, ArrayList<String> userList, ArrayList<String> fileName, ArrayList<String> size, String groupName){
        this.setId(id);
        this.setCreatedDate(createdDate);
        this.setGroupDetails(fileName, size);
        this.setGroupName(groupName);
        this.setUserList(userList);
    }

    public void setGroupDetails( ArrayList<String> fileName, ArrayList<String> size) {
        if(!(fileName.size()== size.size()))
            throw new IllegalArgumentException("Length of filename and Size doesn't match.");
        this.fileNameList = fileName;
        this.sizeList = size;
    }

    public ArrayList<String> getSizeList() { return sizeList; }

    public ArrayList<String> getUserList() { return userList; }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public ArrayList<String> getFileNameList() {
        return fileNameList;
    }

    public void setUserList(ArrayList<String> userList) {
        this.userList = userList;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Deprecated
    public void DebugMe(){
        Log.d("MetaData "+groupName, "FileNames: " + Arrays.toString(fileNameList.toArray()));
        Log.d("MetaData "+groupName, "Size: " + Arrays.toString(sizeList.toArray()));
    }
/*
    public ContentValues getMetadataAsContentValues(Context context){
        ContentValues values = new ContentValues();
        values.put(context.getString(R.string.KEY_ID), this.getId());
        values.put(context.getString(R.string.KEY_CREATEDDATE), this.getCreatedDate());
        values.put(context.getString(R.string.KEY_USERLIST),ArrayListToJSONArrayString(this.getUserList(),context.getString(R.string.KEY_USERLIST)));
        values.put(context.getString(R.string.KEY_FILENAME), ArrayListToJSONArrayString(this.getFileNameList(),context.getString(R.string.KEY_FILENAME)));
        values.put(context.getString(R.string.KEY_SIZE), ArrayListToJSONArrayString(this.getSizeList(),context.getString(R.string.KEY_SIZE)));
        values.put(context.getString(R.string.KEY_GROUPNAME), this.getGroupName());
        return  values;
    }
    */
}
