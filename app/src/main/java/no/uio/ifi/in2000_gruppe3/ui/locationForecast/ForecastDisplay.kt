package no.uio.ifi.in2000_gruppe3.ui.locationForecast

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import no.uio.ifi.in2000_gruppe3.data.date.getCurrentTime
import no.uio.ifi.in2000_gruppe3.data.date.getTodaysDate
import no.uio.ifi.in2000_gruppe3.ui.screens.homeScreen.HomeScreenViewModel

@Composable
fun ForecastDisplay(
    homeScreenViewModel: HomeScreenViewModel,
    modifier: Modifier = Modifier,
    timeseries: String = "instant", // instant as standard
    date: String = getTodaysDate(), // todays date as standard
    showTemperature: Boolean = true
) {
    val homeScreenUiState = homeScreenViewModel.homeScreenUIState.collectAsState().value

    val currentTime = getCurrentTime()
    val todaysDate = getTodaysDate()

    if (homeScreenUiState.forecast != null) {
        val chosenTimeSeries = when (timeseries) {
            "instant" -> homeScreenUiState.forecast.properties.timeseries.firstOrNull()
            else -> {
                if (todaysDate == date && currentTime > timeseries) {
                    homeScreenUiState.forecast.properties.timeseries.firstOrNull()
                } else {
                    homeScreenUiState.forecast.properties.timeseries.find { it.time == "${date}T${timeseries}Z" }
                }
            }
        }

        val temperature = chosenTimeSeries?.data?.instant?.details?.air_temperature

        val symbolCode = when (timeseries) {
            "instant" -> chosenTimeSeries?.data?.next_1_hours?.summary?.symbol_code
            else -> chosenTimeSeries?.data?.next_6_hours?.summary?.symbol_code
        }
        val iconURL = getWeatherIconUrl(symbolCode.toString())

        if (temperature != null) {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = rememberAsyncImagePainter(iconURL),
                    contentDescription = "Vær-ikon",
                    modifier = Modifier.size(60.dp)
                )
                if (showTemperature) {
                    Text(
                        text = "$temperature°C",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

fun getWeatherIconUrl(symbolCode: String): String {
    if (symbolCode.isEmpty()) {
        Log.d("ForecastDisplay", "Symbol code error")
        return "null"
    }
    return "https://raw.githubusercontent.com/metno/weathericons/refs/heads/main/weather/png/$symbolCode.png"
}
