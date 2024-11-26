package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings_screen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack = findViewById<ImageView>(R.id.img_view_back_settings_screen)
        btnBack.setOnClickListener{
            finish()
        }

        val themeSwitch = findViewById<SwitchCompat>(R.id.switch_mode)
        val sharedPreferences = getSharedPreferences(SAVE,MODE_PRIVATE)
        themeSwitch.setChecked(sharedPreferences.getBoolean("value",true))

        themeSwitch.setOnClickListener {
            if (themeSwitch.isChecked) {
                val editor = getSharedPreferences(SAVE, MODE_PRIVATE).edit()
                    .putBoolean("value", true).apply()
                themeSwitch.setChecked(true)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                val editor = getSharedPreferences(SAVE, MODE_PRIVATE).edit()
                    .putBoolean("value", false).apply()
                themeSwitch.setChecked(false)
                AppCompatDelegate.setDefaultNightMode (AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private companion object{
        const val SAVE = "save"
    }
}