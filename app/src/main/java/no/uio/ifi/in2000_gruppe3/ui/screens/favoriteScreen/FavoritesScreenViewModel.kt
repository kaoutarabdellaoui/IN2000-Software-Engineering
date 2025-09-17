package no.uio.ifi.in2000_gruppe3.ui.screens.favoriteScreen

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000_gruppe3.data.database.Favorite
import no.uio.ifi.in2000_gruppe3.data.favorites.repository.FavoriteRepository
import no.uio.ifi.in2000_gruppe3.data.hikeAPI.models.Feature
import no.uio.ifi.in2000_gruppe3.data.hikeAPI.repository.HikeAPIRepository
import no.uio.ifi.in2000_gruppe3.data.profile.repository.ProfileRepository
import no.uio.ifi.in2000_gruppe3.ui.mapbox.MapboxViewModel

class FavoritesScreenViewModel(
    application: Application,
    private val favoriteRepository: FavoriteRepository,
    private val profileRepository: ProfileRepository,
    private val hikeAPIRepository: HikeAPIRepository,
    private val mapboxViewModel: MapboxViewModel?, // must be nullable for testing
) : AndroidViewModel(application) {

    private val _favoriteScreenUIState = MutableStateFlow<FavoriteScreenUIState>(
        FavoriteScreenUIState(
            favorites = emptyList(),
            userLocation = Point.fromLngLat(10.441649, 59.542819),
        )
    )
    val favoriteScreenUIState: StateFlow<FavoriteScreenUIState> =
        _favoriteScreenUIState.asStateFlow()


    init {
        viewModelScope.launch {
            try {
                setUser()
                val username = profileRepository.getSelectedUser().username
                getAllFavorites(username)
                getAllConverteFavorites()
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Error initializing data: ${e.message}")
            }
        }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _favoriteScreenUIState.update {
                it.copy(isLoading = true)
            }
            try {
                val username = profileRepository.getSelectedUser().username
                val updatedFavorites = getAllFavorites(username)

                _favoriteScreenUIState.update {
                    it.copy(favorites = updatedFavorites)
                }

                Log.d(
                    "FavoritesViewModel",
                    "Fetched favorites for user: ${username}: ${_favoriteScreenUIState.value.favorites}"
                )
                getAllConverteFavorites()

            } catch (e: Exception) {
                _favoriteScreenUIState.update {
                    it.copy(isError = true, errorMessage = e.message.toString())
                }
                Log.e("FavoritesViewModel", "Error loading favorites: ${e.message}")
            } finally {
                _favoriteScreenUIState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    fun setUser() {
        viewModelScope.launch {
            _favoriteScreenUIState.update {
                it.copy(username = profileRepository.getSelectedUser().username)
            }
        }
    }

    fun updateUserLocationFromMapbox() {
        val latestPosition = mapboxViewModel!!.mapboxUIState.value.latestUserPosition
        if (latestPosition != null) {
            updateUserLocation(latestPosition)
        }
    }

    fun updateUserLocation(point: Point) {
        _favoriteScreenUIState.update {
            it.copy(userLocation = point)
        }
    }

    // for testing
    suspend fun getAllFavorites(username: String): List<Int> {
        return favoriteRepository.getAllFavorites(username)
    }

    fun isHikeFavorite(feature: Feature): Boolean {
        return _favoriteScreenUIState.value.favorites.contains(feature.properties.fid)
    }

    fun getAllConverteFavorites() {
        viewModelScope.launch {
            _favoriteScreenUIState.update {
                it.copy(isLoading = true)
            }
            try {
                val favoriteFeatures: List<Feature> = hikeAPIRepository.getHikesById(
                    _favoriteScreenUIState.value.favorites,
                    _favoriteScreenUIState.value.userLocation
                )
                _favoriteScreenUIState.update {
                    it.copy(convertedFavorites = favoriteFeatures)
                }
                Log.d("FavoritesViewModel", "Fetched converted favorites: $favoriteFeatures")
            } catch (e: Exception) {
                _favoriteScreenUIState.update {
                    it.copy(isError = true, errorMessage = e.message.toString())
                }
            } finally {
                _favoriteScreenUIState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    fun addFavorite(id: Int) {
        viewModelScope.launch {
            _favoriteScreenUIState.update {
                it.copy(isLoading = true)
            }
            try {
                val currentUser = profileRepository.getSelectedUser()
                val newFavorite = Favorite(currentUser.username, id)
                Log.d("FavoritesViewModel", "Adding favorite: $newFavorite")
                favoriteRepository.addFavorite(newFavorite)
                _favoriteScreenUIState.update {
                    it.copy(
                        favorites = _favoriteScreenUIState.value.favorites + newFavorite.hikeId
                    )
                }
            } catch (e: Exception) {
                _favoriteScreenUIState.update {
                    it.copy(isError = true, errorMessage = e.message.toString())
                }
            } finally {
                _favoriteScreenUIState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    fun deleteFavorite(id: Int) {
        viewModelScope.launch {
            _favoriteScreenUIState.update {
                it.copy(isLoading = true)
            }
            try {
                val currentUser = profileRepository.getSelectedUser()
                val favoriteToRemove = Favorite(currentUser.username, id)
                favoriteRepository.deleteFavorite(favoriteToRemove)

                _favoriteScreenUIState.update {
                    it.copy(
                        favorites = _favoriteScreenUIState.value.favorites - favoriteToRemove.hikeId
                    )
                }
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Error fetching favorites: ${e.message}")
                _favoriteScreenUIState.update {
                    it.copy(isError = true, errorMessage = e.message.toString())
                }
            } finally {
                _favoriteScreenUIState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }
}

data class FavoriteScreenUIState(
    val convertedFavorites: List<Feature> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val favorites: List<Int> = emptyList(),
    val username: String = "",
    val userLocation: Point = Point.fromLngLat(10.441649, 59.542819)
)
