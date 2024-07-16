package com.dlbcsemse.iuthesisconnect


import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity


open class ToolbarBaseActivity : AppCompatActivity() {


    protected lateinit var toolbarButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    protected fun setupToolbarButton() {
        toolbarButton = findViewById(R.id.toolbarImageButton)
        toolbarButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
