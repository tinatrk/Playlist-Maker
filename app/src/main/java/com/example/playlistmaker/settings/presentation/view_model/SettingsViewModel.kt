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

    private val isDefaultThemeDarkLiveData = SingleEventLiveData<Boolean>()

    init {
        isDefaultThemeDarkLiveData.value = settingsInteractor.getTheme()
    }

    fun getIsDefaultThemeDarkLiveData(): LiveData<Boolean> = isDefaultThemeDarkLiveData

    fun changeTheme(isDarkThemeOn: Boolean) {
        settingsInteractor.setAndSaveTheme(isDarkThemeOn)
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