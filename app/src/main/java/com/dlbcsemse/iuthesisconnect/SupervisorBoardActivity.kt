package com.dlbcsemse.iuthesisconnect

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dlbcsemse.iuthesisconnect.R
import com.dlbcsemse.iuthesisconnect.SupervisorBoardAdapter
import com.dlbcsemse.iuthesisconnect.helper.DatabaseHelper
import com.dlbcsemse.iuthesisconnect.model.DashboardUserType
import com.dlbcsemse.iuthesisconnect.model.SupervisorProfile
import com.dlbcsemse.iuthesisconnect.model.UserProfile

class SupervisorBoardActivity : ToolbarBaseActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SupervisorBoardAdapter
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var languageCheckboxLayout: LinearLayout
    private lateinit var topicCheckboxLayout: LinearLayout
    private lateinit var resetFilterButton: Button
    private var allSupervisors: List<SupervisorProfile> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supervisor_board)

        setupToolbarButton()

        dbHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.dashboardRecyclerView)
        languageCheckboxLayout = findViewById(R.id.languageCheckboxLayout)
        topicCheckboxLayout = findViewById(R.id.topicCheckboxLayout)
        resetFilterButton = findViewById(R.id.resetFilterButton)

        recyclerView.layoutManager = LinearLayoutManager(this)

        allSupervisors = dbHelper.getAllSupervisors()
        adapter = SupervisorBoardAdapter(allSupervisors, dbHelper) { supervisor ->
            val intent = Intent(this, SupervisorViewFromStudentActivity::class.java)
            intent.putExtra("supervisorId", supervisor.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        setupCheckboxes()
        setupResetButton()
    }

    private fun setupCheckboxes() {
        val languages = allSupervisors.flatMap { it.languages.toList() }.distinct().sorted()
        val topics = allSupervisors.flatMap { it.topicCategories.toList() }.distinct().sorted()

        languages.forEach { language ->
            addCheckbox(languageCheckboxLayout, language)
        }

        topics.forEach { topic ->
            addCheckbox(topicCheckboxLayout, topic)
        }

        setScrollViewHeight(languageCheckboxLayout)
        setScrollViewHeight(topicCheckboxLayout)
    }

    private fun addCheckbox(layout: LinearLayout, text: String) {
        val checkbox = CheckBox(this).apply {
            this.text = text
            setOnCheckedChangeListener { _, _ -> filterSupervisors() }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 8)
            }
            textSize = 14f
        }
        layout.addView(checkbox)
    }

    private fun setScrollViewHeight(layout: LinearLayout) {
        val parent = layout.parent as ScrollView
        val checkboxHeight = resources.getDimensionPixelSize(R.dimen.checkbox_height)
        val maxVisibleCheckboxes = 4
        val totalHeight = minOf(layout.childCount, maxVisibleCheckboxes) * checkboxHeight
        parent.layoutParams.height = totalHeight
    }

    private fun filterSupervisors() {
        val selectedLanguages = languageCheckboxLayout.children
            .filterIsInstance<CheckBox>()
            .filter { it.isChecked }
            .map { it.text.toString() }
            .toSet()

        val selectedTopics = topicCheckboxLayout.children
            .filterIsInstance<CheckBox>()
            .filter { it.isChecked }
            .map { it.text.toString() }
            .toSet()

        val filteredList = allSupervisors.filter { supervisor ->
            (selectedLanguages.isEmpty() || supervisor.languages.any { it in selectedLanguages }) &&
                    (selectedTopics.isEmpty() || supervisor.topicCategories.any { it in selectedTopics })
        }

        adapter.updateSupervisors(filteredList)
    }

    private fun setupResetButton() {
        resetFilterButton.setOnClickListener {
            languageCheckboxLayout.children.filterIsInstance<CheckBox>().forEach { it.isChecked = false }
            topicCheckboxLayout.children.filterIsInstance<CheckBox>().forEach { it.isChecked = false }
            adapter.updateSupervisors(allSupervisors)
        }
    }
}