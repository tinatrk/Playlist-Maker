package com.example.playlistmaker.settings.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.commonComposeUi.Toolbar
import com.example.playlistmaker.composeAppTheme.AppTheme
import com.example.playlistmaker.settings.presentation.view_model.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val isThemeDarkState by viewModel.getIsThemeDarkLiveData().observeAsState()

    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(R.string.settings_screen_title)
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = AppTheme.colors.colorPrimary)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            SettingsItemWithSwitch(
                title = stringResource(R.string.dark_mode),
                isChecked = isThemeDarkState ?: false,
                onClick = { isDarkThemeOn -> viewModel.changeTheme(isDarkThemeOn) }
            )

            val shareAppLink = stringResource(R.string.share_app_link)
            SettingsItem(
                title = stringResource(R.string.share_app),
                iconId = R.drawable.ic_share_24
            ) {
                viewModel.shareApp(shareAppLink)
            }

            val targetMail = stringResource(R.string.support_target_mail)
            val subjectMail = stringResource(R.string.support_subject_mail)
            val messageMail = stringResource(R.string.support_message_mail)
            SettingsItem(
                title = stringResource(R.string.support),
                iconId = R.drawable.ic_support_24
            ) {
                viewModel.openSupport(
                    targetMail = targetMail,
                    subjectMail = subjectMail,
                    messageMail = messageMail
                )
            }

            val userAgreementLink = stringResource(R.string.user_agreement_link)
            SettingsItem(
                title = stringResource(R.string.user_agreement),
                iconId = R.drawable.ic_arrow_forward_24
            ) {
                viewModel.openUserAgreement(userAgreementLink)
            }
        }
    }
}

@Composable
fun SettingsItemWithSwitch(title: String, isChecked: Boolean, onClick: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f),
            text = title,
            style = AppTheme.typography.h4
        )
        Switch(
            modifier = Modifier.padding(end = 6.dp),
            checked = isChecked,
            onCheckedChange = {
                onClick(it)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = colorResource(R.color.blue_700),
                checkedTrackColor = colorResource(R.color.blue_200),
                uncheckedThumbColor = colorResource(R.color.gray_400),
                uncheckedTrackColor = colorResource(R.color.gray_50)
            )
        )
    }
}

@Composable
fun SettingsItem(title: String, iconId: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable {
                onClick()
            },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f),
            text = title,
            style = AppTheme.typography.h4
        )
        Icon(
            modifier = Modifier.padding(end = 12.dp),
            painter = painterResource(iconId),
            contentDescription = null,
            tint = AppTheme.colors.colorOnTertiary
        )
    }
}
