package com.example.playlistmaker.commonComposeUi

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.composeAppTheme.AppTheme

@Composable
fun CommonButton(title: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(54.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = AppTheme.colors.colorOnPrimary,
            contentColor = AppTheme.colors.colorOnPrimary
        )
    ) {
        Text(text = title, style = AppTheme.typography.button2, fontWeight = FontWeight(500))
    }
}