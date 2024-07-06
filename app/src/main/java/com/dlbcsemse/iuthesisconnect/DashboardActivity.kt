package com.dlbcsemse.iuthesisconnect

import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var items = ArrayList<DashboardItem>()
        items.add(DashboardItem(0, "Betreuerboard", R.drawable.screenshot, DashboardUserType.student))
        val itemAdapter = DashboardItemAdapter(this, items)
        var listView = findViewById<ListView>(R.id.dashboardListView)
    }
}