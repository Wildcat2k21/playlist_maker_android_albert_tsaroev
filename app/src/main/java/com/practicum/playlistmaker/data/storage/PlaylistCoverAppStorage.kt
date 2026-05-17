package com.practicum.playlistmaker.data.storage

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.IOException
import java.util.UUID

object PlaylistCoverAppStorage {
    private const val COVERS_DIR = "playlist_covers"

    fun copyToInternalStorage(context: Context, sourceUri: Uri): String? {
        val coversDirectory = File(context.filesDir, COVERS_DIR).apply { mkdirs() }
        val extension = context.contentResolver.getType(sourceUri)
            ?.substringAfterLast('/')
            ?.takeIf { it.isNotBlank() }
            ?: "jpg"
        val destinationFile = File(coversDirectory, "${UUID.randomUUID()}.$extension")

        return try {
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                destinationFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: return null
            destinationFile.absolutePath
        } catch (_: IOException) {
            null
        }
    }
}