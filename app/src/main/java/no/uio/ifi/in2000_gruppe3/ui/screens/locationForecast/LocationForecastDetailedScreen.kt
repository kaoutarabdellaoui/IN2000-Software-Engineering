package no.uio.ifi.in2000_gruppe3.ui.screens.locationForecast

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import no.uio.ifi.in2000_gruppe3.data.date.getTodaysDay
import no.uio.ifi.in2000_gruppe3.ui.locationForecast.ShowForecastByHour
import no.uio.ifi.in2000_gruppe3.ui.navigation.BottomBar
import no.uio.ifi.in2000_gruppe3.ui.screens.hikeCardScreen.HikeScreenViewModel
import no.uio.ifi.in2000_gruppe3.ui.screens.homeScreen.HomeScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationForecastDetailedScreen(
    homeScreenViewModel: HomeScreenViewModel,
    hikeScreenViewModel: HikeScreenViewModel,
    navController: NavHostController
) {
    val hikeUIState by hikeScreenViewModel.hikeScreenUIState.collectAsState()

    val todaysDay = getTodaysDay()
    val selectedDay = hikeUIState.selectedDay

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    if (selectedDay == todaysDay) "I dag ${hikeUIState.formattedDate}"
                    else "$selectedDay ${hikeUIState.formattedDate}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = { BottomBar(navController = navController) }
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxWidth()
        ) {
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("Tid", "Vær", "Temp.", "Vind", "Luftfuktighet").forEach { header ->
                            Text(
                                text = header,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }

                    // Divider line
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))

                    // Show forecast for each hour
                    (0..23).forEach { hour ->
                        ShowForecastByHour(
                            hour = hour,
                            homeViewModel = homeScreenViewModel,
                            hikeViewModel = hikeScreenViewModel
                        )
                    }
                }
            }
        }
    }
}
