package no.uio.ifi.in2000_gruppe3.ui.mapbox

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import no.uio.ifi.in2000_gruppe3.R
import no.uio.ifi.in2000_gruppe3.ui.screens.hikeCardScreen.HikeScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPreviewScreen(
    navController: NavController,
    hikeScreenViewModel: HikeScreenViewModel,
    mapboxViewModel: MapboxViewModel
) {
    val mapboxUIState by mapboxViewModel.mapboxUIState.collectAsState()
    val hikeScreenUIState by hikeScreenViewModel.hikeScreenUIState.collectAsState()
    val mapViewportState = rememberMapViewportState()
    val feature = hikeScreenUIState.feature

    val redMarker = rememberIconImage(R.drawable.red_marker)
    val blueMarker = rememberIconImage(R.drawable.blue_marker)

    mapViewportState.setCameraOptions {
        center(
            Point.fromLngLat(
                feature.geometry.coordinates[0][0],
                feature.geometry.coordinates[0][1]
            )
        )
        zoom(mapboxUIState.zoom)
        pitch(0.0)
        bearing(0.0)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = feature.properties.desc ?: "Ukjent rutenavn") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        MapboxMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            mapViewportState = mapViewportState,
            style = { MapStyle(mapboxUIState.mapStyle) },
            compass = {},
            logo = {},
            scaleBar = {},
            attribution = {}
        ) {
            PolylineAnnotation(
                points = feature.geometry.coordinates.map { point ->
                    Point.fromLngLat(point[0], point[1])
                },
            ) {
                lineColor = feature.color
                lineWidth = 7.0
                lineOpacity = 0.7
                lineBorderColor = Color.White
                lineBorderWidth = 2.0
            }

            PointAnnotation(
                point = Point.fromLngLat(
                    feature.geometry.coordinates[0][0],
                    feature.geometry.coordinates[0][1]
                )
            ) {
                iconImage = blueMarker
            }
            PointAnnotation(
                point = Point.fromLngLat(
                    feature.geometry.coordinates.last()[0],
                    feature.geometry.coordinates.last()[1]
                ),
            ) {
                iconImage = redMarker
            }

            MapEffect { mapView ->
                mapView.location.updateSettings {
                    locationPuck = createDefault2DPuck(withBearing = true)
                    enabled = true
                }
            }
        }
    }
}
