package com.example.password_generator_project.model;

import static android.widget.Toast.*;

import android.util.Log;
import android.widget.Toast;

public class pass_db {
    private String pass;
    private String websitename;
    public pass_db(String pass, String wn){
        this.pass = pass;
        websitename =  wn;

    }
    public  pass_db(){}
    public String getPass(){
        return pass;
    }
    public void setPass(String pass){
        this.pass=pass;
    }
    public String getWebsitename(){
        return websitename;
    }
    public void setWebsitename(String wn){
        websitename=wn;
    }
}
