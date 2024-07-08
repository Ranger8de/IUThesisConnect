package com.dlbcsemse.iuthesisconnect

import android.app.AlertDialog
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProfilActivity : AppCompatActivity() {
    private lateinit var imgButton: ImageButton
    private lateinit var textViewStatus : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imgButton  = findViewById<ImageButton>(R.id.profileImageViewAvailableStatus)
        textViewStatus = findViewById<TextView>(R.id.profileTextViewAvailableStatus)
        imgButton.setOnClickListener {
            showStatusSelectionDialog()
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
        builder.setTitle("Status wählen")
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
}