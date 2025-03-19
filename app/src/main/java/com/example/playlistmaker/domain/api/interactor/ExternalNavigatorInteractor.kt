package com.example.playlistmaker.domain.api.interactor

interface ExternalNavigatorInteractor {
    fun shareLink(link: String)

    fun sendMail(targetMail: String, subjectMail: String, messageMail: String)

    fun openUrl(url: String)
}