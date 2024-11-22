package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSearch = findViewById<Button>(R.id.btn_search)
        val btnClickListener: View.OnClickListener = object: View.OnClickListener{
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Вы нажали на кнопку " +
                        "\"${resources.getString(R.string.search_button_text)}\"!",
                    Toast.LENGTH_SHORT).show()
            }
        }
        btnSearch.setOnClickListener(btnClickListener)

        val btnLibrary = findViewById<Button>(R.id.btn_library)
        btnLibrary.setOnClickListener{
            Toast.makeText(this@MainActivity, "Вы нажали на кнопку " +
                    "\"${resources.getString(R.string.library_button_text)}\"",
                Toast.LENGTH_SHORT).show()
        }

        val btnSettings = findViewById<Button>(R.id.btn_settings)
        btnSettings.setOnClickListener{
            Toast.makeText(this@MainActivity, "Вы нажали на кнопку " +
                    "\"${resources.getString(R.string.settings_button_text)}\"",
                Toast.LENGTH_SHORT).show()
        }
    }
}