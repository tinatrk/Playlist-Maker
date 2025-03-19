package com.example.playlistmaker.domain.api.repository

interface ExternalNavigator {

    fun shareLink(link: String)

    fun sendMail(targetMail: String, subjectMail: String, messageMail: String)

    fun openUrl(url: String)
}