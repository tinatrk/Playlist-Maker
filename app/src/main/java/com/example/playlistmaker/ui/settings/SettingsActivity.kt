package com.example.playlistmaker.ui.settings

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.CreatorExternalNavigator
import com.example.playlistmaker.creator.CreatorSettings
import com.example.playlistmaker.ui.App
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    private val settingsInteractor = CreatorSettings.provideSettingsInteractor()
    private val externalNavigatorInteractor =
        CreatorExternalNavigator.provideExternalNavigatorInteractor()

    private lateinit var themeSwitch: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings_screen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        themeSwitch = findViewById(R.id.switch_mode_settings)
        setCurrentTheme()
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            (applicationContext as App).switchTheme(isChecked)
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar_settings)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val btnShareApp = findViewById<TextView>(R.id.tv_share_app_setting)
        btnShareApp.setOnClickListener {
            externalNavigatorInteractor.shareLink(getString(R.string.share_app_link))
        }

        val btnSupport = findViewById<TextView>(R.id.tv_support_settings)
        btnSupport.setOnClickListener {
            externalNavigatorInteractor.sendMail(
                getString(R.string.support_target_mail),
                getString(R.string.support_subject_mail),
                getString(R.string.support_message_mail)
            )
        }

        val btnUserAgreement = findViewById<TextView>(R.id.tv_user_agreement_settings)
        btnUserAgreement.setOnClickListener {
            externalNavigatorInteractor.openUrl(getString(R.string.user_agreement_link))
        }
    }

    private fun setCurrentTheme() {
        val curTheme = settingsInteractor.getTheme()
        themeSwitch.isChecked = curTheme
    }
}