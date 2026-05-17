package com.practicum.playlistmaker.ui.utils

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

sealed class IconType {
    data class ImageVectorIcon(val imageVector: ImageVector) : IconType()
    data class PainterIcon(val painter: Painter) : IconType()
}

@Composable
fun CorrectIcon(
    icon: IconType,
    contentDescription: String,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    size: Int = 24,
) {
    when (icon) {
        is IconType.PainterIcon -> Icon(
            modifier = Modifier.size(size.dp),
            painter = icon.painter,
            contentDescription = contentDescription,
            tint = tint,
        )
        is IconType.ImageVectorIcon -> Icon(
            modifier = Modifier.size(size.dp),
            imageVector = icon.imageVector,
            contentDescription = contentDescription,
            tint = tint,
        )
    }
}
