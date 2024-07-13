package com.dlbcsemse.iuthesisconnect.helper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.DatabaseUtils
import com.dlbcsemse.iuthesisconnect.model.UserProfile
import com.dlbcsemse.iuthesisconnect.model.ThesisProfile

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        // Datenbank-Metadaten
        private const val DATABASE_NAME = "iuThesisConnect.db"
        private const val DATABASE_VERSION = 1

        // Tabellennamen
        private const val PROFILE_TABLE_NAME = "profile"
        private const val ROLE_TABLE_NAME = "role"
        private const val CURRENT_USER_TABLE_NAME = "current_user"
        const val THESIS_TABLE_NAME = "thesis"

        // Gemeinsame Spaltennamen
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_ROLE = "role"

        // Spezifische Spaltennamen für Thesis-Tabelle
        const val COLUMN_STATE = "state"
        const val COLUMN_SUPERVISOR = "supervisor"
        const val COLUMN_SECOND_SUPERVISOR = "second_supervisor"
        const val COLUMN_THEME = "theme"
        const val COLUMN_STUDENT = "student"
        const val COLUMN_DUE_DATE_DAY = "due_date_day"
        const val COLUMN_DUE_DATE_MONTH = "due_date_month"
        const val COLUMN_DUE_DATE_YEAR = "due_date_year"
        const val COLUMN_BILL = "bill"
        const val COLUMN_BILL_STATE = "bill_state"
        const val COLUMN_USER_TYPE = "user_type"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Erstellen der Role-Tabelle
        val createRoleTable = """
            CREATE TABLE $ROLE_TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT
            )
        """.trimIndent()
        db.execSQL(createRoleTable)

        // Erstellen der Profile-Tabelle
        val createProfileTable = """
            CREATE TABLE $PROFILE_TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_EMAIL TEXT,
                $COLUMN_ROLE INTEGER,
                FOREIGN KEY($COLUMN_ROLE) REFERENCES $ROLE_TABLE_NAME($COLUMN_ID)
            )
        """.trimIndent()
        db.execSQL(createProfileTable)

        // Erstellen der Current User-Tabelle
        val createCurrentUserTable = """
            CREATE TABLE $CURRENT_USER_TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY,
                FOREIGN KEY($COLUMN_ID) REFERENCES $PROFILE_TABLE_NAME($COLUMN_ID)
            )
        """.trimIndent()
        db.execSQL(createCurrentUserTable)

        // Erstellen der Thesis-Tabelle
        val createThesisTable = """
            CREATE TABLE $THESIS_TABLE_NAME (
                $COLUMN_STUDENT TEXT PRIMARY KEY,
                $COLUMN_STATE TEXT,
                $COLUMN_SUPERVISOR TEXT,
                $COLUMN_SECOND_SUPERVISOR TEXT,
                $COLUMN_THEME TEXT,
                $COLUMN_DUE_DATE_DAY INTEGER,
                $COLUMN_DUE_DATE_MONTH INTEGER,
                $COLUMN_DUE_DATE_YEAR INTEGER,
                $COLUMN_BILL TEXT,
                $COLUMN_BILL_STATE TEXT,
                $COLUMN_USER_TYPE INTEGER,
                FOREIGN KEY($COLUMN_STUDENT) REFERENCES $PROFILE_TABLE_NAME($COLUMN_NAME),
                FOREIGN KEY($COLUMN_SUPERVISOR) REFERENCES $PROFILE_TABLE_NAME($COLUMN_NAME),
                FOREIGN KEY($COLUMN_SECOND_SUPERVISOR) REFERENCES $PROFILE_TABLE_NAME($COLUMN_NAME)
            )
        """.trimIndent()
        db.execSQL(createThesisTable)

        // Einfügen von Beispieldaten
        insertTemplateData(db)
    }

    private fun insertTemplateData(db: SQLiteDatabase) {
        // Einfügen von Rollen, wenn die Tabelle leer ist
        if (DatabaseUtils.queryNumEntries(db, ROLE_TABLE_NAME) == 0L) {
            val insertRoles = """
            INSERT INTO $ROLE_TABLE_NAME ($COLUMN_NAME) 
            VALUES ('student'), ('supervisor')
        """.trimIndent()
            db.execSQL(insertRoles)
        }

        // Überprüfen, ob bereits Benutzer in der Datenbank vorhanden sind
        if (DatabaseUtils.queryNumEntries(db, PROFILE_TABLE_NAME) == 0L) {
            // Einfügen von Beispiel-Benutzern
            val users = listOf(
                Triple("student", "student@iu.com", 1),
                Triple("supervisor", "supervisor@iu.com", 2)
            )

            for ((name, email, roleId) in users) {
                val userValues = ContentValues().apply {
                    put(COLUMN_NAME, name)
                    put(COLUMN_EMAIL, email)
                    put(COLUMN_ROLE, roleId)
                }
                val userId = db.insert(PROFILE_TABLE_NAME, null, userValues)

                // Wenn der Benutzer ein Student ist, füge eine Thesis hinzu
                if (roleId == 1) {
                    val thesisValues = ContentValues().apply {
                        put(COLUMN_STUDENT, name)
                        put(COLUMN_STATE, "Nicht begonnen")
                        put(COLUMN_SUPERVISOR, "Noch nicht zugewiesen")
                        put(COLUMN_SECOND_SUPERVISOR, "Noch nicht zugewiesen")
                        put(COLUMN_THEME, "Noch nicht festgelegt")
                        put(COLUMN_DUE_DATE_DAY, 1)
                        put(COLUMN_DUE_DATE_MONTH, 1)
                        put(COLUMN_DUE_DATE_YEAR, 2024)
                        put(COLUMN_BILL, "")
                        put(COLUMN_BILL_STATE, "Nicht gestellt")
                        put(COLUMN_USER_TYPE, 1) // student
                    }
                    db.insert(THESIS_TABLE_NAME, null, thesisValues)
                }
            }
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Bei einem Upgrade werden alle Tabellen gelöscht und neu erstellt
        db.execSQL("DROP TABLE IF EXISTS $PROFILE_TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $THESIS_TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $ROLE_TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $CURRENT_USER_TABLE_NAME")
        onCreate(db)
    }

    // Entfernt den aktuellen Benutzer
    fun removeCurrentUser() {
        val db = this.writableDatabase
        db.delete(CURRENT_USER_TABLE_NAME, null, null)
        db.close()
    }

    // Setzt den aktuellen Benutzer
    fun setCurrentUser(profile: UserProfile) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID, profile.userId)
        }
        db.insert(CURRENT_USER_TABLE_NAME, null, values)
        db.close()
    }

    // Holt den aktuellen Benutzer
    fun getCurrentUser(): UserProfile {
        val db = this.readableDatabase
        val selectQuery = """
        SELECT p.* FROM $PROFILE_TABLE_NAME p
        JOIN $CURRENT_USER_TABLE_NAME c ON p.$COLUMN_ID = c.$COLUMN_ID
    """.trimIndent()

        val cursor = db.rawQuery(selectQuery, null)
        return cursor.use {
            if (it.moveToFirst()) {
                val id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID))
                val name = it.getString(it.getColumnIndexOrThrow(COLUMN_NAME))
                val email = it.getString(it.getColumnIndexOrThrow(COLUMN_EMAIL))
                val roleId = it.getInt(it.getColumnIndexOrThrow(COLUMN_ROLE))
                UserProfile(id, name, email, roleId)
            } else {
                throw NoSuchElementException("No current user found")
            }
        }
    }

    // Fügt einen neuen Benutzer hinzu
    fun insertUser(userProfile: UserProfile) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, userProfile.userName)
            put(COLUMN_EMAIL, userProfile.userEmail)
            put(COLUMN_ROLE, userProfile.userType.ordinal)
        }
        db.insert(PROFILE_TABLE_NAME, null, values)
        db.close()
    }

    // Holt eine Thesis anhand des Studentennamens
    fun getThesisByStudent(studentName: String): ThesisProfile? {
        val db = this.readableDatabase
        val cursor = db.query(
            THESIS_TABLE_NAME,
            null,
            "$COLUMN_STUDENT = ?",
            arrayOf(studentName),
            null,
            null,
            null
        )

        var thesis: ThesisProfile? = null
        if (cursor.moveToFirst()) {
            thesis = ThesisProfile(
                state = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATE)),
                supervisor = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        COLUMN_SUPERVISOR
                    )
                ),
                secondSupervisor = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        COLUMN_SECOND_SUPERVISOR
                    )
                ),
                theme = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_THEME)),
                student = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT)),
                dueDateDay = cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                        COLUMN_DUE_DATE_DAY
                    )
                ),
                dueDateMonth = cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                        COLUMN_DUE_DATE_MONTH
                    )
                ),
                dueDateYear = cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                        COLUMN_DUE_DATE_YEAR
                    )
                ),
                bill = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BILL)),
                billState = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        COLUMN_BILL_STATE
                    )
                ),
                userType = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_TYPE))
            )
        }
        cursor.close()
        return thesis
    }

    // Aktualisiert eine bestehende Thesis
    fun updateThesis(thesis: ThesisProfile): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_STATE, thesis.state)
            put(COLUMN_SUPERVISOR, thesis.supervisor)
            put(COLUMN_SECOND_SUPERVISOR, thesis.secondSupervisor)
            put(COLUMN_THEME, thesis.theme)
            put(COLUMN_DUE_DATE_DAY, thesis.dueDateDay)
            put(COLUMN_DUE_DATE_MONTH, thesis.dueDateMonth)
            put(COLUMN_DUE_DATE_YEAR, thesis.dueDateYear)
            put(COLUMN_BILL, thesis.bill)
            put(COLUMN_BILL_STATE, thesis.billState)
            put(COLUMN_USER_TYPE, thesis.userType)
        }

        return db.update(
            THESIS_TABLE_NAME,
            values,
            "$COLUMN_STUDENT = ?",
            arrayOf(thesis.student)
        )
    }

    // Prüft, ob ein Benutzer existiert
    fun userExists(email: String): Boolean {
        return try {
            val db = this.readableDatabase
            val count = DatabaseUtils.queryNumEntries(
                db,
                PROFILE_TABLE_NAME,
                "$COLUMN_EMAIL = ?",
                arrayOf(email)
            )
            count > 0
        } catch (e: Exception) {
            false
        }
    }
}