package no.uio.ifi.in2000_gruppe3.ui.screens.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000_gruppe3.data.hikeAPI.models.Feature
import no.uio.ifi.in2000_gruppe3.data.hikeAPI.repository.HikeAPIRepository
import no.uio.ifi.in2000_gruppe3.data.locationForecastAPI.models.Locationforecast
import no.uio.ifi.in2000_gruppe3.data.locationForecastAPI.models.TimeSeries
import no.uio.ifi.in2000_gruppe3.data.locationForecastAPI.repository.LocationForecastRepository
import no.uio.ifi.in2000_gruppe3.data.metAlertsAPI.models.MetAlerts
import no.uio.ifi.in2000_gruppe3.data.metAlertsAPI.repository.MetAlertsRepository
import no.uio.ifi.in2000_gruppe3.ui.bottomSheetDrawer.SheetDrawerDetent
import no.uio.ifi.in2000_gruppe3.ui.screens.chatbotScreen.OpenAIViewModel

class HomeScreenViewModel() : ViewModel() {
    private val hikeAPIRepository = HikeAPIRepository(openAIViewModel = OpenAIViewModel())
    private val locationForecastRepository = LocationForecastRepository()
    private val metAlertsRepository = MetAlertsRepository()

    private val _homeScreenUIState = MutableStateFlow<HomeScreenUIState>(
        HomeScreenUIState(
            hikes = emptyList(),
            alerts = MetAlerts(listOf(), "", "", ""),
            forecast = null,
        )
    )
    val homeScreenUIState: StateFlow<HomeScreenUIState> = _homeScreenUIState.asStateFlow()

    private val _sheetStateTarget = MutableStateFlow(SheetDrawerDetent.SEMIPEEK)
    val sheetStateTarget = _sheetStateTarget.asStateFlow()

    private val _currentSheetOffset = MutableStateFlow(0f)
    val currentSheetOffset = _currentSheetOffset.asStateFlow()

    // To only show the aanund dialog once
    fun markAanundDialogShown() {
        _homeScreenUIState.update {
            it.copy(hasShownAanundDialog = true)
        }
    }

    fun setSheetState(target: SheetDrawerDetent) {
        _sheetStateTarget.value = target
    }

    fun updateNetworkStatus(isConnected: Boolean) {
        _homeScreenUIState.update {
            it.copy(hasNetworkConnection = isConnected)
        }
    }

    fun updateSheetOffset(offset: Float) {
        _currentSheetOffset.value = offset
    }

    fun fetchHikes(
        lat: Double,
        lng: Double,
        limit: Int,
        featureType: String,
        minDistance: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _homeScreenUIState.update {
                it.copy(isLoading = true)
            }
            try {
                val hikesResponse = hikeAPIRepository.getHikes(lat, lng, limit, featureType, minDistance)
                _homeScreenUIState.update {
                    it.copy(
                        hikes = hikesResponse,
                        isError = false
                    )
                }
            } catch (e: Exception) {
                _homeScreenUIState.update {
                    it.copy(
                        isError = true,
                        errorMessage = e.message ?: "Error fetching hikes"
                    )
                }
            } finally {
                _homeScreenUIState.update {
                    it.copy(isLoading = false)
                }
                _sheetStateTarget.value = SheetDrawerDetent.SEMIPEEK
            }
        }
    }

    fun fetchForecast(point: Point) {
        viewModelScope.launch(Dispatchers.IO) {
            _homeScreenUIState.update {
                it.copy(isLoading = true)
            }
            try {
                val forecast = locationForecastRepository.getForecast(point.latitude(), point.longitude())
                _homeScreenUIState.update {
                    it.copy(
                        forecast = forecast,
                        isError = false
                    )
                }
            } catch (e: Exception) {
                _homeScreenUIState.update {
                    it.copy(
                        isError = true,
                        errorMessage = e.message ?: "Error fetching forecast"
                    )
                }
            } finally {
                _homeScreenUIState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    fun fetchAlerts() {
        viewModelScope.launch(Dispatchers.IO) {
            _homeScreenUIState.update {
                it.copy(isLoading = true)
            }
            try {
                val alerts = metAlertsRepository.getAlerts()
                _homeScreenUIState.update {
                    it.copy(
                        alerts = alerts,
                        isError = false
                    )
                }
            } catch (e: Exception) {
                _homeScreenUIState.update {
                    it.copy(
                        isError = true,
                        errorMessage = e.message ?: "Error fetching alerts"
                    )
                }
            } finally {
                _homeScreenUIState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    fun clearHikes() {
        _homeScreenUIState.update {
            it.copy(hikes = emptyList())
        }
    }

    fun timeSeriesFromDate(date: String): List<TimeSeries>? {
        return homeScreenUIState.value.forecast?.properties?.timeseries
            ?.filter { it.time.startsWith(date) }
    }

    fun daysHighestTemp(date: String): Double {
        return timeSeriesFromDate(date)
            ?.maxOfOrNull { it.data.instant.details.air_temperature } ?: Double.NEGATIVE_INFINITY
    }

    fun daysLowestTemp(date: String): Double {
        return timeSeriesFromDate(date)
            ?.minOfOrNull { it.data.instant.details.air_temperature } ?: Double.POSITIVE_INFINITY
    }

    fun daysAverageWindSpeed(date: String): Double {
        val windSpeeds = timeSeriesFromDate(date)
            ?.map { it.data.instant.details.wind_speed }

        return windSpeeds?.average() ?: Double.NEGATIVE_INFINITY
    }
}

data class HomeScreenUIState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val hikes: List<Feature>,
    val alerts: MetAlerts?,
    val forecast: Locationforecast?,
    val hasNetworkConnection: Boolean = true,
    val hasShownAanundDialog: Boolean = false
)
