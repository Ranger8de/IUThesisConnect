package com.dlbcsemse.iuthesisconnect

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dlbcsemse.iuthesisconnect.helper.DatabaseHelper
import com.dlbcsemse.iuthesisconnect.model.UserProfile

class SupervisorViewFromStudentActivity : ToolbarBaseActivity() {
    private lateinit var toolbarImageButton: ImageButton
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var supervisorProfile: UserProfile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supervisor_view_from_student)

        initializeViews()
        setupToolbarButton()
        loadSupervisorData()
    }

    private fun initializeViews() {
        toolbarImageButton = findViewById(R.id.toolbarImageButton)
    }

    private fun loadSupervisorData() {
        databaseHelper = DatabaseHelper(this)
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
}