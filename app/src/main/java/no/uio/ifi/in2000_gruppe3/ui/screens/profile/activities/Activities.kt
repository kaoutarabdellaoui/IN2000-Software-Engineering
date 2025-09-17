package no.uio.ifi.in2000_gruppe3.ui.screens.profile.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import no.uio.ifi.in2000_gruppe3.ui.hikeCard.SmallHikeCard
import no.uio.ifi.in2000_gruppe3.ui.mapbox.MapboxViewModel
import no.uio.ifi.in2000_gruppe3.ui.navigation.Screen
import no.uio.ifi.in2000_gruppe3.ui.screens.hikeCardScreen.HikeScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Activities(
    activityScreenViewModel: ActivityScreenViewModel,
    hikeScreenViewModel: HikeScreenViewModel,
    mapboxViewModel: MapboxViewModel,
    navController: NavHostController
) {
    val logUIState by activityScreenViewModel.activityScreenUIState.collectAsState()

    if (logUIState.hikeLog.isEmpty()) {
        Text(
            text = "Her var det tomt gitt 🤔",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
    } else {
        LazyColumn(
            modifier = Modifier.padding(8.dp),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(logUIState.convertedLog) { feature ->
                SmallHikeCard(
                    mapboxViewModel = mapboxViewModel,
                    feature = feature,
                    onClick = {
                        hikeScreenViewModel.updateHike(feature)
                        navController.navigate(Screen.HikeScreen.route)
                    }
                )

                ActivityNotes(activityScreenViewModel, feature)
            }
        }
    }
}
