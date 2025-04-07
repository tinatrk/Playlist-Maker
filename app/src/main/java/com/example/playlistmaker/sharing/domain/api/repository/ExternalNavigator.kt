package com.example.playlistmaker.sharing.domain.api.repository

import com.example.playlistmaker.sharing.domain.models.EmailData

interface ExternalNavigator {

    fun shareLink(link: String)

    fun sendMail(emailData: EmailData)

    fun openUrl(url: String)
}