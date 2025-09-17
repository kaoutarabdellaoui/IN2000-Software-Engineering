package no.uio.ifi.in2000_gruppe3.data.log.repository

import no.uio.ifi.in2000_gruppe3.data.database.ActivityDao

class ActivityRepository (private val activityDao: ActivityDao) {

    suspend fun addLog(newActivity: no.uio.ifi.in2000_gruppe3.data.database.Activity) {
        activityDao.saveLog(newActivity)
    }

    suspend fun deleteLog(activityToRemove: no.uio.ifi.in2000_gruppe3.data.database.Activity) {
        activityDao.deleteLog(activityToRemove)
    }

    suspend fun getAllLogs(username: String): List<Int> {
        return activityDao.getAllLogs(username)
    }

    suspend fun adjustTimesWalked(username: String, hikeId: Int, adjustTimesWalked: Int) {
        activityDao.adjustTimesWalked(username, hikeId, adjustTimesWalked)
    }

    suspend fun getTotalTimesWalked(username: String): Int {
        return activityDao.getTotalTimesWalked(username)
    }

    suspend fun addNotesToLog(username: String, hikeId: Int, notes: String) {
        activityDao.addNotesToLog(username, hikeId, notes)
    }

    suspend fun getNotesForHike(username: String, hikeId: Int): String {
        return activityDao.getNotesForHike(username, hikeId)
    }

    suspend fun getTimesWalkedForHike(username: String, hikeId: Int): Int {
        return activityDao.getTimesWalkedForHike(username, hikeId)
    }
}