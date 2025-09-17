package no.uio.ifi.in2000_gruppe3.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavoriteDao {

    @Insert
    suspend fun saveFavorite(favorite: Favorite)

    @Delete
    suspend fun deleteFavorite(favorite: Favorite)

    @Query("SELECT hike_id FROM favorite_table WHERE username LIKE :username")
    suspend fun getAllFavorites(username: String): List<Int>
}