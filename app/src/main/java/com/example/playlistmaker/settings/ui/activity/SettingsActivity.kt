package com.example.playlistmaker.settings.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.presentation.model.SettingsScreenState
import com.example.playlistmaker.settings.presentation.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    private val viewModel: SettingsViewModel by viewModels<SettingsViewModel> {
        SettingsViewModel.getViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.settingsScreen) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbarSettings.setNavigationOnClickListener {
            finish()
        }

        viewModel.getScreenStateLiveData().observe(this) { screenState ->
            when (screenState) {
                is SettingsScreenState.Default -> {
                    binding.switchModeSettings.isChecked = screenState.isDefaultThemeDark
                }
            }
        }

        binding.switchModeSettings.setOnCheckedChangeListener { _, isChecked ->
            viewModel.changeTheme(isChecked)
        }

        binding.tvShareAppSetting.setOnClickListener {
            viewModel.shareApp(getString(R.string.share_app_link))
        }

        binding.tvSupportSettings.setOnClickListener {
            viewModel.openSupport(
                getString(R.string.support_target_mail),
                getString(R.string.support_subject_mail),
                getString(R.string.support_message_mail)
            )
        }

        binding.tvUserAgreementSettings.setOnClickListener {
            viewModel.openUserAgreement(getString(R.string.user_agreement_link))
        }
    }
}