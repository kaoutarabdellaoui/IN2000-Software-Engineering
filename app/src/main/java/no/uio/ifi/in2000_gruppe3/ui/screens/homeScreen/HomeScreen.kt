package no.uio.ifi.in2000_gruppe3.ui.screens.homeScreen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import no.uio.ifi.in2000_gruppe3.data.date.getTodaysDate
import no.uio.ifi.in2000_gruppe3.data.date.getTodaysDay
import no.uio.ifi.in2000_gruppe3.ui.ai.AanundFigure
import no.uio.ifi.in2000_gruppe3.ui.bottomSheetDrawer.BottomSheetDrawer
import no.uio.ifi.in2000_gruppe3.ui.bottomSheetDrawer.SheetDrawerDetent
import no.uio.ifi.in2000_gruppe3.ui.loaders.MapLoader
import no.uio.ifi.in2000_gruppe3.ui.locationForecast.ForecastDisplay
import no.uio.ifi.in2000_gruppe3.ui.mapSearchbar.SearchBarForMap
import no.uio.ifi.in2000_gruppe3.ui.mapSearchbar.SuggestionColumn
import no.uio.ifi.in2000_gruppe3.ui.mapbox.AlertsDisplay
import no.uio.ifi.in2000_gruppe3.ui.mapbox.MapStyleSelector
import no.uio.ifi.in2000_gruppe3.ui.mapbox.MapViewer
import no.uio.ifi.in2000_gruppe3.ui.mapbox.MapboxViewModel
import no.uio.ifi.in2000_gruppe3.ui.mapbox.ResetMapCenterButton
import no.uio.ifi.in2000_gruppe3.ui.navigation.BottomBar
import no.uio.ifi.in2000_gruppe3.ui.navigation.Screen
import no.uio.ifi.in2000_gruppe3.ui.networkSnackbar.NetworkSnackbar
import no.uio.ifi.in2000_gruppe3.ui.screens.chatbotScreen.OpenAIViewModel
import no.uio.ifi.in2000_gruppe3.ui.screens.favoriteScreen.FavoritesScreenViewModel
import no.uio.ifi.in2000_gruppe3.ui.screens.hikeCardScreen.HikeScreenViewModel
import no.uio.ifi.in2000_gruppe3.ui.screens.profile.activities.ActivityScreenViewModel

@Composable
fun HomeScreen(
    homeScreenViewModel: HomeScreenViewModel,
    favoritesViewModel: FavoritesScreenViewModel,
    hikeScreenViewModel: HikeScreenViewModel,
    mapboxViewModel: MapboxViewModel,
    openAIViewModel: OpenAIViewModel,
    activityScreenViewModel : ActivityScreenViewModel,
    navController: NavHostController
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val locationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {}

    val targetSheetState by homeScreenViewModel.sheetStateTarget.collectAsState()
    val currentSheetOffset by homeScreenViewModel.currentSheetOffset.collectAsState()
    val isControlsVisible = targetSheetState == SheetDrawerDetent.HIDDEN ||
            targetSheetState == SheetDrawerDetent.SEMIPEEK

    LaunchedEffect(Unit) {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NetworkSnackbar(
                snackbarHostState = snackbarHostState,
                coroutineScope = coroutineScope,
                onNetworkStatusChange = { isConnected ->
                    homeScreenViewModel.updateNetworkStatus(isConnected)
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            MapViewer(
                homeScreenViewModel = homeScreenViewModel,
                hikeScreenViewModel = hikeScreenViewModel,
                favoritesViewModel = favoritesViewModel,
                mapboxViewModel = mapboxViewModel,
                activityScreenViewModel = activityScreenViewModel
            )

            MapLoader(
                mapboxViewModel = mapboxViewModel
            )

            Column(
                modifier = Modifier.padding(top = 90.dp)
            ) {
                Card(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(0.85f)
                    ),
                    onClick = {
                        hikeScreenViewModel.updateSelectedDay(getTodaysDay())
                        hikeScreenViewModel.updateSelectedDate(getTodaysDate())
                        navController.navigate(Screen.LocationForecastDetailed.route)
                    }
                ) {
                    ForecastDisplay(
                        homeScreenViewModel = homeScreenViewModel,
                        modifier = Modifier.padding(2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.85f)
                    )
                ) {
                    AlertsDisplay(
                        homeScreenViewModel = homeScreenViewModel,
                        mapboxViewModel = mapboxViewModel
                    )
                }
            }

            if (isControlsVisible) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(y = currentSheetOffset.dp + 20.dp)
                ) {
                    AanundFigure(
                        homeScreenViewModel = homeScreenViewModel,
                        hikeScreenViewModel = hikeScreenViewModel,
                        mapboxViewModel = mapboxViewModel,
                        navController = navController
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 8.dp)
                        .offset(y = currentSheetOffset.dp - 10.dp)
                ) {
                    ResetMapCenterButton(
                        homeScreenViewModel = homeScreenViewModel,
                        mapboxViewModel = mapboxViewModel
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    MapStyleSelector(
                        mapboxViewModel = mapboxViewModel
                    )
                }
            }

            Column {
                SearchBarForMap(
                    mapboxViewModel = mapboxViewModel
                )

                SuggestionColumn(
                    mapboxViewModel = mapboxViewModel
                )
            }

            BottomSheetDrawer(
                homeScreenViewModel = homeScreenViewModel,
                hikeScreenViewModel = hikeScreenViewModel,
                mapboxViewModel = mapboxViewModel,
                openAIViewModel = openAIViewModel,
                navController = navController
            )
        }
    }
}
