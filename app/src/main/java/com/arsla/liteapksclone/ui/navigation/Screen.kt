package com.arsla.liteapksclone.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object Search : Screen("search", "Search")
    object Downloads : Screen("downloads", "Downloads")
    object Settings : Screen("settings", "Settings")
    object About : Screen("about", "About")
    object Detail : Screen("detail/{postId}", "Detail") {
        fun createRoute(postId: Int) = "detail/$postId"
    }
}
