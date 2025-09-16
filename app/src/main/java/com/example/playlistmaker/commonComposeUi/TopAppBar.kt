package com.example.playlistmaker.commonComposeUi

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.composeAppTheme.AppTheme

@Composable
fun Toolbar(title: String) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = AppTheme.typography.h2
            )
        },
        backgroundColor = AppTheme.colors.colorPrimary,
        elevation = 0.dp,
    )
}