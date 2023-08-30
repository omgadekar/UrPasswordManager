package com.example.password_generator_project.data;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.password_generator_project.MainActivity;
import com.example.password_generator_project.model.pass_db;
import com.example.password_generator_project.params.params;

import java.util.ArrayList;
import java.util.List;

public class MyDBHandler extends SQLiteOpenHelper{
    public MyDBHandler(Context context){
        super(context, params.DB_NAME,null ,params.DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "CREATE TABLE "+params.TABLE_NAME+"("+params.WEBSITE_NAME+" varchar(50),"
                +params.PASS+" varchar(20));";
        Log.d("DBPass","Query Running is :"+create);
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
    public void addpass(pass_db pass_db){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(params.WEBSITE_NAME,pass_db.getWebsitename());
        values.put(params.PASS,pass_db.getPass());
        db.insert(params.TABLE_NAME,null,values);
        Log.d("DBPass2","Insert Query Successful");
        db.close();
    }
    public List<pass_db> getAllPasswords(){
        List<pass_db> passlist = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        //Query to read from Database
        String select = "SELECT * FROM "+params.TABLE_NAME;
        Cursor cursor = db.rawQuery(select,null);
        //Loop
        if(cursor.moveToLast()){
            do{
                pass_db passDb = new pass_db();
                passDb.setWebsitename(cursor.getString(0));
                passDb.setPass(cursor.getString(1));
                passlist.add(passDb);
            }while(cursor.moveToPrevious());
        }
        return passlist;
    }
    public void deletePassword(String webname,String pass){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(params.TABLE_NAME, params.PASS +"=? AND "+params.WEBSITE_NAME+"=?",new String[]{pass,webname});
        db.close();
        Log.d("DELETE","Delete Query Exectued");
    }
}