package com.dlbcsemse.iuthesisconnect

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.dlbcsemse.iuthesisconnect.helper.DatabaseHelper
import com.dlbcsemse.iuthesisconnect.model.UserProfile

class SupervisorViewFromStudentActivity : ToolbarBaseActivity() {
    private lateinit var toolbarImageButton: ImageButton
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var supervisorProfile: UserProfile
    private lateinit var assignThesisButton: Button
    private lateinit var currentUser: UserProfile


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supervisor_view_from_student)

        initializeViews()
        setupToolbarButton()
        loadSupervisorData()
        setupAssignThesisButton()
    }

    private fun initializeViews() {
        toolbarImageButton = findViewById(R.id.toolbarImageButton)
        assignThesisButton = findViewById(R.id.supervisorViewFromStudentAssignThesisButton)
    }

    private fun loadSupervisorData() {
        databaseHelper = DatabaseHelper(this)
        currentUser = databaseHelper.getCurrentUser()
        val supervisorName = intent.getStringExtra("supervisorName")
        if (supervisorName != null) {
            supervisorProfile = databaseHelper.getUser(supervisorName) ?: return
            displaySupervisorInfo()
        } else {
            finish()
        }
    }

    private fun displaySupervisorInfo() {
        findViewById<TextView>(R.id.profileTextViewName).text = supervisorProfile.userName
        findViewById<TextView>(R.id.profileTextViewEmail).text = supervisorProfile.userEmail
    }

    private fun setupAssignThesisButton() {
        assignThesisButton.setOnClickListener {
            assignThesisToSupervisor()
        }
    }

    private fun assignThesisToSupervisor() {
        val thesis = databaseHelper.getThesisByStudent(currentUser.userName)
        if (thesis != null) {
            thesis.supervisor = supervisorProfile.userId.toInt()
            val updated = databaseHelper.updateThesis(thesis)
            if (updated > 0) {
                Toast.makeText(this, "Thesis erfolgreich zugewiesen", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Fehler beim Zuweisen der Thesis", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Keine Thesis gefunden", Toast.LENGTH_SHORT).show()
        }
    }
}
