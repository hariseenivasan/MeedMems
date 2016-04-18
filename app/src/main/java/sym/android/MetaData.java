package sym.android;

import java.util.Date;

/**
 * Created by Aishwarya on 4/16/2016.
 */
public class MetaData {
    private long id;
    private String createdDate;
    private String userList;
    private String fileName;
    private String groupName;

    public MetaData(){

    }

    public MetaData(long id, String createdDate, String userList, String fileName, String groupName){
        this.setId(id);
        this.setCreatedDate(createdDate);
        this.setUserList(userList);
        this.setFileName(fileName);
        this.setGroupName(groupName);
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

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
