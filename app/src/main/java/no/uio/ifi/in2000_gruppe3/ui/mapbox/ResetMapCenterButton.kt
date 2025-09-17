package no.uio.ifi.in2000_gruppe3.ui.mapbox

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000_gruppe3.R
import no.uio.ifi.in2000_gruppe3.ui.screens.homeScreen.HomeScreenViewModel

@Composable
fun ResetMapCenterButton(
    homeScreenViewModel: HomeScreenViewModel,
    mapboxViewModel: MapboxViewModel
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.85f)
        ),
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                mapboxViewModel.centerOnUserPosition()
                homeScreenViewModel.fetchForecast(
                    mapboxViewModel.mapboxUIState.value.latestUserPosition!!
                )
            }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.location_arrow),
            contentDescription = "Sentrer kart",
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
        )
    }
}
