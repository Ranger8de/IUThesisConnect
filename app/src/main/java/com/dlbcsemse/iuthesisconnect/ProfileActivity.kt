package com.dlbcsemse.iuthesisconnect

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dlbcsemse.iuthesisconnect.helper.DatabaseHelper
import com.dlbcsemse.iuthesisconnect.model.AvailabilityStatus
import com.dlbcsemse.iuthesisconnect.model.DashboardUserType
import com.dlbcsemse.iuthesisconnect.model.Language
import com.dlbcsemse.iuthesisconnect.model.SupervisorProfile
import com.dlbcsemse.iuthesisconnect.model.UserProfile
import java.util.Base64

class ProfileActivity : AppCompatActivity() {
    private lateinit var toolBar : Toolbar
    private lateinit var imgButton: ImageButton
    private lateinit var textViewStatus : TextView
    private lateinit var userProfile : UserProfile
    private lateinit var supervisorProfile: SupervisorProfile
    private lateinit var userName : TextView
    private lateinit var userEmail : TextView
    private lateinit var userImage : ImageView
    private lateinit var textBiography : EditText
    private lateinit var textResearch : EditText
    private lateinit var textTopicCategories : TextView
    private lateinit var textSpecialisation : TextView
    private lateinit var cardViewLanguage : CardView
    private lateinit var cardViewBiography : CardView
    private lateinit var cardViewTopicCategories : CardView
    private lateinit var cardViewResearchFields : CardView
    private lateinit var toggleButtonGerman : SwitchCompat
    private lateinit var toggleButtonEnglish : SwitchCompat
    private var isProfileChanged : Boolean = false
    private lateinit var databaseHelper : DatabaseHelper
    private lateinit var chatButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        databaseHelper = DatabaseHelper(this)

        userProfile = databaseHelper.getCurrentUser()
        supervisorProfile = databaseHelper.getSupervisorProfile(userProfile.id.toInt())

        toolBar = findViewById(R.id.profileToolbar)
        imgButton = findViewById<ImageButton>(R.id.profileImageViewAvailableStatus)
        textViewStatus = findViewById<TextView>(R.id.profileTextViewAvailableStatus)
        userEmail = findViewById(R.id.profileTextViewEmail)
        userName = findViewById(R.id.profileTextViewName)
        userImage = findViewById(R.id.profileImageViewImage)
        textBiography = findViewById(R.id.profileEditTextBiography)
        textResearch = findViewById(R.id.profileEditTextResearchField)
        textSpecialisation = findViewById(R.id.profileTextViewSpecializations)
        cardViewLanguage = findViewById(R.id.profileCardViewLanguage)
        cardViewBiography = findViewById(R.id.profileCardViewBiography)
        cardViewTopicCategories = findViewById(R.id.profileCardViewTopicCategories)
        cardViewResearchFields = findViewById(R.id.profileCardViewResearchFields)
        toggleButtonGerman = findViewById(R.id.profileToggleButtonGerman)
        toggleButtonEnglish = findViewById(R.id.profileToggleButtonEnglish)
        chatButton = findViewById(R.id.profileImageButtonChat)

        if (userProfile.userType == DashboardUserType.student) {
            imgButton.visibility = View.INVISIBLE
            textViewStatus.visibility = View.INVISIBLE
            cardViewBiography.visibility = View.GONE
            cardViewLanguage.visibility = View.GONE
            cardViewTopicCategories.visibility = View.GONE
            cardViewResearchFields.visibility = View.GONE

        }
        else{
            supervisorProfile = databaseHelper.getSupervisorProfile(userProfile.id.toInt())
        }

        setSupportActionBar(toolBar);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        toolBar.setNavigationOnClickListener {
            finish()
        }

        chatButton.setOnClickListener {
            val intent = Intent(this, ChatOverviewActivity::class.java)
            startActivity(intent)
        }

        initializeUserInterface()

        imgButton.setOnClickListener {
            showStatusSelectionDialog()
            isProfileChanged = true
        }

        textSpecialisation.setOnClickListener {
            showSubjectsDialog(textSpecialisation.text.toString().split("\r\n"))
            supervisorProfile.topicCategories = textSpecialisation.text.split("\r\n").toTypedArray()
            isProfileChanged = true
        }

        toggleButtonGerman.setOnCheckedChangeListener {_, isChecked ->
            setLanguage(toggleButtonGerman.isChecked, toggleButtonEnglish.isChecked)
            isProfileChanged = true
        }
        toggleButtonEnglish.setOnCheckedChangeListener { _, isChecked ->
            setLanguage(toggleButtonGerman.isChecked, toggleButtonEnglish.isChecked)
            isProfileChanged = true
        }

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                isProfileChanged = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                supervisorProfile.biography = textBiography.text.toString()
                supervisorProfile.researchTopics = textResearch.text.toString()
                supervisorProfile.topicCategories = textSpecialisation.text.toString().split("\r\n").toTypedArray()
            }
        }

        textBiography.addTextChangedListener(textWatcher)
        textResearch.addTextChangedListener(textWatcher)
        textSpecialisation.addTextChangedListener(textWatcher)

    }

    override fun onPause() {
        super.onPause()
        if (isProfileChanged){
            databaseHelper.setSupervisorProfile(supervisorProfile)
        }
    }

    private fun setLanguage(checkedGerman: Boolean, checkedEnglish: Boolean) {
        val languageList = mutableListOf<String>()

        if (checkedGerman){
            languageList.add(Language.German.toString())
        }

        if (checkedEnglish) {
            languageList.add(Language.English.toString())
        }

        supervisorProfile.languages = languageList.toTypedArray()
    }

    private fun initializeUserInterface() {
        userEmail.text = userProfile.eMail
        userName.text = userProfile.name

        val decodedString: ByteArray = Base64.getDecoder().decode(userProfile.picture.toByteArray())
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        userImage.setImageBitmap(decodedByte)

        if (userProfile.userType == DashboardUserType.supervisor) {
            setAvailability(supervisorProfile.status)
            setLanguages(supervisorProfile.languages)
            textBiography.setText(supervisorProfile.biography)
            textResearch.setText(supervisorProfile.researchTopics)
            textSpecialisation.text = supervisorProfile.topicCategories.joinToString("\r\n")
        }
    }


    private fun setAvailability(status: AvailabilityStatus) {
        supervisorProfile.status = status
        when (status) {
            AvailabilityStatus.free -> {
                imgButton.setImageResource(R.drawable.flag_green)
                textViewStatus.text = getString(R.string.availabilityStatus_free)
            }
            AvailabilityStatus.blocked -> {
                imgButton.setImageResource(R.drawable.flag_red)
                textViewStatus.text = getString(R.string.availabilityStatus_blocked)
            }
            AvailabilityStatus.limited -> {
                imgButton.setImageResource(R.drawable.flag_yellow)
                textViewStatus.text = getString(R.string.availabilityStatus_limited)
            }
        }
    }
    private fun setAvailability(status: Int) {
        setAvailability(AvailabilityStatus.entries[status])
    }

    private fun setLanguages(languages : Array<String>){
        for (language in languages){
            when (language){
                Language.German.toString() -> {toggleButtonGerman.isChecked = true }
                Language.English.toString() -> {toggleButtonEnglish.isChecked = true }
            }
        }

    }

    private fun showStatusSelectionDialog() {
        val options = arrayOf(getString(R.string.availabilityStatus_free)
            , getString( R.string.availabilityStatus_blocked)
            , getString(R.string.availabilityStatus_limited))
        val icons = arrayOf(R.drawable.flag_green, R.drawable.flag_red, R.drawable.flag_yellow)

        val adapter = object : ArrayAdapter<String>(this, R.layout.availability_status_item, options) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val inflater = LayoutInflater.from(context)
                val view = convertView ?: inflater.inflate(R.layout.availability_status_item, parent, false)

                val icon = view.findViewById<ImageView>(R.id.availabilityStatusImage)
                val text = view.findViewById<TextView>(R.id.availabilityStatusText)

                icon.setImageResource(icons[position])
                text.text = options[position]

                return view
            }
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose status")
        builder.setAdapter(adapter) { dialog, which ->
            setAvailability(which)
        }
        builder.show()
    }

    private fun showSubjectsDialog(listedItems : List<String>) {
        val databaseHelper = DatabaseHelper(this)
        val selectedSubjects = mutableListOf<String>()
        val subjects = databaseHelper.getAllSpecialisations()
        val checkedItems = BooleanArray(subjects.size)

        for (element in listedItems){
            for (subject in subjects){
                if (subject.equals(element, true)){
                    checkedItems[subjects.indexOf(subject)] = true
                }
            }
        }

        for (i in checkedItems.indices) {
            if (checkedItems[i]) {
                selectedSubjects.add(subjects[i])
            }
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose your specialisations")
        builder.setMultiChoiceItems(subjects, checkedItems) { _, which, isChecked ->
            if (isChecked) {
                selectedSubjects.add(subjects[which])
            } else {
                selectedSubjects.remove(subjects[which])
            }
        }
        builder.setPositiveButton("OK") { _, _ ->
            if (selectedSubjects.isEmpty()) {
                Toast.makeText(this, "Please choose at least one specialisation", Toast.LENGTH_SHORT).show()
            } else {
                textSpecialisation.text =  selectedSubjects.joinToString("\r\n")
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
}