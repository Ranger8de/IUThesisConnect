package com.dlbcsemse.iuthesisconnect.helper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.dlbcsemse.iuthesisconnect.model.DashboardUserType
import com.dlbcsemse.iuthesisconnect.model.Thesis
import com.dlbcsemse.iuthesisconnect.model.UserProfile

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        // Datenbank-Metadaten
        private const val DATABASE_NAME = "iuThesisConnect.db"
        private const val DATABASE_VERSION = 1

        // Tabellennamen
        private const val PROFILE_TABLE_NAME = "profile"
        private const val ROLE_TABLE_NAME = "role"
        private const val CURRENT_USER_TABLE_NAME = "current_user"
        private const val THESIS_TABLE_NAME = "thesis"
        private const val LANGUAGES_TABLE_NAME = "languages"
        private const val TOPICCATEGORIES_TABLE_NAME = "topic_categories"
        private const val SUPERVISORPROFILE_TABLE_NAME = "supervisor_profile"

        // Gemeinsame Spaltennamen
        private const val COLUMN_ID = "id"
        private const val COLUMN_USER_ID = "user_id"
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

    // Erstellen der Datenbanktabellen
    override fun onCreate(db: SQLiteDatabase) {
        // Erstellen der Role-Tabelle
        var createTable = ("CREATE TABLE IF NOT EXISTS $ROLE_TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_NAME TEXT )")
        db.execSQL(createTable)


        // Erstellen der Profile-Tabelle
        createTable = ("CREATE TABLE $PROFILE_TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_NAME TEXT, "
                + "$COLUMN_EMAIL TEXT, "
                + "$COLUMN_ROLE int, "
                + "FOREIGN KEY($COLUMN_ROLE) REFERENCES $ROLE_TABLE_NAME($COLUMN_ID) )")
        db.execSQL(createTable)


        createTable = ("CREATE TABLE $CURRENT_USER_TABLE_NAME ("
                + "$COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "FOREIGN KEY($COLUMN_USER_ID) REFERENCES $PROFILE_TABLE_NAME($COLUMN_ID) )")
        db.execSQL(createTable)

        createTable = ("CREATE TABLE $LANGUAGES_TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_NAME TEXT )")
        db.execSQL(createTable)

        // Erstellen der Topic Categories-Tabelle
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

        // Erstellen der Thesis-Tabelle
        createTable = ("CREATE TABLE $THESIS_TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY, "
                + "$COLUMN_STUDENT INTEGER , "
                + "$COLUMN_STATE TEXT, "
                + "$COLUMN_SUPERVISOR INTEGER , "
                + "$COLUMN_SECOND_SUPERVISOR INTEGER, "
                + "$COLUMN_THEME TEXT, "
                + "$COLUMN_DUE_DATE_DAY INTEGER, "
                + "$COLUMN_DUE_DATE_MONTH INTEGER, "
                + "$COLUMN_DUE_DATE_YEAR INTEGER, "
                + "$COLUMN_BILL_STATE TEXT, "
                + "$COLUMN_USER_TYPE INTEGER, "
                + "FOREIGN KEY($COLUMN_STUDENT) REFERENCES $PROFILE_TABLE_NAME($COLUMN_ID), "
                + "FOREIGN KEY($COLUMN_SUPERVISOR) REFERENCES $PROFILE_TABLE_NAME($COLUMN_ID), "
                + "FOREIGN KEY($COLUMN_SECOND_SUPERVISOR) REFERENCES $PROFILE_TABLE_NAME($COLUMN_ID) ) " )
        db.execSQL(createTable)


        insertTemplateDate(db)

    }

    // Überprüfen, ob Rollen eingefügt werden müssen
    private fun roleInsertNeeded(db: SQLiteDatabase): Boolean {
        val count = DatabaseUtils.queryNumEntries(db, ROLE_TABLE_NAME)
        return count <= 0
    }

    // Einfügen von Beispieldaten
    private fun insertTemplateDate(db: SQLiteDatabase) {
        if (roleInsertNeeded(db)) {
            // Einfügen von Rollen
            var insertStatement =
                "INSERT INTO $ROLE_TABLE_NAME ($COLUMN_NAME) VALUES ('student'), ('supervisor') "
            db.execSQL(insertStatement)

            // Einfügen von Sprachen
            insertStatement =
                "INSERT INTO $LANGUAGES_TABLE_NAME ($COLUMN_NAME) VALUES ('German'), ('English') "
            db.execSQL(insertStatement)

            // Einfügen von Themen
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

    // Aktualisieren der Datenbank
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

    // Entfernt den aktuellen Benutzer
    fun removeCurrentUser(){
        val db = this.writableDatabase
        val deleteStatement = "DELETE FROM $CURRENT_USER_TABLE_NAME "
        db.execSQL(deleteStatement)
        db.close()
    }

    // Setzt den aktuellen Benutzer
    fun setCurrentUser(profile : UserProfile){
        val db = this.writableDatabase
        val insertStatement = "INSERT INTO $CURRENT_USER_TABLE_NAME ($COLUMN_USER_ID) VALUES (${profile.userId}) "
        db.execSQL(insertStatement)
        db.close()
    }

    // Holt den aktuellen Benutzer
    fun getCurrentUser () : UserProfile {
        val db = this.readableDatabase
        val selectStatement = "SELECT * FROM $PROFILE_TABLE_NAME where $COLUMN_ID = (SELECT $COLUMN_USER_ID FROM $CURRENT_USER_TABLE_NAME )"

        val cursor: Cursor = db.rawQuery(selectStatement, null)

        cursor.moveToNext()

        val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
        val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
        val roleId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ROLE))

        val userProfile = UserProfile(id, name, email, roleId)

        cursor.close()

        return userProfile
    }

    // Holt Benutzer anhand des Benutzernamens
    fun getUser(userName: String): UserProfile? {
        val db = this.readableDatabase
        val selectStatement = "SELECT * FROM $PROFILE_TABLE_NAME WHERE $COLUMN_NAME = ?"
        val cursor: Cursor = db.rawQuery(selectStatement, arrayOf(userName))

        var userProfile: UserProfile? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
            val roleId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ROLE))

            userProfile = UserProfile(id, name, email, roleId)
        }
        cursor.close()
        return userProfile
    }
    // Fügt einen neuen Nutzer hinzu
    // Fügt einen neuen Nutzer hinzu
    fun insertUser( userProfile: UserProfile) {
        val db = this.writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_NAME, userProfile.userName)
            put(COLUMN_EMAIL, userProfile.userEmail )
            put(COLUMN_ROLE, userProfile.userType.ordinal)
        }

        db.insert(PROFILE_TABLE_NAME, null, values)
        db.close()
    }

    // Prüft ob ein Benutzer existiert
    fun userExists(email : String) : Boolean{
        val count = DatabaseUtils.queryNumEntries(this.writableDatabase, PROFILE_TABLE_NAME, "$COLUMN_EMAIL = '$email'")
        return count > 0
    }

    // Holt die Fachrichtungen der Betreuer
    fun getAllSpecialisations() : Array<String>{
        val specialisations = ArrayList<String>()
        val selectStatement = "SELECT * FROM $TOPICCATEGORIES_TABLE_NAME "

        val cursor: Cursor = this.readableDatabase.rawQuery(selectStatement, null)
        with(cursor) {
            while (moveToNext()) {
                specialisations.add(getString(getColumnIndexOrThrow(COLUMN_NAME)))
            }
        }
        cursor.close()

        return specialisations.toArray() as Array<String>
    }

    // Holt den Datenbankpfad
    fun getDatabasePath(context: Context): String {
        return context.getDatabasePath(DATABASE_NAME).absolutePath
    }

    // Holt eine Thesis anhand des Studentennamens
    fun getThesisByStudent(studentName: String): Thesis? {
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

        var thesis: Thesis? = null
        if (cursor.moveToFirst()) {
            thesis = Thesis(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                state = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATE)),
                supervisor = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SUPERVISOR)),
                secondSupervisor = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SECOND_SUPERVISOR)),
                theme = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_THEME)),
                student = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT)),
                dueDateDay = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE_DAY)),
                dueDateMonth = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE_MONTH)),
                dueDateYear = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE_YEAR)),
                billState = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BILL_STATE)),
                userType = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_TYPE))
            )
        }
        cursor.close()
        return thesis
    }

    // Aktualisiert die Thesis
    fun updateThesis(thesis: Thesis): Int {

        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_STATE, thesis.state)
            put(COLUMN_SUPERVISOR, thesis.supervisor)
            put(COLUMN_SECOND_SUPERVISOR, thesis.secondSupervisor)
            put(COLUMN_THEME, thesis.theme)
            put(COLUMN_DUE_DATE_DAY, thesis.dueDateDay)
            put(COLUMN_DUE_DATE_MONTH, thesis.dueDateMonth)
            put(COLUMN_DUE_DATE_YEAR, thesis.dueDateYear)
        }

        return db.update(
            THESIS_TABLE_NAME,
            values,
            "$COLUMN_ID = ?",
            arrayOf(thesis.id.toString())
        )
    }
    fun insertThesis(thesis: Thesis): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_STATE, thesis.state)
            put(COLUMN_SUPERVISOR, thesis.supervisor)
            put(COLUMN_SECOND_SUPERVISOR, thesis.secondSupervisor)
            put(COLUMN_THEME, thesis.theme)
            put(COLUMN_STUDENT, thesis.student)
            put(COLUMN_DUE_DATE_DAY, thesis.dueDateDay)
            put(COLUMN_DUE_DATE_MONTH, thesis.dueDateMonth)
            put(COLUMN_DUE_DATE_YEAR, thesis.dueDateYear)
            put(COLUMN_BILL_STATE, thesis.billState)
            put(COLUMN_USER_TYPE, thesis.userType)
        }
        return db.insert(THESIS_TABLE_NAME, null, values)
    }

    // Fügt eine neue Thesis ein oder aktualisiert eine bestehende
    fun insertOrUpdateThesis(thesis: Thesis): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_STATE, thesis.state)
            put(COLUMN_SUPERVISOR, thesis.supervisor)
            put(COLUMN_SECOND_SUPERVISOR, thesis.secondSupervisor)
            put(COLUMN_THEME, thesis.theme)
            put(COLUMN_STUDENT, thesis.student)
            put(COLUMN_DUE_DATE_DAY, thesis.dueDateDay)
            put(COLUMN_DUE_DATE_MONTH, thesis.dueDateMonth)
            put(COLUMN_DUE_DATE_YEAR, thesis.dueDateYear)
            put(COLUMN_BILL_STATE, thesis.billState)
            put(COLUMN_USER_TYPE, thesis.userType)
        }

        return if (thesis.id > 0) {
            db.update(THESIS_TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(thesis.id.toString())).toLong()
        } else {
            db.insert(THESIS_TABLE_NAME, null, values)
        }
    }


    // ============================ NEUE FUNKTIONEN MARC ============================


    fun getAllUserIds(): List<Long> {
        val ids = mutableListOf<Long>()
        val db = this.readableDatabase
        val cursor = db.query(
            PROFILE_TABLE_NAME,
            arrayOf(COLUMN_ID),
            null,
            null,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                ids.add(getLong(getColumnIndexOrThrow(COLUMN_ID)))
            }
        }
        cursor.close()
        return ids
    }

    fun insertInitialUsers() {
        val db = this.writableDatabase

        var cursor = db.query(PROFILE_TABLE_NAME, null, "$COLUMN_NAME = ?", arrayOf("student"), null, null, null,)
        if (cursor.count == 0) {
            val studentValues = ContentValues().apply {
                put(COLUMN_NAME, "student")
                put(COLUMN_EMAIL, "student@iu.org")
                put(COLUMN_ROLE, DashboardUserType.student.ordinal)
            }
            db.insert(PROFILE_TABLE_NAME, null, studentValues)
        }
        cursor.close()

        cursor = db.query(PROFILE_TABLE_NAME, null, "$COLUMN_NAME = ?", arrayOf("supervisor"), null, null, null)
        if (cursor.count == 0) {
            val supervisorValues = ContentValues().apply {
                put(COLUMN_NAME, "supervisor")
                put(COLUMN_EMAIL, "supervisor@iu.org")
                put(COLUMN_ROLE, DashboardUserType.supervisor.ordinal)
            }
            db.insert(PROFILE_TABLE_NAME, null, supervisorValues)
        }
        cursor.close()
    }

    fun getAllSupervisors(): List<UserProfile> {
        val supervisors = mutableListOf<UserProfile>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $PROFILE_TABLE_NAME WHERE $COLUMN_ROLE = ?"
        val cursor = db.rawQuery(query, arrayOf(DashboardUserType.supervisor.ordinal.toString()))

        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(COLUMN_ID))
                val name = getString(getColumnIndexOrThrow(COLUMN_NAME))
                val email = getString(getColumnIndexOrThrow(COLUMN_EMAIL))
                val type = getInt(getColumnIndexOrThrow(COLUMN_ROLE))
                supervisors.add(UserProfile(id, name, email, type))
            }
        }
        cursor.close()
        return supervisors
    }

    fun createThesisForStudent(studentId: Long) {
        if (studentHasThesis(studentId)) {
            return
        }

        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_STUDENT, studentId)
            put(COLUMN_STATE, "Nicht begonnen")
            put(COLUMN_SUPERVISOR, -1)
            put(COLUMN_SECOND_SUPERVISOR, -1)
            put(COLUMN_THEME, "Nich gesetzt")
            put(COLUMN_DUE_DATE_DAY, 0)
            put(COLUMN_DUE_DATE_MONTH, 0)
            put(COLUMN_DUE_DATE_YEAR, 0)
            put(COLUMN_BILL_STATE, "Nicht gestellt")
            put(COLUMN_USER_TYPE, DashboardUserType.student.ordinal)
        }
        db.insert(THESIS_TABLE_NAME, null, values)
    }

    fun ensureAllStudentsHaveThesis() {
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_ID FROM $PROFILE_TABLE_NAME WHERE $COLUMN_ROLE = ?"
        val cursor = db.rawQuery(query, arrayOf(DashboardUserType.student.ordinal.toString()))

        cursor.use { cursor ->
            while (cursor.moveToNext()) {
                val studentId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                if (!studentHasThesis(studentId)) {
                    createThesisForStudent(studentId)
                }
            }
        }
    }

    fun studentHasThesis(studentId: Long): Boolean {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM $THESIS_TABLE_NAME WHERE $COLUMN_STUDENT = ?"
        val cursor = db.rawQuery(query, arrayOf(studentId.toString()))

        cursor.use { cursor ->
            if (cursor.moveToFirst()) {
                return cursor.getInt(0) > 0
            }
        }
        return false
    }
}