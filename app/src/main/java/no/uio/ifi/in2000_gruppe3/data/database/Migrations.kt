package no.uio.ifi.in2000_gruppe3.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {

        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `new_profile_table` (
                `username` TEXT NOT NULL,
                `isSelected` INTEGER NOT NULL DEFAULT 0,
                PRIMARY KEY(`username`)
            )
            """
        )

        database.execSQL(
            """
                INSERT INTO `new_profile_table`(`username`, `isSelected`)
                SELECT `username`, `isSelected` FROM `profile_table`
                """
        )

        database.execSQL("DROP TABLE `profile_table`")

        database.execSQL("ALTER TABLE `new_profile_table` RENAME TO `profile_table`")

        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `log_table` (
                `username` TEXT NOT NULL,
                `hike_id` INTEGER NOT NULL,
                `times_walked` INTEGER NOT NULL DEFAULT 0,
                `notes` TEXT NOT NULL DEFAULT '',
                PRIMARY KEY(`username`, `hike_id`),
                FOREIGN KEY(`username`) REFERENCES `profile_table`(`username`) ON UPDATE CASCADE ON DELETE CASCADE
            )
            """
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `activity_table` (
                    `username` TEXT NOT NULL,
                    `hike_id` INTEGER NOT NULL,
                    `times_walked` INTEGER NOT NULL DEFAULT 1,
                    `notes` TEXT NOT NULL DEFAULT '',
                    PRIMARY KEY(`username`, `hike_id`),
                    FOREIGN KEY(`username`) REFERENCES `profile_table`(`username`) ON UPDATE CASCADE ON DELETE CASCADE
                )
            """
        )

        database.execSQL(
            """
                INSERT INTO `activity_table`(`username`, `hike_id`, `times_walked`, `notes`)
                SELECT `username`, `hike_id`, `times_walked`, `notes` FROM `log_table`
                """
        )

        database.execSQL("DROP TABLE `log_table`")
    }
}