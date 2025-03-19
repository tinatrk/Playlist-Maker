package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.interactor.ExternalNavigatorInteractor
import com.example.playlistmaker.domain.api.repository.ExternalNavigator

class ExternalNavigatorInteractorImpl(private val navigator: ExternalNavigator) :
    ExternalNavigatorInteractor {
    override fun openUrl(url: String) {
        navigator.openUrl(url)
    }

    override fun shareLink(link: String) {
        navigator.shareLink(link)
    }

    override fun sendMail(
        targetMail: String,
        subjectMail: String,
        messageMail: String
    ) {
        navigator.sendMail(targetMail, subjectMail, messageMail)
    }
}