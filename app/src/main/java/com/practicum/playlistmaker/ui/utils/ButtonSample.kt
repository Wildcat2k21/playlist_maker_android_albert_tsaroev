package com.practicum.playlistmaker.ui.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomButtonSample(
    leadingIcon: IconType? = null,
    leadingIconSize: Int = 24,
    trailingIcon: IconType? = null,
    trailingIconSize: Int = 24,
    horizontalPadding: Int = 28,
    verticalPadding: Int = 20,
    contentDescription: String,
    content: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        content = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                leadingIcon?.let { icon ->
                    CorrectIcon(icon, contentDescription, MaterialTheme.colorScheme.onSurface, leadingIconSize)
                }

                Box(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f)
                ) {
                    content()
                }

                trailingIcon?.let { icon ->
                    CorrectIcon(icon, contentDescription, MaterialTheme.colorScheme.onSurfaceVariant, trailingIconSize)
                }
            }
        },
        shape = MaterialTheme.shapes.extraSmall,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        contentPadding = PaddingValues(vertical = verticalPadding.dp, horizontal = horizontalPadding.dp)
    )
}

@Composable
fun ButtonSample(
    leadingIcon: IconType? = null,
    leadingIconSize: Int = 24,
    trailingIcon: IconType? = null,
    trailingIconSize: Int = 24,
    contentFontSize: Int = 22,
    horizontalPadding: Int = 28,
    verticalPadding: Int = 20,
    contentDescription: String,
    onClick: () -> Unit,
) {
    CustomButtonSample(
        leadingIcon = leadingIcon,
        leadingIconSize = leadingIconSize,
        trailingIcon = trailingIcon,
        trailingIconSize = trailingIconSize,
        horizontalPadding = horizontalPadding,
        verticalPadding = verticalPadding,
        onClick = onClick,
        contentDescription = contentDescription,
        content = {
            Text(
                text = contentDescription,
                fontSize = contentFontSize.sp
            )
        },
    )
}