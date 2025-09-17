package no.uio.ifi.in2000_gruppe3.ui.bottomSheetDrawer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import no.uio.ifi.in2000_gruppe3.R
import no.uio.ifi.in2000_gruppe3.ui.hikeCard.SmallHikeCard
import no.uio.ifi.in2000_gruppe3.ui.loaders.Loader
import no.uio.ifi.in2000_gruppe3.ui.mapbox.MapboxViewModel
import no.uio.ifi.in2000_gruppe3.ui.navigation.Screen
import no.uio.ifi.in2000_gruppe3.ui.screens.chatbotScreen.OpenAIViewModel
import no.uio.ifi.in2000_gruppe3.ui.screens.hikeCardScreen.HikeScreenViewModel
import no.uio.ifi.in2000_gruppe3.ui.screens.homeScreen.HomeScreenViewModel

@Composable
fun RecommendedHikes(
    homeScreenViewModel: HomeScreenViewModel,
    hikeScreenViewModel: HikeScreenViewModel,
    mapBoxViewModel: MapboxViewModel,
    openAIViewModel: OpenAIViewModel,
    navController: NavHostController
) {
    val hikeUIState by hikeScreenViewModel.hikeScreenUIState.collectAsState()

    LaunchedEffect(Unit) {
        if (!hikeUIState.recommendedHikesLoaded) {
            openAIViewModel.getRecommendedHikes(
                homeScreenViewModel,
                hikeScreenViewModel
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ai_sparkle_icon),
                contentDescription = "AI Sparkle Icon",
                modifier = Modifier
                    .size(60.dp)
                    .weight(0.2f)
            )

            Text(
                text = "Mine anbefalinger i dag",
                color = Color.Black,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                fontStyle = MaterialTheme.typography.titleLarge.fontStyle,
            )

            Icon(
                painter = painterResource(R.drawable.ai_sparkle_icon),
                contentDescription = "AI Sparkle Icon",
                modifier = Modifier
                    .size(60.dp)
                    .weight(0.2f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (hikeUIState.recommendedHikes.isEmpty()) {
            Text(
                text = "Vent mens jeg finner de beste turene for deg!",
            )
            Loader()
        }

        hikeUIState.recommendedHikes.forEach { hikeFeature ->
            SmallHikeCard(
                mapboxViewModel = mapBoxViewModel,
                feature = hikeFeature,
                onClick = {
                    hikeScreenViewModel.updateHike(hikeFeature)
                    navController.navigate(Screen.HikeScreen.route)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
