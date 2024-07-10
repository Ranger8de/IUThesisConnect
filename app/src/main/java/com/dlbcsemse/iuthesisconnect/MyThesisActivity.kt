package com.dlbcsemse.iuthesisconnect

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MyThesisActivity : AppCompatActivity() {
    private lateinit var editTitle: EditText
    private lateinit var editsupervisor: EditText
    private lateinit var buttonSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_thesis)

        editTitle = findViewById(R.id.titelMyThesiseditTextData)
        editsupervisor = findViewById(R.id.supervisorMyThesiseditTextData)
        buttonSave = findViewById(R.id.myThesisbuttonSave)

        buttonSave.setOnClickListener {
            saveData()
        }

        // Laden gespeicherter Daten beim Start der Activity
        loadData()
    }

    private fun saveData() {
        val title = editTitle.text.toString()
        val supervisor = editsupervisor.text.toString()
        val sharedSettings = getSharedPreferences("MyThesisSettings", Context.MODE_PRIVATE)
        val editor = sharedSettings.edit()
        editor.putString("savedTitle", title)
        editor.putString("savedSupervisor", supervisor)
        editor.apply()
        Toast.makeText(this, "Daten gespeichert", Toast.LENGTH_SHORT).show()
    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences("MyThesisSettings", Context.MODE_PRIVATE)
        val savedTitle = sharedPreferences.getString("savedTitle", "")
        val savedSupervisor = sharedPreferences.getString("savedSupervisor", "")
        editTitle.setText(savedTitle)
        editsupervisor.setText(savedSupervisor)
    }
}