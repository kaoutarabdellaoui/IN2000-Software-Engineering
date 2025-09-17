package no.uio.ifi.in2000_gruppe3.data.favorites.repository

import no.uio.ifi.in2000_gruppe3.data.database.Favorite
import no.uio.ifi.in2000_gruppe3.data.database.FavoriteDao

class FavoriteRepository(private val favoriteDao: FavoriteDao) {
    suspend fun addFavorite(newFavorite: Favorite) {
        favoriteDao.saveFavorite(newFavorite)
    }

    suspend fun deleteFavorite(favoriteToRemove: Favorite) {
        favoriteDao.deleteFavorite(favoriteToRemove)
    }

    suspend fun getAllFavorites(username: String): List<Int> {
        return favoriteDao.getAllFavorites(username)
    }
}