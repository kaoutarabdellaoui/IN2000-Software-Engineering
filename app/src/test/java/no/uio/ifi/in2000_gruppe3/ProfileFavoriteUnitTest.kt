package no.uio.ifi.in2000_gruppe3

import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import no.uio.ifi.in2000_gruppe3.data.database.Profile
import no.uio.ifi.in2000_gruppe3.data.database.ProfileDatabase
import no.uio.ifi.in2000_gruppe3.data.favorites.repository.FavoriteRepository
import no.uio.ifi.in2000_gruppe3.data.hikeAPI.repository.HikeAPIRepository
import no.uio.ifi.in2000_gruppe3.data.profile.repository.ProfileRepository
import no.uio.ifi.in2000_gruppe3.ui.screens.chatbotScreen.OpenAIViewModel
import no.uio.ifi.in2000_gruppe3.ui.screens.favoriteScreen.FavoritesScreenViewModel
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Testklasse for UserRepository og favoritesViewModel.
 * Tester noen utvalgte funksjoner fra userDao, userRepository og favoritesViewModel
 * for å sjekke at SQL spørringene fungerer som de skal.
 * NB! Vi har ikke skrevet tester for ALL funksjonaliteten
 */

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
class ProfileFavoriteUnitTest {
    private val application = RuntimeEnvironment.getApplication()
    private val userDatabase = Room.inMemoryDatabaseBuilder(
        application,
        ProfileDatabase::class.java
    )
        .allowMainThreadQueries()
        .build()

    private val userDao = userDatabase.profileDao()
    private val favoriteDao = userDatabase.favoriteDao()
    private val profileRepository = ProfileRepository(userDao)


    //Dummy data til testene
    val bruker1 = Profile(username = "Aanund")
    val bruker2 = Profile(username = "Victor")

    @After
    fun tearDown() {
        userDatabase.close()
    }

    @Test
    fun testAddUser() {
        println("Tester å legge til bruker: $bruker1")

        runBlocking {
            profileRepository.clearAllUsers()
            profileRepository.addUser(bruker1)
            assertContains(profileRepository.getAllUsers(), bruker1, "Bruker ble ikke lagt til")
        }

        println("---testAddUser PASSERT---")
    }

    @Test
    fun testDeleteUser() {
        println("Tester å slette bruker: $bruker1")

        runBlocking {
            profileRepository.clearAllUsers()
            profileRepository.addUser(bruker1)
            profileRepository.deleteProfile(bruker1)
            assertFalse(
                profileRepository.getAllUsers().contains(bruker1),
                "Bruker ble ikke slettet"
            )
        }

        println("---testDeleteUser PASSERT---")
    }

    @Test
    fun testGetAllUsers() {
        println("tester å hente alle brukere")
        val allProfiles: List<Profile>

        runBlocking {
            profileRepository.clearAllUsers()
            profileRepository.addUser(bruker1)
            profileRepository.addUser(bruker2)

            allProfiles = profileRepository.getAllUsers()

            assertContains(allProfiles, bruker1, "Bruker 1 ble ikke lagt til")
            assertContains(allProfiles, bruker2, "Bruker 2 ble ikke lagt til")
        }

        println("---testGetAllUsers PASSERT---")
    }

    @Test
    fun testSelectUser() {
        println("Tester å velge bruker: $bruker1")
        val selectedProfile: Profile?

        runBlocking {
            profileRepository.clearAllUsers()
            profileRepository.addUser(bruker1)
            profileRepository.selectProfile(bruker1.username)
            selectedProfile = profileRepository.getSelectedUser()

            assertEquals(selectedProfile.username, bruker1.username, "Bruker ble ikke valgt")
        }

        println("---testSelectUser PASSERT---")
    }

    //Tester for favorites view model.
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testAddFavorite() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            profileRepository.clearAllUsers()
            profileRepository.addUser(bruker1)
            profileRepository.selectProfile(bruker1.username)

            val favoritesScreenViewModel = FavoritesScreenViewModel(
                application = application,
                favoriteRepository = FavoriteRepository(favoriteDao),
                profileRepository = profileRepository,
                hikeAPIRepository = HikeAPIRepository(OpenAIViewModel()),
                mapboxViewModel = null
            )

            favoritesScreenViewModel.addFavorite(1)

            var favorites = favoritesScreenViewModel.getAllFavorites(bruker1.username)
            // Dette var den eneste måten jeg klarte å vente på at viewmodelscope
            // coroutine launchen ble ferdig før hovedtråen fortsetter...
            while (favorites.isEmpty()) {
                favorites = favoritesScreenViewModel.getAllFavorites(bruker1.username)
            }

            assertEquals(1, favorites.last(), "Favoritt ble ikke lagt til")
        } finally {
            Dispatchers.resetMain()
        }
        println("---testAddFavorite PASSERT---")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testDeleteFavorite() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            profileRepository.clearAllUsers()
            profileRepository.addUser(bruker1)
            profileRepository.selectProfile(bruker1.username)

            val favoritesScreenViewModel = FavoritesScreenViewModel(
                application = application,
                favoriteRepository = FavoriteRepository(favoriteDao),
                profileRepository = profileRepository,
                hikeAPIRepository = HikeAPIRepository(OpenAIViewModel()),
                mapboxViewModel = null
            )
            favoritesScreenViewModel.addFavorite(1)
            var favorites = favoritesScreenViewModel.getAllFavorites(bruker1.username)
            while (favorites.isEmpty()) {
                favorites = favoritesScreenViewModel.getAllFavorites(bruker1.username)
            }
            favoritesScreenViewModel.deleteFavorite(1)
            while (favorites.isNotEmpty()) {
                favorites = favoritesScreenViewModel.getAllFavorites(bruker1.username)
            }

            assertTrue(favorites.isEmpty(), "Favoritt ble ikke slettet")

            println("---testDeleteFavorite PASSERT---")
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetAllFavorites() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            profileRepository.clearAllUsers()
            profileRepository.addUser(bruker1)
            profileRepository.selectProfile(bruker1.username)

            val favoritesScreenViewModel = FavoritesScreenViewModel(
                application = application,
                favoriteRepository = FavoriteRepository(favoriteDao),
                profileRepository = profileRepository,
                hikeAPIRepository = HikeAPIRepository(OpenAIViewModel()),
                mapboxViewModel = null
            )

            favoritesScreenViewModel.addFavorite(1)
            favoritesScreenViewModel.addFavorite(2)

            var favorites = favoritesScreenViewModel.getAllFavorites(bruker1.username)

            while (favorites.size < 2) {
                favorites = favoritesScreenViewModel.getAllFavorites(bruker1.username)
            }

            assertContains(favorites, 1, "fikk ikke hentet alle ider")
            assertContains(favorites, 2, "fikk ikke hentet alle ider")

        } finally {
            Dispatchers.resetMain()
        }
        println("---testGetAllFavorites PASSERT---")
    }
}