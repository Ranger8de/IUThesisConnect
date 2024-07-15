package com.dlbcsemse.iuthesisconnect

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
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
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dlbcsemse.iuthesisconnect.helper.DatabaseHelper
import com.dlbcsemse.iuthesisconnect.model.UserProfile
import java.nio.charset.Charset
import java.util.Base64

class ProfileActivity : AppCompatActivity() {
    private lateinit var toolBar : Toolbar
    private lateinit var imgButton: ImageButton
    private lateinit var textViewStatus : TextView
    private lateinit var userProfile : UserProfile
    private lateinit var userName : TextView
    private lateinit var userEmail : TextView
    private lateinit var userImage : ImageView
    private lateinit var textBiography : EditText
    private lateinit var textSpecialisation : TextView
    private lateinit var cardViewBiography : CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val databaseHelper = DatabaseHelper(this)

        userProfile = databaseHelper.getCurrentUser()

        toolBar = findViewById(R.id.profileToolbar)
        imgButton = findViewById<ImageButton>(R.id.profileImageViewAvailableStatus)
        textViewStatus = findViewById<TextView>(R.id.profileTextViewAvailableStatus)
        userEmail = findViewById(R.id.profileTextViewEmail)
        userName = findViewById(R.id.profileTextViewName)
        userImage = findViewById(R.id.profileImageViewImage)
        textBiography = findViewById(R.id.profileEditTextBiography)
        textSpecialisation = findViewById(R.id.profileTextViewSpecializations)
        cardViewBiography = findViewById(R.id.profileCardViewBiography)

        if (userProfile.userType == DashboardUserType.student) {
            imgButton.visibility = View.INVISIBLE
            textViewStatus.visibility = View.INVISIBLE
            cardViewBiography.visibility = View.GONE
        }

        setSupportActionBar(toolBar);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        toolBar.setNavigationOnClickListener {
            finish()
        }

        imgButton.setOnClickListener {
            showStatusSelectionDialog()
        }

        textSpecialisation.setOnClickListener {
            showSubjectsDialog(textSpecialisation.text.toString().split("\r\n"))
        }

        userEmail.text = userProfile.eMail
        userName.text = userProfile.name

        val decodedString: ByteArray = Base64.getDecoder().decode(userProfile.picture.toByteArray())
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        userImage.setImageBitmap(decodedByte)
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
            when (which) {
                0 -> {
                    imgButton.setImageResource(R.drawable.flag_green)
                    textViewStatus.text = getString(R.string.availabilityStatus_free)
                }
                1 -> {
                    imgButton.setImageResource(R.drawable.flag_red)
                    textViewStatus.text = getString(R.string.availabilityStatus_blocked)
                }
                2 -> {
                    imgButton.setImageResource(R.drawable.flag_yellow)
                    textViewStatus.text = getString(R.string.availabilityStatus_limited)
                }
            }
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