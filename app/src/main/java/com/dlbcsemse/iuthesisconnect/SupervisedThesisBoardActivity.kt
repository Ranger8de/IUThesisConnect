package com.dlbcsemse.iuthesisconnect

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dlbcsemse.iuthesisconnect.helper.DatabaseHelper
import com.dlbcsemse.iuthesisconnect.model.DashboardUserType
import com.dlbcsemse.iuthesisconnect.model.Thesis

class SupervisedThesisBoardActivity : ToolbarBaseActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SupervisedThesisAdapter
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_supervised_thesis_board)

        setupToolbarButton()

        dbHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.supervisedThesisRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadSupervisedTheses()
    }

    private fun loadSupervisedTheses() {
        val currentUser = dbHelper.getCurrentUser()
        val supervisedTheses = dbHelper.getThesesBySupervisor(currentUser.userId)
        adapter = SupervisedThesisAdapter(supervisedTheses, dbHelper) { thesis ->
            openMyThesisActivity(thesis)
        }
        recyclerView.adapter = adapter
    }

    private fun openMyThesisActivity(thesis: Thesis) {
        val intent = Intent(this, MyThesisActivity::class.java)
        intent.putExtra("thesisId", thesis.id)
        intent.putExtra("userType", DashboardUserType.supervisor.name)
        startActivity(intent)
    }
}