package com.dlbcsemse.iuthesisconnect.helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import android.database.DatabaseUtils
import com.dlbcsemse.iuthesisconnect.model.UserProfile

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "iuThesisConnect.db"
        private const val DATABASE_VERSION = 1
        private const val PROFILE_TABLE_NAME = "profile"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PICTURE = "picture"
        private const val COLUMN_ROLE = "role"

        private const val ROLE_TABLE_NAME = "role"
        private const val CURRENT_USER_TABLE_NAME = "current_user"
    }

    lateinit var currentDb : SQLiteDatabase

    override fun onCreate(db: SQLiteDatabase) {

        var createTable = ("CREATE TABLE IF NOT EXISTS $ROLE_TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_NAME TEXT )")
        db.execSQL(createTable)


        createTable = ("CREATE TABLE $PROFILE_TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_NAME TEXT, "
                + "$COLUMN_EMAIL TEXT, "
                + "$COLUMN_PICTURE TEXT, "
                + "$COLUMN_ROLE int, "
                + "FOREIGN KEY($COLUMN_ROLE) REFERENCES $ROLE_TABLE_NAME($COLUMN_ID) )")
        db.execSQL(createTable)

        createTable = ("CREATE TABLE $CURRENT_USER_TABLE_NAME ("
                + "$COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "FOREIGN KEY($COLUMN_USER_ID) REFERENCES $PROFILE_TABLE_NAME($COLUMN_ID) )")
        db.execSQL(createTable)





        insertTemplateDate(db)
    }

    private fun roleInsertNeeded(db: SQLiteDatabase): Boolean {
        val count = DatabaseUtils.queryNumEntries(db, ROLE_TABLE_NAME)
        return count <= 0
    }

    private fun insertTemplateDate(db: SQLiteDatabase) {
        if (roleInsertNeeded(db)) {
            val insertStatement =
                "INSERT INTO $ROLE_TABLE_NAME ($COLUMN_NAME) VALUES ('student'), ('supervisor') "
            db.execSQL(insertStatement)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $PROFILE_TABLE_NAME")
        onCreate(db)
    }

    fun removeCurrentUser(){
        val db = this.writableDatabase
        val deleteStatement = "DELETE FROM $CURRENT_USER_TABLE_NAME "
        db.execSQL(deleteStatement)
        db.close()
    }

    fun setCurrentUser(profile : UserProfile){
        val db = this.writableDatabase
        val insertStatement = "INSERT INTO $CURRENT_USER_TABLE_NAME ($COLUMN_USER_ID) VALUES (${profile.userId}) "
        db.execSQL(insertStatement)
        db.close()
    }

    fun getCurrentUser () : UserProfile {
        val db = this.readableDatabase
        //val projection = arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_PICTURE, COLUMN_ROLE)
        val selectStatement = "SELECT * FROM $PROFILE_TABLE_NAME where $COLUMN_ID = (SELECT $COLUMN_USER_ID FROM $CURRENT_USER_TABLE_NAME )"

        val cursor: Cursor = db.rawQuery(selectStatement, null)

        cursor.moveToNext()

        val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
        val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
        val picture = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PICTURE))
        val roleId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ROLE))

        val userProfile = UserProfile(id, name, email, roleId)
        userProfile.picture = picture

        cursor.close()

        return userProfile
    }

    fun insertUser( userProfile: UserProfile) {


        val db = this.writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_NAME, userProfile.userName)
            put(COLUMN_EMAIL, userProfile.userEmail )
            put(COLUMN_PICTURE, userProfile.picture)
            put(COLUMN_ROLE, userProfile.userType.ordinal)
        }

        db.insert(PROFILE_TABLE_NAME, null, values)
        db.close()
    }

    fun userExists(email : String) : Boolean{

        val count = DatabaseUtils.queryNumEntries(this.writableDatabase, PROFILE_TABLE_NAME, "$COLUMN_EMAIL = '$email'")
        return count > 0
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