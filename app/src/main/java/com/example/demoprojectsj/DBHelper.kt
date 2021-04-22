package com.example.demoprojectsj

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.FileInputStream
import java.io.IOException


class DBHelper(context: Context) : SQLiteOpenHelper(context, "UserDatabase", null, 1) {

    val DBName = "Login.db"

    override fun onCreate(db: SQLiteDatabase?) {
        if (db != null) {
            db.execSQL("create Table users(email TEXT primary key, password TEXT)")
            db.execSQL("create Table UserDetails (email TEXT primary key, name TEXT, phone TEXT, image TEXT)")

        }

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun insertData(email: String, password: String) : Boolean{
        val MyDB = this.writableDatabase;
        val contentValues: ContentValues= ContentValues();
        contentValues.put("email", email);
        contentValues.put("password", password);
        val result = MyDB.insert("users", null, contentValues);
        return result.toInt() != -1;
    }

    fun checkEmail(email: String) : Boolean{
        val MyDB = this.writableDatabase;
        val cursor = MyDB.rawQuery("Select * from users where email = ?", arrayOf(email))
        return cursor.count > 0;
    }

    fun  checkEmailPassword(email: String, password: String) : Boolean{
        val MyDB = this.writableDatabase;
        val cursor = MyDB.rawQuery("Select * from users where email = ? and password = ?", arrayOf(email, password));
        return cursor.count >0;
    }

    fun changePassword(email: String,password: String){
        val DB = this.writableDatabase
        val contentValues =ContentValues()
        contentValues.put("email",email)
        contentValues.put("password", password)

        DB.update("users", contentValues, "email=?", arrayOf(email))

    }


    fun  InsertUserData(name: String, email: String, phone: String, image: String?, password: String) : Boolean{
        val DB = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("email", email)
        contentValues.put("name", name)
        contentValues.put("phone", phone)
        contentValues.put("image", image)

        this.insertData(email, password)

        val result = DB.insert("UserDetails", null, contentValues).toInt()
        return result != -1
    }

    fun getImage(email: String): Bitmap? {
        val db = this.writableDatabase
        var bt: Bitmap? = null
        val cursor = db.rawQuery(" select * from UserDetails where email = ?", arrayOf(email))
        if (cursor.moveToNext()) {
            val image = cursor.getBlob(4)
            bt = BitmapFactory.decodeByteArray(image, 0, image.size)
        }
        return bt
    }

    fun UpdateUserData(name: String, email: String, phone: String,image: String?) : Boolean{
        val DB = this.writableDatabase
        val contentValues =ContentValues()
        contentValues.put("name", name)
        contentValues.put("phone", phone)
        contentValues.put("image", image)

        val cursor = DB.rawQuery("select * from UserDetails where email = ?", arrayOf(email))
        return if (cursor.count >0) {
            val result = DB.update("UserDetails", contentValues, "email=?", arrayOf(email))

            result != -1
        } else
            false;
    }



    fun deleteUserData( email:String): Boolean{
        val db = this.writableDatabase

//        val cursor = db.rawQuery("select * from UserDetails where name = ?", arrayOf(email));
//        if (cursor.getCount()>0) {
        val result1 = db.delete("users","email=?", arrayOf(email))
            val result = db.delete("UserDetails", "email=?", arrayOf(email));

        return result!=-1 && result1 != -1
//        }
//        else
//            return false;
    }

    fun  getData(email: String): Cursor {
        val DB = this.writableDatabase

        val cursor = DB.rawQuery("select * from UserDetails where email = ?", arrayOf(email));
        return cursor;
    }

    fun getUserName(email: String): String? {
        val db = this.writableDatabase
        var userName:String?=null

        val c = db.rawQuery("select name from UserDetails where email = ?", arrayOf(email))

        if(c.moveToFirst()){
            userName=c.getString(c.getColumnIndex("name"))
        }
        return userName


    }
}

