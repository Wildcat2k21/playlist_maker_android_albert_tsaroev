package com.practicum.playlistmaker.data.utils

private val artworkSizeRegex = Regex(
    pattern = """/\d+x\d+(bb)?\.(jpg|jpeg|png)""",
    option = RegexOption.IGNORE_CASE,
)

fun toAppHighResolutionArtwork(url: String?): String {
    val normalizedUrl = url?.trim().orEmpty()
    if (normalizedUrl.isBlank()) return ""
    return normalizedUrl.replaceFirst(artworkSizeRegex, "/512x512bb.jpg")
}