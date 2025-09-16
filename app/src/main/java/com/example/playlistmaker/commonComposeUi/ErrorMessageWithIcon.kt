package com.example.playlistmaker.commonComposeUi

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.composeAppTheme.AppTheme

@Composable
fun ErrorMessageWithIcon(message: String, iconId: Int, topPaddingDp: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPaddingDp.dp)
    ) {
        Image(
            painter = painterResource(iconId),
            contentDescription = message,
            modifier = Modifier.size(120.dp),
        )
        Text(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 24.dp),
            text = message,
            style = AppTheme.typography.h3,
            textAlign = TextAlign.Center
        )
    }
}