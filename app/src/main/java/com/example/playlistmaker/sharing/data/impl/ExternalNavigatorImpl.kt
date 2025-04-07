package com.example.playlistmaker.sharing.data.impl

import android.app.Application
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.domain.api.repository.ExternalNavigator
import com.example.playlistmaker.sharing.domain.models.EmailData

class ExternalNavigatorImpl(private val application: Application) : ExternalNavigator {

    override fun shareLink(link: String) {
        val intent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, link)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(intent, null)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(shareIntent)

    }

    override fun sendMail(emailData: EmailData) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(
            Intent.EXTRA_EMAIL,
            arrayOf(emailData.targetMail)
        )
        intent.putExtra(Intent.EXTRA_SUBJECT, emailData.subjectMail)
        intent.putExtra(Intent.EXTRA_TEXT, emailData.messageMail)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(intent)
    }

    override fun openUrl(url: String) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(intent)
    }
}