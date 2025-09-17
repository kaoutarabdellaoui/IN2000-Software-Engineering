package no.uio.ifi.in2000_gruppe3.data.profile.repository

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import no.uio.ifi.in2000_gruppe3.data.database.Profile
import no.uio.ifi.in2000_gruppe3.data.database.ProfileDao
import no.uio.ifi.in2000_gruppe3.data.database.ProfileDatabase

class ProfileRepository(private val profileDao: ProfileDao) {

    suspend fun addUser(profile: Profile) {
        profileDao.insertUser(profile)
    }

    suspend fun deleteProfile(profile: Profile) {
        profileDao.deleteUser(profile)
    }

    suspend fun selectProfile(username: String) {
        unselectUser()
        profileDao.selectUser(username)
    }

    suspend fun unselectUser() {
        profileDao.unselectUser()
    }

    suspend fun getSelectedUser(): Profile {
        val selectedUser = profileDao.getSelectedUser()
        return selectedUser ?: profileDao.getDefaultUser()?.also {
            profileDao.selectUser(it.username)
        } ?: throw IllegalStateException("Default user not found in the database.")
    }

    suspend fun getAllUsers(): List<Profile> {
        return profileDao.getAllUsers()
    }

    // Denne funksjonen er kun til testing.
    suspend fun clearAllUsers() {
        profileDao.clearAllUsers()
    }

    companion object {
        @Volatile
        private var INSTANCE: ProfileRepository? = null

        fun getInstance(
            context: Context,
            scope: CoroutineScope = CoroutineScope(SupervisorJob())
        ): ProfileRepository {
            return INSTANCE ?: synchronized(this) {
                val database = ProfileDatabase.getDatabase(context)
                val instance = ProfileRepository(database.profileDao())
                INSTANCE = instance
                instance
            }
        }
    }
}