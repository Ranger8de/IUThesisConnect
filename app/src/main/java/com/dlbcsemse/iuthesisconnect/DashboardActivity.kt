package com.dlbcsemse.iuthesisconnect

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
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
        val userType = DashboardUserType.valueOf(intent.getStringExtra("userType").toString())
        var items: ArrayList<DashboardItem> = getMenuItems(userType)
        val itemAdapter = DashboardItemAdapter(this, items)
        var listView = findViewById<ListView>(R.id.dashboardListView)
        listView.adapter = itemAdapter

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
        items.add(DashboardItem(0, "Meine Abschlussarbeit", R.drawable.screenshot, DashboardUserType.student))
        items.add(DashboardItem(0, "Beaufsichtigte Abschlussarbeiten", R.drawable.screenshot, DashboardUserType.supervisor))
        return items

    }
}
