package no.uio.ifi.in2000_gruppe3.ui.hikeCard

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mapbox.geojson.BoundingBox
import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import no.uio.ifi.in2000_gruppe3.BuildConfig
import no.uio.ifi.in2000_gruppe3.R
import no.uio.ifi.in2000_gruppe3.data.hikeAPI.models.Feature
import no.uio.ifi.in2000_gruppe3.ui.mapbox.MapboxUIState
import no.uio.ifi.in2000_gruppe3.ui.mapbox.MapboxViewModel
import no.uio.ifi.in2000_gruppe3.ui.navigation.Screen
import kotlin.math.ln

@Composable
fun HikeCardMapPreview(
    mapboxViewModel: MapboxViewModel,
    feature: Feature,
    navController: NavController? = null
) {
    val mapboxUIState by mapboxViewModel.mapboxUIState.collectAsState()

    val coordinates = mutableListOf<Point>()
    feature.geometry.coordinates.forEach { coordinate ->
        coordinates.add(Point.fromLngLat(coordinate[0], coordinate[1]))
    }

    // Calculate the center point of the bounding box
    val bbox = getBoundingBox(coordinates)
    val center = Point.fromLngLat(
        (bbox.west() + bbox.east()) / 2,
        (bbox.north() + bbox.south()) / 2
    )

    val zoom = calculateIdealZoom(bbox)

    val staticMapUrl = createStaticMapUrl(
        center = center,
        zoom = zoom,
        lineCoordinates = coordinates,
        uiState = mapboxUIState,
        feature = feature
    )

    Log.d("HikeCardMapPreview", "Static map URL: $staticMapUrl")

    Surface(
        modifier = Modifier
            .height(160.dp)
            .fillMaxWidth()
            .then( // This code is for only making the map clickable if a navController is provided
                if (navController != null) {
                    Modifier.clickable {
                        navController.navigate(Screen.MapPreview.route)
                    }
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
    ) {
        // Static image of map
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(staticMapUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Map preview",
            error = painterResource(id = R.drawable.map_error)
        )
    }
}

// Create a static map URL for a given set of coordinates
private fun createStaticMapUrl(
    center: Point,
    zoom: Double,
    lineCoordinates: List<Point>,
    uiState: MapboxUIState,
    feature: Feature
): String {
    val polyline = encodePolyline(lineCoordinates)
    val encodedPolyline = java.net.URLEncoder.encode(polyline, "UTF-8")

    val startPoint = lineCoordinates.first()
    val endPoint = lineCoordinates.last()
    val markers = "pin-s+4285F4(${startPoint.longitude()},${startPoint.latitude()})," +
            "pin-s+FF0000(${endPoint.longitude()},${endPoint.latitude()})"

    val mapStyle = uiState.mapStyle
    val mapStyleUrl = when (mapStyle) {
        Style.STANDARD_SATELLITE -> "satellite-v9"
        else -> "outdoors-v12"
    }

    val color = colorToHex(feature.color)

    return "https://api.mapbox.com/styles/v1/mapbox/${mapStyleUrl}/static/" +
            "path-10+${color}-1($encodedPolyline),${markers}/" +
            "${center.longitude()},${center.latitude()},${zoom},0,0/" +
            "1200x500@2x" + // widthxheight@2x
            "?access_token=${BuildConfig.MAPBOX_SECRET_TOKEN}" +
            "&attribution=false&logo=false"
}

private fun colorToHex(color: Color?): String {
    return String.format(
        "%02x%02x%02x",
        (color?.red?.times(255))?.toInt(),
        (color?.green?.times(255))?.toInt(),
        (color?.blue?.times(255))?.toInt()
    )
}

private fun encodePolyline(points: List<Point>): String {
    return com.mapbox.geojson.utils.PolylineUtils.encode(
        points.map { Point.fromLngLat(it.longitude(), it.latitude()) },
        5 // precision
    )
}

private fun getBoundingBox(points: List<Point>): BoundingBox {
    if (points.isEmpty()) return BoundingBox.fromLngLats(0.0, 0.0, 0.0, 0.0)

    var minLat = 90.0
    var maxLat = -90.0
    var minLng = 180.0
    var maxLng = -180.0

    points.forEach { point ->
        minLat = minOf(minLat, point.latitude())
        maxLat = maxOf(maxLat, point.latitude())
        minLng = minOf(minLng, point.longitude())
        maxLng = maxOf(maxLng, point.longitude())
    }

    val latBuffer = (maxLat - minLat) * 0.01
    val lngBuffer = (maxLng - minLng) * 0.01

    return BoundingBox.fromLngLats(
        minLng - lngBuffer,
        minLat - latBuffer,
        maxLng + lngBuffer,
        maxLat + latBuffer
    )
}

// Calculate the ideal zoom level based on the bounding box
private fun calculateIdealZoom(bbox: BoundingBox): Double {
    val latDiff = bbox.north() - bbox.south()
    val lngDiff = bbox.east() - bbox.west()

    val screenAspectRatio = 1200.0 / 500.0

    val latZoom = log2(360.0 / (latDiff * screenAspectRatio)) - 1
    val lngZoom = log2(360.0 / lngDiff) - 1

    val calculatedZoom = minOf(latZoom, lngZoom)

    return calculatedZoom.coerceIn(5.0, 20.0)
}

// Helper function for logarithm base 2
private fun log2(value: Double): Double {
    return ln(value) / ln(2.0)
}
