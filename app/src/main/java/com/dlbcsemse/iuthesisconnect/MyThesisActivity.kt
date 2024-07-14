package com.dlbcsemse.iuthesisconnect

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.dlbcsemse.iuthesisconnect.helper.DatabaseHelper
import com.dlbcsemse.iuthesisconnect.model.Thesis
import com.dlbcsemse.iuthesisconnect.model.UserProfile

class MyThesisActivity : AppCompatActivity() {
    private lateinit var editTitle: EditText
    private lateinit var editSupervisor: EditText
    private lateinit var editState: EditText
    private lateinit var editSecondSupervisor: EditText
    private lateinit var editStudent: EditText
    private lateinit var editDueDate: EditText
    private lateinit var buttonRechnungstellung: Button
    private lateinit var buttonSave: Button
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var userProfile: UserProfile
    private lateinit var thesisProfile: Thesis
    private lateinit var viewBillState: TextView

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
        viewBillState = findViewById(R.id.rechnungsstatusMyThesisTextView)
        buttonRechnungstellung = findViewById(R.id.myThesisbuttonRechnungsstellung)
        buttonSave = findViewById(R.id.myThesisbuttonSave)
    }

    private fun setupUserAndThesis() {
        databaseHelper = DatabaseHelper(this)
        userProfile = databaseHelper.getCurrentUser()
        // Hier verwenden wir die wiederhergestellte Funktion getThesisByStudent
        // Wir stellen hier auch die Grundeinstellungen für eine neu Abschlussarbeit bereit
        thesisProfile = databaseHelper.getThesisByStudent(userProfile.userName)
            ?: Thesis(-1, "Noch nicht zugewiesen", -1, -1, userProfile.userName, 0, 0, 0, 0, "Noch nicht gestellt", userProfile.userType.ordinal)
    }

    private fun setupSaveButton() {
        buttonSave.setOnClickListener {
            saveThesisData()
        }
    }

    private fun loadThesisData() {
        editTitle.setText(thesisProfile.theme)
        editSupervisor.setText(thesisProfile.supervisor.toString())
        editState.setText(thesisProfile.state)
        editSecondSupervisor.setText(thesisProfile.secondSupervisor.toString())
        editStudent.setText(thesisProfile.student.toString())
        editDueDate.setText("${thesisProfile.dueDateDay}.${thesisProfile.dueDateMonth}.${thesisProfile.dueDateYear}")
        viewBillState.text = thesisProfile.thesisBillState

        if (userProfile.userType == DashboardUserType.student) {
            editTitle.isEnabled = true
            editSupervisor.isEnabled = true
            editState.isEnabled = false
            editSecondSupervisor.isEnabled = false
            editDueDate.isEnabled = false
            viewBillState.isVisible = false
            buttonRechnungstellung.isVisible = false
        } else {
            editTitle.isEnabled = false
            editSupervisor.isEnabled = false
            editState.isEnabled = true
            editSecondSupervisor.isEnabled = true
            editDueDate.isEnabled = true
            viewBillState.isVisible = true
            buttonRechnungstellung.isVisible = true
        }
    }

    private fun saveThesisData() {
        val supervisorUser = databaseHelper.getUser(editSupervisor.text.toString())
        val secondSupervisorUser = databaseHelper.getUser(editSecondSupervisor.text.toString())
        val dueDateParts = editDueDate.text.toString().split(".")

        if (userProfile.userType == DashboardUserType.student) {
            thesisProfile.theme = editTitle.text.toString()
            if (supervisorUser != null) {
            thesisProfile.supervisor = supervisorUser.id.toInt()}
        } else {
            thesisProfile.state = editState.text.toString()
            if (secondSupervisorUser != null) {
            thesisProfile.secondSupervisor = secondSupervisorUser.id.toInt()}

            if (dueDateParts.size == 3) {
                thesisProfile.dueDateDay = dueDateParts[0].toIntOrNull() ?: 0
                thesisProfile.dueDateMonth = dueDateParts[1].toIntOrNull() ?: 0
                thesisProfile.dueDateYear = dueDateParts[2].toIntOrNull() ?: 0
            }
        }

        // Hier verwenden wir die wiederhergestellte Funktion updateThesis
        var updatedRows = -1
        if (thesisProfile.id <= 0) {
            updatedRows = databaseHelper.updateThesis(thesisProfile)
        }
        else {
            databaseHelper.insertThesis(thesisProfile)
        }
        if (updatedRows > 0) {
            Toast.makeText(this, "Thesis erfolgreich aktualisiert", Toast.LENGTH_SHORT).show()
            loadThesisData()
        } else {
            Toast.makeText(this, "Fehler beim Aktualisieren der Thesis", Toast.LENGTH_SHORT).show()
        }
    }
}