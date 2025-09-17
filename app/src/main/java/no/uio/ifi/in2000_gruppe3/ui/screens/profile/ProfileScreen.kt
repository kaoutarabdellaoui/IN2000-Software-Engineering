package no.uio.ifi.in2000_gruppe3.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import no.uio.ifi.in2000_gruppe3.ui.mapbox.MapboxViewModel
import no.uio.ifi.in2000_gruppe3.ui.navigation.BottomBar
import no.uio.ifi.in2000_gruppe3.ui.navigation.Screen
import no.uio.ifi.in2000_gruppe3.ui.screens.hikeCardScreen.HikeScreenViewModel
import no.uio.ifi.in2000_gruppe3.ui.screens.profile.activities.Activities
import no.uio.ifi.in2000_gruppe3.ui.screens.profile.activities.ActivityScreenViewModel
import no.uio.ifi.in2000_gruppe3.ui.screens.profile.profileSelectScreen.ProfileScreenViewModel
import no.uio.ifi.in2000_gruppe3.ui.theme.LogoPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    hikeScreenViewModel: HikeScreenViewModel,
    mapboxViewModel: MapboxViewModel,
    profileScreenViewModel: ProfileScreenViewModel,
    activityScreenViewModel: ActivityScreenViewModel,
    navController: NavHostController,
) {
    val profileUIState by profileScreenViewModel.profileScreenUIState.collectAsState()
    var currentView by remember { mutableIntStateOf(0) }

    val logScreenUIState = activityScreenViewModel.activityScreenUIState.collectAsState()

    LaunchedEffect(Unit) {
        activityScreenViewModel.loadActivities()
        activityScreenViewModel.getTotalTimesWalked()
    }

    LaunchedEffect(profileUIState.username) {
        activityScreenViewModel.setUser()
    }

    LaunchedEffect(profileUIState.isLoggedIn) {
        if (!profileUIState.isLoggedIn) {
            navController.navigate(Screen.ProfileSelect.route) {
                popUpTo(Screen.Profile.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Min side",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = { navController.navigate(Screen.Settings.route) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        bottomBar = { BottomBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row {
                Button(
                    modifier = Modifier.weight(1f),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black,
                        disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    onClick = { currentView = 0 }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Turer gjennomført"
                        )
                        Box(
                            modifier = Modifier
                                .height(2.dp)
                                .fillMaxWidth()
                                .background(
                                    if (currentView == 0) LogoPrimary
                                    else Color.Transparent
                                )
                        )
                    }
                }
                Button(
                    modifier = Modifier.weight(1f),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black,
                        disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    onClick = { currentView = 1 }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Statistikk"
                        )
                        Box(
                            modifier = Modifier
                                .height(2.dp)
                                .fillMaxWidth()
                                .background(
                                    if (currentView == 1) LogoPrimary
                                    else Color.Transparent
                                )
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (currentView) {
                    0 -> {
                        Activities(
                            hikeScreenViewModel = hikeScreenViewModel,
                            mapboxViewModel = mapboxViewModel,
                            activityScreenViewModel = activityScreenViewModel,
                            navController = navController
                        )
                    }
                    1 -> {
                        ActivityStats(
                            numTrips = logScreenUIState.value.hikesDone,
                            distanceKm = logScreenUIState.value.totalDistance.let { "%.2f".format(it).toDouble() }
                        )
                    }
                }
            }
        }
    }
}
