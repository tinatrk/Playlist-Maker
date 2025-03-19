package com.example.playlistmaker.data.externalNavigator

import android.app.Application
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.domain.api.repository.ExternalNavigator

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

    override fun sendMail(targetMail: String, subjectMail: String, messageMail: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(
            Intent.EXTRA_EMAIL,
            arrayOf(targetMail)
        )
        intent.putExtra(Intent.EXTRA_SUBJECT, subjectMail)
        intent.putExtra(Intent.EXTRA_TEXT, messageMail)
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