package com.dlbcsemse.iuthesisconnect.helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "iuThesisConnect.db"
        private const val DATABASE_VERSION = 1
        private const val PROFILE_TABLE_NAME = "profile"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PICTURE = "picture"
        private const val COLUMN_ROLE = "role"

        private const val ROLE_TABLE_NAME = "role"
    }

    override fun onCreate(db: SQLiteDatabase) {
        var CREATE_TABLE = ("CREATE TABLE $PROFILE_TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_NAME TEXT, "
                + "$COLUMN_EMAIL Text), "
                + "$COLUMN_PICTURE Text, "
                + "$COLUMN_ROLE int")
        db.execSQL(CREATE_TABLE)


        CREATE_TABLE = ("CREATE TABLE $ROLE_TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_NAME TEXT")
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $PROFILE_TABLE_NAME")
        onCreate(db)
    }


    fun insertData(context: Context, name: String, email : String) {
        val dbHelper = DatabaseHelper(context)
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put("name", name)
            put("email", email )
        }

        db.insert("supervisor", null, values)
        db.close()
    }

    fun readData(context: Context): List<String> {
        val dbHelper = DatabaseHelper(context)
        val db = dbHelper.readableDatabase

        val projection = arrayOf("id", "name")

        val cursor: Cursor = db.query(
            "supervisor",   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            null,          // The columns for the WHERE clause
            null,       // The values for the WHERE clause
            null,          // Don't group the rows
            null,           // Don't filter by row groups
            null              // The sort order
        )

        val items = mutableListOf<String>()
        with(cursor) {
            while (moveToNext()) {
                val name = getString(getColumnIndexOrThrow("name"))
                items.add(name)
            }
        }
        cursor.close()

        return items
    }
}