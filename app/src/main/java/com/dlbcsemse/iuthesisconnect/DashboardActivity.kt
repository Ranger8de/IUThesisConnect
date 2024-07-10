package com.dlbcsemse.iuthesisconnect

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DashboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var buttonAdapter: DashboardButtonAdapter
    private lateinit var toolbarButton: ImageButton

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
        buttonAdapter = DashboardButtonAdapter(items) { clickedItem ->
            when (clickedItem.itemID) {
                // das L muss verwendet werden um zu deklarieren, dass es sich hier um den Datentyp Long handelt
                0L -> {
                    val intent = Intent(this, BetreuerboardActivity::class.java)
                    startActivity(intent)
                }
                1L -> {
                    val intent = Intent(this, MyThesisActivity::class.java)
                    startActivity(intent)
                }
                2L -> {
                    val intent = Intent(this, BetreuteAbschlussarbeitenActivity::class.java)
                    startActivity(intent)
                }
            }
            Unit
        }
        recyclerView.adapter = buttonAdapter


        toolbarButton = findViewById(R.id.toolbarImageButton)
        toolbarButton.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

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