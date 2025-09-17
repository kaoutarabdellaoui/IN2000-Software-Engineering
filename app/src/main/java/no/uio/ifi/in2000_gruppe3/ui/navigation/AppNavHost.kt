package no.uio.ifi.in2000_gruppe3.ui.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000_gruppe3.ui.mapbox.MapPreviewScreen
import no.uio.ifi.in2000_gruppe3.ui.mapbox.MapboxViewModel
import no.uio.ifi.in2000_gruppe3.ui.screens.chatbotScreen.ChatbotScreen
import no.uio.ifi.in2000_gruppe3.ui.screens.chatbotScreen.OpenAIViewModel
import no.uio.ifi.in2000_gruppe3.ui.screens.favoriteScreen.FavoriteScreen
import no.uio.ifi.in2000_gruppe3.ui.screens.favoriteScreen.FavoritesScreenViewModel
import no.uio.ifi.in2000_gruppe3.ui.screens.favoriteScreen.FavoritesScreenViewModelFactory
import no.uio.ifi.in2000_gruppe3.ui.screens.hikeCardScreen.HikeScreen
import no.uio.ifi.in2000_gruppe3.ui.screens.hikeCardScreen.HikeScreenViewModel
import no.uio.ifi.in2000_gruppe3.ui.screens.homeScreen.HomeScreen
import no.uio.ifi.in2000_gruppe3.ui.screens.homeScreen.HomeScreenViewModel
import no.uio.ifi.in2000_gruppe3.ui.screens.locationForecast.LocationForecastDetailedScreen
import no.uio.ifi.in2000_gruppe3.ui.screens.locationForecast.LocationForecastScreen
import no.uio.ifi.in2000_gruppe3.ui.screens.profile.ProfileScreen
import no.uio.ifi.in2000_gruppe3.ui.screens.profile.activities.ActivityScreenViewModel
import no.uio.ifi.in2000_gruppe3.ui.screens.profile.activities.ActivityScreenViewModelFactory
import no.uio.ifi.in2000_gruppe3.ui.screens.profile.profileSelectScreen.ProfileScreenViewModel
import no.uio.ifi.in2000_gruppe3.ui.screens.profile.profileSelectScreen.ProfileSelectScreen
import no.uio.ifi.in2000_gruppe3.ui.screens.settings.ProfileSettingsScreen
import no.uio.ifi.in2000_gruppe3.ui.screens.welcome.WelcomeScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    // ViewModels
    val homeScreenViewModel: HomeScreenViewModel = viewModel()
    val hikeScreenViewModel: HikeScreenViewModel = viewModel()
    val favoritesViewModel: FavoritesScreenViewModel = viewModel(
        factory = FavoritesScreenViewModelFactory(
            application = LocalContext.current.applicationContext as Application,
            openAIViewModel = OpenAIViewModel()
        )
    )
    val mapboxViewModel: MapboxViewModel = viewModel()
    val openAIViewModel: OpenAIViewModel = viewModel()
    val profileScreenViewModel: ProfileScreenViewModel = viewModel()
    val activityScreenViewModel: ActivityScreenViewModel = viewModel(
        factory = ActivityScreenViewModelFactory(
            application = LocalContext.current.applicationContext as Application,
            openAIViewModel = OpenAIViewModel()
        )
    )

    val profileUIState by profileScreenViewModel.profileScreenUIState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        // Welcome screen
        composable(Screen.Welcome.route) {
            LaunchedEffect(profileUIState.isLoggedIn) {
                if (profileUIState.isLoggedIn) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            }
            WelcomeScreen(
                navController = navController
            )
        }

        // Home screen
        composable(Screen.Home.route) {
            HomeScreen(
                homeScreenViewModel = homeScreenViewModel,
                hikeScreenViewModel = hikeScreenViewModel,
                favoritesViewModel = favoritesViewModel,
                mapboxViewModel = mapboxViewModel,
                openAIViewModel = openAIViewModel,
                activityScreenViewModel = activityScreenViewModel,
                navController = navController
            )
        }

        // Favorites screen
        composable(Screen.Favorites.route) {
            FavoriteScreen(
                favoritesViewModel = favoritesViewModel,
                hikeScreenViewModel = hikeScreenViewModel,
                mapboxViewModel = mapboxViewModel,
                navController = navController
            )
        }

        // HikeCard screen
        composable(Screen.HikeScreen.route) {
            HikeScreen(
                homeScreenViewModel = homeScreenViewModel,
                hikeScreenViewModel = hikeScreenViewModel,
                favoritesViewModel = favoritesViewModel,
                mapboxViewModel = mapboxViewModel,
                openAIViewModel = openAIViewModel,
                activityScreenViewModel = activityScreenViewModel,
                navController = navController
            )
        }

        // Location forecast screen
        composable(Screen.LocationForecast.route) {
            LocationForecastScreen(
                homeScreenViewModel = homeScreenViewModel,
                hikeScreenViewModel = hikeScreenViewModel,
                navController = navController
            )
        }

        // Location forecast detailed screen
        composable(Screen.LocationForecastDetailed.route) {
            LocationForecastDetailedScreen(
                homeScreenViewModel = homeScreenViewModel,
                hikeScreenViewModel = hikeScreenViewModel,
                navController = navController
            )
        }

        // Chatbot screen
        composable(Screen.Chatbot.route) {
            ChatbotScreen(
                homeScreenViewModel = homeScreenViewModel,
                hikeScreenViewModel = hikeScreenViewModel,
                mapboxViewModel = mapboxViewModel,
                navController = navController
            )
        }

        // User screen
        composable(Screen.Profile.route) {
            ProfileScreen(
                hikeScreenViewModel = hikeScreenViewModel,
                mapboxViewModel = mapboxViewModel,
                activityScreenViewModel = activityScreenViewModel,
                profileScreenViewModel = profileScreenViewModel,
                navController = navController
            )
        }

        // User settings screen
        composable(Screen.Settings.route) {
            ProfileSettingsScreen(
                mapboxViewModel = mapboxViewModel,
                profileScreenViewModel = profileScreenViewModel,
                navController = navController
            )
        }

        // User profile screen
        composable(Screen.ProfileSelect.route) {
            ProfileSelectScreen(
                profileScreenViewModel = profileScreenViewModel,
                navController = navController
            )
        }

        // Map preview screen
        composable(Screen.MapPreview.route) {
            MapPreviewScreen(
                mapboxViewModel = mapboxViewModel,
                hikeScreenViewModel = hikeScreenViewModel,
                navController = navController,
            )
        }
    }
}
