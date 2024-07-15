package com.dlbcsemse.iuthesisconnect

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.dlbcsemse.iuthesisconnect.helper.DatabaseHelper
import com.dlbcsemse.iuthesisconnect.model.UserProfile

class SupervisorViewFromStudentActivity : AppCompatActivity() {
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

    private fun setupToolbarButton() {
        toolbarImageButton.setOnClickListener {
            finish()
        }
    }

    private fun loadSupervisorData() {
        databaseHelper = DatabaseHelper(this)
        val supervisorName = intent.getStringExtra("supervisorName") ?: return
        supervisorProfile = databaseHelper.getUser(supervisorName)!!
        // Load and display supervisor information
    }
}