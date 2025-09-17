package no.uio.ifi.in2000_gruppe3.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProfileDao {

    @Insert
    suspend fun insertUser(profile: Profile)

    @Delete
    suspend fun deleteUser(username: Profile)

    @Query("SELECT * FROM profile_table")
    suspend fun getAllUsers(): List<Profile>

    @Query("UPDATE profile_table SET isSelected = 1 WHERE username LIKE :username")
    suspend fun selectUser(username: String)

    @Query("SELECT * FROM profile_table WHERE isSelected = 1 LIMIT 1")
    suspend fun getSelectedUser(): Profile?

    @Query("SELECT * FROM profile_table WHERE username = 'defaultUser' LIMIT 1")
    suspend fun getDefaultUser(): Profile?

    @Query("UPDATE profile_table SET isSelected = 0")
    suspend fun unselectUser()

    @Query("DELETE from profile_table")
    suspend fun clearAllUsers()
}