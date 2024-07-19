package com.dlbcsemse.iuthesisconnect.helper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.dlbcsemse.iuthesisconnect.model.AvailabilityStatus
import com.dlbcsemse.iuthesisconnect.model.SupervisorProfile
import com.dlbcsemse.iuthesisconnect.model.Thesis

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
        private const val THESIS_TABLE_NAME = "thesis"
		private const val LANGUAGES_TABLE_NAME = "languages"
        private const val TOPICCATEGORIES_TABLE_NAME = "topic_categories"
        private const val SUPERVISORPROFILE_TABLE_NAME = "supervisor_profile"
        private const val TOPIC_SUPERVISOR_TABLE_NAME = "topic_supervisor"

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
                + "$COLUMN_PICTURE TEXT, "
                + "$COLUMN_ROLE int, "
                + "FOREIGN KEY($COLUMN_ROLE) REFERENCES $ROLE_TABLE_NAME($COLUMN_ID) )")
        db.execSQL(createTable)


        createTable = ("CREATE TABLE $CURRENT_USER_TABLE_NAME ("
                + "$COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "FOREIGN KEY($COLUMN_USER_ID) REFERENCES $PROFILE_TABLE_NAME($COLUMN_ID) )")
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
                + "$COLUMN_LANGUAGES TEXT, "
                + "FOREIGN KEY($COLUMN_USER_ID) REFERENCES $PROFILE_TABLE_NAME($COLUMN_ID))")
        db.execSQL(createTable)

        // Erstellen der Topic Categories-Tabelle (Zuordnung der Forschungsgebiete zu Betreuern)
        createTable = ("CREATE TABLE $TOPIC_SUPERVISOR_TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_USER_ID INTEGER, "
                + "$COLUMN_TOPICCATEGORY_ID INTEGER," +
                " FOREIGN KEY($COLUMN_USER_ID) REFERENCES $PROFILE_TABLE_NAME($COLUMN_ID), " +
                " FOREIGN KEY($COLUMN_TOPICCATEGORY_ID) REFERENCES $TOPICCATEGORIES_TABLE_NAME($COLUMN_ID))")
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
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
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
    // Fügt einen neuen Nutzer hinzu
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
    fun insertOrUpdateThesis(thesis: Thesis) : Long {
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

        val cursor = db.query(THESIS_TABLE_NAME, arrayOf(COLUMN_ID), "$COLUMN_STUDENT = ?", arrayOf(thesis.student.toString()), null, null, null)
        return if (cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
            cursor.close()
            db.update(THESIS_TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(id.toString())).toLong()
        } else {
            cursor.close()
            db.insert(THESIS_TABLE_NAME, null, values)
        }
    }

    fun getSupervisorProfile(supervisorId: Int): SupervisorProfile {
        val selectStatement = "SELECT * FROM $SUPERVISORPROFILE_TABLE_NAME WHERE $COLUMN_ID = ?"
        val cursor: Cursor = readableDatabase.rawQuery(selectStatement, arrayOf(supervisorId.toString()))

        var supervisorProfile: SupervisorProfile = SupervisorProfile.emptySupervisorProfile()
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
            val status = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATUS))
            val biography = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIO))
            val topicCategories = getSupervisorTopicCategories(id)
            val researchFields = cursor.getString(cursor.getColumnIndexOrThrow(
                COLUMN_RESEARCH_TOPICS))

            val languages = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LANGUAGES)).split(";").toTypedArray()

            supervisorProfile = SupervisorProfile(id, userId, AvailabilityStatus.entries[status], biography, topicCategories, researchFields, languages)

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
                "$COLUMN_USER_ID = ${supervisorProfile.userId}, " +
                "$COLUMN_STATUS = ${supervisorProfile.status.ordinal}, " +
                "$COLUMN_BIO = '${supervisorProfile.biography}', " +
                "$COLUMN_RESEARCH_TOPICS = '${supervisorProfile.researchTopics}', " +
                "$COLUMN_LANGUAGES = '${supervisorProfile.languages.joinToString (";")}' " +
                "WHERE $COLUMN_ID = ${supervisorProfile.id}" )


        writableDatabase.execSQL(updateStatement)
        setSupervisorTopicCategories(supervisorProfile.userId, supervisorProfile.topicCategories)
    }

    private fun insertSuperVisorProfile(supervisorProfile: SupervisorProfile) {
        val insertStatement = ("INSERT INTO $SUPERVISORPROFILE_TABLE_NAME " +
                "($COLUMN_USER_ID, $COLUMN_STATUS, $COLUMN_BIO, $COLUMN_RESEARCH_TOPICS, $COLUMN_LANGUAGES) " +
                "VALUES (${supervisorProfile.userId}, ${supervisorProfile.status.ordinal}, " +
                "${supervisorProfile.biography}, ${supervisorProfile.researchTopics}, " +
                "${supervisorProfile.languages.joinToString (";")})")

        writableDatabase.execSQL(insertStatement)
        setSupervisorTopicCategories(supervisorProfile.userId, supervisorProfile.topicCategories)
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
}
