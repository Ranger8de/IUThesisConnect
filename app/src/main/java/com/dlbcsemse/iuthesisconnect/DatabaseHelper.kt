package com.dlbcsemse.iuthesisconnect

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // Konstanten für Datenbank- und Tabellennamen sowie Spaltennamen
        private const val DATABASE_NAME = "MyMultiUserAppDatabase.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_GENERAL = "general_data"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_CLASS_NAME = "class_name"
        private const val COLUMN_KEY = "key"
        private const val COLUMN_VALUE = "value"

        // Singleton-Instanz
        @Volatile
        private var instance: DatabaseHelper? = null

        // Methode zum Abrufen der Singleton-Instanz
        fun getInstance(context: Context): DatabaseHelper {
            return instance ?: synchronized(this) {
                instance ?: DatabaseHelper(context.applicationContext).also { instance = it }
            }
        }
    }

    // Erstellt die Datenbanktabelle
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_GENERAL_TABLE = """
            CREATE TABLE $TABLE_GENERAL (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_ID TEXT,
                $COLUMN_CLASS_NAME TEXT,
                $COLUMN_KEY TEXT,
                $COLUMN_VALUE TEXT,
                UNIQUE($COLUMN_USER_ID, $COLUMN_CLASS_NAME, $COLUMN_KEY) ON CONFLICT REPLACE
            )
        """.trimIndent()
        db.execSQL(CREATE_GENERAL_TABLE)
    }

    // Aktualisiert die Datenbankstruktur bei Versionswechsel
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GENERAL")
        onCreate(db)
    }

    // Speichert Daten in der Datenbank
    fun saveData(userId: String, className: String, key: String, value: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_ID, userId)
            put(COLUMN_CLASS_NAME, className)
            put(COLUMN_KEY, key)
            put(COLUMN_VALUE, value)
        }
        db.insertWithOnConflict(TABLE_GENERAL, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    // Ruft Daten aus der Datenbank ab
    fun getData(userId: String, className: String, key: String): String? {
        val db = this.readableDatabase
        val selection = "$COLUMN_USER_ID = ? AND $COLUMN_CLASS_NAME = ? AND $COLUMN_KEY = ?"
        val selectionArgs = arrayOf(userId, className, key)
        val cursor = db.query(TABLE_GENERAL, arrayOf(COLUMN_VALUE), selection, selectionArgs, null, null, null)

        return cursor.use {
            if (it.moveToFirst()) it.getString(it.getColumnIndex(COLUMN_VALUE)) else null
        }
    }

    // Ruft alle Daten für einen bestimmten Benutzer ab
    fun getAllDataForUser(userId: String): Map<String, Map<String, String>> {
        val allData = mutableMapOf<String, MutableMap<String, String>>()
        val db = this.readableDatabase
        val selection = "$COLUMN_USER_ID = ?"
        val selectionArgs = arrayOf(userId)
        val cursor = db.query(TABLE_GENERAL, null, selection, selectionArgs, null, null, null)

        cursor.use {
            while (it.moveToNext()) {
                val className = it.getString(it.getColumnIndex(COLUMN_CLASS_NAME))
                val key = it.getString(it.getColumnIndex(COLUMN_KEY))
                val value = it.getString(it.getColumnIndex(COLUMN_VALUE))

                allData.getOrPut(className) { mutableMapOf() }[key] = value
            }
        }

        return allData
    }
}