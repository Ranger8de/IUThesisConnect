package com.dlbcsemse.iuthesisconnect

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dlbcsemse.iuthesisconnect.R
import com.dlbcsemse.iuthesisconnect.SupervisorBoardAdapter
import com.dlbcsemse.iuthesisconnect.helper.DatabaseHelper
import com.dlbcsemse.iuthesisconnect.model.DashboardUserType
import com.dlbcsemse.iuthesisconnect.model.UserProfile

class SupervisorBoardActivity : ToolbarBaseActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SupervisorBoardAdapter
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_supervisor_board)

        setupToolbarButton()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.dashboardRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val supervisors = dbHelper.getAllSupervisors()
        adapter = SupervisorBoardAdapter(supervisors)
        recyclerView.adapter = adapter
    }
}