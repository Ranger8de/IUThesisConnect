package com.dlbcsemse.iuthesisconnect

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.dlbcsemse.iuthesisconnect.helper.DatabaseHelper
import com.dlbcsemse.iuthesisconnect.model.SupervisorProfile
import com.dlbcsemse.iuthesisconnect.model.UserProfile
import java.util.Base64

class SupervisorViewFromStudentActivity : ToolbarBaseActivity() {
    private lateinit var toolbarImageButton: ImageButton
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var supervisorProfile: SupervisorProfile
    private lateinit var assignThesisButton: Button
    private lateinit var contactSupervisorButton : Button
    private lateinit var currentUser: UserProfile


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supervisor_view_from_student)

        initializeViews()
        setupToolbarButton()
        loadSupervisorData()
        setupAssignThesisButton()
        setupContactSupervisorButton()
    }

    private fun initializeViews() {
        toolbarImageButton = findViewById(R.id.toolbarImageButton)
        assignThesisButton = findViewById(R.id.supervisorViewFromStudentAssignThesisButton)
    }

    private fun loadSupervisorData() {
        databaseHelper = DatabaseHelper(this)
        currentUser = databaseHelper.getCurrentUser()
        val supervisorId = intent.getIntExtra("supervisorId", -1)
        if (supervisorId > 0) {
            supervisorProfile = databaseHelper.getSupervisorProfile(supervisorId) ?: return
            displaySupervisorInfo()
        } else {
            finish()
        }
    }

    private fun displaySupervisorInfo() {
        findViewById<TextView>(R.id.profileTextViewName).text = supervisorProfile.userProfile.userName
        findViewById<TextView>(R.id.profileTextViewEmail).text = supervisorProfile.userProfile.userEmail
        findViewById<TextView>(R.id.supervisorViewFromStudentLanguages).text = supervisorProfile.languages.joinToString (", ")
        findViewById<TextView>(R.id.supervisorViewFromStudentSubjects).text = supervisorProfile.topicCategories.joinToString ("\r\n")
        findViewById<TextView>(R.id.supervisorViewFromStudentBiography).text = supervisorProfile.biography
        findViewById<TextView>(R.id.supervisorViewFromStudentThesisThemes).text = supervisorProfile.researchTopics

        val decodedString: ByteArray = Base64.getDecoder().decode(supervisorProfile.userProfile.picture.toByteArray())
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        findViewById<ImageView>(R.id.profileImageViewImage).setImageBitmap(decodedByte)
    }

    private fun setupAssignThesisButton() {
        assignThesisButton.setOnClickListener {
            assignThesisToSupervisor()
        }
    }

    private fun assignThesisToSupervisor() {
        val thesis = databaseHelper.getThesisByStudent(currentUser.userId)
        if (thesis != null) {
            thesis.supervisor = supervisorProfile.userProfile.userId.toInt()
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

    private fun setupContactSupervisorButton() {
        contactSupervisorButton = findViewById(R.id.supervisorViewFromStudentContactButton)

        contactSupervisorButton.setOnClickListener {
            val chatId = databaseHelper.addChat(supervisorProfile.userProfile.userId, currentUser.userId)

            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("chatId", chatId)
            startActivity(intent)

        }
    }
}
