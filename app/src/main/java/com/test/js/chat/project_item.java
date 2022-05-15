package com.test.js.chat;

public class project_item {

    private String Project_Name; //
    private int Project_Usersize;//
    private String Project_Users;//
    private String admin;//
    private String ProjectID; //

    public String getProject_Name() {
        return this.Project_Name;
    }
    public String getadmin() {
        return this.admin;
    }
    public  int getProject_Usersize() {
        return this.Project_Usersize;
    }
    public String getProject_Users(){
        return this.Project_Users;
    }
    public String getProjectID() {
        return this.ProjectID;
    }

    public void setProject_Name(String Project_Name){
        this.Project_Name = Project_Name;
    }
    public void setProject_Usersize(int room_size){
        this.Project_Usersize = Project_Usersize;
    }
    public void setProject_Users(String Project_Users){
        this.Project_Users = Project_Users;
    }
    public void setProjectID(String ProjectID){
        this.ProjectID = ProjectID;
    }
    public void setadmin(String admin){
        this.admin = admin;
    }
}
