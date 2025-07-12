package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.sharing.domain.api.interactor.ExternalNavigatorInteractor
import com.example.playlistmaker.sharing.domain.api.repository.ExternalNavigator
import com.example.playlistmaker.sharing.domain.models.EmailData

class ExternalNavigatorInteractorImpl(private val externalNavigator: ExternalNavigator) :
    ExternalNavigatorInteractor {
    override fun openUserAgreement(url: String) {
        externalNavigator.openUrl(url)
    }

    override fun shareLink(link: String) {
        externalNavigator.shareLink(link)
    }

    override fun openSupport(
        targetMail: String,
        subjectMail: String,
        messageMail: String
    ) {
        externalNavigator.sendMail(EmailData(targetMail, subjectMail, messageMail))
    }
}