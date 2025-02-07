package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.switchmaterial.SwitchMaterial

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

        val themeSwitch = findViewById<SwitchMaterial>(R.id.switch_mode_settings)
        themeSwitch.isChecked = (applicationContext as App) .isDarkTheme
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            (applicationContext as App) .switchTheme(isChecked)
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar_settings)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val btnShareApp = findViewById<TextView>(R.id.tv_share_app_setting)
        btnShareApp.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_link))
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        val btnSupport = findViewById<TextView>(R.id.tv_support_settings)
        btnSupport.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(
                Intent.EXTRA_EMAIL,
                arrayOf(getString(R.string.support_target_mail))
            )
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject_mail))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_message_mail))
            startActivity(supportIntent)
        }

        val btnUserAgreement = findViewById<TextView>(R.id.tv_user_agreement_settings)
        btnUserAgreement.setOnClickListener {
            val userAgreementIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.user_agreement_link))
            )
            startActivity(userAgreementIntent)
        }

    }
}