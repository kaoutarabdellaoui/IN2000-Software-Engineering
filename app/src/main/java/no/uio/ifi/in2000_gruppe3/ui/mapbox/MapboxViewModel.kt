package no.uio.ifi.in2000_gruppe3.ui.mapbox

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000_gruppe3.data.hikeAPI.models.Feature

class MapboxViewModel() : ViewModel() {
    private val placeAutocomplete = PlaceAutocomplete.create()

    private val _mapboxUIState: MutableStateFlow<MapboxUIState> = MutableStateFlow(
        MapboxUIState(
            mapStyle = Style.OUTDOORS,
            polylineAnnotations = emptyList(),
            searchResponse = emptyList(),
            searchQuery = ""
        )
    )
    var mapboxUIState: StateFlow<MapboxUIState> = _mapboxUIState

    fun updateMapStyle(style: String) {
        viewModelScope.launch {
            _mapboxUIState.update {
                it.copy(mapStyle = style)
            }
        }
    }

    fun updatePointerCoordinates(point: Point?) {
        _mapboxUIState.update {
            it.copy(
                pointerCoordinates = point,
                shouldFetchHikes = true
            )
        }
    }

    fun resetShouldFetchHikes() {
        _mapboxUIState.update {
            it.copy(shouldFetchHikes = false)
        }
    }

    fun updateSearchQuery(query: String) {
        _mapboxUIState.update { it.copy(searchQuery = query) }

        if (query.isNotEmpty()) {
            viewModelScope.launch {
                try {
                    val suggestions = placeAutocomplete.suggestions(query)

                    if (suggestions.isValue) {
                        _mapboxUIState.update {
                            it.copy(searchResponse = suggestions.value ?: emptyList())
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SearchBarViewModel", "Error fetching suggestions", e)
                }
            }
        } else {
            _mapboxUIState.update { it.copy(searchResponse = emptyList()) }
        }
    }

    fun getSelectedSearchResultPoint(suggestion: PlaceAutocompleteSuggestion) {
        viewModelScope.launch {
            try {
                val detailsResponse = placeAutocomplete.select(suggestion)
                val coordinates = detailsResponse.value!!.coordinate.coordinates()
                val point = Point.fromLngLat(coordinates[0], coordinates[1])

                _mapboxUIState.update {
                    it.copy(
                        shouldFetchHikes = true,
                        searchResponse = emptyList(),
                        searchQuery = "",
                        pointerCoordinates = point
                    )
                }
                updateSearchQuery("")
            } catch (e: Exception) {
                Log.e("SearchBarViewModel", "Error selecting place", e)
            }
        }
    }

    fun setLoaderState(isLoading: Boolean) {
        viewModelScope.launch {
            _mapboxUIState.update {
                it.copy(isLoading = isLoading)
            }
        }
    }

    fun updatePolylineAnnotationsFromFeatures(features: List<Feature>) {
        if (features.isEmpty()) {
            _mapboxUIState.update {
                it.copy(polylineAnnotations = emptyList())
            }
            return
        }
        _mapboxUIState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            val annotations = mutableListOf<PolylineAnnotationOptions>().apply {
                features.forEach { feature ->
                    val lineString = LineString.fromLngLats(
                        feature.geometry.coordinates.map {
                            Point.fromLngLat(it[0], it[1])
                        }
                    )
                    add(
                        PolylineAnnotationOptions()
                            .withGeometry(lineString)
                            .withLineColor(feature.color!!.toArgb())
                            .withLineWidth(7.0)
                            .withLineOpacity(0.7)
                            .withLineBorderColor(Color.White.toArgb())
                            .withLineBorderWidth(2.0)
                    )
                }
            }
            _mapboxUIState.update {
                it.copy(polylineAnnotations = annotations, isLoading = false)
            }
        }
    }

    fun clearPolylineAnnotations() {
        _mapboxUIState.update { it ->
            it.copy(polylineAnnotations = emptyList())
        }
    }

    fun centerOnUserPosition() {
        viewModelScope.launch {
            _mapboxUIState.update { it ->
                it.copy(
                    centerOnUserTrigger = System.currentTimeMillis()
                )
            }
        }
    }

    fun updateLatestUserPosition(point: Point) {
        _mapboxUIState.update { it ->
            it.copy(latestUserPosition = point)
        }
    }
}

data class MapboxUIState(
    val mapStyle: String,
    val pointerCoordinates: Point? = null,
    val latestUserPosition: Point? = null,
    val centerOnUserTrigger: Long = 0L,
    val polylineAnnotations: List<PolylineAnnotationOptions>,
    val zoom: Double = 12.0,
    val shouldFetchHikes: Boolean = false,

    val searchResponse: List<PlaceAutocompleteSuggestion>,
    val searchQuery: String,

    val isLoading: Boolean = true
)
