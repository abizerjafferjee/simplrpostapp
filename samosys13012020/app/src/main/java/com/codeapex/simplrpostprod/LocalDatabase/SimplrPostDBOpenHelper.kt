package com.codeapex.simplrpostprod.LocalDatabase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper




class SimplrPostDBOpenHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME,
        factory, DATABASE_VERSION) {

    private val CREATE_TABLE_TODO = ("CREATE TABLE "
            + TABLE_NAME + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_USER_ID
            + " TEXT," + COLUMN_DATA + " TEXT" + ")")

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_TODO)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun addAddress(userId : String, data: String) {
        val values = ContentValues()
        values.put(COLUMN_USER_ID, userId)
        values.put(COLUMN_DATA, data)
        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAddress(id :String): String {
        val db = this.readableDatabase
        var data = ""
        val cursor = db.query(
            TABLE_NAME, arrayOf(COLUMN_ID, COLUMN_USER_ID, COLUMN_DATA), COLUMN_USER_ID + "=?",
            arrayOf(id), null, null, null, null
        )
        if (cursor != null && cursor.count>0) {
            cursor.moveToFirst()
             data = cursor.getString(2)
        }
        return data
    }

    fun deleteDataFromSQDb() {
        val db = this.readableDatabase
        db.execSQL("delete from "+ TABLE_NAME)
    }

    fun updateData(user_id:String,data : String){
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COLUMN_USER_ID, user_id)
        cv.put(COLUMN_DATA, data)
        db.update(TABLE_NAME, cv, "userId="+user_id, null)
    }
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "Simplrpost.db"
        val TABLE_NAME = "Simplrpost"
        val COLUMN_ID = "_id"
        val COLUMN_USER_ID = "userId"
        val COLUMN_DATA = "username"
    }
}