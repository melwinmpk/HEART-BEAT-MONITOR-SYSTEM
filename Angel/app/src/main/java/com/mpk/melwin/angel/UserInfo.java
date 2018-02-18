package com.mpk.melwin.angel;

/**
 * Created by Melwin on 1/15/2018.
 */

public class UserInfo {

    String Name;
    String Descritpion;
    String Uid;

    public UserInfo(){

    }

    public UserInfo(String Name,String Description,String Uid){
        this.Uid = Uid;
        this.Descritpion = Description;
        this.Name = Name;
    }

}
