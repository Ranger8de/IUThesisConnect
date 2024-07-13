package com.dlbcsemse.iuthesisconnect

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dlbcsemse.iuthesisconnect.helper.DatabaseHelper
import com.dlbcsemse.iuthesisconnect.model.ThesisProfile
import com.dlbcsemse.iuthesisconnect.model.UserProfile

class MyThesisActivity : AppCompatActivity() {
    private lateinit var editTitle: EditText
    private lateinit var editSupervisor: EditText
    private lateinit var editState: EditText
    private lateinit var editSecondSupervisor: EditText
    private lateinit var editStudent: EditText
    private lateinit var editDueDate: EditText
    private lateinit var buttonSave: Button
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var userProfile: UserProfile
    private lateinit var thesisProfile: ThesisProfile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_thesis)

        initializeViews()
        setupUserAndThesis()
        setupSaveButton()
        loadThesisData()
    }

    private fun initializeViews() {
        editTitle = findViewById(R.id.titelMyThesiseditTextEdit)
        editSupervisor = findViewById(R.id.supervisorMyThesiseditTextEdit)
        editState = findViewById(R.id.zustandMyThesisTextEdit)
        editSecondSupervisor = findViewById(R.id.zweitgutachterMyThesisTextEdit)
        editStudent = findViewById(R.id.studentMyThesisTextEdit)
        editDueDate = findViewById(R.id.faelligkeitsdatumMyThesisTextEdit)
        buttonSave = findViewById(R.id.myThesisbuttonSave)
    }

    private fun setupUserAndThesis() {
        databaseHelper = DatabaseHelper(this)
        userProfile = databaseHelper.getCurrentUser()
        // Hier verwenden wir die wiederhergestellte Funktion getThesisByStudent
        thesisProfile = databaseHelper.getThesisByStudent(userProfile.userName)
            ?: ThesisProfile("", "", "", "", userProfile.userName, 0, 0, 0, "", "", userProfile.userType.ordinal)
    }

    private fun setupSaveButton() {
        buttonSave.setOnClickListener {
            saveThesisData()
        }
    }

    private fun loadThesisData() {
        editTitle.setText(thesisProfile.thesisTheme)
        editSupervisor.setText(thesisProfile.thesisSupervisor)
        editState.setText(thesisProfile.thesisState)
        editSecondSupervisor.setText(thesisProfile.thesisSecondSupervisor)
        editStudent.setText(thesisProfile.thesisStudent)
        editDueDate.setText("${thesisProfile.thesisDueDateDay}.${thesisProfile.thesisDueDateMonth}.${thesisProfile.thesisDueDateYear}")

        if (userProfile.userType == DashboardUserType.student) {
            editTitle.isEnabled = true
            editSupervisor.isEnabled = true
            editState.isEnabled = false
            editSecondSupervisor.isEnabled = false
            editDueDate.isEnabled = false
        } else {
            editTitle.isEnabled = false
            editSupervisor.isEnabled = false
            editState.isEnabled = true
            editSecondSupervisor.isEnabled = true
            editDueDate.isEnabled = true
        }
    }

    private fun saveThesisData() {
        if (userProfile.userType == DashboardUserType.student) {
            thesisProfile.thesisTheme = editTitle.text.toString()
            thesisProfile.thesisSupervisor = editSupervisor.text.toString()
        } else {
            thesisProfile.thesisState = editState.text.toString()
            thesisProfile.thesisSecondSupervisor = editSecondSupervisor.text.toString()
            // Parse and set due date
            val dueDateParts = editDueDate.text.toString().split(".")
            if (dueDateParts.size == 3) {
                thesisProfile.thesisDueDateDay = dueDateParts[0].toIntOrNull() ?: 0
                thesisProfile.thesisDueDateMonth = dueDateParts[1].toIntOrNull() ?: 0
                thesisProfile.thesisDueDateYear = dueDateParts[2].toIntOrNull() ?: 0
            }
        }

        // Hier verwenden wir die wiederhergestellte Funktion updateThesis
        val updatedRows = databaseHelper.updateThesis(thesisProfile)
        if (updatedRows > 0) {
            Toast.makeText(this, "Thesis erfolgreich aktualisiert", Toast.LENGTH_SHORT).show()
            loadThesisData()
        } else {
            Toast.makeText(this, "Fehler beim Aktualisieren der Thesis", Toast.LENGTH_SHORT).show()
        }
    }
}