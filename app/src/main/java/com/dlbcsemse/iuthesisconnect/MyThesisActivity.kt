package com.dlbcsemse.iuthesisconnect

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MyThesisActivity : AppCompatActivity() {
    // Deklaration der benötigten Variablen
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var currentUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Setzt das Layout für diese Activity
        setContentView(R.layout.activity_my_thesis)

        // Initialisiert den DatabaseHelper
        dbHelper = DatabaseHelper.getInstance(this)

        // Setzt eine Beispiel-Benutzer-ID
        // Dies würde dann aus dem Login-Prozess kommen
        currentUserId = "user123"

        // Speichert Beispieldaten für den aktuellen Benutzer
        dbHelper.saveData(currentUserId, "MyThesisActivity", "title", "Meine Abschlussarbeit")
        dbHelper.saveData(currentUserId, "MyThesisActivity", "supervisor", "Prof. Dr. Mustermann")

        // Ruft die gespeicherten Daten ab
        val title = dbHelper.getData(currentUserId, "MyThesisActivity", "title")
        val supervisor = dbHelper.getData(currentUserId, "MyThesisActivity", "supervisor")

        // Gibt die abgerufenen Daten aus
        println("Titel: $title")
        println("Betreuer: $supervisor")

        // Ruft alle Daten für den aktuellen Benutzer ab und gibt sie aus
        val allData = dbHelper.getAllDataForUser(currentUserId)
        for ((className, data) in allData) {
            println("Daten für $className:")
            for ((key, value) in data) {
                println("  $key: $value")
            }
        }
    }
}