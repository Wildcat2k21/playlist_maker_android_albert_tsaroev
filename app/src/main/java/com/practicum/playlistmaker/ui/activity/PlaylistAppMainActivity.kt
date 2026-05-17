package com.practicum.playlistmaker.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.practicum.playlistmaker.ui.navigation.PlaylistAppHost
import com.practicum.playlistmaker.ui.playlist.PlaylistsAppViewModel
import com.practicum.playlistmaker.ui.search.SearchAppViewModel
import com.practicum.playlistmaker.ui.theme.PlaylistMakerTheme
import com.practicum.playlistmaker.ui.theme.ThemeViewModel

class PlaylistAppMainActivity : ComponentActivity() {
    private val searchAppViewModel by viewModels<SearchAppViewModel> {
        SearchAppViewModel.appFactory(applicationContext)
    }
    private val playlistsAppViewModel by viewModels<PlaylistsAppViewModel> {
        PlaylistsAppViewModel.appFactory(applicationContext)
    }
    private val themeViewModel by viewModels<ThemeViewModel> {
        ThemeViewModel.appFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
            PlaylistMakerTheme(darkTheme = isDarkTheme, dynamicColor = false) {
                PlaylistAppHost(
                    searchAppViewModel = searchAppViewModel,
                    playlistsAppViewModel = playlistsAppViewModel,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = { themeViewModel.toggleTheme() },
                )
            }
        }
    }
}