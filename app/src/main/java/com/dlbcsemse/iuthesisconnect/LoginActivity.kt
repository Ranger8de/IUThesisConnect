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

        loginButton.setOnClickListener {

            val userName = loginUsername.text.toString()

            if (userName.equals("student", true)){

            }
            else if (userName.equals("supervisor", true)){

            }
            else {
                return@setOnClickListener
            }

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)

        }
    }
}