package no.uio.ifi.in2000_gruppe3

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import no.uio.ifi.in2000_gruppe3.data.database.MIGRATION_1_2
import no.uio.ifi.in2000_gruppe3.data.database.ProfileDatabase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        ProfileDatabase::class.java.canonicalName
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        var db: SupportSQLiteDatabase = helper.createDatabase(TEST_DB, 1)

        db.execSQL("INSERT INTO profile_table (username, isSelected) VALUES ('testUser', 0)")

        db.close()

        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2)

        val cursor = db.query("SELECT * FROM profile_table WHERE username = 'testUser'")
        assert(cursor.moveToFirst()) { "Migration failed: testUser not found in profile_table" }
        cursor.close()
    }
}