package no.uio.ifi.in2000_gruppe3.ui.screens.profile.activities

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import no.uio.ifi.in2000_gruppe3.data.database.ProfileDatabase
import no.uio.ifi.in2000_gruppe3.data.hikeAPI.repository.HikeAPIRepository
import no.uio.ifi.in2000_gruppe3.data.log.repository.ActivityRepository
import no.uio.ifi.in2000_gruppe3.data.profile.repository.ProfileRepository
import no.uio.ifi.in2000_gruppe3.ui.mapbox.MapboxViewModel
import no.uio.ifi.in2000_gruppe3.ui.screens.chatbotScreen.OpenAIViewModel

class ActivityScreenViewModelFactory (
    private val application: Application,
    private val openAIViewModel: OpenAIViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActivityScreenViewModel::class.java)) {
            val applicationScope = CoroutineScope(SupervisorJob())
            val database = ProfileDatabase.getDatabase(application)
            val activityRepository = ActivityRepository(database.logDao())
            val hikeAPIRepository = HikeAPIRepository(openAIViewModel)
            val mapboxViewModel = MapboxViewModel()

            // Use the singleton instance of UserRepository
            val profileRepository = ProfileRepository.getInstance(application, applicationScope)

            @Suppress("UNCHECKED_CAST")
            return ActivityScreenViewModel(
                application,
                activityRepository,
                profileRepository,
                hikeAPIRepository,
                mapboxViewModel
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}