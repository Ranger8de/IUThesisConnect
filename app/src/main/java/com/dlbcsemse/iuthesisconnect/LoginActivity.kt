package com.dlbcsemse.iuthesisconnect

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dlbcsemse.iuthesisconnect.helper.AzureAdHelper

class LoginActivity : AppCompatActivity() {
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
            var userType : DashboardUserType
            val azureAdHelper  = AzureAdHelper()

            val uuid = azureAdHelper.logIn(userName, userPassword)



            if (userName.equals("student", true)){
                userType = DashboardUserType.student
            }
            else if (userName.equals("supervisor", true)){
                userType = DashboardUserType.supervisor
            }
            else {
                return@setOnClickListener
            }

            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("userType", userType.toString())
            startActivity(intent)
        }
    }
}