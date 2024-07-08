package com.dlbcsemse.iuthesisconnect

import ButtonAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DashboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var buttonAdapter: ButtonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userType = DashboardUserType.valueOf(intent.getStringExtra("userType").toString())
        val items: ArrayList<DashboardItem> = getMenuItems(userType)

        // RecyclerView Initialisierung
        recyclerView = findViewById(R.id.dashboardRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Verschiedene Weiterleitungen
        buttonAdapter = ButtonAdapter(items) { clickedItem ->
            when (clickedItem.itemID) {
                // das L muss verwendet werden um zu deklarieren, dass es sich hier um den Datentyp Long handelt
                0L -> {
                    Toast.makeText(this, "Betreuerboard geklickt", Toast.LENGTH_SHORT).show()
                }
                1L -> {
                    val intent = Intent(this, MyThesisActivity::class.java)
                    startActivity(intent)
                }
                2L -> {
                    Toast.makeText(this, "Beaufsichtigte Abschlussarbeiten geklickt", Toast.LENGTH_SHORT).show()
                }
            }
            Unit
        }
        recyclerView.adapter = buttonAdapter
    }

    private fun getMenuItems(userType: DashboardUserType): ArrayList<DashboardItem> {
        var items: ArrayList<DashboardItem> = ArrayList<DashboardItem>()
        for (item in createMenuItems()) {
            if (item.userType == userType) {
                items.add(item)
            }
        }
        return items
    }

    private fun createMenuItems(): ArrayList<DashboardItem> {
        var items: ArrayList<DashboardItem> = ArrayList<DashboardItem>()
        items.add(DashboardItem(0, "Betreuerboard", R.drawable.screenshot, DashboardUserType.student))
        items.add(DashboardItem(1, "Meine Abschlussarbeit", R.drawable.screenshot, DashboardUserType.student))
        items.add(DashboardItem(2, "Beaufsichtigte Abschlussarbeiten", R.drawable.screenshot, DashboardUserType.supervisor))
        return items
    }
}