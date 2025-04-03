package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.sharing.domain.api.interactor.ExternalNavigatorInteractor
import com.example.playlistmaker.sharing.domain.api.repository.ExternalNavigator
import com.example.playlistmaker.sharing.domain.models.EmailData

class ExternalNavigatorInteractorImpl(private val navigator: ExternalNavigator) :
    ExternalNavigatorInteractor {
    override fun openUserAgreement(url: String) {
        navigator.openUrl(url)
    }

    override fun shareApp(link: String) {
        navigator.shareLink(link)
    }

    override fun openSupport(
        targetMail: String,
        subjectMail: String,
        messageMail: String
    ) {
        navigator.sendMail(EmailData(targetMail, subjectMail, messageMail))
    }
}