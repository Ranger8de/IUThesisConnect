package com.dlbcsemse.iuthesisconnect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.dlbcsemse.iuthesisconnect.helper.DatabaseHelper
import com.dlbcsemse.iuthesisconnect.model.DashboardUserType
import com.dlbcsemse.iuthesisconnect.model.Thesis
import com.dlbcsemse.iuthesisconnect.model.UserProfile


class MyThesisActivity : AppCompatActivity() {
    private lateinit var toolbarImageButton : ImageButton
    // Datenbank-Helper und Datenmodelle
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var currentUser: UserProfile
    private lateinit var thesis: Thesis
    private lateinit var userType: DashboardUserType
    // UI-Elemente
    private lateinit var titleEditText: EditText
    private lateinit var supervisorEditText: EditText
    private lateinit var stateEditText: EditText
    private lateinit var secondSupervisorEditText: EditText
    private lateinit var dueDateEditText: EditText
    private lateinit var stateTextView: TextView
    private lateinit var secondSupervisorTextView: TextView
    private lateinit var studentEditText: EditText
    private lateinit var dueDateTextView: TextView
    private lateinit var billStateTopicTextView: TextView
    private lateinit var billStateTextView: TextView
    private lateinit var billButton: Button
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_thesis)

        ToolbarButton()

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
    // Gibt dem Toolbarbutton die weiterleit-Funktion
    private fun ToolbarButton() {
        toolbarImageButton = findViewById(R.id.toolbarImageButton)
        toolbarImageButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
    // Initialisiert die Daten aus der Datenbank und dem Intent
    private fun initializeData() {
        dbHelper = DatabaseHelper(this)
        // Sicheres Casting des userType mit Fallback auf einen Fehler
        userType = intent.getSerializableExtra("userType") as? DashboardUserType
            ?: throw IllegalArgumentException("User type not provided")
        currentUser = dbHelper.getCurrentUser()
        // Lädt die Thesis oder erstellt eine neue, wenn keine existiert
        thesis = dbHelper.getThesisByStudent(currentUser.userName) ?: Thesis(
            currentUser.userId.toInt(),
            "Noch nicht begonnen" ,
            -1 ,
            -1 ,
            "Noch nicht erstellt" ,
            currentUser.userId.toInt() ,
            0 ,
            0 ,
            0 ,
            "Noch nicht gestellt",
            currentUser.userType.ordinal
        )
    }
    // Initialisiert die UI-Elemente
    private fun initializeViews() {
        titleEditText = findViewById(R.id.titelMyThesiseditTextEdit)
        supervisorEditText = findViewById(R.id.supervisorMyThesiseditTextEdit)
        stateEditText = findViewById(R.id.zustandMyThesisTextEdit)
        secondSupervisorEditText = findViewById(R.id.zweitgutachterMyThesisTextEdit)
        studentEditText = findViewById(R.id.studentMyThesisTextEdit)
        dueDateEditText = findViewById(R.id.faelligkeitsdatumMyThesisTextEdit)
        //
        billStateTextView = findViewById(R.id.rechnungsstatusMyThesisTextView)
        billStateTopicTextView = findViewById(R.id.titlerechnungssatusMyThesisTextView)

        saveButton = findViewById(R.id.myThesisbuttonSave)
        //
        billButton = findViewById(R.id.myThesisbuttonRechnungsstellung)
    }
    // Lädt und zeigt die Thesis-Daten an
    private fun loadAndDisplayThesisData() {
        titleEditText.setText(thesis.theme)
        supervisorEditText.setText(thesis.supervisor.toString())
        stateEditText.setText(thesis.state)
        secondSupervisorEditText.setText(thesis.secondSupervisor.toString())
        // setzt den Studenten automatin anhand des aktuellen Users
        studentEditText.setText(currentUser.userName)
        dueDateEditText.setText("${thesis.dueDateDay}.${thesis.dueDateMonth}.${thesis.dueDateYear}")

        // Passt die UI basierend auf dem Benutzertyp an
        when (userType) {
            DashboardUserType.student -> {
                titleEditText.isEnabled = true
                supervisorEditText.isEnabled = true
                stateEditText.isEnabled = false
                secondSupervisorEditText.isEnabled = false
                dueDateEditText.isEnabled = false
                billStateTextView.isVisible = false
                billStateTopicTextView.isVisible = false
                billButton.isVisible = false
            }
            DashboardUserType.supervisor -> {
                titleEditText.isEnabled = false
                supervisorEditText.isEnabled = false
                stateEditText.isEnabled = true
                secondSupervisorEditText.isEnabled = true
                dueDateEditText.isEnabled = true
                billStateTextView.isVisible = true
                billStateTopicTextView.isVisible = true
                billButton.isVisible = true
            }
        }
    }
    // Richtet den Speichern-Button ein
    private fun setupSaveButton() {
        saveButton.setOnClickListener {
            updateThesis()
        }
    }
    // Aktualisiert die Thesis-Daten
    private fun updateThesis() {
        when (userType) {
            DashboardUserType.student -> {
                thesis.theme = titleEditText.text.toString()
                thesis.supervisor = supervisorEditText.text.toString().toIntOrNull() ?: -1
                // Anzeigen lassen was geupdatet wurde
                Log.d("MyThesisActivity", "Updating as student: theme=${thesis.theme}, supervisor=${thesis.supervisor}")
            }
            DashboardUserType.supervisor -> {
                thesis.state = stateEditText.text.toString()
                thesis.secondSupervisor = secondSupervisorEditText.text.toString().toString().toIntOrNull() ?: -1
                // Hier müssen Sie die Datumsverarbeitung anpassen
                val dateParts = dueDateEditText.text.toString().split(".")
                if (dateParts.size == 3) {
                    thesis.dueDateDay = dateParts[0].toIntOrNull() ?: 0
                    thesis.dueDateMonth = dateParts[1].toIntOrNull() ?: 0
                    thesis.dueDateYear = dateParts[2].toIntOrNull() ?: 0
                }
                Log.d("MyThesisActivity", "Updating as supervisor: state=${thesis.state}, secondSupervisor=${thesis.secondSupervisor}, dueDate=${thesis.dueDateDay}.${thesis.dueDateMonth}.${thesis.dueDateYear}")
            }
        }
        val updatedRows = dbHelper.updateThesis(thesis)
        Log.d("MyThesisActivity", "Updated rows: $updatedRows")
        if (updatedRows > 0) {
            Toast.makeText(this, "Thesis erfolgreich aktualisiert", Toast.LENGTH_SHORT).show()
            loadAndDisplayThesisData()
        } else {
            Toast.makeText(this, "Fehler beim Aktualisieren der Thesis", Toast.LENGTH_SHORT).show()
        }
    }
}