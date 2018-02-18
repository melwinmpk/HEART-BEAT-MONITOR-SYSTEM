package com.mpk.melwin.patientapp;

import android.net.Uri;

/**
 * Created by Melwin on 1/14/2018.
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
