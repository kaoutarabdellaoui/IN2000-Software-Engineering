package no.uio.ifi.in2000_gruppe3.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "activity_table",
    primaryKeys = ["username", "hike_id"],
    foreignKeys = [
        ForeignKey(
            entity = Profile::class,
            parentColumns = arrayOf("username"),
            childColumns = arrayOf("username"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )]
)

data class Activity (
    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "hike_id")
    val hikeId: Int,

    @ColumnInfo(name = "times_walked")
    val timesWalked: Int = 1,

    @ColumnInfo(name = "notes")
    val notes: String = ""
)