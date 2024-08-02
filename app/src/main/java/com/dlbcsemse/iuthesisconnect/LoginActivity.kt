package com.dlbcsemse.iuthesisconnect

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dlbcsemse.iuthesisconnect.helper.AzureAdHelper
import com.dlbcsemse.iuthesisconnect.helper.DatabaseHelper
import com.dlbcsemse.iuthesisconnect.model.DashboardUserType
import com.google.firebase.messaging.FirebaseMessaging
import java.util.UUID

class LoginActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val loginButton = findViewById<Button>(R.id.loginButtonContinue)
        val loginUsername = findViewById<EditText>(R.id.loginEditTextUsername)
        val loginUserPassword = findViewById<EditText>(R.id.loginEditTextPassword)

        loginButton.setOnClickListener {
            val userName = loginUsername.text.toString()
            val userPassword = loginUserPassword.text.toString()
            val azureAdHelper = AzureAdHelper()
            val databaseHelper = DatabaseHelper(this)

            databaseHelper.removeCurrentUser()

            val uuid = azureAdHelper.logIn(userName, userPassword)
            if (uuid == UUID(0, 0)) {
                return@setOnClickListener
            }
            var userProfile = azureAdHelper.getUserProfile(userName)
            if (!databaseHelper.userExists(userProfile.userEmail)) {
                databaseHelper.insertUser(userProfile)
            }

            val userProfileFromDb = databaseHelper.getUser(userName) ?: run {
                Toast.makeText(this, "Benutzer konnte nicht gefunden werden", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Die nächste 3 Zeilen wurden hinzugefügt, da es immernoch Probleme mit der ThesisDatensatzerzeugung gab Stand 17.07.2024
            if (userProfileFromDb.userType == DashboardUserType.student && !databaseHelper.studentHasThesis(userProfileFromDb.userId)) {
                databaseHelper.createThesisForStudent(userProfileFromDb.userId)
            }

            databaseHelper.setCurrentUser(userProfileFromDb)

            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("userType", userProfileFromDb.userType.toString())
            startActivity(intent)
        }

        val dbHelper = DatabaseHelper(this)
        dbHelper.insertInitialUsers()
        dbHelper.ensureAllStudentsHaveThesis()

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful){
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.w("FCM", token)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Erstellen Sie den NotificationChannel
            val name = getString(R.string.fcm_channel_name)
            val descriptionText = getString(R.string.fcm_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(name, name, importance).apply {
                description = descriptionText
            }

            // Registrieren Sie den Kanal im System
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }
}