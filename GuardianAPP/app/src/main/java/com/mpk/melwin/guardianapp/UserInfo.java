package com.mpk.melwin.guardianapp;

/**
 * Created by Melwin on 3/16/2018.
 */

public class UserInfo {

    String Name;
    String Descritpion;
    String Uid;
    String Pulse;
    String profileImageUrl;


    public UserInfo(){

    }

    public UserInfo(String Name,String Description,String Uid,String Pulse,String profileImageUrl){
        this.Uid = Uid;
        this.Descritpion = Description;
        this.Name = Name;
        this.Pulse =Pulse;
        this.profileImageUrl = profileImageUrl;

    }

    public String getPulse() {
        return Pulse;
    }

    public void setPulse(String pulse) {
        Pulse = pulse;
    }
}
