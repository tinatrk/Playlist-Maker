package com.example.playlistmaker.settings.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.api.interactor.SettingsInteractor
import com.example.playlistmaker.sharing.domain.api.interactor.ExternalNavigatorInteractor
import com.example.playlistmaker.util.SingleEventLiveData

class SettingsViewModel(
    private val externalNavigatorInteractor: ExternalNavigatorInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val isThemeDarkLiveData = SingleEventLiveData<Boolean>()

    init {
        val theme = settingsInteractor.getTheme()
        isThemeDarkLiveData.value = theme
    }

    fun getIsThemeDarkLiveData(): LiveData<Boolean> = isThemeDarkLiveData

    fun changeTheme(isDarkThemeOn: Boolean) {
        settingsInteractor.setAndSaveTheme(isDarkThemeOn)
        isThemeDarkLiveData.value = isDarkThemeOn
    }

    fun shareApp(shareAppLink: String) {
        externalNavigatorInteractor.shareLink(shareAppLink)
    }

    fun openSupport(targetMail: String, subjectMail: String, messageMail: String) {
        externalNavigatorInteractor.openSupport(targetMail, subjectMail, messageMail)
    }

    fun openUserAgreement(url: String) {
        externalNavigatorInteractor.openUserAgreement(url)
    }
}