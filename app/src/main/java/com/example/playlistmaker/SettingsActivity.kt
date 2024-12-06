package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
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

        val themeSwitch = findViewById<SwitchCompat>(R.id.switch_mode)
        val sharedPreferences = getSharedPreferences(PREFERENCES_FILE_NAME_SAVE,MODE_PRIVATE)
        themeSwitch.setChecked(sharedPreferences.getBoolean("value",false))

        themeSwitch.setOnClickListener {
            if (themeSwitch.isChecked) {
                getSharedPreferences(PREFERENCES_FILE_NAME_SAVE, MODE_PRIVATE).edit()
                    .putBoolean("value", true).apply()
                themeSwitch.setChecked(true)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                getSharedPreferences(PREFERENCES_FILE_NAME_SAVE, MODE_PRIVATE).edit()
                    .putBoolean("value", false).apply()
                themeSwitch.setChecked(false)
                AppCompatDelegate.setDefaultNightMode (AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar_settings_screen)
        toolbar.setNavigationOnClickListener{
            finish()
        }

        val btnShareApp = findViewById<TextView>(R.id.tw_share_app)
        btnShareApp.setOnClickListener{
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_link))
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        val btnSupport = findViewById<TextView>(R.id.tw_support)
        btnSupport.setOnClickListener{
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_target_mail)))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject_mail))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_message_mail))
            startActivity(supportIntent)
        }

        val btnUserAgreement = findViewById<TextView>(R.id.tw_user_agreement)
        btnUserAgreement.setOnClickListener{
            val userAgreementIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.user_agreement_link)))
            startActivity(userAgreementIntent)
        }

    }

    private companion object {
        const val PREFERENCES_FILE_NAME_SAVE = "save"
    }
}