package com.example.playlistmaker.settings.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.CreatorExternalNavigator
import com.example.playlistmaker.creator.CreatorSettings
import com.example.playlistmaker.settings.domain.api.interactor.SettingsInteractor
import com.example.playlistmaker.settings.presentation.model.SettingsScreenState
import com.example.playlistmaker.sharing.domain.api.interactor.ExternalNavigatorInteractor
import com.example.playlistmaker.util.SingleEventLiveData

class SettingsViewModel(
    private val externalNavigatorInteractor: ExternalNavigatorInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val screenStateLiveData = SingleEventLiveData<SettingsScreenState>()

    init {
        screenStateLiveData.value = SettingsScreenState.Default(getDefaultTheme())
    }

    fun getScreenStateLiveData(): LiveData<SettingsScreenState> = screenStateLiveData

    private fun getDefaultTheme(): Boolean {
        return settingsInteractor.getTheme()
    }

    fun changeTheme(isDarkThemeOn: Boolean) {
        settingsInteractor.setAndSaveTheme(isDarkThemeOn)
    }

    fun shareApp(shareAppLink: String) {
        externalNavigatorInteractor.shareApp(shareAppLink)
    }

    fun openSupport(targetMail: String, subjectMail: String, messageMail: String) {
        externalNavigatorInteractor.openSupport(targetMail, subjectMail, messageMail)
    }

    fun openUserAgreement(url: String) {
        externalNavigatorInteractor.openUserAgreement(url)
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(
                    CreatorExternalNavigator.provideExternalNavigatorInteractor(),
                    CreatorSettings.provideSettingsInteractor()
                )
            }
        }

    }

}