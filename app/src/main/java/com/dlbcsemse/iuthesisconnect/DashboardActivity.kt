package com.dlbcsemse.iuthesisconnect

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dlbcsemse.iuthesisconnect.model.DashboardUserType

class DashboardActivity : ToolbarBaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var buttonAdapter: DashboardButtonAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        setupToolbarButton()

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
                    val intent = Intent(this, SupervisorBoardActivity::class.java)
                    intent.putExtra("userType", userType)
                    startActivity(intent)
                }
                1L -> {
                    val intent = Intent(this, MyThesisActivity::class.java)
                    // Für MyThesisactivity, Usetype übergabe
                    intent.putExtra("userType", userType)
                    startActivity(intent)
                }
                2L -> {
                    val intent = Intent(this, SupervisedThesisBoardActivity::class.java)
                    intent.putExtra("userType", userType)
                    startActivity(intent)
                }
            }
            Unit
        }
        recyclerView.adapter = buttonAdapter


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
        items.add(DashboardItem(0, "Betreuerboard", R.drawable.supervisoboard, DashboardUserType.student))
        items.add(DashboardItem(1, "Meine Abschlussarbeit", R.drawable.thesis, DashboardUserType.student))
        items.add(DashboardItem(2, "Beaufsichtigte Abschlussarbeiten", R.drawable.thesis, DashboardUserType.supervisor))
        return items
    }
