package com.example.bhavya.myapplication_synced;

/**
 * Created by Aishwarya on 4/16/2016.
 */
public class MetaData {
    private long id;
    private long imageCRC;
    private String userList;
    private String owner;

    public MetaData(){

    }

    public MetaData(long id, long imageCRC, String userList, String owner){
        this.id = id;
        this.imageCRC = imageCRC;
        this.userList = userList;
        this.owner = owner;
    }



    public String getUserList() {
        return userList;
    }

    public void setUserList(String userList) {
        this.userList = userList;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getImageCRC() {
        return imageCRC;
    }

    public void setImageCRC(long imageCRC) {
        this.imageCRC = imageCRC;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
