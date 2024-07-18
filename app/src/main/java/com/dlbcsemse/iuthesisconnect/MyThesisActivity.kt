package com.dlbcsemse.iuthesisconnect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.dlbcsemse.iuthesisconnect.helper.DatabaseHelper
import com.dlbcsemse.iuthesisconnect.model.DashboardUserType
import com.dlbcsemse.iuthesisconnect.model.Thesis
import com.dlbcsemse.iuthesisconnect.model.UserProfile


class MyThesisActivity : ToolbarBaseActivity() {
    private lateinit var toolbarImageButton: ImageButton

    // Datenbank-Helper und Datenmodelle
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var currentUser: UserProfile
    private lateinit var thesis: Thesis
    private lateinit var userType: DashboardUserType

    // UI-Elemente
    private lateinit var titleEditText: EditText
    private lateinit var supervisorTextView: TextView
    private lateinit var stateSpinner: Spinner
    private lateinit var secondSupervisorSpinner: Spinner
    private lateinit var supervisors: List<UserProfile>
    private lateinit var dueDateEditText: EditText
    private lateinit var stateTextView: TextView
    private lateinit var secondSupervisorTextView: TextView
    private lateinit var studentEditText: EditText
    private lateinit var dueDateTextView: TextView
    private lateinit var billStateTopicTextView: TextView
    private lateinit var billStateTextView: TextView
    private lateinit var billButton: Button
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_thesis)

        setupToolbarButton()
        initializeData()
        initializeViews()
        setupStateSpinner()
        setupSecondSupervisorSpinner()
        loadAndDisplayThesisData()
        setupSaveButton()
        setupBillButton()
    }

    // Initialisiert die Daten aus der Datenbank und dem Intent
    private fun initializeData() {
        dbHelper = DatabaseHelper(this)
        userType = try {
            DashboardUserType.valueOf(intent.getStringExtra("userType") ?: "")
        } catch (e: IllegalArgumentException) {
            DashboardUserType.student
        }
        currentUser = dbHelper.getCurrentUser()

        val thesisId = intent.getIntExtra("thesisId", -1)
        thesis = if (thesisId != -1) {
            dbHelper.getThesisById(thesisId) ?: createNewThesis()
        } else {
            dbHelper.getThesisByStudent(currentUser.userId) ?: createNewThesis()
        }
    }

    private fun createNewThesis(): Thesis {
        return Thesis(
            -1,
            "Noch nicht begonnen",
            -1,
            -1,
            "Noch nicht erstellt",
            currentUser.userId.toInt(),
            0,
            0,
            0,
            DatabaseHelper.DEFAULT_BILL_STATE,
            currentUser.userType.ordinal
        ).also { newThesis ->
            val insertedId = dbHelper.insertThesis(newThesis)
            if (insertedId != -1L) {
                newThesis.id = insertedId.toInt()
            } else {
                Log.e("MyThesisActivity", "Failed to insert new Thesis")
            }
        }
    }

    // Initialisiert die UI-Elemente
    private fun initializeViews() {
        titleEditText = findViewById(R.id.titelMyThesiseditTextEdit)
        supervisorTextView = findViewById(R.id.supervisorMyThesisTextView)
        stateSpinner = findViewById(R.id.zustandMyThesisSpinner)
        secondSupervisorSpinner = findViewById(R.id.zweitgutachterMyThesisSpinner)
        studentEditText = findViewById(R.id.studentMyThesisTextEdit)
        dueDateEditText = findViewById(R.id.faelligkeitsdatumMyThesisTextEdit)
        //
        billStateTextView = findViewById(R.id.rechnungsstatusMyThesisTextView)
        billStateTopicTextView = findViewById(R.id.titlerechnungssatusMyThesisTextView)

        saveButton = findViewById(R.id.myThesisbuttonSave)
        //
        billButton = findViewById(R.id.myThesisbuttonRechnungsstellung)
    }

    private fun setupSecondSupervisorSpinner() {
        supervisors = dbHelper.getAllSupervisors()
        val supervisorNames = supervisors.map { it.userName }.toMutableList()
        supervisorNames.add(0, "Nicht zugewiesen")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, supervisorNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        secondSupervisorSpinner.adapter = adapter

        secondSupervisorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    thesis.secondSupervisor = -1
                } else {
                    thesis.secondSupervisor = supervisors[position - 1].userId
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    private fun setupStateSpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.thesis_states,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            stateSpinner.adapter = adapter
        }

        stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                thesis.state = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    // Nachfolgende Funktion wurde dahingehend abgeänder, dass jetzt nutzernamen statt id´s angezeigt werden
    // Lädt und zeigt die Thesis-Daten an
    private fun loadAndDisplayThesisData() {
        titleEditText.setText(thesis.theme)

        val supervisorName = if (thesis.supervisor != -1) {
            dbHelper.getUserNameById(thesis.supervisor)
        } else {
            "Nicht zugewiesen"
        }
        supervisorTextView.text = supervisorName

        val statePosition = (stateSpinner.adapter as ArrayAdapter<String>).getPosition(thesis.state)
        stateSpinner.setSelection(statePosition)

        val secondSupervisorName = if (thesis.secondSupervisor != -1) {
            dbHelper.getUserNameById(thesis.secondSupervisor)
        } else {
            "Nicht zugewiesen"
        }
        val secondSupervisorPosition = (secondSupervisorSpinner.adapter as ArrayAdapter<String>).getPosition(secondSupervisorName)
        if (secondSupervisorPosition != -1) {
            secondSupervisorSpinner.setSelection(secondSupervisorPosition)
        }

        billStateTextView.text = thesis.billState ?: DatabaseHelper.DEFAULT_BILL_STATE
        studentEditText.setText(dbHelper.getUserNameById(thesis.student))
        dueDateEditText.setText("${thesis.dueDateDay}.${thesis.dueDateMonth}.${thesis.dueDateYear}")

        // Passt die UI basierend auf dem Benutzertyp an
        when (userType) {
            DashboardUserType.student -> {
                titleEditText.isEnabled = true
                stateSpinner.isEnabled = false
                secondSupervisorSpinner.isEnabled = false
                dueDateEditText.isEnabled = false
                billStateTextView.isVisible = false
                billStateTopicTextView.isVisible = false
                billButton.isVisible = false
                billButton.isEnabled = false
            }
            DashboardUserType.supervisor -> {
                titleEditText.isEnabled = false
                stateSpinner.isEnabled = true
                secondSupervisorSpinner.isEnabled = true
                dueDateEditText.isEnabled = true
                billStateTextView.isVisible = true
                billStateTopicTextView.isVisible = true
                billButton.isVisible = true
                billButton.isEnabled = true
            }
        }
    }
    // Richtet den Speichern-Button ein
    private fun setupSaveButton() {
        saveButton.setOnClickListener {
            updateThesis()
        }
    }
    // Aktualisiert die Thesis-Daten
    private fun updateThesis() {
        when (userType) {
            DashboardUserType.student -> {
                thesis.theme = titleEditText.text.toString()
            }
            DashboardUserType.supervisor -> {
                val dateParts = dueDateEditText.text.toString().split(".")
                if (dateParts.size == 3) {
                    thesis.dueDateDay = dateParts[0].toIntOrNull() ?: 0
                    thesis.dueDateMonth = dateParts[1].toIntOrNull() ?: 0
                    thesis.dueDateYear = dateParts[2].toIntOrNull() ?: 0
                }
            }
        }

        val result = dbHelper.insertOrUpdateThesis(thesis)
        if (result != -1L) {
            Toast.makeText(this, "Thesis erfolgreich aktualisiert", Toast.LENGTH_SHORT).show()
            loadAndDisplayThesisData()
        } else {
            Toast.makeText(this, "Fehler beim Aktualisieren der Thesis", Toast.LENGTH_SHORT).show()
        }
    }
    private fun setupBillButton() {
        billButton.setOnClickListener {
            if (userType == DashboardUserType.supervisor) {
                thesis.billState = "Rechnung wurde versendet"
                val result = dbHelper.insertOrUpdateThesis(thesis)
                if (result != -1L) {
                    Toast.makeText(this, "Rechnungsstatus aktualisiert", Toast.LENGTH_SHORT).show()
                    loadAndDisplayThesisData()
                } else {
                    Toast.makeText(this, "Fehler beim Aktualisieren des Rechnungsstatus", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}