package com.practicum.playlistmaker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.practicum.playlistmaker.data.network.TrackApp
import com.practicum.playlistmaker.ui.favorites.FavoritesScreen
import com.practicum.playlistmaker.ui.main.MenuScreen
import com.practicum.playlistmaker.ui.player.PlayerAppScreen
import com.practicum.playlistmaker.ui.playlist.CreatePlaylistAppScreen
import com.practicum.playlistmaker.ui.playlist.PlaylistAppScreen
import com.practicum.playlistmaker.ui.playlist.PlaylistAppViewModel
import com.practicum.playlistmaker.ui.playlist.PlaylistsAppScreen
import com.practicum.playlistmaker.ui.playlist.PlaylistsAppViewModel
import com.practicum.playlistmaker.ui.search.SearchAppScreen
import com.practicum.playlistmaker.ui.search.SearchAppViewModel
import com.practicum.playlistmaker.ui.settings.SettingsScreen

enum class Screen(val route: String) {
    MAIN_MENU("main_menu"),
    SEARCH("search"),
    SETTINGS("settings"),
    PLAYLISTS("playlists"),
    FAVORITES("favorites"),
    CREATE_PLAYLIST("create_playlist"),
    PLAYLIST_SCREEN("playlist_screen"),
    PLAYER("player"),
}

@Composable
fun PlaylistAppHost(
    startDestination: String = Screen.MAIN_MENU.route,
    navController: NavHostController = rememberNavController(),
    searchAppViewModel: SearchAppViewModel,
    playlistsAppViewModel: PlaylistsAppViewModel,
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
) {
    val modifier = Modifier
    val context = LocalContext.current

    fun openPlayer(track: TrackApp) {
        PlayerNavigationArgs.pendingTrack = track
        navController.navigate(Screen.PLAYER.route)
    }

    NavHost(navController, startDestination) {
        composable(route = Screen.MAIN_MENU.route) {
            MenuScreen(
                onSearchClick = { navController.navigate(Screen.SEARCH.route) },
                onSettingsClick = { navController.navigate(Screen.SETTINGS.route) },
                onPlaylistsClick = { navController.navigate(Screen.PLAYLISTS.route) },
                onFavoritesClick = { navController.navigate(Screen.FAVORITES.route) },
            )
        }
        composable(route = Screen.SEARCH.route) {
            SearchAppScreen(
                modifier = modifier,
                searchAppViewModel = searchAppViewModel,
                onTrackClick = { track -> openPlayer(track) },
                onBackClick = {
                    searchAppViewModel.clearSearch()
                    navController.popBackStack()
                },
            )
        }
        composable(route = Screen.SETTINGS.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
            )
        }
        composable(route = Screen.PLAYLISTS.route) {
            PlaylistsAppScreen(
                modifier = modifier,
                playlistsAppViewModel = playlistsAppViewModel,
                addNewPlaylist = { navController.navigate(Screen.CREATE_PLAYLIST.route) },
                navigateToPlaylist = { playlistId ->
                    navController.navigate("${Screen.PLAYLIST_SCREEN.route}/$playlistId")
                },
                navigateBack = { navController.popBackStack() },
            )
        }
        composable(route = Screen.FAVORITES.route) {
            FavoritesScreen(
                modifier = modifier,
                playlistsAppViewModel = playlistsAppViewModel,
                onTrackClick = { track -> openPlayer(track) },
                navigateBack = { navController.popBackStack() },
            )
        }
        composable(route = Screen.CREATE_PLAYLIST.route) {
            CreatePlaylistAppScreen(
                modifier = modifier,
                playlistsAppViewModel = playlistsAppViewModel,
                navigateBack = { navController.popBackStack() },
            )
        }
        composable(
            route = "${Screen.PLAYLIST_SCREEN.route}/{playlistId}",
            arguments = listOf(
                navArgument("playlistId") {
                    type = NavType.LongType
                },
            ),
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: 0L
            val playlistViewModel: PlaylistAppViewModel = viewModel(
                key = "playlist_$playlistId",
                factory = PlaylistAppViewModel.appFactory(playlistId, context),
            )
            PlaylistAppScreen(
                modifier = modifier,
                playlistViewModel = playlistViewModel,
                playlistsAppViewModel = playlistsAppViewModel,
                onTrackClick = { track -> openPlayer(track) },
                navigateBack = { navController.popBackStack() },
            )
        }
        composable(route = Screen.PLAYER.route) {
            PlayerAppScreen(
                modifier = modifier,
                playlistsAppViewModel = playlistsAppViewModel,
                navigateBack = { navController.popBackStack() },
            )
        }
    }
}