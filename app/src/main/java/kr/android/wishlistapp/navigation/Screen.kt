package kr.android.wishlistapp.navigation

sealed class Screen(var route : String) {
    object HomeScreen : Screen(route = "home_screen")
    object AddScreen : Screen(route = "add_screen")
}