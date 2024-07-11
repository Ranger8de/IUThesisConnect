package com.dlbcsemse.iuthesisconnect.helper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
<<<<<<< .mine
import android.database.DatabaseUtils
=======

>>>>>>> .theirs
import com.dlbcsemse.iuthesisconnect.model.UserProfile
import com.dlbcsemse.iuthesisconnect.model.ThesisProfile

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        // Datenbank-Metadaten
        private const val DATABASE_NAME = "iuThesisConnect.db"
        private const val DATABASE_VERSION = 1

        // Tabellennamen
        private const val ROLE_TABLE_NAME = "role"
        private const val CURRENT_USER_TABLE_NAME = "current_user"
        private const val LANGUAGES_TABLE_NAME = "languages"
        private const val TOPICCATEGORIES_TABLE_NAME = "topic_categories"
        private const val SUPERVISORPROFILE_TABLE_NAME = "supervisor_profile"
		const val THESIS_TABLE_NAME = "thesis"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_ROLE = "role"
        private const val COLUMN_STATUS = "status"
        private const val COLUMN_BIO = "biography"
        private const val COLUMN_RESEARCH_TOPICS = "research_topics"

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
        



        db.execSQL(createTable)


        // Erstellen der Thesis-Tabelle
        createTable = """
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
        db.execSQL(createTable)

        createTable = ("CREATE TABLE $LANGUAGES_TABLE_NAME ("
                        + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "$COLUMN_NAME TEXT )")
        db.execSQL(createTable)

        createTable = ("CREATE TABLE $TOPICCATEGORIES_TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_NAME TEXT )")
        db.execSQL(createTable)

        createTable = ("CREATE TABLE $SUPERVISORPROFILE_TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_USER_ID INTEGER, "
                + "$COLUMN_STATUS INTEGER,  "
                + "$COLUMN_BIO TEXT, "
                + "$COLUMN_RESEARCH_TOPICS TEXT, "
                + "FOREIGN KEY($COLUMN_USER_ID) REFERENCES $PROFILE_TABLE_NAME($COLUMN_ID))")
        db.execSQL(createTable)


        insertTemplateDate(db)
    }

            var insertStatement =

            insertStatement =
                "INSERT INTO $LANGUAGES_TABLE_NAME ($COLUMN_NAME) VALUES ('German'), ('English') "
            db.execSQL(insertStatement)

            insertStatement =
                ("INSERT INTO $TOPICCATEGORIES_TABLE_NAME ($COLUMN_NAME) " +
                        "VALUES ('Real Estate'), ('Architecture'), ('Industry & Construction') , " +
                        "('Design & Media'), ('Education & Psychology'), ('Social Affairs'), " +
                        "('Health & Nursing'), ('IT & Software Development'), ('Engineering'), " +
                        "('Data Science & Artificial Intelligence'), ('Human Resources & Law'), " +
                        "('Marketing & Communication'), ('Tourism, Hospitality & Event'), " +
                        "('Business Administration & Management'), ('Finance & Tax Accounting'), " +
                            "('Planning & Controlling'), ('Methods'), ('Project Management'), " +
                        " ('Languages'), ('Economics')")
            db.execSQL(insertStatement)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Bei einem Upgrade werden alle Tabellen gelöscht und neu erstellt
        db.execSQL("DROP TABLE IF EXISTS $PROFILE_TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $THESIS_TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $ROLE_TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $CURRENT_USER_TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $LANGUAGES_TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TOPICCATEGORIES_TABLE_NAME")
        onCreate(db)
    }

    fun removeCurrentUser() {
        val db = this.writableDatabase
        db.execSQL(deleteStatement)
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

<<<<<<< .mine
    // Fügt einen neuen Benutzer hinzu
    fun insertUser(userProfile: UserProfile) {























=======
    fun getUser (userName : String) : UserProfile {
        val db = this.readableDatabase
        val selectStatement = "SELECT * FROM $PROFILE_TABLE_NAME where $COLUMN_NAME = '$userName'"

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


>>>>>>> .theirs
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
                supervisor = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUPERVISOR)),
                secondSupervisor = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SECOND_SUPERVISOR)),
                theme = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_THEME)),
                student = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT)),
                dueDateDay = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE_DAY)),
                dueDateMonth = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE_MONTH)),
                dueDateYear = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE_YEAR)),
                bill = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BILL)),
                billState = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BILL_STATE)),
                userType = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_TYPE))
            )
        }
        cursor.close()
        return thesis
    }

<<<<<<< .mine
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



=======
    fun getAllSpecialisations() : Array<String>{

        val cursor: Cursor = this.readableDatabase.query(TOPICCATEGORIES_TABLE_NAME, null, null, null, null,null,null)
        val specialisations = Array<String>(cursor.count){""}
        var cursorIndex = 0
        while (cursor.moveToNext()) {
            specialisations[cursorIndex] = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            cursorIndex++
        }

        cursor.close()
        return specialisations
    }


    fun readData(context: Context): List<String> {
        val dbHelper = DatabaseHelper(context)
        val db = dbHelper.readableDatabase
>>>>>>> .theirs

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




    fun getDatabasePath(context: Context): String {
        return context.getDatabasePath(DATABASE_NAME).absolutePath
    }
}