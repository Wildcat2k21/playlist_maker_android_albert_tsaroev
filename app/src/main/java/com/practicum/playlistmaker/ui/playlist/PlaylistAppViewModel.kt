package com.practicum.playlistmaker.ui.playlist

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.creator.DependencyProvider
import com.practicum.playlistmaker.data.network.PlaylistApp
import com.practicum.playlistmaker.domain.api.PlaylistsAppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class PlaylistAppViewModel(
    playlistsRepository: PlaylistsAppRepository,
    playlistId: Long,
) : ViewModel() {

    val playlist: StateFlow<PlaylistApp?> = playlistsRepository
        .getPlaylist(playlistId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    companion object {
        fun appFactory(playlistId: Long, context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val repo: PlaylistsAppRepository = DependencyProvider.providePlaylistsRepository(context)
                    return PlaylistAppViewModel(repo, playlistId) as T
                }
            }
    }
}