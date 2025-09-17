package no.uio.ifi.in2000_gruppe3.ui.navigation

// Class for navController to navigate between screens
sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Home : Screen("home")
    object Favorites : Screen("favorites")
    object HikeScreen : Screen("hikeScreen")
    object Chatbot : Screen("chatbot")

    object Profile : Screen("profile")
    object ProfileSelect: Screen("profileSelect")
    object Settings : Screen("settings")

    object MapPreview : Screen("mapPreview")

    object LocationForecast : Screen("locationForecast")
    object LocationForecastDetailed : Screen("locationForecastDetailed")
}