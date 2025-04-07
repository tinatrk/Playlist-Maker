package com.example.playlistmaker.sharing.domain.api.interactor

interface ExternalNavigatorInteractor {
    fun shareApp(link: String)

    fun openSupport(targetMail: String, subjectMail: String, messageMail: String)

    fun openUserAgreement(url: String)
}