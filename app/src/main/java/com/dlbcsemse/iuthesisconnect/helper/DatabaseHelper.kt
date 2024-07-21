package com.dlbcsemse.iuthesisconnect.helper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.dlbcsemse.iuthesisconnect.model.DashboardUserType
import com.dlbcsemse.iuthesisconnect.model.Thesis
import com.dlbcsemse.iuthesisconnect.model.AvailabilityStatus
import com.dlbcsemse.iuthesisconnect.model.Chat
import com.dlbcsemse.iuthesisconnect.model.ChatMessage
import com.dlbcsemse.iuthesisconnect.model.SupervisorProfile
import com.dlbcsemse.iuthesisconnect.model.UserProfile
//import com.dlbcsemse.iuthesisconnect.model.ThesisProfile

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DEFAULT_BILL_STATE = "Nicht gestellt"
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
        private const val TOPIC_SUPERVISOR_TABLE_NAME = "topic_supervisor"
        private const val CHAT_HEADER_TABLE_NAME  = "chat"
        private const val CHAT_MESSAGES_TABLE_NAME  = "chat_messages"


        // Gemeinsame Spaltennamen
        private const val COLUMN_ID = "id"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_ROLE = "role"
        private const val COLUMN_STATUS = "status"
        private const val COLUMN_BIO = "biography"
        private const val COLUMN_RESEARCH_TOPICS = "research_topics"
        private const val COLUMN_PICTURE = "picture"
        private const val COLUMN_LANGUAGES = "languages"
        private const val COLUMN_TOPICCATEGORY_ID = "topic_id"
        private const val COLUMN_CHAT_USER1 = "user1"
        private const val COLUMN_CHAT_USER2 = "user2"
        private const val COLUMN_CHAT_TOUSER = "to_user"
        private const val COLUMN_MESSAGE = "message"
        private const val COLUMN_TIMESTAMP = "date_time"
        private const val COLUMN_CHAT_ID = "chat_id"
        private const val COLUMN_READED = "readed"
        private const val COLUMN_ATTACHMENT = "attachment"
        private const val COLUMN_ATT_NAME = "att_name"

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
                + "$COLUMN_USER_ID INTEGER UNIQUE, "
                + "$COLUMN_STATUS INTEGER,  "
                + "$COLUMN_BIO TEXT, "
                + "$COLUMN_RESEARCH_TOPICS TEXT, "
                + "$COLUMN_LANGUAGES TEXT, "
                + "FOREIGN KEY($COLUMN_USER_ID) REFERENCES $PROFILE_TABLE_NAME($COLUMN_ID))")
        db.execSQL(createTable)

        // Erstellen der Thesis-Tabelle
        createTable = ("CREATE TABLE $THESIS_TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_STUDENT INTEGER, "
                + "$COLUMN_STATE TEXT, "
                + "$COLUMN_SUPERVISOR INTEGER , "
                + "$COLUMN_SECOND_SUPERVISOR INTEGER, "
                + "$COLUMN_THEME TEXT, "
                + "$COLUMN_DUE_DATE_DAY INTEGER, "
                + "$COLUMN_DUE_DATE_MONTH INTEGER, "
                + "$COLUMN_DUE_DATE_YEAR INTEGER, "
                + "$COLUMN_BILL_STATE TEXT DEFAULT '$DEFAULT_BILL_STATE', "
                + "$COLUMN_USER_TYPE INTEGER, "
                + "FOREIGN KEY($COLUMN_STUDENT) REFERENCES $PROFILE_TABLE_NAME($COLUMN_ID), "
                + "FOREIGN KEY($COLUMN_SUPERVISOR) REFERENCES $PROFILE_TABLE_NAME($COLUMN_ID), "
                + "FOREIGN KEY($COLUMN_SECOND_SUPERVISOR) REFERENCES $PROFILE_TABLE_NAME($COLUMN_ID) "
                + "UNIQUE($COLUMN_STUDENT) ) ")
        db.execSQL(createTable)

        // Erstellen der Topic Categories-Tabelle (Zuordnung der Forschungsgebiete zu Betreuern)
        createTable = ("CREATE TABLE $TOPIC_SUPERVISOR_TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_USER_ID INTEGER, "
                + "$COLUMN_TOPICCATEGORY_ID INTEGER," +
                " FOREIGN KEY($COLUMN_USER_ID) REFERENCES $PROFILE_TABLE_NAME($COLUMN_ID), " +
                " FOREIGN KEY($COLUMN_TOPICCATEGORY_ID) REFERENCES $TOPICCATEGORIES_TABLE_NAME($COLUMN_ID))")
        db.execSQL(createTable)

        // Erstellen der Chat-Tabelle
        createTable = ("CREATE TABLE $CHAT_HEADER_TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_CHAT_USER1 INTEGER, "
                + "$COLUMN_CHAT_USER2 INTEGER, "
                + " FOREIGN KEY($COLUMN_CHAT_USER1) REFERENCES $PROFILE_TABLE_NAME($COLUMN_ID), "
                + " FOREIGN KEY($COLUMN_CHAT_USER2) REFERENCES $PROFILE_TABLE_NAME($COLUMN_ID)) ")
        db.execSQL(createTable)

        // Erstellen der Chat-Tabelle
        createTable = ("CREATE TABLE $CHAT_MESSAGES_TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_CHAT_ID INTEGER, "
                + "$COLUMN_CHAT_TOUSER INTEGER, "
                + "$COLUMN_MESSAGE TEXT, "
                + "$COLUMN_TIMESTAMP INTEGER, "
                + "$COLUMN_READED INTEGER, "
                + "$COLUMN_ATTACHMENT BLOB, "
                + "$COLUMN_ATT_NAME TEXT, "
                + "FOREIGN KEY($COLUMN_CHAT_ID) REFERENCES $CHAT_HEADER_TABLE_NAME($COLUMN_ID), "
                + "FOREIGN KEY($COLUMN_CHAT_TOUSER) REFERENCES $PROFILE_TABLE_NAME($COLUMN_ID)) ")
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
                "INSERT INTO $ROLE_TABLE_NAME ($COLUMN_ID, $COLUMN_NAME) VALUES (0, 'student'), (1, 'supervisor') "
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

            // Einfügen von Beispiel-Usern:
            insertStatement =
                ("INSERT INTO $PROFILE_TABLE_NAME ($COLUMN_NAME,  $COLUMN_EMAIL, "
                        + "$COLUMN_ROLE, $COLUMN_PICTURE)"
                        + "VALUES ('supervisor', 'supervisor@iu.org', 1, " +
                        " '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')" +
                        ", ('student', 'student@iu.org', 0, " +
                        " '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')")

            db.execSQL(insertStatement)

            // einfügen von Chats
            insertStatement =
                ("INSERT INTO $CHAT_HEADER_TABLE_NAME ($COLUMN_CHAT_USER1,  $COLUMN_CHAT_USER2) " +
                        "VALUES (1, 2)")

            db.execSQL(insertStatement)

            // einfügen von Chat-Nachrichten
            insertStatement =
                ("INSERT INTO $CHAT_MESSAGES_TABLE_NAME ($COLUMN_CHAT_ID,  $COLUMN_CHAT_TOUSER, " +
                        "$COLUMN_MESSAGE, $COLUMN_TIMESTAMP, $COLUMN_READED) " +
                        "VALUES (1, 1, 'Hello ...', 4711, 0), (1, 2, 'Hello back ...', 4712, 0)")

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
        db.execSQL("DROP TABLE IF EXISTS $TOPIC_SUPERVISOR_TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $CHAT_HEADER_TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $CHAT_MESSAGES_TABLE_NAME")
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

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
        val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
        val picture = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PICTURE))
        val roleId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ROLE))

        val userProfile = UserProfile(id, name, email, roleId)
        userProfile.picture = picture

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
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
            val picture = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PICTURE))
            val roleId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ROLE))

            userProfile = UserProfile(id, name, email, roleId)
            userProfile.picture = picture
        }
        cursor.close()
        return userProfile
    }


    // Holt Benutzer anhand der BenutzerID
    fun getUser(userID: Int): UserProfile? {
        val db = this.readableDatabase
        val selectStatement = "SELECT * FROM $PROFILE_TABLE_NAME WHERE $COLUMN_ID = ?"
        val cursor: Cursor = db.rawQuery(selectStatement, arrayOf(userID.toString()))

        var userProfile: UserProfile? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
            val picture = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PICTURE))
            val roleId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ROLE))

            userProfile = UserProfile(id, name, email, roleId)
            userProfile.picture = picture
        }
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

    // Prüft ob ein Benutzer existiert
    fun userExists(email : String) : Boolean{
        val count = DatabaseUtils.queryNumEntries(this.writableDatabase, PROFILE_TABLE_NAME, "$COLUMN_EMAIL = '$email'")
        return count > 0
    }

    // Holt die Fachrichtungen der Betreuer
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

    // Holt den Datenbankpfad
    fun getDatabasePath(context: Context): String {
        return context.getDatabasePath(DATABASE_NAME).absolutePath
    }

    // Holt eine Thesis anhand des Studentennamens
    fun getThesisByStudent(studentId: Int): Thesis? {
        val db = this.readableDatabase
        val cursor = db.query(
            THESIS_TABLE_NAME,
            null,
            "$COLUMN_STUDENT = ?",
            arrayOf(studentId.toString()),
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
            put(COLUMN_BILL_STATE, thesis.billState ?: DEFAULT_BILL_STATE)
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

    fun insertInitialUsers() { // Kann später in insertTemplateDate() überführt werden
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

    fun getAllSupervisors(): List<SupervisorProfile> {
        val supervisors = mutableListOf<SupervisorProfile>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $SUPERVISORPROFILE_TABLE_NAME WHERE 1 = ?"
        val cursor = db.rawQuery(query, arrayOf("1"))

        with(cursor) {
            while (moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
                val userProfile = getUser(userId)
                val status = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATUS))
                val biography = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIO))
                val topicCategories = getSupervisorTopicCategories(id)
                val researchFields = cursor.getString(cursor.getColumnIndexOrThrow(
                    COLUMN_RESEARCH_TOPICS))

                val languages = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LANGUAGES)).split(";").toTypedArray()

                supervisors.add(SupervisorProfile(id, userProfile!!, AvailabilityStatus.entries[status], biography, topicCategories, researchFields, languages))
            }
        }
        cursor.close()
        return supervisors
    }

    fun createThesisForStudent(studentId: Int) {
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
                val studentId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                if (!studentHasThesis(studentId)) {
                    createThesisForStudent(studentId)
                }
            }
        }
    }

    fun studentHasThesis(studentId: Int): Boolean {
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
    fun getThesesBySupervisor(supervisorId: Int): List<Thesis> {
        val theses = mutableListOf<Thesis>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $THESIS_TABLE_NAME WHERE $COLUMN_SUPERVISOR = ? OR $COLUMN_SECOND_SUPERVISOR = ?"
        val cursor = db.rawQuery(query, arrayOf(supervisorId.toString(), supervisorId.toString()))

        cursor.use { cursor ->
            while (cursor.moveToNext()) {
                val thesis = Thesis(
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
                theses.add(thesis)
            }
        }
        return theses
    }
    fun getThesisById(thesisId: Int): Thesis? {
        val db = this.readableDatabase
        val cursor = db.query(
            THESIS_TABLE_NAME,
            null,
            "$COLUMN_ID = ?",
            arrayOf(thesisId.toString()),
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
    fun getUserNameById(userId: Int): String {
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_NAME FROM $PROFILE_TABLE_NAME WHERE $COLUMN_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        var userName = "Nicht zugewiesen"
        if (cursor.moveToFirst()) {
            userName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
        }
        cursor.close()
        return userName
    }
    fun getUserIdByName(userName: String): Int {
        if (userName == "Nicht zugewiesen") return -1

        val db = this.readableDatabase
        val query = "SELECT $COLUMN_ID FROM $PROFILE_TABLE_NAME WHERE $COLUMN_NAME = ?"
        val cursor = db.rawQuery(query, arrayOf(userName))

        var userId = -1
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        }
        cursor.close()
        return userId
    }
    fun getSupervisorProfile(supervisorId: Int): SupervisorProfile {
        val selectStatement = "SELECT * FROM $SUPERVISORPROFILE_TABLE_NAME WHERE $COLUMN_ID = ?"
        val cursor: Cursor = readableDatabase.rawQuery(selectStatement, arrayOf(supervisorId.toString()))

        var supervisorProfile: SupervisorProfile = SupervisorProfile.emptySupervisorProfile()
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
            val userProfile = getUser(supervisorId)
            val status = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATUS))
            val biography = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIO))
            val topicCategories = getSupervisorTopicCategories(id)
            val researchFields = cursor.getString(cursor.getColumnIndexOrThrow(
                COLUMN_RESEARCH_TOPICS))

            val languages = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LANGUAGES)).split(";").toTypedArray()

            supervisorProfile = SupervisorProfile(id, userProfile!!, AvailabilityStatus.entries[status], biography, topicCategories, researchFields, languages)

        }
        cursor.close()
        return supervisorProfile
    }

    private fun getSupervisorTopicCategories(supervisorId: Int): Array<String> {
        val selectStatement = ("SELECT * FROM $TOPIC_SUPERVISOR_TABLE_NAME TS" +
                " JOIN $TOPICCATEGORIES_TABLE_NAME TC on TS.$COLUMN_TOPICCATEGORY_ID = TC.$COLUMN_ID" +
                " WHERE $COLUMN_USER_ID = ?")
        val cursor: Cursor = readableDatabase.rawQuery(selectStatement, arrayOf(supervisorId.toString()))

        val topicCategories = Array<String>(cursor.count){""}
        var cursorIndex = 0
        while (cursor.moveToNext()) {
            topicCategories[cursorIndex] = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            cursorIndex++
        }

        cursor.close()
        return topicCategories
    }

    fun setSupervisorProfile(supervisorProfile: SupervisorProfile) {
        if (supervisorProfile.id <= 0)
            insertSuperVisorProfile(supervisorProfile)
        else
            updateSuperVisorProfile(supervisorProfile)

    }

    private fun updateSuperVisorProfile(supervisorProfile: SupervisorProfile) {
        val updateStatement = ("UPDATE $SUPERVISORPROFILE_TABLE_NAME SET " +
                "$COLUMN_USER_ID = ${supervisorProfile.userProfile.userId}, " +
                "$COLUMN_STATUS = ${supervisorProfile.status.ordinal}, " +
                "$COLUMN_BIO = '${supervisorProfile.biography}', " +
                "$COLUMN_RESEARCH_TOPICS = '${supervisorProfile.researchTopics}', " +
                "$COLUMN_LANGUAGES = '${supervisorProfile.languages.joinToString (";")}' " +
                "WHERE $COLUMN_ID = ${supervisorProfile.id}" )


        writableDatabase.execSQL(updateStatement)
        setSupervisorTopicCategories(supervisorProfile.userProfile.userId, supervisorProfile.topicCategories)
    }

    private fun insertSuperVisorProfile(supervisorProfile: SupervisorProfile) {
        val insertStatement = ("INSERT INTO $SUPERVISORPROFILE_TABLE_NAME " +
                "($COLUMN_USER_ID, $COLUMN_STATUS, $COLUMN_BIO, $COLUMN_RESEARCH_TOPICS, $COLUMN_LANGUAGES) " +
                "VALUES (${supervisorProfile.userProfile.userId}, ${supervisorProfile.status.ordinal}, " +
                "'${supervisorProfile.biography}', '${supervisorProfile.researchTopics}', " +
                "'${supervisorProfile.languages.joinToString (";")}')")

        writableDatabase.execSQL(insertStatement)
        setSupervisorTopicCategories(supervisorProfile.userProfile.userId, supervisorProfile.topicCategories)
    }

    private fun setSupervisorTopicCategories(supervisorId: Int, topics: Array<String>) {
        val allTopics = getAllSpecialisations()
        val selectedTopics = mutableListOf<Int>()

        for (topic in topics) {
            selectedTopics.add(allTopics.indexOf(topic)+1)
        }

        val deleteStatement = ("DELETE FROM $TOPIC_SUPERVISOR_TABLE_NAME WHERE $COLUMN_USER_ID = $supervisorId")
        writableDatabase.execSQL(deleteStatement)

        if (selectedTopics.size > 0) {
            var insertStatement =
                ("INSERT INTO $TOPIC_SUPERVISOR_TABLE_NAME ($COLUMN_USER_ID, $COLUMN_TOPICCATEGORY_ID)" +
                        "VALUES ")

            for (topic in selectedTopics) {
                insertStatement += "($supervisorId, $topic), "
            }

            insertStatement = insertStatement.substring(0, insertStatement.length - 2)

            writableDatabase.execSQL(insertStatement)
        }
    }

    fun getChat( id: Int) : Chat  {
        val selectStatement = ("SELECT * FROM $CHAT_HEADER_TABLE_NAME " +
                " WHERE $COLUMN_ID = ?")
        val cursor: Cursor = readableDatabase.rawQuery(selectStatement, arrayOf(id.toString()))

        cursor.moveToFirst()
        val chatId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val userId1 = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CHAT_USER1))
        val userId2 = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CHAT_USER2))

        val userProfile1 = getUser(userId1)
        val userProfile2 = getUser(userId2)

        if (userProfile1 != null && userProfile2 != null) {
            val tmpChat = Chat(chatId, userProfile1, userProfile2)
            tmpChat.messages.addAll(getChatMessages(chatId))
            return tmpChat
        }
        cursor.close()

        return Chat(-1, getCurrentUser(), getCurrentUser())
    }

    fun getChats( myUserId: Int) : List<Chat>  {
        val selectStatement = ("SELECT * FROM $CHAT_HEADER_TABLE_NAME " +
                " WHERE $COLUMN_CHAT_USER1 = ? or $COLUMN_CHAT_USER2 = ?")
        val cursor: Cursor = readableDatabase.rawQuery(selectStatement, arrayOf(myUserId.toString(), myUserId.toString()))

        val chats = mutableListOf<Chat>()
        while (cursor.moveToNext()) {
            val chatId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val userId1 = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CHAT_USER1))
            val userId2 = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CHAT_USER2))

            val userProfile1 = getUser(userId1)
            val userProfile2 = getUser(userId2)

            if (userProfile1 != null && userProfile2 != null) {
                val tmpChat = Chat(chatId, userProfile1, userProfile2)
                tmpChat.messages.addAll(getChatMessages(chatId))
                chats.add(tmpChat)
            }
        }

        cursor.close()
        return chats
    }

    fun getChatMessages(chatId : Int) : List<ChatMessage> {
        val selectStatement = ("SELECT * FROM $CHAT_MESSAGES_TABLE_NAME " +
                " WHERE $COLUMN_CHAT_ID = ? ")
        val cursor: Cursor = readableDatabase.rawQuery(selectStatement, arrayOf(chatId.toString()))

        val messages = mutableListOf<ChatMessage>()
        while (cursor.moveToNext()) {
            val messageId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val touserId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CHAT_TOUSER))
            val message = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE))
            val timeStamp = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
            val readed = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_READED))
            val attachment = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_ATTACHMENT))
            val attName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ATT_NAME))

            val toUserProfile = getUser(touserId)
            if (toUserProfile != null) {

                val chatMessage = ChatMessage(
                    messageId,
                                                        chatId,
                    toUserProfile,
                    message,
                    timeStamp,
                    readed == 1
                )
                chatMessage.attachment = attachment
                chatMessage.attachmentName = attName

                messages.add(chatMessage)
            }
        }

        cursor.close()
        return messages
    }

    fun addChat(userId1 : Int, userId2 : Int){
        val insertStatement = ("INSERT INTO $CHAT_HEADER_TABLE_NAME " +
                "($COLUMN_CHAT_USER1, $COLUMN_CHAT_USER2 ) " +
                "VALUES (${userId1}, ${userId2})")

        writableDatabase.execSQL(insertStatement)
    }

    fun addChatMessage(
        chatId: Int,
        toUserId: Int,
        message: String,
        timeStamp: Int,
        attachment: ByteArray?,
        attachmentName: String?
    ) {
        val insertStatement = ("INSERT INTO $CHAT_MESSAGES_TABLE_NAME " +
                "($COLUMN_CHAT_ID, $COLUMN_CHAT_TOUSER, $COLUMN_MESSAGE, $COLUMN_TIMESTAMP, $COLUMN_READED, $COLUMN_ATTACHMENT, $COLUMN_ATT_NAME) " +
                "VALUES ($chatId, $toUserId, '$message', $timeStamp, 0, '$attachment', '$attachmentName')")

        writableDatabase.execSQL(insertStatement)
    }
}

