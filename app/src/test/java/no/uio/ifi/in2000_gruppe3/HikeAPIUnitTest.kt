package no.uio.ifi.in2000_gruppe3

import com.mapbox.geojson.Point
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000_gruppe3.data.hikeAPI.datasource.HikeAPIDatasource
import no.uio.ifi.in2000_gruppe3.data.hikeAPI.models.Feature
import org.junit.Test
import kotlin.test.assertEquals

class HikeAPIUnitTest {
    private val hikeAPIDatasource = HikeAPIDatasource()

    @Test
    fun getHikeWithId() {
        val ids = listOf("1")
        var features: List<Feature>

        runBlocking {
            features = hikeAPIDatasource.getHikesById(ids, Point.fromLngLat(10.0, 60.0))
        }
        assertEquals(
            ids.first().toInt(),
            features.first().properties.fid,
            "Fikk ikke feature med korrekt id"
        )
        println("---Test getHikeWithId PASSERT---")
    }

    @Test
    fun getMultipleHikesWithId() {
        val ids = listOf("1", "2", "3")
        var features: List<Feature>
        runBlocking {
            features = hikeAPIDatasource.getHikesById(ids, Point.fromLngLat(10.0, 60.0))
        }

        assertEquals(
            ids.size,
            features.size,
            "Fikk ikke riktig antall features"
        )

        features.forEach { feature ->
            assert(ids.contains(feature.properties.fid.toString())) {
                "Fikk ikke feature med korrekt id"
            }
        }
        println("---Test getMultipleHikesWithId PASSERT---")
    }
}