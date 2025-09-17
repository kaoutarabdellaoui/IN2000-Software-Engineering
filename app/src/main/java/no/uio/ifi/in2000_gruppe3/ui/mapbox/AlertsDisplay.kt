package no.uio.ifi.in2000_gruppe3.ui.mapbox

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.mapbox.geojson.Point
import no.uio.ifi.in2000_gruppe3.data.metAlertsAPI.models.Geometry
import no.uio.ifi.in2000_gruppe3.ui.screens.homeScreen.HomeScreenViewModel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun AlertsDisplay(
    homeScreenViewModel: HomeScreenViewModel,
    mapboxViewModel: MapboxViewModel
) {
    val homeScreenUiState = homeScreenViewModel.homeScreenUIState.collectAsState().value
    val mapboxUiState = mapboxViewModel.mapboxUIState.collectAsState().value
    val pointerCoordinates = mapboxUiState.pointerCoordinates

    val showAlertInfo = remember { mutableStateOf(false) }

    val closestAlertWithDistance = homeScreenUiState.alerts?.features
        ?.mapNotNull { feature ->
            val coords = getFirstCoordinate(feature.geometry)
            coords?.let { firstFeature ->
                Pair(feature, calculateDistance(firstFeature, pointerCoordinates))
            }
        }
        ?.minByOrNull { it.second }

    val closestAlert = closestAlertWithDistance?.first
    val distance = closestAlertWithDistance?.second

    if (closestAlert == null || distance == null) return

    val alertEvent = closestAlert.properties.event?.lowercase()
    val alertColor = closestAlert.properties.riskMatrixColor?.lowercase()

    val radius = when (closestAlert.properties.severity?.lowercase()) {
        "extreme" -> 70.0
        "severe" -> 50.0
        else -> 30.0
    }

    if (distance < radius) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(6.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { showAlertInfo.value = !showAlertInfo.value }
                )
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(getAlertsIconUrl(alertEvent, alertColor))
                        .decoderFactory(SvgDecoder.Factory())
                        .build()
                ),
                contentDescription = "Alert icon",
                modifier = Modifier.size(50.dp)
            )

            if (showAlertInfo.value) {
                Card(
                    shape = MaterialTheme.shapes.medium,
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = """
                            ${closestAlert.properties.area ?: "Ukjent område"}
                            ${closestAlert.properties.description ?: "Ingen beskrivelse tilgjengelig"}
                            Konsekvenser: ${closestAlert.properties.consequences ?: "Ingen konsekvenser tilgjengelig"}
                            Alvorlighetsgrad: ${closestAlert.properties.severity ?: "Ukjent alvorlighetsgrad"}
                            Risiko: ${closestAlert.properties.certainty ?: "Ukjent risiko"}
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

private fun getAlertsIconUrl(event: String?, riskMatrixColor: String?): String {
    if (event.isNullOrEmpty() || riskMatrixColor.isNullOrEmpty()) {
        Log.d("AlertsDisplay", "getAlertsIconUrl: event eller color")
        return "null"
    }
    val eventCode = getEventCode(event)
    return "https://raw.githubusercontent.com/nrkno/yr-warning-icons/master/design/svg/icon-warning-$eventCode-$riskMatrixColor.svg"
}

// Returns the event code based on the event name
private fun getEventCode(event: String?): String? {
    return when (event) {
        "blowingsnow" -> "snow"
        "gale" -> "wind"
        "icing", "unknown" -> "generic"
        else -> event
    }
}

// Returns the first coordinate of the alert feature
private fun getFirstCoordinate(geometry: Geometry): Pair<Double, Double>? {
    return when (geometry) {
        is Geometry.Polygon -> {
            geometry.coordinates.firstOrNull()?.firstOrNull()?.let {
                Pair(it[0], it[1])
            }
        }
        is Geometry.MultiPolygon -> {
            geometry.coordinates.firstOrNull()?.firstOrNull()?.firstOrNull()?.let {
                Pair(it[0], it[1])
            }
        }
    }
}

// Calculate distance between coordinate from alert and pointer coordinates
fun calculateDistance(featureCoordinates: Pair<Double, Double>, pointerCoordinates: Point?): Double {
    val r = 6371.0 // Radius of the Earth in km

    val featureLatitudeRadians = Math.toRadians(featureCoordinates.second)
    val featureLongitudeRadians = Math.toRadians(featureCoordinates.first)

    val pointerLatitudeRadians = Math.toRadians(pointerCoordinates?.latitude() ?: Double.NEGATIVE_INFINITY)
    val pointerLongitudeRadians = Math.toRadians(pointerCoordinates?.longitude() ?: Double.NEGATIVE_INFINITY)

    val latitudeDelta = pointerLatitudeRadians - featureLatitudeRadians
    val longitudeDelta = pointerLongitudeRadians - featureLongitudeRadians

    val a = sin(latitudeDelta / 2).pow(2) + cos(featureLatitudeRadians) * cos(pointerLatitudeRadians) * sin(longitudeDelta / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return r * c // Distance in km
}
