package com.example.playlistmaker.settings.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.presentation.view_model.SettingsViewModel
import com.example.playlistmaker.util.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : BindingFragment<FragmentSettingsBinding>() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val requiredActivity = requireActivity()

        viewModel.getIsDefaultThemeDarkLiveData().observe(viewLifecycleOwner) { isDefaultThemeDark ->
            binding.switchModeSettings.isChecked = isDefaultThemeDark

        }

        binding.switchModeSettings.setOnCheckedChangeListener { _, isChecked ->
            viewModel.changeTheme(isChecked)
        }

        binding.tvShareAppSetting.setOnClickListener {
            viewModel.shareApp(requiredActivity.getString(R.string.share_app_link))
        }

        binding.tvSupportSettings.setOnClickListener {
            viewModel.openSupport(
                requiredActivity.getString(R.string.support_target_mail),
                requiredActivity.getString(R.string.support_subject_mail),
                requiredActivity.getString(R.string.support_message_mail)
            )
        }

        binding.tvUserAgreementSettings.setOnClickListener {
            viewModel.openUserAgreement(requiredActivity.getString(R.string.user_agreement_link))
        }
    }
}