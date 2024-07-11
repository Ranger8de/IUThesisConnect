package com.dlbcsemse.iuthesisconnect

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.dlbcsemse.iuthesisconnect.helper.DatabaseHelper
import com.dlbcsemse.iuthesisconnect.model.DashboardUserType
import com.dlbcsemse.iuthesisconnect.model.UserProfile
import com.dlbcsemse.iuthesisconnect.model.ThesisProfile

class MyThesisActivity : AppCompatActivity() {
    // Datenbank-Helper und Datenmodelle
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var currentUser: UserProfile
    private lateinit var thesis: ThesisProfile
    private lateinit var userType: DashboardUserType

    // UI-Elemente
    private lateinit var titleEditText: EditText
    private lateinit var supervisorEditText: EditText
    private lateinit var stateTextView: TextView
    private lateinit var secondSupervisorTextView: TextView
    private lateinit var studentTextView: TextView
    private lateinit var dueDateTextView: TextView
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_thesis)

        // Toolbar einrichten
        setupToolbar()

        try {
            // Initialisierung der Daten und UI
            initializeData()
            initializeViews()
            loadAndDisplayThesisData()
            setupSaveButton()
        } catch (e: Exception) {
            // Fehlerbehandlung
            Log.e("MyThesisActivity", "Error in onCreate", e)
            Toast.makeText(this, "Ein Fehler ist aufgetreten: ${e.message}", Toast.LENGTH_LONG).show()
            finish() // Beendet die Activity bei einem Fehler
        }
    }

    // Richtet die Toolbar ein
    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    // Initialisiert die Daten aus der Datenbank und dem Intent
    private fun initializeData() {
        dbHelper = DatabaseHelper(this)
        // Sicheres Casting des userType mit Fallback auf einen Fehler
        userType = intent.getSerializableExtra("userType") as? DashboardUserType
            ?: throw IllegalArgumentException("User type not provided")
        currentUser = dbHelper.getCurrentUser()
        // Lädt die Thesis oder erstellt eine neue, wenn keine existiert
        thesis = dbHelper.getThesisByStudent(currentUser.userName) ?: ThesisProfile(
            "", "", "", "", currentUser.userName, 0, 0, 0, "", "", currentUser.userType.ordinal
        )
    }

    // Initialisiert die UI-Elemente
    private fun initializeViews() {
        titleEditText = findViewById(R.id.titelMyThesiseditTextEdit)
        supervisorEditText = findViewById(R.id.supervisorMyThesiseditTextEdit)
        stateTextView = findViewById(R.id.titlezustandMyThesisTextView)
        secondSupervisorTextView = findViewById(R.id.zweitgutachterMyThesisTextEdit)
        studentTextView = findViewById(R.id.studentMyThesisTextEdit)
        dueDateTextView = findViewById(R.id.faelligkeitsdatumMyThesisTextEdit)
        saveButton = findViewById(R.id.myThesisbuttonSave)
    }

    // Lädt und zeigt die Thesis-Daten an
    private fun loadAndDisplayThesisData() {
        titleEditText.setText(thesis.theme)
        supervisorEditText.setText(thesis.supervisor)
        stateTextView.text = thesis.state
        secondSupervisorTextView.text = thesis.secondSupervisor
        studentTextView.text = thesis.student
        dueDateTextView.text = "${thesis.dueDateDay}.${thesis.dueDateMonth}.${thesis.dueDateYear}"

        // Passt die UI basierend auf dem Benutzertyp an
        when (userType) {
            DashboardUserType.student -> {
                titleEditText.isEnabled = false
                supervisorEditText.isEnabled = false
                saveButton.visibility = View.GONE
            }
            DashboardUserType.supervisor -> {
                titleEditText.isEnabled = true
                supervisorEditText.isEnabled = true
                saveButton.visibility = View.VISIBLE
            }
        }
    }

    // Richtet den Speichern-Button ein
    private fun setupSaveButton() {
        saveButton.setOnClickListener {
            if (userType == DashboardUserType.supervisor) {
                updateThesis()
            } else {
                Toast.makeText(this, "Sie haben keine Berechtigung, diese Thesis zu bearbeiten.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Aktualisiert die Thesis-Daten
    private fun updateThesis() {
        thesis.theme = titleEditText.text.toString()
        thesis.supervisor = supervisorEditText.text.toString()

        val updatedRows = dbHelper.updateThesis(thesis)
        if (updatedRows > 0) {
            Toast.makeText(this, "Thesis erfolgreich aktualisiert", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Fehler beim Aktualisieren der Thesis", Toast.LENGTH_SHORT).show()
        }
    }
}