package no.uio.ifi.in2000_gruppe3.ui.locationForecast

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import no.uio.ifi.in2000_gruppe3.data.date.getCurrentTime
import no.uio.ifi.in2000_gruppe3.data.date.getDateFormatted
import no.uio.ifi.in2000_gruppe3.data.date.getTodaysDate
import no.uio.ifi.in2000_gruppe3.data.date.getTodaysDay
import no.uio.ifi.in2000_gruppe3.ui.navigation.Screen
import no.uio.ifi.in2000_gruppe3.ui.screens.hikeCardScreen.HikeScreenViewModel
import no.uio.ifi.in2000_gruppe3.ui.screens.homeScreen.HomeScreenViewModel

@Composable
fun LocationForecastSmallCard(
    day: String,
    date: String,
    homeScreenViewModel: HomeScreenViewModel,
    hikeScreenViewModel: HikeScreenViewModel,
    navController: NavHostController
) {
    val hikeUIState by hikeScreenViewModel.hikeScreenUIState.collectAsState()

    val todaysDay = getTodaysDay()
    val todaysDate = getTodaysDate()
    val dateFormatted = getDateFormatted(date)
    val currentTime = getCurrentTime()

    val daysHighestTemp = homeScreenViewModel.daysHighestTemp(date)
    val daysLowestTemp = homeScreenViewModel.daysLowestTemp(date)

    val visibleBoxesCount = listOf(
        date > todaysDate || (date == todaysDate && currentTime < "06:00:00"),
        date > todaysDate || (date == todaysDate && currentTime < "12:00:00"),
        date > todaysDate || (date == todaysDate && currentTime < "18:00:00"),
        date > todaysDate || (date == todaysDate && currentTime < "23:59:00")
    ).count { it }

    val timeRanges = listOf("00-06" to "00:00:00", "06-12" to "06:00:00", "12-18" to "12:00:00", "18-00" to "18:00:00")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = {
            hikeScreenViewModel.updateSelectedDay(day)
            hikeScreenViewModel.updateSelectedDate(date)
            if (hikeUIState.selectedDay != day) {
                hikeScreenViewModel.updateDescriptionAlreadyLoaded(false)
            }
            navController.navigate(Screen.LocationForecastDetailed.route)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = if (todaysDay == day) "I dag $dateFormatted" else "$day $dateFormatted",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp)
                    ) {
                        Text(
                            text = "$daysHighestTemp°",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = Color.Red
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = "${daysLowestTemp}°",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = Color.Blue
                        )
                    }

                    VerticalDivider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        modifier = Modifier
                            .width(1.dp)
                            .height(80.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(start = 8.dp)
                    ) {
                        timeRanges.takeLast(visibleBoxesCount).forEach { (label, timeseries) ->
                            Box {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = label)
                                    ForecastDisplay(
                                        homeScreenViewModel = homeScreenViewModel,
                                        timeseries = timeseries,
                                        date = date,
                                        showTemperature = false
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                    }
                }
            }
        }
    }
}
